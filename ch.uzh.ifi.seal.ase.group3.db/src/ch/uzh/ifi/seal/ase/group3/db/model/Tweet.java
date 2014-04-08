package ch.uzh.ifi.seal.ase.group3.db.model;

/**
 * Represents a tweet as it is stored in the database
 * 
 * @author Nico
 * 
 */
public class Tweet {

	private String text;
	private Long id;
	private long date;
	private String preprocessed; // optional

	public Tweet() {
		this(-1L, null, -1L);
	}

	public Tweet(Long id, String text, long date) {
		this(id, text, date, null);
	}

	public Tweet(Long id, String text, long date, String preprocessed) {
		this.id = id;
		this.text = text;
		this.preprocessed = preprocessed;
		this.setDate(date);
	}

	public String getText() {
		return text;
	}

	public Long getId() {
		if (id == null)
			return -1L;
		return id;
	}

	public String getPreprocessed() {
		return preprocessed;
	}

	public void setPreprocessed(String preprocessed) {
		this.preprocessed = preprocessed;
	}

	public boolean isPreprocessed() {
		return preprocessed != null;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof Tweet) {
			Tweet tweet = (Tweet) obj;
			return tweet.getId() == id;
		} else {
			return false;
		}
	}
}
