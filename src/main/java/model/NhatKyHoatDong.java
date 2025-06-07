package model;

import java.util.Date;

public class NhatKyHoatDong {
	private int maNhatKy;
	private String tenDangNhap;
	private Date thoiGian;
	private String hanhDong;

	public int getMaNhatKy() {
		return maNhatKy;
	}

	public void setMaNhatKy(int maNhatKy) {
		this.maNhatKy = maNhatKy;
	}

	public String getTenDangNhap() {
		return tenDangNhap;
	}

	public void setTenDangNhap(String tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
	}

	public Date getThoiGian() {
		return thoiGian;
	}

	public void setThoiGian(Date thoiGian) {
		this.thoiGian = thoiGian;
	}

	public String getHanhDong() {
		return hanhDong;
	}

	public void setHanhDong(String hanhDong) {
		this.hanhDong = hanhDong;
	}
}
