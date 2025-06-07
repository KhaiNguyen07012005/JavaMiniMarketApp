package panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import service.CustomerService;
import util.Constants;

public class LoginPanel extends JPanel {
	private JTextField loginUsernameField, registerUsernameField, registerNameField, registerAddressField,
			registerPhoneField, resetPhoneField;
	private JPasswordField loginPasswordField, registerPasswordField;
	private JButton loginButton, registerButton, resetButton;
	private CustomerService customerService;
	private String maKH;

	public LoginPanel(CustomerService customerService) {
		this.customerService = customerService;
		setLayout(new GridBagLayout());
		var gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		var loginTabs = new JTabbedPane();
		loginTabs.addTab("Đăng Nhập", createLoginForm(gbc));
		loginTabs.addTab("Đăng Ký", createRegisterForm(gbc));
		loginTabs.addTab("Quên Mật Khẩu", createResetForm(gbc));
		add(loginTabs, gbc);

		setVisible(true);
	}

	private JPanel createLoginForm(GridBagConstraints gbc) {
		var panel = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("Tên Đăng Nhập:"), gbc);
		gbc.gridy = 1;
		loginUsernameField = new JTextField(20);
		panel.add(loginUsernameField, gbc);
		gbc.gridy = 2;
		panel.add(new JLabel("Mật Khẩu:"), gbc);
		gbc.gridy = 3;
		loginPasswordField = new JPasswordField(20);
		panel.add(loginPasswordField, gbc);
		gbc.gridy = 4;
		loginButton = createStyledButton("Đăng Nhập");
		loginButton.addActionListener(e -> handleLogin());
		panel.add(loginButton, gbc);
		return panel;
	}

	private JPanel createRegisterForm(GridBagConstraints gbc) {
		var panel = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("Tên Đăng Nhập:"), gbc);
		gbc.gridy = 1;
		registerUsernameField = new JTextField(20);
		panel.add(registerUsernameField, gbc);
		gbc.gridy = 2;
		panel.add(new JLabel("Mật Khẩu:"), gbc);
		gbc.gridy = 3;
		registerPasswordField = new JPasswordField(20);
		panel.add(registerPasswordField, gbc);
		gbc.gridy = 4;
		panel.add(new JLabel("Tên KH:"), gbc);
		gbc.gridy = 5;
		registerNameField = new JTextField(20);
		panel.add(registerNameField, gbc);
		gbc.gridy = 6;
		panel.add(new JLabel("Địa Chỉ:"), gbc);
		gbc.gridy = 7;
		registerAddressField = new JTextField(20);
		panel.add(registerAddressField, gbc);
		gbc.gridy = 8;
		panel.add(new JLabel("SĐT:"), gbc);
		gbc.gridy = 9;
		registerPhoneField = new JTextField(20);
		panel.add(registerPhoneField, gbc);
		gbc.gridy = 10;
		registerButton = createStyledButton("Đăng Ký");
		registerButton.addActionListener(e -> handleRegister());
		panel.add(registerButton, gbc);
		return panel;
	}

	private JPanel createResetForm(GridBagConstraints gbc) {
		var panel = new JPanel(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("SĐT:"), gbc);
		gbc.gridy = 1;
		resetPhoneField = new JTextField(20);
		panel.add(resetPhoneField, gbc);
		gbc.gridy = 2;
		resetButton = createStyledButton("Đặt Lại Mật Khẩu");
		resetButton.addActionListener(e -> handleResetPassword());
		panel.add(resetButton, gbc);
		return panel;
	}

	private JButton createStyledButton(String text) {
		var button = new JButton(text);
		button.setBackground(Constants.BUTTON_COLOR);
		button.setForeground(Color.WHITE);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(Constants.BUTTON_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(Constants.BUTTON_COLOR);
			}
		});
		return button;
	}

	private void handleLogin() {
		var username = loginUsernameField.getText().trim();
		var password = new String(loginPasswordField.getPassword()).trim();
		if (customerService.loginCustomer(username, password)) {
			this.maKH = username;
			customerService.setMaKH(username);
			JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
		} else {
			JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void handleRegister() {
		var username = registerUsernameField.getText().trim();
		var password = new String(registerPasswordField.getPassword()).trim();
		var name = registerNameField.getText().trim();
		var address = registerAddressField.getText().trim();
		var phone = registerPhoneField.getText().trim();
		if (username.isEmpty() || password.isEmpty() || name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		var result = customerService.registerCustomer(username, password, name, address, phone);
		if (result.startsWith("KH")) {
			this.maKH = result;
			customerService.setMaKH(result);
			JOptionPane.showMessageDialog(this, "Đăng ký thành công! Mã KH: " + result);
		} else {
			JOptionPane.showMessageDialog(this, "Lỗi: " + result, "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void handleResetPassword() {
		var phone = resetPhoneField.getText().trim();
		if (phone.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập SĐT!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		var result = customerService.resetPassword(phone);
		if (result.equals("success")) {
			JOptionPane.showMessageDialog(this, "Đặt lại mật khẩu thành công!");
		} else {
			JOptionPane.showMessageDialog(this, "Lỗi: " + result, "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	public String getMaKH() {
		return maKH;
	}
}