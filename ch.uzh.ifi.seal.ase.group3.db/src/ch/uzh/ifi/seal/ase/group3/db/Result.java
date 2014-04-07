package ch.uzh.ifi.seal.ase.group3.db;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represents a stored search term including its sentiment results (if available)
 */
public class Result implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String query;
	private final double sentiment;
	private final Date date;

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
