package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	/*
	 * 	Il grafo dovrà essere pesato,
	 *  semplice e non orientato. I vertici rappresentano tutti
	 *  gli oggetti presenti nel database (tabella objects)
	 */
	
	private Graph<ArtObject, DefaultWeightedEdge> grafo;
	
	/*
	 *  Perchè vogliamo usare una Map? 
	 *  Per salvare gli oggetti che creiamo, così rimangono salvati e, 
	 *  ogni volta che ci servono, andiamo a prenderli dalla Map senza dover fare una new.
	 */
	private Map<Integer, ArtObject> idMap;
	
	
	public Model() {
		idMap = new HashMap<Integer, ArtObject>();
	}
	
	/*
	 *  Utilizzo un metodo per creare il grafo anzichè crearlo all'interno di 
	 *  'public Model()' per evitare che, qualora volessi creare un nuovo grafo, 
	 *  mi manchi il metodo per farlo.
	 */
	public void creaGrafo() {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		ArtsmiaDAO dao = new ArtsmiaDAO();
		dao.listObjects(idMap);
		
		// Aggiungo vertici!
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		/*
		 *  Ora bisogna aggiungere gli archi!
		 *  Un arco è presente se due opere sono state esposte nello stesso luogo
		 *  e il peso dell'arco indica il numero di volte che ciò è avvenuto. 
		 *  Abbiamo 3 strategie per implementare gli archi.
		 */
		
		// APPROCCIO 1: doppio ciclo for sui vertici --> dati due vertici --> controllo se sono collegati
//		for(ArtObject ao1 : this.grafo.vertexSet()) {
//			for(ArtObject ao2 : this.grafo.vertexSet()) {
//				// devo collegare ao1 con ao2?
//				/*
//				 *  Controllo se non esiste già l'arco dal momento che è un doppio ciclo for
//				 *  e che controllerà prima ao1 con ao2 e poi ao2 con ao1.
//				 *  --> if(!this.grafo.containsEdge(ao1, ao2))
//				 */
//				
//				int peso = dao.getPeso(ao1, ao2);
//				if(peso > 0) {
//					if(!this.grafo.containsEdge(ao1, ao2)) {
//						Graphs.addEdge(this.grafo, ao1, ao2, peso);
//					}	
//				}
//			}
//		}
//		
//		System.out.println(String.format("Grafo creato! #vertici %d, #archi %d", 
//						this.grafo.vertexSet().size(), this.grafo.edgeSet().size()));
		
		/*
		 *  Questo approccio non funziona in questo caso.
		 *  E' importante riuscire a capire quando posso adottare questo tipo di soluzione 
		 *  perchè, nonostante sia più semplice degli altri approcci, c'è il rischio che non funzioni.
		 *  Perchè? 
		 *  La query sql ci impiega 0.8 ms e gli elementi in questione sono circa 85'500
		 *  TempoEsecuzione = 0.8 (ms) * 85'000 (elementi) * 85'000 (ciclo for annidato) = 5'848'200'000 (ms)
		 *  che se convertiti in giorni = 67 giorni circa 
		 */
		
		/*
		 *  APPROCCIO 2: Cambiamo la query e teniamo un elemento fisso e lo controlliamo con gli altri;
		 *  			 in questo caso il tempo della singola query aumenta ma devo farlo "solo" per 85'500.
		 *  			 String sql = "select eo2.object_id, count(*) " +
		 *								"from exhibition_objects as eo1, exhibition_objects as eo2 " +
		 *								"where eo1.exhibition_id = eo2.exhibition_id and " +
		 *									"eo1.object_id = 8485 and eo2.object_id != eo1.object_id "+ 
		 *								"group by eo2.object_id ";
		 *				 In questo caso la query impiega 19.8 ms
		 *				 TempoEsecuzione = 19.8 (ms) * 85'500 (elementi) = 1'692'900 (ms) = 28 minuti
		 *				 
		 *	Il tempo è ancora elevato, vediamo dunque l'approccio 3!
		 */
		
		// APPROCCIO 3: MIGLIORE! SEMPRE IN TEMPI RAGIONEVOLI!
		
		/*
		 *  Devo creare una nuova classe di appoggio nel Model (Adiacenza)
		 *  Nel DAO creo il metodo getAdiacenze che interrogherà il DB
		 *  Vedere la query presente nel DAO, in cui cambia la select
		 */
		
		for(Adiacenza a : dao.getAdiacenze()) {
			if(a.getPeso() > 0) {
				Graphs.addEdge(this.grafo, idMap.get(a.getObj1()), idMap.get(a.getObj2()), a.getPeso());
			}
		}
		
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}

}
