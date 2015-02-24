package filemanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModalDialog extends Dialog implements ActionListener {
    private JTextField textField = new JTextField();
    private JButton okButton = new JButton("Ok");
    private JButton cancelButton = new JButton("Cancel");
    private boolean cancelled;

    public String getEditTitle(){
        return textField.getText();
    }
    public boolean isCancelled() {
        return this.cancelled;
    }
    ModalDialog(JFrame parent, String title, String text){
        super(parent, title, true);
        if (parent != null) {
            Dimension parentSize = parent.getSize();
            Point p = parent.getLocation();
            setLocation(p.x + parentSize.width / 3, p.y + parentSize.height / 3);
        }
        setSize(200, 180);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridy = 0;
        add(new JLabel("<html>" + text + "<html>"), gbc);
        gbc.gridy = 1;
        add(textField, gbc);
        gbc.gridy = 2;
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout());
        panel.add(okButton);
        panel.add(cancelButton);
        add(panel, gbc);
        okButton.setActionCommand("okButton");
        okButton.addActionListener(this);
        cancelButton.setActionCommand("cancelButton");
        cancelButton.addActionListener(this);
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e){
        String command = e.getActionCommand();
        if (command.equals("okButton")) {
            this.cancelled = false;
            setVisible(false);
        }
        else if (command.equals("cancelButton")) {
            this.cancelled = true;
            setVisible(false);
        }
        setVisible(false);
        dispose();
    }
}
