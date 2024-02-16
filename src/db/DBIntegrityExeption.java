package db;

public class DBIntegrityExeption extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DBIntegrityExeption(String msg) {
		super(msg);
	}
}
