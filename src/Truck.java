import java.util.ArrayList;

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

    public void addToRoute(Customer c) {
        this.route.add(c);
    }

    public Customer getEnd() {
        return this.route.get(this.route.size() - 1);
    }

    public String getLocation() {
        int n = this.route.size();
        return this.route.get(n - 1).getID();
    }
}
