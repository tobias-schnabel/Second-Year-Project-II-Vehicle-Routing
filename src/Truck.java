import javax.swing.text.html.CSS;
import java.util.ArrayList;
import java.util.Arrays;

//SECOND YEAR PROJECT II, OR Part
//Obbe Pulles i6250802 and Tobias Schnabel i6255807
//FileName: Truck.java
//Functionality: Implements the truck class

public class Truck {

    private double currentWeight = 0;
    private double currentVolume = 0;
    private final int truckId;
    private final ArrayList<Shipment> shipments = new ArrayList<>();
    private ArrayList<Customer> route = new ArrayList<>();

    public Truck(int num) {
        Customer start = new Customer(43.6206051062346, 1.39423144268438, "0", 0);
        this.route.add(start);
        this.truckId = num;
    }
    public void addWeight(double w) {
        this.currentWeight += w;
    }

    public void addVolume(double v) {
        this.currentVolume += v;
    }

    public double getCurrentWeight() {
        return this.currentWeight;
    }

    public double getCurrentVolume() {
        return this.currentVolume;
    }

    public void setRoute(ArrayList<Customer> route) {
        this.route = route;
    }
    public ArrayList<Customer> getRoute() {
        return this.route;
    }

    public ArrayList<Shipment> getShipments() {
        return this.shipments;
    }

    public void addShipment(Shipment s) {
        shipments.add(s);
    }
    public void removeShipment(Shipment s) {
        shipments.remove(s);
    }

    public void addToRoute(Customer c) {
        this.route.add(c);
    }
    public String getLocation() {
        int n = this.route.size();
        return this.route.get(n - 1).getID();
    }
    public int getTruckId() {
        return truckId;
    }
    public void printSolution() {
        System.out.print("Truck #" + (this.getTruckId() + 1) + ": ");
        System.out.print("Weight: " + String.format("%.1f",(this.getCurrentWeight()/1000)) + "/22 ");
        System.out.print("Volume: " + String.format("%.2f",this.getCurrentVolume()) + "/82 \n");

        ArrayList<Customer> route = this.getRoute();
        String[] customers = new String[route.size()];
        for (int  i = 0; i < customers.length; i++) {
            customers[i] = route.get(i).getID();
        }
        System.out.print("Route: Cluster >" );
        for (int i = 1; i < customers.length -1 ; i++) {
            System.out.print(customers[i].replace('"',' ') + ">");
        }
        System.out.println(" Cluster \n");
    }
}