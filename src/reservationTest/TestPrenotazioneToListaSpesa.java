package reservationTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import reservationSystem.*;
import org.junit.jupiter.api.Test;

class TestPrenotazioneToListaSpesa {
	
	
	
	
	
	
	@Test
	void esegui() {
		Ingrediente ingredienteA = new Ingrediente("salame", 11);
		Ingrediente ingredienteB = new Ingrediente("torta", 11);
		Ingrediente ingredienteC = new Ingrediente("bistecca", 11);
		ArrayList<Ingrediente> ingredientiTotali = new ArrayList<>();
		ingredientiTotali.add(ingredienteA);
		ingredientiTotali.add(ingredienteB);
		ingredientiTotali.add(ingredienteC);
		
		ArrayList<Ingrediente> ingredientiSalame = new ArrayList<>();
		ingredientiSalame.add(ingredienteA);
		ArrayList<Ingrediente> ingredientiTorta = new ArrayList<>();
		ingredientiTorta.add(ingredienteB);
		
		ArrayList<Ingrediente> ingredientiBistecca = new ArrayList<>();
		ingredientiBistecca.add(ingredienteC);
		
		GeneriAlimentari grissini = new GeneriAlimentari("grissini", 50);
		GeneriAlimentari pane = new GeneriAlimentari("pane", 50);
		ArrayList<GeneriAlimentari> elencoGeneri = new ArrayList<>();
		elencoGeneri.add(pane);
		elencoGeneri.add(grissini);
		HashMap<GeneriAlimentari, Integer> consumoProCapiteGeneri = new HashMap<>();
		consumoProCapiteGeneri.put(pane, 1);
		consumoProCapiteGeneri.put(grissini, 2);
		
		
		Bevanda acqua = new Bevanda("acqua", 50);
		Bevanda fanta = new Bevanda("fanta", 50);
		ArrayList<Bevanda> elencoBevande = new ArrayList<>();
		elencoBevande.add(acqua);
		elencoBevande.add(fanta);
		HashMap<Bevanda, Integer> consumoBevandeProCapite = new HashMap<>();
		consumoBevandeProCapite.put(acqua, 2);
		consumoBevandeProCapite.put(fanta, 1);
		
		
		HashMap<Piatto, Integer> elencoPiatti  = new HashMap<>();
		Ricetta ricettaSalame = new Ricetta("salame", ingredientiSalame, 0.4, 1, 10);
		Ricetta ricettaTorta = new Ricetta("torta", ingredientiTorta, 0.5, 1, 80);
		Ricetta ricettaBistecca = new Ricetta("bistecca", ingredientiBistecca, 0,4, 1, 13);
		Piatto piattoA = new Piatto(ricettaSalame, LocalDate.now().plusDays(10));
		Piatto piattoB = new Piatto(ricettaTorta, LocalDate.now().plusDays(10));
		Piatto piattoC = new Piatto(ricettaBistecca, LocalDate.now().plusDays(10));
		elencoPiatti.put(piattoA, 6);
		elencoPiatti.put(piattoB, 6);
		elencoPiatti.put(piattoC, 6);
		Prenotazione prenotazione = new Prenotazione(elencoPiatti, 1, LocalDate.now(),  20, 120);
		prenotazione.setNumCoperti(2);
		StrutturaRistorante strutturaRistorante = new StrutturaRistorante();
		strutturaRistorante.setInsiemeGeneri(consumoProCapiteGeneri);
		strutturaRistorante.setInsiemeBevande(consumoBevandeProCapite);
		
		
		
		
		PrenotazioneToListaSpesa prenotazioneToLista = new PrenotazioneToListaSpesa(strutturaRistorante, null,prenotazione);
	ListaSpesa	listaPrenotaione = prenotazioneToLista.esegui();
	
ArrayList<Bevanda> listaBevande =	listaPrenotaione.getElencoBevande();
	assertEquals(2, listaBevande.get(1).getQuantita());
	assertEquals(4, listaBevande.get(0).getQuantita());
	
	ArrayList<GeneriAlimentari> listaGeneri = listaPrenotaione.getElencoGeneri();
	
	assertEquals(4, listaGeneri.get(0).getQuantita());
	assertEquals(2, listaGeneri.get(1).getQuantita());
	
	
	ArrayList<Ingrediente> listaIngredienti = listaPrenotaione.getElencoIngredienti();
	
	for (Ingrediente ingrediente : listaIngredienti) {
		assertEquals(6, ingrediente.getQuantita());
		
	}
		

		
	}
	
	
	
	
	
	
	
	



}
