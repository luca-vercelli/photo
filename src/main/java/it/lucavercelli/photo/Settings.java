package it.lucavercelli.photo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "settings")
public class Settings {

	@Id
	public Long id;

	@Temporal(TemporalType.TIMESTAMP)
	public Date lastRun;
}
