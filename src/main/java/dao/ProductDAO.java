package dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connect.ConnectDB;

public class ProductDAO {
	public static class Category {
		private String maLoai;
		private String tenLoai;

		public Category(String maLoai, String tenLoai) {
			this.maLoai = maLoai;
			this.tenLoai = tenLoai;
		}

		public String getMaLoai() {
			return maLoai;
		}

		public String getTenLoai() {
			return tenLoai;
		}
	}

	public static class Supplier {
		private String maNCC;
		private String tenNCC;

		public Supplier(String maNCC, String tenNCC) {
			this.maNCC = maNCC;
			this.tenNCC = tenNCC;
		}

		public String getMaNCC() {
			return maNCC;
		}

		public String getTenNCC() {
			return tenNCC;
		}
	}

	public static class Unit {
		private String maDVT;
		private String tenDVT;

		public Unit(String maDVT, String tenDVT) {
			this.maDVT = maDVT;
			this.tenDVT = tenDVT;
		}

		public String getMaDVT() {
			return maDVT;
		}

		public String getTenDVT() {
			return tenDVT;
		}
	}

	public List<Category> getCategories() throws SQLException {
		List<Category> categories = new ArrayList<>();
		var sql = "SELECT MaLoai, TenLoai FROM LOAISP";
		try (var conn = ConnectDB.getCon();
				var stmt = conn.prepareStatement(sql);
				var rs = stmt.executeQuery()) {
			while (rs.next()) {
				categories.add(new Category(rs.getString("MaLoai"), rs.getString("TenLoai")));
			}
		}
		return categories;
	}

	public List<Supplier> getSuppliers() throws SQLException {
		List<Supplier> suppliers = new ArrayList<>();
		var sql = "SELECT MaNCC, TenNCC FROM NHACUNGCAP";
		try (var conn = ConnectDB.getCon();
				var stmt = conn.prepareStatement(sql);
				var rs = stmt.executeQuery()) {
			while (rs.next()) {
				suppliers.add(new Supplier(rs.getString("MaNCC"), rs.getString("TenNCC")));
			}
		}
		return suppliers;
	}

	public List<Unit> getUnits() throws SQLException {
		List<Unit> units = new ArrayList<>();
		var sql = "SELECT MaDVT, TenDVT FROM DONVITINH";
		try (var conn = ConnectDB.getCon();
				var stmt = conn.prepareStatement(sql);
				var rs = stmt.executeQuery()) {
			while (rs.next()) {
				units.add(new Unit(rs.getString("MaDVT"), rs.getString("TenDVT")));
			}
		}
		return units;
	}
}