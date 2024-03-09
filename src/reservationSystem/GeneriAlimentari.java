package reservationSystem;


import java.io.Serializable;
import java.time.LocalDate;





public class GeneriAlimentari extends Alimento implements Serializable{
	private static String UNITA_MISURA="HG";
private static final long serialVersionUID = 1L;
	
	public GeneriAlimentari(String _nome, int _quantita) {
		super(_nome, _quantita);
		this.setUnitaMisura(UNITA_MISURA);
		
	}
	
	
	public GeneriAlimentari(String _nome, int _quantita, LocalDate date) {
		super(_nome, _quantita, date);
		this.setUnitaMisura(UNITA_MISURA);
		
	}





	
	
	



	/*
	@SuppressWarnings("unchecked")
	public static ArrayList<GeneriAlimentari> caricaGeneriAlimentari(){
		ArrayList<GeneriAlimentari> genAlim = new ArrayList<GeneriAlimentari>() ;
		try {
			genAlim = (ArrayList<GeneriAlimentari>) ServizioFile.caricaSingoloOggetto(new File(FILEPATH_ALIMENTI_EXTRA));
		}
		catch(Exception e ) {
			e.printStackTrace();
		}
			
		if(genAlim==null)
			return new ArrayList<GeneriAlimentari>();
		
		return genAlim;
	}
	*/
	
}
