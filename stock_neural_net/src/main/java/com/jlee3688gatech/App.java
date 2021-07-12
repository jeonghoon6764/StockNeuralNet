package com.jlee3688gatech;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 * TEST PURPOSE CODE
 * NO GUI
 * 
 * TLT
 * GLD
 * IYY
 * NDAQ
 * 
 */
public class App 
{
    private static ArrayList<NeuralNet> nNList;
    private static ArrayList<StockDatas> stList;
    private static ArrayList<RecentInputData> recentInputs;
    private static Scanner sc;
    private static PrintStrings ps;
    private static StockList st;

    public static void main( String[] args )
    {

        ServerAndClient serverAndClient = new ServerAndClient();
        serverAndClient.getMyAddress();
        
        UtilMethods.initialize();
        StartController.main(args);
        nNList = new ArrayList<NeuralNet>();
        stList = new ArrayList<StockDatas>();
        recentInputs = new ArrayList<RecentInputData>();
        sc = new Scanner(System.in);
        ps = new PrintStrings(140);
        st = new StockList();


        boolean continueLoop = true;

        System.out.println("Welcome to Stock Neural-Network");
        while (continueLoop) {
            ps.printCurrentAddress("Main Menu");
            System.out.print("\n");
            
            System.out.println("1. Neural Net Menu.");
            System.out.println("2. StockDatas Menu.");
            System.out.println("3. Leaning NeuralNet");
            System.out.println("4. Running NeuralNet");
            System.out.println("5. Automatic_Macro");
            System.out.println("6. AutomaticGetRecent");
            System.out.println("7. Exit.");

            int userSelect = Integer.parseInt(sc.nextLine());

            if (userSelect == 1) {
                selectNeuralNetMenu();
            } else if (userSelect == 2) {
                selectStockDatasMenu();
            } else if (userSelect == 3) {
                selectLearning();
            } else if (userSelect == 4) {
                runningNeuralNet();
            } else if (userSelect == 5) {
                macro_run();
            } else if (userSelect == 6) {
                autoGet();
            } else {
                continueLoop = false;
            }
        }
    }

    private static void autoGet() {
        autoBuildStockData("20210610", "20210625");
        
        SaveAndLoad sl1 = new SaveAndLoad();
        sl1.addCurrAddr("NeuralNetData");
        sl1.addCurrAddr("1.0");
        SaveAndLoad sl2 = new SaveAndLoad();
        sl2.addCurrAddr("NeuralNetData");
        sl2.addCurrAddr("2.0");

        ArrayList<String> fileNames1 = sl1.getFileNamesInSaveDirectory();
        ArrayList<String> fileNames2 = sl2.getFileNamesInSaveDirectory();

        ArrayList<NeuralNet> nNList1 = new ArrayList<NeuralNet>();
        ArrayList<NeuralNet> nNList2 = new ArrayList<NeuralNet>();

        for (int i = 0; i < fileNames1.size(); i++) {
            System.out.println("load " + fileNames1.get(i) + "file now...");
            NeuralNet loadFile = (NeuralNet)sl1.loadFile(fileNames1.get(i));
            nNList1.add(loadFile);
        }
        for (int i = 0; i < fileNames2.size(); i++) {
            System.out.println("load " + fileNames2.get(i) + "file now...");
            NeuralNet loadFile = (NeuralNet)sl2.loadFile(fileNames2.get(i));
            nNList2.add(loadFile);
        }

        NeuralNetSet ns1 = new NeuralNetSet(nNList1);
        NeuralNetSet ns2 = new NeuralNetSet(nNList2);

        ExampleMaker ex = new ExampleMaker(stList);

        ArrayList<String> inputTypes = new ArrayList<String>();
        inputTypes.add("adjclosed");
        inputTypes.add("volume");
        RecentInputData rid = ex.getRecentInput(inputTypes, 5);
        System.out.println("rid start from" + UtilMethods.CalendarToString(rid.getDateStartOf()));
        System.out.println("rid end to" + UtilMethods.CalendarToString(rid.getDateEndOf()));

        NeuralNetSetOutput ns1Out = ns1.getRecentOutputData(rid);
        NeuralNetSetOutput ns2Out = ns2.getRecentOutputData(rid);

        for (int i = 0; i < ns1Out.getSize(); i++) {
            ps.asteriskPrinter(null, null);
            ps.asteriskPrinter("1.0", ns1Out.getStockName(i));
            Double temp = ns1Out.getTrueValue(i);
            ps.asteriskPrinter("TRUE VALUE", temp.toString());
            temp = ns1Out.getFalseValue(i);
            ps.asteriskPrinter("FALSE VALUE", temp.toString());
        }
        ps.asteriskPrinter(null, null);
        System.out.println("\n\n\n");

        for (int i = 0; i < ns2Out.getSize(); i++) {
            ps.asteriskPrinter(null, null);
            ps.asteriskPrinter("2.0", ns2Out.getStockName(i));
            Double temp = ns2Out.getTrueValue(i);
            ps.asteriskPrinter("TRUE VALUE", temp.toString());
            temp = ns2Out.getFalseValue(i);
            ps.asteriskPrinter("FALSE VALUE", temp.toString());
        }
        ps.asteriskPrinter(null, null);
        System.out.println("\n\n\n");
    }

    
    private static void autoBuildStockData(String dateFrom, String dateTo) {

        StockList st = new StockList();

        for (int i = 0; i < st.getSize(); i++) {
            String name = st.getNameAndTicker(i).get(0);
            String ticker = st.getNameAndTicker(i).get(1);
            Calendar from = UtilMethods.CalendarMaker(dateFrom);
            Calendar to = UtilMethods.CalendarMaker(dateTo);

            while(true) {
                try {
                    StockDatas tempStock = new StockDatas(name, ticker, from, to);
                    stList.add(tempStock);
                    break;
                } catch (IOException e) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }

    }

    private static void macro_run() {
        autoBuildStockData("20000101", "20210622");
        ArrayList<String> sepList = st.getSeparateNameList(1, 0);
        //ArrayList<String> sepList = st.getSeparateNameList(2, 1);
        
        for (int i = 0; i < stList.size(); i++) {
            if (sepList.contains(stList.get(i).getName())) {
                int[] hiddenLayer = {280, 280, 280};
                nNList.add(new NeuralNet(stList.get(i).getName(), "AUTO_GENERATED", 280, 2, hiddenLayer));
            }
        }

        ArrayList<Double> trueRatesInExample = new ArrayList<Double>();
        ArrayList<Double> trueRates = new ArrayList<Double>();
        ArrayList<Double> falseRates = new ArrayList<Double>();
        ArrayList<String> companyNames = new ArrayList<String>();

        int num = 0;
        for (int i = 0; i < stList.size(); i++) {
            if (sepList.contains(stList.get(i).getName())) {
                int targetTicker = i;
                double learningRate = 0.1;
                int maxIter = 300;
                double minError = 0.01;
                Learning learning = new Learning(nNList.get(num), stList);
                int targetNumOfInc = 2;
                String targetDataType = "adjclosed";
                int targetDataCountFrom = 5;
                int targetDataCountTo = 6;
                double incRate = 0.02;
                ArrayList<String> inputTypes = new ArrayList<String>();
                inputTypes.add("adjclosed");
                inputTypes.add("volume");
                Integer numOfDataFromCounter = 5;
                trueRatesInExample.add(learning.makeExamples(targetTicker, targetNumOfInc, targetDataType,
                targetDataCountFrom, targetDataCountTo, incRate, inputTypes, numOfDataFromCounter));
                RecentInputData rid = learning.getRecentInput();
                learning.backPropLearnNeuralNet(learningRate, maxIter, minError);
                ArrayList<Double> retVal = nNList.get(num).feedFoward(rid.getRecentInput());
                trueRates.add(retVal.get(0));
                falseRates.add(retVal.get(1));
                companyNames.add(stList.get(i).getName());

                System.out.println((i + 1) + "th calculation finished.");
                num++;
            } else {
                System.out.println("skip " + (i + 1) + "th calculation.");
            }
        }

        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter(null, "SUMMARY");
        for (int i = 0; i < companyNames.size(); i++) {
            ps.asteriskPrinter(null, null);
            ps.asteriskPrinter(null, companyNames.get(i));
            ps.asteriskPrinter("true ratio in example", trueRatesInExample.get(i).toString());
            ps.asteriskPrinter("TRUE RATE", trueRates.get(i).toString());
            ps.asteriskPrinter("FALSE RATE", falseRates.get(i).toString());
        }
        ps.asteriskPrinter(null, null);

        if (trueRatesInExample.size() == trueRates.size()) {
            System.out.println("!!!!!!");
        }
        if (trueRates.size() == falseRates.size()) {
            System.out.println("!!!!");
        }
        if (falseRates.size() == companyNames.size()) {
            System.out.println("!!");
        }
    }

    private static void runningNeuralNet() {
        if (nNList.size() == 0 || recentInputs.size() == 0) {
            System.out.println("There is no Neural Net or RecentInputs currently exist.");
            return;
        }
        ps.printCurrentAddress("Main Menu / Learning");
        listNeuralNet();

        System.out.print("Choose the NeuralNet: ");
        int nNNumber = Integer.parseInt(sc.nextLine());
        nNNumber--;

        listRecentDatas();

        System.out.print("Choose the RecentData: ");
        int rDNumber = Integer.parseInt(sc.nextLine());
        rDNumber--;

        if (recentInputs.get(rDNumber).getNumOfInput() != nNList.get(nNNumber).inputSize) {
            System.out.println("input size does not match. return to menu.");
            return;
        }

        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter(null, "SUMMARY-Neural Net");
        ps.asteriskPrinter("name", nNList.get(nNNumber).getName());
        ps.asteriskPrinter("note", nNList.get(nNNumber).getNote());
        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter(null, "SUMMARY-RecentData");
        ps.asteriskPrinter("Date Created", UtilMethods.CalendarToString(recentInputs.get(rDNumber).getCreatedDate()));
        ps.asteriskPrinter("Date Start from", UtilMethods.CalendarToString(recentInputs.get(rDNumber).getDateStartOf()));
        ps.asteriskPrinter("Date End to", UtilMethods.CalendarToString(recentInputs.get(rDNumber).getDateEndOf()));
        String str = recentInputs.get(rDNumber).getStockNames().get(0);
        for (int i = 1; i < recentInputs.get(rDNumber).getStockNames().size(); i++) {
            str += ", " + recentInputs.get(rDNumber).getStockNames().get(i);
        }
        ps.asteriskPrinter("Stocks", str);
        ps.asteriskPrinter(null, null);

        System.out.print("\n\n\n continue? (Y/n) : ");
        char confirm = sc.nextLine().charAt(0);

        if (confirm == 'Y') {
            ArrayList<Double> retVal = nNList.get(nNNumber).feedFoward(recentInputs.get(rDNumber).getRecentInput());
            Double trueValue = retVal.get(0);
            Double falseValue = retVal.get(1);

            ps.asteriskPrinter(null, null);
            ps.asteriskPrinter(null, "Neural Net Result");
            ps.asteriskPrinter("True Value : ", trueValue.toString());
            ps.asteriskPrinter("False Value : ", falseValue.toString());
            ps.asteriskPrinter(null, null);

            System.out.println("press any key and enter to go main menu.");
            sc.nextLine().charAt(0);
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
        stocksNum.add(Integer.parseInt(sc.nextLine()) - 1);
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

        listStockDatas();
        System.out.print("\nChoose target ticker : ");
        Integer targetTicker = Integer.parseInt(sc.nextLine()) - 1;

        System.out.print("\nLearning Rate : ");
        Double learningRate = Double.parseDouble(sc.nextLine());

        System.out.print("\nMax Iteration : ");
        Integer maxIter = Integer.parseInt(sc.nextLine());

        System.out.print("\nMinimum Error : ");
        Double minError = Double.parseDouble(sc.nextLine());

        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter(null, "SUMMARY");
        ps.asteriskPrinter(null, null);

        ps.asteriskPrinter(null, "Stock List");
        for (int i = 0; i < stocksInput.size(); i++) {
            Integer num = i;
            num++;
            if (i == targetTicker) {
                ps.asteriskPrinter(num.toString(), stocksInput.get(i).getName() + "<Target>");
            } else {
                ps.asteriskPrinter(num.toString(), stocksInput.get(i).getName());
            }
        }
        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter("Learning Rate", learningRate.toString());
        ps.asteriskPrinter("Max Iteration", maxIter.toString());
        ps.asteriskPrinter("Minimum Error", minError.toString());
        ps.asteriskPrinter(null, null);

        System.out.print("\n\n\n continue? (Y/n) : ");
        char confirm = sc.nextLine().charAt(0);

        if (confirm == 'Y') {
            learningHelper(nNnumber, targetTicker, stocksInput, learningRate, maxIter, minError);
        }
    }

    private static void learningHelper(int nNnumber, int targetTickerNum, ArrayList<StockDatas> stocksInput, double learningRate, int maxIteration, double minError) {
        Learning learning = new Learning(nNList.get(nNnumber), stocksInput);
        System.out.print("\nCustom mode (Y/n) : ");
        char confirm = sc.nextLine().charAt(0);

        Integer targetNumOfInc = 3;
        String targetDataType = "adjclosed";
        Integer targetDataCountFrom = 5;
        Integer targetDataCountTo = 10;
        Double incRate = 0.01;
        ArrayList<String> inputTypes = selectInputTypes(false);
        Integer numOfDataFromCounter = 5;

        if (confirm == 'Y') {
            System.out.print("\nTarget number of increase day [default = 3] : ");
            targetNumOfInc = Integer.parseInt(sc.nextLine());

            System.out.print("\nTarget data Type [default = adjclosed] : ");
            targetDataType = sc.nextLine();

            System.out.print("\nTarget data count from [default = 5] : ");
            targetDataCountFrom = Integer.parseInt(sc.nextLine());

            System.out.print("\nTarget data count to [default = 10] : ");
            targetDataCountTo = Integer.parseInt(sc.nextLine());

            System.out.print("\nminimum increase rate from base [default = 0.01] : ");
            incRate = Double.parseDouble(sc.nextLine());

            inputTypes = selectInputTypes(true);

            System.out.print("\nNumber of data from counter [default = 5] : ");
            numOfDataFromCounter = Integer.parseInt(sc.nextLine());
        }

        System.out.println("\n\n");

        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter(null, "SUMMARY");
        ps.asteriskPrinter("Target Increase day", targetNumOfInc.toString());
        ps.asteriskPrinter("Target Data Type", targetDataType);
        ps.asteriskPrinter("Target Data Count From", targetDataCountFrom.toString());
        ps.asteriskPrinter("Target Data Count To", targetDataCountTo.toString());
        ps.asteriskPrinter("Increase RATE", incRate.toString());
        ps.asteriskPrinter("Number of Data from Counter", numOfDataFromCounter.toString());
        ps.asteriskPrinter(null, null);
        ps.asteriskPrinter(null, "INPUT Types");
        for (int i = 0; i < inputTypes.size(); i++) {
            Integer temp = i + 1;
            ps.asteriskPrinter(temp.toString(), inputTypes.get(i));
        }
        ps.asteriskPrinter(null, null);

        System.out.print("\n\n\n continue? (Y/n/R) : ");
        char ans = sc.nextLine().charAt(0);

        if (ans == 'Y') {
            System.out.println("\n\nMaking Examples/recentInput...");
            double trueRate = learning.makeExamples(targetTickerNum, targetNumOfInc, targetDataType,
             targetDataCountFrom, targetDataCountTo, incRate, inputTypes, numOfDataFromCounter);
            System.out.println("\n\nGetting Recent Input...");
            RecentInputData recentInput = learning.getRecentInput();

            System.out.println("RecentData Start of : " + UtilMethods.CalendarToString(recentInput.getDateStartOf()));
            System.out.println("RecentData End to : " + UtilMethods.CalendarToString(recentInput.getDateEndOf()));
            
            System.out.println("True rate : " + trueRate);
            System.out.print("\n\n continue? (Y/n/R) : ");
            ans = sc.nextLine().charAt(0);
            
            if (ans == 'Y') {
                recentInputs.add(recentInput);
                learning.backPropLearnNeuralNet(learningRate, maxIteration, minError);
            } else if (ans == 'R') {
                learningHelper(nNnumber, targetTickerNum, stocksInput, learningRate, maxIteration, minError);
            }
        } else if (ans == 'R') {
            learningHelper(nNnumber, targetTickerNum, stocksInput, learningRate, maxIteration, minError);
        }
    }

    private static ArrayList<String> selectInputTypes(boolean custom) {
        ArrayList<String> inputTypes = new ArrayList<String>();
        boolean[] userSelect = new boolean[6];
        ArrayList<String> ret = new ArrayList<String>();

        inputTypes.add("adjclosed");
        inputTypes.add("close");
        inputTypes.add("high");
        inputTypes.add("low");
        inputTypes.add("open");
        inputTypes.add("volume");

        if(!custom) {
            return inputTypes;
        }

        for (int i = 0; i < inputTypes.size(); i++) {
            Integer temp = i + 1;
            System.out.println(temp.toString() + " : " + inputTypes.get(i));
        }

        System.out.print("\n\n\n choose type of inputs : ");
        int num = Integer.parseInt(sc.nextLine());
        num--;

        userSelect[num] = true;

        while(num != -10) {
            for (int i = 0; i < inputTypes.size(); i++) {
                Integer temp = i + 1;
                if (userSelect[i]) {
                    System.out.println(temp.toString() + " : " + inputTypes.get(i) + " <Selected>");
                } else {
                    System.out.println(temp.toString() + " : " + inputTypes.get(i));
                }
            }

            System.out.print("\n\n\n choose type of inputs (-9 if done): ");
            num = Integer.parseInt(sc.nextLine());
            num--;
            if (num != -10) {
                userSelect[num] = !userSelect[num];
            }
        }

        for (int i = 0; i < userSelect.length; i++) {
            if (userSelect[i]) {
                ret.add(inputTypes.get(i));
            }
        }

        return ret;
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
        
        if (stList.size() > 0) {
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
        if (stList.size() == 0) {
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

    private static void listRecentDatas() {
        for (int i = 0; i < recentInputs.size(); i++) {
            System.out.println((i + 1) + ": " + UtilMethods.CalendarToString(recentInputs.get(i).getCreatedDate()));
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
