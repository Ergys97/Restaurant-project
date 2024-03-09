package reservationTest;
import reservationSystem.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import reservationSystem.ListaSpesa;
import org.junit.jupiter.api.Test;

class TestListaSpesa {

	@Test
	void testAggiungiIngredientiDaListaSpesa() {
		ArrayList<Ingrediente> ingredientiA = new  ArrayList<>();
		ArrayList<Bevanda> bevandeA = new ArrayList<>();
		ArrayList<GeneriAlimentari> generiA = new ArrayList<>();
		ArrayList<Ingrediente> ingredientiB = new  ArrayList<>();
		ArrayList<Bevanda> bevandeB = new ArrayList<>();
		ArrayList<GeneriAlimentari> generiB = new ArrayList<>();
		ingredientiA.add(new Ingrediente("ingA", 1));
		ingredientiB.add(new Ingrediente("ingB", 1));
		bevandeA.add(new Bevanda("bevA", 1));
		bevandeB.add(new Bevanda("bevB", 1));
		generiA.add(new GeneriAlimentari("genA", 1));
		generiB.add(new GeneriAlimentari("genB", 1));
		ArrayList<Ingrediente> ingredientiTot = new  ArrayList<>();
		ArrayList<Bevanda> bevandeTot = new ArrayList<>();
		ArrayList<GeneriAlimentari> generiTot = new ArrayList<>();
		ingredientiTot.add(new Ingrediente("ingA", 1));
		ingredientiTot.add(new Ingrediente("ingB", 1));
		bevandeTot.add(new Bevanda("bevA", 1));
		bevandeTot.add(new Bevanda("bevB", 1));
		generiTot.add(new GeneriAlimentari("genA", 1));
		generiTot.add(new GeneriAlimentari("genB", 1));
		
		
		
		
		
		ListaSpesa listaTot = new ListaSpesa(ingredientiTot, bevandeTot, generiTot);
		ListaSpesa  listaA = new ListaSpesa(ingredientiA,bevandeA,generiA);
		ListaSpesa listaB = new ListaSpesa(ingredientiB, bevandeB, generiB);
		listaA.aggiungiElementiDaListaSpesa(listaB);
		
		
		
		
		assertEquals(listaTot.getElencoIngredienti().get(0).getNome(), listaA.getElencoIngredienti().get(0).getNome());
		assertEquals(listaTot.getElencoIngredienti().get(0).getQuantita(), listaA.getElencoIngredienti().get(0).getQuantita());
		assertEquals(listaTot.getElencoIngredienti().get(1).getNome(), listaA.getElencoIngredienti().get(1).getNome());
		assertEquals(listaTot.getElencoIngredienti().get(1).getQuantita(), listaA.getElencoIngredienti().get(1).getQuantita());
		assertEquals(listaTot.getElencoBevande().get(0).getNome(), listaA.getElencoBevande().get(0).getNome());
		assertEquals(listaTot.getElencoBevande().get(0).getQuantita(), listaA.getElencoBevande().get(0).getQuantita());
		assertEquals(listaTot.getElencoBevande().get(1).getNome(), listaA.getElencoBevande().get(1).getNome());
		assertEquals(listaTot.getElencoBevande().get(1).getQuantita(), listaA.getElencoBevande().get(1).getQuantita());
		assertEquals(listaTot.getElencoGeneri().get(0).getNome(), listaA.getElencoGeneri().get(0).getNome());
		assertEquals(listaTot.getElencoGeneri().get(0).getQuantita(), listaA.getElencoGeneri().get(0).getQuantita());
		assertEquals(listaTot.getElencoGeneri().get(1).getNome(), listaA.getElencoGeneri().get(1).getNome());
		assertEquals(listaTot.getElencoGeneri().get(1).getQuantita(), listaA.getElencoGeneri().get(1).getQuantita());
	}
	
	
	@Test 
	
	 void individuaIngredientiScadutiListaSpesa(){
		
		Ingrediente ingredienteNonScaduto = new Ingrediente("salame", 5, LocalDate.now().plusDays(5));
		Ingrediente ingredienteScaduto = new Ingrediente("torta", 5, LocalDate.now().minusDays(5));
		ArrayList<Ingrediente> ingredientiTotali = new ArrayList<>();
		ArrayList<Ingrediente> ingredientiScaduti = new ArrayList<>();
		ingredientiScaduti.add(ingredienteScaduto);
		ingredientiTotali.add(ingredienteNonScaduto);
		ingredientiTotali.add(ingredienteScaduto);
		
		ListaSpesa listaTotale = new ListaSpesa(ingredientiTotali,null,null);
		ArrayList<Ingrediente>  elencoIngredientiScaduti =   listaTotale.individuaIngredientiScaduti();
		assertEquals(ingredientiScaduti.get(0).getNome(), elencoIngredientiScaduti.get(0).getNome());
		
	}
	
	@Test 
	
	 void individuaBevandeScaduteListaSpesa(){
		
		Bevanda bevandeNonScaduto = new Bevanda("acqua", 5, LocalDate.now().plusDays(5));
		Bevanda bevandeScaduto = new Bevanda("cola", 5, LocalDate.now().minusDays(5));
		ArrayList<Bevanda>bevandeTotali = new ArrayList<>();
		ArrayList<Bevanda> bevandeScadute = new ArrayList<>();
		bevandeTotali.add(bevandeScaduto);
		bevandeTotali.add(bevandeNonScaduto);
		bevandeScadute.add(bevandeScaduto);
		
		
		ListaSpesa lista = new  ListaSpesa(null,bevandeTotali,null);
   ArrayList<Bevanda> elencoBevandeScadute =		 lista.individuaBevandeScadute() ;
		assertEquals(bevandeScadute.get(0).getNome(),  elencoBevandeScadute.get(0).getNome());
		
	}
	
	
	@Test
	
	void individuaIngredientiFinitiListaSpesa() {
		
		Ingrediente ingredienteNonFinito = new Ingrediente("salame", 15);
		Ingrediente ingredienteFinito = new Ingrediente("torta", 4);
		ArrayList<Ingrediente> ingredientiTotali = new ArrayList<>();
		ArrayList<Ingrediente> ingredientiFiniti = new ArrayList<>();
		ingredientiTotali.add(ingredienteFinito);
		ingredientiTotali.add(ingredienteNonFinito);
		ingredientiFiniti.add(ingredienteFinito);
		
		ListaSpesa listaTotale = new ListaSpesa(ingredientiTotali,null,null);
		ArrayList<Ingrediente>  elencoIngredientiFiniti =   listaTotale.individuaIngredientiFiniti();
		assertEquals(ingredientiFiniti.get(0).getNome(), elencoIngredientiFiniti.get(0).getNome());
		
		
	}
	
	
	@Test 
	 void creaListaAlmentiFiniti(){
		Ingrediente ingredienteNonFinito = new Ingrediente("salame", 15);
		Ingrediente ingredienteFinito = new Ingrediente("torta", 4);
		Bevanda bevandeFinito = new Bevanda("acqua", 2);
		Bevanda bevandeNonFinito = new Bevanda("cola", 15);
		GeneriAlimentari genereFinito = new GeneriAlimentari("pane", 3);
		GeneriAlimentari genereNonFinito = new GeneriAlimentari("grissini", 12);
		
		ArrayList<Ingrediente> ingredientiTotali = new ArrayList<>();
		ArrayList<Ingrediente> ingredientiFiniti = new ArrayList<>();
		ArrayList<Bevanda> bevandeTotali = new ArrayList<>();
		ArrayList<Bevanda> bevandeFinite = new ArrayList<>();
		ArrayList<GeneriAlimentari> generiTotali = new ArrayList<>();
		ArrayList<GeneriAlimentari> generiFiniti = new ArrayList<>();
		
		ingredientiTotali.add(ingredienteFinito);
		ingredientiTotali.add(ingredienteNonFinito);
		ingredientiFiniti.add(ingredienteFinito);
		bevandeTotali.add(bevandeNonFinito);
		bevandeTotali.add(bevandeFinito);
		bevandeFinite.add(bevandeFinito);
		generiTotali.add(genereNonFinito);
		generiTotali.add(genereFinito);
		generiFiniti.add(genereFinito);
		
		ListaSpesa listaTotali = new ListaSpesa(ingredientiTotali, bevandeTotali, generiTotali);
		ListaSpesa listaFinitiDaTotali = listaTotali.creaListaElementiFiniti();
		
		assertEquals(listaFinitiDaTotali.getElencoIngredienti().get(0).getNome(), ingredientiFiniti.get(0).getNome());
		assertEquals(listaFinitiDaTotali.getElencoIngredienti().get(0).getQuantita(), ingredientiFiniti.get(0).getQuantita());
		assertEquals(listaFinitiDaTotali.getElencoBevande().get(0).getNome(), bevandeFinite.get(0).getNome());
		assertEquals(listaFinitiDaTotali.getElencoBevande().get(0).getQuantita(), bevandeFinite.get(0).getQuantita());
		assertEquals(listaFinitiDaTotali.getElencoGeneri().get(0).getNome(), generiFiniti.get(0).getNome());
		assertEquals(listaFinitiDaTotali.getElencoGeneri().get(0).getQuantita(), generiFiniti.get(0).getQuantita());
	}
	
	
	@Test 
	
	void creaListaAlimentiNonFiniti() {
		Ingrediente ingredienteNonFinito = new Ingrediente("salame", 15);
		Ingrediente ingredienteFinito = new Ingrediente("torta", 4);
		Bevanda bevandeFinito = new Bevanda("acqua", 2);
		Bevanda bevandeNonFinito = new Bevanda("cola", 15);
		GeneriAlimentari genereFinito = new GeneriAlimentari("pane", 3);
		GeneriAlimentari genereNonFinito = new GeneriAlimentari("grissini", 12);
		
		ArrayList<Ingrediente> ingredientiTotali = new ArrayList<>();
		ArrayList<Ingrediente> ingredientiNonFiniti = new ArrayList<>();
		ArrayList<Bevanda> bevandeTotali = new ArrayList<>();
		ArrayList<Bevanda> bevandeNonFinite = new ArrayList<>();
		ArrayList<GeneriAlimentari> generiTotali = new ArrayList<>();
		ArrayList<GeneriAlimentari> generiNonFiniti = new ArrayList<>();
		
		ingredientiTotali.add(ingredienteFinito);
		ingredientiTotali.add(ingredienteNonFinito);
		ingredientiNonFiniti.add(ingredienteNonFinito);
		bevandeTotali.add(bevandeNonFinito);
		bevandeTotali.add(bevandeFinito);
		bevandeNonFinite.add(bevandeNonFinito);
		generiTotali.add(genereNonFinito);
		generiTotali.add(genereFinito);
		generiNonFiniti.add(genereNonFinito);
		
		ListaSpesa listaTotali = new ListaSpesa(ingredientiTotali, bevandeTotali, generiTotali);
		ListaSpesa listaFinitiDaTotali = listaTotali.creaListaElementiNonFiniti();
		
		assertEquals(listaFinitiDaTotali.getElencoIngredienti().get(0).getNome(), ingredientiNonFiniti.get(0).getNome());
		assertEquals(listaFinitiDaTotali.getElencoIngredienti().get(0).getQuantita(), ingredientiNonFiniti.get(0).getQuantita());
		assertEquals(listaFinitiDaTotali.getElencoBevande().get(0).getNome(), bevandeNonFinite.get(0).getNome());
		assertEquals(listaFinitiDaTotali.getElencoBevande().get(0).getQuantita(), bevandeNonFinite.get(0).getQuantita());
		assertEquals(listaFinitiDaTotali.getElencoGeneri().get(0).getNome(), generiNonFiniti.get(0).getNome());
		assertEquals(listaFinitiDaTotali.getElencoGeneri().get(0).getQuantita(), generiNonFiniti.get(0).getQuantita());
		
	}
	
@Test 
	
	void creaListaAlimentiScaduti() {
		Ingrediente ingredienteScaduto = new Ingrediente("salame", 15, LocalDate.now().minusDays(5));
		Ingrediente ingredienteNonScaduto = new Ingrediente("torta", 4, LocalDate.now());
		Bevanda bevandaScaduta = new Bevanda("acqua", 2,LocalDate.now().minusDays(5));
		Bevanda bevandaNonScaduta = new Bevanda("cola", 15, LocalDate.now());
		GeneriAlimentari genereScaduto = new GeneriAlimentari("pane", 3,LocalDate.now().minusDays(5));
		GeneriAlimentari genereNonScaduto = new GeneriAlimentari("grissini", 12, LocalDate.now());
		
		ArrayList<Ingrediente> ingredientiTotali = new ArrayList<>();
		ArrayList<Ingrediente> ingredientiScaduti = new ArrayList<>();
		ArrayList<Bevanda> bevandeTotali = new ArrayList<>();
		ArrayList<Bevanda> bevandeScadute = new ArrayList<>();
		ArrayList<GeneriAlimentari> generiTotali = new ArrayList<>();
		ArrayList<GeneriAlimentari> generiScaduti = new ArrayList<>();
		
		ingredientiTotali.add(ingredienteNonScaduto);
		ingredientiTotali.add(ingredienteScaduto);
		ingredientiScaduti.add(ingredienteScaduto);
		bevandeTotali.add(bevandaNonScaduta);
		bevandeTotali.add(bevandaScaduta);
		bevandeScadute.add(bevandaScaduta);
		generiTotali.add(genereNonScaduto);
		generiTotali.add(genereScaduto);
		generiScaduti.add(genereScaduto);
		
		ListaSpesa listaTotali = new ListaSpesa(ingredientiTotali, bevandeTotali, generiTotali);
		ListaSpesa listaScaduti = listaTotali.creaListaElementiScaduti();
		
		assertEquals(listaScaduti.getElencoIngredienti().get(0).getNome(), ingredientiScaduti.get(0).getNome());
		assertEquals(listaScaduti.getElencoIngredienti().get(0).getQuantita(), ingredientiScaduti.get(0).getQuantita());
		assertEquals(listaScaduti.getElencoBevande().get(0).getNome(), bevandeScadute.get(0).getNome());
		assertEquals(listaScaduti.getElencoBevande().get(0).getQuantita(), bevandeScadute.get(0).getQuantita());
		assertEquals(listaScaduti.getElencoGeneri().get(0).getNome(), generiScaduti.get(0).getNome());
		assertEquals(listaScaduti.getElencoGeneri().get(0).getQuantita(), generiScaduti.get(0).getQuantita());
		
	}
	

@Test 

void creaListaAlimentiNonScaduti() {
	Ingrediente ingredienteScaduto = new Ingrediente("salame", 15, LocalDate.now().minusDays(5));
	Ingrediente ingredienteNonScaduto = new Ingrediente("torta", 4, LocalDate.now());
	Bevanda bevandaScaduta = new Bevanda("acqua", 2,LocalDate.now().minusDays(5));
	Bevanda bevandaNonScaduta = new Bevanda("cola", 15, LocalDate.now());
	GeneriAlimentari genereScaduto = new GeneriAlimentari("pane", 3,LocalDate.now().minusDays(5));
	GeneriAlimentari genereNonScaduto = new GeneriAlimentari("grissini", 12, LocalDate.now());
	
	ArrayList<Ingrediente> ingredientiTotali = new ArrayList<>();
	ArrayList<Ingrediente> ingredientiNonScaduti = new ArrayList<>();
	ArrayList<Bevanda> bevandeTotali = new ArrayList<>();
	ArrayList<Bevanda> bevandeNonScadute = new ArrayList<>();
	ArrayList<GeneriAlimentari> generiTotali = new ArrayList<>();
	ArrayList<GeneriAlimentari> generiNonScaduti = new ArrayList<>();
	
	ingredientiTotali.add(ingredienteNonScaduto);
	ingredientiTotali.add(ingredienteScaduto);
	ingredientiNonScaduti.add(ingredienteNonScaduto);
	bevandeTotali.add(bevandaNonScaduta);
	bevandeTotali.add(bevandaScaduta);
	bevandeNonScadute.add(bevandaNonScaduta);
	generiTotali.add(genereNonScaduto);
	generiTotali.add(genereScaduto);
	generiNonScaduti.add(genereNonScaduto);
	
	ListaSpesa listaTotali = new ListaSpesa(ingredientiTotali, bevandeTotali, generiTotali);
	ListaSpesa listaNonScaduti = listaTotali.creaListaElementiNonScaduti();
	
	assertEquals(listaNonScaduti.getElencoIngredienti().get(0).getNome(), ingredientiNonScaduti.get(0).getNome());
	assertEquals(listaNonScaduti.getElencoIngredienti().get(0).getQuantita(), ingredientiNonScaduti.get(0).getQuantita());
	assertEquals(listaNonScaduti.getElencoBevande().get(0).getNome(), bevandeNonScadute.get(0).getNome());
	assertEquals(listaNonScaduti.getElencoBevande().get(0).getQuantita(), bevandeNonScadute.get(0).getQuantita());
	assertEquals(listaNonScaduti.getElencoGeneri().get(0).getNome(), generiNonScaduti.get(0).getNome());
	assertEquals(listaNonScaduti.getElencoGeneri().get(0).getQuantita(), generiNonScaduti.get(0).getQuantita());
	
}
	
		
		
		
		
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	


