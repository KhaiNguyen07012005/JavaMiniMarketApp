package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import service.CustomerService;

public class InvoicePanelUser extends JPanel {
	private JTable invoiceTable, detailTable;
	private DefaultTableModel invoiceModel, detailModel;
	private CustomerService customerService;
	private String maKH;
	private JLabel statusLabel;
	private static final Color HEADER_COLOR = new Color(0, 120, 215); // Blue from the title bar
	private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Light blue background
	private static final Color TABLE_HEADER_COLOR = new Color(200, 220, 240); // Light Blue
	private static final Color ALT_ROW_COLOR = new Color(240, 242, 245); // Very Light Gray
	private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,###.0");

	public InvoicePanelUser(CustomerService customerService, String maKH) {
		this.customerService = customerService;
		this.maKH = maKH;
		customerService.setMaKH(maKH);
		setLayout(new BorderLayout());
		setBackground(BACKGROUND_COLOR);
		initializeComponents();
	}

	private void initializeComponents() {
		// Header Panel
		var headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		headerPanel.setBackground(HEADER_COLOR);
		headerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
		var titleLabel = new JLabel("Grocery Store - Khách Hàng");
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
		headerPanel.add(titleLabel);
		add(headerPanel, BorderLayout.NORTH);

		// Menu Panel (Simulated horizontal menu)
		var menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		menuPanel.setBackground(BACKGROUND_COLOR);
		menuPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
		String[] menuItems = { "Đăng Nhập/Đăng Ký", "Sản Phẩm", "Giỏ Hàng", "Hóa Đơn", "Tích Điểm & Ưu Đãi",
				"Thông Tin Của Nhân", "Hỗ Trợ" };
		for (String item : menuItems) {
			var menuButton = new JButton(item);
			menuButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			menuButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			menuButton.setContentAreaFilled(false);
			menuButton.setFocusPainted(false);
			menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			menuButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					menuButton.setForeground(HEADER_COLOR);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					menuButton.setForeground(Color.BLACK);
				}
			});
			menuPanel.add(menuButton);
		}
		add(menuPanel, BorderLayout.CENTER);

		// Invoice Filter
		var filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		filterPanel.setBackground(BACKGROUND_COLOR);
		filterPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
		var filterLabel = new JLabel("Chọn hóa đơn để xem chi tiết:");
		filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		var filterButton = new JButton("Tất cả");
		filterButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		filterButton.setBackground(Color.WHITE);
		filterButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		filterButton.setFocusPainted(false);
		filterPanel.add(filterLabel);
		filterPanel.add(filterButton);
		add(filterPanel, BorderLayout.NORTH);

		// Invoice Table
		invoiceModel = new DefaultTableModel(new String[] { "Mã HD", "Ngày Lập", "Tổng Tiền" }, 0);
		invoiceTable = new JTable(invoiceModel) {
			@Override
			public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row,
					int column) {
				var c = super.prepareRenderer(renderer, row, column);
				c.setBackground(row % 2 == 0 ? Color.WHITE : ALT_ROW_COLOR);
				if (isRowSelected(row)) {
					c.setBackground(HEADER_COLOR.brighter());
				}
				return c;
			}
		};
		invoiceTable.setRowHeight(30);
		invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		invoiceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		invoiceTable.getTableHeader().setBackground(TABLE_HEADER_COLOR);
		invoiceTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		invoiceTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Mã HD
		invoiceTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Ngày Lập
		invoiceTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Tổng Tiền

		var centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		for (var i = 0; i < invoiceTable.getColumnCount(); i++) {
			invoiceTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		var invoiceScrollPane = new JScrollPane(invoiceTable);
		invoiceScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		// Detail Table
		detailModel = new DefaultTableModel(new String[] { "Mã SP", "Tên SP", "Số Lượng", "Đơn Giá" }, 0);
		detailTable = new JTable(detailModel) {
			@Override
			public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row,
					int column) {
				var c = super.prepareRenderer(renderer, row, column);
				c.setBackground(row % 2 == 0 ? Color.WHITE : ALT_ROW_COLOR);
				return c;
			}
		};
		detailTable.setRowHeight(30);
		detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		detailTable.getTableHeader().setBackground(TABLE_HEADER_COLOR);
		detailTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Mã SP
		detailTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Tên SP
		detailTable.getColumnModel().getColumn(2).setPreferredWidth(80); // Số Lượng
		detailTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Đơn Giá
		for (var i = 0; i < detailTable.getColumnCount(); i++) {
			detailTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		var detailScrollPane = new JScrollPane(detailTable);
		detailScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		// Split Pane for Tables
		var splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, invoiceScrollPane, detailScrollPane);
		splitPane.setDividerLocation(200);
		splitPane.setResizeWeight(0.5);
		splitPane.setBorder(BorderFactory.createEmptyBorder());
		add(splitPane, BorderLayout.CENTER);

		// Button Panel
		var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		buttonPanel.setBackground(BACKGROUND_COLOR);
		buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		var printButton = createStyledButton("In Hóa Đơn", 'P');
		printButton.setToolTipText("In hóa đơn thành file PDF");
		printButton.addActionListener(e -> printInvoice());

		var viewButton = createStyledButton("Xem Chi Tiết", 'V');
		viewButton.setToolTipText("Xem chi tiết hóa đơn được chọn");
		viewButton.addActionListener(e -> viewInvoiceDetails());

		var refreshButton = createStyledButton("Làm Mới", 'R');
		refreshButton.setToolTipText("Tải lại danh sách hóa đơn");
		var refreshIcon = loadIcon("/icons/refresh.png");
		if (refreshIcon != null) {
			refreshButton.setIcon(refreshIcon);
		}
		refreshButton.addActionListener(e -> {
			loadInvoices();
			detailModel.setRowCount(0);
			statusLabel.setText("Danh sách hóa đơn đã được làm mới.");
		});

		buttonPanel.add(printButton);
		buttonPanel.add(viewButton);
		buttonPanel.add(refreshButton);
		add(buttonPanel, BorderLayout.SOUTH);

		// Status Label
		statusLabel = new JLabel("Chọn hóa đơn để xem chi tiết.");
		statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		statusLabel.setForeground(Color.DARK_GRAY);
		var statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		statusPanel.setBackground(BACKGROUND_COLOR);
		statusPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
		statusPanel.add(statusLabel);
		add(statusPanel, BorderLayout.NORTH);

		loadInvoices();
	}

	private JButton createStyledButton(String text, char mnemonic) {
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
		button.setBackground(HEADER_COLOR);
		button.setForeground(Color.WHITE);
		button.setFont(new Font("Segoe UI", Font.BOLD, 14));
		button.setFocusPainted(false);
		button.setBorder(new EmptyBorder(8, 15, 8, 15));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setMnemonic(mnemonic);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(HEADER_COLOR.brighter());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(HEADER_COLOR);
			}
		});
		return button;
	}

	private ImageIcon loadIcon(String path) {
		try {
			var imgURL = getClass().getResource(path);
			if (imgURL != null) {
				var icon = new ImageIcon(imgURL);
				if (icon.getImage() != null) {
					var image = icon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH);
					return new ImageIcon(image);
				}
			}
			System.err.println("Icon không tìm thấy: " + path);
			return null;
		} catch (Exception e) {
			System.err.println("Lỗi tải icon: " + path + " - " + e.getMessage());
			return null;
		}
	}

	private void loadInvoices() {
		invoiceModel.setRowCount(0);
		for (Object[] invoice : customerService.getInvoices(maKH)) {
			if (invoice[2] != null) {
				invoice[2] = PRICE_FORMAT.format(Double.parseDouble(invoice[2].toString())) + " VNĐ";
			}
			invoiceModel.addRow(invoice);
		}
	}

	private void loadInvoiceDetails(String maHD) {
		detailModel.setRowCount(0);
		var details = customerService.getInvoiceDetails(maHD);
		System.out.println("Số dòng chi tiết cho hóa đơn " + maHD + ": " + details.size());
		for (Object[] detail : details) {
			if (detail[3] != null) {
				detail[3] = PRICE_FORMAT.format(Double.parseDouble(detail[3].toString())) + " VNĐ";
			}
			System.out.println("Dữ liệu chi tiết: Mã SP=" + detail[0] + ", Tên SP=" + detail[1] + ", Số Lượng="
					+ detail[2] + ", Đơn Giá=" + detail[3]);
			detailModel.addRow(detail);
		}
		System.out.println("Số dòng trong detailModel sau khi thêm: " + detailModel.getRowCount());
	}

	private void viewInvoiceDetails() {
		var selectedRow = invoiceTable.getSelectedRow();
		if (selectedRow < 0) {
			statusLabel.setText("Vui lòng chọn một hóa đơn.");
			JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xem chi tiết!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		var maHD = (String) invoiceModel.getValueAt(selectedRow, 0);
		loadInvoiceDetails(maHD);
		statusLabel.setText("Chi tiết hóa đơn " + maHD + " đã được tải.");
	}

	private void printInvoice() {
		var selectedRow = invoiceTable.getSelectedRow();
		if (selectedRow < 0) {
			statusLabel.setText("Vui lòng chọn một hóa đơn.");
			JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để in!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		var maHD = (String) invoiceModel.getValueAt(selectedRow, 0);
		loadInvoiceDetails(maHD);

		var fileChooser = new JFileChooser();
		fileChooser.setSelectedFile(new File("HoaDon_" + maHD + ".pdf"));
		if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		var outputFile = fileChooser.getSelectedFile();
		try {
			var parentDir = outputFile.getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}

			try (var testStream = new java.io.FileOutputStream(outputFile, true)) {
				testStream.close();
			} catch (IOException e) {
				throw new IOException("File " + outputFile.getAbsolutePath()
						+ " đang được sử dụng bởi một tiến trình khác. Vui lòng đóng file và thử lại.");
			}

			var writer = new PdfWriter(outputFile);
			var pdf = new PdfDocument(writer);
			var document = new Document(pdf);

			PdfFont font;
			try {
				font = PdfFontFactory.createFont("src/main/resources/fonts/DejaVuSans.ttf", PdfEncodings.IDENTITY_H);
			} catch (IOException e) {
				System.out.println("Không tìm thấy font DejaVuSans, dùng font mặc định...");
				font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
			}

			var storeInfo = new Paragraph("CỬA HÀNG BÁN LẺ VINMART").setFont(font).setFontSize(14).setBold()
					.setTextAlignment(TextAlignment.CENTER);
			document.add(storeInfo);
			document.add(new Paragraph("Địa chỉ: 123 Đường Lê Lợi, Quận 1, TP. HCM").setFont(font).setFontSize(10)
					.setTextAlignment(TextAlignment.CENTER));
			document.add(new Paragraph("SĐT: 0909 123 456").setFont(font).setFontSize(10)
					.setTextAlignment(TextAlignment.CENTER));
			document.add(new Paragraph("\n"));

			var title = new Paragraph("HÓA ĐƠN BÁN HÀNG").setFont(font).setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.CENTER);
			document.add(title);

			document.add(new Paragraph("Mã Hóa Đơn: " + maHD).setFont(font).setFontSize(12));
			document.add(new Paragraph("Ngày Lập: " + invoiceModel.getValueAt(selectedRow, 1)).setFont(font)
					.setFontSize(12));
			var totalAmount = invoiceModel.getValueAt(selectedRow, 2).toString();
			document.add(new Paragraph("Tổng Tiền: " + totalAmount).setFont(font).setFontSize(12));
			document.add(new Paragraph("\n"));

			var table = new Table(new float[] { 15f, 40f, 15f, 20f });
			table.setWidth(com.itextpdf.kernel.geom.PageSize.A4.getWidth() - 50);
			table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Mã SP").setFont(font))
					.setBackgroundColor(ColorConstants.LIGHT_GRAY));
			table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Tên SP").setFont(font))
					.setBackgroundColor(ColorConstants.LIGHT_GRAY));
			table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Số Lượng").setFont(font))
					.setBackgroundColor(ColorConstants.LIGHT_GRAY));
			table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Đơn Giá").setFont(font))
					.setBackgroundColor(ColorConstants.LIGHT_GRAY));

			if (detailModel.getRowCount() == 0) {
				document.add(new Paragraph("Không có dữ liệu chi tiết cho hóa đơn này!").setFont(font).setFontSize(10)
						.setItalic());
				System.out.println("Không có dữ liệu chi tiết trong detailModel cho hóa đơn " + maHD);
			} else {
				for (var i = 0; i < detailModel.getRowCount(); i++) {
					var maSP = detailModel.getValueAt(i, 0);
					var tenSP = detailModel.getValueAt(i, 1);
					var soLuong = detailModel.getValueAt(i, 2);
					var donGia = detailModel.getValueAt(i, 3);
					System.out.println("Hiển thị dòng " + i + " trong PDF: Mã SP=" + maSP + ", Tên SP=" + tenSP
							+ ", Số Lượng=" + soLuong + ", Đơn Giá=" + donGia);
					table.addCell(new com.itextpdf.layout.element.Cell()
							.add(new Paragraph(maSP != null ? maSP.toString() : "").setFont(font)));
					table.addCell(new com.itextpdf.layout.element.Cell()
							.add(new Paragraph(tenSP != null ? tenSP.toString() : "").setFont(font)));
					table.addCell(new com.itextpdf.layout.element.Cell()
							.add(new Paragraph(soLuong != null ? soLuong.toString() : "").setFont(font)));
					table.addCell(new com.itextpdf.layout.element.Cell()
							.add(new Paragraph(donGia != null ? donGia.toString() : "").setFont(font)));
				}
			}
			document.add(table);

			document.add(new Paragraph("\n"));
			document.add(new Paragraph("Cảm ơn quý khách đã mua hàng!").setFont(font).setFontSize(10)
					.setTextAlignment(TextAlignment.CENTER));

			document.close();
			statusLabel.setText("Hóa đơn " + maHD + " đã được in thành công.");
			JOptionPane.showMessageDialog(this,
					"In hóa đơn thành công! File được lưu tại: " + outputFile.getAbsolutePath());
		} catch (IOException e) {
			statusLabel.setText("Lỗi khi in hóa đơn.");
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi khi in hóa đơn: " + e.getMessage() + "\nChi tiết: " + e.getCause(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			statusLabel.setText("Lỗi không xác định khi in hóa đơn.");
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void updateMaKH(String newMaKH) {
		this.maKH = newMaKH;
		customerService.setMaKH(newMaKH);
		loadInvoices();
		detailModel.setRowCount(0);
		statusLabel.setText("Đã cập nhật khách hàng: " + newMaKH);
	}
}