package dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import dao.ProductDAO.Category;
import dao.ProductDAO.Supplier;
import dao.ProductDAO.Unit;

public class ProductFormDialog extends JDialog {
	private JTextField txtMaSP, txtTenSP, txtDonGia, txtSoLuongTon, txtHanSuDung;
	private JComboBox<Category> cbMaLoai;
	private JComboBox<Supplier> cbMaNCC;
	private JComboBox<Unit> cbMaDVT;
	private JLabel lblImagePath, lblImagePreview;
	private JButton btnChooseImage, btnRemoveImage;
	private String imagePath;
	private boolean confirmed;
	private boolean isAddMode;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public ProductFormDialog(Frame parent, String title, String maSP, String tenSP, String tenLoai, String tenNCC,
			String tenDVT, String donGia, String soLuongTon, String hanSuDung, String imagePath,
			List<Category> categories, List<Supplier> suppliers, List<Unit> units, boolean isAddMode) {
		super(parent, title, true);
		this.isAddMode = isAddMode;
		this.imagePath = imagePath != null ? imagePath : "";
		this.confirmed = false;

		setLayout(new BorderLayout(10, 10));
		setSize(450, 650);
		setLocationRelativeTo(parent);

		var formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		formPanel.setBackground(new Color(245, 247, 250));
		var gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		var lblMaSP = createLabel("Mã SP:");
		txtMaSP = createTextField(maSP);
		txtMaSP.setEditable(isAddMode);
		lblMaSP.setToolTipText("Mã sản phẩm, không thể chỉnh sửa khi sửa sản phẩm");

		var lblTenSP = createLabel("Tên SP:");
		txtTenSP = createTextField(tenSP);
		lblTenSP.setToolTipText("Tên sản phẩm, bắt buộc");

		var lblMaLoai = createLabel("Loại SP:");
		cbMaLoai = new JComboBox<>();
		loadCategories(categories, tenLoai);
		lblMaLoai.setToolTipText("Chọn loại sản phẩm");

		var lblMaNCC = createLabel("Nhà CC:");
		cbMaNCC = new JComboBox<>();
		loadSuppliers(suppliers, tenNCC);
		lblMaNCC.setToolTipText("Chọn nhà cung cấp");

		var lblMaDVT = createLabel("ĐV Tính:");
		cbMaDVT = new JComboBox<>();
		loadUnits(units, tenDVT);
		lblMaDVT.setToolTipText("Chọn đơn vị tính");

		var lblDonGia = createLabel("Đơn giá:");
		txtDonGia = createTextField(donGia);
		lblDonGia.setToolTipText("Đơn giá, phải là số thực >= 0");

		var lblSoLuongTon = createLabel("Số lượng tồn:");
		txtSoLuongTon = createTextField(soLuongTon);
		lblSoLuongTon.setToolTipText("Số lượng tồn, phải là số nguyên >= 0");

		var lblHanSuDung = createLabel("Hạn sử dụng (YYYY-MM-DD):");
		txtHanSuDung = createTextField(hanSuDung);
		lblHanSuDung.setToolTipText("Hạn sử dụng, định dạng YYYY-MM-DD, để trống nếu không có");

		var lblImage = createLabel("Ảnh:");
		btnChooseImage = new JButton("Chọn ảnh");
		btnChooseImage.setFont(new Font("Arial", Font.PLAIN, 14));
		btnChooseImage.setBackground(new Color(33, 150, 243));
		btnChooseImage.setForeground(Color.WHITE);
		btnChooseImage.setFocusPainted(false);
		btnChooseImage.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		addHoverEffect(btnChooseImage, new Color(25, 118, 210));
		btnChooseImage.addActionListener(e -> chooseImage());

		btnRemoveImage = new JButton("Xóa ảnh");
		btnRemoveImage.setFont(new Font("Arial", Font.PLAIN, 14));
		btnRemoveImage.setBackground(new Color(244, 67, 54));
		btnRemoveImage.setForeground(Color.WHITE);
		btnRemoveImage.setFocusPainted(false);
		btnRemoveImage.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		addHoverEffect(btnRemoveImage, new Color(211, 47, 47));
		btnRemoveImage.addActionListener(e -> removeImage());

		lblImagePath = new JLabel(this.imagePath.isEmpty() ? "Chưa chọn ảnh" : this.imagePath);
		lblImagePath.setFont(new Font("Arial", Font.PLAIN, 14));
		lblImagePath.setForeground(new Color(33, 37, 41));

		lblImagePreview = new JLabel();
		lblImagePreview.setHorizontalAlignment(JLabel.CENTER);
		lblImagePreview.setPreferredSize(new Dimension(100, 100));
		lblImagePreview.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220)));
		updateImagePreview();

		gbc.gridx = 0;
		gbc.gridy = 0;
		formPanel.add(lblMaSP, gbc);
		gbc.gridx = 1;
		formPanel.add(txtMaSP, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		formPanel.add(lblTenSP, gbc);
		gbc.gridx = 1;
		formPanel.add(txtTenSP, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		formPanel.add(lblMaLoai, gbc);
		gbc.gridx = 1;
		formPanel.add(cbMaLoai, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		formPanel.add(lblMaNCC, gbc);
		gbc.gridx = 1;
		formPanel.add(cbMaNCC, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		formPanel.add(lblMaDVT, gbc);
		gbc.gridx = 1;
		formPanel.add(cbMaDVT, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		formPanel.add(lblDonGia, gbc);
		gbc.gridx = 1;
		formPanel.add(txtDonGia, gbc);

		gbc.gridx = 0;
		gbc.gridy = 6;
		formPanel.add(lblSoLuongTon, gbc);
		gbc.gridx = 1;
		formPanel.add(txtSoLuongTon, gbc);

		gbc.gridx = 0;
		gbc.gridy = 7;
		formPanel.add(lblHanSuDung, gbc);
		gbc.gridx = 1;
		formPanel.add(txtHanSuDung, gbc);

		gbc.gridx = 0;
		gbc.gridy = 8;
		formPanel.add(lblImage, gbc);
		gbc.gridx = 1;
		var imageButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		imageButtonPanel.setBackground(new Color(245, 247, 250));
		imageButtonPanel.add(btnChooseImage);
		imageButtonPanel.add(btnRemoveImage);
		formPanel.add(imageButtonPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.gridwidth = 2;
		formPanel.add(lblImagePath, gbc);

		gbc.gridx = 0;
		gbc.gridy = 10;
		gbc.gridwidth = 2;
		formPanel.add(lblImagePreview, gbc);

		add(formPanel, BorderLayout.CENTER);

		var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		buttonPanel.setBackground(new Color(245, 247, 250));

		var btnOK = new JButton("OK");
		btnOK.setFont(new Font("Arial", Font.BOLD, 14));
		btnOK.setBackground(new Color(76, 175, 80));
		btnOK.setForeground(Color.WHITE);
		btnOK.setFocusPainted(false);
		btnOK.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		addHoverEffect(btnOK, new Color(56, 142, 60));
		btnOK.addActionListener(e -> onOK());

		var btnCancel = new JButton("Hủy");
		btnCancel.setFont(new Font("Arial", Font.BOLD, 14));
		btnCancel.setBackground(new Color(244, 67, 54));
		btnCancel.setForeground(Color.WHITE);
		btnCancel.setFocusPainted(false);
		btnCancel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		addHoverEffect(btnCancel, new Color(211, 47, 47));
		btnCancel.addActionListener(e -> onCancel());

		buttonPanel.add(btnOK);
		buttonPanel.add(btnCancel);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private JLabel createLabel(String text) {
		var label = new JLabel(text);
		label.setFont(new Font("Arial", Font.PLAIN, 14));
		label.setForeground(new Color(33, 37, 41));
		return label;
	}

	private JTextField createTextField(String text) {
		var textField = new JTextField(text, 20);
		textField.setFont(new Font("Arial", Font.PLAIN, 14));
		textField.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 210, 220), 1, true),
						BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		return textField;
	}

	private void addHoverEffect(JButton btn, Color hoverColor) {
		var originalColor = btn.getBackground();
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				btn.setBackground(hoverColor);
				btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				btn.setBackground(originalColor);
			}
		});
	}

	private void loadCategories(List<Category> categories, String selectedTenLoai) {
		cbMaLoai.removeAllItems();
		if (categories == null || categories.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Không có loại sản phẩm nào trong cơ sở dữ liệu!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			cbMaLoai.setEnabled(false);
			return;
		}
		cbMaLoai.setEnabled(true);
		for (Category category : categories) {
			cbMaLoai.addItem(category);
			if (selectedTenLoai != null && selectedTenLoai.equals(category.getTenLoai())) {
				cbMaLoai.setSelectedItem(category);
			}
		}
	}

	private void loadSuppliers(List<Supplier> suppliers, String selectedTenNCC) {
		cbMaNCC.removeAllItems();
		if (suppliers == null || suppliers.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Không có nhà cung cấp nào trong cơ sở dữ liệu!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			cbMaNCC.setEnabled(false);
			return;
		}
		cbMaNCC.setEnabled(true);
		for (Supplier supplier : suppliers) {
			cbMaNCC.addItem(supplier);
			if (selectedTenNCC != null && selectedTenNCC.equals(supplier.getTenNCC())) {
				cbMaNCC.setSelectedItem(supplier);
			}
		}
	}

	private void loadUnits(List<Unit> units, String selectedTenDVT) {
		cbMaDVT.removeAllItems();
		if (units == null || units.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Không có đơn vị tính nào trong cơ sở dữ liệu!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			cbMaDVT.setEnabled(false);
			return;
		}
		cbMaDVT.setEnabled(true);
		for (Unit unit : units) {
			cbMaDVT.addItem(unit);
			if (selectedTenDVT != null && selectedTenDVT.equals(unit.getTenDVT())) {
				cbMaDVT.setSelectedItem(unit);
			}
		}
	}

	private void chooseImage() {
		var fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Chọn ảnh sản phẩm");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		var filter = new FileNameExtensionFilter("Image files", "jpg", "png");
		fileChooser.setFileFilter(filter);

		var result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			var selectedFile = fileChooser.getSelectedFile();
			var extension = getFileExtension(selectedFile).toLowerCase();
			if (!extension.equals("jpg") && !extension.equals("png")) {
				JOptionPane.showMessageDialog(this, "Chỉ hỗ trợ file ảnh định dạng JPG hoặc PNG!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				imagePath = saveImage(selectedFile.getAbsolutePath());
				lblImagePath.setText(imagePath);
				updateImagePreview();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Lỗi khi lưu ảnh: " + e.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private String saveImage(String sourcePath) throws IOException {
		if (sourcePath == null || sourcePath.isEmpty()) {
			return "";
		}
		var sourceFile = new File(sourcePath);
		var destDir = new File("images/products");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		var destFile = new File(destDir, sourceFile.getName());
		Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return destFile.getName(); // Lưu tên file thay vì đường dẫn tuyệt đối
	}

	private void removeImage() {
		imagePath = "";
		lblImagePath.setText("Chưa chọn ảnh");
		lblImagePreview.setIcon(null);
	}

	private void updateImagePreview() {
		if (imagePath == null || imagePath.isEmpty()) {
			lblImagePreview.setIcon(null);
			return;
		}
		try {
			var icon = new ImageIcon("images/products/" + imagePath);
			if (icon.getImage() != null) {
				var image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
				lblImagePreview.setIcon(new ImageIcon(image));
			} else {
				lblImagePreview.setIcon(null);
			}
		} catch (Exception e) {
			System.err.println("Error loading image preview: " + imagePath);
			lblImagePreview.setIcon(null);
		}
	}

	private String getFileExtension(File file) {
		var name = file.getName();
		var lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return "";
		}
		return name.substring(lastIndexOf + 1);
	}

	private void onOK() {
		if (txtTenSP.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (cbMaLoai.isEnabled() && cbMaLoai.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn loại sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (cbMaNCC.isEnabled() && cbMaNCC.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (cbMaDVT.isEnabled() && cbMaDVT.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn vị tính!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			var donGia = Double.parseDouble(txtDonGia.getText().trim());
			if (donGia < 0) {
				JOptionPane.showMessageDialog(this, "Đơn giá phải >= 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Đơn giá phải là số thực hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			var soLuongTon = Integer.parseInt(txtSoLuongTon.getText().trim());
			if (soLuongTon < 0) {
				JOptionPane.showMessageDialog(this, "Số lượng tồn phải >= 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Số lượng tồn phải là số nguyên hợp lệ!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		var hanSuDung = txtHanSuDung.getText().trim();
		if (!hanSuDung.isEmpty()) {
			try {
				DATE_FORMAT.parse(hanSuDung);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Hạn sử dụng phải có định dạng YYYY-MM-DD!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		confirmed = true;
		dispose();
	}

	private void onCancel() {
		confirmed = false;
		dispose();
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public String getMaSP() {
		return txtMaSP.getText().trim();
	}

	public String getTenSP() {
		return txtTenSP.getText().trim();
	}

	public String getMaLoai() {
		return cbMaLoai.isEnabled() && cbMaLoai.getSelectedItem() != null
				? ((Category) cbMaLoai.getSelectedItem()).getMaLoai()
				: "";
	}

	public String getMaNCC() {
		return cbMaNCC.isEnabled() && cbMaNCC.getSelectedItem() != null
				? ((Supplier) cbMaNCC.getSelectedItem()).getMaNCC()
				: "";
	}

	public String getMaDVT() {
		return cbMaDVT.isEnabled() && cbMaDVT.getSelectedItem() != null ? ((Unit) cbMaDVT.getSelectedItem()).getMaDVT()
				: "";
	}

	public String getDonGia() {
		return txtDonGia.getText().trim();
	}

	public String getSoLuongTon() {
		return txtSoLuongTon.getText().trim();
	}

	public String getHanSuDung() {
		return txtHanSuDung.getText().trim();
	}

	public String getImagePath() {
		return imagePath;
	}
}