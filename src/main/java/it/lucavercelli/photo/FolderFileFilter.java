package it.lucavercelli.photo;

import java.io.File;
import java.io.FileFilter;

public class FolderFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		if (pathname == null)
			return false;
		if (pathname.getName().equals(".") || pathname.getName().equals(".."))
			return false;
		return (pathname.isDirectory());
	}

}
