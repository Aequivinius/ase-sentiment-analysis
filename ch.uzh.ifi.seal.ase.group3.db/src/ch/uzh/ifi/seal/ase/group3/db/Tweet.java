package ch.uzh.ifi.seal.ase.group3.db;

/**
 * Represents a tweet as it is stored in the database
 * 
 * @author Nico
 * 
 */
public class Tweet {

	private final String text;
	private final Long id;
	private String preprocessed; // optional

	public Tweet(Long id, String text) {
		this(id, text, null);
	}

	public Tweet(Long id, String text, String preprocessed) {
		this.id = id;
		this.text = text;
		this.preprocessed = preprocessed;
	}

	public String getText() {
		return text;
	}

	public Long getId() {
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
