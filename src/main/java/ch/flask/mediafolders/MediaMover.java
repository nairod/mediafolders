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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dorian
 */
abstract class MediaMover {
    private static final Logger LOGGER = Logger.getLogger(MediaMover.class.getName());
    protected final File file;


    public static Optional<MediaMover> fromFile(File file) {
        if (PhotoMover.isFoto(file.getName())) {
            return Optional.of(new PhotoMover(file, new FotoMetaData(file)));
        } else if (VideoMover.isVideo(file.getName())) {
            return Optional.of(new VideoMover(file, new VideoMetaData(file)));
        }
        return Optional.empty();
    }

    public MediaMover(File file) {
        this.file = file;
    }

    protected abstract Optional<Path> target();

    protected abstract Optional<Path> duplicatesTarget();

    public static Function<File, MediaMover> toMover
            = (File t) -> MediaMover.fromFile(t).get();

    protected final void move() {
        if (!target().isPresent() || !duplicatesTarget().isPresent()) {
            LOGGER.log(Level.INFO, "skipped {0}", file.getAbsolutePath());
            return;
        }
        Path target = target().get();
        Path duplicatesTarget = duplicatesTarget().get();

//        assert target.toFile().isFile();
//        assert duplicatesTarget.toFile().isFile();

        try {
            target = Files.exists(target) ? duplicatesTarget : target;
            createDirectory(target);
            Files.move(file.toPath(), target);
            LOGGER.info(String.format("from %s to %s", file.toPath(), target.toFile().getCanonicalPath()));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void createDirectory(Path target) throws IOException {
        Path targetDirectory = target.getParent();
        if (!Files.exists(targetDirectory)) {
            Files.createDirectories(targetDirectory);
        }
    }
}
