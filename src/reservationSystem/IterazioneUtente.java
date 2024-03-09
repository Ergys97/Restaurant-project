package reservationSystem;



import reservationUtility.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;



public class IterazioneUtente {
	
	
	UtilityElenchi uElenchi = new UtilityElenchi();
	UtilityTime uTime = new UtilityTime();
	
	private final String intestazione ="> ";
	public StrutturaRistorante creaStrutturaRistorante() {
		int numPosti = InputDati.leggiInteroNonNegativo(MessaggiApplicazione.MESSAGGIO_NUMERO_POSTI);
		double caricoLavoroPersona = InputDati.leggiInteroNonNegativo(MessaggiApplicazione.MESSAGGIO_CARICO_LAVORO_PERSONA);
		System.out.println( MessaggiApplicazione.MESSAGGIO_INIZIALIZZIAMO_INGREDIENTI);
		ArrayList<Ingrediente> ingredienti = creaIngredienti(MessaggiApplicazione.MESSAGGIO_NOME_INGREDIENTE, MessaggiApplicazione.MESSAGGIO_QUANTITA_INGREDIENTE, MessaggiApplicazione.MESSAGGIO_INGREDIENTE_GIA_INSERITO, MessaggiApplicazione.MESSAGGIO_INSERIRE_ALTRO_INGREDIENTE);
		System.out.println(MessaggiApplicazione.MESSAGGIO_INIZIALIZZAZIONE_BEVANDE);
		ArrayList<Bevanda> bevande = creaBevande(MessaggiApplicazione.MESSAGGIO_NOME_BEVANDA, MessaggiApplicazione.MESSAGGIO_QUANTITA_BEVANDA, MessaggiApplicazione.MESSAGGIO_BEVANDA_GIA_INSERITA, MessaggiApplicazione.MESSAGGIO_INSERIRE_ALTRA_BEVANDA);
		System.out.println(MessaggiApplicazione.MESSAGGGIO_INIZIALIZZAZIONE_GENERI);
		ArrayList<GeneriAlimentari> generiExtra = creaGenereAlimentari(MessaggiApplicazione.MESSAGGIO_NOME_GENERE, MessaggiApplicazione.MESSAGGIO_QUANTITA_GENERE, MessaggiApplicazione.MESSAGGIO_GENERE_GIA_INSERITO, MessaggiApplicazione.MESSAGGIO_INSERIRE_ALTRO_GENERE);
		System.out.println(MessaggiApplicazione.MESSAGGIO_INIZIALIZZAZIONE_PROCAPITE_BEVANDE);
		ArrayList<Integer> selezioneInsiemBevande = selezionaInsiemeAlimento(bevande  , MessaggiApplicazione.MESSAGGIO_SELEZIONE_INSIEME_BEVANDE, MessaggiApplicazione.MESSAGGIO_AGGIUNTA_BEVANDA_INSIEME);
		ArrayList<Bevanda> insiemeBevande = creaInsiemeBevade(selezioneInsiemBevande, bevande);
		HashMap<Bevanda, Integer> consumoProcapiteBevande = inizializzaQuantitaInsiemeBevande(insiemeBevande);
		System.out.println(MessaggiApplicazione.MESSAGGIO_INIZIALIZZAZIONE_PROCAPITE_GENERI);
		ArrayList<Integer> selezioneInsiemeGeneri = selezionaInsiemeAlimento(generiExtra , MessaggiApplicazione.MESSAGGIO_SELEZIONE_INSIEME_GENERE, MessaggiApplicazione.MESSAGGIO_AGGIUNTA_GENERE_INSIEME);
		ArrayList<GeneriAlimentari> insiemeGeneri = creaInsiemeGeneri(selezioneInsiemeGeneri, generiExtra);
		HashMap<GeneriAlimentari, Integer> consumoProcapiteGeneri = inizializzaQuantitaGeneri(insiemeGeneri);
		return new StrutturaRistorante(numPosti, caricoLavoroPersona, ingredienti, bevande, consumoProcapiteBevande, generiExtra, consumoProcapiteGeneri);
		}
	public Ricetta creaRicetta(double caricoLavoroPersona, ArrayList<Ingrediente> ingredientiTotali) {
		
		String nome = InputDati.leggiStringaNonVuota(MessaggiApplicazione.MESSAGGIO_NOME_RICETTA);
		ArrayList<Integer> sel = selezionaInsiemeIngrediente(ingredientiTotali, MessaggiApplicazione.MESSAGGIO_SELEZIONE_INSIEME_INGREDIENTI, MessaggiApplicazione.MESSAGGIO_AGGIUNTA_INGREDIENTE_INSIEME);
		ArrayList<Ingrediente> selIngredienti = creaInsiemeIngredienti(sel,ingredientiTotali);
		double caricoLavoroPerPorzione = InputDati.leggiDouble(MessaggiApplicazione.MESSAGGIO_FRAZIONE_RICETTA, 0.0, 1.0);
		int tempoPreparazione = InputDati.leggiInteroConMinimo(MessaggiApplicazione.MESSAGGIO_TEMPO_RICETTA, 1);
		return new Ricetta(nome, selIngredienti, caricoLavoroPerPorzione, caricoLavoroPersona, tempoPreparazione);
}
	
	private ArrayList<Ingrediente> creaInsiemeIngredienti (ArrayList<Integer> selezione, ArrayList<Ingrediente> ingredientiTotali)
	{
		
		ArrayList<Ingrediente> insiemeIngredienti = new ArrayList<>();
		for(Integer sel : selezione) {
			try {
				
				insiemeIngredienti.add(ingredientiTotali.get(sel));
			}
				catch(IndexOutOfBoundsException e) {
					System.out.println(MessaggiApplicazione.MESSAGGIO_ERRORE_SELEZIONE );
					continue;
				}
			
		}
		
		return insiemeIngredienti;
	}
	

		
	
	
	

	public Bevanda creaBevanda (String MESSAGGIO_NOME_BEVANDA, String MESSAGGIO_QUANTITA_BEVANDA) {
	
		String nome = InputDati.leggiStringaNonVuota(MESSAGGIO_NOME_BEVANDA);
		int quantita = InputDati.leggiInteroConMinimo(MESSAGGIO_QUANTITA_BEVANDA,  1);
		return new Bevanda(nome, quantita);
	}
	public GeneriAlimentari creaGenereAlimentare (String MESSAGGIO_NOME_GENERE, String MESSAGGIO_QUANTITA_GENERE) {
		String nome = InputDati.leggiStringaNonVuota(MESSAGGIO_NOME_GENERE);
		int quantita = InputDati.leggiInteroConMinimo(MESSAGGIO_QUANTITA_GENERE, 1);
		return new GeneriAlimentari(nome, quantita);
	}
	public Ingrediente creaIngrediente(String MESSAGGIO_NOME_INGREDIENTE, String MESSAGGIO_QUANTITA_INGREDIENTE) {
		String nome = InputDati.leggiStringaNonVuota( MESSAGGIO_NOME_INGREDIENTE);
		int quantita = InputDati.leggiInteroConMinimo(MESSAGGIO_QUANTITA_INGREDIENTE, 1);
		return new Ingrediente(nome, quantita);
	}
	
	public ArrayList<Bevanda> creaBevande (String MESSAGGIO_NOME_BEVANDA, String MESSAGGIO_QUANTITA_BEVANDA,
			String MESSAGGIO_BEVANDA_INSERITA, String MESSAGGIO_INSERIRE_ALTRA_BEVANDA){
		ArrayList<Bevanda> ret = new ArrayList<Bevanda>();
		do {
			Bevanda nBev = creaBevanda(MESSAGGIO_NOME_BEVANDA, MESSAGGIO_QUANTITA_BEVANDA);
			if(!uElenchi.contieneBevanda(ret, nBev))
				ret.add(nBev);
			else
				System.out.println( MESSAGGIO_BEVANDA_INSERITA);
		}
		while(InputDati.yesOrNo( MESSAGGIO_INSERIRE_ALTRA_BEVANDA));
		return ret;
	}
	public ArrayList<GeneriAlimentari> creaGenereAlimentari(String MESSAGGIO_NOME_GENERE, String MESSAGGIO_QUANTITA_GENERE,
			String MESSAGGIO_GENERE_INSERITO, String MESSAGGIO_INSERIRE_ALTRO_GENERE){
		ArrayList<GeneriAlimentari> ret = new ArrayList<GeneriAlimentari>();
		do {
			GeneriAlimentari nGen = creaGenereAlimentare(MESSAGGIO_NOME_GENERE, MESSAGGIO_QUANTITA_GENERE);
			if(!uElenchi.contieneGenere(ret, nGen)) 
				ret.add(nGen);
			else
			System.out.println(MESSAGGIO_GENERE_INSERITO);
			
		}
		while(InputDati.yesOrNo(MESSAGGIO_INSERIRE_ALTRO_GENERE));
		return ret;
	}
	public ArrayList<Ingrediente> creaIngredienti(String MESSAGGIO_NOME_INGREDIENTE, String MESSAGGIO_QUANTITA_INGREDIENTE,
			String MESSAGGIO_INGREDIENTE_GIA_INSERITO, String MESSAGGIO_INSERIRE_ALTRO_INGREDIENTE){
		ArrayList<Ingrediente> ret = new ArrayList<Ingrediente>();
		do {
			Ingrediente nIng =  creaIngrediente(MESSAGGIO_NOME_INGREDIENTE, MESSAGGIO_QUANTITA_INGREDIENTE);
				if(! uElenchi.contieneIngrediente(ret, nIng))
					ret.add(nIng);
				else
				System.out.println(MESSAGGIO_INGREDIENTE_GIA_INSERITO);
		}
		while(InputDati.yesOrNo(MESSAGGIO_INSERIRE_ALTRO_INGREDIENTE));
		return ret;
	}
	
	
	
	
		
		
		
		
	
	
	
	private ArrayList<Bevanda> creaInsiemeBevade (ArrayList<Integer> selezione, ArrayList<Bevanda> bevandeTotali ){
		
		ArrayList<Bevanda> insiemeBevande = new ArrayList<>();
		
		for(Integer sel : selezione) {
			
			try
			{
				insiemeBevande.add(bevandeTotali.get(sel));
			}
			catch (IndexOutOfBoundsException e) {
				System.out.println(MessaggiApplicazione.MESSAGGIO_ERRORE_SELEZIONE );
				continue;
			}
			
			
		}
		
		
		return insiemeBevande;
			
		}
	private ArrayList<GeneriAlimentari> creaInsiemeGeneri (ArrayList<Integer> selezione, ArrayList<GeneriAlimentari> generiTotali)
	{
		
		ArrayList<GeneriAlimentari> insiemeGeneri = new ArrayList<>();
		for(Integer sel : selezione) {
			try {
				insiemeGeneri.add(generiTotali.get(sel));
			}
				catch(IndexOutOfBoundsException e) {
					System.out.println( MessaggiApplicazione.MESSAGGIO_ERRORE_SELEZIONE );
					continue;
				}
		
			
		}
		return insiemeGeneri;
	}
		
	private ArrayList<Piatto> creaListaPiattiMenu(ArrayList<Integer> selezione, ArrayList<Piatto>elencoPiatti){
		
		ArrayList<Piatto> piattiMenu = new ArrayList<>();
		for (Integer sel : selezione) {
			try {
				piattiMenu.add(elencoPiatti.get(sel));
			}
				catch(IndexOutOfBoundsException e) {
					System.out.println(MessaggiApplicazione.MESSAGGIO_ERRORE_SELEZIONE);
					continue;
				}
			
		}
		
		return piattiMenu;
	
	}
		
		

	private HashMap<Bevanda, Integer> inizializzaQuantitaInsiemeBevande(ArrayList<Bevanda> insiemeBevande){
		HashMap<Bevanda, Integer> bevandeQuantita = new HashMap<>();
		for (Bevanda bevanda : insiemeBevande) {
			System.out.println(MessaggiApplicazione.INTESTAZIONE+ bevanda.getNome());
			int  d = InputDati.leggiIntero(MessaggiApplicazione.MESSAGGIO_INSERIMENTO_QUANTITA_BEVANDA_PROCAPITE);
			bevandeQuantita.put(bevanda, d);
			
		}
		return bevandeQuantita;
	}
	
	private HashMap<GeneriAlimentari, Integer> inizializzaQuantitaGeneri(ArrayList<GeneriAlimentari> insiemeGeneri){
		HashMap<GeneriAlimentari, Integer> generiQuantita = new HashMap<>();
		for (GeneriAlimentari generiAlimentari : insiemeGeneri) {
			System.out.println(MessaggiApplicazione.INTESTAZIONE+ generiAlimentari.getNome());
			int d = InputDati.leggiIntero(MessaggiApplicazione.MESSAGGIO_INSERIMENTO_QUANTITA_GENERE_PROCAPITE);
			generiQuantita.put(generiAlimentari, d);
		}
		return generiQuantita;
	}

	
	public void stampaAlimento(ArrayList<? extends Alimento> listaAlimenti) {
		int x = 0;
		for (Alimento alimento : listaAlimenti) {
			System.out.println(MessaggiApplicazione.INTESTAZIONE+x+" - "+alimento.getNome());
			x++;
		}
	}
	
	
	
	public void stampaValoriRistorante(StrutturaRistorante sRisto) {
		

		if(!sRisto.isInizializzato()) {
			System.out.println(intestazione + MessaggiApplicazione.MESSAGGIO_RISTORANTE_NON_INIZIALIZZATO);
			return;
		}
		
		System.out.println( MessaggiApplicazione.MESSAGGIO_CARATTERISTICHE_RISTORANTE);
		System.out.println(MessaggiApplicazione.MESSAGGIO_POSTI_RISTORANTE+ sRisto.getNumPostiASedere());
		System.out.println(MessaggiApplicazione.MESSAGGIO_CARICO_LAVORO_PERSONA +sRisto.getCaricoLavoroXPersona());
		System.out.println(MessaggiApplicazione.MESSAGGIO_CARICO_LAVORO_SOSTENIBILE +sRisto.getCaricoLavoroSostenibile());
		
	}
	

	public void stampaRicette (ArrayList<Ricetta> in) {
		int x = 0;
		if(in.isEmpty())
			System.out.println(MessaggiApplicazione.MESSAGGIO_ELENCO_RICETTE_VUTO);
		else {
			
		
		System.out.println(MessaggiApplicazione.MESSAGGIO_ELENCO_RICETTE);
		for (Ricetta ricetta : in) {
			System.out.println(MessaggiApplicazione.INTESTAZIONE+x+" - " + ricetta.getNome());
			x++;
		}
		}
	}
	
	
	public void stampaPiatti(ArrayList<Piatto> elenco) {
		int x = 0;
		if(elenco.isEmpty())
			System.out.println( MessaggiApplicazione.MESSAGGIO_NO_PIATTI_IN_ELENCO);
		else {
			for (Piatto piatto : elenco) {
				System.out.println(MessaggiApplicazione.INTESTAZIONE + x + " - "+ piatto.getNome());
				x++;
			}
		}
	}
	
	public  void stampaMenu(ArrayList<MenuTematico> elenco)
	{
		int x =0;
		
		if(elenco.isEmpty())
			System.out.println( MessaggiApplicazione.MESSAGGIO_NO_MENU_IN_ELENCO);
		else {
				
				for (MenuTematico menu : elenco) {
					System.out.println(MessaggiApplicazione.INTESTAZIONE+x+" "+menu.getNome());
					//stampaPiatti(menu.getPiatti());
					for (Piatto piatto : menu.getPiatti()) {
						System.out.println("\t"+MessaggiApplicazione.INTESTAZIONE+piatto.getNome());
						
					}
					
				}
		}
	}
	
	public Piatto creaPiatto(ArrayList<Ricetta> elencoRicette) {
		if(elencoRicette.isEmpty()) {
			System.out.println( MessaggiApplicazione.MESSAGGIO_ERRORE_NO_RICETTE);
			return null;
		}
	int selRicetta =	selezionaRicetta(elencoRicette);
	Ricetta newRicetta = elencoRicette.get(selRicetta);
	int giorni  = InputDati.leggiInteroConMinimo(  MessaggiApplicazione.MESSAGGIO_DURATA_VALIDITA_PIATTO, 1);
	LocalDate data = uTime.creaDataScadenzaGiorni(giorni);
	return new Piatto(newRicetta, data);
		
	}
	
	
	public  void stampaPrenotazioni(ArrayList<Prenotazione> elenco)
	{
		int x = 0;
		if(elenco.isEmpty())
			System.out.println( MessaggiApplicazione.MESSAGGIO_PRENOTAZIONI_NON_DISPONIBILI);
		else {
				for (Prenotazione prenotazione : elenco) {
					System.out.println(MessaggiApplicazione.INTESTAZIONE+ x + "- Data Prenotazione: "+ prenotazione.getData()+ " Numero Persone: "+ prenotazione.getNumCoperti());
					System.out.println(MessaggiApplicazione.INTESTAZIONE+ "Menu Tematici: " + prenotazione.getMenuPrenotati());
					System.out.println(MessaggiApplicazione.INTESTAZIONE+ "Piatti: " + prenotazione.getPiattiPrenotati());
					x++;
				}
		}
	}
	public Prenotazione creaPrenotazione(int numeroPostiRistorante , double caricoLavoroSostenibile, ArrayList<MenuTematico> elencoMenu, ArrayList<Piatto> elencoPiatti, ArrayList<Prenotazione> elencoPrenotazioni) {
		Prenotazione preno = new Prenotazione(numeroPostiRistorante, caricoLavoroSostenibile);
		
		
		if(elencoMenu.isEmpty() && elencoPiatti.isEmpty()) {
			System.out.println(   MessaggiApplicazione.MESSAGGIO_NO_MENU_NO_PIATTI_DISPONIBILI);
			return null;
		}
		else {
			int numGiorni = InputDati.leggiInteroConMinimo( MessaggiApplicazione.MESSAGGIO_INSERIRE_GIORNI_PRENOTAZIONE, 0);
			LocalDate dataPrenotazione = uTime.creaDataScadenzaGiorni(numGiorni);
			int postiMax = preno.limiteMassimoPostiaSedere(dataPrenotazione, elencoPrenotazioni);
			if(postiMax <= 0) {
				System.out.println(  MessaggiApplicazione.MESSAGGIO_RISTORANTE_OCCUPATO);
				return null;
			}
			int numPersone = InputDati.leggiIntero(MessaggiApplicazione.MESSAGGIO_INSERIRE_NUMERO_PERSONE_PRENOTAZIONE, 1, postiMax);
			
			preno.setData(dataPrenotazione);
			preno.setNumCoperti(numPersone);
			Prenotazione prenotazioneFinale =  selezionaPiattieMenu(preno,elencoMenu, elencoPiatti);
			return prenotazioneFinale;
			
			
		}
		
	}
	
	private Prenotazione selezionaPiattieMenu(Prenotazione prenotazioneCorrente, ArrayList<MenuTematico> elencoMenu, ArrayList<Piatto> elencoPiatti) {
		int numeroPersone = prenotazioneCorrente.getNumCoperti();
		do {
			System.out.println( MessaggiApplicazione.MESSAGGIO_PERSONE_RIMANENTI_PRENOTAZIONE+ prenotazioneCorrente.getNumCoperti());
			if(InputDati.yesOrNo( MessaggiApplicazione.MESSAGGIO_VISUALIZZARE_MENU_TEMATICI))
			{
				if(!elencoMenu.isEmpty()) {
				int selezioneMenu = selezionaMenuTematico(elencoMenu);
				int numSelezioni = InputDati.leggiIntero( MessaggiApplicazione.MESSAGGIO_NUMERO_PERSONE_SELEZIONE_MENU, 1, numeroPersone);
				HashMap<MenuTematico, Integer> prenotazioneMenu = prenotazioneCorrente.selezionaMenuTematico(selezioneMenu, numSelezioni, elencoMenu);
				prenotazioneCorrente.aggiungiMenuTematico(prenotazioneMenu);
				numeroPersone = numeroPersone-numSelezioni;
				}
				else
				System.out.println( MessaggiApplicazione.MESSAGGIO_MENU_TEMATICI_NON_DISPONIBILI);
			}
			else {
			
				if(!elencoPiatti.isEmpty()) {
				int selezionePiatto = selezionaPiatto(elencoPiatti);
				int numSelezioni = InputDati.leggiIntero(MessaggiApplicazione.MESSAGGIO_NUMERO_PERSONE_SELEZIONE_PIATTO, 1, numeroPersone);
				 HashMap<Piatto,Integer> prenotazionePiatto = prenotazioneCorrente.selezionaPiatto(selezionePiatto, numSelezioni, elencoPiatti);
				 prenotazioneCorrente.aggiungiPiatto(prenotazionePiatto);
				 numeroPersone = numeroPersone-numSelezioni;
				}
				else
				System.out.println( MessaggiApplicazione.MESSAGGIO_PIATTI_NON_DISPONIBILI);
			}
			
			
		}while(! (numeroPersone == 0));
		
		return prenotazioneCorrente;
		
	}
	
	
	private void stampaAlimentiCompleti(ArrayList<? extends Alimento> elencoAlimenti) {
		if(elencoAlimenti.isEmpty()) {
			System.out.println( MessaggiApplicazione.MESSAGGIO_NO_ALIEMENTI);
		}
		else {
			int x = 0;
			for (Alimento alimento : elencoAlimenti) {
				System.out.println(MessaggiApplicazione.INTESTAZIONE+x+ " - "+ alimento.getNome() +" - " +alimento.getQuantita()  +" - "+alimento.getUnitaMisura()+" - "+alimento.getDataScadenza());
				x++;
			}
		}
	}

	
	public void stampaListaSpesa (ListaSpesa lSpesa) {
		if(lSpesa.getElencoIngredienti().isEmpty())
			System.out.println( MessaggiApplicazione.MESSAGGIO_NON_CI_SONO_INGREDIENTI);
		else {
				System.out.println(MessaggiApplicazione.INTESTAZIONE+"Ingredienti");
				stampaAlimentiCompleti(lSpesa.getElencoIngredienti());
		}
		if(lSpesa.getElencoBevande().isEmpty())
			System.out.println( MessaggiApplicazione.MESSAGGIO_NON_CI_SONO_BEVANDE);
		else {
			System.out.println(MessaggiApplicazione.INTESTAZIONE+"Bevande");
			stampaAlimentiCompleti(lSpesa.getElencoBevande());
		}
		if(lSpesa.getElencoGeneri().isEmpty())
			System.out.println( MessaggiApplicazione.MESSAGGIO_NON_CI_SONO_GENERI);
		else {
			System.out.println(MessaggiApplicazione.INTESTAZIONE+"Generi Alimentari Extra");
			stampaAlimentiCompleti(lSpesa.getElencoGeneri());
		}
	}
	
	public ArrayList<Integer> selezionaInsiemeAlimento(ArrayList<? extends Alimento> lista,String MESSAGGIO_SELEZIONE_INSIEME, String MESSAGGIO_AGGIUNTA_ALIMENTO_INSIEME){
		
		
		ArrayList<Integer> ret = new ArrayList<Integer>();
		if(lista.size()==1) {
			ret.add(0);
			return ret;
		}
		this.stampaAlimento(lista);
		do {
			int x = InputDati.leggiIntero( MESSAGGIO_SELEZIONE_INSIEME, 0, lista.size()-1);
			if(!ret.contains(x))
				ret.add(x);
		}
		while(InputDati.yesOrNo(MESSAGGIO_AGGIUNTA_ALIMENTO_INSIEME));
		return ret;
	}


	public ArrayList<Integer> selezionaInsiemeIngrediente(ArrayList<Ingrediente> lista,String MESSAGGIO_SELEZIONE_INSIEME, String MESSAGGIO_AGGIUNTA_ALIMENTO_INSIEME){
		
		
		ArrayList<Integer> ret = new ArrayList<Integer>();
		
		this.stampaAlimento(lista);
		do {
			int x = InputDati.leggiIntero( MESSAGGIO_SELEZIONE_INSIEME, 0, lista.size()-1);
			if(!ret.contains(x))
				ret.add(x);
		}
		while(InputDati.yesOrNo(MESSAGGIO_AGGIUNTA_ALIMENTO_INSIEME));
		return ret;
	}

	
	
	public int selezionaRicetta(ArrayList<Ricetta> elencoRicette) {
		ArrayList<Ricetta> ricette = elencoRicette;
		int x;
		stampaRicette(ricette);
		x = InputDati.leggiIntero( MessaggiApplicazione.MESSAGGIO_SELEZIONE_RICETTA, 0, ricette.size() -1);
		
		return x;
		
	}
		
	public ArrayList<Integer> selezionaPiatti (ArrayList<Piatto> elencoPiatti) {
		
		ArrayList<Integer> selezione = new ArrayList<>();
		stampaPiatti(elencoPiatti);
		do {
			int d = InputDati.leggiIntero(  MessaggiApplicazione.MESSAGGIO_SELEZIONE_PIATTO_PER_MENU,0,elencoPiatti.size() -1);
			if(!selezione.contains(d))
				selezione.add(d);
		} while(InputDati.yesOrNo( MessaggiApplicazione.MESSAGGIO_AGGIUNTA_PIATTO_MENU));
		
		return selezione;
	}
	
	public int  selezionaPiatto (ArrayList<Piatto> elenco) {
		stampaPiatti(elenco);
		int d = InputDati.leggiIntero( MessaggiApplicazione.MESSAGGIO_SELEZIONE_PIATTO, 0, elenco.size()-1);
		return d;
		
	}
	
	private int selezionaMenuTematico (ArrayList<MenuTematico> elenco) {
		stampaMenu(elenco);
		int d = InputDati.leggiIntero( MessaggiApplicazione.MESSAGGIO_SELEZIONE_MENU_TEMATICO, 0, elenco.size()-1);
		return d ;
	}
	
	
	public  MenuTematico    creaMenuTematico(ArrayList<Piatto> elencoPiatti, double caricoLavoroPersona) {
		if(elencoPiatti.isEmpty()) {
			return null;
		}
		ArrayList<Integer> piattiSelezionati  = selezionaPiatti(elencoPiatti);
		ArrayList<Piatto> piattiMenu = creaListaPiattiMenu(piattiSelezionati, elencoPiatti);
		String nome = InputDati.leggiStringaNonVuota(MessaggiApplicazione.MESSAGGIO_NOME_MENU);
		int giorni = InputDati.leggiInteroConMinimo( MessaggiApplicazione.MESSAGGIO_GIORNI_DISPONIBILI_MENU, 1);
		LocalDate dataScadenzaMenu = uTime.creaDataScadenzaGiorni(giorni);
		MenuTematico mTematico = new MenuTematico(piattiMenu, dataScadenzaMenu,caricoLavoroPersona , nome);
		return mTematico;
		
	}
	
	public ArrayList<Ingrediente> aggiungiIngredienti(ArrayList<Ingrediente> elenco, boolean strutturaInizializzata ) {
		if(!strutturaInizializzata) {
			System.out.println(MessaggiApplicazione.MESSAGGIO_RISTORANTE_NON_INIZIALIZZATO);
			return new ArrayList<>();
		}
		while(InputDati.yesOrNo( MessaggiApplicazione.MESSAGGIO_AGGIUNGERE_INGREDIENTE)) {
			Ingrediente i = creaIngrediente(MessaggiApplicazione.MESSAGGIO_NOME_INGREDIENTE, MessaggiApplicazione.MESSAGGIO_QUANTITA_INGREDIENTE);
			if(uElenchi.aggiungiIngrediente(elenco, i))
				System.out.println(MessaggiApplicazione.MESSAGGIO_INGREDIENTE_INSERITO_CORRETTAMENTE);
				
			else
				System.out.println( MessaggiApplicazione.MESSAGGIO_INGREDIENTE_NON_INSERITO);
		}
		
		return elenco;
			
	}
	
	
	
	
	public ArrayList<Ricetta> aggiungiRicette ( ArrayList<Ricetta> elencoRicette,ArrayList<Ingrediente>elencoIngredienti ,boolean strutturaInizializzata, double caricoLavoroPersona){
		if(!strutturaInizializzata) {
			System.out.println( MessaggiApplicazione.MESSAGGIO_RISTORANTE_NON_INIZIALIZZATO);
			return new ArrayList<>();
		}
		while(InputDati.yesOrNo( MessaggiApplicazione.MESSAGGIO_AGGIUNTA_RICETTA_ELENCO)){
			Ricetta r = creaRicetta(caricoLavoroPersona, elencoIngredienti);
			if(uElenchi.aggiungiRicettaAElenco(elencoRicette, r))
				System.out.println(MessaggiApplicazione.MESSAGGIO_RICETTA_AGGIUNTA_CORRETTAMENTE);
			else
				System.out.println(MessaggiApplicazione.MESSAGGIO_RICETTA_NON_INSERITA);
			
		}
				
		return elencoRicette;
	
	}
	
	public ArrayList<MenuTematico> aggiungiMenuTematico (ArrayList<MenuTematico> elencoMenu, ArrayList<Piatto> elencoPiatti, boolean strutturaInizializzata, double caricoLavoroPersona)
	{
		if(!strutturaInizializzata) {
			System.out.println(MessaggiApplicazione.MESSAGGIO_RISTORANTE_NON_INIZIALIZZATO);
			return new ArrayList<>();
		}
		while(InputDati.yesOrNo(MessaggiApplicazione.MESSAGGIO_AGGIUNTA_MENU_TEMATICO)) {
			MenuTematico t = creaMenuTematico(elencoPiatti, caricoLavoroPersona);
			if(uElenchi.aggiungiMenuAElenco(elencoMenu, t))
				System.out.println(MessaggiApplicazione.MESSAGGIO_MENU_TEMATICO_INSERITO_CORETTAMENTE);
			else
			System.out.println(MessaggiApplicazione.MESSAGGIO_MENU_TEMATICO_NON_INSERITO);
		}
		return elencoMenu;
	}
	
	public ArrayList<Piatto> aggiungiPiatto(ArrayList<Ricetta> elencoRicette,ArrayList<Piatto> elencoPiatti,ArrayList<Ingrediente> elencoIngredienti,boolean strutturaInizializzata){
		if(!strutturaInizializzata) {
			System.out.println(MessaggiApplicazione.MESSAGGIO_RISTORANTE_NON_INIZIALIZZATO);
			return new ArrayList<>();
		}
		if(!uElenchi.possibilitaAggiungerePiatto(elencoRicette, elencoPiatti)) {
			System.out.println( MessaggiApplicazione.MESSAGGIO_IMPOSSIBILITA_AGGIUNTA_RICETTE);
			return elencoPiatti;
		}
		else {
			Piatto p = creaPiatto(elencoRicette);
			 return uElenchi.aggiungiPiatto(elencoPiatti, p);
			
		}
	}

}
