package panels;

import java.awt.Color; // Thêm import này
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import service.CustomerService;
import util.Constants;

public class SupportPanel extends JPanel {
	private JTextArea feedbackArea;
	private CustomerService customerService;
	private String maKH;

	public SupportPanel(CustomerService customerService, String maKH) {
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
		add(new JLabel("Phản hồi của bạn:"), gbc);

		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		feedbackArea = new JTextArea(5, 20);
		add(feedbackArea, gbc);

		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		var sendButton = createStyledButton("Gửi Phản Hồi");
		sendButton.addActionListener(e -> sendFeedback());
		add(sendButton, gbc);
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

	private void sendFeedback() {
		var content = feedbackArea.getText().trim();
		if (content.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung phản hồi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (customerService.sendFeedback(maKH, content)) {
			JOptionPane.showMessageDialog(this, "Gửi phản hồi thành công!", "Thành công",
					JOptionPane.INFORMATION_MESSAGE);
			feedbackArea.setText("");
		} else {
			JOptionPane.showMessageDialog(this, "Lỗi khi gửi phản hồi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void updateMaKH(String newMaKH) {
		this.maKH = newMaKH;
		customerService.setMaKH(newMaKH);
	}
}