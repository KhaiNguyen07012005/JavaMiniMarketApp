package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import connect.ConnectDB;
import panels.AccountPanel;
import panels.CustomerPanel;
import panels.DashboardPanel;
import panels.EmployeePanel;
import panels.HistoryPanel;
import panels.InvoicePanel;
import panels.ProductPanel;
import panels.PromotionPanel;
import panels.ReportPanel;
import panels.SalesPanel;
import panels.SettingsPanel;

public class MainFrame extends JFrame {
	private String tenDangNhap;
	private JPanel mainPanel;
	private boolean isAdmin = false;

	public MainFrame(String tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
		checkRole();
		setTitle("🛒 Hệ Thống Quản Lý Cửa Hàng - Xin chào: " + tenDangNhap);
		setSize(1100, 720);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		initMenu();
		initSidebar();
		initMainPanel();
		// Show Dashboard by default
		showPanel("Tổng quan");
	}

	private void checkRole() {
		try (var conn = ConnectDB.getCon()) {
			var sql = "SELECT VaiTro FROM TAIKHOAN WHERE TenDangNhap = ?";
			try (var ps = conn.prepareStatement(sql)) {
				ps.setString(1, tenDangNhap);
				var rs = ps.executeQuery();
				if (rs.next()) {
					isAdmin = rs.getString("VaiTro").equals("Admin");
				} else {
					JOptionPane.showMessageDialog(this, "Tài khoản không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
					dispose();
					new LoginFrame().setVisible(true);
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi kiểm tra vai trò: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			dispose();
			new LoginFrame().setVisible(true);
		}
	}

	private void initMenu() {
		var menuBar = new JMenuBar();
		var menuSystem = new JMenu("Hệ thống");

		var itemLogout = new JMenuItem("Đăng xuất");
		var itemExit = new JMenuItem("Thoát");

		itemLogout.addActionListener(e -> {
			dispose();
			new LoginFrame().setVisible(true);
		});

		itemExit.addActionListener(e -> System.exit(0));

		menuSystem.add(itemLogout);
		menuSystem.addSeparator();
		menuSystem.add(itemExit);

		menuBar.add(menuSystem);
		setJMenuBar(menuBar);
	}

	private void initSidebar() {
		var sidebar = new JPanel(new BorderLayout());
		sidebar.setPreferredSize(new Dimension(240, getHeight()));
		sidebar.setBackground(new Color(245, 248, 255));

		var sidebarWidth = 200;
		var imgHeight = 150;

		var rawIcon = new ImageIcon(getClass().getResource("/images/grocery_image.jpg"));
		var scaledImage = rawIcon.getImage().getScaledInstance(sidebarWidth, imgHeight, Image.SCALE_SMOOTH);
		var icon = new ImageIcon(scaledImage);

		var imgLabel = new JLabel(icon);
		imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
		imgLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		sidebar.add(imgLabel, BorderLayout.NORTH);

		var buttonPanel = new JPanel(new GridLayout(0, 1, 0, 4));
		buttonPanel.setBackground(new Color(245, 248, 255));
		buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		var menuItems = new String[][] { { "Tổng quan", "Tổng quan hệ thống" }, { "Bán hàng", "Giao dịch bán hàng" },
				{ "Sản phẩm", "Quản lý sản phẩm" }, { "Khách hàng", "Thông tin khách hàng" },
				{ "Nhân viên", "Quản lý nhân viên" }, { "Tài khoản", "Thông tin tài khoản" },
				{ "Hóa đơn", "Hóa đơn bán hàng" }, { "Thống kê", "Xem báo cáo" },
				{ "Khuyến mãi", "Chương trình khuyến mãi" }, { "Lịch sử", "Nhật ký hoạt động" },
				{ "Cài đặt", "Cài đặt hệ thống" } // Thêm mục Cài đặt
		};

		for (String[] item : menuItems) {
			// Chỉ hiển thị nút "Cài đặt" cho Admin
//			if (item[0].equals("Cài đặt") && !isAdmin) {
//				continue;
//			}
			var btn = createSidebarButton(item[0], item[1]);
			buttonPanel.add(btn);
		}

		var scrollPane = new JScrollPane(buttonPanel);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		sidebar.add(scrollPane, BorderLayout.CENTER);

		add(sidebar, BorderLayout.WEST);
	}

	private JButton createSidebarButton(String text, String tooltip) {
		var button = new JButton(text);
		button.setFocusPainted(false);
		button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setIconTextGap(10);
		button.setBackground(Color.WHITE);
		button.setForeground(new Color(33, 37, 41));
		button.setToolTipText(tooltip);
		button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(new Color(230, 240, 255));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(Color.WHITE);
			}
		});

		button.addActionListener(e -> showPanel(text));
		return button;
	}

	private void initMainPanel() {
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(Color.WHITE);
		add(mainPanel, BorderLayout.CENTER);
	}

	private void showPanel(String feature) {
		var panelToShow = switch (feature) {
		case "Tổng quan" -> new DashboardPanel();
		case "Bán hàng" -> new SalesPanel();
		case "Sản phẩm" -> new ProductPanel();
		case "Khách hàng" -> new CustomerPanel();
		case "Nhân viên" -> new EmployeePanel();
		case "Tài khoản" -> new AccountPanel();
		case "Hóa đơn" -> new InvoicePanel();
		case "Thống kê" -> new ReportPanel();
		case "Khuyến mãi" -> new PromotionPanel();
		case "Lịch sử" -> new HistoryPanel();
		case "Cài đặt" -> {
			if (isAdmin) {
				yield new SettingsPanel();
			} else {
				JOptionPane.showMessageDialog(this, "Bạn không có quyền truy cập cài đặt!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				yield new JPanel();
			}
		}
		default -> {
			JOptionPane.showMessageDialog(this, "Tính năng '" + feature + "' đang được phát triển!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			yield new JPanel();
		}
		};

		mainPanel.removeAll();
		mainPanel.add(panelToShow, BorderLayout.CENTER);
		mainPanel.revalidate();
		mainPanel.repaint();
	}
}