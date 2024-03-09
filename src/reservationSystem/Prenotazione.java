package reservationSystem;

import java.io.Serializable;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class Prenotazione implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	
	
	private HashMap<MenuTematico, Integer> myMenuTematici;
	private HashMap<Piatto, Integer> myPiatti;
	private LocalDate dataPrenotazione;
	private int numeroPersone;
	private int numeroPostiMax;
	private double caricoLavoroSostenibile;
	
	
	
	
	
	

	public Prenotazione(HashMap<MenuTematico, Integer> prenotazione, HashMap<Piatto, Integer> prenotazioneAlternativa,
			int numCoperti, LocalDate data, int numeroPostiMassimi, double caricoLavoroSostenibile ) {
		super();
		this.myMenuTematici = prenotazione;
		this.myPiatti = prenotazioneAlternativa;
		this.numeroPersone = numCoperti;
		this.dataPrenotazione = data;
		this.numeroPostiMax = numeroPostiMassimi;
		this.caricoLavoroSostenibile = caricoLavoroSostenibile;
	}
	
	public Prenotazione(HashMap<MenuTematico, Integer> prenotazioneMenuT,LocalDate data, int numCoperti,   int numeroPostiMassimi, double caricoLavoroSostenibile) {
		
		this.myMenuTematici=prenotazioneMenuT;
		this.numeroPersone = numCoperti;
		this.dataPrenotazione = data;
		this.myPiatti = new HashMap<Piatto,Integer>();
		this.numeroPostiMax = numeroPostiMassimi;
		this.caricoLavoroSostenibile = caricoLavoroSostenibile;
		
	}
	
	public Prenotazione(HashMap<Piatto, Integer> prenotazionePiatto, int numCoperti, LocalDate data,  int numeroPostiMassimi, double caricoLavoroSostenibile) {
		this.myPiatti = prenotazionePiatto;
		this.numeroPersone = numCoperti;
		this.dataPrenotazione = data;
		this.myMenuTematici = new HashMap<>();
		this.numeroPostiMax = numeroPostiMassimi;
		this.caricoLavoroSostenibile = caricoLavoroSostenibile;
	}
	
	public Prenotazione (int numeroPostiMassimi, double caricoLavoroSostenibile) {
		this.myPiatti = new HashMap<Piatto,Integer>();
		this.myMenuTematici = new HashMap<>();
		this.dataPrenotazione = LocalDate.now();
		this.numeroPersone=0;
		this.numeroPostiMax = numeroPostiMassimi;
		this.caricoLavoroSostenibile = caricoLavoroSostenibile;
	}

	public HashMap<MenuTematico, Integer> getMenuPrenotati() {
		return myMenuTematici;
	}

	public void setMenuPrenotati(HashMap<MenuTematico, Integer> prenotazione) {
		this.myMenuTematici = prenotazione;
	}

	public HashMap<Piatto, Integer> getPiattiPrenotati() {
		return myPiatti;
	}

	public void setPiattiPrenotati(HashMap<Piatto, Integer> prenotazioneAlternativa) {
		this.myPiatti = prenotazioneAlternativa;
	}

	public int getNumCoperti() {
		return this.numeroPersone;
	}

	public void setNumCoperti(int numCoperti) {
		this.numeroPersone = numCoperti;
	}

	public LocalDate getData() {
		return this.dataPrenotazione;
	}

	public void setData(LocalDate data) {
		this.dataPrenotazione = data;
	}
	
	
	public HashMap<MenuTematico, Integer> selezionaMenuTematico(int selezione,int numPersone, ArrayList<MenuTematico> listaMenu){
		HashMap<MenuTematico, Integer> ret = new HashMap<>();
		MenuTematico selezionato = listaMenu.get(selezione);
		ret.put(selezionato, numPersone);
		return ret;		
	}
	
	public HashMap<Piatto, Integer> selezionaPiatto (int selezione, int numPersone , ArrayList<Piatto> listaPiatti){
		
		HashMap<Piatto, Integer> ret = new HashMap<>();
		Piatto selezionato = listaPiatti.get(selezione);
		ret.put(selezionato, numPersone);
		return ret;
	}
	
	public void aggiungiMenuTematico( HashMap<MenuTematico, Integer> in) {
		
		for(Map.Entry<MenuTematico,Integer> kv : in.entrySet()) {
			if(this.myMenuTematici.isEmpty()) {
				this.myMenuTematici.put(kv.getKey(), kv.getValue());
			}
			else {
					if(this.myMenuTematici.containsKey(kv.getKey())) {
						int oldValue =this.myMenuTematici.get(kv.getKey());
						int newValue = oldValue + kv.getValue();
						this.myMenuTematici.put(kv.getKey(), newValue);
					}
					else {
						this.myMenuTematici.put(kv.getKey(), kv.getValue());
					}
						
				}
			}
		}
	
	public void aggiungiPiatto (HashMap<Piatto, Integer> in) {
		for(Map.Entry<Piatto, Integer> kv : in.entrySet()) {
			if(this.myPiatti.isEmpty()) {
				this.myPiatti.put(kv.getKey(), kv.getValue());
			}
			else {
					if(this.myPiatti.containsKey(kv.getKey())) {
						int oldValue = this.myPiatti.get(kv.getKey());
						int newValue = oldValue + kv.getValue();
						this.myPiatti.put(kv.getKey(), newValue);
					}
					else {
						this.myPiatti.put(kv.getKey(), kv.getValue());
					}
			
				}
			}
		}
	
	
	public int limiteMassimoPostiaSedere(LocalDate data, ArrayList<Prenotazione> elencoPrenotazioni ) {
		
		int cnt = 0;
		for (Prenotazione prenotazione : elencoPrenotazioni) {
			 if(prenotazione.getData().equals(data)) {
				 cnt += prenotazione.numeroPersone;
			 }
		}
		int ret = this.numeroPostiMax - cnt;
		return ret;
	}
	
	public boolean conformeAlCaricoLavoro( ArrayList<Prenotazione> elencoPrenotazioni) {
		
		ArrayList<Prenotazione> prenotazioni = (ArrayList<Prenotazione>) elencoPrenotazioni.clone();
		prenotazioni.add(this);
		double caricoLavoroPrenotazioni = this.caricoLavoroPrenotazioniGiornata(this.dataPrenotazione, prenotazioni);
		if(caricoLavoroPrenotazioni < this.caricoLavoroSostenibile) {
			return true;
		}
		else
		{
			return false;
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	

	
	public double caricoLavoroSingolaPrenotazione() {
		double r = 0.0;
		for (Map.Entry<Piatto, Integer> kv : this.myPiatti.entrySet()) {
			r += kv.getKey().caricoLavoro() * kv.getValue();
			
		}
		
		for (Map.Entry<MenuTematico, Integer> kv : this.myMenuTematici.entrySet()) {
			r+= kv.getKey().caricoLavoro() * kv.getValue();
			
			
		}
		
		return r;
	}
	
	public double caricoLavoroPrenotazioniGiornata ( LocalDate date , ArrayList<Prenotazione> elencoPrenotazioni) {
		double r = 0.0;
		for (Prenotazione prenotazione : elencoPrenotazioni) {
				if(prenotazione.getData().equals(date)) {
					r += prenotazione.caricoLavoroSingolaPrenotazione(); 
				}
			
		}
		return r;
	}
	

	
	
	
	
	
}
		
		
		
		
	
	
	
	
	

