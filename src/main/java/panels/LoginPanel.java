package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import service.CustomerService;

public class LoginPanel extends JPanel {
	private JTextField loginUsernameField, regNameField, regPhoneField, regAddressField, regUsernameField;
	private JPasswordField loginPasswordField, regPasswordField;
	private CustomerService customerService;
	private String maKH;
	private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
	private static final Color SECONDARY_COLOR = new Color(244, 67, 54);
	private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
	private static final Color CARD_COLOR = Color.WHITE;
	private static final Color TEXT_COLOR = new Color(33, 37, 41);
	private static final Color TAB_SELECTED_COLOR = new Color(144, 202, 249);

	public LoginPanel(CustomerService customerService) {
		this.customerService = customerService;
		setLayout(new BorderLayout());
		setBackground(BACKGROUND_COLOR);
		initializeComponents();
	}

	private void initializeComponents() {
		JTabbedPane tabbedPane = new JTabbedPane() {
			@Override
			protected void paintComponent(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(CARD_COLOR);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
				super.paintComponent(g);
			}
		};
		tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
		tabbedPane.setForeground(TEXT_COLOR);
		tabbedPane.setOpaque(false);

		// Login Tab
		var loginPanel = new JPanel(new GridBagLayout());
		loginPanel.setBackground(CARD_COLOR);
		loginPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		var gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		var loginTitle = new JLabel("Đăng Nhập", SwingConstants.CENTER);
		loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
		loginTitle.setForeground(PRIMARY_COLOR);
		loginPanel.add(loginTitle, gbc);

		gbc.gridwidth = 1;
		gbc.gridy = 1;
		gbc.gridx = 0;
		loginPanel.add(new JLabel("Tên đăng nhập:"), gbc);
		gbc.gridx = 1;
		loginUsernameField = new JTextField(20);
		loginPanel.add(loginUsernameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		loginPanel.add(new JLabel("Mật khẩu:"), gbc);
		gbc.gridx = 1;
		loginPasswordField = new JPasswordField(20);
		loginPanel.add(loginPasswordField, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		var loginButton = createStyledButton("Đăng Nhập");
		loginButton.addActionListener(e -> {
			try {
				handleLogin();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		loginPanel.add(loginButton, gbc);

		// Registration Tab
		var regPanel = new JPanel(new GridBagLayout());
		regPanel.setBackground(CARD_COLOR);
		regPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		var regTitle = new JLabel("Đăng Ký", SwingConstants.CENTER);
		regTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
		regTitle.setForeground(PRIMARY_COLOR);
		regPanel.add(regTitle, gbc);

		gbc.gridwidth = 1;
		gbc.gridy = 1;
		gbc.gridx = 0;
		regPanel.add(new JLabel("Họ và tên:"), gbc);
		gbc.gridx = 1;
		regNameField = new JTextField(20);
		regPanel.add(regNameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		regPanel.add(new JLabel("Số điện thoại:"), gbc);
		gbc.gridx = 1;
		regPhoneField = new JTextField(20);
		regPanel.add(regPhoneField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		regPanel.add(new JLabel("Địa chỉ:"), gbc);
		gbc.gridx = 1;
		regAddressField = new JTextField(20);
		regPanel.add(regAddressField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		regPanel.add(new JLabel("Tên đăng nhập:"), gbc);
		gbc.gridx = 1;
		regUsernameField = new JTextField(20);
		regPanel.add(regUsernameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		regPanel.add(new JLabel("Mật khẩu:"), gbc);
		gbc.gridx = 1;
		regPasswordField = new JPasswordField(20);
		regPanel.add(regPasswordField, gbc);

		gbc.gridx = 1;
		gbc.gridy = 6;
		var regButton = createStyledButton("Đăng Ký");
		regButton.addActionListener(e -> {
			try {
				handleRegistration();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		regPanel.add(regButton, gbc);

		tabbedPane.addTab("Đăng Nhập", loginPanel);
		tabbedPane.addTab("Đăng Ký", regPanel);
		add(tabbedPane, BorderLayout.CENTER);
	}

	private JButton createStyledButton(String text) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(getBackground());
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				super.paintComponent(g);
			}

			@Override
			protected void paintBorder(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(new Color(0, 0, 0, 50));
				g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
			}
		};
		button.setBackground(PRIMARY_COLOR);
		button.setForeground(Color.WHITE);
		button.setFont(new Font("Segoe UI", Font.BOLD, 14));
		button.setFocusPainted(false);
		button.setBorder(new EmptyBorder(8, 15, 8, 15));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(PRIMARY_COLOR.brighter());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(PRIMARY_COLOR);
			}
		});
		return button;
	}

	private void handleLogin() throws SQLException {
		var username = loginUsernameField.getText().trim();
		var password = new String(loginPasswordField.getPassword()).trim();

		if (username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		var result = customerService.authenticateCustomer(username, password);
		if (result != null) {
			this.maKH = result;
			JOptionPane.showMessageDialog(this, "Đăng nhập thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
			loginUsernameField.setText("");
			loginPasswordField.setText("");
		} else {
			JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void handleRegistration() throws SQLException {
		var name = regNameField.getText().trim();
		var phone = regPhoneField.getText().trim();
		var address = regAddressField.getText().trim();
		var username = regUsernameField.getText().trim();
		var password = new String(regPasswordField.getPassword()).trim();

		if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!phone.matches("^0\\d{9}$")) {
			JOptionPane.showMessageDialog(this,
					"Số điện thoại không hợp lệ! Vui lòng nhập đúng định dạng (10 chữ số, bắt đầu bằng 0).", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		var newMaKH = customerService.registerCustomer(name, phone, address, username, password);
		if (newMaKH != null) {
			this.maKH = newMaKH;
			JOptionPane.showMessageDialog(this, "Đăng ký thành công! Mã KH: " + newMaKH, "Thành công",
					JOptionPane.INFORMATION_MESSAGE);
			regNameField.setText("");
			regPhoneField.setText("");
			regAddressField.setText("");
			regUsernameField.setText("");
			regPasswordField.setText("");
		} else {
			JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại hoặc lỗi khi đăng ký!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public String getMaKH() {
		return maKH;
	}
}