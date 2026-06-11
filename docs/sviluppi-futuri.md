# Sviluppi Futuri

In ordine di sensatezza, una volta completata la modernizzazione:

## Alto valore, sforzo contenuto

1. **Validazione input più ricca e messaggi d'errore curati** — è un'app interattiva, la UX del terminale è la prima cosa che si nota provandola.
2. **Cancellazione/modifica prenotazioni** — oggi le prenotazioni si creano e basta; è la lacuna funzionale più evidente del dominio e un'ottima palestra perché tocca gli observer (annullare = ripristinare il magazzino).
3. **Logging con SLF4J al posto delle stampe diagnostiche** — voce da CV a costo quasi zero.

## Evoluzioni architetturali (per imparare)

4. **API REST con Spring Boot sopra il service layer** — il refactoring fatto è esattamente ciò che lo rende possibile: controller console e controller REST diventano due "facce" intercambiabili sugli stessi service. È la dimostrazione pratica che l'architettura a layer paga, e Spring Boot è la singola tecnologia più richiesta nei colloqui Java in Italia.
5. **SQLite/H2 con JDBC o JPA come DataStore alternativo** — l'interfaccia `DataStore` c'è già, implementarla con un database vero è un esercizio pulito e mostra che capisci il repository pattern.
6. **Docker** — un `Dockerfile` per il jar (banale) e più avanti per l'eventuale versione REST.

## Più ambiziosi (solo se ti appassiona il progetto)

7. **UI web leggera (anche solo Thymeleaf) sopra l'API REST** — per avere qualcosa di visivamente mostrabile.
8. **Multi-utente con autenticazione per i tre ruoli** (gestore, addetto prenotazioni, magazziniere).

---

La scelta più strategica per il portfolio è la pipeline **REST → database → Docker**: trasforma "progetto universitario rimodernato" in "applicazione full-stack Java", riusando tutto il lavoro fatto.
