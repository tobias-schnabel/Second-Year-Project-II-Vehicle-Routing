import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.*;

//Obbe Pulles and Tobias Schnabel
public class Main {

    private static int numLinesInInput = 0;

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
        double[][] distanceMatrix = createDistanceMatrix(ShipList, CustomerList);

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

        Customer[] customerList = new Customer[uniqueCust.length];
        //Creates customer list, note that some customers with different SLC have the same coordinates
        for (int i = 0; i < customerList.length; i++) {
            customerList[i] = new Customer(uniqueCust[i]);
            for(Shipment ship : ShipList) {
                if(customerList[i].getID().equals(ship.getSLC())) {
                    customerList[i].setLat(ship.getOriginLat());
                    customerList[i].setLon(ship.getOriginLong());
                    customerList[i].incrementNum();
                } //if
            } //inner for
        } //outer for
        return customerList;
    }

    private static ArrayList<Date> genDateList(Shipment[] SL){

        ArrayList<Date> dateList = new ArrayList<>();

        for (Shipment shipment : SL) {//creates list of dates
            boolean inList = false;
            Date cur = shipment.getPDate();
            for (Date date : dateList) {
                if (cur.compareTo(date) == 0) {
                    inList = true;
                    break;
                }
            }
            if (!inList) {
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
            numLinesInInput = (int) count;
            System.out.println("Import successful: "+ numLinesInInput + " lines.");
        } catch (Exception e) {
            e.getStackTrace();
        }

        Shipment[] shipList = new Shipment[numLinesInInput];

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(" ");
                String DateString = data[0];
                Date Date = formatter.parse(DateString);
                String SLC = data[1];
                double Weight = Double.parseDouble(data[2]);
                double Nb = Double.parseDouble(data[3]);
                double Volume = Double.parseDouble(data[4]);
                double ClusterLat = Double.parseDouble(data[5]);
                double ClusterLong = Double.parseDouble(data[6]);
                double OriginLat = Double.parseDouble(data[7]);
                double OriginLong = Double.parseDouble(data[8]);

                shipList[i] = new Shipment(Date, SLC, Weight, Nb, Volume, ClusterLat, ClusterLong, OriginLat, OriginLong);

                i++;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

        return shipList;
        } //close get input method

    public static void verifyInput(Shipment[] sl, Customer[] cl) {
        String[] uc = Shipment.getUnique(sl);
        System.out.println("There are " + uc.length + " unique Customers plus 1 Cluster.");

        int n = sl.length;
        int tally = 0;
        for (Customer c : cl) {
            tally += c.getNumShip();
        }

        if (n == tally && n == numLinesInInput) {
            System.out.println("Input integrity verified: Shipment List length of " + n + " MATCHES Tally of individual clusters' number of shipments of " + tally + ", MATCHING input Number of Lines of " + numLinesInInput);
        } else {
            System.out.println("Input integrity could not be verified: Shipment List length of " + n + " DOES NOT MATCH Tally of individual clusters' number of shipments of " + tally + ", DOES NOT MATCH input Number of Lines of " + numLinesInInput);
        }
    }//close verifyInput method

   public static double[][] createDistanceMatrix(Shipment[] SL, Customer[] CL){

       int n = CL.length;
       double[][] dMatrix = new double[n+1][n+1];

       //add the cluster as a "customer"
       //all shipments have the same cluster, we use the first shipment to get the cluster info
       Customer cluster = new Customer(SL[0].getOriginClusterLat(), SL[0].getOriginClusterLong(),"0",0);

       //fill in distance from cluster to customers
       for(int k = 0; k < n; k++){
            double d = cluster.distance(CL[k]);
            dMatrix[0][k+1] = d;
            dMatrix[k+1][0] = d;
       }

       //fill in distance from customer to customer
       for(int i = 0; i < n; i++){
           for(int j = i; j < n; j++){
               double d = CL[i].distance(CL[j]);
               dMatrix[i+1][j+1] = d;
               dMatrix[j+1][i+1] = d;
           }
       }
       return dMatrix;
    }//close distance matrix method

} //close main class



