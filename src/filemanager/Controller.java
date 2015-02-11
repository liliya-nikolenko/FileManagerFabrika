package filemanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.io.IOException;

public class Controller implements ActionListener, MouseListener{
    private Model model;
    private Model model2;
    private View view;
    private boolean activeModel = true;
    private File activeClickFile;

    public Controller(Model m, Model m2, View v) {
        model = m;
        model2 = m2;
        view = v;
        model.addObserver(view);
        model2.addObserver(view);
        view.makeGUI();
        view.fillDriverNameComboBoxes(view.getDriveNameComboBox(), model.getSystemDrivers());
        view.fillDriverNameComboBoxes(view.getDriveNameComboBox2(), model2.getSystemDrivers());
        //view.fillDriverNameLabels(view.getDriveNameComboBox(), view.getPathLabel());
        //view.fillDriverNameLabels(view.getDriveNameComboBox2(), view.getPathLabel2());
        view.fillPathLabels(model.getDesktopPath().toString(), view.getPathLabel()); //just as a variant
        view.fillPathLabels(model2.getDesktopPath().toString(), view.getPathLabel2()); //just as a variant
        view.fillDriverSpaceLabels(view.getDriveNameComboBox(), view.getLabelMemory());
        view.fillDriverSpaceLabels(view.getDriveNameComboBox2(), view.getLabelMemory2());
        view.fillLabelCommandLine(model.getCurrentActivePath());
        view.fillList(view.getListModel(), view.getListOfFiles(), model.getFiles(), new File(model.getCurrentActivePath()));
        view.fillList(view.getListModel2(), view.getListOfFiles2(), model2.getFiles(), new File(model2.getCurrentActivePath()));
        view.addActionListener(this);
        view.addMouseClicked(this);
    }

    public  void actionPerformed(ActionEvent e){
        Object source = e.getSource();
        if(source == view.getDriveNameComboBox()){
            view.fillPathLabels(view.getDriveNameComboBox().getSelectedItem().toString(), view.getPathLabel());
            view.fillDriverSpaceLabels(view.getDriveNameComboBox(), view.getLabelMemory());
            view.fillList(view.getListModel(), view.getListOfFiles(),
                    model.getFileListByDriveName(view.getDriveNameComboBox().getSelectedIndex()), model.getStackOfFilePath().pop());
            view.fillLabelCommandLine(model.getCurrentActivePath());
            activeModel = true;
        }
        else if(source == view.getDriveNameComboBox2()){
            view.fillPathLabels(view.getDriveNameComboBox2().getSelectedItem().toString(), view.getPathLabel2());
            view.fillDriverSpaceLabels(view.getDriveNameComboBox2(), view.getLabelMemory2());
            view.fillList(view.getListModel2(), view.getListOfFiles2(),
                    model2.getFileListByDriveName(view.getDriveNameComboBox2().getSelectedIndex()), model2.getStackOfFilePath().pop());
            view.fillLabelCommandLine(model2.getCurrentActivePath());
            activeModel = false;
        }
        else if(source == view.getHelpButton()){
            view.makeHelpFrame();
        }
        else if(source == view.getCreateFolderButton()){
            ModalDialog createFolderDialog = new ModalDialog(view, "Create folder");
            if(!createFolderDialog.isCancelled()) {
                if (activeModel) {
                    createNewFolder(model, view.getListModel(), view.getListOfFiles(), createFolderDialog.getEditTitle());
                } else {
                    createNewFolder(model2, view.getListModel2(), view.getListOfFiles2(), createFolderDialog.getEditTitle());
                }
            }
        }
        else if(source == view.getCopyButton()){

        }
        else if(source == view.getRenameButton()){

        }
        else if(source == view.getDeleteButton()){
            if(activeModel) {
                deleteFile(model, view.getListModel(), view.getListOfFiles());
            }else {
                deleteFile(model2, view.getListModel2(), view.getListOfFiles2());
            }
        }
        else if(source == view.getTerminalButton()){

        }
        else if(source == view.getExitButton()){

        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int index;
        if(e.getSource() == view.getListOfFiles()){
            if(e.getClickCount() == 1){
                activeModel = true;
                index = ((JList) e.getSource()).locationToIndex(e.getPoint()) - 1;
                if(index>=0)
                    activeClickFile = model.getFiles().get(index);
            }
            if (e.getClickCount() == 2 && !e.isConsumed()) {
                e.consume();
                if(!fillListByDoubleClick(e, model, view.getListModel(), view.getListOfFiles())){
                    openFileByDoubleClick(e, model);
                }
            }
        }else if(e.getSource() == view.getListOfFiles2()) {
            if(e.getClickCount() == 1){
                activeModel = false;
                index = ((JList) e.getSource()).locationToIndex(e.getPoint()) - 1;
                if(index>=0)
                    activeClickFile = model2.getFiles().get(index);
            }
            if (e.getClickCount() == 2 && !e.isConsumed()) {
                e.consume();
                if(!fillListByDoubleClick(e, model2, view.getListModel2(), view.getListOfFiles2())){
                    openFileByDoubleClick(e, model2);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public boolean fillListByDoubleClick(MouseEvent e, Model model, DefaultListModel listModel, JList list){
        int index = ((JList) e.getSource()).locationToIndex(e.getPoint()) - 1; //+1 parent directory
        if (index == -1) {
            if (!model.getStackOfFilePath().empty()) {
                view.fillList(listModel, list,
                            model.getFileListByPath(model.getStackOfFilePath().peek()), model.getStackOfFilePath().pop());
                view.fillLabelCommandLine(model.getCurrentActivePath());
                return true;
            }
        }
        if (index >= 0) {
            if(model.getFiles().get(index).isDirectory()) {
                model.getStackOfFilePath().push(new File(model.getCurrentActivePath()));
                view.fillList(listModel, list,
                        model.getFileListByPath(model.getFiles().get(index)), model.getStackOfFilePath().peek());
                view.fillLabelCommandLine(model.getCurrentActivePath());
                return true;
            }
        }
        return false;
    }
    public boolean openFileByDoubleClick(MouseEvent e, Model model){
        int index = ((JList) e.getSource()).locationToIndex(e.getPoint()) - 1; //+1 parent directory
        if (index >= 0) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(new File(model.getFiles().get(index).getPath()));
                return true;
            } catch (IOException ie) {
                view.makeErrorFrame(ie.getMessage());
                return false;
            }
        }
        return false;
    }
    public void createNewFolder(Model model, DefaultListModel listModel, JList list, String title){
        boolean success = (new File((model.getCurrentActivePath() + "/" + title)).mkdirs());
        view.fillList(listModel, list,
                model.getFileListByPath(new File(model.getCurrentActivePath())), model.getStackOfFilePath().peek());
        if (!success) {
            String errorMessage = "You can't create a new folder";
            view.makeErrorFrame(errorMessage);
        }
    }

    public void deleteFile(Model model, DefaultListModel listModel, JList list){
        boolean success = (new File(activeClickFile.getPath())).delete();
        view.fillList(listModel, list,
                model.getFileListByPath(new File(model.getCurrentActivePath())), model.getStackOfFilePath().peek());
        if (!success) {
            String errorMessage = "You can't delete a file";
            view.makeErrorFrame(errorMessage);
        }
    }

}
