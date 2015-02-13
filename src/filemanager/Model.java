package filemanager;

import javax.swing.*;
import java.io.File;
import java.util.*;

public class Model extends Observable{

    private File desktopPath = new File(System.getProperty("user.home") + "/Desktop");
    private List<File> systemDrivers = new ArrayList(Arrays.asList(File.listRoots()));
    private List<File> files = new ArrayList(Arrays.asList(desktopPath.listFiles()));
    private String currentActivePath = desktopPath.getPath();
    private Stack<File> stackOfFilePath = new Stack<File>();

    public Stack<File> getStackOfFilePath() {
        return stackOfFilePath;
    }

    public String getCurrentActivePath() {
        return currentActivePath;
    }

    public File getDesktopPath() {
        return desktopPath;
    }

    public List<File> getSystemDrivers() {
        return systemDrivers;
    }

    public List<File> getFiles() {
        return files;
    }

    public File getParentDirectory(){
        return stackOfFilePath.peek();
    }

    public File getRemovingParentDirectory(){
        if(stackOfFilePath.size()==1)
            return stackOfFilePath.peek();
        else
            return stackOfFilePath.pop();
    }

    public void fillFilesByDriveName(int drive_id){
        stackOfFilePath.clear();
        stackOfFilePath.push(new File(systemDrivers.get(drive_id).getPath()));
        currentActivePath = systemDrivers.get(drive_id).getPath();
        if(systemDrivers.get(drive_id).listFiles() != null) {
            files = Arrays.asList(systemDrivers.get(drive_id).listFiles());
        }
        else files = null;
    }

    public void fillFilesByPath(File filePath){
        if(filePath.listFiles() != null) {
            files = Arrays.asList(filePath.listFiles());
            currentActivePath = filePath.getPath();
        }else
            files = null;
    }

    @Override
    public synchronized void addObserver(Observer o){
        super.addObserver(o);
    }
    @Override
    public synchronized void deleteObserver(Observer o) {
        super.deleteObserver(o);
    }
    @Override
    public void notifyObservers(){
        super.notifyObservers();
    }
    public void setChanged(){
        super.setChanged();
        notifyObservers();
    }
    public void setChanged(DefaultListModel listModel){
        super.setChanged();
        notifyObservers(listModel);
    }
}
