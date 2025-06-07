package panels;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AccountPanel extends JPanel {
	public AccountPanel() {
		setLayout(new BorderLayout());
		var label = new JLabel(" Thông tin tài khoản", SwingConstants.CENTER);
		label.setFont(new Font("Segoe UI", Font.BOLD, 22));
		add(label, BorderLayout.CENTER);
	}
}
