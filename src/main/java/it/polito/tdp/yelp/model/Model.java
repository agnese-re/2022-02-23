package it.polito.tdp.yelp.model;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private YelpDao dao;
	private Graph<Review,DefaultWeightedEdge> grafo;
	
	private List<Review> soluzioneMigliore;
	
	public Model() {
		dao = new YelpDao();
	}
	
	public String creaGrafo(Business locale) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.grafo,this.dao.getReviewLocale(locale));
		
		for(Review r1: this.grafo.vertexSet())
			for(Review r2: this.grafo.vertexSet())
				if(!r1.equals(r2)) 		// evito i selfloops
					if(r1.getDate().isBefore(r2.getDate()))		// se stessa data ritorna false -> archi con peso != 0
						/*  positive if temporal2Exclusive is later than temporal1Inclusive, negative if earlier.
						 	Gli archi avranno peso positivo in quanto r1.getDate() viene prima di r2.getDate()  */
						Graphs.addEdgeWithVertices(this.grafo, r1, r2, 
								ChronoUnit.DAYS.between(r1.getDate(), r2.getDate()));
		
		String msg = "Grafo creato (" + this.grafo.vertexSet().size() + " vertici e " +
						this.grafo.edgeSet().size() + " archi)";
		return msg;
		
	}
	
	public List<ReviewWithEdges> maxArchiUscenti() {
		List<ReviewWithEdges> result = new ArrayList<ReviewWithEdges>();
		int maxEdges = 0;
		
		for(Review r: this.grafo.vertexSet())
			if(this.grafo.outgoingEdgesOf(r).size() > maxEdges)
				maxEdges = this.grafo.outgoingEdgesOf(r).size();
		
		for(Review r: this.grafo.vertexSet())
			if(this.grafo.outgoingEdgesOf(r).size() == maxEdges)
				result.add(new ReviewWithEdges(r,maxEdges));
		
		return result;
	}
	
	public List<String> getAllCities() {
		return dao.getAllCities();
	}
	
	public List<Business> getBusinessCity(String city) {
		return dao.getBusinessCity(city);
	}
	
	public List<Review> getPercorsoMigliore() {
		List<Review> allReviews = new ArrayList<Review>(this.grafo.vertexSet());
		
		List<Review> parziale = new ArrayList<Review>();
		this.soluzioneMigliore = new ArrayList<Review>();
		
		for(Review sorgente: allReviews) {
			parziale.clear();
			parziale.add(sorgente);
			cercaPercorso(sorgente, parziale, allReviews);
		}
		return soluzioneMigliore;
	}

	private void cercaPercorso(Review sorgente, List<Review> parziale, List<Review> allReviews) {
		if(parziale.size() > this.soluzioneMigliore.size()) {
			this.soluzioneMigliore = new ArrayList<Review>(parziale);
		}
		
		List<Review> successori = Graphs.successorListOf(this.grafo, parziale.get(parziale.size()-1));
		for(Review successore: successori)
			if(!parziale.contains(successore) && 
					successore.getStars() >= parziale.get(parziale.size()-1).getStars()) {
				parziale.add(successore);
				cercaPercorso(sorgente,parziale,allReviews);
				parziale.remove(parziale.get(parziale.size()-1));
			}
		
	}
	
	public int getGiorniPrimaUltima(List<Review> soluzioneMigliore) {
		int numGiorni = 0;
		
		for(int indice = 0; indice < soluzioneMigliore.size()-1; indice++) {
			DefaultWeightedEdge edge = this.grafo.getEdge(
					soluzioneMigliore.get(indice), soluzioneMigliore.get(indice+1));
			numGiorni += this.grafo.getEdgeWeight(edge);
		}
		
		return numGiorni;
	}
	
}
