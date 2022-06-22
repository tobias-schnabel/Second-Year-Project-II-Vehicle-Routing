import java.util.ArrayList;

public class Truck {


    private final double maxWeight = 22000;
    private double currentWeight = 0;
    private final double maxVolume = 82;
    private double currentVolume = 0;
    private final int truckId;
    private ArrayList<Shipment> shipments = new ArrayList<Shipment>();
    private ArrayList<Customer> route = new ArrayList<>();

    public Truck(int num){
        Customer start = new Customer(43.6206051062346, 1.39423144268438, "0",0);
        this.route.add(start);
        this.truckId = num;
    }
    public void addWeight(double w){
        this.currentWeight += w;
    }
    public void addVolume(double v){
        this.currentVolume += v;
    }
    public double getCurrentWeight() {
        return this.currentWeight;
    }
    public double getCurrentVolume(){
        return this.currentVolume;
    }
    public ArrayList<Customer> getRoute(){
        return this.route;
    }
    public void addShipment(Shipment s){
        shipments.add(s);
    }
    public void addToRoute(Customer c){
        this.route.add(c);
    }
    public int getTruckId(){
        return this.truckId;
    }

    public String getLocation(){
        int n = this.route.size();
        return this.route.get(n-1).getID();
    }
}
