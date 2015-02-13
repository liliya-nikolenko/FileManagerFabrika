package filemanager;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

enum Byte {
    KB("KB", 1024), MB("MB", 1024 * 1024), GB("GB", 1024 * 1024 * 1024);
    private int amount;
    private String name = "bytes";

    Byte(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }
}

class ModalDialog extends Dialog implements ActionListener{
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
    ModalDialog(JFrame parent, String title){
        super(parent, title, true);
        if (parent != null) {
            Dimension parentSize = parent.getSize();
            Point p = parent.getLocation();
            setLocation(p.x + parentSize.width / 4, p.y + parentSize.height / 4);
        }
        setSize(200, 120);
        setLayout(new GridLayout(2, 2, 3, 3));
        add(new JLabel("Edit"));
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

public class View extends JFrame implements Observer{
    private Byte bytes = Byte.GB;
    private JComboBox driveNameComboBox, driveNameComboBox2;
    private JButton helpButton, createFolderButton, copyButton, removeButton, renameButton, deleteButton, terminalButton, exitButton;
    private JLabel pathLabel, pathLabel2, labelMemory, labelMemory2, labelCommandLine;
    private JTextField commandLine;
    private DefaultListModel listModel, listModel2;
    private JList listOfFiles, listOfFiles2;
    private JFrame helpFrame = new JFrame("Help");
    private JFrame errorFrame;
    private int frameSizeX = 800;
    private int frameSizeY = 600;
    private int countButton = 8;

    public JComboBox getDriveNameComboBox() {
        return driveNameComboBox;
    }

    public JComboBox getDriveNameComboBox2() {
        return driveNameComboBox2;
    }

    public JLabel getPathLabel() {
        return pathLabel;
    }

    public JLabel getPathLabel2() {
        return pathLabel2;
    }

    public JLabel getLabelMemory2() {
        return labelMemory2;
    }

    public JLabel getLabelMemory() {
        return labelMemory;
    }

    public DefaultListModel getListModel() {
        return listModel;
    }

    public DefaultListModel getListModel2() {
        return listModel2;
    }

    public JList getListOfFiles() {
        return listOfFiles;
    }

    public JList getListOfFiles2() {
        return listOfFiles2;
    }

    public JButton getHelpButton() {
        return helpButton;
    }

    public JButton getCreateFolderButton() {
        return createFolderButton;
    }

    public JButton getCopyButton() {
        return copyButton;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }

    public JButton getRenameButton() {
        return renameButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }

    public JButton getTerminalButton() {
        return terminalButton;
    }

    public View() {
        this.setTitle("File Manager");
        this.setLayout(new GridBagLayout());
        this.setSize(frameSizeX, frameSizeY);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void makeGUI(){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipady = 10;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JPanel leftTopPanel = new JPanel();
        leftTopPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        driveNameComboBox = new JComboBox();
        leftTopPanel.add(driveNameComboBox);
        pathLabel = new JLabel();
        leftTopPanel.add(pathLabel);
        labelMemory = new JLabel();
        leftTopPanel.add(labelMemory);

        JPanel rightTopPanel = new JPanel();
        rightTopPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        driveNameComboBox2 = new JComboBox();
        rightTopPanel.add(driveNameComboBox2);
        pathLabel2 = new JLabel();
        rightTopPanel.add(pathLabel2);
        labelMemory2 = new JLabel();
        rightTopPanel.add(labelMemory2);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));
        topPanel.add(leftTopPanel);
        topPanel.add(rightTopPanel);
        this.add(topPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.ipady = 470;
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(1, 2));
        listModel = new DefaultListModel();
        listOfFiles = new JList(listModel);
        JScrollPane scroll = new JScrollPane(listOfFiles);
        listPanel.add(scroll);

        listModel2 = new DefaultListModel();
        listOfFiles2 = new JList(listModel2);
        JScrollPane scroll2 = new JScrollPane(listOfFiles2);
        listPanel.add(scroll2);
        this.add(listPanel, gbc);

        gbc.ipady = 20;
        gbc.gridy = 2;
        JPanel commandLinePanel = new JPanel();
        commandLinePanel.setLayout(new GridLayout(1, 2));
        labelCommandLine = new JLabel();
        commandLinePanel.add(labelCommandLine);
        commandLine = new JTextField(50);
        commandLinePanel.add(commandLine);

        this.add(commandLinePanel, gbc);

        helpButton = new JButton("F1-Help");
        renameButton = new JButton("F2-Rename");
        copyButton = new JButton("F5-Copy");
        removeButton = new JButton("F6-Remove");
        createFolderButton = new JButton("F7-New folder");
        deleteButton = new JButton("F8-Delete");
        terminalButton = new JButton("F9-Terminal");
        exitButton = new JButton("F10-Exit");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, countButton));
        buttonPanel.add(helpButton);
        buttonPanel.add(renameButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(createFolderButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(terminalButton);
        buttonPanel.add(exitButton);

        gbc.ipady = 20;
        gbc.gridy = 3;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        this.add(buttonPanel, gbc);
        this.setResizable(false);
    }

    public void makeHelpFrame(){
        helpFrame.setSize(frameSizeX/2, frameSizeY);
        helpFrame.setLayout(new GridBagLayout());
        String text = "Help menu";
        JLabel textLabel = new JLabel("<html>" + text + "<html>");
        helpFrame.add(textLabel);
        helpFrame.setVisible(true);
    }

    public void makeErrorFrame(String textError){
        errorFrame = new JFrame("Error");
        errorFrame.setSize(frameSizeX/4, frameSizeY/4);
        errorFrame.setLayout(new GridBagLayout());
        JLabel textLabel = new JLabel("<html>" + textError + "<html>");
        errorFrame.add(textLabel);
        errorFrame.setVisible(true);
    }

    // This inner class is needed to input icons + text
    public class IconListRenderer
            extends DefaultListCellRenderer {

        private Map<Object, Icon> icons = null;

        public IconListRenderer(Map<Object, Icon> icons) {
            this.icons = icons;
        }

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            // Get the renderer component from parent class
            JLabel label =
                    (JLabel) super.getListCellRendererComponent(list,
                            value, index, isSelected, cellHasFocus);

            // Set background of list item
            if ( index % 2 == 0 ) {
                setBackground(new Color(240, 240, 255));
            }
            else {
                setBackground(Color.white );
            }

            // Get icon to use for the list item value
            Icon icon = icons.get(value);

            // Set icon to display for value
            label.setIcon(icon);
            return label;
        }
    }

    // This method fills comboboxes with driver letters and icons
    public void fillDriverNameComboBoxes(JComboBox driverNameComboBox, List<File> files) {
        if(files != null) {
            Map<Object, Icon> icons = new HashMap<Object, Icon>();
            for (File f : files) {
                icons.put(f.toString(), FileSystemView.getFileSystemView().getSystemIcon(f));
            }
            for (File f : files) {
                driverNameComboBox.addItem(f.toString());
            }
            driverNameComboBox.setRenderer(new IconListRenderer(icons)); // this string is for correct output of combobox
        }
    }

    //This method isn't used
    public void fillDriverNameLabels(JComboBox driverNameComboBox, JLabel labelDisk){
        File f = new File(driverNameComboBox.getSelectedItem().toString());
        String diskName = FileSystemView.getFileSystemView().getSystemDisplayName(f).toString();
        labelDisk.setText("[" + diskName + "]");
    }

    public void fillPathLabels(String diskPath, JLabel pathLabel){
        pathLabel.setText("[" + diskPath + "]");
    }

    public void fillDriverSpaceLabels(JComboBox driverNameComboBox, JLabel labelMemory){
        File f = new File(driverNameComboBox.getSelectedItem().toString());
        long Usable = f.getUsableSpace()/bytes.getAmount();
        long Total = f.getTotalSpace()/bytes.getAmount();
        labelMemory.setText(Usable + " " + bytes.getName() + " from " + Total + " " + bytes.getName());
    }

    //This method isn't used, fill list without parent directory path
    public void fillList(DefaultListModel listModel, JList listOfFiles, List<File> files) {
        listModel.clear();
        if(files != null) {
            Map<Object, Icon> icons = new HashMap<Object, Icon>();
            for (File iterator : files) {
                icons.put(FileSystemView.getFileSystemView().getSystemDisplayName(iterator),
                        FileSystemView.getFileSystemView().getSystemIcon(iterator));
            }
            for (File iterator : files) {
                listModel.addElement(FileSystemView.getFileSystemView().getSystemDisplayName(iterator));
            }
            listOfFiles.setCellRenderer(new IconListRenderer(icons));
        }
    }

    public void fillList(DefaultListModel listModel, JList listOfFiles, List<File> files, File parentDirectory) {
        listModel.clear();
        if(files != null) {
            Map<Object, Icon> icons = new HashMap<Object, Icon>();
            for (File iterator : files) {
                icons.put(FileSystemView.getFileSystemView().getSystemDisplayName(iterator),
                        FileSystemView.getFileSystemView().getSystemIcon(iterator));
            }
            icons.put(parentDirectory, FileSystemView.getFileSystemView().getSystemIcon(parentDirectory));
            for (File iterator : files) {
                listModel.addElement(FileSystemView.getFileSystemView().getSystemDisplayName(iterator));
            }
            listModel.add(0, parentDirectory);
            listOfFiles.setCellRenderer(new IconListRenderer(icons));
        }
    }

    public void fillLabelCommandLine(String labelText){
        labelCommandLine.setText(labelText);
    }

    public void addActionListener(ActionListener l){
        driveNameComboBox.addActionListener(l);
        driveNameComboBox2.addActionListener(l);
        helpButton.addActionListener(l);
        createFolderButton.addActionListener(l);
        copyButton.addActionListener(l);
        renameButton.addActionListener(l);
        removeButton.addActionListener(l);
        deleteButton.addActionListener(l);
        terminalButton.addActionListener(l);
        exitButton.addActionListener(l);
        commandLine.addActionListener(l);

    }

    public void addMouseClicked(MouseListener l) {
        listOfFiles.addMouseListener(l);
        listOfFiles2.addMouseListener(l);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof Model){
            Model model = (Model) o;
            if(arg.equals(listModel)){
                fillList(listModel, listOfFiles, model.getFiles(), model.getParentDirectory());
            }
            else if(arg.equals(listModel2)){
                fillList(listModel2,listOfFiles2, model.getFiles(), model.getParentDirectory());
            }
        }
    }
}