package reservationSystem;

import java.io.File;
import java.util.ArrayList;

import reservationUtility.ServizioFile;

public class SerializableFileOperation implements FileOperation {
	public static final File FILE_PRENOTAZIONI =  new File("Prenotazioni.txt");
	public static final File FILE_LISTA_SPESA = new File("ListaSpesa.txt");
	public static final File FILE_MENU_TEMATICI = new File("MenuTematici.txt");
	public static final File FILE_INGREDIENTI = new File("Ingredienti.txt");
	public static final File FILE_GENERI_EXTRA =  new File("AlimentiExtra.txt");
	public static final File FILE_BEVANDE = new File("Bevande.txt");
	public static final File FILE_STRUTTURA_RISTORANTE = new File("StrutturaRistorante.txt");
	public static final File FILE_RICETTE =  new File("Ricette.txt");
	public static final File FILE_PIATTI =  new File("Piatti.txt");

	
	public <E> ArrayList<E> load(File file) {
		ArrayList<E> caricato = (ArrayList<E>) ServizioFile.caricaSingoloOggetto(file);
		if(caricato == null) {
			return new ArrayList<E>();
		}
		else {
			return caricato;
		}
		 
	}



	@Override
	public <E> ArrayList<E> loadMultipleFile(File file) {
		ArrayList<E> caricato = (ArrayList<E>) ServizioFile.caricaSingoloOggetto(file);
		if(caricato == null) {
			return new ArrayList<E>();
		}
		else {
			return caricato;
		}
		
	}


	@Override
	public <E> void storeMultipleFile(File file, ArrayList<E> lista) {
		ServizioFile.salvaSingoloOggetto(file, lista);
		
		
	}


	@Override
	public Object loadSingleFile(File file) {
		Object caricato = ServizioFile.caricaSingoloOggetto(file);
		if( caricato == null) {
			return new Object();
		}
		else {
			return caricato;
			
		}
		
	}


	@Override
	public void storeSingleFile(File file, Object oggettoDaSalavre) {
		ServizioFile.salvaSingoloOggetto(file, oggettoDaSalavre);
		
	}


	@Override
	public StrutturaRistorante loadStrutturaRistorante(File file) {
		StrutturaRistorante struttura = (StrutturaRistorante) ServizioFile.caricaSingoloOggetto(file);
		if(struttura == null) {
			return new StrutturaRistorante();
		}
		else
			
		return struttura;
	}

	@Override
	public ListaSpesa loadListaSpesa(File file) {
		ListaSpesa lista = (ListaSpesa) ServizioFile.caricaSingoloOggetto(FILE_LISTA_SPESA);
		if(lista == null) {
			return new ListaSpesa();
		}
		else
		return lista;
	}


	

}
