import java.util.ArrayList;

public class Truck {


    private final double maxWeight = 22000;
    private double currentWeight = 0;
    private final double maxVolume = 82;
    private double currentVolume = 0;
    private ArrayList<Shipment> shipments;

    public Truck(){
    }

    public String getLocation(){
        int n = this.shipments.size();
        return this.shipments.get(n-1).getSLC();
    }
}
