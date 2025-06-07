package model;

import java.util.Date;

public class PhieuXuat {
	private String maPX;
	private Date ngayXuat;
	private String maNV;
	private String lyDoXuat;
	private String ghiChu;

	public String getMaPX() {
		return maPX;
	}

	public void setMaPX(String maPX) {
		this.maPX = maPX;
	}

	public Date getNgayXuat() {
		return ngayXuat;
	}

	public void setNgayXuat(Date ngayXuat) {
		this.ngayXuat = ngayXuat;
	}

	public String getMaNV() {
		return maNV;
	}

	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}

	public String getLyDoXuat() {
		return lyDoXuat;
	}

	public void setLyDoXuat(String lyDoXuat) {
		this.lyDoXuat = lyDoXuat;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}
}
