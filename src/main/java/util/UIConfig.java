package util;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class UIConfig {
	private static Color primaryColor = new Color(0, 128, 128); // Teal
	private static Color secondaryColor = new Color(77, 182, 172); // Light Teal
	private static Color backgroundColor = new Color(245, 247, 250); // Off-white
	private static Color textColor = new Color(45, 55, 72); // Dark Gray
	private static Font headerFont = new Font("SansSerif", Font.BOLD, 24);
	private static Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
	private static Font titleFont = new Font("SansSerif", Font.BOLD, 16);

	static {
		loadConfig();
	}

	private static void loadConfig() {
		try {
			var props = new Properties();
			var configFile = new File("ui.properties");
			if (configFile.exists()) {
				var fis = new FileInputStream(configFile);
				try {
					props.load(fis);
					var theme = props.getProperty("ui.theme", "Default");
					var fontName = props.getProperty("ui.font", "SansSerif");

					// Load theme
					if (theme.equals("Dark")) {
						primaryColor = new Color(33, 33, 33); // Dark Gray
						secondaryColor = new Color(66, 66, 66); // Lighter Gray
						backgroundColor = new Color(44, 44, 44); // Dark Background
						textColor = new Color(200, 200, 200); // Light Text
					} else if (theme.equals("Light")) {
						primaryColor = new Color(255, 255, 255); // White
						secondaryColor = new Color(220, 220, 220); // Light Gray
						backgroundColor = new Color(255, 255, 255); // White Background
						textColor = new Color(0, 0, 0); // Black Text
					}

					// Load font
					headerFont = new Font(fontName, Font.BOLD, 24);
					labelFont = new Font(fontName, Font.PLAIN, 14);
					titleFont = new Font(fontName, Font.BOLD, 16);
				} finally {
					fis.close();
				}
			}
		} catch (IOException e) {
			System.err.println("Lỗi đọc file cấu hình UI: " + e.getMessage());
		}
	}

	public static void updateConfig(String theme, String font) throws IOException {
		var props = new Properties();
		props.setProperty("ui.theme", theme);
		props.setProperty("ui.font", font);

		var fos = new FileOutputStream("ui.properties");
		try {
			props.store(fos, "UI Configuration");
		} finally {
			fos.close();
		}
		loadConfig(); // Reload config to apply changes
	}

	// Getters
	public static Color getPrimaryColor() {
		return primaryColor;
	}

	public static Color getSecondaryColor() {
		return secondaryColor;
	}

	public static Color getBackgroundColor() {
		return backgroundColor;
	}

	public static Color getTextColor() {
		return textColor;
	}

	public static Font getHeaderFont() {
		return headerFont;
	}

	public static Font getLabelFont() {
		return labelFont;
	}

	public static Font getTitleFont() {
		return titleFont;
	}
}