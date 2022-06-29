import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.file.*;

//Obbe Pulles and Tobias Schnabel
public class Main {
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
//        for(int i = 0; i < dateList.size(); i++) {
//           System.out.println("Date #" + (i=1) );
//            solve(distanceMatrix, dateList.get(i), ShipList, CustomerList);
//            System.out.println();
//        }

        int counter = 1;
        for (Date date : dateList) {
            System.out.println("Date #" + counter + ": " + simpleDateFormat.format(date) );
            solve(distanceMatrix, date, ShipList, CustomerList);
            counter++;
        }
    }

    public static void solve(double[][] dMatrix, Date date, Shipment[] SL,Customer[] CL){

        //start with one truck
        double TFC = 0; double TVC = 0;
        double FC = 450; double VC = 1.5;

        //creates arraylist of the to be shipped shipments
        ArrayList<Shipment> curShipments = new ArrayList<>();
        for (Shipment shipment: SL) {
            if (shipment.getPDate().compareTo(date) == 0) {
                curShipments.add(shipment);
            }
        }

        //create list of trucks
        ArrayList<Truck> truckArrayList = new ArrayList<>();
        int count = 0;
        Truck truck = new Truck(count);
        truckArrayList.add(truck);

        //while there are still shipments to be delivered
       while(curShipments.size() != 0){

           //find the closest customer who needs a delivery
           Customer next = getMinDistCustomer(curShipments, truck, dMatrix, CL);
           String ID = next.getID();


           //removes all shipments that belong to this customer from the shipment list
           //checks whether we need a new truck and move the truck
           for(int i = 0; i < curShipments.size(); i++){
               //checks if shipment i belongs to the customer
               if(curShipments.get(i).getCustomer().getID().equals(ID)){
                   Shipment s = curShipments.get(i);

                   //if either w>22000 or v>82 would happen, add new truck to trucklist and start from cluster
                   if(s.getWeight() + truck.getCurrentWeight() > 22000 || s.getVolume() + truck.getCurrentVolume() > 82){

                       count++;
                       truckArrayList.add(new Truck(count));
                       truck = truckArrayList.get(truckArrayList.size()-1);
                       //get out of for-loop to find minDistCustomer for new truck
                       break;

                   }//close new truck if

                   truck.addVolume(s.getVolume());
                   truck.addWeight(s.getWeight());
                   truck.addShipment(s);

                   curShipments.remove(i);
                   //put the location of the customer into the route of the truck if it's not already in there
                   if(!inRoute(next,truck)){
                       truck.addToRoute(next);
                   }
                   i--;
               }//close shipment if
           }//close for-loop

       }

       double totalCostBefore = 0;
       //make all trucks go back to cluster
       for(Truck t: truckArrayList){
           t.addToRoute(CL[0]);
           totalCostBefore += FC + VC * getRouteLength(t);
       }
       System.out.println("Total cost before optimization: " + String.format("%.2f",totalCostBefore));
       boolean improvement = true;

       //after each improvement, check whether new improvements can be made
       while(improvement) {
           improvement = false;
           //2-opt exchange
           for (Truck t : truckArrayList) {
               ArrayList<Customer> oldRoute = t.getRoute();
               do2optExchange(t);
               if (!oldRoute.equals(t.getRoute())) {
                   improvement = true;
               }
           }

           if (truckArrayList.size() > 1) {
               for (int i = 0; i < truckArrayList.size(); i++) {
                   for (int j = 0; j < truckArrayList.size(); j++) {
                       if (i != j) {
                           ArrayList<Customer> oldRoute_i = truckArrayList.get(i).getRoute();
                           ArrayList<Customer> oldRoute_j = truckArrayList.get(j).getRoute();
                           Truck[] list = doLocalSearchMove(truckArrayList.get(i), truckArrayList.get(j));
                           truckArrayList.set(i, list[0]);
                           truckArrayList.set(j, list[1]);

                           if(!oldRoute_i.equals(truckArrayList.get(i).getRoute()) && !oldRoute_j.equals(truckArrayList.get(j).getRoute())){
                               improvement = true;
                           }
                       }
                   }
               }
           }
       }

        //get the total cost
        count = 0;
        for(Truck t: truckArrayList){
            if(t.getShipments().size() != 0) {
                TFC += FC;
                TVC += VC * getRouteLength(t);
                count++;
            }
        }

        System.out.println("Optimal Total cost : "+String.format("%.2f",TFC+TVC));
        System.out.println("Number of trucks used: " + count + "\n");

    }
    public static Truck[] doLocalSearchMove(Truck truck1, Truck truck2){
        //idea is to move shipment(s) from truck 2 to truck 1

        double newDistances;
        boolean foundImprovement = true;

        double curWeight = truck1.getCurrentWeight();
        double curVol = truck1.getCurrentVolume();

        ArrayList<Customer> route1 = truck1.getRoute();
        ArrayList<Customer> route2 = truck2.getRoute();
        double bestDistances = getDist(route1) + getDist(route2);
//        System.out.println("Initial feasible distance: " + String.format("%.2f", bestDistances));
//        System.out.println("Before LSmove: " + bestDistances);

        while(foundImprovement){
            foundImprovement = false;
            for(int i = 1; i<route1.size()-1; i++){
                for(int j = 1; j < route2.size()-1; j++){

                    Customer curr = route2.get(j);
                    //add the customer in the route for truck 1 at position i
                    route1.add(i, curr);
                    route2.remove(j);

                    boolean revert = true;
                    newDistances = getDist(route1) + getDist(route2);
                    //if the new route by moving customer j from route 2 to route 1 in position i is smaller
                    if(newDistances < bestDistances){
                        revert = false;
                        double w = 0;
                        double v = 0;
                        //sum up the weight and volume of all shipments to be delivered to that customer
                        for(Shipment s: truck2.getShipments()){
                            if(s.getCustomer().getID().equals(curr.getID())){
                                w += s.getWeight();
                                v += s.getVolume();
                            }
                        }
                        //if we add the weight and volume to truck 1 and no constraints are violated
                        if(curWeight + w < 22000 && curVol + v < 82) {
                            //add all shipments to truck 1 for the customer from truck 2
                            for (int p = 0; p < truck2.getShipments().size(); p++){
                                Shipment s = truck2.getShipments().get(p);
                                if (s.getCustomer().getID().equals(curr.getID())) {
                                    truck2.removeShipment(s);
                                    truck1.addShipment(s);
                                    p--;
                                }
                            }
                            //update the truck's new weight and volume
                            truck1.addWeight(w);
                            truck1.addVolume(v);
                            truck2.addWeight(-w);
                            truck2.addVolume(-v);

                            //set the new thresholds and update routes
                            foundImprovement = true;
                            bestDistances = newDistances;
                            j--;
                        }
                        else{
                            revert = true;
                        }
                    }
                    if(revert){
                        route1.remove(i);
                        route2.add(j, curr);
                    }
                }
            }
        }
        Truck[] trucklist = new Truck[2];
       // System.out.println("After LSmove: " + bestDistances);
        truck1.setRoute(route1);
        truck2.setRoute(route2);

        trucklist[0] = truck1;
        trucklist[1] = truck2;
        return trucklist;
    }
    public static boolean inRoute(Customer next, Truck t){
        for(Customer c:t.getRoute()){
            if(c.getID().equals(next.getID())){
                return true;
            }
        }
        return false;
    }
    public static void do2optExchange(Truck t){
        double bestDistance = getRouteLength(t);
        double newDistance;
        boolean foundImprovement = true;
        ArrayList<Customer> currentRoute = t.getRoute();
//        System.out.println("Unoptimized best distance: " + bestDistance);

        //until there is no more improvements
        while(foundImprovement){
            foundImprovement = false;
            //do not change the positions of the start and end nodes
            for(int i = 1; i < t.getRoute().size()-2; i++){
                for(int j = i+1; j < t.getRoute().size()-1; j++){
                //currently in O(n) but can probably be done in O(1)
                ArrayList<Customer> newRoute = Swap(currentRoute, i, j);
                newDistance = getDist(newRoute);
                //update the best distance and current route if we have found a 2-opt exhange which improves on the current route
                if(newDistance < bestDistance){
                        bestDistance = newDistance;
                        currentRoute = newRoute;
                        foundImprovement = true;
                    }
                }
            }
        }

        t.setRoute(currentRoute);
        //System.out.println("After 2opt: " + bestDistance);
    }

    public static double getDist(ArrayList<Customer> CL){

        double dist = 0;
        for(int i = 1; i < CL.size(); i++){
            dist += CL.get(i).distance(CL.get(i-1));
        }
        return dist;
    }
    public static ArrayList<Customer> Swap(ArrayList<Customer> route, int i, int j) {
        ArrayList<Customer> newRoute = new ArrayList<>();
        for (int k = 0; k < i; k++) {
            newRoute.add(route.get(k));
        }
        int num = 0;
        for(int k = i; k <= j; k++){
            newRoute.add(k,route.get(j-num));
            num++;
        }
        for(int k = j+1; k < route.size();k++){
            newRoute.add(route.get(k));
        }

        return newRoute;
    }
    public static double getRouteLength(Truck t){
        return getDist(t.getRoute());

    }
    public static Customer getMinDistCustomer(ArrayList<Shipment> CSL, Truck t, double[][] matrix, Customer[] CL){

        String locString = t.getLocation();
        int loc = -1;

        Customer[] currentCL = getCustomers(toArrayS(CSL));
        //get index of location of truck to use in distance matrix
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
    public static Shipment[] toArrayS(ArrayList<Shipment> ASL){
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
                if(customerList[i].getID().equals(ship.getCustomer().getID())) {
                    customerList[i].setLat(ship.getCustomer().getLat());
                    customerList[i].setLon(ship.getCustomer().getLon());
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
               //double Nb = Double.parseDouble(data[3]);
                double Volume = Double.parseDouble(data[4]);
                double ClusterLat = Double.parseDouble(data[5]);
                double ClusterLong = Double.parseDouble(data[6]);
                double OriginLat = Double.parseDouble(data[7]);
                double OriginLong = Double.parseDouble(data[8]);

                shipList[i] = new Shipment(Date, SLC, Weight, Volume, ClusterLat, ClusterLong, OriginLat, OriginLong);

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



