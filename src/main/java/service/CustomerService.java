package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

public class CustomerService {
	private Connection conn;
	private Map<String, Integer> cart = new HashMap<>();
	private String maKH;

	public CustomerService(Connection conn) {
		this.conn = conn;
	}

	// Setter cho maKH
	public void setMaKH(String maKH) {
		this.maKH = maKH;
	}

	// Getter cho Connection
	public Connection getConnection() {
		return conn;
	}

	// Đăng nhập
	public boolean loginCustomer(String username, String password) {
		try {
			var query = "SELECT * FROM TAIKHOAN WHERE TenDangNhap = ? AND MatKhau = ? AND VaiTro = 'Customer'";
			var stmt = conn.prepareStatement(query);
			stmt.setString(1, username);
			stmt.setString(2, password);
			var rs = stmt.executeQuery();
			if (rs.next()) {
				this.maKH = username;
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Đăng ký
	public String registerCustomer(String username, String password, String name, String address, String phone) {
		try {
			var checkQuery = "SELECT COUNT(*) FROM TAIKHOAN WHERE TenDangNhap = ?";
			var checkStmt = conn.prepareStatement(checkQuery);
			checkStmt.setString(1, username);
			var rs = checkStmt.executeQuery();
			rs.next();
			if (rs.getInt(1) > 0) {
				return "Tên đăng nhập đã tồn tại!";
			}

			var maKH = "KH" + String.format("%04d", getNextMaKH());
			var insertKHQuery = "INSERT INTO KHACHHANG (MaKH, TenKH, SoDienThoai, DiemTichLuy) VALUES (?, ?, ?, 0)";
			var khStmt = conn.prepareStatement(insertKHQuery);
			khStmt.setString(1, maKH);
			khStmt.setString(2, name);
			khStmt.setString(3, phone);
			khStmt.executeUpdate();

			var insertAddressQuery = "INSERT INTO DIACHI_KH (MaKH, DiaChi, LaMacDinh) VALUES (?, ?, 1)";
			var addressStmt = conn.prepareStatement(insertAddressQuery);
			addressStmt.setString(1, maKH);
			addressStmt.setString(2, address);
			addressStmt.executeUpdate();

			var insertTKQuery = "INSERT INTO TAIKHOAN (TenDangNhap, MatKhau, VaiTro) VALUES (?, ?, 'Customer')";
			var tkStmt = conn.prepareStatement(insertTKQuery);
			tkStmt.setString(1, username);
			tkStmt.setString(2, password);
			tkStmt.executeUpdate();

			return maKH;
		} catch (SQLException e) {
			e.printStackTrace();
			return "Lỗi cơ sở dữ liệu: " + e.getMessage();
		}
	}

	// Đặt lại mật khẩu
	public String resetPassword(String phone) {
		try {
			var query = "SELECT TenDangNhap FROM TAIKHOAN t JOIN KHACHHANG k ON t.TenDangNhap = k.MaKH WHERE k.SoDienThoai = ? AND t.VaiTro = 'Customer'";
			var stmt = conn.prepareStatement(query);
			stmt.setString(1, phone);
			var rs = stmt.executeQuery();
			if (!rs.next()) {
				return "SĐT không tồn tại!";
			}
			var otp = String.valueOf((int) (Math.random() * 9000) + 1000);
			System.out.println("Mã OTP (giả lập): " + otp);
			var input = JOptionPane.showInputDialog(null, "Nhập mã OTP (OTP: " + otp + "):", "Đặt lại mật khẩu",
					JOptionPane.PLAIN_MESSAGE);
			if (input != null && input.equals(otp)) {
				var updateQuery = "UPDATE TAIKHOAN SET MatKhau = ? WHERE TenDangNhap = ?";
				var updateStmt = conn.prepareStatement(updateQuery);
				updateStmt.setString(1, "newpassword123");
				updateStmt.setString(2, rs.getString("TenDangNhap"));
				updateStmt.executeUpdate();
				return "success";
			}
			return "Mã OTP không đúng!";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Lỗi cơ sở dữ liệu: " + e.getMessage();
		}
	}

	// Thêm vào giỏ hàng
	public boolean addToCart(String maSP, int quantity) {
		System.out.println("Attempting to add to cart: maSP=" + maSP + ", quantity=" + quantity + ", maKH=" + maKH);
		try {
			var query = "SELECT SoLuongTon FROM SANPHAM WHERE MaSP = ?";
			var stmt = conn.prepareStatement(query);
			stmt.setString(1, maSP);
			var rs = stmt.executeQuery();
			if (!rs.next()) {
				System.out.println("Product not found: " + maSP);
				return false;
			}
			var stock = rs.getInt("SoLuongTon");
			System.out.println("Stock for " + maSP + ": " + stock);
			if (stock >= quantity && quantity > 0) {
				cart.put(maSP, cart.getOrDefault(maSP, 0) + quantity);
				System.out.println("Added to cart: " + maSP + ", Quantity: " + quantity);
				return true;
			}
			System.out.println(
					"Failed to add: Insufficient stock (" + stock + ") or invalid quantity (" + quantity + ")");
			return false;
		} catch (SQLException e) {
			System.err.println("Error adding to cart: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	// Xóa khỏi giỏ hàng
	public void removeFromCart(String maSP) {
		cart.remove(maSP);
	}

	// Lấy giỏ hàng
	public List<Object[]> getCart() {
		List<Object[]> cartItems = new ArrayList<>();
		try {
			for (String maSP : cart.keySet()) {
				var query = "SELECT MaSP, TenSP, DonGia, ImagePath FROM SANPHAM WHERE MaSP = ?";
				var stmt = conn.prepareStatement(query);
				stmt.setString(1, maSP);
				var rs = stmt.executeQuery();
				if (rs.next()) {
					int quantity = cart.get(maSP);
					var price = rs.getDouble("DonGia");
					var total = price * quantity;
					// Added ImagePath to the returned array
					cartItems.add(new Object[] { maSP, rs.getString("TenSP"), quantity, price, total,
							rs.getString("ImagePath") != null ? rs.getString("ImagePath") : "" });
				}
			}
		} catch (SQLException e) {
			System.err.println("Error loading cart: " + e.getMessage());
			e.printStackTrace();
		}
		return cartItems;
	}

	// Thanh toán
	public String checkout(String maKH, String paymentMethod) {
		try {
			conn.setAutoCommit(false);
			if (cart.isEmpty()) {
				return "Giỏ hàng trống!";
			}

			var total = 0D;
			for (Object[] item : getCart()) {
				total += (double) item[4];
			}
			var discount = getDiscount();
			total -= discount;

			var maHD = "HD" + String.format("%04d", getNextMaHD());
			var insertHDQuery = "INSERT INTO HOADON (MaHD, MaKH, NgayLap, TongTien) VALUES (?, ?, GETDATE(), ?)";
			try (var hdStmt = conn.prepareStatement(insertHDQuery)) {
				hdStmt.setString(1, maHD);
				hdStmt.setString(2, maKH);
				hdStmt.setDouble(3, total);
				hdStmt.executeUpdate();
			}

			for (Object[] item : getCart()) {
				var maSP = (String) item[0];
				var quantity = (int) item[2];
				var price = (double) item[3];
				var insertCTHDQuery = "INSERT INTO CHITIETHOADON (MaHD, MaSP, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
				try (var cthdStmt = conn.prepareStatement(insertCTHDQuery)) {
					cthdStmt.setString(1, maHD);
					cthdStmt.setString(2, maSP);
					cthdStmt.setInt(3, quantity);
					cthdStmt.setDouble(4, price);
					cthdStmt.executeUpdate();
				}

				var updateStockQuery = "UPDATE SANPHAM SET SoLuongTon = SoLuongTon - ? WHERE MaSP = ?";
				try (var stockStmt = conn.prepareStatement(updateStockQuery)) {
					stockStmt.setInt(1, quantity);
					stockStmt.setString(2, maSP);
					stockStmt.executeUpdate();
				}
			}

			var points = (int) (total / 100000);
			var updatePointsQuery = "UPDATE KHACHHANG SET DiemTichLuy = DiemTichLuy + ? WHERE MaKH = ?";
			try (var pointsStmt = conn.prepareStatement(updatePointsQuery)) {
				pointsStmt.setInt(1, points);
				pointsStmt.setString(2, maKH);
				pointsStmt.executeUpdate();
			}

			cart.clear();
			conn.commit();
			return "Thanh toán thành công! Mã HD: " + maHD + (discount > 0 ? " (Giảm giá: " + discount + " VNĐ)" : "");
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
			e.printStackTrace();
			return "Lỗi: " + e.getMessage();
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// Lấy danh sách hóa đơn
	public List<Object[]> getInvoices(String maKH) {
		List<Object[]> invoices = new ArrayList<>();
		try {
			var query = "SELECT MaHD, NgayLap, TongTien FROM HOADON WHERE MaKH = ?";
			var stmt = conn.prepareStatement(query);
			stmt.setString(1, maKH);
			var rs = stmt.executeQuery();
			while (rs.next()) {
				invoices.add(new Object[] { rs.getString("MaHD"), rs.getDate("NgayLap"), rs.getDouble("TongTien") });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return invoices;
	}

	// Lấy chi tiết hóa đơn
	public List<Object[]> getInvoiceDetails(String maHD) {
		List<Object[]> details = new ArrayList<>();
		try {
			var query = "SELECT c.MaSP, s.TenSP, c.SoLuong, c.DonGia FROM CHITIETHOADON c JOIN SANPHAM s ON c.MaSP = s.MaSP WHERE MaHD = ?";
			var stmt = conn.prepareStatement(query);
			stmt.setString(1, maHD);
			var rs = stmt.executeQuery();
			while (rs.next()) {
				details.add(new Object[] { rs.getString("MaSP"), rs.getString("TenSP"), rs.getInt("SoLuong"),
						rs.getDouble("DonGia") });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return details;
	}

	// Lấy thông tin khách hàng
	public Object[] getCustomerInfo(String maKH) {
		try {
			var query = "SELECT k.MaKH, k.TenKH, k.SoDienThoai, k.DiemTichLuy, t.Email, k.GioiTinh, k.NgaySinh "
					+ "FROM KHACHHANG k LEFT JOIN TAIKHOAN t ON k.MaKH = t.TenDangNhap WHERE k.MaKH = ?";
			var stmt = conn.prepareStatement(query);
			stmt.setString(1, maKH);
			var rs = stmt.executeQuery();
			if (rs.next()) {
				return new Object[] { rs.getString("MaKH"), rs.getString("TenKH"), rs.getString("SoDienThoai"),
						rs.getInt("DiemTichLuy"), rs.getString("Email"), rs.getString("GioiTinh"),
						rs.getDate("NgaySinh") };
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean updateCustomerInfo(String maKH, String name, String phone, String address, String email,
			String gender, String dob) {
		try {
			conn.setAutoCommit(false);

			var updateKHQuery = "UPDATE KHACHHANG SET TenKH = ?, SoDienThoai = ?, GioiTinh = ?, NgaySinh = ? WHERE MaKH = ?";
			var khStmt = conn.prepareStatement(updateKHQuery);
			khStmt.setString(1, name);
			khStmt.setString(2, phone);
			khStmt.setString(3, gender);
			khStmt.setString(4, dob);
			khStmt.setString(5, maKH);
			khStmt.executeUpdate();

			// Cập nhật địa chỉ
			var updateAddressQuery = "UPDATE DIACHI_KH SET DiaChi = ? WHERE MaKH = ? AND LaMacDinh = 1";
			var addressStmt = conn.prepareStatement(updateAddressQuery);
			addressStmt.setString(1, address);
			addressStmt.setString(2, maKH);
			addressStmt.executeUpdate();

			// Cập nhật Email
			if (email != null && !email.isEmpty()) {
				var updateTKQuery = "UPDATE TAIKHOAN SET Email = ? WHERE TenDangNhap = ?";
				var tkStmt = conn.prepareStatement(updateTKQuery);
				tkStmt.setString(1, email);
				tkStmt.setString(2, maKH);
				tkStmt.executeUpdate();
			}

			conn.commit();
			return true;
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
			e.printStackTrace();
			return false;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// Đổi mật khẩu
	public boolean changePassword(String maKH, String newPassword) {
		try {
			var query = "UPDATE TAIKHOAN SET MatKhau = ? WHERE TenDangNhap = ?";
			var stmt = conn.prepareStatement(query);
			stmt.setString(1, newPassword);
			stmt.setString(2, maKH);
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Lấy danh sách khuyến mãi
	public List<Object[]> getPromotions() {
		List<Object[]> promotions = new ArrayList<>();
		try {
			var query = "SELECT MaKM, TenKM, GiaTriGiam, NgayBatDau, NgayKetThuc FROM KHUYENMAI WHERE GETDATE() BETWEEN NgayBatDau AND NgayKetThuc";
			var stmt = conn.prepareStatement(query);
			var rs = stmt.executeQuery();
			while (rs.next()) {
				promotions.add(new Object[] { rs.getString("MaKM"), rs.getString("TenKM"), rs.getDouble("GiaTriGiam"),
						rs.getDate("NgayBatDau"), rs.getDate("NgayKetThuc") });
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return promotions;
	}

	// Đổi điểm
	public boolean redeemPoints(String maKH, int points) {
		try {
			var query = "SELECT DiemTichLuy FROM KHACHHANG WHERE MaKH = ?";
			var stmt = conn.prepareStatement(query);
			stmt.setString(1, maKH);
			var rs = stmt.executeQuery();
			if (rs.next() && rs.getInt("DiemTichLuy") >= points) {
				var updateQuery = "UPDATE KHACHHANG SET DiemTichLuy = DiemTichLuy - ? WHERE MaKH = ?";
				var updateStmt = conn.prepareStatement(updateQuery);
				updateStmt.setInt(1, points);
				updateStmt.setString(2, maKH);
				updateStmt.executeUpdate();
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Gửi phản hồi
	public boolean sendFeedback(String maKH, String content) {
		try {
			var maPH = "PH" + String.format("%04d", getNextMaPH());
			var query = "INSERT INTO PHANHOI (MaPH, MaKH, NoiDung, NgayGui) VALUES (?, ?, ?, GETDATE())";
			var stmt = conn.prepareStatement(query);
			stmt.setString(1, maPH);
			stmt.setString(2, maKH);
			stmt.setString(3, content);
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Các phương thức hỗ trợ
	private int getNextMaKH() throws SQLException {
		var query = "SELECT MAX(CAST(SUBSTRING(MaKH, 3, LEN(MaKH)) AS INT)) FROM KHACHHANG";
		var stmt = conn.createStatement();
		var rs = stmt.executeQuery(query);
		return rs.next() ? rs.getInt(1) + 1 : 1;
	}

	private int getNextMaHD() throws SQLException {
		var query = "SELECT MAX(CAST(SUBSTRING(MaHD, 3, LEN(MaHD)) AS INT)) FROM HOADON";
		var stmt = conn.createStatement();
		var rs = stmt.executeQuery(query);
		return rs.next() ? rs.getInt(1) + 1 : 1;
	}

	private int getNextMaPH() throws SQLException {
		var query = "SELECT MAX(CAST(SUBSTRING(MaPH, 3, LEN(MaPH)) AS INT)) FROM PHANHOI";
		var stmt = conn.createStatement();
		var rs = stmt.executeQuery(query);
		return rs.next() ? rs.getInt(1) + 1 : 1;
	}

	public double getDiscount() {
		try {
			var query = "SELECT TOP 1 GiaTriGiam FROM KHUYENMAI WHERE GETDATE() BETWEEN NgayBatDau AND NgayKetThuc ORDER BY GiaTriGiam DESC";
			var stmt = conn.prepareStatement(query);
			var rs = stmt.executeQuery();
			return rs.next() ? rs.getDouble("GiaTriGiam") : 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public String authenticateCustomer(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	public String loginCustomer(String phone) {
		try {
			var query = "SELECT MaKH FROM KHACHHANG WHERE SoDienThoai = ?";
			var stmt = conn.prepareStatement(query);
			stmt.setString(1, phone);
			var rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString("MaKH");
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String registerCustomer(String name, String phone) {
		try {
			if (name == null || name.trim().isEmpty()) {
				return "Họ tên không được để trống!";
			}
			if (phone == null || phone.trim().isEmpty()) {
				return "Số điện thoại không được để trống!";
			}
			if (!phone.matches("\\d{10}")) {
				return "Số điện thoại phải có 10 chữ số!";
			}

			var checkQuery = "SELECT COUNT(*) FROM KHACHHANG WHERE SoDienThoai = ?";
			var checkStmt = conn.prepareStatement(checkQuery);
			checkStmt.setString(1, phone);
			var rs = checkStmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				return "Số điện thoại đã tồn tại!";
			}

			conn.setAutoCommit(false);

			var maKHQuery = "SELECT 'KH' + RIGHT('0000' + CAST(COALESCE(MAX(CAST(SUBSTRING(MaKH, 3, LEN(MaKH)) AS INT)), 0) + 1 AS VARCHAR(4)), 4) FROM KHACHHANG";
			var maKHStmt = conn.prepareStatement(maKHQuery);
			var maKHRs = maKHStmt.executeQuery();
			var maKH = "KH0001";
			if (maKHRs.next()) {
				maKH = maKHRs.getString(1);
			}

			var insertKHQuery = "INSERT INTO KHACHHANG (MaKH, TenKH, SoDienThoai, DiemTichLuy) VALUES (?, ?, ?, 0)";
			var khStmt = conn.prepareStatement(insertKHQuery);
			khStmt.setString(1, maKH);
			khStmt.setString(2, name);
			khStmt.setString(3, phone);
			var khRows = khStmt.executeUpdate();
			if (khRows != 1) {
				conn.rollback();
				return "Lỗi khi thêm khách hàng!";
			}

			conn.commit();
			return maKH;
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
			e.printStackTrace();
			return "Lỗi cơ sở dữ liệu: " + e.getMessage();
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isCustomer(String username) {
		try {
			var query = "SELECT VaiTro FROM TAIKHOAN WHERE TenDangNhap = ?";
			var stmt = conn.prepareStatement(query);
			stmt.setString(1, username);
			var rs = stmt.executeQuery();
			if (rs.next()) {
				return "Customer".equalsIgnoreCase(rs.getString("VaiTro"));
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}