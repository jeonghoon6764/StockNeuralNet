package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    private static ArrayList<NeuralNet> nNList;
    private static Scanner sc;
    private static PrintStrings ps;
    public static void main( String[] args )
    {
        Calendar cal = UtilMethods.CalendarMaker("20170101000000");
        Calendar tempCal = UtilMethods.CalendarMaker("20180101000000");
        Calendar cal2 = Calendar.getInstance();
        ArrayList<StockDatas> stockDatasArr = new ArrayList<StockDatas>();
        try {
            StockDatas goog = new StockDatas("GOOGLE", "GOOG", cal, cal2);
            StockDatas amzn = new StockDatas("AMAZON", "AMZN", cal, cal2);
            StockDatas tsla = new StockDatas("TESLA", "TSLA", cal, cal2);

            stockDatasArr.add(goog);
            stockDatasArr.add(amzn);
            stockDatasArr.add(tsla);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ExampleMaker exm = new ExampleMaker(stockDatasArr);
        ArrayList<String> dataTypes = new ArrayList<String>();
        dataTypes.add("adjclosed");
        dataTypes.add("open");
        dataTypes.add("low");
        
        System.out.println(exm.getExamples(0, 3, "adjclosed", 5, 10, dataTypes, 5));

        

        

        nNList = new ArrayList<NeuralNet>();
        sc = new Scanner(System.in);
        ps = new PrintStrings();

        boolean continueLoop = true;

        System.out.println("Welcome to Stock Neural-Network");
        while (continueLoop) {
            ps.printCurrentAddress("Main Menu");
            System.out.print("\n");
            
            System.out.println("1. Neural Net Menu.");
            System.out.println("2. Neural Net Menu.");
            System.out.println("3. Load Stock Data Storage");
            System.out.println("4. Exit.");

            int userSelect = Integer.parseInt(sc.nextLine());

            if (userSelect == 1) {
                selectNeuralNetMenu();
            }
        }
        
    }

    private static void selectNeuralNetMenu() {
        boolean continueLoop = true;

        ps.printCurrentAddress("Main Menu / Neural Net Menu");
        
        if (nNList.size() > 0) {
            ps.asteriskPrinter(null, "Exist Neural Net");
            for (int i = 0; i < nNList.size(); i++) {
                ps.asteriskPrinter(nNList.get(i).getName(), nNList.get(i).getNote());
            }
            ps.asteriskPrinter(null, null);
        }

        while (continueLoop) {
            System.out.println("1. Build Neural Net.");
            System.out.println("2. See exist Neural Networks specific informations.");
            System.out.println("3. Remove Neural Net.");
            System.out.println("4. Modify note in the Neural Net");
            System.out.println("5. Save/Load Neural Net");
            System.out.println("6. Exit");
            System.out.print("\n select options: ");
            int temp = 0;
            temp = Integer.parseInt(sc.nextLine());

            if (temp == 1) {
                selectBuildNeuralNet();
            } else if (temp == 2) {
                selectSeeNeuralNet();
            } else if (temp == 3) {
                selectRemoveNeuralNet();
            } else if (temp == 4) {
                selectModifyNote();
            } else if (temp == 5) {
                selectSaveAndLoadNN();
            } else {
                continueLoop = false;
            }
        }
    }

    private static void selectSaveAndLoadNN() {
        SaveAndLoad sl = new SaveAndLoad();
        boolean exit = false;
        boolean deleteMode = false;
        while (!exit) {
            if (deleteMode) {
                ps.asteriskPrinter(null, null);
                ps.asteriskPrinter(null, "DELETE MODE ON");
                ps.asteriskPrinter(null, null);
            }
            ArrayList<String> fileNames = sl.getFileNamesInSaveDirectory();
            ps.asteriskPrinter(null, null);
            if (fileNames.size() == 0) {
                ps.asteriskPrinter(null, "Folder is empty.");
            } else {
                for (int i = 0; i < fileNames.size(); i++) {
                    ps.asteriskPrinter(String.valueOf(i + 1), fileNames.get(i));
                }
            }
            ps.asteriskPrinter(null, null);
            ps.asteriskPrinter("-1", "move to external folder");
            ps.asteriskPrinter("-2", "make folder");
            ps.asteriskPrinter("-3", "save file");
            ps.asteriskPrinter("-9", "turn on/off delete mode");
            ps.asteriskPrinter("0", "exit");
            ps.asteriskPrinter(null, null);

            int input = Integer.parseInt(sc.nextLine());
            if (input == -1) {
                sl.removeLastAddr();
            } else if (input == -2) {
                System.out.print("Folder Name: ");
                sl.makeDirectory(sc.nextLine());
                System.out.print("\n");
            } else if (input == -3) {
                ps.asteriskPrinter(null, null);
                for (int i = 0; i < nNList.size(); i++) {
                    ps.asteriskPrinter(String.valueOf(i + 1), nNList.get(i).getName());
                }
                ps.asteriskPrinter(null, null);
                System.out.print("Which Neural Net you want to save: ");
                int saveNum = Integer.parseInt(sc.nextLine()) - 1;
                System.out.print("\n");
                System.out.print("fileName: ");
                String saveName = sc.nextLine() + ".ser";
                System.out.print("\n");
                sl.saveFile(saveName, nNList.get(saveNum));
            } else if (input == 0) {
                exit = true;
            } else if (input == -9) {
                deleteMode = !deleteMode;
            } else if (deleteMode) {
                sl.removeFile(fileNames.get(input - 1));
            } else {
                if (fileNames.get(input - 1).contains(".ser")) {
                    ps.asteriskPrinter(null, null);
                    NeuralNet loadFile = (NeuralNet)sl.loadFile(fileNames.get(input - 1));
                    nNList.add(loadFile);
                } else {
                    sl.addCurrAddr(fileNames.get(input - 1));
                }
            }
        }
    }

    private static void selectModifyNote() {
        if (nNList.size() == 0) {
            System.out.println("There is no Neural Net currently exist.");
            return;
        }
        
        ps.printCurrentAddress("Main Menu / Neural Net Menu / Modify Note");
        listNeuralNet();
        System.out.println("Choose the NeuralNet or type 0 to cancel");
        System.out.print("input: ");
        int temp = Integer.parseInt(sc.nextLine());
        temp--;
        System.out.print("\n");
        if (temp == -1) {
            return;
        } else {
            String prevNote = nNList.get(temp).getNote();
            ps.asteriskPrinter(null, null);
            ps.asteriskPrinter("Current Note", prevNote);
            ps.asteriskPrinter(null, null);
            System.out.print("Type the note: ");
            String newNote = sc.nextLine();
            ps.asteriskPrinter(null, null);
            ps.asteriskPrinter("Previous Note", prevNote);
            ps.asteriskPrinter("Current Note", newNote);
            ps.asteriskPrinter(null, null);
            System.out.println("Do you really want to change note?");
            System.out.print("Y/n: ");
            char yn = sc.nextLine().charAt(0);
            if (yn == 'Y') {
                nNList.get(temp).setNote(newNote);
                System.out.println("Note change successfully.");
                nNList.get(temp).addLog("change the note from \"" + prevNote + "\" to \"" + newNote + "\".");
            } else {
                System.out.println("Cancel to change note. return to neuralnet menu.");
            }
        }
    }

    private static void listNeuralNet() {
        for (int i = 0; i < nNList.size(); i++) {
            System.out.println((i + 1) + ": " + nNList.get(i).getName());
        }
    }

    private static void selectRemoveNeuralNet() {
        if (nNList.size() == 0) {
            System.out.println("There is no Neural Net currently exist.");
            return;
        }
        ps.printCurrentAddress("Main Menu / Neural Net Menu / Remove Neural Net");
        listNeuralNet();
        System.out.println("Choose the NeuralNet or type 0 to cancel");
        System.out.print("input: ");
        int temp = Integer.parseInt(sc.nextLine());
        temp--;
        System.out.print("\n");
        if (temp == -1) {
            return;
        } else {
            String removeNNName = nNList.get(temp).getName();
            System.out.println("Do you really want to remove \"" + removeNNName + "\" NeuralNet ?");
            System.out.print("Y/n: ");
            char yn = sc.nextLine().charAt(0);
            if (yn == 'Y') {
                nNList.remove(temp);
                System.out.println("remove \"" + removeNNName + "\" successfully.");
            } else {
                System.out.println("remove canceled");
            }
            System.out.println("Return to Neural Net Menu.");
        }
    }

    private static void selectSeeNeuralNet() {
        if (nNList.size() == 0) {
            System.out.println("There is no Neural Net currently exist.");
            return;
        }

        ps.printCurrentAddress("Main Menu / Neural Net Menu / See Info");
        listNeuralNet();

        System.out.print("Choose the NeuralNet: ");
        int temp = Integer.parseInt(sc.nextLine());
        temp--;

        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter(nNList.get(temp).name, nNList.get(temp).getNote());
        int[] nodeInfo = nNList.get(temp).getNodesInfo();
        ps.asteriskPrinter("INPUT SIZE", String.valueOf(nodeInfo[0]));
        String hiddenLayer = new String();
        for (int i = 1; i < nodeInfo.length - 1; i++) {
            if (i != 1) {
                hiddenLayer += " - ";
            }
            hiddenLayer += nodeInfo[i];
        }
        ps.asteriskPrinter("HIDDEN LAYER'S NODE", hiddenLayer);
        ps.asteriskPrinter("OUTPUT LAYER'S NODE", String.valueOf(nodeInfo[nodeInfo.length - 1]));
        ps.asteriskPrinter(null, null);
        if (nNList.get(temp).getLog().size() > 0) {
            ps.asteriskPrinter(null, "logs");
            for (int i = 0; i < nNList.get(temp).getLog().size(); i++) {
                ps.asteriskPrinter(nNList.get(temp).getLog().get(i)[0], nNList.get(temp).getLog().get(i)[1]);
            }
            ps.asteriskPrinter(null, null);
        }

        System.out.println("Look up another Neural Net?");
        System.out.print("Y/n: ");
        char input = sc.nextLine().charAt(0);
        System.out.print("\n");

        if (input == 'Y') {
            selectSeeNeuralNet();
        }
    }

    private static void selectBuildNeuralNet() {
        ps.printCurrentAddress("Main Menu / Neural Net Menu / Build Neural Net");
        System.out.print("\nNeuralNet name: ");
        String NNname = sc.nextLine();
        System.out.print("\nNeuralNet info: ");
        String NNinfo = sc.nextLine();
        System.out.print("\nnumber of input: ");
        int NNinput = Integer.parseInt(sc.nextLine());
        System.out.print("\nnumber of layers: ");
        int NNnumOfLayers = Integer.parseInt(sc.nextLine());
        int[] nodeNums = new int[NNnumOfLayers];

        for (int i = 0; i < NNnumOfLayers; i++) {
            System.out.print("\nnumber of node(s) in layer" + (i + 1) + " :");
            nodeNums[i] = Integer.parseInt(sc.nextLine());
        }

        System.out.print("\nnumber of output: ");
        int NNnumOfOutput = Integer.parseInt(sc.nextLine());

        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter(null, "SUMMARY");
        ps.asteriskPrinter(null, " ");
        ps.asteriskPrinter("NeuralNet NAME", NNname);
        ps.asteriskPrinter("NeuralNet INFO", NNinfo);
        ps.asteriskPrinter("NUMBER OF INPUT", String.valueOf(NNinput));
        ps.asteriskPrinter("NUMBER OF LAYERS", String.valueOf(NNnumOfLayers));
        for (int i = 0; i < NNnumOfLayers; i++) {
            ps.asteriskPrinter("LAYER" + String.valueOf(i + 1) + "'s NODE NUMEBR", String.valueOf(nodeNums[i]));
        }
        ps.asteriskPrinter("NUMBER OF OUTPUT", String.valueOf(NNnumOfOutput));
        ps.asteriskPrinter(null, null);
        System.out.println("Continue?");
        System.out.print("Y/n: ");
        char confirm = sc.nextLine().charAt(0);

        if (confirm == 'Y') {
            NeuralNet NN = new NeuralNet(NNname, NNinfo, NNinput, NNnumOfOutput, nodeNums);
            nNList.add(NN);
            NN.addLog("NeuralNet is made.");
            System.out.println("Successfully making Neural Network.");
        } else {
            System.out.println("Cancel making the Neural Network.");
        }

        System.out.println("Return to Neural Net Menu.");

    }


}
