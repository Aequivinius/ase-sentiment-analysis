package ch.uzh.ifi.seal.ase.group3.shared;

import org.apache.commons.lang3.StringEscapeUtils;

public class JsonTweet {
	public String text;
	public String id_str;

	public String getText() {
		if (text == null)
			return "";
		// unescape null-string and all java attributes
		return StringEscapeUtils.unescapeJava(text.replace('\u0000', ' '));
	}

	public long getId() {
		try {
			return Long.valueOf(id_str);
		} catch (NumberFormatException e) {
			// cannot parse the id, take current timestamp as a workaround
			return System.currentTimeMillis();
		}
	}

	@Override
	public String toString() {
		return "id: " + id_str + ", text: " + getText();
	}
}