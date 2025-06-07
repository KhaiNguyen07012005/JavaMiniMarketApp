package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.sql.Types;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import connect.ConnectDB;

public class SalesPanel extends JPanel {
	private JTextField txtMaSP, txtSoLuong, txtMaKH;
	private JTable table;
	private DefaultTableModel tableModel;
	private JButton btnAddItem, btnConfirmSale, btnClear;
	private JLabel lblTotal;

	public SalesPanel() {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);

		var title = new JLabel("Bán Hàng", SwingConstants.CENTER);
		title.setFont(new Font("Segoe UI", Font.BOLD, 26));
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		add(title, BorderLayout.NORTH);

		var inputPanel = new JPanel(new GridBagLayout());
		inputPanel.setBackground(new Color(230, 240, 250));
		inputPanel.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(180, 200, 230), 1),
						BorderFactory.createEmptyBorder(8, 12, 8, 12)));
		var gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		inputPanel.add(new JLabel("Mã SP:"), gbc);
		txtMaSP = new JTextField(15);
		txtMaSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		gbc.gridx = 1;
		inputPanel.add(txtMaSP, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		inputPanel.add(new JLabel("Số lượng:"), gbc);
		txtSoLuong = new JTextField(15);
		txtSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		gbc.gridx = 1;
		inputPanel.add(txtSoLuong, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		inputPanel.add(new JLabel("Mã KH:"), gbc);
		txtMaKH = new JTextField(15);
		txtMaKH.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		gbc.gridx = 1;
		inputPanel.add(txtMaKH, gbc);

		var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
		btnAddItem = new JButton("Thêm sản phẩm", loadIcon("/icons/add.png"));
		btnConfirmSale = new JButton("Xác nhận bán", loadIcon("/icons/confirm.png"));
		btnClear = new JButton("Xóa giỏ hàng", loadIcon("/icons/clear.png"));

		btnAddItem.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnConfirmSale.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnClear.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnAddItem.setBackground(new Color(0, 150, 70));
		btnConfirmSale.setBackground(new Color(0, 120, 215));
		btnClear.setBackground(new Color(200, 60, 60));
		btnAddItem.setForeground(Color.WHITE);
		btnConfirmSale.setForeground(Color.WHITE);
		btnClear.setForeground(Color.WHITE);
		btnAddItem.setFocusPainted(false);
		btnConfirmSale.setFocusPainted(false);
		btnClear.setFocusPainted(false);

		addHoverEffect(btnAddItem, new Color(0, 130, 60));
		addHoverEffect(btnConfirmSale, new Color(0, 100, 180));
		addHoverEffect(btnClear, new Color(170, 40, 40));

		buttonPanel.add(btnAddItem);
		buttonPanel.add(btnConfirmSale);
		buttonPanel.add(btnClear);

		var contentPanel = new JPanel(new BorderLayout(0, 10));
		contentPanel.setBackground(Color.WHITE);
		contentPanel.add(inputPanel, BorderLayout.NORTH);
		contentPanel.add(buttonPanel, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.PAGE_START);

		String[] columns = { "Mã SP", "Tên SP", "Số lượng", "Đơn giá", "Thành tiền" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(tableModel);
		table.setRowHeight(40);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		var header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 15));
		header.setBackground(new Color(230, 240, 250));
		header.setForeground(new Color(0, 80, 150));
		header.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 230)));
		table.setSelectionBackground(new Color(0, 120, 215));
		table.setGridColor(new Color(220, 230, 240));
		table.setShowGrid(true);

		var centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for (var i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		var scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 230)));
		add(scrollPane, BorderLayout.CENTER);

		lblTotal = new JLabel("Tổng tiền: 0 VNĐ");
		lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTotal.setForeground(new Color(0, 120, 215));
		lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(lblTotal, BorderLayout.SOUTH);

		btnAddItem.addActionListener(e -> addItem());
		btnConfirmSale.addActionListener(e -> confirmSale());
		btnClear.addActionListener(e -> clearCart());
	}

	private void addHoverEffect(JButton btn, Color hoverColor) {
		var originalColor = btn.getBackground();
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				btn.setBackground(hoverColor);
				btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				btn.setBackground(originalColor);
			}
		});
	}

	private ImageIcon loadIcon(String path) {
		try {
			return new ImageIcon(getClass().getResource(path));
		} catch (Exception e) {
			System.err.println("Error loading icon: " + path);
			return null;
		}
	}

	private void addItem() {
		var maSP = txtMaSP.getText().trim();
		var soLuongStr = txtSoLuong.getText().trim();
		if (maSP.isEmpty() || soLuongStr.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập mã sản phẩm và số lượng!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		int soLuong;
		try {
			soLuong = Integer.parseInt(soLuongStr);
			if (soLuong <= 0) {
				JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên dương!", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên hợp lệ!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		try (var conn = ConnectDB.getCon()) {
			var sql = "SELECT TenSP, DonGia, SoLuongTon FROM SANPHAM WHERE MaSP = ?";
			try (var ps = conn.prepareStatement(sql)) {
				ps.setString(1, maSP);
				var rs = ps.executeQuery();
				if (rs.next()) {
					var soLuongTon = rs.getInt("SoLuongTon");
					if (soLuong > soLuongTon) {
						JOptionPane.showMessageDialog(this, "Số lượng tồn không đủ!", "Thông báo",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					var tenSP = rs.getString("TenSP");
					var donGia = rs.getDouble("DonGia");
					var thanhTien = soLuong * donGia;
					tableModel.addRow(new Object[] { maSP, tenSP, soLuong, String.format("%.2f", donGia),
							String.format("%.2f", thanhTien) });
					updateTotal();
				} else {
					JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!", "Thông báo",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi thêm sản phẩm: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void confirmSale() {
		if (tableModel.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Giỏ hàng trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}
		var maKH = txtMaKH.getText().trim();
		if (!maKH.isEmpty()) {
			try (var conn = ConnectDB.getCon()) {
				var sql = "SELECT COUNT(*) FROM KHACHHANG WHERE MaKH = ?";
				try (var ps = conn.prepareStatement(sql)) {
					ps.setString(1, maKH);
					var rs = ps.executeQuery();
					if (rs.next() && rs.getInt(1) == 0) {
						JOptionPane.showMessageDialog(this, "Mã khách hàng không tồn tại!", "Thông báo",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Lỗi kiểm tra khách hàng: " + e.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		try (var conn = ConnectDB.getCon()) {
			conn.setAutoCommit(false);
			var maHD = "HD" + System.currentTimeMillis();
			var sql = "INSERT INTO HOADON (MaHD, MaKH, NgayLap, TongTien) VALUES (?, ?, GETDATE(), ?)";
			try (var ps = conn.prepareStatement(sql)) {
				ps.setString(1, maHD);
				if (maKH.isEmpty()) {
					ps.setNull(2, Types.VARCHAR);
				} else {
					ps.setString(2, maKH);
				}
				ps.setDouble(3, calculateTotal());
				ps.executeUpdate();
			}

			sql = "INSERT INTO CHITIETHD (MaHD, MaSP, SoLuong, DonGia, ThanhTien) VALUES (?, ?, ?, ?, ?)";
			try (var ps = conn.prepareStatement(sql)) {
				for (var i = 0; i < tableModel.getRowCount(); i++) {
					var maSP = (String) tableModel.getValueAt(i, 0);
					var soLuong = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
					var donGia = Double.parseDouble(tableModel.getValueAt(i, 3).toString());
					var thanhTien = Double.parseDouble(tableModel.getValueAt(i, 4).toString());
					ps.setString(1, maHD);
					ps.setString(2, maSP);
					ps.setInt(3, soLuong);
					ps.setDouble(4, donGia);
					ps.setDouble(5, thanhTien);
					ps.executeUpdate();

					var updateSql = "UPDATE SANPHAM SET SoLuongTon = SoLuongTon - ? WHERE MaSP = ?";
					try (var updatePs = conn.prepareStatement(updateSql)) {
						updatePs.setInt(1, soLuong);
						updatePs.setString(2, maSP);
						updatePs.executeUpdate();
					}
				}
			}

			conn.commit();
			JOptionPane.showMessageDialog(this, "Bán hàng thành công! Mã hóa đơn: " + maHD, "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			clearCart();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Lỗi xác nhận bán hàng: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void clearCart() {
		tableModel.setRowCount(0);
		txtMaSP.setText("");
		txtSoLuong.setText("");
		txtMaKH.setText("");
		updateTotal();
	}

	private double calculateTotal() {
		var total = 0D;
		for (var i = 0; i < tableModel.getRowCount(); i++) {
			total += Double.parseDouble(tableModel.getValueAt(i, 4).toString());
		}
		return total;
	}

	private void updateTotal() {
		lblTotal.setText("Tổng tiền: " + String.format("%.2f", calculateTotal()) + " VNĐ");
	}
}