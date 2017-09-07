package it.lucavercelli.photo;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;

public class FileDAO {
	EntityManager em;

	public FileDAO(EntityManager em) {
		this.em = em;
	}

	public FileRecord selectByName(String filename) throws SQLException {
		return em.find(FileRecord.class, filename);
	}

	public FileRecord selectByName(File f) throws SQLException {
		return em.find(FileRecord.class, f.getAbsolutePath());
	}

	public List<FileRecord> selectBySize(FileRecord f) throws SQLException {
		return em.createNamedQuery("selectBySize", FileRecord.class).setParameter("fn", f.filename).getResultList();
	}

	public List<FileRecord> selectBySizeExcludedThis(FileRecord f) throws SQLException {
		return em.createNamedQuery("selectBySizeExcluded", FileRecord.class).setParameter("sz", f.filesize)
				.setParameter("fn", f.filename).getResultList();
	}

	public List<FileRecord> selectBySizeExcludedThisNoHash(FileRecord f) throws SQLException {
		return em.createNamedQuery("selectBySizeNoHash", FileRecord.class).setParameter("sz", f.filesize)
				.setParameter("fn", f.filename).getResultList();
	}

	public List<FileRecord> selectByDuplicated(FileRecord f) throws SQLException {
		return em.createNamedQuery("selectByDuplicated", FileRecord.class).setParameter("dupl", f.filename).getResultList();
	}

	public List<FileRecord> selectByHash(String filehash) throws SQLException {
		return em.createNamedQuery("selectByHash", FileRecord.class).setParameter("hash", filehash).getResultList();
	}

	public List<FileRecord> selectByHashExcludedThis(FileRecord f) throws SQLException {
		return em.createNamedQuery("selectByHashExcluded", FileRecord.class).setParameter("hash", f.filehash)
				.setParameter("fn", f.filename).getResultList();
	}

	/**
	 * Pick one random FilePOJO with same filehash, excluded itself.
	 * 
	 * @param f
	 * @return
	 * @throws SQLException
	 */
	public FileRecord selectOneDuplicated(FileRecord f) throws SQLException {
		List<FileRecord> list = em.createNamedQuery("selectByHashExcluded", FileRecord.class).setParameter("hash", f.filehash)
				.setParameter("fn", f.filename).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public List<FileRecord> selectDuplicated() throws SQLException {
		return em.createNamedQuery("selectDuplicated", FileRecord.class).getResultList();
	}

	public List<FileRecord> updateNoDuplicated(FileRecord f) throws SQLException {
		return em.createNamedQuery("updateNoDuplicated", FileRecord.class).setParameter("dupl", f.filename).getResultList();
	}

	/**
	 * Insert or update a File into a FilePOJO.
	 * 
	 * In case of INSERT, "filehash" and "duplicated" are set "null".
	 * 
	 * In case of UPDATE, if the file seems really have changed, "filehash" and
	 * "duplicated" are reset "null".
	 * 
	 * @param file
	 * @return
	 * @throws SQLException
	 */
	public FileRecord insertUpdateFile(File file) throws SQLException {

		file = file.getAbsoluteFile();
		FileRecord f = selectByName(file);
		if (f == null) {
			// must INSERT
			f = new FileRecord(file.getPath(), file.length(), file.lastModified(), null, null);

		} else {
			if (!f.filesize.equals(new Long(file.length())) || !f.lastModified.equals(new Long(file.lastModified()))) {
				// must UPDATE
				f.filesize = file.length();
				f.lastModified = file.lastModified();
				f.filehash = null;
				f.duplicated = null;

				em.createNamedQuery("updateNoDuplicated").setParameter("dupl", f.filename).executeUpdate();
				// TODO update with some other file with same hash
			}
		}

		return f;
	}

	public void save(FileRecord f) {
		em.merge(f);
	}

	/**
	 * Do NOT delete file!
	 * 
	 * @param f
	 */
	public void deleteRecord(FileRecord f) {

		em.createNamedQuery("updateNoDuplicati").setParameter(1, f.filename).executeUpdate();
		// TODO update with some other file with same hash

		em.remove(f);
	}
}
