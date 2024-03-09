package reservationSystem;

import java.io.File;
import java.io.Serializable;

import java.util.ArrayList;





public class RegistroMagazzino implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	private static final int SOGLIA = 20;
	
	private ListaSpesa elencoAlimenti ;
	private FileOperation gestioneFile;
	
	public RegistroMagazzino(ListaSpesa lSpesa, FileOperation gestoreFile) {
		this.elencoAlimenti = lSpesa;
		this.gestioneFile = gestoreFile;
	}
	
	public RegistroMagazzino (ArrayList<Ingrediente> ingredienti , ArrayList<Bevanda> bevande ,
			ArrayList<GeneriAlimentari> generiAlimentari , FileOperation gestorefile) {
		this.elencoAlimenti = new ListaSpesa(ingredienti, bevande, generiAlimentari);
		this.gestioneFile = gestorefile;
	}
	
	public void ripristinoAlimenti(File fileIngredienti, File fileBevande, File fileGeneri) {
		ListaSpesa alimentiScadutiRipristinati = this.ripristinoAlimentiScaduti();
		this.setElencoAlimenti(alimentiScadutiRipristinati);
		ListaSpesa alimentiFinitiRipristinati = this.ripristinoAlimentiFiniti();
		this.setElencoAlimenti(alimentiFinitiRipristinati);
		this.gestioneFile.storeMultipleFile(fileIngredienti, this.elencoAlimenti.getElencoIngredienti());
		this.gestioneFile.storeMultipleFile(fileBevande, this.elencoAlimenti.getElencoBevande());
		this.gestioneFile.storeMultipleFile(fileGeneri, this.elencoAlimenti.getElencoGeneri());
	}
	
	
	private ListaSpesa ripristinoAlimentiScaduti() {
		ListaSpesa alimentiScaduti = this.elencoAlimenti.creaListaElementiScaduti();
		ListaSpesa alimentiRimanenti = this.elencoAlimenti.creaListaElementiNonScaduti();
		ArrayList<Ingrediente> ingredientiRipristinati = this.ripristinoIngredientiScaduti(alimentiScaduti.getElencoIngredienti());
		ArrayList<Bevanda> bevandeRipristinate = this.ripristinoBevandeScadute(alimentiScaduti.getElencoBevande());
		ArrayList<GeneriAlimentari> generiRipristinati = this.ripristinaGeneriScduti(alimentiScaduti.getElencoGeneri());
		ListaSpesa alimentiRipristinati = new ListaSpesa(ingredientiRipristinati, bevandeRipristinate, generiRipristinati);
		alimentiRimanenti.aggiungiElementiDaListaSpesa(alimentiRipristinati);
		return alimentiRimanenti;
	}
	
	private ListaSpesa ripristinoAlimentiFiniti() {
		ListaSpesa elementiFiniti = this.elencoAlimenti.creaListaElementiFiniti();
		ListaSpesa elementiRimanenti = this.elencoAlimenti.creaListaElementiNonFiniti();
		ArrayList<Ingrediente> ingredientiRipristinati = this.ripristinoIngredientiFiniti(elementiFiniti.getElencoIngredienti());
		ArrayList<Bevanda> bevandeRipristinate = this.ripristinoBevandeFinite(elementiFiniti.getElencoBevande());
		ArrayList<GeneriAlimentari> generiRipristinati = this.ripristinoGeneriFiniti(elementiFiniti.getElencoGeneri());
		ListaSpesa elementiRipristinati = new ListaSpesa(ingredientiRipristinati, bevandeRipristinate, generiRipristinati);
		elementiRimanenti.aggiungiElementiDaListaSpesa(elementiRipristinati);
		return elementiRimanenti;
		
		
	}
	
	public ArrayList<Ingrediente> ripristinoIngredientiFiniti(ArrayList<Ingrediente> ingFiniti){
		for (Ingrediente ingrediente : ingFiniti) {
				int oldValue = ingrediente.getQuantita();
				if(oldValue < 0) {
					int newValue =   oldValue + Math.abs(oldValue) + SOGLIA;
					ingrediente.setQuantita(newValue);
				}
				else {
					ingrediente.setQuantita(oldValue + SOGLIA);
				}
			
		}
		return ingFiniti;
	}
	
	public ArrayList<Bevanda> ripristinoBevandeFinite(ArrayList<Bevanda> bevFinite){
		for (Bevanda bevanda : bevFinite) {
			int oldValue = bevanda.getQuantita();
			if(oldValue < 0) {
				int newValue = oldValue + Math.abs(oldValue) + SOGLIA;
				bevanda.setQuantita(newValue);
			}
			else {
				bevanda.setQuantita(oldValue + SOGLIA);
				
			}
			
		}
		return bevFinite;
	}
	
	public ArrayList<GeneriAlimentari> ripristinoGeneriFiniti (ArrayList<GeneriAlimentari> generiFiniti){
		for (GeneriAlimentari generiAlimentari : generiFiniti) {
			int oldValue = generiAlimentari.getQuantita();
			if(oldValue < 0) {
				int newValue = oldValue + Math.abs(oldValue) +SOGLIA;
				generiAlimentari.setQuantita(newValue);
			}
			else {
				generiAlimentari.setQuantita(oldValue + SOGLIA);
			}
			
		}
		return generiFiniti;
	}
	
	public ArrayList<Ingrediente> ripristinoIngredientiScaduti(ArrayList<Ingrediente> ingScaduti){
		UtilityTime uTime = new UtilityTime();
		for (Ingrediente ingrediente : ingScaduti) {
			ingrediente.setDataScadenza(uTime.creaDataScadenzaCasuale());
		}
		return ingScaduti;
	
	
	
	
	}
	
	public ArrayList<Bevanda> ripristinoBevandeScadute (ArrayList<Bevanda> bevandeScadute){
		UtilityTime uTime = new UtilityTime();
		for (Bevanda bevanda : bevandeScadute) {
			bevanda.setDataScadenza(uTime.creaDataScadenzaCasuale());
			
		}
		return bevandeScadute;
	}
	
		
	public   ArrayList<GeneriAlimentari>  ripristinaGeneriScduti (ArrayList<GeneriAlimentari> generiScaduti){
		UtilityTime uTime = new UtilityTime();
		for (GeneriAlimentari generiAlimentari : generiScaduti) {
			  generiAlimentari.setDataScadenza(uTime.creaDataScadenzaCasuale());
		}
		return generiScaduti;
	}
	
	public ListaSpesa getElencoAlimenti() {
		return elencoAlimenti;
	}

	public void setElencoAlimenti(ListaSpesa elencoAlimenti) {
		this.elencoAlimenti = elencoAlimenti;
	}
	
	

}
