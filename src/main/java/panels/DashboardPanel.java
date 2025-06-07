package panels;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import connect.ConnectDB;

public class DashboardPanel extends JPanel {
	private static final Logger LOGGER = Logger.getLogger(DashboardPanel.class.getName());
	private static final DecimalFormat df = new DecimalFormat("#,##0.00");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final Color PRIMARY_COLOR = new Color(0, 128, 128);
	private static final Color SECONDARY_COLOR = new Color(77, 182, 172);
	private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
	private static final Color TEXT_COLOR = new Color(45, 55, 72);
	private static final Color CARD_TEXT_COLOR = Color.WHITE;
	private static final Font HEADER_FONT = new Font("Roboto", Font.BOLD, 24);
	private static final Font LABEL_FONT = new Font("Roboto", Font.PLAIN, 14);
	private static final Font TITLE_FONT = new Font("Roboto", Font.BOLD, 16);
	private float opacity = 0f;

	public DashboardPanel() {
		setLayout(new BorderLayout());
		setBackground(BACKGROUND_COLOR);
		setOpaque(false);

		// Start fade-in animation
		startFadeInAnimation();

		// Header
		var headerPanel = new JPanel();
		headerPanel.setBackground(PRIMARY_COLOR);
		headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		var headerLabel = new JLabel("Tổng Quan Hệ Thống", JLabel.CENTER);
		headerLabel.setFont(HEADER_FONT);
		headerLabel.setForeground(Color.WHITE);
		headerPanel.add(headerLabel);
		add(headerPanel, BorderLayout.NORTH);

		// Main content
		var contentPanel = new JPanel(new GridBagLayout());
		contentPanel.setBackground(BACKGROUND_COLOR);
		contentPanel.setOpaque(false);
		var gbc = new GridBagConstraints();
		gbc.insets = new Insets(15, 15, 15, 15);
		gbc.fill = GridBagConstraints.BOTH;

		// Metrics Panel
		var metricsPanel = createMetricsPanel();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.4;
		gbc.weighty = 0.5;
		contentPanel.add(metricsPanel, gbc);

		// Revenue Chart
		var chartPanel = createRevenueChart();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.6;
		contentPanel.add(chartPanel, gbc);

		// Notifications Panel
		var notificationsPanel = createNotificationsPanel();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weighty = 0.3;
		contentPanel.add(notificationsPanel, gbc);

		add(contentPanel, BorderLayout.CENTER);
	}

	private void startFadeInAnimation() {
		var timer = new Timer(50, e -> {
			opacity += 0.05f;
			if (opacity >= 1f) {
				opacity = 1f;
				((Timer) e.getSource()).stop();
			}
			repaint();
		});
		timer.start();
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

	private JPanel createMetricsPanel() {
		var panel = new JPanel(new GridLayout(3, 2, 15, 15));
		panel.setBackground(BACKGROUND_COLOR);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TEXT_COLOR), "Chỉ Số Chính",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				TITLE_FONT, TEXT_COLOR));

		try (var conn = ConnectDB.getCon()) {
			panel.add(createMetricCard("Doanh thu hôm nay:", getDailyRevenue(conn), "💰", new Color(46, 204, 113),
					() -> showRevenueDetails(conn, "daily")));
			panel.add(createMetricCard("Doanh thu tháng:", getMonthlyRevenue(conn), "📈", new Color(52, 152, 219),
					() -> showRevenueDetails(conn, "monthly")));
			panel.add(createMetricCard("Số hóa đơn:", getTotalInvoices(conn), "📄", new Color(155, 89, 182), null));
			panel.add(createMetricCard("Sản phẩm bán chạy:", getTopProduct(conn), "⭐", new Color(241, 196, 15),
					() -> showTopProductDetails(conn)));
			panel.add(createMetricCard("Sản phẩm tồn kho thấp:", getLowStockCount(conn), "⚠️", new Color(231, 76, 60),
					() -> showLowStockDetails(conn)));
			panel.add(createMetricCard("Tổng khách hàng:", getTotalCustomers(conn), "👥", new Color(149, 165, 166),
					null));
		} catch (SQLException e) {
			LOGGER.severe("Failed to load metrics data: " + e.getMessage());
			var errorLabel = new JLabel("Lỗi tải dữ liệu: " + e.getMessage());
			errorLabel.setFont(LABEL_FONT);
			errorLabel.setForeground(new Color(231, 76, 60));
			panel.add(errorLabel);
		}

		return panel;
	}

	private JPanel createMetricCard(String title, String value, String icon, Color iconColor, Runnable clickAction) {
		JPanel card = new JPanel(new BorderLayout(10, 10)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				var gp = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
				g2d.setPaint(gp);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
			}
		};
		card.setOpaque(false);
		card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200, 200, 200), 1, true),
				new EmptyBorder(15, 15, 15, 15)));
		card.setPreferredSize(new Dimension(180, 120));

		var iconLabel = new JLabel(icon);
		iconLabel.setFont(new Font("Roboto", Font.PLAIN, 28));
		iconLabel.setForeground(iconColor);
		card.add(iconLabel, BorderLayout.WEST);

		var textLabel = new JLabel("<html><b>" + title + "</b><br>" + value + "</html>");
		textLabel.setFont(LABEL_FONT);
		textLabel.setForeground(CARD_TEXT_COLOR);
		card.add(textLabel, BorderLayout.CENTER);

		if (clickAction != null) {
			card.setCursor(new Cursor(Cursor.HAND_CURSOR));
			card.addMouseListener(new MouseAdapter() {
				private boolean hovered = false;

				@Override
				public void mouseEntered(MouseEvent e) {
					hovered = true;
					card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(100, 100, 100), 2, true),
							new EmptyBorder(15, 15, 15, 15)));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					hovered = false;
					card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200, 200, 200), 1, true),
							new EmptyBorder(15, 15, 15, 15)));
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					clickAction.run();
				}
			});
		}

		return card;
	}

	private JPanel createRevenueChart() {
		var dataset = new DefaultCategoryDataset();
		var hasData = false;

		try (var conn = ConnectDB.getCon()) {
			var sql = """
					SELECT CAST(NgayLap AS DATE) as Ngay, SUM(TongTien) as DoanhThu
					FROM HOADON WHERE NgayLap >= DATEADD(day, -7, GETDATE())
					GROUP BY CAST(NgayLap AS DATE)""";
			try (var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
				while (rs.next()) {
					var date = dateFormat.format(rs.getDate("Ngay"));
					var revenue = rs.getDouble("DoanhThu");
					dataset.addValue(revenue / 1000000, "Doanh thu (triệu)", date);
					hasData = true;
				}
			}
		} catch (SQLException e) {
			LOGGER.severe("Failed to load revenue chart data: " + e.getMessage());
		}

		if (!hasData) {
			dataset.addValue(0, "Doanh thu (triệu)", "Không có dữ liệu");
		}

		var chart = ChartFactory.createLineChart("Doanh Thu 7 Ngày Gần Nhất", "Ngày", "Doanh Thu (triệu VND)", dataset,
				PlotOrientation.VERTICAL, true, true, false);

		var plot = chart.getCategoryPlot();
		var gp = new GradientPaint(0, 0, new Color(240, 248, 255), 0, 300, BACKGROUND_COLOR);
		plot.setBackgroundPaint(gp);
		plot.setRangeGridlinePaint(new Color(200, 200, 200));
		plot.setDomainGridlinePaint(new Color(200, 200, 200));
		var renderer = (LineAndShapeRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, PRIMARY_COLOR);
		renderer.setSeriesStroke(0, new BasicStroke(2.5f));
		renderer.setSeriesShapesVisible(0, true);
		chart.getTitle().setFont(TITLE_FONT);
		plot.getDomainAxis().setTickLabelFont(LABEL_FONT);
		plot.getRangeAxis().setTickLabelFont(LABEL_FONT);

		var chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(450, 350));
		var panel = new JPanel(new BorderLayout());
		panel.setBackground(BACKGROUND_COLOR);
		panel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200, 200, 200), 1, true),
				new EmptyBorder(10, 10, 10, 10)));
		panel.add(chartPanel, BorderLayout.CENTER);
		return panel;
	}

	private JPanel createNotificationsPanel() {
		var panel = new JPanel(new BorderLayout());
		panel.setBackground(BACKGROUND_COLOR);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TEXT_COLOR), "Thông Báo",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
				TITLE_FONT, TEXT_COLOR));

		var notificationsArea = new JEditorPane();
		notificationsArea.setContentType("text/html");
		notificationsArea.setEditable(false);
		notificationsArea.setBackground(BACKGROUND_COLOR);
		notificationsArea.setForeground(TEXT_COLOR);

		try (var conn = ConnectDB.getCon()) {
			var notifications = new StringBuilder("<html>");

			// Expiring Products
			var sqlProducts = """
					SELECT TenSP, HanSuDung FROM SANPHAM \
					WHERE HanSuDung IS NOT NULL AND HanSuDung <= DATEADD(day, 7, GETDATE()) \
					AND HanSuDung >= CAST(GETDATE() AS DATE)""";
			try (var ps = conn.prepareStatement(sqlProducts); var rs = ps.executeQuery()) {
				while (rs.next()) {
					notifications.append("<font color='red'>⚠️ Sản phẩm '").append(rs.getString("TenSP"))
							.append("' sắp hết hạn: ").append(dateFormat.format(rs.getDate("HanSuDung")))
							.append("</font><br>");
				}
			}

			// Expiring Promotions
			var sqlPromos = "SELECT TenKM, NgayKetThuc FROM KHUYENMAI "
					+ "WHERE NgayKetThuc <= DATEADD(day, 7, GETDATE()) AND NgayKetThuc >= CAST(GETDATE() AS DATE)";
			try (var ps = conn.prepareStatement(sqlPromos); var rs = ps.executeQuery()) {
				while (rs.next()) {
					notifications.append("<font color='blue'>🎉 Khuyến mãi '").append(rs.getString("TenKM"))
							.append("' sắp kết thúc: ").append(dateFormat.format(rs.getDate("NgayKetThuc")))
							.append("</font><br>");
				}
			}

			notifications.append("</html>");
			notificationsArea.setText(notifications.length() > 6 ? notifications.toString()
					: "<html><font color='green'>✅ Không có thông báo mới.</font></html>");
		} catch (SQLException e) {
			LOGGER.severe("Failed to load notifications: " + e.getMessage());
			notificationsArea
					.setText("<html><font color='red'>❌ Lỗi tải thông báo: " + e.getMessage() + "</font></html>");
		}

		var scrollPane = new JScrollPane(notificationsArea);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}

	private String getDailyRevenue(Connection conn) throws SQLException {
		var sql = "SELECT SUM(TongTien) as DoanhThu FROM HOADON WHERE CAST(NgayLap AS DATE) = CAST(GETDATE() AS DATE)";
		try (var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
			if (rs.next()) {
				var revenue = rs.getDouble("DoanhThu");
				return rs.wasNull() ? "0 triệu VND" : df.format(revenue / 1000000) + " triệu VND";
			}
			return "0 triệu VND";
		}
	}

	private String getMonthlyRevenue(Connection conn) throws SQLException {
		var sql = "SELECT SUM(TongTien) as DoanhThu FROM HOADON WHERE MONTH(NgayLap) = MONTH(GETDATE()) AND YEAR(NgayLap) = YEAR(GETDATE())";
		try (var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
			if (rs.next()) {
				var revenue = rs.getDouble("DoanhThu");
				return rs.wasNull() ? "0 triệu VND" : df.format(revenue / 1000000) + " triệu VND";
			}
			return "0 triệu VND";
		}
	}

	private String getTotalInvoices(Connection conn) throws SQLException {
		var sql = "SELECT COUNT(*) as Total FROM HOADON";
		try (var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
			if (rs.next()) {
				return String.valueOf(rs.getInt("Total"));
			}
			return "0";
		}
	}

	private String getTopProduct(Connection conn) throws SQLException {
		var sql = """
				SELECT TOP 1 sp.TenSP, SUM(ct.SoLuong) as TotalSold
				FROM CHITIETHOADON ct JOIN SANPHAM sp ON ct.MaSP = sp.MaSP
				GROUP BY sp.MaSP, sp.TenSP ORDER BY TotalSold DESC""";
		try (var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
			if (rs.next()) {
				return rs.getString("TenSP") + " (" + rs.getInt("TotalSold") + " đã bán)";
			}
			return "Không có dữ liệu";
		}
	}

	private String getLowStockCount(Connection conn) throws SQLException {
		var sql = "SELECT COUNT(*) as LowStock FROM SANPHAM WHERE SoLuongTon < 10";
		try (var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
			if (rs.next()) {
				return String.valueOf(rs.getInt("LowStock"));
			}
			return "0";
		}
	}

	private String getTotalCustomers(Connection conn) throws SQLException {
		var sql = "SELECT COUNT(*) as Total FROM KHACHHANG";
		try (var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
			if (rs.next()) {
				return String.valueOf(rs.getInt("Total"));
			}
			return "0";
		}
	}

	private void showRevenueDetails(Connection conn, String period) {
		String sql;
		String title;
		if (period.equals("daily")) {
			sql = "SELECT MaHD, MaKH, TongTien, NgayLap FROM HOADON WHERE CAST(NgayLap AS DATE) = CAST(GETDATE() AS DATE)";
			title = "Chi tiết doanh thu hôm nay";
		} else {
			sql = "SELECT MaHD, MaKH, TongTien, NgayLap FROM HOADON WHERE MONTH(NgayLap) = MONTH(GETDATE()) AND YEAR(NgayLap) = YEAR(GETDATE())";
			title = "Chi tiết doanh thu tháng này";
		}

		var details = new StringBuilder();
		try (var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
			while (rs.next()) {
				details.append("Hóa đơn: ").append(rs.getString("MaHD")).append(", Khách hàng: ")
						.append(rs.getString("MaKH")).append(", Tổng tiền: ")
						.append(df.format(rs.getDouble("TongTien") / 1000000)).append(" triệu VND").append(", Ngày: ")
						.append(dateFormat.format(rs.getDate("NgayLap"))).append("\n");
			}
		} catch (SQLException e) {
			LOGGER.severe("Failed to load revenue details: " + e.getMessage());
			details.append("Lỗi tải chi tiết: ").append(e.getMessage());
		}

		JOptionPane.showMessageDialog(this, details.length() > 0 ? details.toString() : "Không có dữ liệu.", title,
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void showTopProductDetails(Connection conn) {
		var sql = """
				SELECT TOP 1 sp.TenSP, sp.MaSP, SUM(ct.SoLuong) as TotalSold, sp.DonGia
				FROM CHITIETHOADON ct JOIN SANPHAM sp ON ct.MaSP = sp.MaSP
				GROUP BY sp.MaSP, sp.TenSP, sp.DonGia ORDER BY TotalSold DESC""";
		var details = new StringBuilder();
		try (var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
			if (rs.next()) {
				details.append("Sản phẩm: ").append(rs.getString("TenSP")).append("\nMã SP: ")
						.append(rs.getString("MaSP")).append("\nSố lượng bán: ").append(rs.getInt("TotalSold"))
						.append("\nĐơn giá: ").append(df.format(rs.getDouble("DonGia") / 1000000)).append(" triệu VND");
			} else {
				details.append("Không có dữ liệu.");
			}
		} catch (SQLException e) {
			LOGGER.severe("Failed to load top product details: " + e.getMessage());
			details.append("Lỗi tải chi tiết: ").append(e.getMessage());
		}

		JOptionPane.showMessageDialog(this, details.toString(), "Chi tiết sản phẩm bán chạy",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void showLowStockDetails(Connection conn) {
		var sql = "SELECT MaSP, TenSP, SoLuongTon FROM SANPHAM WHERE SoLuongTon < 10";
		var details = new StringBuilder();
		try (var ps = conn.prepareStatement(sql); var rs = ps.executeQuery()) {
			while (rs.next()) {
				details.append("Sản phẩm: ").append(rs.getString("TenSP")).append(", Mã SP: ")
						.append(rs.getString("MaSP")).append(", Tồn kho: ").append(rs.getInt("SoLuongTon"))
						.append("\n");
			}
		} catch (SQLException e) {
			LOGGER.severe("Failed to load low stock details: " + e.getMessage());
			details.append("Lỗi tải chi tiết: ").append(e.getMessage());
		}

		JOptionPane.showMessageDialog(this,
				details.length() > 0 ? details.toString() : "Không có sản phẩm tồn kho thấp.",
				"Chi tiết sản phẩm tồn kho thấp", JOptionPane.INFORMATION_MESSAGE);
	}
}