package reservationSystem;

import java.io.Serializable;
import java.time.LocalDate;





public class Ingrediente extends Alimento implements Serializable{
	private static final long serialVersionUID = 1L;
	private static String UNITA_MISURA="KG";

	public Ingrediente(String _nome, int _quantita) {
		super(_nome, _quantita);
		this.setUnitaMisura(UNITA_MISURA);
		
	}
	
	public Ingrediente(String nome, int quantita, LocalDate date) {
		super(nome, quantita, date);
		this.setUnitaMisura(UNITA_MISURA);
	}






	
	
	
	
	



	
	
	
	
	
}
