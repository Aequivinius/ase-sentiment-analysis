package ch.uzh.ifi.seal.ase.group3.shared;

import java.io.Serializable;

/**
 * This class represents a stored search term including its sentiment results (if available)
 */
public class SearchTerm implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String term;
	private Double sentiment;
	
	public SearchTerm (String term, Double sentiment){
		this.term = term;
		this.sentiment = sentiment;		
	}
	
	public SearchTerm (String term){
		this.term = term;
		this.sentiment = 0.0d;		
	}
	
	public SearchTerm(){
		this.term = "unknown";
		this.sentiment = 0.0d;
	}

	public String getTerm() {
		return this.term;
	}

	public Double getSentiment() {
		return this.sentiment;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public void setSentiment(Double sentiment) {
		this.sentiment = sentiment;
	}
}
