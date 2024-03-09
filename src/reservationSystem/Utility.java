package reservationSystem;


import java.time.LocalDate;
import java.util.ArrayList;

import reservationUtility.*;



public class Utility {
	
	/*
	 AGGIUNTI A UTILITTY TIME
	public  LocalDate creaDataScadenza()
	{
		return LocalDate.now().plusDays(EstrazioniCasuali.estraiIntero(7, 30));
	}
	public boolean isScaduto(LocalDate date) {
		if(date.isBefore(LocalDate.now()))
			return true;
		return false;
	}
	
	
public LocalDate tempoDurataMenu(int giorni) {
	
	LocalDate data = LocalDate.now().plusDays(giorni);
	return data;
	
		
	}
	
	*/
	
	
	public MenuTematico selezionaMenuTematico(ArrayList<MenuTematico> in , int sel){
		
		return in.get(sel);
	}
	public Piatto selezionaPiatto(ArrayList<Piatto> in , int sel) {
		return in.get(sel);
	}
	
public ArrayList<Ingrediente> selezionaIngredienti(ArrayList<Ingrediente> in , int[] sel){
		
		ArrayList<Ingrediente> ret = new ArrayList<Ingrediente>();
		for(int i =0; i<sel.length;i++)
			ret.add(in.get(sel[i]));
			
		
	return ret;
				
		
	}










/*
 * metodi della classe lista spesa

public ArrayList<Ingrediente> calcolaIngredientiScaduti (ArrayList<Ingrediente> in ){
	
	ArrayList<Ingrediente> scaduti = new ArrayList<Ingrediente>();
	for (Ingrediente ingrediente : in) {
		
		if(isScaduto(ingrediente.getScadenza()))
			scaduti.add(ingrediente);
		
	}
	
	return scaduti;
	
}

public ArrayList<Bevanda> calcolaBevandeScadute (ArrayList<Bevanda> in ){
	
	ArrayList<Bevanda> scaduti = new ArrayList<Bevanda>();
	for (Bevanda bevande : in) {
		
		if(isScaduto(bevande.getScadenza()))
			scaduti.add(bevande);
		
	}
	
	return scaduti;
	
}

public ArrayList<GeneriAlimentari> calcolaGeneriScaduti (ArrayList<GeneriAlimentari> in ){
	
	ArrayList<GeneriAlimentari> scaduti = new ArrayList<GeneriAlimentari>();
	for (GeneriAlimentari gen : in) {
		
		if(isScaduto(gen.getScadenza()))
			scaduti.add(gen);
		
	}
	
	return scaduti;
	
}

*/



public ArrayList<Bevanda> selezionaInsiemeBevande (ArrayList<Integer> selezione, ArrayList<Bevanda> bevande){
	
	ArrayList<Bevanda> ret = new ArrayList<Bevanda>();
	
	for(Integer selezionato : selezione) {
		ret.add(bevande.get(selezionato));
		
	}
	return ret;
}

public ArrayList<GeneriAlimentari> selezionaInsiemeGeneri (ArrayList<Integer> selezione, ArrayList<GeneriAlimentari> generiAlimentari){
	ArrayList<GeneriAlimentari> ret= new ArrayList<GeneriAlimentari>();
	
	for (Integer selezionato : selezione) {
		ret.add(generiAlimentari.get(selezionato));
		
	}
	return ret;
}
/*
public String elencoGeneriAlimntari() {
	ArrayList<GeneriAlimentari> gAlim = GeneriAlimentari.caricaGeneriAlimentari();
	StringBuilder bl = new StringBuilder();
	for (GeneriAlimentari gA : gAlim) {
		bl.append(gA.toString());
		
	}
	return bl.toString();
	
}
/*
 METODI DA SPOSTARE IN UTILITY ELENCHI
public boolean aggiungiIngredienteAElenco( ArrayList<Ingrediente> elenco , Ingrediente ing) {
		for (Ingrediente ingrediente : elenco) {
			if(ingrediente.getNome().equalsIgnoreCase(ing.getNome()))
				return false;
			
		}
		
		 return elenco.add(ing);
	
}



/*
public boolean aggiungiRicettaAElenco ( ArrayList<Ricetta> elenco, Ricetta ric) {
	for (Ricetta ricetta : elenco) {
		
		if(ricetta.getNome().equalsIgnoreCase(ric.getNome()))
			return false;
		
	}
	return elenco.add(ric);
	
}
/*
 * METODI DA SPOSTARE IN UTILITY ELENCHI
public boolean aggiungiMenuAElenco (ArrayList<MenuTematico> elenco, MenuTematico mt) {
	if(!mt.isAccettabile())
		return false;
	for (MenuTematico menuTematico : elenco) {
		if(menuTematico.equals(mt))
			return false;
		
	}
	return elenco.add(mt);
}
*/
public Ricetta estraiRicettadaElenco (ArrayList<Ricetta> elenco , int sel) {
	
	if(sel == -1)
		return null;
	
	try {
		elenco.get(sel);
	}catch (Exception e) {
		return null;
	}
	
	return elenco.get(sel);
}
public boolean ricettaEstrattaCorrettamente(Ricetta r) {
	if(r== null)
		return false;
	else
		return true;
}


/*
 METOD0 DA SPOSTARE E UNIFICARE IN UTILITY ELENCHI
public ArrayList<Piatto> aggiungiPiatto (ArrayList<Piatto> elenco , Piatto p){
	
	for (Piatto piatto : elenco) {
		if(piatto.getNome().equalsIgnoreCase(p.getNome()))
			return elenco;
		
	}
	elenco.add(p);
	return elenco;
	
}
/*
 METODI DA SPOSTARE E UNIFICARE IN UTILITY ELENCHI
public boolean contieneIngrediente (ArrayList<Ingrediente> base , Ingrediente daCercare) {
	for (Ingrediente ingredienteBase : base) {
		if(ingredienteBase.getNome().equalsIgnoreCase(daCercare.getNome()))
			return true;
		
	}
	
	
	
	return false;
}

public boolean contieneGenere (ArrayList<GeneriAlimentari> base , GeneriAlimentari daCercare) {
	for (GeneriAlimentari generiAlimentari : base) {
		if(generiAlimentari.getNome().equalsIgnoreCase(daCercare.getNome())) {
			return true;
		}
		
	}
	return false;
}

public boolean contieneBevanda (ArrayList<Bevanda> base , Bevanda daCercare) {
	for (Bevanda bevanda : base) {
		if(bevanda.getNomeBevanda().equalsIgnoreCase(daCercare.getNomeBevanda())) {
			return true;
		}
		
	}
	return false;
}


 */



}