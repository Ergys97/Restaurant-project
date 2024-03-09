package reservationTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import reservationSystem.Prenotazione;
import org.junit.jupiter.api.Test;
import reservationSystem.*;
class TestPrenotazione {
	
	
	
	
	
	
	
	
	
	@Test
	
	void caricoLavoroPrenotazioniGiornata() {
		Ingrediente ingredienteA = new Ingrediente("salame", 15);
		Ingrediente ingredienteB = new Ingrediente("torta", 4);
		
		
		ArrayList<Ingrediente> ingredienti = new ArrayList<>();
		ingredienti.add(ingredienteA);
		
		
		HashMap<Piatto, Integer> elencoPiatti  = new HashMap<>();
		Ricetta ricetta = new Ricetta("salame", ingredienti, 0.4, 1, 10);
		Piatto piattoA = new Piatto(ricetta, LocalDate.now().plusDays(10));
		elencoPiatti.put(piattoA, 1);
		Prenotazione prenotazione = new Prenotazione(elencoPiatti, 1, LocalDate.now(),  20, 120);
		Prenotazione prenotazioneA = new Prenotazione(elencoPiatti, 1, LocalDate.now(),  20, 120);
		Prenotazione prenotazioneB = new Prenotazione(elencoPiatti, 1, LocalDate.now(), 20, 120);
		ArrayList<Prenotazione> elencoPrenotazioni = new ArrayList<>();
		elencoPrenotazioni.add(prenotazioneA);
		elencoPrenotazioni.add(prenotazioneB);
	double caricoLavoroCalcolato = 	  prenotazione.caricoLavoroPrenotazioniGiornata(LocalDate.now(), elencoPrenotazioni);
	double caricoAspettato = 0.8;
	assertEquals(caricoAspettato, caricoLavoroCalcolato);
		
	}
	
	
@Test
	
	void conformeAlCaricoDiLavoro() {
		Ingrediente ingredienteA = new Ingrediente("salame", 15);
		Ingrediente ingredienteB = new Ingrediente("torta", 4);
		
		
		ArrayList<Ingrediente> ingredienti = new ArrayList<>();
		ingredienti.add(ingredienteA);
		
		
		HashMap<Piatto, Integer> elencoPiatti  = new HashMap<>();
		Ricetta ricetta = new Ricetta("salame", ingredienti, 0.4, 1, 10);
		Piatto piattoA = new Piatto(ricetta, LocalDate.now().plusDays(10));
		elencoPiatti.put(piattoA, 1);
		Prenotazione prenotazione = new Prenotazione(elencoPiatti, 1, LocalDate.now(),  20, 120);
		Prenotazione prenotazioneA = new Prenotazione(elencoPiatti, 1, LocalDate.now(),  20, 120);
		Prenotazione prenotazioneB = new Prenotazione(elencoPiatti, 1, LocalDate.now(), 20, 120);
		ArrayList<Prenotazione> elencoPrenotazioni = new ArrayList<>();
		elencoPrenotazioni.add(prenotazioneA);
		elencoPrenotazioni.add(prenotazioneB);
	 boolean conforme =	prenotazione.conformeAlCaricoLavoro(elencoPrenotazioni);
			
		
	assertTrue(conforme);
		
	}
	
	
	
	
	
	
	
	
	
	

	

}
