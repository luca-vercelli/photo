package it.lucavercelli.photo;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class TestMoveFiles {

	@Test
	public void testMoveVideos() throws Exception {
		File f1 = new File("." + File.separator + "folder_a" + File.separator + "video1.mp4");
		File f2 = new File("." + File.separator + "folder_a" + File.separator + "img1.png");
		File f3 = new File("." + File.separator + "folder_b" + File.separator + "video2.mp4");

		if (!(f1.exists() && f2.exists() && f3.exists()))
			fail("Set up error: missing resource files");

		App.main(new String[] { "--move-videos" });
		// :( System.exit !!

		if ((f1.exists() || f3.exists()))
			fail("Videos should move");

		if (!(f2.exists()))
			fail("Images should't move");

		File f1a = new File("." + File.separator + "video" + File.separator + "video1.mp4");
		File f3a = new File("." + File.separator + "video" + File.separator + "video2.mp4");

		if (!(f1a.exists() && f3a.exists()))
			fail("Where the hell videos were moved?");

		f1a.renameTo(f1);
		f3a.renameTo(f3);
	}

}
