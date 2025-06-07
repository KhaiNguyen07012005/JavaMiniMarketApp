package panels;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
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

public class PromotionPanel extends JPanel {
	private static final Color PRIMARY_COLOR = new Color(0, 128, 128); // Teal
	private static final Color SECONDARY_COLOR = new Color(77, 182, 172); // Light Teal
	private static final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Off-white
	private static final Color TEXT_COLOR = new Color(45, 55, 72); // Dark Gray
	private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 24);
	private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 14);
	private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
	private float opacity = 0f;
	private JTable promotionTable;
	private DefaultTableModel tableModel;
	private JTextField txtMaKM;
	private JTextField txtTenKM;
	private JTextField txtPhanTramGiam;
	private JDateChooser dateStart;
	private JDateChooser dateEnd;

	public PromotionPanel() {
		setLayout(new BorderLayout());
		setBackground(BACKGROUND_COLOR);
		setOpaque(false);

		// Start fade-in animation
		startFadeInAnimation();

		// Header
		var headerPanel = new JPanel();
		headerPanel.setBackground(PRIMARY_COLOR);
		headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		var headerLabel = new JLabel("Quản Lý Khuyến Mãi", JLabel.CENTER);
		headerLabel.setFont(HEADER_FONT);
		headerLabel.setForeground(Color.WHITE);
		headerPanel.add(headerLabel);
		add(headerPanel, BorderLayout.NORTH);

		// Main Content
		var contentPanel = new JPanel(new BorderLayout(10, 10));
		contentPanel.setBackground(BACKGROUND_COLOR);
		contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Table
		String[] columns = { "Mã KM", "Tên KM", "Phần Trăm Giảm", "Ngày Bắt Đầu", "Ngày Kết Thúc" };
		tableModel = new DefaultTableModel(columns, 0);
		promotionTable = new JTable(tableModel);
		promotionTable.setFillsViewportHeight(true);
		promotionTable.setBackground(Color.WHITE);
		promotionTable.setForeground(TEXT_COLOR);
		promotionTable.setFont(LABEL_FONT);
		promotionTable.getTableHeader().setFont(TITLE_FONT);
		var tableScrollPane = new JScrollPane(promotionTable);
		tableScrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));

		// Form Panel
		var formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(BACKGROUND_COLOR);
		formPanel.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
		var gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		var lblMaKM = new JLabel("Mã KM:");
		lblMaKM.setFont(LABEL_FONT);
		gbc.gridx = 0;
		gbc.gridy = 0;
		formPanel.add(lblMaKM, gbc);

		txtMaKM = new JTextField(15);
		txtMaKM.setFont(LABEL_FONT);
		gbc.gridx = 1;
		gbc.gridy = 0;
		formPanel.add(txtMaKM, gbc);

		var lblTenKM = new JLabel("Tên KM:");
		lblTenKM.setFont(LABEL_FONT);
		gbc.gridx = 0;
		gbc.gridy = 1;
		formPanel.add(lblTenKM, gbc);

		txtTenKM = new JTextField(15);
		txtTenKM.setFont(LABEL_FONT);
		gbc.gridx = 1;
		gbc.gridy = 1;
		formPanel.add(txtTenKM, gbc);

		var lblPhanTramGiam = new JLabel("Phần Trăm Giảm:");
		lblPhanTramGiam.setFont(LABEL_FONT);
		gbc.gridx = 0;
		gbc.gridy = 2;
		formPanel.add(lblPhanTramGiam, gbc);

		txtPhanTramGiam = new JTextField(15);
		txtPhanTramGiam.setFont(LABEL_FONT);
		gbc.gridx = 1;
		gbc.gridy = 2;
		formPanel.add(txtPhanTramGiam, gbc);

		var lblNgayBatDau = new JLabel("Ngày Bắt Đầu:");
		lblNgayBatDau.setFont(LABEL_FONT);
		gbc.gridx = 0;
		gbc.gridy = 3;
		formPanel.add(lblNgayBatDau, gbc);

		dateStart = new JDateChooser();
		dateStart.setFont(LABEL_FONT);
		dateStart.setDateFormatString("dd/MM/yyyy");
		gbc.gridx = 1;
		gbc.gridy = 3;
		formPanel.add(dateStart, gbc);

		var lblNgayKetThuc = new JLabel("Ngày Kết Thúc:");
		lblNgayKetThuc.setFont(LABEL_FONT);
		gbc.gridx = 0;
		gbc.gridy = 4;
		formPanel.add(lblNgayKetThuc, gbc);

		dateEnd = new JDateChooser();
		dateEnd.setFont(LABEL_FONT);
		dateEnd.setDateFormatString("dd/MM/yyyy");
		gbc.gridx = 1;
		gbc.gridy = 4;
		formPanel.add(dateEnd, gbc);

		// Buttons
		var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		buttonPanel.setBackground(BACKGROUND_COLOR);
		var btnAdd = new JButton("Thêm");
		var btnUpdate = new JButton("Sửa");
		var btnDelete = new JButton("Xóa");
		var btnClear = new JButton("Làm Mới");
		styleButton(btnAdd);
		styleButton(btnUpdate);
		styleButton(btnDelete);
		styleButton(btnClear);
		buttonPanel.add(btnAdd);
		buttonPanel.add(btnUpdate);
		buttonPanel.add(btnDelete);
		buttonPanel.add(btnClear);

		contentPanel.add(tableScrollPane, BorderLayout.CENTER);
		contentPanel.add(formPanel, BorderLayout.WEST);
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(contentPanel, BorderLayout.CENTER);

		// Event Listeners
		btnAdd.addActionListener(e -> addPromotion());
		btnUpdate.addActionListener(e -> updatePromotion());
		btnDelete.addActionListener(e -> deletePromotion());
		btnClear.addActionListener(e -> clearForm());
		promotionTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				fillForm();
			}
		});

		// Load initial data
		loadPromotions();
	}

	private void startFadeInAnimation() {
		var timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				opacity += 0.05f;
				if (opacity >= 1f) {
					opacity = 1f;
					timer.cancel(); // Fixed: Changed cancelEvent() to cancel()
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

	private void loadPromotions() {
		tableModel.setRowCount(0);
		try {
			var conn = ConnectDB.getCon();
			var sql = "SELECT * FROM KHUYENMAI";
			var ps = conn.prepareStatement(sql);
			var rs = ps.executeQuery();
			try {
				var sdf = new SimpleDateFormat("dd/MM/yyyy");
				while (rs.next()) {
					var maKM = rs.getString("MaKM");
					var tenKM = rs.getString("TenKM");
					var phanTramGiam = rs.getInt("PhanTramGiam");
					var ngayBatDau = sdf.format(rs.getDate("NgayBatDau"));
					var ngayKetThuc = sdf.format(rs.getDate("NgayKetThuc"));
					tableModel.addRow(new Object[] { maKM, tenKM, phanTramGiam, ngayBatDau, ngayKetThuc });
				}
			} finally {
				rs.close();
				ps.close();
				conn.close();
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi tải danh sách khuyến mãi: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void addPromotion() {
		if (!validateForm()) {
			return;
		}
		try {
			var conn = ConnectDB.getCon();
			var sql = "INSERT INTO KHUYENMAI (MaKM, TenKM, PhanTramGiam, NgayBatDau, NgayKetThuc) VALUES (?, ?, ?, ?, ?)";
			var ps = conn.prepareStatement(sql);
			try {
				ps.setString(1, txtMaKM.getText());
				ps.setString(2, txtTenKM.getText());
				ps.setInt(3, Integer.parseInt(txtPhanTramGiam.getText()));
				ps.setDate(4, new java.sql.Date(dateStart.getDate().getTime()));
				ps.setDate(5, new java.sql.Date(dateEnd.getDate().getTime()));
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!", "Thành công",
						JOptionPane.INFORMATION_MESSAGE);
				loadPromotions();
				clearForm();
			} finally {
				ps.close();
				conn.close();
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi thêm khuyến mãi: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updatePromotion() {
		var selectedRow = promotionTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi để sửa!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!validateForm()) {
			return;
		}
		try {
			var conn = ConnectDB.getCon();
			var sql = "UPDATE KHUYENMAI SET TenKM = ?, PhanTramGiam = ?, NgayBatDau = ?, NgayKetThuc = ? WHERE MaKM = ?";
			var ps = conn.prepareStatement(sql);
			try {
				ps.setString(1, txtTenKM.getText());
				ps.setInt(2, Integer.parseInt(txtPhanTramGiam.getText()));
				ps.setDate(3, new java.sql.Date(dateStart.getDate().getTime()));
				ps.setDate(4, new java.sql.Date(dateEnd.getDate().getTime()));
				ps.setString(5, txtMaKM.getText());
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!", "Thành công",
						JOptionPane.INFORMATION_MESSAGE);
				loadPromotions();
				clearForm();
			} finally {
				ps.close();
				conn.close();
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi cập nhật khuyến mãi: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deletePromotion() {
		var selectedRow = promotionTable.getSelectedRow();
		if (selectedRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi để xóa!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		var confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khuyến mãi này?", "Xác nhận",
				JOptionPane.YES_NO_OPTION);
		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			var conn = ConnectDB.getCon();
			var sql = "DELETE FROM KHUYENMAI WHERE MaKM = ?";
			var ps = conn.prepareStatement(sql);
			try {
				ps.setString(1, (String) tableModel.getValueAt(selectedRow, 0));
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công!", "Thành công",
						JOptionPane.INFORMATION_MESSAGE);
				loadPromotions();
				clearForm();
			} finally {
				ps.close();
				conn.close();
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi xóa khuyến mãi: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void clearForm() {
		txtMaKM.setText("");
		txtTenKM.setText("");
		txtPhanTramGiam.setText("");
		dateStart.setDate(null);
		dateEnd.setDate(null);
		promotionTable.clearSelection();
	}

	private void fillForm() {
		var selectedRow = promotionTable.getSelectedRow();
		if (selectedRow == -1) {
			return;
		}
		txtMaKM.setText((String) tableModel.getValueAt(selectedRow, 0));
		txtTenKM.setText((String) tableModel.getValueAt(selectedRow, 1));
		txtPhanTramGiam.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
		try {
			var sdf = new SimpleDateFormat("dd/MM/yyyy");
			dateStart.setDate(sdf.parse((String) tableModel.getValueAt(selectedRow, 3)));
			dateEnd.setDate(sdf.parse((String) tableModel.getValueAt(selectedRow, 4)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean validateForm() {
		if (txtMaKM.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Mã khuyến mãi không được để trống!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if (txtTenKM.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Tên khuyến mãi không được để trống!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		try {
			var phanTram = Integer.parseInt(txtPhanTramGiam.getText());
			if (phanTram <= 0 || phanTram > 100) {
				JOptionPane.showMessageDialog(this, "Phần trăm giảm phải từ 1 đến 100!", "Cảnh báo",
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Phần trăm giảm phải là số hợp lệ!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if (dateStart.getDate() == null || dateEnd.getDate() == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày bắt đầu và ngày kết thúc!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if (dateStart.getDate().after(dateEnd.getDate())) {
			JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải trước ngày kết thúc!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	// Phương thức để SalesPanel lấy khuyến mãi hiện tại
	public int getCurrentDiscount() {
		try {
			var conn = ConnectDB.getCon();
			var sql = "SELECT TOP 1 PhanTramGiam FROM KHUYENMAI WHERE GETDATE() BETWEEN NgayBatDau AND NgayKetThuc";
			var ps = conn.prepareStatement(sql);
			var rs = ps.executeQuery();
			try {
				if (rs.next()) {
					return rs.getInt("PhanTramGiam");
				}
			} finally {
				rs.close();
				ps.close();
				conn.close();
			}
		} catch (SQLException e) {
			System.err.println("Lỗi lấy khuyến mãi hiện tại: " + e.getMessage());
		}
		return 0; // Không có khuyến mãi
	}

	// Phương thức để DashboardPanel hiển thị khuyến mãi đang chạy
	public String getActivePromotion() {
		try {
			var conn = ConnectDB.getCon();
			var sql = "SELECT TOP 1 TenKM, PhanTramGiam FROM KHUYENMAI WHERE GETDATE() BETWEEN NgayBatDau AND NgayKetThuc";
			var ps = conn.prepareStatement(sql);
			var rs = ps.executeQuery();
			try {
				if (rs.next()) {
					return "Khuyến mãi: " + rs.getString("TenKM") + " - Giảm " + rs.getInt("PhanTramGiam") + "%";
				}
			} finally {
				rs.close();
				ps.close();
				conn.close();
			}
		} catch (SQLException e) {
			System.err.println("Lỗi lấy thông tin khuyến mãi: " + e.getMessage());
		}
		return "Không có khuyến mãi đang chạy.";
	}
}