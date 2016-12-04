package ch.flask.mediafolders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

final class MediaMoverProperties {
	private final Properties props = new Properties();
	private final InputStream inputStream = null;
	private final String sourcePath;
	private final String googleApiKey;
	private final String targetVideoPath;
	private final String targetPhotoPath;

	MediaMoverProperties() {
		MediaMoverProperties.class.getResourceAsStream("mediafolders.properties");
		try {
			props.load(inputStream);
		} catch (IOException e) {
			Logger.getLogger("MediaFolders").log(Level.SEVERE, "could not load properties", e);
			System.exit(0);
		}
		sourcePath = props.getProperty("sourcePath");
		googleApiKey = props.getProperty("googleApiKey");
		targetVideoPath = props.getProperty("targetVideoPath");
		targetPhotoPath = props.getProperty("targetPhotoPath");
	}

	String sourcePath() {
		return sourcePath;
	}

	String googleApiKey() {
		return googleApiKey;
	}

	String targetVideoPath() {
		return targetVideoPath;
	}

	String targetPhotoPath() {
		return targetPhotoPath;
	}

}
