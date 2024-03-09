
package reservationSystem;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import reservationUtility.*;


public class MenuTematico implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private ArrayList<Piatto> piatti = new ArrayList<Piatto>(); 
	private double caricoLavoroMenuTematico;
	private double caricoLavoroPerPersona;
	private String nome	;									
	private LocalDate disponibile;
	
	
	public MenuTematico (ArrayList<Piatto> piatti , LocalDate disponibile, double caricoLavoroPersona, String nome) {
		this.piatti = piatti;
		this.disponibile = disponibile;
		this.caricoLavoroMenuTematico = calcolaCaricoMenu(piatti);
		this.nome = nome;
		this.caricoLavoroPerPersona =caricoLavoroPersona;
	}
	
	
	public ArrayList<Piatto> getPiatti() {
		return piatti;
	}
	public void setPiatti(ArrayList<Piatto> menu) {
		this.piatti = menu;
	}
	public double getCaricoLavoroMenuTematico() {
		return caricoLavoroMenuTematico;
	}
	public void setCaricoLavoroMenuTematico(double caricoLavoroMenuTematico) {
		this.caricoLavoroMenuTematico = caricoLavoroMenuTematico;
	}
	public LocalDate getDisponibile() {
		return disponibile;
	}
	public void setDisponibile(LocalDate disponibile) {
		this.disponibile = disponibile;
	}




	@Override
	public String toString() {
		return 	piatti.toString();
	}
	
	public double calcolaCaricoMenu(ArrayList<Piatto> piattiMenu) {
		double totale = 0;
		for (Piatto piatto : piattiMenu) {
			totale += piatto.getRicetta().getCaricoLavoroPorzione();
		}
		return totale;
	}
	
	public boolean isAccettabile() {
		
		double v = this.caricoLavoroPerPersona*4/3;
		if(this.caricoLavoroMenuTematico < v )
			return true;
		
		else return false;
	}
	
	public ArrayList<Piatto> selezionaPiatti(ArrayList<Piatto> in , int[] sel){
		
		ArrayList<Piatto> ret = new ArrayList<Piatto>();
		for(int i =0; i<sel.length;i++)
			ret.add(in.get(sel[i]));
			
		
	return ret;
				
		
	}
	
	public double caricoLavoro() {
		double r = 0.0;
		for (Piatto piatto : this.getPiatti()) {
			r+= piatto.caricoLavoro();
		}
		return r;
	}


	public String getNome() {
		return nome;
	}
	
	
	
}
