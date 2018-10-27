package rotherator;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class EconomyAllYrs {
    String description;
    ArrayList<Economy1Yr> allYrsFor1Economy;

    public EconomyAllYrs(String description) {
        this.description = description;
        allYrsFor1Economy = new ArrayList<Economy1Yr>();
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Economy1Yr> getAllYrsFor1Economy() {
        return allYrsFor1Economy;
    }

    public static ArrayList<EconomyAllYrs>  ReadEconomyCSV(boolean UNIT_TEST) {
        int indexEconomy = 0;
        ArrayList<EconomyAllYrs> allEconomies = new ArrayList<EconomyAllYrs>();
        try {
            File economyFile = new File("Economy" + String.valueOf(indexEconomy) + ".csv");
            while (economyFile.exists()) {
                String economyFileName = "Economy" + String.valueOf(indexEconomy) + ".csv";
                CSVReader reader = new CSVReader(new FileReader(economyFileName));
                String[] nextLine;  // nextLine[] is an array of values from the line
                nextLine = reader.readNext();  // First 2 lines are just text, first line is description
                allEconomies.add(new EconomyAllYrs(nextLine[0]));
                nextLine = reader.readNext();
                while ((nextLine = reader.readNext()) != null) {
                    if (nextLine.length < 6) {
                        System.out.println("Error in " + economyFileName + ". Need 6 fields");
                        System.exit(-1);
                    }
/*
                    for(int i=0; i<nextLine.length; i++) {
                        System.out.format("%s ",nextLine[i]);
                    }
                    System.out.println(" ");
*/
                    allEconomies.get(indexEconomy).allYrsFor1Economy.add(new Economy1Yr(nextLine));

                }
                indexEconomy++;
                economyFile = new File("Economy" + String.valueOf(indexEconomy) + ".csv");
            }
//            System.out.println("Read " + indexEconomy + " economy files.");
        } catch (IOException ioe) {
            System.out.println(ioe.toString());
        }
        System.out.println("# Economy Files = " + indexEconomy);
        if (UNIT_TEST) {
            for (int i = 0; i < indexEconomy; i++) {
                int jSize = allEconomies.get(i).allYrsFor1Economy.size();
                System.out.println("Size of economy" + i + " is " + jSize);
            }
        }
        return allEconomies;
    }

}
