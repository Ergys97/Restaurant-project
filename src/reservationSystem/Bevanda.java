package reservationSystem;


import java.io.Serializable;
import java.time.LocalDate;



public class Bevanda extends Alimento implements Serializable{
	private static String UNITA_MISURA="L";
	
	public Bevanda(String _nome, int _quantita) {
		super(_nome, _quantita);
		this.setUnitaMisura(UNITA_MISURA);
		
	}
	
	public Bevanda(String _nome, int _quantita, LocalDate date) {
		super(_nome, _quantita,date);
		this.setUnitaMisura(UNITA_MISURA);
		
	}

	private static final long serialVersionUID = 1L;
	
	
}
	
	

