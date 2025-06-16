package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import service.CustomerService;

public class ProfilePanel extends JPanel {
	private JTextField nameField, phoneField;
	private CustomerService customerService;
	private String maKH;
	private static final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Light Gray
	private static final Color SIDEBAR_COLOR = new Color(240, 240, 240); // Slightly darker gray
	private static final Color BUTTON_COLOR = new Color(255, 69, 0); // Orange from Shopee

	public ProfilePanel(CustomerService customerService, String maKH) {
		this.customerService = customerService;
		this.maKH = maKH;
		customerService.setMaKH(maKH);
		setLayout(new BorderLayout());
		setBackground(BACKGROUND_COLOR);
		initializeComponents();
	}

	private void initializeComponents() {
		// Sidebar (Cột trái)
		var sidebar = new JPanel();
		sidebar.setPreferredSize(new Dimension(200, 0));
		sidebar.setBackground(SIDEBAR_COLOR);
		sidebar.setLayout(new BorderLayout());
		sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		var sidebarContent = new JPanel();
		sidebarContent.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
		sidebarContent.setBackground(SIDEBAR_COLOR);

		addSidebarItem(sidebarContent, "📬", "Thông Báo", null);
		addSidebarItem(sidebarContent, "👤", "Tài Khoản Của Tôi", null);
		addSidebarItem(sidebarContent, "🛒", "Đơn Mua", null);
		addSidebarItem(sidebarContent, "🎟️", "Kho Voucher", null);
		addSidebarItem(sidebarContent, "💰", "Shopee Xu", null);

		sidebar.add(sidebarContent, BorderLayout.NORTH);
		add(sidebar, BorderLayout.WEST);

		// Main Content (Cột phải)
		var mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(BACKGROUND_COLOR);
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		var formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(Color.WHITE);
		formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		var gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Tiêu đề
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		var titleLabel = new JLabel("Hồ Sơ Của Tôi");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
		formPanel.add(titleLabel, gbc);

		gbc.gridwidth = 1;
		gbc.gridy = 1;
		formPanel.add(new JLabel("Quản lý thông tin hồ sơ để bảo mật tài khoản"), gbc);

		// Tên
		gbc.gridx = 0;
		gbc.gridy = 2;
		formPanel.add(new JLabel("Tên:"), gbc);
		gbc.gridx = 1;
		nameField = new JTextField(20);
		formPanel.add(nameField, gbc);

		// Số điện thoại
		gbc.gridx = 0;
		gbc.gridy = 3;
		formPanel.add(new JLabel("Số điện thoại:"), gbc);
		gbc.gridx = 1;
		phoneField = new JTextField(20);
		formPanel.add(phoneField, gbc);

		// Nút Lưu
		gbc.gridx = 1;
		gbc.gridy = 4;
		var saveButton = createStyledButton("Lưu");
		saveButton.addActionListener(e -> updateProfile());
		formPanel.add(saveButton, gbc);

		// Hiển thị ảnh đại diện (placeholder)
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.gridheight = 3;
		var avatarLabel = new JLabel(createAvatarIcon());
		avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		formPanel.add(avatarLabel, gbc);

		// Chọn ảnh
		gbc.gridy = 5;
		var chooseImageButton = new JButton("Chọn Ảnh");
		chooseImageButton.setBackground(Color.WHITE);
		chooseImageButton.setForeground(BUTTON_COLOR);
		chooseImageButton.setBorder(BorderFactory.createLineBorder(BUTTON_COLOR));
		chooseImageButton.setFocusPainted(false);
		chooseImageButton.addActionListener(e -> chooseImage());
		formPanel.add(chooseImageButton, gbc);

		mainPanel.add(formPanel, BorderLayout.CENTER);
		add(mainPanel, BorderLayout.CENTER);

		loadProfile();
	}

	private void addSidebarItem(JPanel panel, String icon, String text, java.awt.event.ActionListener action) {
		var button = new JButton("<html>" + icon + " " + text + "</html>");
		button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		button.setBackground(SIDEBAR_COLOR);
		button.setForeground(Color.BLACK);
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(new Color(220, 220, 220));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(SIDEBAR_COLOR);
			}
		});
		if (action != null) {
			button.addActionListener(action);
		}
		panel.add(button);
	}

	private JButton createStyledButton(String text) {
		var button = new JButton(text) {
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
		button.setBackground(BUTTON_COLOR);
		button.setForeground(Color.WHITE);
		button.setFont(new Font("Segoe UI", Font.BOLD, 14));
		button.setFocusPainted(false);
		button.setBorder(new EmptyBorder(8, 15, 8, 15));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(BUTTON_COLOR.brighter());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(BUTTON_COLOR);
			}
		});
		return button;
	}

	private ImageIcon createAvatarIcon() {
		var imgURL = getClass().getResource("/images/avatar.png");
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		}

		imgURL = getClass().getResource("/images/default_avatar.png");
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		}

		System.err.println(
				"Không tìm thấy file avatar.png hoặc default_avatar.png trong /images/. Sử dụng hình ảnh mặc định.");
		var defaultImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		var g2d = defaultImg.createGraphics();
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(0, 0, 100, 100);
		g2d.setColor(Color.DARK_GRAY);
		g2d.drawString("No Image", 30, 50);
		g2d.dispose();
		return new ImageIcon(defaultImg);
	}

	private void chooseImage() {
		JOptionPane.showMessageDialog(this, "Chức năng chọn ảnh chưa được triển khai!");
	}

	private void loadProfile() {
		var info = customerService.getCustomerInfo(maKH);
		if (info != null && info.length >= 3) {
			nameField.setText((String) info[0]); // TenKH
			phoneField.setText((String) info[1]); // SoDienThoai
		} else {
			JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin khách hàng!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateProfile() {
		var name = nameField.getText().trim();
		var phone = phoneField.getText().trim();

		// Kiểm tra các trường bắt buộc
		if (name.isEmpty() || phone.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (Tên và Số điện thoại)!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Kiểm tra định dạng số điện thoại
		if (!isValidPhone(phone)) {
			JOptionPane.showMessageDialog(this,
					"Số điện thoại không hợp lệ! Vui lòng nhập đúng định dạng (ví dụ: 0909123456).", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Lấy địa chỉ mặc định
		var address = getDefaultAddress();
		if (address == null) {
			JOptionPane.showMessageDialog(this, "Không thể lấy địa chỉ mặc định! Vui lòng kiểm tra lại.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Gọi phương thức cập nhật thông tin
		if (customerService.updateCustomerInfo(maKH, name, phone, address, address, address, address)) {
			JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thành công",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thông tin! Vui lòng thử lại.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private boolean isValidPhone(String phone) {
		var phoneRegex = "^0\\d{9}$";
		return phone != null && phone.matches(phoneRegex);
	}

	private String getDefaultAddress() {
		try {
			var query = "SELECT DiaChi FROM DIACHI_KH WHERE MaKH = ? AND LaMacDinh = 1";
			var stmt = customerService.getConnection().prepareStatement(query);
			stmt.setString(1, maKH);
			var rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString("DiaChi");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void updateMaKH(String newMaKH) {
		this.maKH = newMaKH;
		customerService.setMaKH(newMaKH);
		loadProfile();
	}
}