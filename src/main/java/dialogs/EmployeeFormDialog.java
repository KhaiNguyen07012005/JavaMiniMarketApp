package dialogs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class EmployeeFormDialog extends JDialog {
	private JTextField tfMaNV, tfTenNV, tfSoDienThoai;
	private JComboBox<String> cbChucVu;
	private JButton btnConfirm, btnCancel;
	private boolean confirmed = false;

	public EmployeeFormDialog(Frame parent, String title, String maNV, String tenNV, String soDienThoai, String chucVu,
			boolean isAddMode) {
		super(parent, title, true);
		setLayout(new GridLayout(5, 2, 10, 10));
		setSize(400, 250);
		setLocationRelativeTo(parent);
		getContentPane().setBackground(Color.WHITE);

		add(new JLabel("Mã nhân viên:"));
		tfMaNV = new JTextField(maNV);
		tfMaNV.setEditable(isAddMode);
		add(tfMaNV);

		add(new JLabel("Tên nhân viên:"));
		tfTenNV = new JTextField(tenNV);
		add(tfTenNV);

		add(new JLabel("Số điện thoại:"));
		tfSoDienThoai = new JTextField(soDienThoai);
		add(tfSoDienThoai);

		add(new JLabel("Chức vụ:"));
		cbChucVu = new JComboBox<>(new String[] { "Quản lý", "Nhân viên bán hàng", "Nhân viên kho", "Kế toán" });
		cbChucVu.setSelectedItem(chucVu.isEmpty() ? "Quản lý" : chucVu);
		add(cbChucVu);

		btnConfirm = new JButton(isAddMode ? "Thêm" : "Cập nhật");
		btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnConfirm.setBackground(new Color(0, 150, 70));
		btnConfirm.setForeground(Color.WHITE);
		btnConfirm.setFocusPainted(false);

		btnCancel = new JButton("Hủy");
		btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnCancel.setBackground(new Color(200, 60, 60));
		btnCancel.setForeground(Color.WHITE);
		btnCancel.setFocusPainted(false);

		add(btnConfirm);
		add(btnCancel);

		btnConfirm.addActionListener(e -> {
			if (tfMaNV.getText().trim().isEmpty() || tfTenNV.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Mã và tên nhân viên không được để trống!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			confirmed = true;
			setVisible(false);
		});

		btnCancel.addActionListener(e -> setVisible(false));

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				confirmed = false;
				setVisible(false);
			}
		});
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public String getMaNV() {
		return tfMaNV.getText().trim();
	}

	public String getTenNV() {
		return tfTenNV.getText().trim();
	}

	public String getSoDienThoai() {
		return tfSoDienThoai.getText().trim();
	}

	public String getChucVu() {
		return (String) cbChucVu.getSelectedItem();
	}
}