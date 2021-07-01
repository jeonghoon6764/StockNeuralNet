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
     * @return String
     */
    public String asteriskPrinter(String header, String str) {
        String ret = new String();
        if ((header != null && header.length() + 6 > size) || size <= 2) {
            return "";
        }
        if (str == null) {
            for (int i = 0; i < size; i++) {
                ret += "#";
            }
            ret += "\n";
        } else {
            int preCounter = 2;
            ret += "# ";
            
            if (header != null) {
                preCounter += header.length();
                ret += header + ": ";
                preCounter += 2;
            }
            
            int strCounter = 0;
            int line = 0;

            while (strCounter < str.length()) {
                int counter = 0;
                if (line > 0) {
                    ret += "# ";
                    counter += 2;
                    for (int i = 0; i < header.length(); i++) {
                        ret += " ";
                        counter++;
                    }
                    ret += "  ";
                    counter += 2;
                } else {
                    counter += preCounter;
                }
                while (counter + 2 < size) {
                    if (strCounter < str.length()) {
                        ret += (str.charAt(strCounter));
                        strCounter++;
                        counter++;
                    } else {
                        ret += " ";
                        counter++;
                    }
                }
                ret += " #\n";
                line++;
            }
        }
        return ret;
    }

}