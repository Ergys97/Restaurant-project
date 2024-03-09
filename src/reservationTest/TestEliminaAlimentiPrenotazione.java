package reservationTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import reservationSystem.*;

class TestEliminaAlimentiPrenotazione {
	
	
	
	
	@Test
	
	void diminuisciIngredientiPiatti() {
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
		EliminaAlimentiPrenotazione eliminaAlimenti = new EliminaAlimentiPrenotazione(prenotazione,null, null, null, null, null);
		ArrayList<Ingrediente> ingredientiModificati = eliminaAlimenti.diminuisciIngredientiPiatti(ingredientiTotali);
	
		
		for (Ingrediente ingrediente : ingredientiTotali) {
				for (Ingrediente ingredienteModificato : ingredientiModificati) {
					if(ingrediente.getNome().equalsIgnoreCase(ingredienteModificato.getNome())) {
						assertEquals(5, ingredienteModificato.getQuantita());
					}
				}
			
		}
		
	}
	
	@Test
	void diminuisciIngredientiMenu() {
		Ingrediente ingredienteA = new Ingrediente("salame", 10);
		Ingrediente ingredienteB = new Ingrediente("torta", 10);
		Ingrediente ingredienteC = new Ingrediente("bistecca", 10);
		
		
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
		
		
	
		Ricetta ricettaSalame = new Ricetta("salame", ingredientiSalame, 0.4, 1, 10);
		Ricetta ricettaTorta = new Ricetta("torta", ingredientiTorta, 0.5, 1, 80);
		Ricetta ricettaBistecca = new Ricetta("bistecca", ingredientiBistecca, 0,4, 1, 13);
		Piatto piattoA = new Piatto(ricettaSalame, LocalDate.now().plusDays(10));
		Piatto piattoB = new Piatto(ricettaTorta, LocalDate.now().plusDays(10));
		Piatto piattoC = new Piatto(ricettaBistecca, LocalDate.now().plusDays(10));
		ArrayList<Piatto> piattiMenu = new ArrayList<>();
		piattiMenu.add(piattoA);
		piattiMenu.add(piattoC);
		piattiMenu.add(piattoB);
		MenuTematico menu = new MenuTematico(piattiMenu, LocalDate.now().plusDays(10), 2, "carne mista");
		HashMap<MenuTematico, Integer> elencoMenu = new HashMap<>();
		elencoMenu.put(menu, 2);
		Prenotazione prenotazione = new Prenotazione(elencoMenu, LocalDate.now().plusDays(10), 2, 20, 120);
		EliminaAlimentiPrenotazione eliminaAlimenti = new EliminaAlimentiPrenotazione(prenotazione,null, null, null, null, null);
	ArrayList<Ingrediente> ingredientiModificati = 	eliminaAlimenti.diminuisciIngredientiMenuTematici(ingredientiTotali);
	
	for (Ingrediente ingrediente : ingredientiTotali) {
		for (Ingrediente ingredienteModificato : ingredientiModificati) {
			if(ingrediente.getNome().equalsIgnoreCase(ingredienteModificato.getNome()))
				assertEquals(8, ingredienteModificato.getQuantita());
			
		}
		
	}
	
	
	}
	
	
@Test 

	
	void diminuisciQuantitaBevande() {
		Bevanda acqua = new Bevanda("acqua", 50);
		Bevanda fanta = new Bevanda("fanta", 50);
		ArrayList<Bevanda> elencoBevande = new ArrayList<>();
		elencoBevande.add(acqua);
		elencoBevande.add(fanta);
		HashMap<Bevanda, Integer> consumoBevandeProCapite = new HashMap<>();
		consumoBevandeProCapite.put(acqua, 2);
		consumoBevandeProCapite.put(fanta, 1);
		Prenotazione prenotazione = new Prenotazione(20,120);
		prenotazione.setNumCoperti(2);
		StrutturaRistorante strutturaRistorante = new StrutturaRistorante();
		strutturaRistorante.setInsiemeBevande(consumoBevandeProCapite);
		EliminaAlimentiPrenotazione eliminaAlimenti = new EliminaAlimentiPrenotazione(prenotazione,null, strutturaRistorante, null, null, null);
		 ArrayList<Bevanda>  bevandeModificate =  eliminaAlimenti.diminuisciQuantitaBevande(elencoBevande);
		 assertEquals(46, bevandeModificate.get(0).getQuantita());
		 assertEquals(48, bevandeModificate.get(1).getQuantita());
	}


@Test

void diminuisciQuantitaGeneri() {
	
	GeneriAlimentari grissini = new GeneriAlimentari("grissini", 50);
	GeneriAlimentari pane = new GeneriAlimentari("pane", 50);
	ArrayList<GeneriAlimentari> elencoGeneri = new ArrayList<>();
	elencoGeneri.add(pane);
	elencoGeneri.add(grissini);
	HashMap<GeneriAlimentari, Integer> consumoProCapiteGeneri = new HashMap<>();
	consumoProCapiteGeneri.put(pane, 1);
	consumoProCapiteGeneri.put(grissini, 2);
	Prenotazione prenotazione = new Prenotazione(20, 120);
	prenotazione.setNumCoperti(3);
	StrutturaRistorante strutturaRistorante = new StrutturaRistorante();
	strutturaRistorante.setInsiemeGeneri(consumoProCapiteGeneri);
	EliminaAlimentiPrenotazione eliminaAlimenti = new EliminaAlimentiPrenotazione(prenotazione,null, strutturaRistorante, null, null, null);
	ArrayList<GeneriAlimentari> generiModificati = eliminaAlimenti.diminuisciQuantitaGeneriAlimentari(elencoGeneri);
	assertEquals(44, generiModificati.get(0).getQuantita());
	 assertEquals(47, generiModificati.get(1).getQuantita());
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

	

}
