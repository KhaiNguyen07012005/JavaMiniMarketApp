package model;

import java.util.Date;

public class KhuyenMai {
	private String maKM;
	private String tenKM;
	private String moTa;
	private Date ngayBatDau;
	private Date ngayKetThuc;
	private int phanTramGiam;

	public String getMaKM() {
		return maKM;
	}

	public void setMaKM(String maKM) {
		this.maKM = maKM;
	}

	public String getTenKM() {
		return tenKM;
	}

	public void setTenKM(String tenKM) {
		this.tenKM = tenKM;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public Date getNgayBatDau() {
		return ngayBatDau;
	}

	public void setNgayBatDau(Date ngayBatDau) {
		this.ngayBatDau = ngayBatDau;
	}

	public Date getNgayKetThuc() {
		return ngayKetThuc;
	}

	public void setNgayKetThuc(Date ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}

	public int getPhanTramGiam() {
		return phanTramGiam;
	}

	public void setPhanTramGiam(int phanTramGiam) {
		this.phanTramGiam = phanTramGiam;
	}
}
