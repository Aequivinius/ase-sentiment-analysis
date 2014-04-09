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
	private int numTweets; // how many tweets have been processed
	private long userId; // the user that has requested the sentiment
	private Date startDate; // start of sentiment
	private Date endDate; // end of sentiment
	private Date computedAt; // when the sentiment has been computed
	private long calculationTime; // the time needed for calculation

	public Result() {
		this(null, 0d, new Date(), new Date(), -1, -1L, new Date(), 0);
	}

	public Result(String query, Date startDate, Date endDate, long userId) {
		this(query, 0, startDate, endDate, -1, userId, new Date(), 0);
	}

	public Result(String query, double sentiment, Date startDate, Date endDate, int numTweets, long userId,
			Date computedAt, long calculationTime) {
		this.query = query;
		this.sentiment = sentiment;
		this.startDate = startDate;
		this.endDate = endDate;
		this.numTweets = numTweets;
		this.userId = userId;
		this.computedAt = computedAt;
		this.calculationTime = calculationTime;
	}

	public String getQuery() {
		return this.query;
	}

	public double getSentiment() {
		return this.sentiment;
	}

	public int getNumTweets() {
		return numTweets;
	}

	public long getUserId() {
		return userId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public long getCalculationTime() {
		return calculationTime;
	}

	public void setSentiment(double sentiment) {
		this.sentiment = sentiment;
	}

	public void setNumTweets(int numTweets) {
		this.numTweets = numTweets;
	}

	public void setCalculationTime(long calculationTime) {
		this.calculationTime = calculationTime;
	}

	public Date getComputedAt() {
		return computedAt;
	}
}
