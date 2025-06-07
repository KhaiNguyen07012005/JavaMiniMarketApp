package connect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectDB {
	private static final Properties config = new Properties();
	private static final String CONFIG_FILE = "dbconfig.properties";

	// Default values
	static {
		config.setProperty("db.server", "DESKTOP-U2GBOER");
		config.setProperty("db.port", "1433");
		config.setProperty("db.databaseName", "grocery_storee");
		config.setProperty("db.user", "sa");
		config.setProperty("db.password", "khaianh951");
		config.setProperty("db.encrypt", "true");
		config.setProperty("db.trustServerCertificate", "true");
		loadConfig();
	}

	// Synchronized method to load configuration
	private static synchronized void loadConfig() {
		try {
			var configFile = new File(CONFIG_FILE);
			if (configFile.exists()) {
				try (var fis = new FileInputStream(configFile)) {
					config.load(fis);
				}
			}
		} catch (IOException e) {
			System.err.println("Lỗi đọc file cấu hình database: " + e.getMessage());
		}
	}

	// Synchronized method to get the database URL
	public static synchronized String getUrl() {
		return "jdbc:sqlserver://" + config.getProperty("db.server") + ":" + config.getProperty("db.port")
				+ ";databaseName=" + config.getProperty("db.databaseName") + ";user=" + config.getProperty("db.user")
				+ ";password=" + config.getProperty("db.password") + ";encrypt=" + config.getProperty("db.encrypt")
				+ ";trustServerCertificate=" + config.getProperty("db.trustServerCertificate");
	}

	// Synchronized method to get the database connection
	public static synchronized Connection getCon() {
		try {
			return DriverManager.getConnection(getUrl());
		} catch (SQLException e) {
			System.err.println("Lỗi kết nối database: " + e.getMessage());
			return null;
		}
	}

	// Synchronized method to update configuration
	public static synchronized void updateConfig(String newServer, String newPort, String newDatabaseName,
			String newUser, String newPassword, String newEncrypt, String newTrustServerCertificate)
			throws IOException {
		config.setProperty("db.server", newServer);
		config.setProperty("db.port", newPort);
		config.setProperty("db.databaseName", newDatabaseName);
		config.setProperty("db.user", newUser);
		config.setProperty("db.password", newPassword);
		config.setProperty("db.encrypt", newEncrypt);
		config.setProperty("db.trustServerCertificate", newTrustServerCertificate);

		try (var fos = new FileOutputStream(CONFIG_FILE)) {
			config.store(fos, "Database Configuration");
		}
	}

	// Synchronized getter methods
	public static synchronized String getServer() {
		return config.getProperty("db.server");
	}

	public static synchronized String getPort() {
		return config.getProperty("db.port");
	}

	public static synchronized String getDatabaseName() {
		return config.getProperty("db.databaseName");
	}

	public static synchronized String getUser() {
		return config.getProperty("db.user");
	}

	public static synchronized String getPassword() {
		return config.getProperty("db.password");
	}

	public static synchronized String getEncrypt() {
		return config.getProperty("db.encrypt");
	}

	public static synchronized String getTrustServerCertificate() {
		return config.getProperty("db.trustServerCertificate");
	}
}