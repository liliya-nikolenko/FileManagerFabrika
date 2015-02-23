package filemanager;

import java.io.File;
import java.util.*;

public class Model extends Observable{
    private List<File> systemDrivers;
    private List<File> files;
    private String currentActivePath;
    private Stack<File> stackOfFilePath = new Stack<File>();

    Model(){
        currentActivePath = System.getProperty("user.home") + "/Desktop";
        stackOfFilePath.push(new File (currentActivePath));
        systemDrivers = new ArrayList(Arrays.asList(File.listRoots()));
        files = new ArrayList(Arrays.asList(new File (currentActivePath).listFiles()));
    }

    public Stack<File> getStackOfFilePath() {
        return stackOfFilePath;
    }

    public String getCurrentActivePath() {
        return currentActivePath;
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
        setChanged();
    }

    public void fillFilesByPath(File filePath){
        if(filePath.listFiles() != null) {
            files = Arrays.asList(filePath.listFiles());
            if(!currentActivePath.equals(filePath.getPath()))
                currentActivePath = filePath.getPath();
        }else
            files = null;
        setChanged();
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
}
