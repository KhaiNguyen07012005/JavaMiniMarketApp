package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import service.CustomerService;

public class RegisterFrame extends JFrame {
	private CustomerService customerService;
	private JTextField txtUsername, txtName, txtPhone;
	private JPasswordField txtPassword;
	private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
	private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
	private static final Color CARD_COLOR = Color.WHITE;

	public RegisterFrame(CustomerService customerService) {
		this.customerService = customerService;
		setTitle("Đăng Ký - Grocery Store");
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setUndecorated(true);

		JPanel mainPanel = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
				var gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), BACKGROUND_COLOR);
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
				g2d.setColor(new Color(0, 0, 0, 50));
				g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
			}
		};
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel contentPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
				g2d.setColor(CARD_COLOR);
				g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
			}
		};
		contentPanel.setBackground(BACKGROUND_COLOR);

		var lblTitle = new JLabel("Đăng Ký", SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblTitle.setForeground(PRIMARY_COLOR);
		lblTitle.setBounds(0, 20, 360, 30);
		contentPanel.add(lblTitle);

		var lblUsername = new JLabel("Tên đăng nhập:");
		lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblUsername.setBounds(30, 60, 120, 25);
		contentPanel.add(lblUsername);

		txtUsername = new JTextField();
		txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		txtUsername.setBounds(150, 60, 180, 30);
		contentPanel.add(txtUsername);

		var lblPassword = new JLabel("Mật khẩu:");
		lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblPassword.setBounds(30, 100, 120, 25);
		contentPanel.add(lblPassword);

		txtPassword = new JPasswordField();
		txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		txtPassword.setBounds(150, 100, 180, 30);
		contentPanel.add(txtPassword);

		var lblName = new JLabel("Họ tên:");
		lblName.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblName.setBounds(30, 140, 120, 25);
		contentPanel.add(lblName);

		txtName = new JTextField();
		txtName.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		txtName.setBounds(150, 140, 180, 30);
		contentPanel.add(txtName);

		var lblPhone = new JLabel("Số điện thoại:");
		lblPhone.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblPhone.setBounds(30, 180, 120, 25);
		contentPanel.add(lblPhone);

		txtPhone = new JTextField();
		txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		txtPhone.setBounds(150, 180, 180, 30);
		contentPanel.add(txtPhone);

		var btnRegister = createStyledButton("Đăng Ký");
		btnRegister.setBounds(130, 230, 100, 35);
		btnRegister.addActionListener(e -> handleRegister());
		contentPanel.add(btnRegister);

		mainPanel.add(contentPanel, BorderLayout.CENTER);
		add(mainPanel);

		var point = new Point();
		mainPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				point.setLocation(e.getPoint());
			}
		});
		mainPanel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				var newPoint = e.getLocationOnScreen();
				setLocation(newPoint.x - point.x, newPoint.y - point.y);
			}
		});

		setVisible(true);
	}

	private JButton createStyledButton(String text) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
				var gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), new Color(100, 181, 246));
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
				super.paintComponent(g);
			}
		};
		button.setFont(new Font("Segoe UI", Font.BOLD, 15));
		button.setForeground(Color.WHITE);
		button.setContentAreaFilled(false);
		button.setBorder(new EmptyBorder(5, 15, 5, 15));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(PRIMARY_COLOR.darker());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(null);
			}
		});
		return button;
	}

	private void handleRegister() {
		var username = txtUsername.getText().trim();
		var password = new String(txtPassword.getPassword()).trim();
		var name = txtName.getText().trim();
		var phone = txtPhone.getText().trim();

		if (username.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (username.contains(" ")) {
			JOptionPane.showMessageDialog(this, "Tên đăng nhập không được chứa khoảng trắng!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!phone.matches("\\d{10}")) {
			JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		var result = customerService.registerCustomer(username, password, name, phone, null);
		if (result.startsWith("KH")) {
			JOptionPane.showMessageDialog(this, "Đăng ký thành công! Mã KH: " + result, "Thành công",
					JOptionPane.INFORMATION_MESSAGE);
			dispose();
		} else {
			JOptionPane.showMessageDialog(this, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}
}