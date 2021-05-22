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

public class SaveAndLoad {
    private String address;
    private String saveFileAddress;
    private ArrayList<String> currAddrDirectories;
    private String currAddr;
    private String slash;

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

    private void checkOSAndSetSlash() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            this.slash = "\\";
        } else {
            this.slash = "/";
        }
    }

    public ArrayList<String> getFileNamesInSaveDirectory() {
        ArrayList<String> ret = new ArrayList<String>();
        for (File file: new File(saveFileAddress).listFiles()) {
            ret.add(file.getName());
        }
        return ret;
    }

    public boolean saveFile(String fileName, Object o) {
        FileOutputStream fileStream;
        try {
            fileStream = new FileOutputStream(saveFileAddress + currAddr + slash + fileName + ".ser");
            ObjectOutputStream os = new ObjectOutputStream(fileStream);
            os.writeObject(o);
            os.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object loadFile(String fileName) {
         FileInputStream fileStream;
         ObjectInputStream os;
        try {
            fileStream = new FileInputStream(saveFileAddress + currAddr + slash + fileName + ".ser");
            os = new ObjectInputStream(fileStream);
            Object ret = os.readObject();
            os.close();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addCurrAddr(String directoryName) {
        this.currAddrDirectories.add(directoryName);
        setCurrAddr();
    }

    public boolean removeLastAddr() {
        if (currAddrDirectories.size() == 0) {
            return false;
        } else {
            currAddrDirectories.remove(currAddrDirectories.size() - 1);
            setCurrAddr();
            return true;
        }
    }

    private void setCurrAddr() {
        currAddr = new String();
        for (int i = 0; i < currAddrDirectories.size(); i++) {
            currAddr += slash;
            currAddr += currAddrDirectories.get(i);
        }
    }

    public void removeFile(String fileName) {
        removeFilesWithAddr(saveFileAddress + currAddr + slash + fileName);
    }

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

    public void makeDirectory(String fileName) {
        File newDirectory = new File(saveFileAddress + currAddr + slash + fileName);
        try {
            newDirectory.mkdir();
            System.out.println("make directory \"" + saveFileAddress + "\" successfully.");
        } catch (Exception e) {
            System.out.println("Fail to make directory at \"" + saveFileAddress + "\"");
            e.getStackTrace();
        }
    }
    


}
