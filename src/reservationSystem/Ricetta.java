package reservationSystem;



import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import reservationUtility.*;









public class Ricetta implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private ArrayList<Ingrediente> ingredienti;
	private int porzioni;
	private String nome;
	private double caricoLavoroPorzione; 
	private double caricoLavoroPersona;
	
	
	
	private int tempoPreparazione;

	public Ricetta(String nome,ArrayList<Ingrediente> ingredienti, int porzioni, double caricoLavoroPorzione, double caricoLavoroPersona,int tempoPreparazione ) {
		this.ingredienti = ingredienti;
		this.porzioni = porzioni;
		this.caricoLavoroPersona = caricoLavoroPersona;
		this.caricoLavoroPorzione = inizializzaCaricoLavoroPorzione(caricoLavoroPorzione);
		this.tempoPreparazione = tempoPreparazione;
		this.nome = nome;
		
	}
	public Ricetta(String nome, ArrayList<Ingrediente> ingredienti, double caricoLavoroPorzione, double caricoLavoroPersona,int tempoPreparazione  ) {
		this.nome = nome;
		this.ingredienti = ingredienti;
		this.caricoLavoroPersona = caricoLavoroPersona;
		this.caricoLavoroPorzione = inizializzaCaricoLavoroPorzione(caricoLavoroPorzione);
		this.tempoPreparazione = tempoPreparazione;
		
		
	}
	public Ricetta(double caricoLavoroPersona) {
		this.ingredienti = new ArrayList<>();
		this.porzioni =0;
		this.caricoLavoroPersona = caricoLavoroPersona;
		this.caricoLavoroPorzione=0.0;
		this.tempoPreparazione=0;
		this.nome="";
		
	}


	public ArrayList<Ingrediente> getIngredienti() {
		return ingredienti;
	}
	
	public double inizializzaCaricoLavoroPorzione (double fraz) {
		Double r = this.caricoLavoroPersona * fraz;
		 return r;
	}


	public void setIngredienti(ArrayList<Ingrediente> ingredienti) {
		this.ingredienti = ingredienti;
	}


	public int getPorzioni() {
		return porzioni;
	}	

	public void setPorzioni(int porzioni) {
		this.porzioni = porzioni;
	}

	public double getCaricoLavoroPorzione() {
		return caricoLavoroPorzione;
	}

	public void setCaricoLavoroPorzione(double caricoLavoroPorzione) {
		this.caricoLavoroPorzione = caricoLavoroPorzione;
	}

	public int getTempoPreparazione() {
		return tempoPreparazione;
	}

	public void setTempoPreparazione(int tempoPreparazione) {
		this.tempoPreparazione = tempoPreparazione;
	}
	public String getNome() {
		return nome;
	}
	
	
}
