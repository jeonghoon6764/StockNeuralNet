package com.jlee3688gatech;

import java.util.ArrayList;
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

        SaveAndLoad sl = new SaveAndLoad();

        ArrayList<String> test = sl.getFileNamesInSaveDirectory();
        System.out.println(test.get(0));
        sl.removeFile("abc");

        nNList = new ArrayList<NeuralNet>();
        sc = new Scanner(System.in);
        ps = new PrintStrings();

        boolean continueLoop = true;

        System.out.println("Welcome to Stock Neural-Network");
        while (continueLoop) {
            ps.printCurrentAddress(70, "Main Menu");
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

        ps.printCurrentAddress(70, "Main Menu / Neural Net Menu");
        
        if (nNList.size() > 0) {
            ps.asteriskPrinter(70, null, "Exist Neural Net");
            for (int i = 0; i < nNList.size(); i++) {
                ps.asteriskPrinter(70, nNList.get(i).getName(), nNList.get(i).getNote());
            }
            ps.asteriskPrinter(70, null, null);
        }

        while (continueLoop) {
            System.out.println("1. Build Neural Net.");
            System.out.println("2. See exist Neural Networks specific informations.");
            System.out.println("3. Remove Neural Net.");
            System.out.println("4. Modify note in the Neural Net");
            System.out.println("5. Exit");
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
            } else {
                continueLoop = false;
            }
        }
    }

    private static void selectModifyNote() {
        if (nNList.size() == 0) {
            System.out.println("There is no Neural Net currently exist.");
            return;
        }
        
        ps.printCurrentAddress(70, "Main Menu / Neural Net Menu / Modify Note");
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
            ps.asteriskPrinter(70, null, null);
            ps.asteriskPrinter(70, "Current Note", prevNote);
            ps.asteriskPrinter(70, null, null);
            System.out.print("Type the note: ");
            String newNote = sc.nextLine();
            ps.asteriskPrinter(70, null, null);
            ps.asteriskPrinter(70, "Previous Note", prevNote);
            ps.asteriskPrinter(70, "Current Note", newNote);
            ps.asteriskPrinter(70, null, null);
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
        ps.printCurrentAddress(70, "Main Menu / Neural Net Menu / Remove Neural Net");
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

        ps.printCurrentAddress(70, "Main Menu / Neural Net Menu / See Info");
        listNeuralNet();

        System.out.print("Choose the NeuralNet: ");
        int temp = Integer.parseInt(sc.nextLine());
        temp--;

        ps.asteriskPrinter(70, null, null);
        ps.asteriskPrinter(70, nNList.get(temp).name, nNList.get(temp).getNote());
        int[] nodeInfo = nNList.get(temp).getNodesInfo();
        ps.asteriskPrinter(70, "INPUT SIZE", String.valueOf(nodeInfo[0]));
        String hiddenLayer = new String();
        for (int i = 1; i < nodeInfo.length - 1; i++) {
            if (i != 1) {
                hiddenLayer += " - ";
            }
            hiddenLayer += nodeInfo[i];
        }
        ps.asteriskPrinter(70, "HIDDEN LAYER'S NODE", hiddenLayer);
        ps.asteriskPrinter(70, "OUTPUT LAYER'S NODE", String.valueOf(nodeInfo[nodeInfo.length - 1]));
        ps.asteriskPrinter(70, null, null);
        if (nNList.get(temp).getLog().size() > 0) {
            ps.asteriskPrinter(70, null, "logs");
            for (int i = 0; i < nNList.get(temp).getLog().size(); i++) {
                ps.asteriskPrinter(70, nNList.get(temp).getLog().get(i)[0], nNList.get(temp).getLog().get(i)[1]);
            }
            ps.asteriskPrinter(70, null, null);
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
        ps.printCurrentAddress(70, "Main Menu / Neural Net Menu / Build Neural Net");
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

        ps.asteriskPrinter(70, null, null);
        ps.asteriskPrinter(70, null, "SUMMARY");
        ps.asteriskPrinter(70, null, " ");
        ps.asteriskPrinter(70, "NeuralNet NAME", NNname);
        ps.asteriskPrinter(70, "NeuralNet INFO", NNinfo);
        ps.asteriskPrinter(70, "NUMBER OF INPUT", String.valueOf(NNinput));
        ps.asteriskPrinter(70, "NUMBER OF LAYERS", String.valueOf(NNnumOfLayers));
        for (int i = 0; i < NNnumOfLayers; i++) {
            ps.asteriskPrinter(70, "LAYER" + String.valueOf(i + 1) + "'s NODE NUMEBR", String.valueOf(nodeNums[i]));
        }
        ps.asteriskPrinter(70, "NUMBER OF OUTPUT", String.valueOf(NNnumOfOutput));
        ps.asteriskPrinter(70, null, null);
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
