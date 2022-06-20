import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.*;
import java.time.*;

import static java.sql.Date.valueOf;

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

        Shipment test = ShipList[0];
        System.out.println(test.toString());
        System.out.println(ShipList[0].distance());
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
                double Weight = Double.parseDouble(data[1]);
                double Nb = Double.parseDouble(data[2]);
                double Volume = Double.parseDouble(data[3]);
                double ClusterLat = Double.parseDouble(data[4]);
                double ClusterLong = Double.parseDouble(data[5]);
                double OriginLat = Double.parseDouble(data[6]);
                double OriginLong = Double.parseDouble(data[7]);

                shiplist[i] = new Shipment(Date, Weight, Nb, Volume, ClusterLat, ClusterLong, OriginLat, OriginLong);
                i++;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        System.out.println("success");
        return shiplist;

        }
    }

