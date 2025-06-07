package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.List;

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
import dao.ProductDAO;
import dao.ProductDAO.Category;
import dao.ProductDAO.Supplier;
import dao.ProductDAO.Unit;
import dialogs.ProductFormDialog;

public class ProductPanel extends JPanel {
	private JTable table;
	private DefaultTableModel tableModel;
	private int currentPage = 1;
	private int rowsPerPage = 10;
	private int totalRows = 0;
	private int totalPages = 1;
	private JLabel pageLabel;
	private JTextField searchField;
	private JButton btnSearch, btnClearSearch, btnAdd;
	private JComboBox<String> pageSizeComboBox;
	private ProductDAO productDAO;
	private List<Category> categories;
	private List<Supplier> suppliers;
	private List<Unit> units;

	public ProductPanel() {
		productDAO = new ProductDAO();
		loadReferenceData();
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.WHITE);
		initializeComponents();
	}

	private void loadReferenceData() {
		try {
			categories = productDAO.getCategories();
			suppliers = productDAO.getSuppliers();
			units = productDAO.getUnits();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu tham chiếu: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void initializeComponents() {
		var title = new JLabel("Quản Lý Sản Phẩm", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 26));
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
		searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));

		searchField = new JTextField(25);
		searchField.setFont(new Font("Arial", Font.PLAIN, 14));
		btnSearch = new JButton("Tìm kiếm", loadIcon("/icons/search.png"));
		btnClearSearch = new JButton("Xóa tìm kiếm", loadIcon("/icons/clear.png"));

		btnSearch.setFont(new Font("Arial", Font.BOLD, 14));
		btnClearSearch.setFont(new Font("Arial", Font.BOLD, 14));
		btnSearch.setBackground(new Color(0, 120, 215));
		btnSearch.setForeground(Color.WHITE);
		btnClearSearch.setBackground(new Color(200, 60, 60));
		btnClearSearch.setForeground(Color.WHITE);
		btnSearch.setFocusPainted(false);
		btnClearSearch.setFocusPainted(false);

		addHoverEffect(btnSearch, new Color(0, 100, 180));
		addHoverEffect(btnClearSearch, new Color(170, 40, 40));

		searchPanel.add(searchLabel);
		searchPanel.add(searchField);
		searchPanel.add(btnSearch);
		searchPanel.add(btnClearSearch);

		contentPanel.add(searchPanel, BorderLayout.NORTH);

		var addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
		addButtonPanel.setBackground(Color.WHITE);

		btnAdd = new JButton("Thêm sản phẩm mới", loadIcon("/icons/add.png"));
		btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
		btnAdd.setBackground(new Color(0, 150, 70));
		btnAdd.setForeground(Color.WHITE);
		btnAdd.setFocusPainted(false);
		addHoverEffect(btnAdd, new Color(0, 130, 60));

		addButtonPanel.add(btnAdd);
		contentPanel.add(addButtonPanel, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.PAGE_START);

		String[] columns = { "Mã SP", "Tên SP", "Loại", "Nhà CC", "ĐV Tính", "Đơn giá", "Số lượng tồn", "Hạn sử dụng",
				"Thao tác" };
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(tableModel);
		table.setRowHeight(40);
		table.setFont(new Font("Arial", Font.PLAIN, 14));

		var header = table.getTableHeader();
		header.setFont(new Font("Arial", Font.BOLD, 15));
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

		TableColumn column;
		column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(80);
		column = table.getColumnModel().getColumn(1);
		column.setPreferredWidth(200);
		column = table.getColumnModel().getColumn(2);
		column.setPreferredWidth(100);
		column = table.getColumnModel().getColumn(3);
		column.setPreferredWidth(120);
		column = table.getColumnModel().getColumn(4);
		column.setPreferredWidth(80);
		column = table.getColumnModel().getColumn(5);
		column.setPreferredWidth(100);
		column = table.getColumnModel().getColumn(6);
		column.setPreferredWidth(100);
		column = table.getColumnModel().getColumn(7);
		column.setPreferredWidth(100);
		column = table.getColumnModel().getColumn(8);
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
		pageLabel.setFont(new Font("Arial", Font.PLAIN, 14));

		String[] pageSizeOptions = { "10", "20", "50", "100" };
		pageSizeComboBox = new JComboBox<>(pageSizeOptions);
		pageSizeComboBox.setSelectedItem(String.valueOf(rowsPerPage));
		pageSizeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
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
			searchField.setText("");
			currentPage = 1;
			loadData();
		});

		pageSizeComboBox.addActionListener(e -> {
			rowsPerPage = Integer.parseInt((String) pageSizeComboBox.getSelectedItem());
			currentPage = 1;
			loadData();
		});

		btnAdd.addActionListener(e -> onAdd());

		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				var row = table.rowAtPoint(e.getPoint());
				var col = table.columnAtPoint(e.getPoint());

				if (row >= 0 && col == 8) {
					var maSP = (String) tableModel.getValueAt(row, 0);
					var tenSP = (String) tableModel.getValueAt(row, 1);
					var tenLoai = (String) tableModel.getValueAt(row, 2);
					var tenNCC = (String) tableModel.getValueAt(row, 3);
					var tenDVT = (String) tableModel.getValueAt(row, 4);
					var donGia = tableModel.getValueAt(row, 5).toString();
					var soLuongTon = tableModel.getValueAt(row, 6).toString();
					var hanSuDung = tableModel.getValueAt(row, 7).toString();

					var x = e.getX() - table.getCellRect(row, col, false).x;
					if (x < 60) {
						editProduct(maSP, tenSP, tenLoai, tenNCC, tenDVT, donGia, soLuongTon, hanSuDung);
					} else {
						deleteProduct(maSP);
					}
				}
			}
		});

		loadData();
	}

	private JButton createPageButton(String text) {
		var btn = new JButton(text);
		btn.setFont(new Font("Arial", Font.BOLD, 13));
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
			var icon = new ImageIcon(getClass().getResource(path));
			if (icon.getImage() == null) {
				System.err.println("Icon not found: " + path);
				return null;
			}
			return icon;
		} catch (Exception e) {
			System.err.println("Error loading icon: " + path + " - " + e.getMessage());
			return null;
		}
	}

	private void loadData() {
		try (var conn = ConnectDB.getCon()) {
			var countSql = "SELECT COUNT(*) FROM SANPHAM";
			var searchKeyword = searchField.getText().trim();

			if (!searchKeyword.isEmpty()) {
				countSql = "SELECT COUNT(*) FROM SANPHAM WHERE TenSP LIKE ?";
			}

			try (var countStmt = conn.prepareStatement(countSql)) {
				if (!searchKeyword.isEmpty()) {
					countStmt.setString(1, "%" + searchKeyword + "%");
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
				sql = """
						SELECT sp.MaSP, sp.TenSP, lsp.TenLoai, ncc.TenNCC, dvt.TenDVT, sp.DonGia, sp.SoLuongTon, sp.HanSuDung
						FROM SANPHAM sp
						LEFT JOIN LOAISP lsp ON sp.MaLoai = lsp.MaLoai
						LEFT JOIN NHACUNGCAP ncc ON sp.MaNCC = ncc.MaNCC
						LEFT JOIN DONVITINH dvt ON sp.MaDVT = dvt.MaDVT
						ORDER BY sp.MaSP
						OFFSET ? ROWS FETCH NEXT ? ROWS ONLY""";
			} else {
				sql = """
						SELECT sp.MaSP, sp.TenSP, lsp.TenLoai, ncc.TenNCC, dvt.TenDVT, sp.DonGia, sp.SoLuongTon, sp.HanSuDung
						FROM SANPHAM sp
						LEFT JOIN LOAISP lsp ON sp.MaLoai = lsp.MaLoai
						LEFT JOIN NHACUNGCAP ncc ON sp.MaNCC = ncc.MaNCC
						LEFT JOIN DONVITINH dvt ON sp.MaDVT = dvt.MaDVT
						WHERE sp.TenSP LIKE ?
						ORDER BY sp.MaSP
						OFFSET ? ROWS FETCH NEXT ? ROWS ONLY""";
			}

			try (var stmt = conn.prepareStatement(sql)) {
				if (searchKeyword.isEmpty()) {
					stmt.setInt(1, (currentPage - 1) * rowsPerPage);
					stmt.setInt(2, rowsPerPage);
				} else {
					stmt.setString(1, "%" + searchKeyword + "%");
					stmt.setInt(2, (currentPage - 1) * rowsPerPage);
					stmt.setInt(3, rowsPerPage);
				}

				var rs = stmt.executeQuery();
				tableModel.setRowCount(0);

				while (rs.next()) {
					var maSP = rs.getString("MaSP");
					var tenSP = rs.getString("TenSP");
					var tenLoai = rs.getString("TenLoai") != null ? rs.getString("TenLoai") : "Không xác định";
					var tenNCC = rs.getString("TenNCC") != null ? rs.getString("TenNCC") : "Không xác định";
					var tenDVT = rs.getString("TenDVT") != null ? rs.getString("TenDVT") : "Không xác định";
					var donGia = String.format("%.2f", rs.getDouble("DonGia"));
					var soLuongTon = String.valueOf(rs.getInt("SoLuongTon"));
					var hanSuDung = rs.getDate("HanSuDung") != null ? rs.getDate("HanSuDung").toString() : "";

					tableModel.addRow(new Object[] { maSP, tenSP, tenLoai, tenNCC, tenDVT, donGia, soLuongTon,
							hanSuDung, "Sửa | Xóa" });
				}

				table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
					@Override
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						var panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
						panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

						var btnEdit = new JButton("Sửa");
						btnEdit.setFont(new Font("Arial", Font.BOLD, 12));
						btnEdit.setBackground(new Color(0, 120, 215));
						btnEdit.setForeground(Color.WHITE);
						btnEdit.setFocusPainted(false);
						var editIcon = loadIcon("/icons/edit.png");
						if (editIcon != null) {
							btnEdit.setIcon(editIcon);
						}

						var btnDelete = new JButton("Xóa");
						btnDelete.setFont(new Font("Arial", Font.BOLD, 12));
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
					JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm phù hợp với từ khóa: " + searchKeyword,
							"Thông báo", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu sản phẩm: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onAdd() {
		if (categories == null || suppliers == null || units == null) {
			JOptionPane.showMessageDialog(this,
					"Không thể tải dữ liệu tham chiếu. Vui lòng kiểm tra kết nối cơ sở dữ liệu.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		var dialog = new ProductFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm sản phẩm", "", "", "",
				"", "", "", "", "", "", categories, suppliers, units, true);
		dialog.setVisible(true);

		if (dialog.isConfirmed()) {
			try (var conn = ConnectDB.getCon()) {
				var sql = "INSERT INTO SANPHAM (MaSP, TenSP, MaLoai, MaNCC, MaDVT, DonGia, SoLuongTon, HanSuDung) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
				try (var pst = conn.prepareStatement(sql)) {
					pst.setString(1, dialog.getMaSP());
					pst.setString(2, dialog.getTenSP());
					pst.setString(3, dialog.getMaLoai());
					pst.setString(4, dialog.getMaNCC());
					pst.setString(5, dialog.getMaDVT());
					pst.setDouble(6, Double.parseDouble(dialog.getDonGia()));
					pst.setInt(7, Integer.parseInt(dialog.getSoLuongTon()));
					var hanSuDung = dialog.getHanSuDung();
					if (hanSuDung.isEmpty()) {
						pst.setNull(8, java.sql.Types.DATE);
					} else {
						pst.setDate(8, java.sql.Date.valueOf(hanSuDung));
					}
					pst.executeUpdate();

					JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!", "Thông báo",
							JOptionPane.INFORMATION_MESSAGE);
					loadData();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Không thể thêm sản phẩm: " + ex.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void editProduct(String maSP, String tenSP, String tenLoai, String tenNCC, String tenDVT, String donGia,
			String soLuongTon, String hanSuDung) {
		if (categories == null || suppliers == null || units == null) {
			JOptionPane.showMessageDialog(this,
					"Không thể tải dữ liệu tham chiếu. Vui lòng kiểm tra kết nối cơ sở dữ liệu.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		var dialog = new ProductFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa sản phẩm", maSP, tenSP,
				tenLoai, tenNCC, tenDVT, donGia, soLuongTon, hanSuDung, "", categories, suppliers, units, false);
		dialog.setVisible(true);

		if (dialog.isConfirmed()) {
			try (var conn = ConnectDB.getCon()) {
				var sql = "UPDATE SANPHAM SET TenSP = ?, MaLoai = ?, MaNCC = ?, MaDVT = ?, DonGia = ?, SoLuongTon = ?, HanSuDung = ? WHERE MaSP = ?";
				try (var pst = conn.prepareStatement(sql)) {
					pst.setString(1, dialog.getTenSP());
					pst.setString(2, dialog.getMaLoai());
					pst.setString(3, dialog.getMaNCC());
					pst.setString(4, dialog.getMaDVT());
					pst.setDouble(5, Double.parseDouble(dialog.getDonGia()));
					pst.setInt(6, Integer.parseInt(dialog.getSoLuongTon()));
					var hanSuDungNew = dialog.getHanSuDung();
					if (hanSuDungNew.isEmpty()) {
						pst.setNull(7, java.sql.Types.DATE);
					} else {
						pst.setDate(7, java.sql.Date.valueOf(hanSuDungNew));
					}
					pst.setString(8, dialog.getMaSP());

					var rowsAffected = pst.executeUpdate();
					if (rowsAffected > 0) {
						JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!", "Thông báo",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm cần cập nhật!", "Thông báo",
								JOptionPane.WARNING_MESSAGE);
					}
					loadData();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Không thể cập nhật sản phẩm: " + ex.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void deleteProduct(String maSP) {
		var confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa sản phẩm này?", "Xác nhận xóa",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (confirm == JOptionPane.YES_OPTION) {
			try (var conn = ConnectDB.getCon()) {
				var sql = "DELETE FROM SANPHAM WHERE MaSP = ?";
				try (var pst = conn.prepareStatement(sql)) {
					pst.setString(1, maSP);
					var rowsAffected = pst.executeUpdate();
					if (rowsAffected > 0) {
						JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!", "Thông báo",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm cần xóa!", "Thông báo",
								JOptionPane.WARNING_MESSAGE);
					}
					loadData();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Không thể xóa sản phẩm: " + ex.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void onSearch() {
		var keyword = searchField.getText().trim();
		if (keyword.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		currentPage = 1;
		loadData();
	}
}