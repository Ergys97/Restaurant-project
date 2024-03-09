package reservationSystem;

import java.time.LocalDate;

import reservationUtility.NumeriCasuali;

public class UtilityTime {
	private LocalDate oggi;
	public UtilityTime() {
		oggi = LocalDate.now();
	}
	
	public LocalDate creaDataScadenzaCasuale() {
		int random = NumeriCasuali.estraiIntero(5, 10);
		return this.oggi.plusDays(random);
		
	}
	
	public LocalDate creaDataScadenzaGiorni(int _numeroGiorni) {
		return this.oggi.plusDays(_numeroGiorni);
	}
	
	public boolean isAlimentoScaduto(Alimento daControllare) {
		if(daControllare.getDataScadenza().isBefore(LocalDate.now()))
			return true;
		else
			return false;

}
	public boolean dataScaduta(LocalDate data) {
		if(data.isBefore(LocalDate.now()))
			return true;
		else
			return false;
	}
}
