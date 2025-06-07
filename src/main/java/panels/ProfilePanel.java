package panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import service.CustomerService;
import util.Constants;

public class ProfilePanel extends JPanel {
	private JTextField nameField, phoneField, addressField;
	private CustomerService customerService;
	private String maKH;

	public ProfilePanel(CustomerService customerService, String maKH) {
		this.customerService = customerService;
		this.maKH = maKH;
		customerService.setMaKH(maKH);
		setLayout(new GridBagLayout());
		initializeComponents();
	}

	private void initializeComponents() {
		var gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		add(new JLabel("Tên KH:"), gbc);
		gbc.gridx = 1;
		nameField = new JTextField(20);
		add(nameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JLabel("SĐT:"), gbc);
		gbc.gridx = 1;
		phoneField = new JTextField(20);
		add(phoneField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel("Địa chỉ:"), gbc);
		gbc.gridx = 1;
		addressField = new JTextField(20);
		add(addressField, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		var updateButton = createStyledButton("Cập nhật");
		updateButton.addActionListener(e -> updateProfile());
		add(updateButton, gbc);

		loadProfile();
	}

	private JButton createStyledButton(String text) {
		var button = new JButton(text);
		button.setBackground(Constants.BUTTON_COLOR);
		button.setForeground(Color.WHITE);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(Constants.BUTTON_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(Constants.BUTTON_COLOR);
			}
		});
		return button;
	}

	private void loadProfile() {
		var info = customerService.getCustomerInfo(maKH);
		if (info != null) {
			nameField.setText((String) info[1]);
			phoneField.setText((String) info[2]);
			try {
				var query = "SELECT DiaChi FROM DIACHI_KH WHERE MaKH = ? AND LaMacDinh = 1";
				var stmt = customerService.getConnection().prepareStatement(query);
				stmt.setString(1, maKH);
				var rs = stmt.executeQuery();
				if (rs.next()) {
					addressField.setText(rs.getString("DiaChi"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Lỗi tải địa chỉ: " + e.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin khách hàng!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateProfile() {
		var name = nameField.getText().trim();
		var phone = phoneField.getText().trim();
		var address = addressField.getText().trim();
		if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (customerService.updateCustomerInfo(maKH, name, phone, address)) {
			JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thành công",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void updateMaKH(String newMaKH) {
		this.maKH = newMaKH;
		customerService.setMaKH(newMaKH);
		loadProfile();
	}
}