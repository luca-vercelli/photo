package it.lucavercelli.photo;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

public class TestDatabase {

	static {
		System.setProperty("derby.system.home", System.getProperty("user.home"));
	}

	EntityManagerFactory ef = null;
	EntityManager em = null;
	FileDAO fileDAO = null;

	@Before
	public void setUp() throws SQLException {
		ef = null;
		ef = Persistence.createEntityManagerFactory("MyPersistenceUnit");
		em = ef.createEntityManager();
		fileDAO = new FileDAO(em);
		System.setProperty("user.dir", new File("target/test-classes").getAbsolutePath());
	}

	@After
	public void tearDown() throws SQLException {

		System.setProperty("user.dir", new File("../..").getAbsolutePath());

		if (ef != null)
			ef.close();
	}

	@Test
	public void testFake() throws SQLException {

		Options opt = new Options(new String[] { "--goofy" });
		assertTrue("Options should include 'errorInArgs'", opt.errorInArgs);

	}

	private File getResource(String uri) {
		return new File(uri.replace('/', File.separatorChar)).getAbsoluteFile();
	}

	@Test
	public void testList() throws SQLException, NoSuchAlgorithmException, IOException {

		File f1 = getResource("./folder_a/img1.png");
		File f2 = getResource("./folder_a/img2.jpeg");

		assertTrue("Set up error: missing resource files", f1.exists() && f2.exists());

		Options opt = new Options(new String[] { "--list-files" });
		assertTrue("Options should include 'listFiles'", opt.listFiles);

		App app = new App(opt);
		app.mainLoop();

		List<FileRecord> list1 = em.createQuery("from FileRecord where filename = :fn ", FileRecord.class)
				.setParameter("fn", f1.getPath()).getResultList();

		assertNotNull("There should be exactly 1 record", list1);
		assertEquals("There should be exactly 1 record", 1, list1.size());

		FileRecord fr1 = list1.get(0);

		List<FileRecord> list2 = em.createQuery("from FileRecord where filename = :fn ", FileRecord.class)
				.setParameter("fn", f2.getPath()).getResultList();

		assertNotNull("There should be exactly 1 record", list2);
		assertEquals("There should be exactly 1 record", 1, list2.size());

		FileRecord fr2 = list2.get(0);

		assertTrue("Exactly one of the files should be marked duplicated",
				fr1.duplicated != null || fr2.duplicated != null);

	}

}
