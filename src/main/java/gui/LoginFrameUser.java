package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import connect.ConnectDB;
import service.CustomerService;

public class LoginFrameUser extends JFrame {
	private CustomerService customerService;
	private JTextField txtUsername;
	private JPasswordField txtPassword;
	private boolean isMaximized = false;
	private Point dragPoint;

	private static final Color PRIMARY_COLOR = new Color(33, 150, 243); // Blue
	private static final Color SECONDARY_COLOR = new Color(244, 67, 54); // Red
	private static final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Light gray
	private static final Color CARD_COLOR = new Color(255, 255, 255); // White
	private static final Color TEXT_COLOR = new Color(33, 37, 41); // Dark gray
	private static final Color BORDER_COLOR = new Color(206, 212, 218); // Light border

	public LoginFrameUser() {
		var dbConnection = new ConnectDB();
		if (dbConnection.getCon() == null) {
			JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		customerService = new CustomerService(dbConnection.getCon());

		setTitle("Đăng Nhập - Grocery Store");
		setSize(400, 350);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setShape(new RoundRectangle2D.Double(0, 0, 400, 350, 15, 15));

		JPanel mainPanel = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				var gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), BACKGROUND_COLOR);
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
			}
		};
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		var headerPanel = createHeaderPanel();
		mainPanel.add(headerPanel, BorderLayout.NORTH);

		JPanel contentPanel = new JPanel(new GridBagLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(CARD_COLOR);
				g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
				g2d.setColor(new Color(0, 0, 0, 30));
				g2d.drawRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
			}
		};
		contentPanel.setBackground(new Color(0, 0, 0, 0));
		contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		var gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		var lblTitle = new JLabel("Đăng Nhập", SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblTitle.setForeground(PRIMARY_COLOR);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		contentPanel.add(lblTitle, gbc);

		var lblUsername = new JLabel("Tên đăng nhập:");
		lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		gbc.gridy++;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.WEST;
		contentPanel.add(lblUsername, gbc);

		txtUsername = new JTextField(15);
		txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtUsername.setBorder(new LineBorder(BORDER_COLOR, 1, true));
		txtUsername.setBackground(CARD_COLOR);
		txtUsername.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				txtUsername.setBorder(new LineBorder(PRIMARY_COLOR, 2, true));
			}

			@Override
			public void focusLost(FocusEvent e) {
				txtUsername.setBorder(new LineBorder(BORDER_COLOR, 1, true));
			}
		});
		gbc.gridx = 1;
		contentPanel.add(txtUsername, gbc);

		var lblPassword = new JLabel("Mật khẩu:");
		lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		gbc.gridx = 0;
		gbc.gridy++;
		contentPanel.add(lblPassword, gbc);

		txtPassword = new JPasswordField(15);
		txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtPassword.setBorder(new LineBorder(BORDER_COLOR, 1, true));
		txtPassword.setBackground(CARD_COLOR);
		txtPassword.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				txtPassword.setBorder(new LineBorder(PRIMARY_COLOR, 2, true));
			}

			@Override
			public void focusLost(FocusEvent e) {
				txtPassword.setBorder(new LineBorder(BORDER_COLOR, 1, true));
			}
		});
		gbc.gridx = 1;
		contentPanel.add(txtPassword, gbc);

		var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		buttonPanel.setOpaque(false);

		var btnLogin = createStyledButton("Đăng Nhập");
		btnLogin.addActionListener(e -> handleLogin());
		buttonPanel.add(btnLogin);

		var btnRegister = createStyledButton("Đăng Ký");
		btnRegister.addActionListener(e -> {
			new RegisterFrame(customerService);
			dispose();
		});
		buttonPanel.add(btnRegister);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		contentPanel.add(buttonPanel, gbc);

		mainPanel.add(contentPanel, BorderLayout.CENTER);
		add(mainPanel);

		setOpacity(0.0f);
		var fadeTimer = new Timer(30, new ActionListener() {
			float opacity = 0.0f;

			@Override
			public void actionPerformed(ActionEvent e) {
				opacity += 0.05f;
				setOpacity(Math.min(opacity, 1.0f));
				if (opacity >= 1.0f) {
					((Timer) e.getSource()).stop();
				}
			}
		});
		fadeTimer.start();

		setVisible(true);
	}

	private JPanel createHeaderPanel() {
		JPanel headerPanel = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				var gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), new Color(100, 181, 246));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		headerPanel.setPreferredSize(new Dimension(0, 30));
		headerPanel.setOpaque(false);

		var titleLabel = new JLabel("Grocery Store", SwingConstants.LEFT);
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
		headerPanel.add(titleLabel, BorderLayout.WEST);

		var controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		controlPanel.setOpaque(false);

		var btnMinimize = createControlButton("─", new Color(255, 193, 7));
		btnMinimize.addActionListener(e -> setState(JFrame.ICONIFIED));
		controlPanel.add(btnMinimize);

		var btnMaximize = createControlButton("□", new Color(76, 175, 80));
		btnMaximize.addActionListener(e -> {
			if (isMaximized) {
				setExtendedState(JFrame.NORMAL);
				setShape(new RoundRectangle2D.Double(0, 0, 400, 350, 15, 15));
			} else {
				setExtendedState(JFrame.MAXIMIZED_BOTH);
				setShape(null);
			}
			isMaximized = !isMaximized;
		});
		controlPanel.add(btnMaximize);

		var btnClose = createControlButton("✕", SECONDARY_COLOR);
		btnClose.addActionListener(e -> System.exit(0));
		controlPanel.add(btnClose);

		headerPanel.add(controlPanel, BorderLayout.EAST);

		headerPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				dragPoint = e.getPoint();
			}
		});
		headerPanel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				var p = e.getLocationOnScreen();
				setLocation(p.x - dragPoint.x, p.y - dragPoint.y);
			}
		});

		return headerPanel;
	}

	private JButton createControlButton(String text, Color color) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(getBackground());
				g2d.fillOval(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		button.setForeground(Color.WHITE);
		button.setBackground(color);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		button.setPreferredSize(new Dimension(20, 20));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(color.darker());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(color);
			}
		});
		return button;
	}

	private JButton createStyledButton(String text) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				var gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), new Color(100, 181, 246));
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
				g2d.setColor(new Color(0, 0, 0, 50));
				g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
				super.paintComponent(g);
			}
		};
		button.setFont(new Font("Segoe UI", Font.BOLD, 14));
		button.setForeground(Color.WHITE);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		button.setPreferredSize(new Dimension(120, 40));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		return button;
	}

	private void handleLogin() {
		var username = txtUsername.getText().trim();
		var password = new String(txtPassword.getPassword()).trim();
		if (username.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (customerService.loginCustomer(username, password)) {
			dispose();
			SwingUtilities.invokeLater(() -> new CustomerInterface());
		} else {
			JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new LoginFrameUser());
	}
}