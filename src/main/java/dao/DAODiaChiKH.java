package dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connect.ConnectDB;
import model.DiaChiKhachHang;

public class DAODiaChiKH {

	public boolean themDiaChi(DiaChiKhachHang diaChi) {
		var sql = "{call sp_ThemDiaChiKH(?, ?, ?)}";
		try (var conn = ConnectDB.getCon(); var cs = conn.prepareCall(sql)) {
			cs.setString(1, diaChi.getMaKH());
			cs.setString(2, diaChi.getDiaChi());
			cs.setBoolean(3, diaChi.isLaMacDinh());

			return cs.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean capNhatDiaChi(DiaChiKhachHang diaChi) {
		var sql = "{call sp_CapNhatDiaChiKH(?, ?, ?, ?)}";
		try (var conn = ConnectDB.getCon(); var cs = conn.prepareCall(sql)) {
			cs.setInt(1, diaChi.getMaDiaChi());
			cs.setString(2, diaChi.getMaKH());
			cs.setString(3, diaChi.getDiaChi());
			cs.setBoolean(4, diaChi.isLaMacDinh());

			return cs.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean xoaDiaChi(int maDiaChi) {
		var sql = "{call sp_XoaDiaChiKH(?)}";
		try (var conn = ConnectDB.getCon(); var cs = conn.prepareCall(sql)) {
			cs.setInt(1, maDiaChi);
			return cs.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<DiaChiKhachHang> getDiaChiTheoMaKH(String maKH) {
		List<DiaChiKhachHang> list = new ArrayList<>();
		var sql = "{call sp_LayDiaChiTheoMaKH(?)}";

		try (var conn = ConnectDB.getCon(); var cs = conn.prepareCall(sql)) {
			cs.setString(1, maKH);
			try (var rs = cs.executeQuery()) {
				while (rs.next()) {
					var diaChi = new DiaChiKhachHang();
					diaChi.setMaDiaChi(rs.getInt("MaDiaChi"));
					diaChi.setMaKH(rs.getString("MaKH"));
					diaChi.setDiaChi(rs.getString("DiaChi"));
					diaChi.setLaMacDinh(rs.getBoolean("LaMacDinh"));
					list.add(diaChi);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	public DiaChiKhachHang getDiaChiMacDinh(String maKH) {
		DiaChiKhachHang diaChi = null;
		var sql = "{call sp_LayDiaChiMacDinh(?)}";

		try (var conn = ConnectDB.getCon(); var cs = conn.prepareCall(sql)) {
			cs.setString(1, maKH);
			try (var rs = cs.executeQuery()) {
				if (rs.next()) {
					diaChi = new DiaChiKhachHang();
					diaChi.setMaDiaChi(rs.getInt("MaDiaChi"));
					diaChi.setMaKH(rs.getString("MaKH"));
					diaChi.setDiaChi(rs.getString("DiaChi"));
					diaChi.setLaMacDinh(rs.getBoolean("LaMacDinh"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return diaChi;
	}
}
