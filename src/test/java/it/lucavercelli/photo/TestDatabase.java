package it.lucavercelli.photo;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
	}

	@After
	public void tearDown() throws SQLException {

		if (ef != null)
			ef.close();
	}

	@Test
	public void test() throws SQLException {

		// TODO
	}

}
