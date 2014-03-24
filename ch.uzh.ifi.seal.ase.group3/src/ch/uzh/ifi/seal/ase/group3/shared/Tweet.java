package ch.uzh.ifi.seal.ase.group3.shared;

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
}
