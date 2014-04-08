package ch.uzh.ifi.seal.ase.group3.utils.populate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringEscapeUtils;

public class JsonTweet {

	// e.g. Mon Aug 01 16:23:28 +0000 2011
	public SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
	public String text;
	public String id_str;
	public String created_at;

	public String getText() {
		if (text == null)
			return null;
		// unescape null-string and all java attributes
		try {
			return StringEscapeUtils.unescapeJava(text.replace('\u0000', ' '));
		} catch (Exception e) {
			return null;
		}
	}

	public long getId() {
		try {
			return Long.valueOf(id_str);
		} catch (NumberFormatException e) {
			// cannot parse the id, take current timestamp as a workaround
			return System.currentTimeMillis();
		}
	}

	public long getDate() {
		try {
			return sdf.parse(created_at).getTime();
		} catch (ParseException e) {
			return -1L;
		}
	}

	@Override
	public String toString() {
		return "id: " + id_str + ", text: " + getText();
	}
}