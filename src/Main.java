import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.*;

//Obbe Pulles and Tobias Schnabel
public class Main {

    private static int numLinesinInput = 0;

    public static void main (String[] args) {
        //reads shipment files
        Shipment[] ShipList = null;
        try {
            String filename = "Data.txt";
            ShipList = getInput(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Customer[] CustomerList = getCustomers(ShipList);
        verifyInput(ShipList, CustomerList);

        ArrayList<Date> dateList = genDateList(ShipList);
        //TODO in no particular order
        //1. Create distance/adjacency matrix
        //2. Base case
        //3. Pathfinding / minimum cost flow / packing of trucks
        //4. Local Search / neighbouring optimization
        //5. cleanup
    }
    private static Customer[] getCustomers(Shipment[] ShipList) {
        assert ShipList != null;
        String[] uniqueCust = Shipment.getUnique(ShipList);

        Customer[] Customerlist = new Customer[uniqueCust.length];
        //Creates customer list, note that some customers with different SLC have the same coordinates
        for (int i = 0; i < Customerlist.length; i++) {
            Customerlist[i] = new Customer(uniqueCust[i]);
            for(Shipment ship : ShipList) {
                if(Customerlist[i].getID().equals(ship.getSLC())) {
                    Customerlist[i].setLat(ship.getOriginLat());
                    Customerlist[i].setLon(ship.getOriginLong());
                    Customerlist[i].incrementNum();
                } //if
            } //inner for
        } //outer for
        return Customerlist;
    }

    private static ArrayList<Date> genDateList(Shipment[] SL){

        ArrayList<Date> dateList = new ArrayList<>();

        for (Shipment shipment : SL) {//creates list of dates
            boolean inlist = false;
            Date cur = shipment.getPDate();
            for (Date date : dateList) {
                if (cur.compareTo(date) == 0) {
                    inlist = true;
                    break;
                }
            }
            if (!inlist) {
                dateList.add(cur);
            }
        }
        return dateList;
    }

    public static Shipment[] getInput(String filepath)
        throws java.io.FileNotFoundException {
        File file = new File(filepath);
        //get number of lines
        try {
            Path file2 = Paths.get(filepath);

            long count = Files.lines(file2).count();
            numLinesinInput = (int) count;
            System.out.println("Import successful: "+ numLinesinInput + " lines.");
        } catch (Exception e) {
            e.getStackTrace();
        }

        Shipment[] shiplist = new Shipment[numLinesinInput];

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(" ");
                String Datestring = data[0];
                Date Date = formatter.parse(Datestring);
                String SLC = data[1];
                double Weight = Double.parseDouble(data[2]);
                double Nb = Double.parseDouble(data[3]);
                double Volume = Double.parseDouble(data[4]);
                double ClusterLat = Double.parseDouble(data[5]);
                double ClusterLong = Double.parseDouble(data[6]);
                double OriginLat = Double.parseDouble(data[7]);
                double OriginLong = Double.parseDouble(data[8]);

                shiplist[i] = new Shipment(Date, SLC, Weight, Nb, Volume, ClusterLat, ClusterLong, OriginLat, OriginLong);

                i++;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

        return shiplist;
        } //close get input method

    public static void verifyInput(Shipment[] sl, Customer[] cl) {
        String[] uc = Shipment.getUnique(sl);
        System.out.println("There are " + uc.length + " unique Customers plus 1 Cluster.");

        int n = sl.length;
        int tally = 0;
        for (Customer c : cl) {
            tally += c.getNumShip();
        }

        if (n == tally && n == numLinesinInput) {
            System.out.println("Input integrity verified: Shipment List length of " + n + " MATCHES Tally of individual clusters' number of shipments of " + tally + ", MATCHING input Number of Lines of " + numLinesinInput);
        } else {
            System.out.println("Input integrity could not be verified: Shipment List length of " + n + " DOES NOT MATCH Tally of individual clusters' number of shipments of " + tally + ", DOES NOT MATCH input Number of Lines of " + numLinesinInput);
        }
    }
    } //close class

