package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import service.CustomerService;
import util.Constants;

public class LoyaltyPanel extends JPanel {
	private JTable promotionTable;
	private DefaultTableModel tableModel;
	private CustomerService customerService;
	private String maKH;

	public LoyaltyPanel(CustomerService customerService, String maKH) {
		this.customerService = customerService;
		this.maKH = maKH;
		customerService.setMaKH(maKH);
		setLayout(new BorderLayout());
		initializeComponents();
	}

	private void initializeComponents() {
		tableModel = new DefaultTableModel(
				new String[] { "Mã KM", "Tên KM", "Giá Trị Giảm", "Ngày Bắt Đầu", "Ngày Kết Thúc" }, 0);
		promotionTable = new JTable(tableModel);
		var scrollPane = new JScrollPane(promotionTable);
		add(scrollPane, BorderLayout.CENTER);

		var buttonPanel = new JPanel(new FlowLayout());
		var redeemButton = createStyledButton("Đổi Điểm");
		redeemButton.addActionListener(e -> redeemPoints());
		buttonPanel.add(redeemButton);

		add(buttonPanel, BorderLayout.SOUTH);

		loadPromotions();
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

	private void loadPromotions() {
		tableModel.setRowCount(0);
		for (Object[] promotion : customerService.getPromotions()) {
			tableModel.addRow(promotion);
		}
	}

	private void redeemPoints() {
		var pointsStr = JOptionPane.showInputDialog(this, "Nhập số điểm muốn đổi:");
		if (pointsStr != null && !pointsStr.trim().isEmpty()) {
			try {
				var points = Integer.parseInt(pointsStr);
				if (customerService.redeemPoints(maKH, points)) {
					JOptionPane.showMessageDialog(this, "Đổi điểm thành công!", "Thành công",
							JOptionPane.INFORMATION_MESSAGE);
					loadPromotions();
				} else {
					JOptionPane.showMessageDialog(this, "Không đủ điểm hoặc lỗi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Số điểm không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void updateMaKH(String newMaKH) {
		this.maKH = newMaKH;
		customerService.setMaKH(newMaKH);
		loadPromotions();
	}
}