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
        setSize(200, 120);
        setLayout(new GridLayout(2, 2, 3, 3));
        add(new JLabel(text));
        add(textField);
        add(okButton);
        add(cancelButton);
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
