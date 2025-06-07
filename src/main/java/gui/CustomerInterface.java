package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import connect.ConnectDB;
import panels.CartPanel;
import panels.InvoicePanelUser;
import panels.LoginPanel;
import panels.LoyaltyPanel;
import panels.ProductPanelUser;
import panels.ProfilePanel;
import panels.SupportPanel;
import service.CustomerService;

public class CustomerInterface extends JFrame {
	private JTabbedPane tabbedPane;
	private ConnectDB dbConnection;
	private CustomerService customerService;
	private CartPanel cartPanel;
	private ProductPanelUser productPanel;
	private InvoicePanelUser invoicePanel;
	private LoyaltyPanel loyaltyPanel;
	private ProfilePanel profilePanel;
	private SupportPanel supportPanel;
	private LoginPanel loginPanel;
	private String maKH = "KH0001";
	private boolean isMaximized = false;

	private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
	private static final Color SECONDARY_COLOR = new Color(244, 67, 54);
	private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
	private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
	private static final Color CARD_COLOR = Color.WHITE;
	private static final Color TEXT_COLOR = new Color(33, 37, 41);
	private static final Color BORDER_COLOR = new Color(206, 212, 218);
	private static final Color TAB_SELECTED_COLOR = new Color(144, 202, 249);
	private static final Color TAB_HOVER_COLOR = new Color(200, 220, 240);

	public CustomerInterface() {
		// Kết nối cơ sở dữ liệu
		dbConnection = new ConnectDB();
		if (dbConnection.getCon() == null) {
			JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		customerService = new CustomerService(dbConnection.getCon());

		setTitle("Grocery Store - Khách Hàng");
		setSize(900, 650);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setUndecorated(true);

		JPanel mainPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				var gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), BACKGROUND_COLOR);
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
				g2d.setColor(new Color(0, 0, 0, 50));
				g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
			}
		};
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

		JPanel headerPanel = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				var gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), new Color(100, 181, 246));
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
			}
		};
		headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		var titleLabel = new JLabel("Grocery Store - Khách Hàng", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
		titleLabel.setForeground(Color.WHITE);

		var controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
		controlPanel.setOpaque(false);

		var minimizeButton = createControlButton("●", new Color(255, 193, 7));
		minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED));
		controlPanel.add(minimizeButton);

		var maximizeButton = createControlButton("●", new Color(76, 175, 80));
		maximizeButton.addActionListener(e -> {
			if (isMaximized) {
				setExtendedState(JFrame.NORMAL);
			} else {
				setExtendedState(JFrame.MAXIMIZED_BOTH);
			}
			isMaximized = !isMaximized;
		});
		controlPanel.add(maximizeButton);

		var closeButton = createControlButton("●", SECONDARY_COLOR);
		closeButton.addActionListener(e -> System.exit(0));
		controlPanel.add(closeButton);

		var point = new Point();
		headerPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				point.setLocation(e.getPoint());
			}
		});
		headerPanel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				var newPoint = e.getLocationOnScreen();
				setLocation(newPoint.x - point.x, newPoint.y - point.y);
			}
		});

		headerPanel.add(titleLabel, BorderLayout.CENTER);
		headerPanel.add(controlPanel, BorderLayout.EAST);
		mainPanel.add(headerPanel, BorderLayout.NORTH);

		JPanel contentPanel = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(CARD_COLOR);
				g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
				g2d.setColor(new Color(0, 0, 0, 50));
				g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
			}
		};
		contentPanel.setBackground(BACKGROUND_COLOR);
		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		tabbedPane = new JTabbedPane() {
			@Override
			protected void paintComponent(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(CARD_COLOR);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
				super.paintComponent(g);
			}
		};
		tabbedPane.setUI(new BasicTabbedPaneUI() {
			@Override
			protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
					boolean isSelected) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (isSelected) {
					g2d.setColor(TAB_SELECTED_COLOR);
				} else {
					g2d.setColor(tabIndex == tabPane.getTabCount() - 1 ? TAB_HOVER_COLOR : CARD_COLOR);
				}
				g2d.fillRoundRect(x, y, w - 1, h - 1, 15, 15);
				g2d.setColor(BORDER_COLOR);
				g2d.drawRoundRect(x, y, w - 1, h - 1, 15, 15);
			}

			@Override
			protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
					boolean isSelected) {
				// Không vẽ viền
			}

			@Override
			protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
				// Không vẽ viền nội dung
			}

			@Override
			protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex,
					String title, Rectangle textRect, boolean isSelected) {
				g.setFont(new Font("Segoe UI", Font.BOLD, 15));
				g.setColor(isSelected ? PRIMARY_COLOR : TEXT_COLOR);
				var x = textRect.x + (tabbedPane.getIconAt(tabIndex) != null ? 30 : 0);
				var y = textRect.y + metrics.getAscent();
				g.drawString(title, x, y);
			}

			protected int calculateTabHeight(int tabPlacement, int tabIndex, FontMetrics metrics) {
				return 40;
			}
		});

		tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
		tabbedPane.setForeground(TEXT_COLOR);
		tabbedPane.setOpaque(false);

		cartPanel = new CartPanel(customerService, maKH);
		productPanel = new ProductPanelUser(customerService, cartPanel);
		invoicePanel = new InvoicePanelUser(customerService, maKH);
		loyaltyPanel = new LoyaltyPanel(customerService, maKH);
		profilePanel = new ProfilePanel(customerService, maKH);
		supportPanel = new SupportPanel(customerService, maKH);
		loginPanel = new LoginPanel(customerService);

		// Bỏ icon để tránh lỗi
		tabbedPane.addTab("Đăng Nhập/Đăng Ký", null, loginPanel);
		tabbedPane.addTab("Sản Phẩm", null, productPanel);
		tabbedPane.addTab("Giỏ Hàng", null, cartPanel);
		tabbedPane.addTab("Hóa Đơn", null, invoicePanel);
		tabbedPane.addTab("Tích Điểm & Ưu Đãi", null, loyaltyPanel);
		tabbedPane.addTab("Thông Tin Cá Nhân", null, profilePanel);
		tabbedPane.addTab("Hỗ Trợ", null, supportPanel);

		tabbedPane.addMouseMotionListener(new MouseAdapter() {
			int lastTab = -1;

			@Override
			public void mouseMoved(MouseEvent e) {
				var tabIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
				if (tabIndex != lastTab) {
					if (lastTab != -1 && lastTab != tabbedPane.getSelectedIndex()) {
						tabbedPane.setBackgroundAt(lastTab, CARD_COLOR);
					}
					if (tabIndex != -1 && tabIndex != tabbedPane.getSelectedIndex()) {
						tabbedPane.setBackgroundAt(tabIndex, TAB_HOVER_COLOR);
					}
					lastTab = tabIndex;
				}
			}
		});

		tabbedPane.addChangeListener(e -> {
			if (tabbedPane.getSelectedComponent() != loginPanel) {
				var newMaKH = loginPanel.getMaKH();
				if (newMaKH != null && !newMaKH.equals(maKH)) {
					updateMaKH(newMaKH);
				}
			}
		});

		contentPanel.add(tabbedPane, BorderLayout.CENTER);
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		add(mainPanel);
		setVisible(true);
	}

	private JButton createControlButton(String text, Color baseColor) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(getBackground());
				g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
				super.paintComponent(g);
			}

			@Override
			protected void paintBorder(Graphics g) {
				// Không vẽ viền
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(14, 14);
			}
		};
		button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		button.setForeground(Color.WHITE);
		button.setBackground(baseColor);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		button.setBorder(new EmptyBorder(0, 0, 0, 0));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(baseColor.darker());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(baseColor);
			}
		});
		return button;
	}

	private void updateMaKH(String newMaKH) {
		this.maKH = newMaKH;
		cartPanel.updateMaKH(newMaKH);
		invoicePanel.updateMaKH(newMaKH);
		loyaltyPanel.updateMaKH(newMaKH);
		profilePanel.updateMaKH(newMaKH);
		supportPanel.updateMaKH(newMaKH);
		JOptionPane.showMessageDialog(this, "Đăng nhập thành công! Mã KH: " + newMaKH, "Thành công",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new CustomerInterface());
	}
}