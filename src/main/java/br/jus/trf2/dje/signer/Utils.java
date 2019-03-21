package br.jus.trf2.dje.signer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import com.crivano.swaggerservlet.SwaggerServlet;
import com.crivano.swaggerservlet.SwaggerUtils;

public class Utils {
	private static final Map<String, byte[]> cache = new HashMap<String, byte[]>();

	public static String getUrlBluCServer() {
		return SwaggerServlet.getProperty("blucservice.url");
	}

	public static Connection getConnection() throws Exception {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:");
			DataSource ds = (DataSource) envContext.lookup(SwaggerServlet.getProperty("datasource.name"));
			Connection connection = ds.getConnection();
			if (connection == null)
				throw new Exception("Can't open connection to Oracle.");
			return connection;
		} catch (NameNotFoundException nnfe) {
			Connection connection = null;

			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

			String dbURL = SwaggerServlet.getProperty("datasource.url");
			String username = SwaggerServlet.getProperty("datasource.username");
			String password = SwaggerServlet.getProperty("datasource.password");
			connection = DriverManager.getConnection(dbURL, username, password);
			if (connection == null)
				throw new Exception("Can't open connection.");
			return connection;
		}
	}

	public static String getSQL(String filename) {
		String text = new Scanner(DocListGet.class.getResourceAsStream(filename + ".sql"), "UTF-8").useDelimiter("\\A")
				.next();
		return text;
	}

	public static void store(String sha1, byte[] ba) {
		cache.put(sha1, ba);
	}

	public static byte[] retrieve(String sha1) {
		if (cache.containsKey(sha1)) {
			byte[] ba = cache.get(sha1);
			cache.remove(sha1);
			return ba;
		}
		return null;
	}

	public static byte[] hexToBytes(String hexString) {
		HexBinaryAdapter adapter = new HexBinaryAdapter();
		byte[] bytes = adapter.unmarshal(hexString);
		return bytes;
	}

	public static String bytesToHex(byte[] bytes) {
		HexBinaryAdapter adapter = new HexBinaryAdapter();
		String hexString = adapter.marshal(bytes);
		return hexString;
	}

}
