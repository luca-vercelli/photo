package it.lucavercelli.photo;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMoveFiles {

	@Before
	public void before() {
		System.setProperty("derby.system.home", System.getProperty("user.home"));
		System.out.println(new File(".").getAbsolutePath());
		System.setProperty("user.dir", new File("target/test-classes").getAbsolutePath());
	}

	@After
	public void after() {
		System.setProperty("user.dir", new File("../..").getAbsolutePath());
	}

	private File getResource(String uri) {
		return new File(uri.replace('/', File.separatorChar)).getAbsoluteFile();
	}

	@Test
	public void testMoveVideos() throws Exception {

		File f1 = getResource("./folder_a/video1.mp4");
		File f2 = getResource("./folder_a/img1.png");
		File f3 = getResource("./folder_b/video2.mp4");
		
		assertTrue("Set up error: missing resource files", f1.exists() && f2.exists() && f3.exists());

		Options opt = new Options(new String[] { "--move-videos" });
		assertTrue("Options should include 'moveVideosToFolder'", opt.moveVideosToFolder);

		App app = new App(opt);
		app.mainLoop();

		assertFalse("Videos should move", f1.exists() || f3.exists());
		assertTrue("Images should't move", f2.exists());

		File f1a = getResource("./video/video1.mp4");
		File f3a = getResource("./video/video2.mp4");

		assertTrue("Where the hell videos were moved?", f1a.exists() && f3a.exists());

		f1a.renameTo(f1);
		f3a.renameTo(f3);
	}

}
