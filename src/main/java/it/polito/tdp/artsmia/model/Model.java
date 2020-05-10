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
		
		// Ora bisogna aggiungere gli archi! (Min 21.28 L25)
	}

}
