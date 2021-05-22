package com.jlee3688gatech;

/**
 * class for print Strings
 * @version 1.0
 * @author Jeonghoon Lee
 */
public class PrintStrings {

    private int size;

    /**
     * Constructor for PeintStrings class.
     * @param size size of box.
     */
    public PrintStrings(int size) {
        this.size = size;
    }

    /**
     * Default Constructor.
     * Default size == 70.
     */
    public PrintStrings() {
        this(70);
    }

    /**
     * helper method for print current address
     * @param str addr
     */
    public void printCurrentAddress(String str) {
        asteriskPrinter(null, null);
        asteriskPrinter(null, str);
        asteriskPrinter(null, null);
    }

    /**
     * helper method for printing the scripts
     * @param header header
     * @param str description
     */
    public void asteriskPrinter(String header, String str) {
        if ((header != null && header.length() + 6 > size) || size <= 2) {
            return;
        }
        if (str == null) {
            for (int i = 0; i < size; i++) {
                System.out.print("#");
            }
            System.out.print("\n");
        } else {
            int preCounter = 2;
            System.out.print("# ");
            
            if (header != null) {
                preCounter += header.length();
                System.out.print(header + ": ");
                preCounter += 2;
            }
            
            int strCounter = 0;
            int line = 0;

            while (strCounter < str.length()) {
                int counter = 0;
                if (line > 0) {
                    System.out.print("# ");
                    counter += 2;
                    for (int i = 0; i < header.length(); i++) {
                        System.out.print(" ");
                        counter++;
                    }
                    System.out.print("  ");
                    counter += 2;
                } else {
                    counter += preCounter;
                }
                while (counter + 2 < size) {
                    if (strCounter < str.length()) {
                        System.out.print(str.charAt(strCounter));
                        strCounter++;
                        counter++;
                    } else {
                        System.out.print(" ");
                        counter++;
                    }
                }
                System.out.println(" #");
                line++;
            }
        }
    }

}