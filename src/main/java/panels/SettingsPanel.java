package panels;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import connect.ConnectDB;
import util.UIConfig;

public class SettingsPanel extends JPanel {
	private float opacity = 0f;
	private JTextField txtDbUrl;
	private JTextField txtDbUsername;
	private JPasswordField txtDbPassword;
	private JComboBox<String> themeCombo;
	private JComboBox<String> fontCombo;
	private JTextField txtBackupPath;
	private JTextField txtRestorePath;

	public SettingsPanel() {
		setLayout(new BorderLayout());
		setBackground(UIConfig.getBackgroundColor());
		setOpaque(false);

		// Start fade-in animation
		startFadeInAnimation();

		// Header
		var headerPanel = new JPanel();
		headerPanel.setBackground(UIConfig.getPrimaryColor());
		headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		var headerLabel = new JLabel("Cài Đặt Hệ Thống", JLabel.CENTER);
		headerLabel.setFont(UIConfig.getHeaderFont());
		headerLabel.setForeground(Color.WHITE);
		headerPanel.add(headerLabel);
		add(headerPanel, BorderLayout.NORTH);

		// Form Panel
		var formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(UIConfig.getBackgroundColor());
		formPanel.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		var gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Database Config
		var lblDbConfig = new JLabel("Cấu Hình Database");
		lblDbConfig.setFont(UIConfig.getTitleFont());
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		formPanel.add(lblDbConfig, gbc);
		gbc.gridwidth = 1;

		var lblDbUrl = new JLabel("URL:");
		lblDbUrl.setFont(UIConfig.getLabelFont());
		gbc.gridx = 0;
		gbc.gridy = 1;
		formPanel.add(lblDbUrl, gbc);

		txtDbUrl = new JTextField(20);
		txtDbUrl.setFont(UIConfig.getLabelFont());
		txtDbUrl.setText("jdbc:sqlserver://localhost:1433;databaseName=YourDatabase");
		gbc.gridx = 1;
		gbc.gridy = 1;
		formPanel.add(txtDbUrl, gbc);

		var lblDbUsername = new JLabel("Username:");
		lblDbUsername.setFont(UIConfig.getLabelFont());
		gbc.gridx = 0;
		gbc.gridy = 2;
		formPanel.add(lblDbUsername, gbc);

		txtDbUsername = new JTextField(20);
		txtDbUsername.setFont(UIConfig.getLabelFont());
		txtDbUsername.setText("sa");
		gbc.gridx = 1;
		gbc.gridy = 2;
		formPanel.add(txtDbUsername, gbc);

		var lblDbPassword = new JLabel("Password:");
		lblDbPassword.setFont(UIConfig.getLabelFont());
		gbc.gridx = 0;
		gbc.gridy = 3;
		formPanel.add(lblDbPassword, gbc);

		txtDbPassword = new JPasswordField(20);
		txtDbPassword.setFont(UIConfig.getLabelFont());
		txtDbPassword.setText("yourpassword");
		gbc.gridx = 1;
		gbc.gridy = 3;
		formPanel.add(txtDbPassword, gbc);

		var btnSaveDbConfig = new JButton("Lưu Cấu Hình DB");
		styleButton(btnSaveDbConfig);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		formPanel.add(btnSaveDbConfig, gbc);
		gbc.gridwidth = 1;

		// UI Config
		var lblUiConfig = new JLabel("Cấu Hình Giao Diện");
		lblUiConfig.setFont(UIConfig.getTitleFont());
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		formPanel.add(lblUiConfig, gbc);
		gbc.gridwidth = 1;

		var lblTheme = new JLabel("Theme:");
		lblTheme.setFont(UIConfig.getLabelFont());
		gbc.gridx = 0;
		gbc.gridy = 6;
		formPanel.add(lblTheme, gbc);

		themeCombo = new JComboBox<>(new String[] { "Default", "Dark", "Light" });
		themeCombo.setFont(UIConfig.getLabelFont());
		gbc.gridx = 1;
		gbc.gridy = 6;
		formPanel.add(themeCombo, gbc);

		var lblFont = new JLabel("Font:");
		lblFont.setFont(UIConfig.getLabelFont());
		gbc.gridx = 0;
		gbc.gridy = 7;
		formPanel.add(lblFont, gbc);

		fontCombo = new JComboBox<>(new String[] { "SansSerif", "Arial", "Times New Roman" });
		fontCombo.setFont(UIConfig.getLabelFont());
		gbc.gridx = 1;
		gbc.gridy = 7;
		formPanel.add(fontCombo, gbc);

		var btnSaveUiConfig = new JButton("Lưu Cấu Hình UI");
		styleButton(btnSaveUiConfig);
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 2;
		formPanel.add(btnSaveUiConfig, gbc);
		gbc.gridwidth = 1;

		// Backup/Restore
		var lblBackupRestore = new JLabel("Sao Lưu/Phục Hồi Database");
		lblBackupRestore.setFont(UIConfig.getTitleFont());
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.gridwidth = 2;
		formPanel.add(lblBackupRestore, gbc);
		gbc.gridwidth = 1;

		var lblBackupPath = new JLabel("Đường Dẫn Sao Lưu:");
		lblBackupPath.setFont(UIConfig.getLabelFont());
		gbc.gridx = 0;
		gbc.gridy = 10;
		formPanel.add(lblBackupPath, gbc);

		txtBackupPath = new JTextField(20);
		txtBackupPath.setFont(UIConfig.getLabelFont());
		txtBackupPath.setText("C:\\Backups\\YourDatabase.bak");
		gbc.gridx = 1;
		gbc.gridy = 10;
		formPanel.add(txtBackupPath, gbc);

		var btnBackup = new JButton("Sao Lưu");
		styleButton(btnBackup);
		gbc.gridx = 0;
		gbc.gridy = 11;
		gbc.gridwidth = 2;
		formPanel.add(btnBackup, gbc);
		gbc.gridwidth = 1;

		var lblRestorePath = new JLabel("Đường Dẫn Phục Hồi:");
		lblRestorePath.setFont(UIConfig.getLabelFont());
		gbc.gridx = 0;
		gbc.gridy = 12;
		formPanel.add(lblRestorePath, gbc);

		txtRestorePath = new JTextField(20);
		txtRestorePath.setFont(UIConfig.getLabelFont());
		txtRestorePath.setText("C:\\Backups\\YourDatabase.bak");
		gbc.gridx = 1;
		gbc.gridy = 12;
		formPanel.add(txtRestorePath, gbc);

		var btnRestore = new JButton("Phục Hồi");
		styleButton(btnRestore);
		gbc.gridx = 0;
		gbc.gridy = 13;
		gbc.gridwidth = 2;
		formPanel.add(btnRestore, gbc);

		add(formPanel, BorderLayout.CENTER);

		// Event Listeners
		btnSaveDbConfig.addActionListener(e -> saveDbConfig());
		btnSaveUiConfig.addActionListener(e -> saveUiConfig());
		btnBackup.addActionListener(e -> backupDatabase());
		btnRestore.addActionListener(e -> restoreDatabase());
	}

	private void startFadeInAnimation() {
		var timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				opacity += 0.05f;
				if (opacity >= 1f) {
					opacity = 1f;
					timer.cancel();
				}
				repaint();
			}
		}, 0, 50);
	}

	@Override
	protected void paintComponent(Graphics g) {
		var g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g2d);
		g2d.dispose();
	}

	private void styleButton(JButton button) {
		button.setBackground(UIConfig.getSecondaryColor());
		button.setForeground(Color.WHITE);
		button.setFont(UIConfig.getLabelFont());
		button.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		button.setFocusPainted(false);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(UIConfig.getPrimaryColor());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(UIConfig.getSecondaryColor());
			}
		});
	}

	private void saveDbConfig() {
		try {
			var url = txtDbUrl.getText().trim();
			var username = txtDbUsername.getText().trim();
			var password = new String(txtDbPassword.getPassword());
			if (url.isEmpty() || username.isEmpty()) {
				JOptionPane.showMessageDialog(this, "URL và Username không được để trống!", "Cảnh báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			ConnectDB.updateConfig(url, username, password, password, password, password, password);
			JOptionPane.showMessageDialog(this, "Lưu cấu hình database thành công! Vui lòng khởi động lại ứng dụng.",
					"Thành công", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Lỗi lưu cấu hình database: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveUiConfig() {
		try {
			var theme = (String) themeCombo.getSelectedItem();
			var font = (String) fontCombo.getSelectedItem();
			UIConfig.updateConfig(theme, font);
			JOptionPane.showMessageDialog(this, "Lưu cấu hình giao diện thành công! Vui lòng khởi động lại ứng dụng.",
					"Thành công", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Lỗi lưu cấu hình giao diện: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void backupDatabase() {
		var backupPath = txtBackupPath.getText().trim();
		if (backupPath.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đường dẫn sao lưu!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			var conn = ConnectDB.getCon();
			var sql = "BACKUP DATABASE YourDatabase TO DISK = ? WITH FORMAT";
			var ps = conn.prepareStatement(sql);
			try {
				ps.setString(1, backupPath);
				ps.execute();
				JOptionPane.showMessageDialog(this, "Sao lưu database thành công!", "Thành công",
						JOptionPane.INFORMATION_MESSAGE);
			} finally {
				ps.close();
				conn.close();
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi sao lưu database: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void restoreDatabase() {
		var restorePath = txtRestorePath.getText().trim();
		if (restorePath.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đường dẫn phục hồi!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			var conn = ConnectDB.getCon();
			// Đặt database về single-user mode để restore
			var setSingleUser = "ALTER DATABASE YourDatabase SET SINGLE_USER WITH ROLLBACK IMMEDIATE";
			var stmt = conn.createStatement();
			try {
				stmt.execute(setSingleUser);
				var sql = "RESTORE DATABASE YourDatabase FROM DISK = ? WITH REPLACE";
				var ps = conn.prepareStatement(sql);
				try {
					ps.setString(1, restorePath);
					ps.execute();
					JOptionPane.showMessageDialog(this,
							"Phục hồi database thành công! Vui lòng khởi động lại ứng dụng.", "Thành công",
							JOptionPane.INFORMATION_MESSAGE);
				} finally {
					ps.close();
				}
				// Đặt lại multi-user mode
				var setMultiUser = "ALTER DATABASE YourDatabase SET MULTI_USER";
				stmt.execute(setMultiUser);
			} finally {
				stmt.close();
				conn.close();
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi phục hồi database: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}