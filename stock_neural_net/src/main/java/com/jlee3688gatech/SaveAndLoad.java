package com.jlee3688gatech;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * class for save and load Object.
 * @version 1.0
 * @author Jeonghoon Lee
 */
public class SaveAndLoad {
    private String address;
    private String saveFileAddress;
    private ArrayList<String> currAddrDirectories;
    private String currAddr;
    private String slash;

    /**
     * Constructor for SaveAndLoad class.
     * This will check the save_files directory existance,
     * and If it is not exist, it will make the save_files directory.
     */
    public SaveAndLoad() {
        currAddrDirectories = new ArrayList<String>();
        currAddr = new String();
        checkOSAndSetSlash();
        Path path = Paths.get("");
        address = path.toAbsolutePath().toString();
        saveFileAddress = address + slash + "save_files";

        boolean saveExist = false;
        for (File file: new File(address).listFiles()) {
            if (file.getName().equals("save_files")) {
                saveExist = true;
            } 
        }

        if (!saveExist) {
            File newDirectory = new File(saveFileAddress);
            try {
                newDirectory.mkdir();
                System.out.println("make directory \"" + saveFileAddress + "\" successfully.");
            } catch (Exception e) {
                System.out.println("Fail to make directory at \"" + saveFileAddress + "\"");
                e.getStackTrace();
            }
        }
    }

    /**
     * The Directory separator ("\", "/") depends on which OS user use.
     * this method will check user's OS and set Directory separator (slash variable)
     */
    private void checkOSAndSetSlash() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            this.slash = "\\";
        } else {
            this.slash = "/";
        }
    }

    /**
     * getter for filenames in the current directory.
     * @return fileNames in current address
     */
    public ArrayList<String> getFileNamesInSaveDirectory() {
        ArrayList<String> ret = new ArrayList<String>();
        for (File file: new File(saveFileAddress + currAddr).listFiles()) {
            ret.add(file.getName());
        }
        return ret;
    }

    /**
     * Helper method for save file.
     * @param fileName name of file
     * @param o object o
     * @return return true if file saved successfully, or false.
     */
    public boolean saveFile(String fileName, Object o) {
        FileOutputStream fileStream;
        try {
            fileStream = new FileOutputStream(saveFileAddress + currAddr + slash + fileName);
            ObjectOutputStream os = new ObjectOutputStream(fileStream);
            os.writeObject(o);
            os.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * load file from the current address.
     * @param fileName name of the file
     * @return Object from the file.
     */
    public Object loadFile(String fileName) {
         FileInputStream fileStream;
         ObjectInputStream os;
        try {
            fileStream = new FileInputStream(saveFileAddress + currAddr + slash + fileName);
            os = new ObjectInputStream(fileStream);
            Object ret = os.readObject();
            os.close();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method for adding directory name in currAddrDirectories(ArrayList)
     * @param directoryName name of directory.
     */
    public void addCurrAddr(String directoryName) {
        this.currAddrDirectories.add(directoryName);
        setCurrAddr();
    }

    /**
     * Helper method for removing directory name in currAddrDirectories(ArrayList)
     * @return
     */
    public boolean removeLastAddr() {
        if (currAddrDirectories.size() == 0) {
            return false;
        } else {
            currAddrDirectories.remove(currAddrDirectories.size() - 1);
            setCurrAddr();
            return true;
        }
    }

    /**
     * setter (refresh) for the CurrAddr String)
     */
    private void setCurrAddr() {
        currAddr = new String();
        for (int i = 0; i < currAddrDirectories.size(); i++) {
            currAddr += slash;
            currAddr += currAddrDirectories.get(i);
        }
    }

    /**
     * Method for removing the file in current address.
     * @param fileName name of the file.
     * @return true if remove successfully, else false.
     */
    public boolean removeFile(String fileName) {
        return removeFilesWithAddr(saveFileAddress + currAddr + slash + fileName);
    }

    /**
     * Helper method for remove file.
     * @param addr address of file to remove
     * @return true if remove file successfully else return false.
     */
    private boolean removeFilesWithAddr(String addr) {
        File file = new File(addr);
        boolean ret = true;

        if (file.isDirectory()) {
            for (File temp : new File(addr).listFiles()) {
                if (!removeFilesWithAddr(addr + slash + temp.getName())) {
                    ret = false;
                }
            }
        }
        return ret & file.delete();
    }

    /**
     * Method for make directory in current address.
     * @param DirectoryName name of Directory
     */
    public void makeDirectory(String DirectoryName) {
        File newDirectory = new File(saveFileAddress + currAddr + slash + DirectoryName);
        try {
            newDirectory.mkdir();
            System.out.println("make directory \"" + saveFileAddress + "\" successfully.");
        } catch (Exception e) {
            System.out.println("Fail to make directory at \"" + saveFileAddress + "\"");
            e.getStackTrace();
        }
    }
    


}
