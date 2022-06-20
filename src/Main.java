import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.*;

//Obbe Pulles and Tobias Schnabel
public class Main {

    public static void main (String[] args) {
        Shipment[] ShipList = null;
        try{
            String filename = "Data.txt";

            ShipList = getInput(filename);
            System.out.println("Import successful.\n");
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }

        String[] uniqueCust = Shipment.getUnique(ShipList);

        System.out.println("There are " + uniqueCust.length + " unique Customers plus 1 Cluster.");

        Customer[] Customerlist = new Customer[uniqueCust.length + 1];
        Shipment cluster = ShipList[0];
        Customerlist[0] = new Customer(cluster.getOriginClusterLat(), cluster.getOriginClusterLong(), "CLUSTER", ShipList.length);

        for (int i = 1; i < Customerlist.length; i++) {
            Customerlist[i] = new Customer(uniqueCust[i-1]);
            for(Shipment ship : ShipList) {
                if(Customerlist[i].getID().equals(ship.getSLC())) {
                    Customerlist[i].setLat(ship.getOriginLat());
                    Customerlist[i].setLon(ship.getOriginLong());
                    Customerlist[i].incrementNum();
                }
            }
        }

        System.out.println(Customerlist[20]);
        System.out.println(Customerlist[0]);

    }

    public static Shipment[] getInput(String filepath)
        throws java.io.FileNotFoundException {
        File file = new File(filepath);
        //get number of lines
        int n = 0;
        try {
            Path file2 = Paths.get(filepath);

            long count = Files.lines(file2).count();
            n = (int) count;
        } catch (Exception e) {
            e.getStackTrace();
        }

        System.out.println("Input has " + n + " lines.");

        Scanner input = new Scanner(System.in);
        Shipment[] shiplist = new Shipment[n];

        DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] data=line.split(" ");
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
        System.out.println("success");
        return shiplist;

        }
    }

