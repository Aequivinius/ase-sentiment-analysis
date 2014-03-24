package ch.uzh.ifi.seal.ase.group3.shared;

import org.apache.commons.lang3.StringEscapeUtils;

public class JsonTweet {
	public String text;
	public String id_str;

	public String getText() {
		return StringEscapeUtils.unescapeJava(text);
	}

	public long getId() {
		return Long.valueOf(id_str);
	}

	@Override
	public String toString() {
		return "id: " + id_str + ", text: " + getText();
	}
}