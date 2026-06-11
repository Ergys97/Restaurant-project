# Restaurant Project — Design di modernizzazione

**Data:** 2026-06-12
**Stato:** in revisione
**Obiettivo:** trasformare il progetto universitario (app console Java per la gestione di un ristorante) in un progetto da portfolio: build Maven standard, architettura MVC a layer, codice in inglese, persistenza JSON, test, CI e JAR eseguibile.

## Contesto e motivazione

Il progetto attuale ha una buona base concettuale (pattern Observer, interfaccia di persistenza, dependency injection manuale, test JUnit presenti) ma soffre di:

- nessun build tool: compila solo dentro Eclipse (`.classpath`/`.project`); file `.class` versionati in `bin/`;
- persistenza tramite serializzazione binaria Java su file con estensione `.txt` nella working directory; fragile (ogni modifica alle classi invalida i dati) e pratica deprecata;
- `Main.java` metodo-dio: tre switch giganti che mescolano caricamento file, input utente e logica;
- `IterazioneUtente.java` (583 righe) mescola input, presentazione e logica di business; 4 metodi quasi identici;
- `loadSingleFile` ritorna `new Object()` su file mancante → `ClassCastException` criptica a valle;
- `Prenotazione` contiene proprietà del ristorante (`numeroPostiMax`, `caricoLavoroSostenibile`) e 4 costruttori sovrapposti;
- codice in italiano con typo diffusi (`oggettoDaSalavre`, `creaInsiemeBevade`, `MESSAGGIO_ELENCO_RICETTE_VUTO`).

## Decisioni prese (con l'utente)

| Tema | Decisione |
|---|---|
| Obiettivo | Portfolio per colloqui |
| Lingua | Codice (classi, metodi, variabili) in inglese; messaggi UI restano in italiano |
| Persistenza | Migrazione a JSON con Jackson |
| Refactoring | Ristrutturazione completa MVC a layer |
| Strategia | Approccio A: prima infrastruttura (Maven, CI, test), poi ristrutturazione incrementale flusso per flusso, app sempre compilante a ogni passo |
| Git | **Nessun commit da parte dell'agente**: l'utente gestisce i commit |

## Architettura a layer

```
src/main/java/it/restaurant/
├── Main.java                    # solo bootstrap (~20 righe): crea dipendenze, avvia il menu
├── model/                       # entità di dominio, nessun I/O
│   ├── FoodItem.java            # era Alimento (classe base astratta)
│   ├── Ingredient.java          # era Ingrediente
│   ├── Drink.java               # era Bevanda
│   ├── ExtraGood.java           # era GeneriAlimentari
│   ├── Recipe.java              # era Ricetta
│   ├── Dish.java                # era Piatto
│   ├── ThemedMenu.java          # era MenuTematico
│   ├── DishOrder.java           # nuovo: piatto + quantità (sostituisce HashMap<Piatto,Integer>)
│   ├── MenuOrder.java           # nuovo: menu tematico + quantità (sostituisce HashMap<MenuTematico,Integer>)
│   ├── Reservation.java         # era Prenotazione
│   ├── ShoppingList.java        # era ListaSpesa
│   └── RestaurantConfig.java    # era StrutturaRistorante
├── repository/                  # persistenza, unico layer che tocca i file
│   ├── DataStore.java           # interfaccia (era FileOperation)
│   └── JsonDataStore.java       # implementazione Jackson (era SerializableFileOperation + ServizioFile)
├── service/                     # logica di business, nessun System.out/in
│   ├── ReservationService.java  # validazione prenotazioni, calcolo carico di lavoro (era GestionePrenotazione + logica di Prenotazione)
│   ├── KitchenService.java      # ricette, piatti, menu tematici (era parte di UtilityElenchi)
│   ├── WarehouseService.java    # magazzino e lista spesa (era RegistroMagazzino)
│   └── event/
│       ├── ReservationObserver.java        # era Osservatori/Osservabile
│       ├── StockUpdater.java               # era EliminaAlimentiPrenotazione
│       ├── ShoppingListUpdater.java        # era PrenotazioneToListaSpesa
│       ├── ReservationRegistry.java        # era GestioneElencoPrenotazioni
│       └── ReservationNotifier.java        # era NotificaIngredientiPrenotazione (avvisi al magazziniere)
├── controller/                  # orchestrazione dei 3 flussi utente
│   ├── ManagerController.java   # era il flusso "gestore" in Main.gestioneRistorante
│   ├── ReservationController.java  # era Main.gestionePrenotazioni
│   └── WarehouseController.java    # era Main.gestioneMagazzino
└── view/                        # tutto il terminale: stampa e lettura input
    ├── ConsoleView.java         # stampe (era metà di IterazioneUtente)
    ├── ConsoleInput.java        # letture validate (era InputDati + l'altra metà di IterazioneUtente)
    ├── Menu.java                # era MyMenu
    └── Messages.java            # era MessaggiApplicazione (testi in italiano)
```

**Regola delle dipendenze:** `view` non conosce nessuno; `controller` usa `service` e `view`; `service` usa `model` e `repository`; `model` non dipende da niente. Nessun `System.out` fuori da `view`, nessun accesso a file fuori da `repository`.

**Classi eliminate perché assorbite:** `IterazioneUtente`, `Utility`, `UtilityElenchi`, `UtilityTime`, `ServizioFile`, `EstrazioniCasuali`/`NumeriCasuali` (previa verifica che siano inutilizzate).

### Correzioni di modellazione

1. **`Reservation`** perde `numeroPostiMax` e `caricoLavoroSostenibile` (proprietà del ristorante, non della prenotazione). I metodi `limiteMassimoPostiaSedere`, `conformeAlCaricoLavoro`, `caricoLavoroPrenotazioniGiornata` migrano in `ReservationService`. La classe diventa un contenitore dati con un solo costruttore.
2. **`HashMap<Piatto,Integer>` / `HashMap<MenuTematico,Integer>`** in `Reservation` sostituite da `List<DishOrder>` e `List<MenuOrder>` (elemento + quantità): modello più chiaro e serializzazione JSON naturale (le chiavi di mappa non-stringa sono scomode in JSON).
3. I 4 metodi fotocopia `creaInsiemeBevade/Generi/Ingredienti/creaListaPiattiMenu` diventano un solo metodo generico in `ConsoleInput`: `<T> List<T> selectItems(List<T> items, String prompt, String againPrompt)`.
4. Firme con `List`/`Map` al posto di `ArrayList`/`HashMap`.

## Persistenza JSON

**Libreria:** Jackson (`jackson-databind`, eventualmente `jackson-datatype-jsr310` per `LocalDate`).

**Interfaccia type-safe:**

```java
public interface DataStore {
    <T> List<T> loadList(String key, Class<T> type);
    <T> void saveList(String key, List<T> items);
    <T> Optional<T> load(String key, Class<T> type);
    <T> void save(String key, T item);
}
```

- `Optional<T>` al posto di `new Object()`: il caso "dato assente" diventa gestito per costruzione.
- Dati in una directory **`data/`** (creata al primo avvio, in `.gitignore`): `data/reservations.json`, `data/ingredients.json`, `data/recipes.json`, `data/dishes.json`, `data/themed-menus.json`, `data/drinks.json`, `data/extra-goods.json`, `data/restaurant-config.json`, `data/shopping-list.json`.
- Le chiavi/nomi file sono costanti private del layer repository, non più costanti pubbliche usate da `Main`.
- **Nessuna migrazione dei vecchi dati serializzati** (sono dati di prova; si parte puliti).
- **Errori I/O:** file corrotto o non scrivibile → eccezione dedicata (es. `DataStoreException`) con messaggio chiaro; il controller la intercetta e mostra l'errore senza far crashare l'app. Niente `null` silenziosi.

## Controller e flusso

- `Main` istanzia `JsonDataStore`, i tre service, registra gli observer sul `ReservationService`, crea i controller e avvia il menu principale (3 ruoli: gestore, addetto prenotazioni, magazziniere — il dominio non cambia).
- Ogni `case` degli switch attuali diventa un metodo privato con nome parlante nel controller corrispondente (`addRecipe()`, `showAlaCarteMenu()`, `collectReservation()`...).
- **Pattern Observer conservato:** alla conferma di una prenotazione, `ReservationService` notifica `StockUpdater` (scala le quantità dal magazzino), `ShoppingListUpdater` e `ReservationRegistry`. Gli observer ricevono le dipendenze nel costruttore invece di costruirsi i gestori file da soli.
- **Bug fix incluso:** `selezionaInsiemeAlimento` su lista vuota oggi chiama `leggiIntero(msg, 0, -1)` (range impossibile); `selectItems` gestisce esplicitamente la lista vuota restituendo lista vuota con messaggio.

## Test e CI

- JUnit 5 come dipendenza Maven (oggi i test dipendono dal jar interno di Eclipse); test esistenti (5) tradotti e portati in `src/test/java`.
- Nuovi test sui service (testabili senza console né file): `ReservationServiceTest` (carico di lavoro, limite posti, date), `WarehouseServiceTest` (scalo quantità, lista spesa), `JsonDataStoreTest` (round-trip salva→ricarica su directory temporanea, `@TempDir`).
- Obiettivo realistico: ~15–20 test sulla logica core.
- **GitHub Actions:** `.github/workflows/ci.yml` con `mvn verify` su Java 21 (Temurin) a ogni push/PR. Badge nel README.

## Build e packaging

- `pom.xml`: Java 21, dipendenze Jackson + JUnit 5, `maven-shade-plugin` per fat-jar eseguibile (`java -jar restaurant.jar`).
- Layout standard Maven: `src/main/java`, `src/test/java`.
- Rimozione dal tracking git di `bin/`, `.classpath`, `.project`, `.settings/`; nuovo `.gitignore` (target/, data/, file IDE).
- **README riscritto in inglese:** descrizione, esempio d'uso del terminale, diagramma dei layer, istruzioni build/run, badge CI.
- Release GitHub con JAR allegato: a carico dell'utente, manualmente.

## Strategia di esecuzione (Approccio A)

Ordine di lavoro, con app compilante e avviabile a ogni passo:

1. **Rete di sicurezza:** `.gitignore` + untrack dei file generati; migrazione layout Maven con il codice attuale così com'è; test esistenti verdi con `mvn test`; CI attiva.
2. **Ristrutturazione incrementale per flusso:** model + repository (JSON) → flusso gestore → flusso prenotazioni (con observer) → flusso magazzino. Ogni passo: tradurre, spostare nel layer giusto, testare.
3. **Rifinitura:** nuovi test sui service, fat-jar, README.

## Fuori scope

- UI grafica o API REST.
- Database (SQLite o altro).
- Migrazione dei dati serializzati esistenti.
- Pubblicazione su Maven Central (è un'applicazione, non una libreria).
- Nuove funzionalità di dominio: il comportamento dell'app resta identico per l'utente finale.

## Criteri di successo

- `mvn verify` verde in locale e in CI.
- `java -jar target/restaurant.jar` avvia l'app e tutti e tre i flussi funzionano come prima (verifica manuale).
- Nessuna classe sopra ~300 righe; nessun `System.out` fuori da `view`; nessun accesso file fuori da `repository`.
- Repository pulito: niente `.class` né file IDE tracciati.
- Codice e README in inglese, messaggi UI in italiano.
