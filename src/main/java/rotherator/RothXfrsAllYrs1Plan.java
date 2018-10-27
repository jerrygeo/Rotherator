package rotherator;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RothXfrsAllYrs1Plan {
    String description;
    ArrayList<RothXfer1Yr> allRothXfers1Plan;

    public RothXfrsAllYrs1Plan(String description) {
        this.description = description;
        allRothXfers1Plan = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }
    public ArrayList<RothXfer1Yr> getAllRothXfers1Plan() { return allRothXfers1Plan; }

    public static ArrayList<RothXfrsAllYrs1Plan> ReadRothPlansCSV() {
        int indexRothXfer = 0;
        ArrayList<RothXfrsAllYrs1Plan> allRothXferPlans = new ArrayList<>();
        try {
            File rothXferFile = new File("RothXfer" + String.valueOf(indexRothXfer) + ".csv");
            while (rothXferFile.exists()) {
                String rothXferFileName = "RothXfer" + String.valueOf(indexRothXfer) + ".csv";
                CSVReader reader = new CSVReader(new FileReader(rothXferFileName));
                String[] nextLine;  // nextLine[] is an array of values from the line
                nextLine = reader.readNext();  // First lines of file is brief description
                allRothXferPlans.add(new RothXfrsAllYrs1Plan(nextLine[0]));
                nextLine = reader.readNext(); // Second line of file is header "Year, Roth Transfer" to guide user in entering data into spreadsheet

                while ((nextLine = reader.readNext()) != null) {
/*
                    for(int i=0; i<nextLine.length; i++) {
                        System.out.format("%s ",nextLine[i]);
                    }
                    System.out.println(" ");
*/
                    allRothXferPlans.get(indexRothXfer).allRothXfers1Plan.add(new RothXfer1Yr(nextLine));
                }
                indexRothXfer++;
                rothXferFile = new File("RothXfer" + String.valueOf(indexRothXfer) + ".csv");
            }
            System.out.println("Read " + indexRothXfer + " RothXfer files.");
        } catch (IOException ioe) {
            System.out.println(ioe.toString());
        }
        return allRothXferPlans;
    }

}
