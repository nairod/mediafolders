package ch.flask.mediafolders;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class MediaMoverTest {
	@Rule
	public TemporaryFolder tempSourceFolder = new TemporaryFolder();

	@Rule
	public TemporaryFolder tempTargetFolder = new TemporaryFolder();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private File photoFile;
	private File videoFile;

	@Before
	public void prepare() throws IOException {
		photoFile = new File(this.getClass().getClassLoader().getResource("DSC_0672.JPG").getPath());
		videoFile=new File(this.getClass().getClassLoader().getResource("test-mpeg.mp4").getPath());
	}

	private void copyFilesTo(Path testFilePath, File... originalFiles) throws IOException {
		for (File original : originalFiles) {
			Path targetPath = Paths.get(tempSourceFolder.getRoot().getPath(), original.getName());
			Files.copy(original.toPath(), targetPath);
		}
	}


	@Test
	public void moveFoto_simple(@Mocked final MediaMoverProperties props, @Mocked final FotoMetaData fotoData)
			throws Exception {
		final String targetPath = tempTargetFolder.getRoot().getPath() + "/target/";
		new NonStrictExpectations() {
			{
				props.targetPhotoPath();
				result = targetPath;
				props.sourcePath();
				result = tempSourceFolder.getRoot().getPath();
				fotoData.getPlace();
				result = "Berlin";
				fotoData.getYear();
				result = "2016";
			}
		};

		Path testFilePath = Paths.get(tempSourceFolder.getRoot().getPath(), photoFile.getName());

		copyFilesTo(testFilePath, photoFile);

		MediaMover mediaMover = MediaMover.fromFile(testFilePath.toFile()).get();
		mediaMover.move();

		Path expectedPath = Paths.get(targetPath, "2016", "Berlin", "DSC_0672.JPG");
		assertTrue(String.format("Photo is moved to %s", expectedPath.toString()), Files.exists(expectedPath));
		assertTrue("Photo is moved not copied", Files.notExists(testFilePath));
	}

	@Test
	public void moveFoto_duplicated(@Mocked final MediaMoverProperties props, @Mocked final FotoMetaData fotoData)
			throws Exception {
		final String targetPath = tempTargetFolder.getRoot().getPath() + "/target/";
		new NonStrictExpectations() {
			{
				props.targetPhotoPath();
				result = targetPath;
				props.sourcePath();
				result = tempSourceFolder.getRoot().getPath();
				fotoData.getPlace();
				result = "Berlin";
				fotoData.getYear();
				result = "2016";
			}
		};

		Path testFilePath = Paths.get(tempSourceFolder.getRoot().getPath(), photoFile.getName());

		copyFilesTo(testFilePath, photoFile);

		MediaMover mediaMover = MediaMover.fromFile(testFilePath.toFile()).get();
		mediaMover.move();

		copyFilesTo(testFilePath, photoFile);
		mediaMover.move();

		Path expectedPath = Paths.get(targetPath, "2016", "Berlin", "doppelte", photoFile.getName());
		assertTrue("Foto is moved to correct folder", Files.exists(expectedPath));
	}
	
	@Test
	public void moveVideo_simple(@Mocked final MediaMoverProperties props, @Mocked final FotoMetaData fotoData)
			throws Exception {
		final String targetPath = tempTargetFolder.getRoot().getPath() + "/target/";
		new NonStrictExpectations() {
			{
				props.targetVideoPath();
				result = targetPath;
				props.sourcePath();
				result = tempSourceFolder.getRoot().getPath();
			}
		};

		Path testFilePath = Paths.get(tempSourceFolder.getRoot().getPath(), videoFile.getName());
		copyFilesTo(testFilePath, videoFile);
		MediaMover mediaMover = MediaMover.fromFile(testFilePath.toFile()).get();
		mediaMover.move();

		Path expectedPath = Paths.get(targetPath, "2016", videoFile.getName());
		assertTrue(String.format("Video is moved to %s", expectedPath.toString()), Files.exists(expectedPath));
		assertTrue("Video is moved not copied", Files.notExists(testFilePath));
	}

	@Test
	public void moveVideo_duplicated(@Mocked final MediaMoverProperties props, @Mocked final FotoMetaData fotoData)
			throws Exception {
		final String targetPath = tempTargetFolder.getRoot().getPath() + "/target/";
		new NonStrictExpectations() {
			{
				props.targetVideoPath();
				result = targetPath;
				props.sourcePath();
				result = tempSourceFolder.getRoot().getPath();
			}
		};
		
		Path testFilePath = Paths.get(tempSourceFolder.getRoot().getPath(), videoFile.getName());
		copyFilesTo(testFilePath, videoFile);
		MediaMover mediaMover = MediaMover.fromFile(testFilePath.toFile()).get();
		mediaMover.move();

		copyFilesTo(testFilePath, videoFile);
		mediaMover = MediaMover.fromFile(testFilePath.toFile()).get();
		mediaMover.move();
		

		Path expectedPath = Paths.get(targetPath, "2016","doppelte", videoFile.getName());
		assertTrue(String.format("Video is moved to %s", expectedPath.toString()), Files.exists(expectedPath));
		assertTrue("Video is moved not copied", Files.notExists(testFilePath));
	}
}
