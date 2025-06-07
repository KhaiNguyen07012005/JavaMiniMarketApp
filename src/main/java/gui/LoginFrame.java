package gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import connect.ConnectDB;

public class LoginFrame extends JFrame {
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	private JCheckBox chkShowPassword;

	public LoginFrame() {
		setTitle("Đăng nhập hệ thống - WinMart");
		setSize(400, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		var panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(Color.WHITE);

		var lblTitle = new JLabel("WINMART LOGIN");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblTitle.setForeground(new Color(0, 102, 204));
		lblTitle.setBounds(120, 20, 200, 30);
		panel.add(lblTitle);

		var lblUsername = new JLabel("Tên đăng nhập:");
		lblUsername.setBounds(50, 70, 100, 25);
		panel.add(lblUsername);

		txtUsername = new JTextField();
		txtUsername.setBounds(160, 70, 180, 25);
		panel.add(txtUsername);

		var lblPassword = new JLabel("Mật khẩu:");
		lblPassword.setBounds(50, 110, 100, 25);
		panel.add(lblPassword);

		txtPassword = new JPasswordField();
		txtPassword.setBounds(160, 110, 180, 25);
		panel.add(txtPassword);

		chkShowPassword = new JCheckBox("Hiện mật khẩu");
		chkShowPassword.setBackground(Color.WHITE);
		chkShowPassword.setBounds(160, 140, 150, 25);
		panel.add(chkShowPassword);

		chkShowPassword.addActionListener(e -> {
			txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '●');
		});

		var btnLogin = new JButton("Đăng nhập");
		btnLogin.setBounds(140, 180, 120, 35);
		btnLogin.setBackground(new Color(0, 102, 204));
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setFocusPainted(false);
		panel.add(btnLogin);

		btnLogin.addActionListener(e -> login());

		getRootPane().setDefaultButton(btnLogin);

		add(panel);
		setVisible(true);
	}

	private void login() {
		var user = txtUsername.getText().trim();
		var pass = new String(txtPassword.getPassword());

		if (user.isEmpty() || pass.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
			return;
		}

		try (var conn = ConnectDB.getCon()) {
			if (conn == null) {
				JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu!");
				return;
			}

			var sql = "SELECT * FROM TAIKHOAN WHERE TenDangNhap = ? AND MatKhau = ?";
			try (var ps = conn.prepareStatement(sql)) {
				ps.setString(1, user);
				ps.setString(2, pass);

				try (var rs = ps.executeQuery()) {
					if (rs.next()) {
						JOptionPane.showMessageDialog(null, "Đăng nhập thành công!");
						dispose();
						new MainFrame(user).setVisible(true);
					} else {
						JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng!");
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi khi đăng nhập: " + ex.getMessage());
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(LoginFrame::new);
	}
}
