package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import service.CustomerService;

public class ProductPanelUser extends JPanel {
	private JTextField searchField;
	private JComboBox<String> categoryFilter, priceFilter;
	private CustomerService customerService;
	private CartPanel cartPanel;
	private JPanel productsContainer;

	// Define the same modern color palette as ProductPanel
	private static final Color PRIMARY_COLOR = new Color(33, 150, 243); // Blue
	private static final Color SECONDARY_COLOR = new Color(244, 67, 54); // Red
	private static final Color SUCCESS_COLOR = new Color(76, 175, 80); // Green
	private static final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Light Gray
	private static final Color CARD_COLOR = Color.WHITE; // White
	private static final Color TEXT_COLOR = new Color(33, 37, 41); // Dark Gray
	private static final Color BORDER_COLOR = new Color(206, 212, 218); // Gray
	private static final Color TAB_SELECTED_COLOR = new Color(144, 202, 249); // Light Blue
	private static final Color TAB_HOVER_COLOR = new Color(200, 220, 240); // Lighter Blue
	private static final String DEFAULT_IMAGE_PATH = "/images/no-image.png"; // Default image in resources

	public ProductPanelUser(CustomerService customerService, CartPanel cartPanel) {
		this.customerService = customerService;
		this.cartPanel = cartPanel;
		setLayout(new BorderLayout(15, 15));
		setBackground(BACKGROUND_COLOR);
		setBorder(new EmptyBorder(15, 15, 15, 15));
		initializeComponents();
	}

	private void initializeComponents() {
		// Title panel with gradient
		JPanel titlePanel = new JPanel() {
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
		titlePanel.setLayout(new BorderLayout());
		titlePanel.setBorder(new EmptyBorder(15, 0, 15, 0));

		var title = new JLabel("Danh Sách Sản Phẩm", SwingConstants.CENTER);
		title.setFont(new Font("Segoe UI", Font.BOLD, 28));
		title.setForeground(Color.WHITE);
		titlePanel.add(title, BorderLayout.CENTER);
		add(titlePanel, BorderLayout.NORTH);

		// Filter panel with card effect
		JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)) {
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
		filterPanel.setBackground(BACKGROUND_COLOR);
		filterPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				BorderFactory.createEmptyBorder(10, 15, 10, 15)));

		var searchLabel = new JLabel("Tìm kiếm:");
		searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		searchLabel.setForeground(TEXT_COLOR);

		searchField = new JTextField(25);
		searchField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		searchField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		searchField.addActionListener(e -> loadProducts(searchField.getText(),
				(String) categoryFilter.getSelectedItem(), (String) priceFilter.getSelectedItem()));

		filterPanel.add(searchLabel);
		filterPanel.add(searchField);

		var categoryLabel = new JLabel("Danh mục:");
		categoryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		categoryLabel.setForeground(TEXT_COLOR);

		categoryFilter = new JComboBox<>(
				new String[] { "Tất cả", "Thực phẩm", "Đồ uống", "Gia dụng", "Hóa mỹ phẩm", "Đồ chơi" });
		categoryFilter.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		categoryFilter.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
		categoryFilter.addActionListener(e -> loadProducts(searchField.getText(),
				(String) categoryFilter.getSelectedItem(), (String) priceFilter.getSelectedItem()));
		filterPanel.add(categoryLabel);
		filterPanel.add(categoryFilter);

		var priceLabel = new JLabel("Giá:");
		priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		priceLabel.setForeground(TEXT_COLOR);

		priceFilter = new JComboBox<>(new String[] { "Tất cả", "< 100,000", "100,000 - 500,000", "> 500,000" });
		priceFilter.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		priceFilter.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
		priceFilter.addActionListener(e -> loadProducts(searchField.getText(),
				(String) categoryFilter.getSelectedItem(), (String) priceFilter.getSelectedItem()));
		filterPanel.add(priceLabel);
		filterPanel.add(priceFilter);

		add(filterPanel, BorderLayout.PAGE_START);

		// Products container with FlowLayout
		productsContainer = new JPanel();
		productsContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
		productsContainer.setBackground(BACKGROUND_COLOR);

		var scrollPane = new JScrollPane(productsContainer);
		scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
		scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
		add(scrollPane, BorderLayout.CENTER);

		loadProducts("", "Tất cả", "Tất cả");
	}

	private void loadProducts(String search, String category, String priceRange) {
		productsContainer.removeAll(); // Xóa các sản phẩm hiện tại

		// Xây dựng truy vấn cơ bản
		var query = new StringBuilder("""
				SELECT sp.MaSP, sp.TenSP, sp.DonGia, sp.SoLuongTon, sp.ImagePath \
				FROM SANPHAM sp \
				LEFT JOIN LOAISP lsp ON sp.MaLoai = lsp.MaLoai \
				WHERE sp.TenSP LIKE ?""");

		// Thêm điều kiện lọc theo danh mục nếu không phải "Tất cả"
		if (!category.equals("Tất cả")) {
			query.append(" AND lsp.TenLoai = ?");
		}

		// Thêm điều kiện lọc theo giá nếu không phải "Tất cả"
		if (!priceRange.equals("Tất cả")) {
			query.append(" AND sp.DonGia ").append(getPriceCondition(priceRange));
		}

		try (var stmt = customerService.getConnection().prepareStatement(query.toString())) {
			// Gán tham số cho truy vấn
			stmt.setString(1, "%" + search + "%");
			var paramIndex = 2;
			if (!category.equals("Tất cả")) {
				stmt.setString(paramIndex++, category);
			}

			var rs = stmt.executeQuery();
			var hasProducts = false;
			while (rs.next()) {
				hasProducts = true;
				var maSP = rs.getString("MaSP");
				var tenSP = rs.getString("TenSP");
				var donGia = rs.getDouble("DonGia");
				var soLuongTon = rs.getInt("SoLuongTon");
				var imagePath = rs.getString("ImagePath");

				// Load the image
				var imageIcon = loadImage(imagePath);

				// Tạo ProductCard và thêm vào container
				var productCard = new ProductCard(maSP, tenSP, donGia, soLuongTon, imageIcon);
				productsContainer.add(productCard);
			}

			// Nếu không có sản phẩm nào, hiển thị thông báo
			if (!hasProducts) {
				JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm phù hợp!", "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi tải sản phẩm: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}

		productsContainer.revalidate();
		productsContainer.repaint();
	}

	private ImageIcon loadImage(String imagePath) {
		try {
			// Nếu imagePath null hoặc rỗng, trả về ảnh mặc định
			if (imagePath == null || imagePath.trim().isEmpty()) {
				return getDefaultImage();
			}

			// Giả sử imagePath là đường dẫn tương đối trong thư mục dự án
			// (images/products/)
			var imageFile = new File("images/products/" + imagePath);
			if (imageFile.exists()) {
				var imageIcon = new ImageIcon(imageFile.getAbsolutePath());
				var image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
				return new ImageIcon(image);
			}
			System.err.println("Image file does not exist: " + imageFile.getAbsolutePath());
			return getDefaultImage();
		} catch (Exception e) {
			System.err.println("Error loading image: " + imagePath);
			e.printStackTrace();
			return getDefaultImage();
		}
	}

	private ImageIcon getDefaultImage() {
		try {
			// Tải ảnh mặc định từ resources
			var icon = new ImageIcon(getClass().getResource(DEFAULT_IMAGE_PATH));
			var image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
			return new ImageIcon(image);
		} catch (Exception e) {
			System.err.println("Error loading default image: " + DEFAULT_IMAGE_PATH);
			return new ImageIcon(); // Trả về ImageIcon trống nếu lỗi
		}
	}

	private String getPriceCondition(String priceRange) {
		return switch (priceRange) {
		case "< 100,000" -> "< 100000";
		case "100,000 - 500,000" -> "BETWEEN 100000 AND 500000";
		case "> 500,000" -> "> 500000";
		default -> "";
		};
	}

	// ProductCard class to represent each product
	private class ProductCard extends JPanel {
		private String maSP;
		private int soLuongTon;

		public ProductCard(String maSP, String tenSP, double donGia, int soLuongTon, ImageIcon imageIcon) {
			this.maSP = maSP;
			this.soLuongTon = soLuongTon;

			setLayout(new BorderLayout(5, 5));
			setBackground(CARD_COLOR);
			setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
					BorderFactory.createEmptyBorder(10, 10, 10, 10)));
			setPreferredSize(new Dimension(200, 300));

			// Image
			var imageLabel = new JLabel(imageIcon);
			imageLabel.setHorizontalAlignment(JLabel.CENTER);
			add(imageLabel, BorderLayout.CENTER);

			// Product info panel
			var infoPanel = new JPanel();
			infoPanel.setLayout(new BorderLayout());
			infoPanel.setBackground(CARD_COLOR);

			var nameLabel = new JLabel(tenSP);
			nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			nameLabel.setForeground(TEXT_COLOR);
			nameLabel.setHorizontalAlignment(JLabel.CENTER);
			infoPanel.add(nameLabel, BorderLayout.NORTH);

			var priceLabel = new JLabel(String.format("%,.0fđ", donGia));
			priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
			priceLabel.setForeground(SECONDARY_COLOR);
			priceLabel.setHorizontalAlignment(JLabel.CENTER);
			infoPanel.add(priceLabel, BorderLayout.CENTER);

			var buyButton = createStyledButton("MUA", SUCCESS_COLOR);
			buyButton.addActionListener(e -> handleAddToCart(maSP, tenSP));
			infoPanel.add(buyButton, BorderLayout.SOUTH);

			add(infoPanel, BorderLayout.SOUTH);
		}

		private void handleAddToCart(String maSP, String tenSP) {
			var input = JOptionPane.showInputDialog(ProductPanelUser.this, "Nhập số lượng muốn thêm cho " + tenSP + ":",
					"1");
			if (input == null || input.trim().isEmpty()) {
				return;
			}

			try {
				var soLuong = Integer.parseInt(input);
				if (soLuong <= 0) {
					JOptionPane.showMessageDialog(ProductPanelUser.this, "Số lượng phải lớn hơn 0!", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (soLuong > soLuongTon) {
					JOptionPane.showMessageDialog(ProductPanelUser.this,
							"Số lượng tồn kho không đủ! Chỉ còn: " + soLuongTon, "Lỗi", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (customerService.addToCart(maSP, soLuong)) {
					JOptionPane.showMessageDialog(ProductPanelUser.this,
							"Đã thêm " + soLuong + " " + tenSP + " vào giỏ hàng!", "Thành công",
							JOptionPane.INFORMATION_MESSAGE);
					if (cartPanel != null) {
						cartPanel.loadCart();
					}
					loadProducts(searchField.getText(), (String) categoryFilter.getSelectedItem(),
							(String) priceFilter.getSelectedItem());
				} else {
					JOptionPane.showMessageDialog(ProductPanelUser.this,
							"Không thể thêm vào giỏ hàng! Sản phẩm không tồn tại hoặc lỗi.", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(ProductPanelUser.this, "Số lượng phải là số nguyên hợp lệ!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private JButton createStyledButton(String text, Color baseColor) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(getBackground());
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				super.paintComponent(g);
			}

			@Override
			protected void paintBorder(Graphics g) {
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(new Color(0, 0, 0, 50));
				g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
			}
		};
		button.setFont(new Font("Segoe UI", Font.BOLD, 15));
		button.setBackground(baseColor);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorder(new EmptyBorder(8, 15, 8, 15));
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
}