import java.util.ArrayList;
import java.util.LinkedList;

public class Truck {

    private double currentWeight = 0;
    private double currentVolume = 0;
    private final int truckId;
    private final ArrayList<Shipment> shipments = new ArrayList<>();
    private final LinkedList<Customer> route = new LinkedList<>();

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
    public LinkedList<Customer> getRoute(){
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
    public Customer getEnd(){return this.route.get(this.route.size()-1);}

    public String getLocation(){
        int n = this.route.size();
        return this.route.get(n-1).getID();
    }

    public boolean isEqual(Truck that){
        return(this.truckId==that.truckId);
    }
    public boolean isFull(){
        return (this.currentVolume < 82 && this.currentWeight < 22000);
    }
}
