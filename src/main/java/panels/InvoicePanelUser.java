package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
import util.Constants;

public class InvoicePanelUser extends JPanel {
	private JTable invoiceTable, detailTable;
	private DefaultTableModel invoiceModel, detailModel;
	private CustomerService customerService;
	private String maKH;

	public InvoicePanelUser(CustomerService customerService, String maKH) {
		this.customerService = customerService;
		this.maKH = maKH;
		customerService.setMaKH(maKH);
		setLayout(new BorderLayout());
		initializeComponents();
	}

	private void initializeComponents() {
		invoiceModel = new DefaultTableModel(new String[] { "Mã HD", "Ngày Lập", "Tổng Tiền" }, 0);
		invoiceTable = new JTable(invoiceModel);
		invoiceTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					var selectedRow = invoiceTable.getSelectedRow();
					if (selectedRow >= 0) {
						var maHD = (String) invoiceModel.getValueAt(selectedRow, 0);
						loadInvoiceDetails(maHD);
					}
				}
			}
		});
		var invoiceScrollPane = new JScrollPane(invoiceTable);
		add(invoiceScrollPane, BorderLayout.CENTER);

		detailModel = new DefaultTableModel(new String[] { "Mã SP", "Tên SP", "Số Lượng", "Đơn Giá" }, 0);
		detailTable = new JTable(detailModel);
		var detailScrollPane = new JScrollPane(detailTable);
		add(detailScrollPane, BorderLayout.SOUTH);

		var printButton = createStyledButton("In Hóa Đơn");
		printButton.addActionListener(e -> printInvoice());
		var buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(printButton);
		add(buttonPanel, BorderLayout.NORTH);

		loadInvoices();
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

	private void loadInvoices() {
		invoiceModel.setRowCount(0);
		for (Object[] invoice : customerService.getInvoices(maKH)) {
			invoiceModel.addRow(invoice);
		}
	}

	private void loadInvoiceDetails(String maHD) {
		detailModel.setRowCount(0);
		var details = customerService.getInvoiceDetails(maHD);
		System.out.println("Số dòng chi tiết cho hóa đơn " + maHD + ": " + details.size());
		for (Object[] detail : details) {
			System.out.println("Dữ liệu chi tiết: Mã SP=" + detail[0] + ", Tên SP=" + detail[1] + ", Số Lượng="
					+ detail[2] + ", Đơn Giá=" + detail[3]);
			detailModel.addRow(detail);
		}
		System.out.println("Số dòng trong detailModel sau khi thêm: " + detailModel.getRowCount());
	}

	private void printInvoice() {
		var selectedRow = invoiceTable.getSelectedRow();
		if (selectedRow < 0) {
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
				// Dùng IDENTITY_H để hỗ trợ tiếng Việt, iTextPDF sẽ tự động nhúng font
				font = PdfFontFactory.createFont("src/main/resources/fonts/DejaVuSans.ttf", PdfEncodings.IDENTITY_H);
			} catch (IOException e) {
				System.out.println("Không tìm thấy font DejaVuSans, dùng font mặc định...");
				font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
			}

			// Thông tin cửa hàng
			var storeInfo = new Paragraph("CỬA HÀNG BÁN LẺ ABC").setFont(font).setFontSize(14).setBold()
					.setTextAlignment(TextAlignment.CENTER);
			document.add(storeInfo);
			document.add(new Paragraph("Địa chỉ: 123 Đường Lê Lợi, Quận 1, TP. HCM").setFont(font).setFontSize(10)
					.setTextAlignment(TextAlignment.CENTER));
			document.add(new Paragraph("SĐT: 0909 123 456").setFont(font).setFontSize(10)
					.setTextAlignment(TextAlignment.CENTER));
			document.add(new Paragraph("\n"));

			// Tiêu đề hóa đơn
			var title = new Paragraph("HÓA ĐƠN BÁN HÀNG").setFont(font).setFontSize(16).setBold()
					.setTextAlignment(TextAlignment.CENTER);
			document.add(title);

			// Thông tin hóa đơn
			document.add(new Paragraph("Mã Hóa Đơn: " + maHD).setFont(font).setFontSize(12));
			document.add(new Paragraph("Ngày Lập: " + invoiceModel.getValueAt(selectedRow, 1)).setFont(font)
					.setFontSize(12));

			var df = new DecimalFormat("#,###.0");
			var totalAmount = df.format(Double.parseDouble(String.valueOf(invoiceModel.getValueAt(selectedRow, 2))))
					+ " VNĐ";
			document.add(new Paragraph("Tổng Tiền: " + totalAmount).setFont(font).setFontSize(12));
			document.add(new Paragraph("\n"));

			// Tạo bảng chi tiết hóa đơn
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

			// Lời cảm ơn
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("Cảm ơn quý khách đã mua hàng!").setFont(font).setFontSize(10)
					.setTextAlignment(TextAlignment.CENTER));

			document.close();
			JOptionPane.showMessageDialog(this,
					"In hóa đơn thành công! File được lưu tại: " + outputFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi khi in hóa đơn: " + e.getMessage() + "\nChi tiết: " + e.getCause(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
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
	}
}