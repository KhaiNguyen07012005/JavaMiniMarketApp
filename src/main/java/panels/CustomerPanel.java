package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import connect.ConnectDB;
import dialogs.CustomerFormDialog;

public class CustomerPanel extends JPanel {
	private JTable table;
	private DefaultTableModel tableModel;
	private int currentPage = 1;
	private int rowsPerPage = 10;
	private int totalRows = 0;
	private int totalPages = 1;
	private JLabel pageLabel;
	private JTextField txtSearch;
	private JButton btnSearch, btnClearSearch, btnAdd;
	private JComboBox<String> pageSizeComboBox;

	public CustomerPanel() {
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);

		var title = new JLabel("Thông tin khách hàng", SwingConstants.CENTER);
		title.setFont(new Font("Segoe UI", Font.BOLD, 26));
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
		add(title, BorderLayout.NORTH);

		var contentPanel = new JPanel(new BorderLayout(0, 10));
		contentPanel.setBackground(Color.WHITE);

		var searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
		searchPanel.setBackground(new Color(230, 240, 250));
		searchPanel.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(180, 200, 230), 1),
						BorderFactory.createEmptyBorder(8, 12, 8, 12)));

		var searchLabel = new JLabel("Tìm kiếm:");
		searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		txtSearch = new JTextField(25);
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btnSearch = new JButton("Tìm kiếm", loadIcon("/icons/search.png"));
		btnClearSearch = new JButton("Xóa tìm kiếm", loadIcon("/icons/clear.png"));

		btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnClearSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnSearch.setBackground(new Color(0, 120, 215));
		btnSearch.setForeground(Color.WHITE);
		btnClearSearch.setBackground(new Color(200, 60, 60));
		btnClearSearch.setForeground(Color.WHITE);
		btnSearch.setFocusPainted(false);
		btnClearSearch.setFocusPainted(false);

		addHoverEffect(btnSearch, new Color(0, 100, 180));
		addHoverEffect(btnClearSearch, new Color(170, 40, 40));

		searchPanel.add(searchLabel);
		searchPanel.add(txtSearch);
		searchPanel.add(btnSearch);
		searchPanel.add(btnClearSearch);

		contentPanel.add(searchPanel, BorderLayout.NORTH);

		var addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
		addButtonPanel.setBackground(Color.WHITE);

		btnAdd = new JButton("Thêm khách hàng mới", loadIcon("/icons/add.png"));
		btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnAdd.setBackground(new Color(0, 150, 70));
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setFocusPainted(false);
		addHoverEffect(btnAdd, new Color(0, 130, 60));

		addButtonPanel.add(btnAdd);
		contentPanel.add(addButtonPanel, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.PAGE_START);

		String[] columns = { "Mã KH", "Tên khách hàng", "Số điện thoại", "Điểm tích luỹ", "Thao tác" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Object.class;
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
		table.setSelectionForeground(Color.WHITE);
		table.setGridColor(new Color(220, 230, 240));
		table.setShowGrid(true);

		var centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		for (var i = 0; i < table.getColumnCount() - 1; i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		TableColumn column = null;
		column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(80);
		column = table.getColumnModel().getColumn(1);
		column.setPreferredWidth(200);
		column = table.getColumnModel().getColumn(2);
		column.setPreferredWidth(120);
		column = table.getColumnModel().getColumn(3);
		column.setPreferredWidth(100);
		column = table.getColumnModel().getColumn(4);
		column.setPreferredWidth(150);

		var scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 230)));
		add(scrollPane, BorderLayout.CENTER);

		var paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		paginationPanel.setBackground(new Color(230, 240, 250));
		paginationPanel.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 230), 1));

		var btnFirst = createPageButton("<<");
		var btnPrev = createPageButton("<");
		var btnNext = createPageButton(">");
		var btnLast = createPageButton(">>");

		pageLabel = new JLabel("Trang " + currentPage + " / " + totalPages);
		pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		String[] pageSizeOptions = { "10", "20", "50", "100" };
		pageSizeComboBox = new JComboBox<>(pageSizeOptions);
		pageSizeComboBox.setSelectedItem(String.valueOf(rowsPerPage));
		pageSizeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		pageSizeComboBox.setFocusable(false);

		paginationPanel.add(btnFirst);
		paginationPanel.add(btnPrev);
		paginationPanel.add(pageLabel);
		paginationPanel.add(btnNext);
		paginationPanel.add(btnLast);
		paginationPanel.add(new JLabel("Số dòng/trang:"));
		paginationPanel.add(pageSizeComboBox);

		add(paginationPanel, BorderLayout.PAGE_END);

		btnFirst.addActionListener(e -> {
			if (currentPage > 1) {
				currentPage = 1;
				loadData();
			}
		});

		btnPrev.addActionListener(e -> {
			if (currentPage > 1) {
				currentPage--;
				loadData();
			}
		});

		btnNext.addActionListener(e -> {
			if (currentPage < totalPages) {
				currentPage++;
				loadData();
			}
		});

		btnLast.addActionListener(e -> {
			if (currentPage < totalPages) {
				currentPage = totalPages;
				loadData();
			}
		});

		btnSearch.addActionListener(e -> onSearch());

		btnClearSearch.addActionListener(e -> {
			txtSearch.setText("");
			currentPage = 1;
			loadData();
		});

		pageSizeComboBox.addActionListener(e -> {
			rowsPerPage = Integer.parseInt((String) pageSizeComboBox.getSelectedItem());
			currentPage = 1;
			loadData();
		});

		btnAdd.addActionListener(e -> onAdd());

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				var row = table.rowAtPoint(e.getPoint());
				var col = table.columnAtPoint(e.getPoint());

				if (row >= 0 && col == 4) {

					var maKH = (String) tableModel.getValueAt(row, 0);
					var tenKH = (String) tableModel.getValueAt(row, 1);
					var sdt = (String) tableModel.getValueAt(row, 2);
					var diem = tableModel.getValueAt(row, 3).toString();

					var x = e.getX() - table.getCellRect(row, col, false).x;

					if (x < 60) {
						editCustomer(maKH, tenKH, sdt, diem);
					} else {
						deleteCustomer(maKH);
					}
				}
			}
		});

		loadData();
	}

	private JButton createPageButton(String text) {
		var btn = new JButton(text);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btn.setBackground(new Color(200, 220, 240));
		btn.setForeground(new Color(0, 80, 150));
		btn.setFocusPainted(false);
		btn.setBorder(BorderFactory.createLineBorder(new Color(150, 180, 210)));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				btn.setBackground(new Color(160, 190, 220));
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				btn.setBackground(new Color(200, 220, 240));
			}
		});
		return btn;
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

	private Component createButtonPanel(String maKH, String tenKH, String sdt, String diem) {
		var panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
		panel.setBackground(Color.WHITE);

		var btnEdit = new JButton("Sửa", loadIcon("/icons/edit.png"));
		btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnEdit.setBackground(new Color(0, 120, 215));
		btnEdit.setForeground(Color.WHITE);
		btnEdit.setFocusPainted(false);

		var btnDelete = new JButton("Xóa", loadIcon("/icons/delete.png"));
		btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnDelete.setBackground(new Color(200, 60, 60));
		btnDelete.setForeground(Color.WHITE);
		btnDelete.setFocusPainted(false);

		panel.add(btnEdit);
		panel.add(btnDelete);

		return panel;
	}

	private void loadData() {
		try (var conn = ConnectDB.getCon()) {

			var countSql = "SELECT COUNT(*) FROM KHACHHANG";
			var searchKeyword = txtSearch.getText().trim();

			if (!searchKeyword.isEmpty()) {
				countSql = "SELECT COUNT(*) FROM KHACHHANG WHERE TenKH LIKE ? OR SoDienThoai LIKE ?";
			}

			try (var countStmt = conn.prepareStatement(countSql)) {
				if (!searchKeyword.isEmpty()) {
					var pattern = "%" + searchKeyword + "%";
					countStmt.setString(1, pattern);
					countStmt.setString(2, pattern);
				}

				try (var countRs = countStmt.executeQuery()) {
					if (countRs.next()) {
						totalRows = countRs.getInt(1);
					}
				}
			}

			totalPages = (int) Math.ceil((double) totalRows / rowsPerPage);
			if (totalPages == 0) {
				totalPages = 1;
			}
			if (currentPage > totalPages) {
				currentPage = totalPages;
			}

			pageLabel.setText("Trang " + currentPage + " / " + totalPages);

			String sql;
			if (searchKeyword.isEmpty()) {
				sql = "SELECT * FROM KHACHHANG ORDER BY MaKH OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
			} else {
				sql = "SELECT * FROM KHACHHANG WHERE TenKH LIKE ? OR SoDienThoai LIKE ? ORDER BY MaKH OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
			}

			try (var stmt = conn.prepareStatement(sql)) {
				if (searchKeyword.isEmpty()) {
					stmt.setInt(1, (currentPage - 1) * rowsPerPage);
					stmt.setInt(2, rowsPerPage);
				} else {
					var pattern = "%" + searchKeyword + "%";
					stmt.setString(1, pattern);
					stmt.setString(2, pattern);
					stmt.setInt(3, (currentPage - 1) * rowsPerPage);
					stmt.setInt(4, rowsPerPage);
				}

				var rs = stmt.executeQuery();
				tableModel.setRowCount(0);

				while (rs.next()) {
					var maKH = rs.getString("MaKH");
					var tenKH = rs.getString("TenKH");
					var soDienThoai = rs.getString("SoDienThoai");
					var diemTichLuy = String.valueOf(rs.getInt("DiemTichLuy"));

					tableModel.addRow(new Object[] { maKH, tenKH, soDienThoai, rs.getInt("DiemTichLuy"), "Sửa | Xóa" });
				}

				table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
					@Override
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						var panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));

						panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

						var btnEdit = new JButton("Sửa");
						btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 12));
						btnEdit.setBackground(new Color(0, 120, 215));
						btnEdit.setForeground(Color.WHITE);
						btnEdit.setFocusPainted(false);

						var editIcon = loadIcon("/icons/edit.png");
						if (editIcon != null) {
							btnEdit.setIcon(editIcon);
						}

						var btnDelete = new JButton("Xóa");
						btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
						btnDelete.setBackground(new Color(200, 60, 60));
						btnDelete.setForeground(Color.WHITE);
						btnDelete.setFocusPainted(false);

						var deleteIcon = loadIcon("/icons/delete.png");
						if (deleteIcon != null) {
							btnDelete.setIcon(deleteIcon);
						}

						panel.add(btnEdit);
						panel.add(btnDelete);

						return panel;
					}
				});

				if (tableModel.getRowCount() == 0 && !searchKeyword.isEmpty()) {
					JOptionPane.showMessageDialog(this,
							"Không tìm thấy khách hàng phù hợp với từ khóa: " + searchKeyword, "Thông báo",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu khách hàng: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onAdd() {
		var dialog = new CustomerFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm khách hàng", "", "",
				"", "", true);

		dialog.setVisible(true);

		if (dialog.isConfirmed()) {
			try (var conn = ConnectDB.getCon()) {
				var sql = "INSERT INTO KHACHHANG (MaKH, TenKH, SoDienThoai, DiemTichLuy) VALUES (?, ?, ?, ?)";
				try (var pst = conn.prepareStatement(sql)) {
					pst.setString(1, dialog.getMaKH());
					pst.setString(2, dialog.getTenKH());
					pst.setString(3, dialog.getSoDienThoai());
					pst.setInt(4, Integer.parseInt(dialog.getDiemTichLuy()));
					pst.executeUpdate();

					JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo",
							JOptionPane.INFORMATION_MESSAGE);

					loadData();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Không thể thêm khách hàng: " + ex.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void editCustomer(String maKH, String tenKH, String sdt, String diem) {
		var dialog = new CustomerFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa khách hàng", maKH,
				tenKH, sdt, diem, false);

		dialog.setVisible(true);

		if (dialog.isConfirmed()) {
			try (var conn = ConnectDB.getCon()) {
				var sql = "UPDATE KHACHHANG SET TenKH = ?, SoDienThoai = ?, DiemTichLuy = ? WHERE MaKH = ?";
				try (var pst = conn.prepareStatement(sql)) {
					pst.setString(1, dialog.getTenKH());
					pst.setString(2, dialog.getSoDienThoai());
					pst.setInt(3, Integer.parseInt(dialog.getDiemTichLuy()));
					pst.setString(4, dialog.getMaKH());

					var rowsAffected = pst.executeUpdate();

					if (rowsAffected > 0) {
						JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!", "Thông báo",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng cần cập nhật!", "Thông báo",
								JOptionPane.WARNING_MESSAGE);
					}

					loadData();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Không thể cập nhật khách hàng: " + ex.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void deleteCustomer(String maKH) {
		var confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xoá khách hàng này?", "Xác nhận xoá",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (confirm == JOptionPane.YES_OPTION) {
			try (var conn = ConnectDB.getCon()) {

				var hasReferences = checkCustomerReferences(conn, maKH);

				if (hasReferences) {
					JOptionPane.showMessageDialog(this,
							"Không thể xoá khách hàng này vì đang được sử dụng trong hệ thống!", "Cảnh báo",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				var sql = "DELETE FROM KHACHHANG WHERE MaKH=?";
				try (var pst = conn.prepareStatement(sql)) {
					pst.setString(1, maKH);

					var rowsAffected = pst.executeUpdate();

					if (rowsAffected > 0) {
						JOptionPane.showMessageDialog(this, "Xoá khách hàng thành công!", "Thông báo",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng cần xoá!", "Thông báo",
								JOptionPane.WARNING_MESSAGE);
					}

					loadData();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Không thể xoá khách hàng: " + ex.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private boolean checkCustomerReferences(java.sql.Connection conn, String maKH) throws SQLException {
		return false;
	}

	private void onSearch() {
		var keyword = txtSearch.getText().trim();
		if (keyword.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		currentPage = 1;
		loadData();
	}

	public void refreshData() {
		currentPage = 1;
		txtSearch.setText("");
		loadData();
	}

	public void showAllCustomers() {
		txtSearch.setText("");
		currentPage = 1;
		loadData();
	}

	public void filterByPoints(int minPoints) {
		try (var conn = ConnectDB.getCon()) {
			var sql = "SELECT COUNT(*) FROM KHACHHANG WHERE DiemTichLuy >= ?";
			try (var countStmt = conn.prepareStatement(sql)) {
				countStmt.setInt(1, minPoints);
				try (var countRs = countStmt.executeQuery()) {
					if (countRs.next()) {
						totalRows = countRs.getInt(1);
					}
				}
			}

			totalPages = (int) Math.ceil((double) totalRows / rowsPerPage);
			if (totalPages == 0) {
				totalPages = 1;
			}
			currentPage = 1;
			pageLabel.setText("Trang " + currentPage + " / " + totalPages);

			sql = "SELECT * FROM KHACHHANG WHERE DiemTichLuy >= ? ORDER BY DiemTichLuy DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
			try (var stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, minPoints);
				stmt.setInt(2, (currentPage - 1) * rowsPerPage);
				stmt.setInt(3, rowsPerPage);

				var rs = stmt.executeQuery();
				tableModel.setRowCount(0);

				while (rs.next()) {
					var maKH = rs.getString("MaKH");
					var tenKH = rs.getString("TenKH");
					var soDienThoai = rs.getString("SoDienThoai");
					var diemTichLuy = String.valueOf(rs.getInt("DiemTichLuy"));

					var buttonPanel = createButtonPanel(maKH, tenKH, soDienThoai, diemTichLuy);
					tableModel.addRow(new Object[] { maKH, tenKH, soDienThoai, rs.getInt("DiemTichLuy"), buttonPanel });
				}

				if (tableModel.getRowCount() == 0) {
					JOptionPane.showMessageDialog(this,
							"Không tìm thấy khách hàng có điểm tích lũy từ " + minPoints + " trở lên!", "Thông báo",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi khi lọc khách hàng theo điểm tích lũy: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void exportCustomerData() {

		JOptionPane.showMessageDialog(this, "Chức năng xuất dữ liệu sẽ được triển khai trong phiên bản tiếp theo!",
				"Thông báo", JOptionPane.INFORMATION_MESSAGE);
	}
}