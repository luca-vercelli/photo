package it.lucavercelli.photo;

import java.io.File;
import java.io.FileFilter;

public class VideoFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		if (pathname == null)
			return false;
		if (pathname.isDirectory())
			return false;
		String name = pathname.getName().toLowerCase();
		return name.endsWith("mp4");
	}

}
