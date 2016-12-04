package ch.flask.mediafolders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.LocalDateTime;

abstract class MediaMetaData {
	private static final Logger LOGGER = Logger.getLogger(MediaMetaData.class.getName());
	private final File file;

	public MediaMetaData(File file) {
		this.file = file;
	}

	protected int getYearOfFile() {
		BasicFileAttributes fileAttributes;
		FileTime creationTime = null;
		try {
			fileAttributes = Files.readAttributes(file().toPath(), BasicFileAttributes.class);
			creationTime = fileAttributes.creationTime();
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
		}
		if (creationTime != null) {
			return new LocalDateTime(creationTime.toMillis()).getYear();
		} else {
			return 1970;
		}
	}

	protected File file() {
		return file;
	}
}
