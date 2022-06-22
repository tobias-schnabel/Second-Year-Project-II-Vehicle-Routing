import java.util.ArrayList;

public class Truck {


    private final double maxWeight = 22000;
    private double currentWeight = 0;
    private final double maxVolume = 82;
    private double currentVolume = 0;
    private final int truckId;
    private ArrayList<Shipment> shipments;
    private ArrayList<String> route;

    public Truck(int num){
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
    public ArrayList<String> getRoute(){
        return this.route;
    }
    public void addShipment(Shipment s){
        shipments.add(s);
    }
    public void addToRoute(String ID){
        this.route.add(ID);
    }
    public int getTruckId(){
        return this.truckId;
    }

    public String getLocation(){
        int n = this.shipments.size();
        return this.shipments.get(n-1).getSLC();
    }
}
