package panels;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import connect.ConnectDB;

public class HistoryPanel extends JPanel {
	private static final Color PRIMARY_COLOR = new Color(0, 128, 128); // Teal
	private static final Color SECONDARY_COLOR = new Color(77, 182, 172); // Light Teal
	private static final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Off-white
	private static final Color TEXT_COLOR = new Color(45, 55, 72); // Dark Gray
	private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 24);
	private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
	private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
	private float opacity = 0f;
	private JTable logTable;
	private DefaultTableModel tableModel;
	private JTextField txtSearchUser;
	private JDateChooser dateFilter;
	private JComboBox<String> actionTypeFilter;

	public HistoryPanel() {
		setLayout(new BorderLayout());
		setBackground(BACKGROUND_COLOR);
		setOpaque(false);

		// Start fade-in animation
		startFadeInAnimation();

		// Header
		var headerPanel = new JPanel();
		headerPanel.setBackground(PRIMARY_COLOR);
		headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		var headerLabel = new JLabel("Nhật Ký Hoạt Động", JLabel.CENTER);
		headerLabel.setFont(HEADER_FONT);
		headerLabel.setForeground(Color.WHITE);
		headerPanel.add(headerLabel);
		add(headerPanel, BorderLayout.NORTH);

		// Filter Panel
		var filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		filterPanel.setBackground(BACKGROUND_COLOR);

		var lblSearchUser = new JLabel("Tên Đăng Nhập:");
		lblSearchUser.setFont(LABEL_FONT);
		filterPanel.add(lblSearchUser);

		txtSearchUser = new JTextField(15);
		txtSearchUser.setFont(LABEL_FONT);
		filterPanel.add(txtSearchUser);

		var lblDate = new JLabel("Ngày:");
		lblDate.setFont(LABEL_FONT);
		filterPanel.add(lblDate);

		dateFilter = new JDateChooser();
		dateFilter.setFont(LABEL_FONT);
		dateFilter.setDateFormatString("dd/MM/yyyy");
		filterPanel.add(dateFilter);

		var lblActionType = new JLabel("Loại Hành Động:");
		lblActionType.setFont(LABEL_FONT);
		filterPanel.add(lblActionType);

		actionTypeFilter = new JComboBox<>(new String[] { "Tất Cả", "Login/Logout", "CRUD", "Sales" });
		actionTypeFilter.setFont(LABEL_FONT);
		filterPanel.add(actionTypeFilter);

		var btnFilter = new JButton("Lọc");
		styleButton(btnFilter);
		filterPanel.add(btnFilter);

		var btnClearFilter = new JButton("Xóa Bộ Lọc");
		styleButton(btnClearFilter);
		filterPanel.add(btnClearFilter);

		// Table
		String[] columns = { "Thời Gian", "Người Dùng", "Hành Động" };
		tableModel = new DefaultTableModel(columns, 0);
		logTable = new JTable(tableModel);
		logTable.setFillsViewportHeight(true);
		logTable.setBackground(Color.WHITE);
		logTable.setForeground(TEXT_COLOR);
		logTable.setFont(LABEL_FONT);
		logTable.getTableHeader().setFont(TITLE_FONT);
		var tableScrollPane = new JScrollPane(logTable);
		tableScrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));

		add(filterPanel, BorderLayout.PAGE_START);
		add(tableScrollPane, BorderLayout.CENTER);

		// Event Listeners
		btnFilter.addActionListener(e -> applyFilter());
		btnClearFilter.addActionListener(e -> clearFilter());

		// Load initial data
		loadLogs();
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

	private void loadLogs() {
		tableModel.setRowCount(0);
		try {
			var conn = ConnectDB.getCon();
			var sql = "SELECT * FROM ACTIVITY_LOG ORDER BY LogTime DESC";
			var ps = conn.prepareStatement(sql);
			var rs = ps.executeQuery();
			try {
				var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				while (rs.next()) {
					var logTime = sdf.format(rs.getTimestamp("LogTime"));
					var tenDangNhap = rs.getString("TenDangNhap");
					var action = rs.getString("Action");
					tableModel.addRow(new Object[] { logTime, tenDangNhap, action });
				}
			} finally {
				rs.close();
				ps.close();
				conn.close();
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi tải nhật ký hoạt động: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void applyFilter() {
		tableModel.setRowCount(0);
		try {
			var conn = ConnectDB.getCon();
			var sql = new StringBuilder("SELECT * FROM ACTIVITY_LOG WHERE 1=1");
			var tenDangNhap = txtSearchUser.getText().trim();
			var selectedDate = dateFilter.getDate();
			var actionType = (String) actionTypeFilter.getSelectedItem();

			if (!tenDangNhap.isEmpty()) {
				sql.append(" AND TenDangNhap LIKE ?");
			}
			if (selectedDate != null) {
				sql.append(" AND CAST(LogTime AS DATE) = ?");
			}
			if (!actionType.equals("Tất Cả")) {
				switch (actionType) {
				case "Login/Logout":
					sql.append(" AND Action IN ('LOGIN', 'LOGOUT')");
					break;
				case "CRUD":
					sql.append(" AND Action IN ('ADD_PRODUCT', 'UPDATE_PRODUCT', 'DELETE_PRODUCT')");
					break;
				case "Sales":
					sql.append(" AND Action IN ('CREATE_INVOICE', 'CANCEL_INVOICE')");
					break;
				case null:
				default:
					break;
				}
			}
			sql.append(" ORDER BY LogTime DESC");

			var ps = conn.prepareStatement(sql.toString());
			var paramIndex = 1;
			if (!tenDangNhap.isEmpty()) {
				ps.setString(paramIndex++, "%" + tenDangNhap + "%");
			}
			if (selectedDate != null) {
				ps.setDate(paramIndex++, new java.sql.Date(selectedDate.getTime()));
			}
			var rs = ps.executeQuery();
			try {
				var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				while (rs.next()) {
					var logTime = sdf.format(rs.getTimestamp("LogTime"));
					var user = rs.getString("TenDangNhap");
					var action = rs.getString("Action");
					tableModel.addRow(new Object[] { logTime, user, action });
				}
			} finally {
				rs.close();
				ps.close();
				conn.close();
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi lọc nhật ký: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void clearFilter() {
		txtSearchUser.setText("");
		dateFilter.setDate(null);
		actionTypeFilter.setSelectedIndex(0);
		loadLogs();
	}

	// Phương thức để ghi log từ các panel khác
	public static void logActivity(String tenDangNhap, String action) {
		try {
			var conn = ConnectDB.getCon();
			var sql = "INSERT INTO ACTIVITY_LOG (TenDangNhap, Action, LogTime) VALUES (?, ?, GETDATE())";
			var ps = conn.prepareStatement(sql);
			try {
				ps.setString(1, tenDangNhap);
				ps.setString(2, action);
				ps.executeUpdate();
			} finally {
				ps.close();
				conn.close();
			}
		} catch (SQLException e) {
			System.err.println("Lỗi ghi log hoạt động: " + e.getMessage());
		}
	}
}