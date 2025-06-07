package panels;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import connect.ConnectDB;

public class ReportPanel extends JPanel {
	private static final DecimalFormat df = new DecimalFormat("#,##0.00");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final Color PRIMARY_COLOR = new Color(0, 128, 128); // Teal
	private static final Color SECONDARY_COLOR = new Color(77, 182, 172); // Light Teal
	private static final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Off-white
	private static final Color TEXT_COLOR = new Color(45, 55, 72); // Dark Gray
	private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 24);
	private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
	private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
	private float opacity = 0f;
	private JComboBox<String> reportTypeCombo;
	private JComboBox<String> periodCombo;
	private JTable reportTable;
	private DefaultTableModel tableModel;
	private JPanel chartPanel;

	public ReportPanel() {
		setLayout(new BorderLayout());
		setBackground(BACKGROUND_COLOR);
		setOpaque(false);

		// Start fade-in animation
		startFadeInAnimation();

		// Header
		var headerPanel = new JPanel();
		headerPanel.setBackground(PRIMARY_COLOR);
		headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		var headerLabel = new JLabel("Báo Cáo Thống Kê", JLabel.CENTER);
		headerLabel.setFont(HEADER_FONT);
		headerLabel.setForeground(Color.WHITE);
		headerPanel.add(headerLabel);
		add(headerPanel, BorderLayout.NORTH);

		// Control Panel
		var controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		controlPanel.setBackground(BACKGROUND_COLOR);
		reportTypeCombo = new JComboBox<>(new String[] { "Doanh Thu", "Sản Phẩm", "Khách Hàng" });
		periodCombo = new JComboBox<>(new String[] { "Ngày", "Tuần", "Tháng", "Năm" });
		var refreshButton = new JButton("Tải Báo Cáo");
		var exportButton = new JButton("Xuất Excel");
		styleButton(refreshButton);
		styleButton(exportButton);
		controlPanel.add(new JLabel("Loại báo cáo:"));
		controlPanel.add(reportTypeCombo);
		controlPanel.add(new JLabel("Kỳ:"));
		controlPanel.add(periodCombo);
		controlPanel.add(refreshButton);
		controlPanel.add(exportButton);

		// Main Content
		var contentPanel = new JPanel(new BorderLayout(10, 10));
		contentPanel.setBackground(BACKGROUND_COLOR);
		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Table
		tableModel = new DefaultTableModel();
		reportTable = new JTable(tableModel);
		reportTable.setFillsViewportHeight(true);
		reportTable.setBackground(Color.WHITE);
		reportTable.setForeground(TEXT_COLOR);
		reportTable.setFont(LABEL_FONT);
		reportTable.getTableHeader().setFont(TITLE_FONT);
		var tableScrollPane = new JScrollPane(reportTable);
		tableScrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));

		// Chart
		chartPanel = new JPanel(new BorderLayout());
		chartPanel.setBackground(BACKGROUND_COLOR);
		chartPanel.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));

		contentPanel.add(tableScrollPane, BorderLayout.CENTER);
		contentPanel.add(chartPanel, BorderLayout.EAST);

		add(controlPanel, BorderLayout.PAGE_START);
		add(contentPanel, BorderLayout.CENTER);

		// Event Listeners
		refreshButton.addActionListener(e -> loadReport());
		exportButton.addActionListener(e -> exportToExcel());
		reportTypeCombo.addActionListener(e -> updatePeriodOptions());
		loadReport(); // Initial load
	}

	private void startFadeInAnimation() {
		var timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				opacity += 0.05f;
				if (opacity >= 1f) {
					opacity = 1f;
					timer.cancel();
				}
				repaint();
			}
		}, 0, 50);
	}

	@Override
	protected void paintComponent(Graphics g) {
		var g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g2d);
		g2d.dispose();
	}

	private void styleButton(JButton button) {
		button.setBackground(SECONDARY_COLOR);
		button.setForeground(Color.WHITE);
		button.setFont(LABEL_FONT);
		button.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		button.setFocusPainted(false);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(PRIMARY_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(SECONDARY_COLOR);
			}
		});
	}

	private void updatePeriodOptions() {
		var reportType = (String) reportTypeCombo.getSelectedItem();
		if (reportType.equals("Sản Phẩm") || reportType.equals("Khách Hàng")) {
			periodCombo.setModel(new DefaultComboBoxModel<>(new String[] { "Tháng", "Năm" }));
		} else {
			periodCombo.setModel(new DefaultComboBoxModel<>(new String[] { "Ngày", "Tuần", "Tháng", "Năm" }));
		}
	}

	private void loadReport() {
		var reportType = (String) reportTypeCombo.getSelectedItem();
		var period = (String) periodCombo.getSelectedItem();
		try {
			var conn = ConnectDB.getCon();
			switch (reportType) {
			case "Doanh Thu":
				loadRevenueReport(conn, period);
				break;
			case "Sản Phẩm":
				loadProductReport(conn, period);
				break;
			case "Khách Hàng":
				loadCustomerReport(conn, period);
				break;
			}
			conn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi tải báo cáo: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadRevenueReport(java.sql.Connection conn, String period) throws SQLException {
		String sqlCurrent;
		String sqlPrevious;
		String[] columns = { "Kỳ", "Doanh Thu (triệu VND)" };
		tableModel.setColumnIdentifiers(columns);
		tableModel.setRowCount(0);
		var dataset = new DefaultCategoryDataset();

		switch (period) {
		case "Ngày":
			sqlCurrent = "SELECT CAST(NgayLap AS DATE) as Ky, SUM(TongTien) as DoanhThu "
					+ "FROM HOADON WHERE CAST(NgayLap AS DATE) = CAST(GETDATE() AS DATE) GROUP BY CAST(NgayLap AS DATE)";
			sqlPrevious = "SELECT CAST(NgayLap AS DATE) as Ky, SUM(TongTien) as DoanhThu "
					+ "FROM HOADON WHERE CAST(NgayLap AS DATE) = DATEADD(day, -1, CAST(GETDATE() AS DATE)) GROUP BY CAST(NgayLap AS DATE)";
			break;
		case "Tuần":
			sqlCurrent = "SELECT DATEPART(week, NgayLap) as Ky, SUM(TongTien) as DoanhThu "
					+ "FROM HOADON WHERE DATEPART(week, NgayLap) = DATEPART(week, GETDATE()) AND YEAR(NgayLap) = YEAR(GETDATE()) GROUP BY DATEPART(week, NgayLap)";
			sqlPrevious = "SELECT DATEPART(week, NgayLap) as Ky, SUM(TongTien) as DoanhThu "
					+ "FROM HOADON WHERE DATEPART(week, NgayLap) = DATEPART(week, DATEADD(week, -1, GETDATE())) AND YEAR(NgayLap) = YEAR(GETDATE()) GROUP BY DATEPART(week, NgayLap)";
			break;
		case "Tháng":
			sqlCurrent = "SELECT MONTH(NgayLap) as Ky, SUM(TongTien) as DoanhThu "
					+ "FROM HOADON WHERE MONTH(NgayLap) = MONTH(GETDATE()) AND YEAR(NgayLap) = YEAR(GETDATE()) GROUP BY MONTH(NgayLap)";
			sqlPrevious = "SELECT MONTH(NgayLap) as Ky, SUM(TongTien) as DoanhThu "
					+ "FROM HOADON WHERE MONTH(NgayLap) = MONTH(DATEADD(month, -1, GETDATE())) AND YEAR(NgayLap) = YEAR(DATEADD(month, -1, GETDATE())) GROUP BY MONTH(NgayLap)";
			break;
		case "Năm":
			sqlCurrent = "SELECT YEAR(NgayLap) as Ky, SUM(TongTien) as DoanhThu "
					+ "FROM HOADON WHERE YEAR(NgayLap) = YEAR(GETDATE()) GROUP BY YEAR(NgayLap)";
			sqlPrevious = "SELECT YEAR(NgayLap) as Ky, SUM(TongTien) as DoanhThu "
					+ "FROM HOADON WHERE YEAR(NgayLap) = YEAR(DATEADD(year, -1, GETDATE())) GROUP BY YEAR(NgayLap)";
			break;
		default:
			return;
		}

		// Current Period
		var ps = conn.prepareStatement(sqlCurrent);
		var rs = ps.executeQuery();
		try {
			if (rs.next()) {
				var ky = period.equals("Ngày") ? dateFormat.format(rs.getDate("Ky")) : rs.getString("Ky");
				var revenue = rs.getDouble("DoanhThu") / 1_000_000;
				tableModel.addRow(new Object[] { ky, df.format(revenue) });
				dataset.addValue(revenue, "Kỳ Hiện Tại", ky);
			}
		} finally {
			rs.close();
			ps.close();
		}

		// Previous Period
		ps = conn.prepareStatement(sqlPrevious);
		rs = ps.executeQuery();
		try {
			if (rs.next()) {
				var ky = period.equals("Ngày") ? dateFormat.format(rs.getDate("Ky")) : "Kỳ Trước";
				var revenue = rs.getDouble("DoanhThu") / 1_000_000;
				tableModel.addRow(new Object[] { ky, df.format(revenue) });
				dataset.addValue(revenue, "Kỳ Trước", ky);
			}
		} finally {
			rs.close();
			ps.close();
		}

		// Chart
		var chart = ChartFactory.createBarChart("Doanh Thu " + period, "Kỳ", "Doanh thu (triệu VND)", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		styleChart(chart);
		updateChartPanel(chart);
	}

	private void loadProductReport(java.sql.Connection conn, String period) throws SQLException {
		String sqlBestSelling;
		String sqlStale;
		if (period.equals("Tháng")) {
			sqlBestSelling = """
					SELECT TOP 5 sp.TenSP, SUM(ct.SoLuong) as TotalSold \
					FROM CHITIETHOADON ct JOIN SANPHAM sp ON ct.MaSP = sp.MaSP \
					JOIN HOADON hd ON ct.MaHD = hd.MaHD \
					WHERE MONTH(hd.NgayLap) = MONTH(GETDATE()) AND YEAR(hd.NgayLap) = YEAR(GETDATE()) \
					GROUP BY sp.MaSP, sp.TenSP ORDER BY TotalSold DESC""";
			sqlStale = """
					SELECT sp.TenSP, sp.SoLuongTon \
					FROM SANPHAM sp LEFT JOIN CHITIETHOADON ct ON sp.MaSP = ct.MaSP \
					LEFT JOIN HOADON hd ON ct.MaHD = hd.MaHD AND hd.NgayLap >= DATEADD(day, -30, GETDATE()) \
					WHERE hd.MaHD IS NULL AND sp.SoLuongTon > 0""";
		} else {
			sqlBestSelling = """
					SELECT TOP 5 sp.TenSP, SUM(ct.SoLuong) as TotalSold \
					FROM CHITIETHOADON ct JOIN SANPHAM sp ON ct.MaSP = sp.MaSP \
					JOIN HOADON hd ON ct.MaHD = hd.MaHD \
					WHERE YEAR(hd.NgayLap) = YEAR(GETDATE()) \
					GROUP BY sp.MaSP, sp.TenSP ORDER BY TotalSold DESC""";
			sqlStale = """
					SELECT sp.TenSP, sp.SoLuongTon \
					FROM SANPHAM sp LEFT JOIN CHITIETHOADON ct ON sp.MaSP = ct.MaSP \
					LEFT JOIN HOADON hd ON ct.MaHD = hd.MaHD AND hd.NgayLap >= DATEADD(day, -90, GETDATE()) \
					WHERE hd.MaHD IS NULL AND sp.SoLuongTon > 0""";
		}

		// Best-Selling Products
		String[] columns = { "Sản Phẩm", "Số Lượng Bán" };
		tableModel.setColumnIdentifiers(columns);
		tableModel.setRowCount(0);
		var dataset = new DefaultPieDataset();
		var ps = conn.prepareStatement(sqlBestSelling);
		var rs = ps.executeQuery();
		try {
			while (rs.next()) {
				var product = rs.getString("TenSP");
				var quantity = rs.getInt("TotalSold");
				tableModel.addRow(new Object[] { product, quantity });
				dataset.setValue(product, quantity);
			}
		} finally {
			rs.close();
			ps.close();
		}

		// Stale Products
		ps = conn.prepareStatement(sqlStale);
		rs = ps.executeQuery();
		try {
			if (tableModel.getColumnCount() < 3) {
				tableModel.setColumnIdentifiers(new String[] { "Sản Phẩm", "Tồn Kho" });
			}
			while (rs.next()) {
				tableModel.addRow(new Object[] { rs.getString("TenSP"), rs.getInt("SoLuongTon") });
			}
		} finally {
			rs.close();
			ps.close();
		}

		// Chart
		var chart = ChartFactory.createPieChart("Top 5 Sản Phẩm Bán Chạy " + period, dataset, true, true, false);
		stylePieChart(chart);
		updateChartPanel(chart);
	}

	private void loadCustomerReport(java.sql.Connection conn, String period) throws SQLException {
		String sqlTopSpenders;
		String sqlFrequency;
		if (period.equals("Tháng")) {
			sqlTopSpenders = """
					SELECT TOP 5 kh.TenKH, SUM(hd.TongTien) as TotalSpent \
					FROM KHACHHANG kh JOIN HOADON hd ON kh.MaKH = hd.MaKH \
					WHERE MONTH(hd.NgayLap) = MONTH(GETDATE()) AND YEAR(hd.NgayLap) = YEAR(GETDATE()) \
					GROUP BY kh.MaKH, kh.TenKH ORDER BY TotalSpent DESC""";
			sqlFrequency = """
					SELECT kh.TenKH, COUNT(hd.MaHD) as PurchaseCount \
					FROM KHACHHANG kh JOIN HOADON hd ON kh.MaKH = hd.MaKH \
					WHERE MONTH(hd.NgayLap) = MONTH(GETDATE()) AND YEAR(hd.NgayLap) = YEAR(GETDATE()) \
					GROUP BY kh.MaKH, kh.TenKH ORDER BY PurchaseCount DESC""";
		} else {
			sqlTopSpenders = """
					SELECT TOP 5 kh.TenKH, SUM(hd.TongTien) as TotalSpent \
					FROM KHACHHANG kh JOIN HOADON hd ON kh.MaKH = hd.MaKH \
					WHERE YEAR(hd.NgayLap) = YEAR(GETDATE()) \
					GROUP BY kh.MaKH, kh.TenKH ORDER BY TotalSpent DESC""";
			sqlFrequency = """
					SELECT kh.TenKH, COUNT(hd.MaHD) as PurchaseCount \
					FROM KHACHHANG kh JOIN HOADON hd ON kh.MaKH = hd.MaKH \
					WHERE YEAR(hd.NgayLap) = YEAR(GETDATE()) \
					GROUP BY kh.MaKH, kh.TenKH ORDER BY PurchaseCount DESC""";
		}

		// Top Spenders
		String[] columns = { "Khách Hàng", "Chi tiêu (triệu VND)" };
		tableModel.setColumnIdentifiers(columns);
		tableModel.setRowCount(0);
		var dataset = new DefaultCategoryDataset();
		var ps = conn.prepareStatement(sqlTopSpenders);
		var rs = ps.executeQuery();
		try {
			while (rs.next()) {
				var customer = rs.getString("TenKH");
				var spent = rs.getDouble("TotalSpent") / 1_000_000;
				tableModel.addRow(new Object[] { customer, df.format(spent) });
				dataset.addValue(spent, "Chi tiêu", customer);
			}
		} finally {
			rs.close();
			ps.close();
		}

		// Purchase Frequency
		ps = conn.prepareStatement(sqlFrequency);
		rs = ps.executeQuery();
		try {
			if (tableModel.getColumnCount() < 3) {
				tableModel.setColumnIdentifiers(new String[] { "Khách Hàng", "Số Lần Mua", "Tần Suất" });
			}
			while (rs.next()) {
				tableModel.addRow(new Object[] { rs.getString("TenKH"), rs.getInt("PurchaseCount"),
						rs.getInt("PurchaseCount") + " lần" });
			}
		} finally {
			rs.close();
			ps.close();
		}

		// Chart
		var chart = ChartFactory.createBarChart("Top 5 Khách Hàng Chi Tiêu " + period, "Khách hàng",
				"Chi tiêu (triệu VND)", dataset, PlotOrientation.VERTICAL, true, true, false);
		styleChart(chart);
		updateChartPanel(chart);
	}

	private void styleChart(JFreeChart chart) {
		var plot = chart.getCategoryPlot();
		var gp = new GradientPaint(0, 0, new Color(220, 240, 255), 0, 300, BACKGROUND_COLOR);
		plot.setBackgroundPaint(gp);
		plot.setRangeGridlinePaint(new Color(180, 180, 180));
		plot.setDomainGridlinePaint(new Color(180, 180, 180));
		var renderer = (BarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, PRIMARY_COLOR);
		renderer.setMaximumBarWidth(0.1);
		chart.getTitle().setFont(TITLE_FONT);
		plot.getDomainAxis().setTickLabelFont(LABEL_FONT);
		plot.getRangeAxis().setTickLabelFont(LABEL_FONT);
	}

	private void stylePieChart(JFreeChart chart) {
		var plot = (PiePlot) chart.getPlot();
		plot.setBackgroundPaint(BACKGROUND_COLOR);
		plot.setLabelFont(LABEL_FONT);
		plot.setSectionPaint(0, PRIMARY_COLOR);
		plot.setSectionPaint(1, SECONDARY_COLOR);
		plot.setSectionPaint(2, new Color(46, 204, 113));
		plot.setSectionPaint(3, new Color(231, 76, 60));
		plot.setSectionPaint(4, new Color(155, 89, 182));
		chart.getTitle().setFont(TITLE_FONT);
	}

	private void updateChartPanel(JFreeChart chart) {
		chartPanel.removeAll();
		var cp = new ChartPanel(chart);
		cp.setPreferredSize(new Dimension(400, 300));
		chartPanel.add(cp, BorderLayout.CENTER);
		chartPanel.revalidate();
		chartPanel.repaint();
	}

	private void exportToExcel() {
		try {
			Workbook workbook = new XSSFWorkbook();
			try {
				var sheet = workbook.createSheet("Report");
				var headerRow = sheet.createRow(0);
				var headerStyle = workbook.createCellStyle();
				var headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerStyle.setFont(headerFont);

				// Write header
				for (var i = 0; i < tableModel.getColumnCount(); i++) {
					var cell = headerRow.createCell(i);
					cell.setCellValue(tableModel.getColumnName(i));
					cell.setCellStyle(headerStyle);
				}

				// Write data rows
				for (var i = 0; i < tableModel.getRowCount(); i++) {
					var row = sheet.createRow(i + 1);
					for (var j = 0; j < tableModel.getColumnCount(); j++) {
						var cell = row.createCell(j);
						var value = tableModel.getValueAt(i, j);
						cell.setCellValue(value != null ? value.toString() : "");
					}
				}

				// Auto-size columns
				for (var i = 0; i < tableModel.getColumnCount(); i++) {
					sheet.autoSizeColumn(i);
				}

				// Write to file
				var fos = new FileOutputStream("report.xlsx");
				try {
					workbook.write(fos);
					JOptionPane.showMessageDialog(this, "Xuất file Excel thành công: report.xlsx", "Thành công",
							JOptionPane.INFORMATION_MESSAGE);
				} finally {
					fos.close();
				}
			} finally {
				workbook.close();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}
}