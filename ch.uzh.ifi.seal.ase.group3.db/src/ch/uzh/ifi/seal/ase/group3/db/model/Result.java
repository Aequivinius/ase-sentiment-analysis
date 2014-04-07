package ch.uzh.ifi.seal.ase.group3.db.model;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represents a stored search term including its sentiment results (if available)
 */
public class Result implements Serializable {

	private static final long serialVersionUID = 1L;
	private String query;
	private double sentiment;
	private Date date;

	public Result() {
		this(null, 0d, new Date());
	}

	public Result(String term, double sentiment, Date date) {
		this.query = term;
		this.sentiment = sentiment;
		this.date = date;
	}

	public String getQuery() {
		return this.query;
	}

	public double getSentiment() {
		return this.sentiment;
	}

	public Date getDate() {
		return date;
	}
}
