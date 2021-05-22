package com.jlee3688gatech;

public class PrintStrings {

    /**
     * helper method for print current address
     * @param n number of length
     * @param str addr
     */
    public void printCurrentAddress(int n, String str) {
        asteriskPrinter(n, null, null);
        asteriskPrinter(n, null, str);
        asteriskPrinter(n, null, null);
    }

    /**
     * helper method for printing the scripts
     * @param n number of length
     * @param header header
     * @param str description
     */
    public void asteriskPrinter(int n, String header, String str) {
        if ((header != null && header.length() + 6 > n) || n <= 2) {
            return;
        }
        if (str == null) {
            for (int i = 0; i < n; i++) {
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
                while (counter + 2 < n) {
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