package reservationSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface FileOperation {
	 <E> ArrayList<E>   loadMultipleFile (File file);
	 <E> void storeMultipleFile(File file, ArrayList<E> lista);
	 StrutturaRistorante loadStrutturaRistorante(File file);
	 ListaSpesa loadListaSpesa(File file);
	 void storeSingleFile(File file, Object oggettoDaSalavre);
	 Object loadSingleFile(File file);
	 
}
