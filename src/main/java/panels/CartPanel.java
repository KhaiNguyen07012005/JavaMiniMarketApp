package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import service.CustomerService;
import util.Constants;

public class CartPanel extends JPanel {
	private JTable cartTable;
	private DefaultTableModel tableModel;
	private JLabel totalLabel;
	private CustomerService customerService;
	private String maKH;
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
	private static final String DEFAULT_IMAGE_PATH = "/images/no-image.png";

	public CartPanel(CustomerService customerService, String maKH) {
		this.customerService = customerService;
		this.maKH = maKH;
		customerService.setMaKH(maKH);
		setLayout(new BorderLayout());
		initializeComponents();
	}

	private void initializeComponents() {
		tableModel = new DefaultTableModel(
				new String[] { "Mã SP", "Tên SP", "Số Lượng", "Đơn Giá", "Thành Tiền", "Hình Ảnh" }, 0) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return columnIndex == 5 ? ImageIcon.class : Object.class;
			}
		};
		cartTable = new JTable(tableModel);
		cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		cartTable.setRowHeight(60);
		cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
		cartTable.getTableHeader().setBackground(new Color(230, 240, 250));
		cartTable.getTableHeader().setForeground(Constants.BUTTON_COLOR);
		cartTable.setGridColor(new Color(206, 212, 218));
		cartTable.setShowGrid(true);
		cartTable.getColumnModel().getColumn(5).setCellRenderer(new ImageCellRenderer());
		cartTable.getColumnModel().getColumn(5).setPreferredWidth(100);
		var scrollPane = new JScrollPane(cartTable);
		add(scrollPane, BorderLayout.CENTER);

		var buttonPanel = new JPanel(new FlowLayout());
		var addButton = createStyledButton("Thêm từ Sản Phẩm");
		addButton.addActionListener(e -> addToCart());
		buttonPanel.add(addButton);

		var removeButton = createStyledButton("Xóa");
		removeButton.addActionListener(e -> removeFromCart());
		buttonPanel.add(removeButton);

		var checkoutButton = createStyledButton("Thanh Toán");
		checkoutButton.addActionListener(e -> checkout());
		buttonPanel.add(checkoutButton);

		add(buttonPanel, BorderLayout.SOUTH);

		totalLabel = new JLabel("Tổng tiền: 0 VNĐ");
		totalLabel.setForeground(Constants.BUTTON_COLOR);
		add(totalLabel, BorderLayout.NORTH);

		loadCart();
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

	private void addToCart() {
		var maSP = JOptionPane.showInputDialog(this, "Nhập Mã SP:");
		if (maSP != null && !maSP.trim().isEmpty()) {
			var quantityStr = JOptionPane.showInputDialog(this, "Nhập số lượng:");
			try {
				var quantity = Integer.parseInt(quantityStr);
				if (customerService.addToCart(maSP, quantity)) {
					loadCart();
				} else {
					JOptionPane.showMessageDialog(this, "Không đủ số lượng hoặc sản phẩm không tồn tại!", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void removeFromCart() {
		var selectedRow = cartTable.getSelectedRow();
		if (selectedRow >= 0) {
			var maSP = (String) tableModel.getValueAt(selectedRow, 0);
			customerService.removeFromCart(maSP);
			loadCart();
		} else {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void checkout() {
		String[] options = { "Tiền mặt", "Thẻ" };
		var paymentMethod = (String) JOptionPane.showInputDialog(this, "Chọn phương thức thanh toán:", "Thanh Toán",
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (paymentMethod != null) {
			var result = customerService.checkout(maKH, paymentMethod);
			JOptionPane.showMessageDialog(this, result);
			loadCart();
		}
	}

	public void loadCart() {
		tableModel.setRowCount(0);
		var total = 0D;
		for (Object[] item : customerService.getCart()) {
			var imageIcon = loadIcon((String) item[5]);
			tableModel.addRow(new Object[] { item[0], item[1], item[2], DECIMAL_FORMAT.format(item[3]),
					DECIMAL_FORMAT.format(item[4]), imageIcon });
			total += (double) item[4];
		}
		var discount = customerService.getDiscount();
		total -= discount;
		totalLabel.setText("Tổng tiền: " + DECIMAL_FORMAT.format(total) + " VNĐ"
				+ (discount > 0 ? " (Giảm giá: " + DECIMAL_FORMAT.format(discount) + " VNĐ)" : ""));
	}

	public void updateMaKH(String newMaKH) {
		this.maKH = newMaKH;
		customerService.setMaKH(newMaKH);
		loadCart();
	}

	private ImageIcon loadIcon(String path) {
		System.out.println("Attempting to load image: path=" + path);
		try {
			if (path != null && !path.trim().isEmpty()) {

				var resourcePath = "/images/products/" + path.replaceAll("^/+", "");
				var imgURL = getClass().getResource(resourcePath);
				if (imgURL != null) {
					System.out.println("Found as resource: " + resourcePath);
					var icon = new ImageIcon(imgURL);
					if (icon.getImage() != null) {
						var image = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
						return new ImageIcon(image);
					}
					System.err.println("Image data is null for resource: " + resourcePath);
				} else {
					System.out.println("Not found as resource: " + resourcePath);
				}

				var filePath = path;
				// If path is filename only, assume images/products/ directory
				if (!path.contains("/") && !path.contains("\\")) {
					filePath = "images/products/" + path;
				}
				filePath = filePath.replace("\\", "/");
				var imageFile = new File(filePath);
				System.out.println("Trying file path: " + imageFile.getAbsolutePath());
				System.out.println("File exists: " + imageFile.exists());
				System.out.println("File readable: " + imageFile.canRead());
				if (imageFile.exists() && imageFile.canRead()) {
					var icon = new ImageIcon(imageFile.getAbsolutePath());
					if (icon.getImage() != null) {
						System.out.println("Image loaded from file, scaling to 50x50");
						var image = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
						return new ImageIcon(image);
					}
					System.err.println("Image data is null for file: " + imageFile.getAbsolutePath());
				} else {
					System.err.println("File does not exist or is not readable: " + imageFile.getAbsolutePath());
				}

				imageFile = new File(path);
				System.out.println("Trying original path: " + imageFile.getAbsolutePath());
				System.out.println("File exists: " + imageFile.exists());
				System.out.println("File readable: " + imageFile.canRead());
				if (imageFile.exists() && imageFile.canRead()) {
					var icon = new ImageIcon(imageFile.getAbsolutePath());
					if (icon.getImage() != null) {
						System.out.println("Image loaded from original path, scaling to 50x50");
						var image = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
						return new ImageIcon(image);
					}
					System.err.println("Image data is null for original path: " + imageFile.getAbsolutePath());
				} else {
					System.err
							.println("Original path does not exist or is not readable: " + imageFile.getAbsolutePath());
				}
			} else {
				System.err.println("Image path is null or empty");
			}

			System.out.println("Attempting to load default image: " + DEFAULT_IMAGE_PATH);
			var defaultImgURL = getClass().getResource(DEFAULT_IMAGE_PATH);
			if (defaultImgURL != null) {
				var defaultIcon = new ImageIcon(defaultImgURL);
				if (defaultIcon.getImage() != null) {
					System.out.println("Default image loaded, scaling to 50x50");
					var image = defaultIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
					return new ImageIcon(image);
				}
				System.err.println("Default image data is null: " + DEFAULT_IMAGE_PATH);
			} else {
				System.err.println("Default image not found: " + DEFAULT_IMAGE_PATH);
			}
			System.out.println("Returning null, renderer will show 'No Image'");
			return null; // Renderer will display "No Image"
		} catch (Exception e) {
			System.err.println("Error loading image: " + path + " - " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	static class ImageCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			var label = new JLabel();
			if (value instanceof ImageIcon && ((ImageIcon) value).getImage() != null) {
				label.setIcon((ImageIcon) value);
				label.setHorizontalAlignment(JLabel.CENTER);
			} else {
				label.setText("No Image");
				label.setHorizontalAlignment(JLabel.CENTER);
			}
			label.setOpaque(true);
			if (isSelected) {
				label.setBackground(table.getSelectionBackground());
				label.setForeground(table.getSelectionForeground());
			} else {
				label.setBackground(table.getBackground());
				label.setForeground(table.getForeground());
			}
			return label;
		}
	}
}