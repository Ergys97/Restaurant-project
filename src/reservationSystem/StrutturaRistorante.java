package reservationSystem;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import reservationUtility.*;

public class StrutturaRistorante implements Serializable{

	private static final String FILEPATH_STRUTTURA_RISTORANTE = "StrutturaRistorante.txt";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int numPostiASedere;
	private double caricoLavoroXPersona;
	private double caricoLavoroSostenibile; 
	private ArrayList<Bevanda> bevande;
	private ArrayList<GeneriAlimentari> alimentiExtra;
	private ArrayList<Ingrediente> ingredienti;
	private HashMap<Bevanda, Integer> insiemeBevande;
	private HashMap<GeneriAlimentari, Integer> insiemeGeneri;
	
	





	private boolean inizializzato; 

	/*
	public StrutturaRistorante(int numPostiASedere, double caricoLavoroXPersona, 
			ArrayList<Bevanda> insiemeBevande, ArrayList<GeneriAlimentari> insiemeGeneri, ArrayList<Integer> consumoProCapiteBevande,
			ArrayList<Integer> consumoProCapiteAlimentiExtra, boolean inizializzato) {
		
		this.numPostiASedere = numPostiASedere;
		this.caricoLavoroXPersona = caricoLavoroXPersona;
		this.caricoLavoroSostenibile = (double)numPostiASedere * caricoLavoroXPersona * 1.2;
		if(insiemeBevande == null) {
			this.insiemeBevande = new ArrayList<Bevanda>();
		}
		else {
			this.insiemeBevande = insiemeBevande;
		}
		if(insiemeGeneri == null) {
			this.insiemeAlimentiExtra = new ArrayList<GeneriAlimentari>();
		}
		else {
			this.insiemeAlimentiExtra = insiemeGeneri;
		}
	
		this.consumoProCapiteBevande = inizializzaConsumoBevande(consumoProCapiteBevande);
		this.consumoProCapiteAlimentiExtra = inizializzaConsumoGeneriExtra(consumoProCapiteAlimentiExtra);
		this.inizializzato = inizializzato;
	}
	*/
	public StrutturaRistorante( int numPosti, double caricoLavoroPerPersona, ArrayList<Ingrediente> ingredienti ,ArrayList<Bevanda> bevande, HashMap<Bevanda, Integer> consumoProCapiteBevande,
			
			ArrayList<GeneriAlimentari> generiExtra , HashMap<GeneriAlimentari, Integer> consumoProCapiteGeneri) {
		this.numPostiASedere = numPosti;
		this.caricoLavoroXPersona = caricoLavoroPerPersona;
		this.caricoLavoroSostenibile = (double)numPosti * caricoLavoroXPersona * 1.2;
		this.bevande = bevande;
		this.alimentiExtra = generiExtra;
		this.insiemeGeneri = consumoProCapiteGeneri;
		this.insiemeBevande = consumoProCapiteBevande;
		this.ingredienti = ingredienti;
		this.inizializzato= true;
		
		
		
		
	}
			
			
	
	
	
	public StrutturaRistorante() {
		this.numPostiASedere=0;
		this.caricoLavoroXPersona=0;
		this.caricoLavoroSostenibile=(double)numPostiASedere * caricoLavoroXPersona * 1.2;
		this.ingredienti = new ArrayList<>();
		this.bevande= new ArrayList<>();
		this.alimentiExtra= new ArrayList<>();
		this.insiemeGeneri= new HashMap<>();
		this.insiemeBevande= new HashMap<>();
		this.inizializzato=false;
	}
	
	
	


	public int getNumPostiASedere() {
		return numPostiASedere;
	}


	public double getCaricoLavoroXPersona() {
		return caricoLavoroXPersona;
	}


	public double getCaricoLavoroSostenibile() {
		return caricoLavoroSostenibile;
	}


	public void setInsiemeBevande(HashMap<Bevanda, Integer> insiemeBevande) {
		this.insiemeBevande = insiemeBevande;
	}





	public ArrayList<Bevanda> getBevande() {
		return bevande;
	}


	public ArrayList<GeneriAlimentari> getAlimentiExtra() {
		return alimentiExtra;
	}


	public HashMap<Bevanda, Integer> getConsumoProCapiteBevande() {
		return insiemeBevande;
	}


	public HashMap<GeneriAlimentari, Integer> getConsumoProCapiteAlimentiExtra() {
		return insiemeGeneri;
	}


	

	public boolean isInizializzato() {
		return inizializzato;
	}


	public void setInizializzato(boolean inizializzato) {
		this.inizializzato = inizializzato;
	}

	public void setInsiemeGeneri(HashMap<GeneriAlimentari, Integer> insiemeGeneri) {
		this.insiemeGeneri = insiemeGeneri;
	}



	public ArrayList<Ingrediente> getIngredienti() {
		return ingredienti;
	}

	

		
	}
	

