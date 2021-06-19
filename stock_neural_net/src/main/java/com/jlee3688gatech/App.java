package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

/**
 * TEST PURPOSE CODE
 * NO GUI
 */
public class App 
{
    private static ArrayList<NeuralNet> nNList;
    private static ArrayList<StockDatas> stList;
    private static Scanner sc;
    private static PrintStrings ps;
    public static void main( String[] args )
    {
        nNList = new ArrayList<NeuralNet>();
        stList = new ArrayList<StockDatas>();
        sc = new Scanner(System.in);
        ps = new PrintStrings();

        boolean continueLoop = true;

        System.out.println("Welcome to Stock Neural-Network");
        while (continueLoop) {
            ps.printCurrentAddress("Main Menu");
            System.out.print("\n");
            
            System.out.println("1. Neural Net Menu.");
            System.out.println("2. StockDatas Menu.");
            System.out.println("3. Leaning NeuralNet");
            System.out.println("4. Exit.");

            int userSelect = Integer.parseInt(sc.nextLine());

            if (userSelect == 1) {
                selectNeuralNetMenu();
            } else if (userSelect == 2) {
                selectStockDatasMenu();
            } else if (userSelect == 3) {
                selectLearning();
            } else {
                continueLoop = false;
            }
        }
        
    }

    private static void selectLearning() {
        if (nNList.size() == 0 || stList.size() == 0) {
            System.out.println("There is no Neural Net or Stock Datas currently exist.");
            return;
        }

        ps.printCurrentAddress("Main Menu / Learning");
        listNeuralNet();

        System.out.print("Choose the NeuralNet: ");
        int nNnumber = Integer.parseInt(sc.nextLine());
        nNnumber--;

        ArrayList<Integer> stocksNum = new ArrayList<Integer>();
        listStockDatas();
        System.out.print("Choose the StockData: ");
        stocksNum.add(Integer.parseInt(sc.nextLine()));
        String input = new String();
        while (!input.equals("-9")) {
            listStockDatas();
            System.out.print("Choose the StockData(-9 if done): ");
            input = sc.nextLine();
            int inputNum = Integer.parseInt(input);
            inputNum--;
            if (inputNum != -10) {
                stocksNum.add(inputNum);
            }
        }

        ArrayList<StockDatas> stocksInput = new ArrayList<StockDatas>();
        for (int i = 0; i < stocksNum.size(); i++) {
            stocksInput.add(stList.get(stocksNum.get(i)));
        } 

        Learning learning = new Learning(nNList.get(nNnumber), stocksInput);
        learning.makeExamples(0);
        learning.backPropLearnNeuralNet(0.1D, 100, 0.1D);
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

    private static void selectStockDatasMenu() {
        boolean continueLoop = true;

        ps.printCurrentAddress("Main Menu / Stock Datas Menu");
        
        if (nNList.size() > 0) {
            ps.asteriskPrinter(null, "Exist Stock Datas");
            for (int i = 0; i < stList.size(); i++) {
                ps.asteriskPrinter(stList.get(i).getName(), stList.get(i).getName());
            }
            ps.asteriskPrinter(null, null);
        }

        while (continueLoop) {
            System.out.println("1. Build Stock Data.");
            System.out.println("2. See exist Stock Data specific informations.");
            System.out.println("3. Remove Stock Data.");
            System.out.println("4. Update Stock Data");
            System.out.println("5. Save/Load Stock Data");
            System.out.println("6. Exit");
            System.out.print("\n select options: ");
            int temp = 0;
            temp = Integer.parseInt(sc.nextLine());

            if (temp == 1) {
                selectBuildStockData();
            } else if (temp == 2) {
                selectSeeStockData();
            } else if (temp == 3) {
                selectRemoveStockData();
            } else if (temp == 4) {
                selectUpdateStockData();
            } else if (temp == 5) {
                selectSaveAndLoadSt();
            } else {
                continueLoop = false;
            }
        }
    }

    private static void selectSaveAndLoadSt() {
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
                for (int i = 0; i < stList.size(); i++) {
                    ps.asteriskPrinter(String.valueOf(i + 1), stList.get(i).getName());
                }
                ps.asteriskPrinter(null, null);
                System.out.print("Which StockData you want to save: ");
                int saveNum = Integer.parseInt(sc.nextLine()) - 1;
                System.out.print("\n");
                System.out.print("fileName: ");
                String saveName = sc.nextLine() + "SD.ser";
                System.out.print("\n");
                sl.saveFile(saveName, stList.get(saveNum));
            } else if (input == 0) {
                exit = true;
            } else if (input == -9) {
                deleteMode = !deleteMode;
            } else if (deleteMode) {
                sl.removeFile(fileNames.get(input - 1));
            } else {
                if (fileNames.get(input - 1).contains(".ser")) {
                    ps.asteriskPrinter(null, null);
                    StockDatas loadFile = (StockDatas)sl.loadFile(fileNames.get(input - 1));
                    stList.add(loadFile);
                } else {
                    sl.addCurrAddr(fileNames.get(input - 1));
                }
            }
        }
    }

    private static void selectUpdateStockData() {
        if (stList.size() == 0) {
            System.out.println("There is no Stock Datas currently exist.");
            return;
        }

        ps.printCurrentAddress("Main Menu / Stock Data Menu / See Info");
        listStockDatas();

        System.out.print("Choose the Stock Datas: ");
        int temp = Integer.parseInt(sc.nextLine());
        temp--;

        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter(stList.get(temp).getName(), stList.get(temp).getTicker());
        ps.asteriskPrinter("DATA START FROM", UtilMethods.CalendarToString(stList.get(temp).getFromDate()));
        ps.asteriskPrinter("DATA END TO", UtilMethods.CalendarToString(stList.get(temp).getToDate()));
        ps.asteriskPrinter(null, null);

        System.out.print("update date : ");
        String updateDate = sc.nextLine();
        Calendar cal = UtilMethods.CalendarMaker(updateDate);

        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter(stList.get(temp).getName(), stList.get(temp).getTicker());
        ps.asteriskPrinter("UPDATE DATE: ", UtilMethods.CalendarToString(cal));
        ps.asteriskPrinter(null, null);

        System.out.println("Continue?");
        System.out.print("Y/n: ");
        char confirm = sc.nextLine().charAt(0);

        if (confirm == 'Y') {
            stList.get(temp).updateStockData(cal);
            System.out.println("Successfully making Stock Datas.");
        } else {
            System.out.println("Cancel making the Stock Datas.");
        }
        System.out.println("Return to Stock Data Menu.");
    }

    private static void selectRemoveStockData() {
        if (nNList.size() == 0) {
            System.out.println("There is no Stock Datas currently exist.");
            return;
        }
        ps.printCurrentAddress("Main Menu / Stock Data Menu / Remove Stock Data");
        listStockDatas();
        System.out.println("Choose the Stock Data or type 0 to cancel");
        System.out.print("input: ");
        int temp = Integer.parseInt(sc.nextLine());
        temp--;
        System.out.print("\n");
        if (temp == -1) {
            return;
        } else {
            String removeStName = stList.get(temp).getName();
            System.out.println("Do you really want to remove \"" + removeStName + "\" Data ?");
            System.out.print("Y/n: ");
            char yn = sc.nextLine().charAt(0);
            if (yn == 'Y') {
                stList.remove(temp);
                System.out.println("remove \"" + removeStName + "\" successfully.");
            } else {
                System.out.println("remove canceled");
            }
            System.out.println("Return to Stock Data Menu.");
        }
    }

    private static void selectSeeStockData() {
        if (stList.size() == 0) {
            System.out.println("There is no Stock Datas currently exist.");
            return;
        }

        ps.printCurrentAddress("Main Menu / Stock Data Menu / See Info");
        listStockDatas();

        System.out.print("Choose the Stock Datas: ");
        int temp = Integer.parseInt(sc.nextLine());
        temp--;

        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter(stList.get(temp).getName(), stList.get(temp).getTicker());
        ps.asteriskPrinter("DATA START FROM", UtilMethods.CalendarToString(stList.get(temp).getFromDate()));
        ps.asteriskPrinter("DATA END TO", UtilMethods.CalendarToString(stList.get(temp).getToDate()));
        ps.asteriskPrinter(null, null);
        
        System.out.println("Look up another Stock Data?");
        System.out.print("Y/n: ");
        char input = sc.nextLine().charAt(0);
        System.out.print("\n");

        if (input == 'Y') {
            selectSeeStockData();
        }
    }

    private static void listStockDatas() {
        for (int i = 0; i < stList.size(); i++) {
            System.out.println((i + 1) + ": " + stList.get(i).getName());
        }
    }

    private static void selectBuildStockData() {
        ps.printCurrentAddress("Main Menu / Stock Data Menu / Build Stock Data");
        System.out.print("\nStock Data Name: ");
        String stockName = sc.nextLine();
        System.out.print("\nTicker Symbol: ");
        String stockTicker = sc.nextLine();
        System.out.print("\nDate Start From: ");
        String startFrom = sc.nextLine();
        System.out.print("\nDate End To: ");
        String endTo = sc.nextLine();

        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter(null, "SUMMARY");
        ps.asteriskPrinter(null, " ");
        ps.asteriskPrinter("Stock Data NAME", stockName);
        ps.asteriskPrinter("Stock Ticker Symbol", stockTicker);
        ps.asteriskPrinter("DATE START FROM", startFrom);
        ps.asteriskPrinter("DATE END TO", endTo);
        ps.asteriskPrinter(null, null);
        System.out.println("Continue?");
        System.out.print("Y/n: ");
        char confirm = sc.nextLine().charAt(0);

        if (confirm == 'Y') {
            Calendar from = UtilMethods.CalendarMaker(startFrom);
            Calendar to = UtilMethods.CalendarMaker(endTo);
            StockDatas stock;
            try {
                stock = new StockDatas(stockName, stockTicker, from, to);
                stList.add(stock);
            } catch (IOException e) {
                System.out.println("Failing to make Stock Datas. return main menu");
            }
            System.out.println("Successfully making Stock Datas.");
        } else {
            System.out.println("Cancel making the Stock Datas.");
        }
        System.out.println("Return to Stock Data Menu.");
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
                String saveName = sc.nextLine() + "NN.ser";
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
