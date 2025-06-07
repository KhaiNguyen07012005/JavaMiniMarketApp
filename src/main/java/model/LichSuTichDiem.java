package model;

import java.util.Date;

public class LichSuTichDiem {
	private int maLichSu;
	private String maKH;
	private Date ngayGiaoDich;
	private int thayDoi;
	private String lyDo;

	public int getMaLichSu() {
		return maLichSu;
	}

	public void setMaLichSu(int maLichSu) {
		this.maLichSu = maLichSu;
	}

	public String getMaKH() {
		return maKH;
	}

	public void setMaKH(String maKH) {
		this.maKH = maKH;
	}

	public Date getNgayGiaoDich() {
		return ngayGiaoDich;
	}

	public void setNgayGiaoDich(Date ngayGiaoDich) {
		this.ngayGiaoDich = ngayGiaoDich;
	}

	public int getThayDoi() {
		return thayDoi;
	}

	public void setThayDoi(int thayDoi) {
		this.thayDoi = thayDoi;
	}

	public String getLyDo() {
		return lyDo;
	}

	public void setLyDo(String lyDo) {
		this.lyDo = lyDo;
	}
}
