package dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CustomerFormDialog extends JDialog {
	private JTextField txtMaKH;
	private JTextField txtTenKH;
	private JTextField txtSoDienThoai;
	private JTextField txtDiemTichLuy;
	private boolean confirmed = false;

	public CustomerFormDialog(Frame owner, String title, String maKH, String tenKH, String soDienThoai,
			String diemTichLuy, boolean isAdd) {
		super(owner, title, true);
		setSize(400, 300);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout(10, 10));

		var formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		var gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		formPanel.add(new JLabel("Mã KH:"), gbc);

		txtMaKH = new JTextField(20);
		txtMaKH.setText(maKH);
		txtMaKH.setEditable(isAdd);
		gbc.gridx = 1;
		formPanel.add(txtMaKH, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		formPanel.add(new JLabel("Tên khách hàng:"), gbc);

		txtTenKH = new JTextField(20);
		txtTenKH.setText(tenKH);
		gbc.gridx = 1;
		formPanel.add(txtTenKH, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		formPanel.add(new JLabel("Số điện thoại:"), gbc);

		txtSoDienThoai = new JTextField(20);
		txtSoDienThoai.setText(soDienThoai);
		gbc.gridx = 1;
		formPanel.add(txtSoDienThoai, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		formPanel.add(new JLabel("Điểm tích luỹ:"), gbc);

		txtDiemTichLuy = new JTextField(20);
		txtDiemTichLuy.setText(diemTichLuy);
		gbc.gridx = 1;
		formPanel.add(txtDiemTichLuy, gbc);

		add(formPanel, BorderLayout.CENTER);

		var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		var btnOk = new JButton("OK");
		var btnCancel = new JButton("Hủy");

		btnOk.addActionListener(e -> onOk());
		btnCancel.addActionListener(e -> onCancel());

		buttonPanel.add(btnOk);
		buttonPanel.add(btnCancel);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void onOk() {

		if (txtMaKH.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Mã khách hàng không được để trống!");
			return;
		}
		if (txtTenKH.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Tên khách hàng không được để trống!");
			return;
		}
		if (txtSoDienThoai.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống!");
			return;
		}

		try {
			var diem = Integer.parseInt(txtDiemTichLuy.getText().trim());
			if (diem < 0) {
				JOptionPane.showMessageDialog(this, "Điểm tích luỹ phải là số nguyên không âm!");
				return;
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Điểm tích luỹ phải là số nguyên hợp lệ!");
			return;
		}

		confirmed = true;
		setVisible(false);
	}

	private void onCancel() {
		confirmed = false;
		setVisible(false);
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public String getMaKH() {
		return txtMaKH.getText().trim();
	}

	public String getTenKH() {
		return txtTenKH.getText().trim();
	}

	public String getSoDienThoai() {
		return txtSoDienThoai.getText().trim();
	}

	public String getDiemTichLuy() {
		return txtDiemTichLuy.getText().trim();
	}
}
