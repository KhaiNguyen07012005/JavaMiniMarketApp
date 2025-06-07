package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KhachHangDAO {
	private Connection conn;

	public KhachHangDAO(Connection conn) {
		this.conn = conn;
	}

	public void themKhachHang(String maKH, String tenKH, String soDienThoai, String diaChi) throws SQLException {
		var sql = "{CALL sp_ThemKhachHang(?, ?, ?, ?)}";

		try (var stmt = conn.prepareCall(sql)) {
			stmt.setString(1, maKH);
			stmt.setString(2, tenKH);
			stmt.setString(3, soDienThoai);
			stmt.setString(4, diaChi);

			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void capNhatKhachHang(String maKH, String tenKH, String soDienThoai) throws SQLException {
		var sql = "{CALL sp_CapNhatKhachHang(?, ?, ?)}";

		try (var stmt = conn.prepareCall(sql)) {
			stmt.setString(1, maKH);
			stmt.setString(2, tenKH);
			stmt.setString(3, soDienThoai);

			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void xoaKhachHang(String maKH) throws SQLException {
		var sql = "{CALL sp_XoaKhachHang(?)}";

		try (var stmt = conn.prepareCall(sql)) {
			stmt.setString(1, maKH);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public ResultSet timKhachHang(String tuKhoa) throws SQLException {
		var sql = "{CALL sp_TimKhachHang(?)}";

		try (var stmt = conn.prepareCall(sql)) {
			stmt.setString(1, tuKhoa);
			return stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public ResultSet layDanhSachKhachHang() throws SQLException {
		var sql = "{CALL sp_LayDanhSachKhachHang()}";

		try (var stmt = conn.prepareCall(sql)) {
			return stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public ResultSet layDiaChiKhachHang(String maKH) throws SQLException {
		var sql = "{CALL sp_LayDiaChiKhachHang(?)}";

		try (var stmt = conn.prepareCall(sql)) {
			stmt.setString(1, maKH);
			return stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void themDiaChiKhachHang(String maKH, String diaChi, boolean laMacDinh) throws SQLException {
		var sql = "{CALL sp_ThemDiaChiKhachHang(?, ?, ?)}";

		try (var stmt = conn.prepareCall(sql)) {
			stmt.setString(1, maKH);
			stmt.setString(2, diaChi);
			stmt.setBoolean(3, laMacDinh);

			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void themDiaChiKhachHangMoi(String maKH, String diaChi, boolean laMacDinh) throws SQLException {
		var sql = "{CALL sp_ThemDiaChiKH(?, ?, ?)}";

		try (var stmt = conn.prepareCall(sql)) {
			stmt.setString(1, maKH);
			stmt.setString(2, diaChi);
			stmt.setBoolean(3, laMacDinh);

			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
