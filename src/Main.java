import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.*;

//Obbe Pulles and Tobias Schnabel
public class Main {

    //TODO in no particular order
    //1. minimum cost flow? / packing of trucks?
    //2. Local Search / neighbouring optimization
    //3. cleanup
    //basic idea: take truck -> go to nearest loc -> ... -> either maxvol/maxweight, repeat
    //improve with localsearch

    //add method to customer class to assign unique ID to each shipment (maybe)

    //interact truck objects with customer objects (visited variable, #packages still ot be picked up)

    //(Tobias) extend Customers class to subclass cluster, add tally variable to verify that total # of shipments
    //up covers everything in ship list

    //

    static String pattern = "dd/MM/yyyy";
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

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
        double[][] distanceMatrix = createDistanceMatrix(CustomerList);

        solve(distanceMatrix, dateList.get(0), ShipList, CustomerList);

    }

    public static void solve(double[][] dMatrix, Date date, Shipment[] SL,Customer[] CL){

        //start with one truck
        double TFC = 450; double TVC = 0;
        double FC = 450; double VC = 1.5;

        //creates arraylist of the to be shipped shipments
        ArrayList<Shipment> curShipments = new ArrayList<>();
        for (Shipment shipment: SL) {
            if (shipment.getPDate().compareTo(date) == 0) {
                curShipments.add(shipment);
            }
        }

        //saves current date
        Date currentDate = curShipments.get(0).getPDate();

        //create list of trucks
        int count = 0;
        ArrayList<Truck> truckArrayList = new ArrayList<>();
        truckArrayList.add(new Truck(count));
        Truck truck = truckArrayList.get(0);


        //while there are still shipments to be delivered
        while(curShipments.size() != 0){

            //find the closest customer who needs a delivery
            Customer next = getMinDistCustomer(curShipments, truck, dMatrix, CL);
            String ID = next.getID();


            //removes all shipments that belong to this customer from the shipment list
            //checks whether we need a new truck and move the truck
            for(int i = 0; i < curShipments.size(); i++){
                //checks if shipment i belongs to the customer
                if(curShipments.get(i).getSLC().equals(ID)){
                    Shipment s = curShipments.get(i);

                    //if either w>22000 or v>82 would happen, add new truck to trucklist
                    if(s.getWeight() + truck.getCurrentWeight() > 22000 || s.getVolume() + truck.getCurrentVolume()>82){

                        //TODO: PROBLEM: when we add new truck, we are NOT optimizing the first distance don't know yet how to solve (maybe break and boolean)
                        count++;
                        truckArrayList.add(new Truck(count));
                        truck = truckArrayList.get(truckArrayList.size()-1);
                        TFC+=FC;
                    }

                    truck.addVolume(s.getVolume());
                    truck.addWeight(s.getWeight());
                    truck.addShipment(s);

                    curShipments.remove(i);
                    i--;
                }
            }
            TVC += VC*next.distance(truck.getRoute().get(truck.getRoute().size()-1));
            truck.addToRoute(next);

        }

        //make all trucks go back to cluster
        for(Truck t: truckArrayList){
            Customer last = t.getEnd();
            t.addToRoute(CL[0]);
            TVC += VC*last.distance(CL[0]);
        }

      // TODO BASIC IDEA OF IMPROVING:
      // Step 1: take all trucks in the current list
      // Step 2: LOCALSEARCH: 2-opt-exchange
      // Step 3: LOCALSEARCH: Move
      // Step 4: LOCALSEARCH: Swap




        System.out.println("Trucks and cost on the date: " + simpleDateFormat.format(currentDate));
       System.out.println("Number of total trucks: " + truckArrayList.size());
       System.out.println("The cost of delivery is: "+String.format("%.2f",TFC+TVC));

    }

    public static Customer getMinDistCustomer(ArrayList<Shipment> CSL, Truck t, double[][] matrix, Customer[] CL){

        String locString = t.getLocation();
        int loc = -1;

        Customer[] currentCL = getCustomers(toArray(CSL));


        //get location of truck to use in distance matrix
        for(int i = 0; i < CL.length; i++){
            if(CL[i].getID().equals(locString)){
                loc = i;
            }
        }

        //setup minPos
        int minPos = -1;
        double min = Double.MAX_VALUE;

        //goes through the row of the current location of truck's distance matrix
        for(int i = 1; i<matrix.length; i++){

            //don't want to stay at the current location
            if (i != loc) {

                //goes through the current customers list(retrieved from the shipments)
                for (Customer customer : currentCL) {

                    //if the customer at position "i" is in the list of customers that need a delivery
                    //AND we are closest to that customer -> update the minimum
                    if (customer.getID().equals(CL[i].getID()) && matrix[loc][i] < min) {
                        min = matrix[loc][i];
                        minPos = i;
                        break;
                    }//close if
                }//close for
            }//close if
        }//close for
        return CL[minPos];
    }//close method
    public static Shipment[] toArray(ArrayList<Shipment> ASL){
        Shipment[] SL = new Shipment[ASL.size()];
        for(int i = 0; i< ASL.size(); i++){
            SL[i] = ASL.get(i);
        }
        return SL;
    }
    private static Customer[] getCustomers(Shipment[] ShipList) {
        assert ShipList != null;
        String[] uniqueCust = Shipment.getUnique(ShipList);

        Customer[] customerList = new Customer[uniqueCust.length + 1];
        customerList[0] = new Customer(ShipList[0].getOriginClusterLat(), ShipList[0].getOriginClusterLong(),"0",0);
        customerList[0].setNumShip(ShipList.length);
        //Creates customer list, note that some customers with different SLC have the same coordinates
        for (int i = 1; i < customerList.length; i++) {
            customerList[i] = new Customer(uniqueCust[i -1]);
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
        for (int i = 1; i < cl.length; i++) {

                tally += cl[i].getNumShip();
            }

        if (n == tally && n == numLinesInInput) {
            System.out.println("Input integrity verified: Shipment List length of " + n + " MATCHES Tally of individual clusters' number of shipments of " + tally + ", MATCHING input Number of Lines of " + numLinesInInput + "\n");
        } else {
            System.out.println("Input integrity could not be verified: Shipment List length of " + n + " DOES NOT MATCH Tally of individual clusters' number of shipments of " + tally + ", DOES NOT MATCH input Number of Lines of " + numLinesInInput + "\n");
        }
    }//close verifyInput method

   public static double[][] createDistanceMatrix(Customer[] CL){

       int n = CL.length;
       double[][] dMatrix = new double[n][n];

       //fill in distance from customer to customer
       for(int i = 0; i < n; i++){
           for(int j = i; j < n; j++){
               double d = CL[i].distance(CL[j]);
               dMatrix[i][j] = d;
               dMatrix[j][i] = d;
           }
       }
       return dMatrix;
    }//close distance matrix method



} //close main class



