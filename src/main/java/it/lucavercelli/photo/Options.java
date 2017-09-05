package it.lucavercelli.photo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Options {

	public boolean usage = false;
	public boolean version = false;
	public boolean errorInArgs = false;
	public boolean listFiles = false;
	public boolean listDuplicates = false;
	public boolean deleteDuplicates = false;
	public boolean moveImagesToFolder = false;
	public boolean moveVideosToFolder = false;
	public boolean moveAudiosToFolder = false;
	public boolean moveMediasToFolder = false;
	public boolean clearDuplicates = false;
	public boolean clearDB = false;

	public Options(String[] args) {

		List<String> args2 = new LinkedList<String>(Arrays.asList(args));

		if (args.length == 0) {
			// FIXME should raise exception
			errorInArgs = true;
			return;
		}

		if (args2.contains("-?") || args2.contains("-h") || args2.contains("/?") || args2.contains("/h")
				|| args2.contains("--help")) {
			usage = true;
			return;
		}

		if (args2.contains("-v") || args2.contains("--version") || args2.contains("/v")) {
			version = true;
			return;
		}

		while (!args2.isEmpty()) {
			String option = args2.get(0);
			args2.remove(0);

			if (option.equals("--list-files")) {
				listFiles = true;
			} else if (option.equals("--delete-duplicates")) {
				deleteDuplicates = true;
			} else if (option.equals("--move-images")) {
				moveImagesToFolder = true;
			} else if (option.equals("--move-videos")) {
				moveVideosToFolder = true;
			} else if (option.equals("--move-medias")) {
				moveMediasToFolder = true;
			} else if (option.equals("--move-audios")) {
				moveAudiosToFolder = true;
			} else if (option.equals("--clear-db")) {
				clearDB = true;
			} else {
				// FIXME should raise exception
				System.err.println("Unknown option: " + option);
				errorInArgs = true;
				return;
			}
		}

	}
}
