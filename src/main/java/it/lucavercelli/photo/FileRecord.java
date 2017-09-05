package it.lucavercelli.photo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "files")
@NamedQueries({ @NamedQuery(name = "selectByName", query = "FROM FileRecord WHERE filename = ?"),
		@NamedQuery(name = "selectBySize", query = "from FileRecord WHERE filesize = ? "),
		@NamedQuery(name = "selectBySizeExcluded", query = "from FileRecord WHERE filesize = ? AND filename <> ?"),
		@NamedQuery(name = "selectBySizeNoHash", query = "from FileRecord WHERE filesize = ? AND filename <> ? AND filehash IS NULL"),
		@NamedQuery(name = "selectByDuplicated", query = "from FileRecord WHERE duplicated = ? "),
		@NamedQuery(name = "selectByHash", query = "from FileRecord WHERE filehash = ? "),
		@NamedQuery(name = "selectByHashExcluded", query = "from FileRecord WHERE filehash = ? AND filename <> ? "),
		@NamedQuery(name = "selectDuplicated", query = "from FileRecord WHERE duplicated IS NOT NULL") })
@NamedNativeQueries({
		@NamedNativeQuery(name = "updateNoDuplicated", query = "UPDATE files SET duplicated = NULL WHERE duplicated = ? ") })
public class FileRecord {

	@Id
	@Column(name = "filename")
	public String filename;

	@Column(name = "filesize", nullable = false)
	public Long filesize;

	@Column(name = "last_modified", nullable = false)
	public Long lastModified;

	@Column(name = "filehash")
	public String filehash;

	@ManyToOne
	public FileRecord duplicato;

	public FileRecord() {
	}

	public FileRecord(String filename, long filesize, long lastModified, String filehash, FileRecord duplicato) {
		this.filename = filename;
		this.filesize = filesize;
		this.filehash = filehash;
		this.duplicato = duplicato;
	}

}
