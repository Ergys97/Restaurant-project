package reservationTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import reservationSystem.*;
import org.junit.jupiter.api.Test;

class TestVario {
	
	RegistroMagazzino reg;
	//private ListaSpesa elencoAlimenti ;
	
	protected ArrayList<Ingrediente> ingredienti = new ArrayList<>();
	protected ArrayList<Bevanda> bevande = new ArrayList<>();
	protected ArrayList<GeneriAlimentari> extra = new ArrayList<>();

	

	@Test
	void testMagazzino() {
		

		ingredienti.add(new Ingrediente("Salame", -5));
		

		bevande.add(new Bevanda("Acqua", 10));
		

		extra.add(new GeneriAlimentari("Grissini", -4));
		extra.get(0).setDataScadenza(LocalDate.now().minusDays(10));
		
		reg = new RegistroMagazzino(ingredienti, bevande, extra, null);

		
		reg.ripristinoIngredientiFiniti(ingredienti);
		reg.ripristinoBevandeFinite(bevande);
		reg.ripristinaGeneriScduti(extra);
		
	
		assertEquals(20, reg.getElencoAlimenti().getElencoIngredienti().get(0).getQuantita());  //test per quantità negative
		assertEquals(30, reg.getElencoAlimenti().getElencoBevande().get(0).getQuantita());   //test aggiunta valore soglia a quelli già esistenti
		assertEquals(true, reg.getElencoAlimenti().getElencoGeneri().get(0).getDataScadenza().isAfter(LocalDate.now())); //aggiornamento data dei generi extra scaduti
		
	}
	
	@Test
	void testPiatto() {
		
		Ricetta ricetta = new Ricetta("Prova", ingredienti, 1.2, 1, 5);
		Piatto piatto = new Piatto(ricetta, LocalDate.now().minusDays(10));
		
		assertEquals(false, piatto.isDisponibile());
		
	}
	
	@Test
	synchronized void testPrenotazione() {
		
		Ricetta ricetta = new Ricetta("Prova", ingredienti, 1.2, 1, 5);
		Piatto piatto = new Piatto(ricetta, LocalDate.now());
		
		HashMap<Piatto, Integer> in = new HashMap<Piatto, Integer>();
		in.put(piatto, 1);
		
		Prenotazione prenotazione1 = new Prenotazione(20, 2);
		Prenotazione prenotazione2 = new Prenotazione(20, -5);
		prenotazione1.getPiattiPrenotati().put(piatto, 1);
		prenotazione2.aggiungiPiatto(in);
		
		assertEquals(true, prenotazione1.getPiattiPrenotati().equals(prenotazione2.getPiattiPrenotati()));
		
		ArrayList<Prenotazione> prenotazioni = new ArrayList<Prenotazione>();
		prenotazioni.clear();
		prenotazioni.add(prenotazione1);
		prenotazioni.add(prenotazione2);
		
		assertEquals(false, prenotazione2.conformeAlCaricoLavoro(prenotazioni));
		
		SerializableFileOperation file = new SerializableFileOperation();
		GestioneElencoPrenotazioni gestione = new GestioneElencoPrenotazioni(file);
		
		prenotazioni.get(0).setData(LocalDate.now().minusDays(30));
		
		gestione.eliminaPrenotazioniPassate(prenotazioni);
		assertEquals(1, prenotazioni.size()); //test metodo che toglie prenotazione passate
		
		file.storeMultipleFile(SerializableFileOperation.FILE_PRENOTAZIONI, prenotazioni);
		
		ArrayList<Prenotazione> prova = new ArrayList<Prenotazione>();
		prova = file.loadMultipleFile(SerializableFileOperation.FILE_PRENOTAZIONI);
		assertEquals(1, prova.size());
		
	//	assertEquals(1,)
		
	}

}
