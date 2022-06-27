import java.util.*;

public class Shipment {
    //Data has columns as follows:
    //Date | Weight | Nb | Volume | OriginCLusterLat | OriginCLusterLong | OriginLat |OriginLong

    private final Date PDate;
    private final double Weight;
    private final double Volume;
    private final double OriginClusterLat;
    private final double OriginClusterLong;
    private final Customer customer;
    public Shipment(Date PUDate, String SLC, double W, double V, double OClat, double OClong, double Olat, double Olong){
       this.PDate = PUDate;
       this.Weight = W;
       this.Volume = V;
       this.OriginClusterLat = OClat;
       this.OriginClusterLong = OClong;
       this.customer = new Customer(Olat, Olong, SLC);
    }

    public double distance(){
        double r = 6372.8;
        double dLat = Math.toRadians(this.OriginClusterLat - this.customer.getLat());
        double dLon = Math.toRadians(this.OriginClusterLong - this.customer.getLon());
        double lat1 = Math.toRadians(this.customer.getLat());
        double lat2 = Math.toRadians(this.OriginClusterLat);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2*Math.asin(Math.sqrt(a));

        return r*c;
    }

    public Date getPDate() {
        return this.PDate;
    }
    public double getWeight() { return this.Weight; }
    public double getVolume() { return this.Volume; }
//    public double getOriginLat() {return this.OriginLat;}
//    public double getOriginLong() {return this.OriginLong;}

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "PDate=" + PDate +
                ", Weight=" + Weight +
                ", Volume=" + Volume +
                ", OriginClusterLat=" + OriginClusterLat +
                ", OriginClusterLong=" + OriginClusterLong +
                '}';
    }
    //public String getSLC() {return SLC;}
    public double getOriginClusterLat() {
        return OriginClusterLat;
    }
    public double getOriginClusterLong() {
        return OriginClusterLong;
    }
    public static String[] getUnique(Shipment[] shiplist) {
        String[] rawSLC = new String[shiplist.length];
        for (int i = 0; i < shiplist.length; i++) {
            rawSLC[i] = shiplist[i].getCustomer().getID();
        }
    return Arrays.stream(rawSLC).distinct().toArray(String[]::new);
    }

} //close class