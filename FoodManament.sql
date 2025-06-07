-- ========================
-- RESET DATABASE
-- ========================
USE master
GO

IF DB_ID('grocery_storee') IS NOT NULL
BEGIN
    ALTER DATABASE grocery_storee SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
END
DROP DATABASE IF EXISTS grocery_storee;
CREATE DATABASE grocery_storee;
GO

USE grocery_storee;
GO

-- ========================
-- 1. Bảng LOẠI SẢN PHẨM
-- ========================
CREATE TABLE LOAISP (
    MaLoai VARCHAR(10) PRIMARY KEY,
    TenLoai NVARCHAR(100) NOT NULL
);

-- ========================
-- 2. Bảng NHÀ CUNG CẤP
-- ========================
CREATE TABLE NHACUNGCAP (
    MaNCC VARCHAR(10) PRIMARY KEY,
    TenNCC NVARCHAR(100) NOT NULL,
    DiaChi NVARCHAR(MAX),
    SoDienThoai VARCHAR(20)
);

-- ========================
-- 3. Bảng ĐƠN VỊ TÍNH
-- ========================
CREATE TABLE DONVITINH (
    MaDVT VARCHAR(10) PRIMARY KEY,
    TenDVT NVARCHAR(50)
);

-- ========================
-- 4. Bảng SẢN PHẨM
-- ========================
CREATE TABLE SANPHAM (
    MaSP VARCHAR(10) PRIMARY KEY,
    TenSP NVARCHAR(100) NOT NULL,
    MaLoai VARCHAR(10),
    MaNCC VARCHAR(10),
    MaDVT VARCHAR(10),
    SoLuongTon INT CHECK (SoLuongTon >= 0) DEFAULT 0,
    DonGia DECIMAL(10,2) CHECK (DonGia >= 0),
    HanSuDung DATE,
    FOREIGN KEY (MaLoai) REFERENCES LOAISP(MaLoai),
    FOREIGN KEY (MaNCC) REFERENCES NHACUNGCAP(MaNCC),
    FOREIGN KEY (MaDVT) REFERENCES DONVITINH(MaDVT)
);

-- ========================
-- 5. Bảng NHÂN VIÊN
-- ========================
CREATE TABLE NHANVIEN (
    MaNV VARCHAR(10) PRIMARY KEY,
    TenNV NVARCHAR(100),
    SoDienThoai VARCHAR(20),
    ChucVu NVARCHAR(50)
);

-- ========================
-- 6. Bảng KHÁCH HÀNG
-- ========================
CREATE TABLE KHACHHANG (
    MaKH VARCHAR(10) PRIMARY KEY,
    TenKH NVARCHAR(100),
    SoDienThoai VARCHAR(20),
    DiemTichLuy INT CHECK (DiemTichLuy >= 0) DEFAULT 0
);

-- ========================
-- 7. Bảng TÀI KHOẢN
-- ========================
CREATE TABLE TAIKHOAN (
    TenDangNhap VARCHAR(50) PRIMARY KEY,
    MatKhau VARCHAR(255),
    MaNV VARCHAR(10) UNIQUE,
    VaiTro VARCHAR(20),
    FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV)
);

-- ========================
-- 8. Bảng HÓA ĐƠN
-- ========================
CREATE TABLE HOADON (
    MaHD VARCHAR(10) PRIMARY KEY,
    NgayLap DATETIME,
    MaNV VARCHAR(10),
    MaKH VARCHAR(10),
    TongTien DECIMAL(10,2) CHECK (TongTien >= 0),
    FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV),
    FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH)
);

-- ========================
-- 9. Bảng CHI TIẾT HÓA ĐƠN
-- ========================
CREATE TABLE CHITIETHOADON (
    MaHD VARCHAR(10),
    MaSP VARCHAR(10),
    SoLuong INT CHECK (SoLuong > 0),
    DonGia DECIMAL(10,2) CHECK (DonGia >= 0),
    PRIMARY KEY (MaHD, MaSP),
    FOREIGN KEY (MaHD) REFERENCES HOADON(MaHD),
    FOREIGN KEY (MaSP) REFERENCES SANPHAM(MaSP)
);

-- ========================
-- 10. Bảng PHIẾU NHẬP
-- ========================
CREATE TABLE PHIEUNHAP (
    MaPN VARCHAR(10) PRIMARY KEY,
    NgayNhap DATETIME NOT NULL,
    MaNV VARCHAR(10),
    GhiChu NVARCHAR(MAX),
    FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV)
);

-- ========================
-- 11. Bảng CHI TIẾT PHIẾU NHẬP
-- ========================
CREATE TABLE CHITIETPHIEUNHAP (
    MaPN VARCHAR(10),
    MaSP VARCHAR(10),
    SoLuong INT CHECK (SoLuong > 0),
    DonGiaNhap DECIMAL(10,2) CHECK (DonGiaNhap >= 0),
    HanSuDung DATE,
    PRIMARY KEY (MaPN, MaSP),
    FOREIGN KEY (MaPN) REFERENCES PHIEUNHAP(MaPN),
    FOREIGN KEY (MaSP) REFERENCES SANPHAM(MaSP)
);

-- ========================
-- 12. Bảng KHUYẾN MÃI
-- ========================
CREATE TABLE KHUYENMAI (
    MaKM VARCHAR(10) PRIMARY KEY,
    TenKM NVARCHAR(100),
    MoTa NVARCHAR(MAX),
    NgayBatDau DATE,
    NgayKetThuc DATE,
    PhanTramGiam INT CHECK (PhanTramGiam >= 0 AND PhanTramGiam <= 100)
);

-- ========================
-- 13. Bảng SẢN PHẨM - KHUYẾN MÃI
-- ========================
CREATE TABLE SANPHAM_KHUYENMAI (
    MaKM VARCHAR(10),
    MaSP VARCHAR(10),
    PRIMARY KEY (MaKM, MaSP),
    FOREIGN KEY (MaKM) REFERENCES KHUYENMAI(MaKM),
    FOREIGN KEY (MaSP) REFERENCES SANPHAM(MaSP)
);

-- ========================
-- 14. Bảng ĐỊA CHỈ KHÁCH HÀNG
-- ========================
CREATE TABLE DIACHI_KH (
    MaDiaChi INT IDENTITY(1,1) PRIMARY KEY,
    MaKH VARCHAR(10),
    DiaChi NVARCHAR(MAX),
    LaMacDinh BIT DEFAULT 0,
    FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH)
);

-- ========================
-- 15. Bảng LỊCH SỬ TÍCH ĐIỂM
-- ========================
CREATE TABLE LICHSUTICHDIEM (
    MaLichSu INT IDENTITY(1,1) PRIMARY KEY,
    MaKH VARCHAR(10),
    NgayGiaoDich DATETIME,
    ThayDoi INT,
    LyDo NVARCHAR(MAX),
    FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH)
);

-- ========================
-- 16. Bảng NHẬT KÝ HOẠT ĐỘNG
-- ========================
CREATE TABLE NHATKYHOATDONG (
    MaNhatKy INT IDENTITY(1,1) PRIMARY KEY,
    TenDangNhap VARCHAR(50),
    ThoiGian DATETIME DEFAULT GETDATE(),
    HanhDong NVARCHAR(MAX),
    FOREIGN KEY (TenDangNhap) REFERENCES TAIKHOAN(TenDangNhap)
);

-- ========================
-- 17. Bảng PHIẾU XUẤT
-- ========================
CREATE TABLE PHIEUXUAT (
    MaPX VARCHAR(10) PRIMARY KEY,
    NgayXuat DATETIME NOT NULL,
    MaNV VARCHAR(10),
    LyDoXuat NVARCHAR(255), 
    GhiChu NVARCHAR(MAX),
    FOREIGN KEY (MaNV) REFERENCES NHANVIEN(MaNV)
);

CREATE TABLE CHITIETPHIEUXUAT (
    MaPX VARCHAR(10),
    MaSP VARCHAR(10),
    SoLuong INT CHECK (SoLuong > 0),
    DonGiaBan DECIMAL(10,2) CHECK (DonGiaBan >= 0),
    PRIMARY KEY (MaPX, MaSP),
    FOREIGN KEY (MaPX) REFERENCES PHIEUXUAT(MaPX),
    FOREIGN KEY (MaSP) REFERENCES SANPHAM(MaSP)
);

-- ========================
-- 18. Bảng PHIẾU ĐỔI TRẢ
-- ========================
CREATE TABLE PHIEUDOITRA (
    MaPhieu VARCHAR(10) PRIMARY KEY,
    MaHD VARCHAR(10), 
    MaKH VARCHAR(10), 
    NgayTra DATETIME NOT NULL,
    LyDo NVARCHAR(MAX),
    FOREIGN KEY (MaHD) REFERENCES HOADON(MaHD),
    FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH)
);

CREATE TABLE CHITIETDOITRA (
    MaPhieu VARCHAR(10),
    MaSP VARCHAR(10),
    SoLuong INT CHECK (SoLuong > 0),
    PRIMARY KEY (MaPhieu, MaSP),
    FOREIGN KEY (MaPhieu) REFERENCES PHIEUDOITRA(MaPhieu),
    FOREIGN KEY (MaSP) REFERENCES SANPHAM(MaSP)
);

-- ========================
-- DỮ LIỆU MẪU
-- ========================
-- Thêm nhân viên mẫu
INSERT INTO NHANVIEN (MaNV, TenNV, SoDienThoai, ChucVu)
VALUES ('NV001', N'Nguyễn Văn A', '0909123456', N'Quản lý');

-- Thêm tài khoản đăng nhập mẫu
INSERT INTO TAIKHOAN (TenDangNhap, MatKhau, MaNV, VaiTro)
VALUES ('admin', '123', 'NV001', 'admin');

select * from TAIKHOAN

INSERT INTO KHACHHANG (MaKH, TenKH, SoDienThoai, DiemTichLuy)
VALUES 
    ('KH0001', N'Nguyễn Văn An', '0912345678', 150),
    ('KH0002', N'Trần Thị Bình', '0923456789', 75),
    ('KH0003', N'Lê Văn Cường', '0934567890', 200),
    ('KH0004', N'Phạm Thị Dung', '0945678901', 50),
    ('KH0005', N'Hoàng Văn Em', '0956789012', 300),
    ('KH0006', N'Ngô Thị Phương', '0967890123', 120),
    ('KH0007', N'Đặng Văn Giang', '0978901234', 180),
    ('KH0008', N'Vũ Thị Hoa', '0989012345', 230),
    ('KH0009', N'Bùi Văn Kiên', '0990123456', 85),
    ('KH0010', N'Lý Thị Lan', '0901234567', 420);

	select * from KHACHHANG

	INSERT INTO KHACHHANG (MaKH, TenKH, SoDienThoai, DiemTichLuy) 
VALUES 
    ('KH0011', N'Nguyễn Văn B', '0912345679', 60),
    ('KH0012', N'Trần Thị C', '0923456790', 90),
    ('KH0013', N'Lê Văn D', '0934567891', 110),
    ('KH0014', N'Phạm Thị E', '0945678902', 130),
    ('KH0015', N'Hoàng Văn F', '0956789013', 150),
    ('KH0016', N'Ngô Thị G', '0967890124', 170),
    ('KH0017', N'Đặng Văn H', '0978901235', 190),
    ('KH0018', N'Vũ Thị I', '0989012346', 210),
    ('KH0019', N'Bùi Văn J', '0990123457', 230),
    ('KH0020', N'Lý Thị K', '0901234568', 250),
    ('KH0021', N'Nguyễn Văn L', '0912345680', 270),
    ('KH0022', N'Trần Thị M', '0923456791', 290),
    ('KH0023', N'Lê Văn N', '0934567892', 310),
    ('KH0024', N'Phạm Thị O', '0945678903', 330),
    ('KH0025', N'Hoàng Văn P', '0956789014', 350),
    ('KH0026', N'Ngô Thị Q', '0967890125', 370),
    ('KH0027', N'Đặng Văn R', '0978901236', 390),
    ('KH0028', N'Vũ Thị S', '0989012347', 410),
    ('KH0029', N'Bùi Văn T', '0990123458', 430),
    ('KH0030', N'Lý Thị U', '0901234569', 450),
    ('KH0031', N'Nguyễn Văn V', '0912345690', 470),
    ('KH0032', N'Trần Thị W', '0923456792', 490),
    ('KH0033', N'Lê Văn X', '0934567893', 510),
    ('KH0034', N'Phạm Thị Y', '0945678904', 530),
    ('KH0035', N'Hoàng Văn Z', '0956789015', 550),
    ('KH0036', N'Ngô Thị AA', '0967890126', 570),
    ('KH0037', N'Đặng Văn BB', '0978901237', 590),
    ('KH0038', N'Vũ Thị CC', '0989012348', 610),
    ('KH0039', N'Bùi Văn DD', '0990123459', 630),
    ('KH0040', N'Lý Thị EE', '0901234570', 650),
    ('KH0041', N'Nguyễn Văn FF', '0912345700', 670),
    ('KH0042', N'Trần Thị GG', '0923456793', 690),
    ('KH0043', N'Lê Văn HH', '0934567894', 710),
    ('KH0044', N'Phạm Thị II', '0945678905', 730),
    ('KH0045', N'Hoàng Văn JJ', '0956789016', 750),
    ('KH0046', N'Ngô Thị KK', '0967890127', 770),
    ('KH0047', N'Đặng Văn LL', '0978901238', 790),
    ('KH0048', N'Vũ Thị MM', '0989012349', 810),
    ('KH0049', N'Bùi Văn NN', '0990123460', 830),
    ('KH0050', N'Lý Thị OO', '0901234571', 850);



	-- Chèn dữ liệu vào bảng LOAISP (Loại sản phẩm)
INSERT INTO LOAISP (MaLoai, TenLoai) VALUES
    ('LSP001', N'Thực phẩm'),
    ('LSP002', N'Đồ uống'),
    ('LSP003', N'Gia dụng'),
    ('LSP004', N'Hóa mỹ phẩm'),
    ('LSP005', N'Đồ chơi');

-- Chèn dữ liệu vào bảng NHACUNGCAP (Nhà cung cấp)
INSERT INTO NHACUNGCAP (MaNCC, TenNCC, DiaChi, SoDienThoai) VALUES
    ('NCC001', N'Công ty Thực phẩm Sạch', N'123 Đường Láng, Hà Nội', '0909123456'),
    ('NCC002', N'Công ty Nước giải khát VN', N'456 Lê Lợi, TP.HCM', '0909123457'),
    ('NCC003', N'Công ty Gia dụng Minh Anh', N'789 Nguyễn Huệ, Đà Nẵng', '0909123458'),
    ('NCC004', N'Công ty Hóa mỹ phẩm Tốt', N'101 Trần Phú, Nha Trang', '0909123459'),
    ('NCC005', N'Công ty Đồ chơi Vui Vẻ', N'202 Hùng Vương, Hải Phòng', '0909123460');

-- Chèn dữ liệu vào bảng DONVITINH (Đơn vị tính)
INSERT INTO DONVITINH (MaDVT, TenDVT) VALUES
    ('DVT001', N'Hộp'),
    ('DVT002', N'Chai'),
    ('DVT003', N'Gói'),
    ('DVT004', N'Cái'),
    ('DVT005', N'Kg');


	CREATE TABLE PHANHOI (
    MaPH VARCHAR(10) PRIMARY KEY,
    MaKH VARCHAR(10),
    NoiDung NVARCHAR(500),
    NgayGui DATE,
    FOREIGN KEY (MaKH) REFERENCES KHACHHANG(MaKH)
);

EXEC sp_help 'SANPHAM';

UPDATE KHUYENMAI
SET GiaTriGiam = 10000
WHERE MaKM = 'KM001'; -- Thay MaKM bằng giá trị thực tế

INSERT INTO HOADON (MaHD, MaKH, NgayLap, TongTien) VALUES ('HD0001', 'KH0001', GETDATE(), 500000);
INSERT INTO CHITIETHD (MaHD, MaSP, SoLuong, DonGia) VALUES ('HD0001', 'SP0001', 2, 250000);
ALTER TABLE SANPHAM
ADD ImagePath NVARCHAR(255);

