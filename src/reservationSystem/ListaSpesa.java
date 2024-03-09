package reservationSystem;

import java.io.Serializable;
import java.util.ArrayList;




public class ListaSpesa implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private static final int SOGLIA = 5;
	private ArrayList<Ingrediente> elencoIngredienti;
	private ArrayList<Bevanda> elencoBevande;
	private ArrayList<GeneriAlimentari> elencoGeneri;
	
	
	public ListaSpesa(ArrayList<Ingrediente> elencoIngredienti, ArrayList<Bevanda> elencoBevande , ArrayList<GeneriAlimentari> elencoGeneri) {
		this.elencoGeneri = elencoGeneri;
		this.elencoBevande = elencoBevande;
		this.elencoIngredienti = elencoIngredienti;
	}
	
	public ListaSpesa () {
		this.elencoBevande = new ArrayList<>();
		this.elencoIngredienti = new ArrayList<>();
		this.elencoGeneri = new ArrayList<>();
	}
	


	
	public ListaSpesa creaListaElementiFiniti() {
		ArrayList<Ingrediente> ingFin = individuaIngredientiFiniti();
		ArrayList<Bevanda> bevFin = individuaBevandeFinite();
		ArrayList<GeneriAlimentari> genFin = individuaGeneriAlimentariFiniti();
		return new ListaSpesa(ingFin, bevFin, genFin);
	}
	
	public ListaSpesa creaListaElementiNonFiniti() {
		UtilityElenchi uElenchi = new UtilityElenchi();
		ListaSpesa listaFiniti = this.creaListaElementiFiniti();
		for (Bevanda bevandaFinita : listaFiniti.elencoBevande) {
			if(uElenchi.contieneBevanda(this.elencoBevande, bevandaFinita)) {
				this.elencoBevande.remove(bevandaFinita);
			}
			
		}
		for (Ingrediente ingredienteFinito : listaFiniti.elencoIngredienti) {
			if(uElenchi.contieneIngrediente(this.elencoIngredienti, ingredienteFinito)) {
				this.elencoIngredienti.remove(ingredienteFinito);
			}
			
		}
		
		for(GeneriAlimentari genereFinito : listaFiniti.elencoGeneri) {
			this.elencoGeneri.remove(genereFinito);
		}
		
		return new ListaSpesa(this.elencoIngredienti, this.elencoBevande, this.elencoGeneri);
		
	}
	
	
	public ListaSpesa creaListaElementiNonScaduti() {
		UtilityElenchi uElenchi = new UtilityElenchi();
		ListaSpesa listaScaduti = this.creaListaElementiScaduti();
		for (Bevanda bevandaScaduta : listaScaduti.getElencoBevande()) {
			if(uElenchi.contieneBevanda(this.elencoBevande, bevandaScaduta)) {
				this.elencoBevande.remove(bevandaScaduta);
			}
			
			
		}
		
		for (Ingrediente ingredienteScaduto : listaScaduti.elencoIngredienti) {
			if(uElenchi.contieneIngrediente(this.elencoIngredienti, ingredienteScaduto)) {
				this.elencoIngredienti.remove(ingredienteScaduto);
			}
			
		}
		for (GeneriAlimentari genereScaduto : listaScaduti.elencoGeneri) {
			if(uElenchi.contieneGenere(this.elencoGeneri, genereScaduto)) {
				this.elencoGeneri.remove(genereScaduto);
			}
			
		}
		
		return new ListaSpesa(this.elencoIngredienti,this.elencoBevande,this.elencoGeneri);
	}
	
	public ListaSpesa creaListaElementiScaduti() {

		ArrayList<Ingrediente> ingScad = individuaIngredientiScaduti();
		ArrayList<Bevanda> bevScad = individuaBevandeScadute();
		ArrayList<GeneriAlimentari> genScad = individuaGeneriScaduti();
		return new ListaSpesa(ingScad, bevScad, genScad);
	
	}
	
	
	
	public ArrayList<Ingrediente> individuaIngredientiFiniti(){
		ArrayList<Ingrediente> ingredientiFiniti = new ArrayList<>();
		for (Ingrediente ingrediente : this.elencoIngredienti) {
				if(ingrediente.getQuantita() <= SOGLIA) {
					ingredientiFiniti.add(ingrediente);
				}
			
			
		}
		return ingredientiFiniti;
	}
	
	private ArrayList<Bevanda> individuaBevandeFinite(){
		ArrayList<Bevanda> bevandeFinite = new ArrayList<>();
		for (Bevanda bevanda : this.elencoBevande) {
			if(bevanda.getQuantita() <= SOGLIA) {
				bevandeFinite.add(bevanda);
			}
			
		}
		
		return bevandeFinite;
	}
	
	private ArrayList<GeneriAlimentari> individuaGeneriAlimentariFiniti(){
		ArrayList<GeneriAlimentari> generiFiniti = new ArrayList<>();
		for (GeneriAlimentari generiAlimentari : this.elencoGeneri) {
			if(generiAlimentari.getQuantita() <= SOGLIA) {
				generiFiniti.add(generiAlimentari);
			}
			
		}
		return generiFiniti;
	}
	
	public void aggiungiElementiDaListaSpesa(ListaSpesa lp) {
		ArrayList<Ingrediente> ingredientiDaAggiungere = lp.getElencoIngredienti();
		ArrayList<Bevanda> bevandeDaAggiungere = lp.getElencoBevande();
		ArrayList<GeneriAlimentari> generiDaAggiungere = lp.getElencoGeneri();
		this.aggiungiGeneriDaListaSpesa(generiDaAggiungere);
		this.aggiungiBevandeDaListaSpesa(bevandeDaAggiungere);
		this.aggiungiIngredientiDaListaSpesa(ingredientiDaAggiungere);
	}
	
	
	private void aggiungiGeneriDaListaSpesa(ArrayList<GeneriAlimentari> generiDaAggiungere) {
		UtilityElenchi uElenchi = new UtilityElenchi();
		for (GeneriAlimentari genereDaAggiungere : generiDaAggiungere) {
			if(!uElenchi.contieneGenere(this.elencoGeneri, genereDaAggiungere)) {
				this.getElencoGeneri().add(genereDaAggiungere);
			}
			else {
						GeneriAlimentari genereEstratto=uElenchi.estraiGenere(this.elencoGeneri, genereDaAggiungere);
						int oldValue = genereEstratto.getQuantita();
		 				int  newValue = oldValue + genereDaAggiungere.getQuantita();
		 				this.elencoGeneri.remove(genereEstratto);
		 				genereEstratto.setQuantita(newValue);
		 				this.elencoGeneri.add(genereEstratto);
						
					}
					
				}
		}
	
	private void aggiungiBevandeDaListaSpesa(ArrayList<Bevanda> bevandeDaAggiungere) {
		UtilityElenchi uElenchi = new UtilityElenchi();
		for (Bevanda bevandaDaAggiungere : bevandeDaAggiungere) {
			
			if(!uElenchi.contieneBevanda(this.elencoBevande, bevandaDaAggiungere)) {
				this.elencoBevande.add(bevandaDaAggiungere);
			}
			else {
				Bevanda bevandaEstratta = uElenchi.estraiBevanda(this.elencoBevande, bevandaDaAggiungere);
				int oldValue = bevandaEstratta.getQuantita();
				int newValue = oldValue +bevandaDaAggiungere.getQuantita();
				this.elencoBevande.remove(bevandaEstratta);
				bevandaEstratta.setQuantita(newValue);
				this.elencoBevande.add(bevandaEstratta);
				
			}
			
		}
	}
	
	private void aggiungiIngredientiDaListaSpesa(ArrayList<Ingrediente> ingredientiDaAggiungere) {
		UtilityElenchi uElenchi = new UtilityElenchi();
		for(Ingrediente ingredienteDaAggiungere : ingredientiDaAggiungere) {
			if(!uElenchi.contieneIngrediente(this.elencoIngredienti, ingredienteDaAggiungere)) {
				this.elencoIngredienti.add(ingredienteDaAggiungere);
			}
			else {
				  Ingrediente ingredienteEstratto = uElenchi.estraiIngrediente(this.elencoIngredienti, ingredienteDaAggiungere);
				  int oldValue = ingredienteEstratto.getQuantita();
				  int newValue = oldValue + ingredienteDaAggiungere.getQuantita();
				  this.elencoIngredienti.remove(ingredienteEstratto);
				  ingredienteEstratto.setQuantita(newValue);
				  this.elencoIngredienti.add(ingredienteEstratto);
			}
		}
	}
	
	
	
	
	public ArrayList<Ingrediente> individuaIngredientiScaduti ( )
	{
		UtilityTime uTime = new UtilityTime();
		ArrayList<Ingrediente> ingredientiScaduti = new ArrayList<>();
		for (Ingrediente ingrediente : elencoIngredienti) {
				if(uTime.isAlimentoScaduto(ingrediente))
				ingredientiScaduti.add(ingrediente);
			
			
		}
		return ingredientiScaduti;
		
	}
	
	public ArrayList<Bevanda> individuaBevandeScadute(){
		UtilityTime uTime = new UtilityTime();
		ArrayList<Bevanda> bevandeScadute = new ArrayList<>();
		for (Bevanda bevanda : elencoBevande) {
			if(uTime.isAlimentoScaduto(bevanda))
				bevandeScadute.add(bevanda);
			
		}
		return bevandeScadute;
		
	}
	
	
	private ArrayList<GeneriAlimentari> individuaGeneriScaduti (){
		UtilityTime uTime = new UtilityTime();
		ArrayList<GeneriAlimentari> generiScaduti = new ArrayList<>();
		for (GeneriAlimentari generiAlimentari : elencoGeneri) {
			if(uTime.isAlimentoScaduto(generiAlimentari))
				generiScaduti.add(generiAlimentari);
			
		}
		
		return generiScaduti;
	}
	
	public ArrayList<Ingrediente> getElencoIngredienti() {
		return elencoIngredienti;
	}
	public void setElencoIngredienti(ArrayList<Ingrediente> elencoIngredienti) {
		this.elencoIngredienti = elencoIngredienti;
	}
	public ArrayList<Bevanda> getElencoBevande() {
		return elencoBevande;
	}
	public void setElencoBevande(ArrayList<Bevanda> elencoBevande) {
		this.elencoBevande = elencoBevande;
	}
	public ArrayList<GeneriAlimentari> getElencoGeneri() {
		return elencoGeneri;
	}
	public void setElencoGeneri(ArrayList<GeneriAlimentari> elencoGeneri) {
		this.elencoGeneri = elencoGeneri;
	}

	public boolean isVuota() {
		if(this.elencoIngredienti.isEmpty() && this.elencoBevande.isEmpty() && this.elencoGeneri.isEmpty())
			return true;
		else
		 return false;
	}
	
}

