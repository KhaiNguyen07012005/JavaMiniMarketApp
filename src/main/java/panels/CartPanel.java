package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import service.CustomerService;
import util.Constants;

public class CartPanel extends JPanel {
	private JTable cartTable;
	private DefaultTableModel tableModel;
	private JLabel totalLabel;
	private CustomerService customerService;
	private String maKH;

	public CartPanel(CustomerService customerService, String maKH) {
		this.customerService = customerService;
		this.maKH = maKH;
		customerService.setMaKH(maKH);
		setLayout(new BorderLayout());
		initializeComponents();
	}

	private void initializeComponents() {
		tableModel = new DefaultTableModel(new String[] { "Mã SP", "Tên SP", "Số Lượng", "Đơn Giá", "Thành Tiền" }, 0);
		cartTable = new JTable(tableModel);
		var scrollPane = new JScrollPane(cartTable);
		add(scrollPane, BorderLayout.CENTER);

		var buttonPanel = new JPanel(new FlowLayout());
		var addButton = createStyledButton("Thêm từ Sản Phẩm");
		addButton.addActionListener(e -> addToCart());
		buttonPanel.add(addButton);

		var removeButton = createStyledButton("Xóa");
		removeButton.addActionListener(e -> removeFromCart());
		buttonPanel.add(removeButton);

		var checkoutButton = createStyledButton("Thanh Toán");
		checkoutButton.addActionListener(e -> checkout());
		buttonPanel.add(checkoutButton);

		add(buttonPanel, BorderLayout.SOUTH);

		totalLabel = new JLabel("Tổng tiền: 0 VNĐ");
		totalLabel.setForeground(Constants.BUTTON_COLOR);
		add(totalLabel, BorderLayout.NORTH);

		loadCart();
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

	private void addToCart() {
		var maSP = JOptionPane.showInputDialog(this, "Nhập Mã SP:");
		if (maSP != null && !maSP.trim().isEmpty()) {
			var quantityStr = JOptionPane.showInputDialog(this, "Nhập số lượng:");
			try {
				var quantity = Integer.parseInt(quantityStr);
				if (customerService.addToCart(maSP, quantity)) {
					loadCart();
				} else {
					JOptionPane.showMessageDialog(this, "Không đủ số lượng hoặc sản phẩm không tồn tại!", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void removeFromCart() {
		var selectedRow = cartTable.getSelectedRow();
		if (selectedRow >= 0) {
			var maSP = (String) tableModel.getValueAt(selectedRow, 0);
			customerService.removeFromCart(maSP);
			loadCart();
		} else {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void checkout() {
		String[] options = { "Tiền mặt", "Thẻ" };
		var paymentMethod = (String) JOptionPane.showInputDialog(this, "Chọn phương thức thanh toán:", "Thanh Toán",
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (paymentMethod != null) {
			var result = customerService.checkout(maKH, paymentMethod);
			JOptionPane.showMessageDialog(this, result);
			loadCart();
		}
	}

	public void loadCart() {
		tableModel.setRowCount(0);
		var total = 0D;
		for (Object[] item : customerService.getCart()) {
			tableModel.addRow(item);
			total += (double) item[4];
		}
		var discount = customerService.getDiscount();
		total -= discount;
		totalLabel.setText("Tổng tiền: " + total + " VNĐ" + (discount > 0 ? " (Giảm giá: " + discount + " VNĐ)" : ""));
	}

	public void updateMaKH(String newMaKH) {
		this.maKH = newMaKH;
		customerService.setMaKH(newMaKH);
		loadCart();
	}
}