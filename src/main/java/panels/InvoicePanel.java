package panels;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class InvoicePanel extends JPanel {
	public InvoicePanel() {
		setLayout(new BorderLayout());
		var label = new JLabel("Hóa đơn bán hàng", SwingConstants.CENTER);
		label.setFont(new Font("Segoe UI", Font.BOLD, 22));
		add(label, BorderLayout.CENTER);
	}
}
