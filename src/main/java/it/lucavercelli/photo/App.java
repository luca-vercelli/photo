package it.lucavercelli.photo;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Main program. We only calculate hashes when needed.
 *
 */
public class App {

	public String VIDEO_FOLDER = "." + File.separator + "video";
	public String IMG_FOLDER = "." + File.separator + "images";
	public String MEDIA_FOLDER = "." + File.separator + "media";
	public String AUDIO_FOLDER = "." + File.separator + "audio";

	public static void main(String[] args) throws Exception {

		System.setProperty("derby.system.home", System.getProperty("user.home"));
		
		Options options = new Options(args);

		if (options.usage) {
			usage();
			System.exit(0);
		}

		if (options.errorInArgs) {
			usage();
			System.exit(-1);
		}

		App app = new App(options);
		app.mainLoop();
	}

	protected static void usage() {
		System.out.println("Usage: java -jar <filename.jar> [command]");
		System.out.println("Commands:");
		System.out.println("   --list-files");
		System.out.println("   --list-duplicates");
		System.out.println("   --delete-duplicates");
		System.out.println("   --move-images");
		System.out.println("   --move-videos");
		System.out.println("   --move-audios");
		System.out.println("   --move-medias");
	}

	public App(Options options) {
		this.options = options;
	}

	private Options options;
	private EntityManager em = null;
	private FileDAO fileDAO;

	// FIXME il percorso del DB sta in persistence.xml

	protected void mainLoop() throws SQLException, NoSuchAlgorithmException, IOException {

		EntityManagerFactory ef = null;
		try {
			ef = Persistence.createEntityManagerFactory("MyPersistenceUnit");
			em = ef.createEntityManager();
			fileDAO = new FileDAO(em);

			// It is possible to give more commands

			if (options.moveVideosToFolder)
				moveVideos();

			if (options.moveImagesToFolder)
				moveImages();

			if (options.moveAudiosToFolder)
				moveAudios();

			if (options.moveMediasToFolder)
				moveMedias();

			if (options.listFiles)
				refreshDatabase();

			if (options.deleteDuplicates)
				deleteDuplicates();

		} finally {
			if (ef != null)
				ef.close();
		}

	}

	private void moveImages() throws IOException {
		System.out.println("Moving images...");
		File destFolder = new File(IMG_FOLDER).getAbsoluteFile();
		File curFolder = new File(".").getAbsoluteFile();
		commonMoveFiles(destFolder, curFolder, new ImageFileFilter());
	}

	private void moveVideos() throws IOException {
		System.out.println("Moving videos...");
		File destFolder = new File(VIDEO_FOLDER).getAbsoluteFile();
		File curFolder = new File(".").getAbsoluteFile();
		commonMoveFiles(destFolder, curFolder, new VideoFileFilter());
	}

	private void moveAudios() throws IOException {
		System.out.println("Moving audios...");
		File destFolder = new File(AUDIO_FOLDER).getAbsoluteFile();
		File curFolder = new File(".").getAbsoluteFile();
		commonMoveFiles(destFolder, curFolder, new AudioFileFilter());
	}

	private void moveMedias() throws IOException {
		System.out.println("Moving medias...");
		File destFolder = new File(MEDIA_FOLDER).getAbsoluteFile();
		File curFolder = new File(".").getAbsoluteFile();
		commonMoveFiles(destFolder, curFolder, new MediaFileFilter());
	}

	/**
	 * Move all files in all subfolders into a given destination folder
	 * 
	 * @param destFolder
	 *            must be absolute
	 * @param curFolder
	 *            must be absolute
	 * @param filter
	 * @throws IOException
	 */
	private void commonMoveFiles(File destFolder, File curFolder, FileFilter filter) throws IOException {

		destFolder.mkdirs();
		if (!destFolder.exists())
			throw new IOException("Cannot create folder " + destFolder.getPath());

		System.out.println("Scanning folder " + curFolder.getPath() + " ...");

		File[] files = curFolder.listFiles(filter);
		for (File f : files) {
			File destFile = new File(destFolder, f.getName());
			System.out.println("Moving " + f.getPath() + " to " + destFile.getPath());
			boolean success = f.renameTo(destFile);
			if (!success)
				System.err.println("Cannot move file " + f.getPath());
			// TODO on success, should update database
		}

		File[] subfolders = curFolder.listFiles(new FolderFileFilter());

		for (File f : subfolders) {
			f = f.getAbsoluteFile();
			if (!f.getPath().startsWith(destFolder.getPath()))
				commonMoveFiles(destFolder, f, filter);
		}
	}

	private void refreshDatabase() throws SQLException, NoSuchAlgorithmException, IOException {
		System.out.println("Refreshing database...");
		File folder = new File(".").getAbsoluteFile();
		refreshDatabase(folder);
	}

	private void refreshDatabase(File folder) throws SQLException, NoSuchAlgorithmException, IOException {

		File[] mediaFiles = folder.listFiles(new MediaFileFilter());
		
		for (File file : mediaFiles) {

			System.out.println(file.getPath());

			FileRecord f = fileDAO.insertUpdateFile(file);
			searchForDuplicates(f, null);
		}

		File[] subfolders = folder.listFiles(new FolderFileFilter());

		for (File f : subfolders) {
			refreshDatabase(f);
		}

	}

	/**
	 * f has already been inserted in DB, we just update it
	 * 
	 * @param mediaFile
	 * @param filehash
	 *            if already known
	 * @throws SQLException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	protected void searchForDuplicates(FileRecord mediaFile, String filehash)
			throws SQLException, NoSuchAlgorithmException, IOException {

		// Case 1: are there any files with same size? if no, no duplicates.
		List<FileRecord> list = fileDAO.selectBySizeExcludedThis(mediaFile);
		if (list.isEmpty()) {
			return;
		}

		// Now we need hash.
		if (filehash == null || filehash.equals("")) {

			if (mediaFile.filehash == null || mediaFile.filehash.trim().equals("")) {
				mediaFile.filehash = MD5util.getMD5Checksum(new File(mediaFile.filename));
				fileDAO.save(mediaFile);
			}
		}

		// Case 2: are there any files with same size and hash? if so, that is a
		// duplicate.
		FileRecord oneDuplicated = fileDAO.selectOneDuplicated(mediaFile);
		if (oneDuplicated != null) {
			mediaFile.duplicated = oneDuplicated;
			fileDAO.save(mediaFile);
			System.out.println("DUPLICATED OF: " + oneDuplicated.filename);
			return;
		}

		// now I calculate all lacking hash'es
		list = fileDAO.selectBySizeExcludedThisNoHash(mediaFile);
		boolean someThingDone = false;
		for (FileRecord f : list) {
			File f2 = new File(f.filename);
			if (!f2.exists()) {
				fileDAO.deleteRecord(f);
			} else {
				f.filehash = MD5util.getMD5Checksum(f2);
				fileDAO.save(f);
				someThingDone = true;
			}
		}

		// Case 3: repeat case 2 with all hash'es loaded
		if (someThingDone) {
			oneDuplicated = fileDAO.selectOneDuplicated(mediaFile);
			if (oneDuplicated != null) {
				mediaFile.duplicated = oneDuplicated;
				fileDAO.save(mediaFile);
				System.out.println("DUPLICATED OF: " + oneDuplicated.filename);
				return;
			}
		}
	}

	/**
	 * Delete items with "duplicated" field set. Check that at least one
	 * non-duplicated per hash exists.
	 * 
	 * @throws SQLException
	 */
	private void deleteDuplicates() throws SQLException {

		List<FileRecord> list = fileDAO.selectDuplicated();
		for (FileRecord f : list) {
			System.out.println("Deleting " + f.filename);

			if (fileDAO.selectByHashExcludedThis(f).isEmpty()) {
				// This is wrongly marked as duplicated
				f.duplicated = null;
				fileDAO.save(f);

			} else {
				fileDAO.deleteRecord(f);

				File file = new File(f.filename);
				file.delete();
			}
		}

	}
}
