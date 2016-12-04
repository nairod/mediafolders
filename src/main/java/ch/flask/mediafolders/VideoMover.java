/*
 * Copyright 2016 dorian.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.flask.mediafolders;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * @author dorian
 */
final class VideoMover extends MediaMover {
	private static final String VIDEO_PATTERN = ".*\\.mov|.*\\.mpg|.*\\.mp4|.*\\.3gp|.*\\.mts";
	private static final String TARGET_DUPLICATES = "doppelte";
	private VideoMetaData videoMetaData;

	VideoMover(File file, VideoMetaData videoMetaData) {
		super(file);
		this.videoMetaData = videoMetaData;
	}

	@Override
	protected Optional<Path> target() {
		return Optional.of(
				Paths.get(MediaFolders.PROPS.targetVideoPath(),
						String.valueOf(videoMetaData.getYearOfFile()),
						file.getName()));
	}

	@Override
	protected Optional<Path> duplicatesTarget() {
		return Optional.of(
				Paths.get(MediaFolders.PROPS.targetVideoPath(),
						String.valueOf(videoMetaData.getYearOfFile()),
						TARGET_DUPLICATES,
						file.getName()));
	}

	public static boolean isVideo(String name) {
		return name.toLowerCase().matches(VIDEO_PATTERN);
	}
}
