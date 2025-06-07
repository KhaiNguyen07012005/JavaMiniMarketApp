package model;

import java.util.Date;

public class ChiTietPhieuNhap {
	private String maPN;
	private String maSP;
	private int soLuong;
	private double donGiaNhap;
	private Date hanSuDung;

	public String getMaPN() {
		return maPN;
	}

	public void setMaPN(String maPN) {
		this.maPN = maPN;
	}

	public String getMaSP() {
		return maSP;
	}

	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public double getDonGiaNhap() {
		return donGiaNhap;
	}

	public void setDonGiaNhap(double donGiaNhap) {
		this.donGiaNhap = donGiaNhap;
	}

	public Date getHanSuDung() {
		return hanSuDung;
	}

	public void setHanSuDung(Date hanSuDung) {
		this.hanSuDung = hanSuDung;
	}
}
