package reservationSystem;

import java.util.ArrayList;

public class UtilityElenchi {
	
	
	private UtilityTime uTime = new UtilityTime();
	
	private boolean nomiUguali(String x,String y) {
		return x.equalsIgnoreCase(y);
	}
	


	
	public boolean contieneIngrediente(ArrayList<Ingrediente> elencoIngredienti, Ingrediente daCercare) {
		for (Ingrediente ingrediente :elencoIngredienti ) {
			if(nomiUguali(ingrediente.getNome(), daCercare.getNome()))
				return true;
		}
		return false;
	}
	
	public Ingrediente estraiIngrediente(ArrayList<Ingrediente> elencoIngredienti , Ingrediente daCercare) {
		for (Ingrediente ingrediente : elencoIngredienti) {
			if(nomiUguali(ingrediente.getNome(), daCercare.getNome())) {
				return ingrediente;
			}
			
		}
		return null;
	}
	
	public  boolean contieneBevanda(ArrayList<Bevanda> elencoBevande, Bevanda daCercare) {
		for (Bevanda bevanda : elencoBevande) {
			if(nomiUguali(bevanda.getNome(), daCercare.getNome()))
				return true;
			
		}
		return false;
	}
	public Bevanda estraiBevanda(ArrayList<Bevanda> elencoBevande , Bevanda daCercare) {
		for (Bevanda bevanda : elencoBevande) {
			if(nomiUguali(bevanda.getNome(), daCercare.getNome())) {
				return bevanda;
			}
			
		}
		return null;
	}
	public boolean contieneGenere(ArrayList<GeneriAlimentari> elencoGeneri, GeneriAlimentari daCercare) {
		for (GeneriAlimentari generiAlimentari : elencoGeneri) {
				if(nomiUguali(generiAlimentari.getNome(), daCercare.getNome()))
					return true;
			
		}
		return false;
	}
	
	public GeneriAlimentari estraiGenere(ArrayList<GeneriAlimentari> elencoGeneri, GeneriAlimentari daCercare) {
		for (GeneriAlimentari generiAlimentari : elencoGeneri) {
				if(nomiUguali(generiAlimentari.getNome(), daCercare.getNome()))
					return generiAlimentari;
			
		}
		return null;
	}
	
	
	
	
	 
	
	public ArrayList<Piatto> aggiungiPiatto (ArrayList<Piatto> elenco , Piatto daAggiungere){
		
		for (Piatto piatto : elenco) {
			if(nomiUguali(piatto.getNome(), daAggiungere.getNome()))
				return elenco;
			
		}
		elenco.add(daAggiungere);
		return elenco;
		
	}
	
	
	public boolean aggiungiIngrediente( ArrayList<Ingrediente> elenco , Ingrediente daAggiungere) {
			for (Ingrediente ingrediente : elenco) {
				if(nomiUguali(ingrediente.getNome(), daAggiungere.getNome()))
					return false;
			}
			 return elenco.add(daAggiungere);
		
	}



	
	public boolean aggiungiRicettaAElenco ( ArrayList<Ricetta> elenco, Ricetta daAggiungere) {
		for (Ricetta ricetta : elenco) {
			
			if(nomiUguali(ricetta.getNome(), daAggiungere.getNome() ))
				return false;
			
		}
		return elenco.add(daAggiungere);
		
	}
	
	
	public boolean aggiungiMenuAElenco (ArrayList<MenuTematico> elenco, MenuTematico mt) {
		if(!mt.isAccettabile())
			return false;
		for (MenuTematico menuTematico : elenco) {
			if(menuTematico.equals(mt))
				return false;
			
		}
		return elenco.add(mt);
	}
	
	
	public ArrayList<Ingrediente> unisciElenchiIngredienti( ArrayList<Ingrediente> elencoA , ArrayList<Ingrediente> elencoB){
		ArrayList<Ingrediente> ingredientiTotali = new ArrayList<>();
		for (Ingrediente ingrediente : elencoA) {
			if(contieneIngrediente(elencoB, ingrediente)) {
				int quantitaA = ingrediente.getQuantita();
				Ingrediente estratto =   estraiIngrediente(elencoB, ingrediente);
				int quantitaB = estratto.getQuantita();
				elencoB.remove(estratto);
				estratto.setQuantita(quantitaB + quantitaA);
				ingredientiTotali.add(estratto);
			}
			else {
				ingredientiTotali.add(ingrediente);
			}
			
		}
		for (Ingrediente ingrediente : elencoB) {
			ingredientiTotali.add(ingrediente);
			
		}
		
		return ingredientiTotali;
	}
	
	public ArrayList<Piatto> piattiDisponibili(ArrayList<Piatto> elencoPiatti){
		ArrayList<Piatto> piattiDisponibili = new ArrayList<>();
		for (Piatto piatto : elencoPiatti) {
			if(piatto.isDisponibile())
				piattiDisponibili.add(piatto);
		}
		return piattiDisponibili;
	}
	
	public boolean possibilitaAggiungerePiatto(ArrayList<Ricetta> ricette , ArrayList<Piatto> piatti) {
	
		if(ricette.size() == piatti.size() || ricette.isEmpty())
			return false;
		else
			return true;
	}
	
	
	

}
