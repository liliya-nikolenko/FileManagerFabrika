package filemanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Controller implements ActionListener, MouseListener{
    private Model leftModel;
    private Model rightModel;
    private View view;
    private boolean activeLeftModel = true;
    private File activeClickFile;

    public Controller(Model m, Model m2, View v) {
        leftModel = m;
        rightModel = m2;
        view = v;
        view.makeGUI();
        view.fillDriverNameComboBoxes(view.getDriveNameComboBox(), leftModel.getSystemDrivers());
        view.fillDriverNameComboBoxes(view.getDriveNameComboBox2(), rightModel.getSystemDrivers());
        view.fillPathLabels(leftModel.getCurrentActivePath(), view.getPathLabel());
        view.fillPathLabels(rightModel.getCurrentActivePath(), view.getPathLabel2());
        view.fillDriverSpaceLabels(view.getDriveNameComboBox(), view.getLabelMemory());
        view.fillDriverSpaceLabels(view.getDriveNameComboBox2(), view.getLabelMemory2());
        view.fillLabelCommandLine(leftModel.getCurrentActivePath());
        view.fillList(view.getListModel(), view.getListOfFiles(), leftModel.getFiles(), new File(leftModel.getCurrentActivePath()));
        view.fillList(view.getListModel2(), view.getListOfFiles2(), rightModel.getFiles(), new File(rightModel.getCurrentActivePath()));
        view.addActionListener(this);
        view.addMouseClicked(this);
    }

    public  void actionPerformed(ActionEvent e){
        Object source = e.getSource();
        if(source == view.getDriveNameComboBox()){
            view.fillPathLabels(view.getDriveNameComboBox().getSelectedItem().toString(), view.getPathLabel());
            view.fillDriverSpaceLabels(view.getDriveNameComboBox(), view.getLabelMemory());
            leftModel.fillFilesByDriveName(view.getDriveNameComboBox().getSelectedIndex());
            view.fillLabelCommandLine(leftModel.getCurrentActivePath());
            activeLeftModel = true;
        }
        else if(source == view.getDriveNameComboBox2()){
            view.fillPathLabels(view.getDriveNameComboBox2().getSelectedItem().toString(), view.getPathLabel2());
            view.fillDriverSpaceLabels(view.getDriveNameComboBox2(), view.getLabelMemory2());
            rightModel.fillFilesByDriveName(view.getDriveNameComboBox2().getSelectedIndex());
            view.fillLabelCommandLine(rightModel.getCurrentActivePath());
            activeLeftModel = false;
        }
        else if(source == view.getHelpButton()){
            view.makeHelpFrame();
        }
        else if(source == view.getRenameButton()){
            if(activeClickFile != null) {
                ModalDialog renameFileDialog = new ModalDialog(view, "Rename", "Edit title");
                if (!renameFileDialog.isCancelled()) {
                    if (activeLeftModel) {
                        renameFile(leftModel, renameFileDialog.getEditTitle());
                    } else {
                        renameFile(rightModel, renameFileDialog.getEditTitle());
                    }
                }
            }
        }
        else if(source == view.getCopyButton()){
            if(activeClickFile != null) {
                if (activeLeftModel) {
                    copyFile(leftModel, rightModel);
                } else {
                    copyFile(rightModel, leftModel);
                }
            }
        }
        else if(source == view.getRemoveButton()){
            if(activeClickFile != null) {
                if (activeLeftModel) {
                    removeFile(leftModel, rightModel);
                } else {
                    removeFile(rightModel, leftModel);
                }
            }
        }
        else if(source == view.getCreateFolderButton()){
            ModalDialog createFolderDialog = new ModalDialog(view, "Create folder", "Input the title");
            if(!createFolderDialog.isCancelled()) {
                if (activeLeftModel) {
                    createNewFolder(leftModel, createFolderDialog.getEditTitle());
                } else {
                    createNewFolder(rightModel, createFolderDialog.getEditTitle());
                }
            }
        }
        else if(source == view.getDeleteButton()){
            if(activeClickFile != null) {
                if (activeLeftModel) {
                    deleteFile(leftModel);
                } else {
                    deleteFile(rightModel);
                }
            }
        }
        else if(source == view.getTerminalButton()){
            ///
        }
        else if(source == view.getExitButton()){
            System.exit(0);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        if(source == view.getListOfFiles() || source == view.getListOfFiles2()) {
            int index = ((JList) source).locationToIndex(e.getPoint()) - 1;
            if (source == view.getListOfFiles()) {
                checkMouseClick(e, index, leftModel, true);
            } else if (source == view.getListOfFiles2()) {
                checkMouseClick(e, index, rightModel, false);
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

    public void checkMouseClick(MouseEvent e, int index, Model model, boolean isActiveModel){
            if (e.getClickCount() == 1) {
                e.consume();
                activeLeftModel = isActiveModel;
                if (index >= 0)
                    activeClickFile = model.getFiles().get(index);
            }
            if (e.getClickCount() == 2) {
                e.consume();
                if (index == -1){
                    if (!model.getStackOfFilePath().empty()) {
                        model.fillFilesByPath(model.getRemovingParentDirectory());
                        view.fillLabelCommandLine(model.getCurrentActivePath());
                        if(isActiveModel){
                            view.fillPathLabels(model.getCurrentActivePath(), view.getPathLabel());
                        }else{
                            view.fillPathLabels(model.getCurrentActivePath(), view.getPathLabel2());
                        }
                    }
                }
                if (index >= 0){
                    openFile(model, index);
                }
            }
    }

    public void openFile(Model model, int index){
        if(model.getFiles().get(index).isDirectory()){
            model.getStackOfFilePath().push(new File(model.getCurrentActivePath()));
            model.fillFilesByPath(model.getFiles().get(index));
            view.fillLabelCommandLine(model.getCurrentActivePath());
            if(activeLeftModel){
                view.fillPathLabels(model.getCurrentActivePath(), view.getPathLabel());
            }else{
                view.fillPathLabels(model.getCurrentActivePath(), view.getPathLabel2());
            }
        }
        else {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(new File(model.getFiles().get(index).getPath()));
            } catch (IOException ie) {
                view.makeWarningFrame(ie.getMessage());
            }
        }
    }

    public void createNewFolder(Model model, String title){
        boolean success = (new File((model.getCurrentActivePath() + "/" + title)).mkdirs());
        if (success){
            model.fillFilesByPath(new File(model.getCurrentActivePath()));
        }
        else {
            String errorMessage = "You can't create a new folder";
            view.makeWarningFrame(errorMessage);
        }
    }

    public void renameFile(Model model, String title){
        File newNameFile = new File(model.getCurrentActivePath() + "/" + title +
                getFileExtention(activeClickFile.toString()));
        if(newNameFile.exists()) {
            try {
                throw new IOException("File exists");
            } catch (IOException ie) {
                String errorMessage = ie.getMessage();
                view.makeWarningFrame(errorMessage);
            }
        } else {
            boolean success = activeClickFile.renameTo(newNameFile);
            if (success) {
                model.fillFilesByPath(new File(model.getCurrentActivePath()));
            } else {
                String errorMessage = "Operation was failed";
                view.makeWarningFrame(errorMessage);
            }
        }
    }

    public void deleteFile(Model model){
        try {
            deleteFilesAndFolders(activeClickFile);
            model.fillFilesByPath(new File(model.getCurrentActivePath()));
        }catch (IOException ie){
            String errorMessage = ie.getMessage();
            view.makeWarningFrame(errorMessage);
        }
    }

    public void deleteFilesAndFolders(File dest) throws IOException{
        if (dest.isDirectory() && dest.listFiles().length != 0) {
            File files[] = dest.listFiles();
            for (File file : files) {
                deleteFilesAndFolders(file);
            }
        }
        if ((dest.isDirectory() && dest.listFiles().length == 0) || dest.isFile()) {
            dest.delete();
        }
    }

    public void copyFile(Model activeModel, Model passiveModel) {
        if(activeModel.getCurrentActivePath().equals(passiveModel.getCurrentActivePath())){
            String fileTitle = getFileTitle(activeClickFile);
            ModalDialog copyFile = new ModalDialog(view, "Copy file", "Source and destination path are the same." +
                    " Do you want to copy " + fileTitle + " to the same directory? If yes, input new title");
            if(!copyFile.isCancelled()){
                File source = new File(activeClickFile.getPath());
                File dest = new File(passiveModel.getCurrentActivePath() + "/" + copyFile.getEditTitle() +
                                    getFileExtention(source.toString()));
                try {
                    copyFilesAndFolders(source, dest);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
                passiveModel.fillFilesByPath(new File(passiveModel.getCurrentActivePath()));
                activeModel.fillFilesByPath(new File(activeModel.getCurrentActivePath()));
            }
        }
        else {
            File source = new File(activeClickFile.getPath());
            File dest = new File(passiveModel.getCurrentActivePath() + "/" + getFileName(source.toString()));
            try {
                copyFilesAndFolders(source, dest);
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            passiveModel.fillFilesByPath(new File(passiveModel.getCurrentActivePath()));
        }
    }

    public void copyFilesAndFolders(File source, File dest) throws IOException{
        if(source.isDirectory()){
            if(!dest.exists()){
                dest.mkdir();
            }
            String files[] = source.list();
            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(dest, file);
                copyFilesAndFolders(srcFile, destFile);
            }
        }else{
            Files.copy(source.toPath(), dest.toPath());
        }
    }

    public void removeFile(Model activeModel, Model passiveModel){
        if(activeModel.getCurrentActivePath().equals(passiveModel.getCurrentActivePath())){
            String fileTitle = getFileTitle(activeClickFile);
            String errorMessage = "Source and destination path are the same. You can't remove " + fileTitle;
            view.makeWarningFrame(errorMessage);
        }
        else {
            File source = new File(activeClickFile.getPath());
            File dest = new File(passiveModel.getCurrentActivePath() + "/" + getFileName(source.toString()));
            try {
                copyFilesAndFolders(source, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                deleteFilesAndFolders(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
            activeModel.fillFilesByPath(new File(activeModel.getCurrentActivePath()));
            passiveModel.fillFilesByPath(new File(passiveModel.getCurrentActivePath()));
        }
    }

    public String getFileExtention(String filename){
        int dotPosition = filename.lastIndexOf(".") + 1;
        if(dotPosition == 0){
            return "";
        }
        return filename.substring(dotPosition - 1);
    }

    public String getFileName(String filename){
        int dotPosition = filename.lastIndexOf("\\") + 1;
        return filename.substring(dotPosition);
    }

    public String getFileTitle(File file){
        if(file.isDirectory())
            return "directory";
        else
            return "file";
    }
}
