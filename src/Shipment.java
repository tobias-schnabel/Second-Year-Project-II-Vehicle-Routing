import java.time.*;
import java.util.*;

public class Shipment {
    //Data has columns as follows:
    //Date | Weight | Nb | Volume | OriginCLusterLat | OriginCLusterLong | OriginLat |OriginLong
    private final Date PDate;
    private final double Weight;
    private final double Nb;
    private final double Volume;
    private final double OriginClusterLat;
    private final double OriginClusterLong;
    private final double OriginLat;
    private final double OriginLong;

    private final String SLC;

    public Shipment(Date PUDate, String SLC, double W, double Nb, double V, double OClat, double OClong, double Olat, double Olong){
       this.PDate = PUDate;
       this.Weight = W;
       this.Nb = Nb;
       this.Volume = V;
       this.OriginClusterLat = OClat;
       this.OriginClusterLong = OClong;
       this.OriginLat = Olat;
       this.OriginLong = Olong;
       this.SLC = SLC;
    }

    public double distance(){
        double r = 6372.8;
        double dLat = Math.toRadians(this.OriginClusterLat - this.OriginLat);
        double dLon = Math.toRadians(this.OriginClusterLong - this.OriginLong);
        double lat1 = Math.toRadians(this.OriginLat);
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
    public double getNb() { return this.Nb; }
    public double getVolume() { return this.Volume; }
    public double getOriginLat() {return this.OriginLat;}
    public double getOriginLong() {return this.OriginLong;}

    @Override
    public String toString() {
        return "Shipment{" +
                "PDate=" + PDate +
                ", Weight=" + Weight +
                ", Nb=" + Nb +
                ", Volume=" + Volume +
                ", OriginClusterLat=" + OriginClusterLat +
                ", OriginClusterLong=" + OriginClusterLong +
                ", OriginLat=" + OriginLat +
                ", OriginLong=" + OriginLong +
                '}';
    }

//    public static Customer[] toCustomerList(Shipment[] shiplist){
//        int n = shiplist.length;
//        Customer[] list = new Customer[n];
//        for(int i = 0; i < n; i++){
//            Customer ref = new Customer();
//            ref.setLat(shiplist[i].getOriginLat());
//            ref.setLon(shiplist[i].getOriginLong());
//            list[i] = ref;
//        }
//        return list;
//
//    }

    public String getSLC() {
        return SLC;
    }

    public double getOriginClusterLat() {
        return OriginClusterLat;
    }

    public double getOriginClusterLong() {
        return OriginClusterLong;
    }

    public static String[] getUnique(Shipment[] shiplist) {
        String[] rawSLC = new String[shiplist.length];
        for (int i = 0; i < shiplist.length; i++) {
            rawSLC[i] = shiplist[i].getSLC();
        }
        String[] uniqueSLC = Arrays.stream(rawSLC).distinct().toArray(String[]::new);
    return uniqueSLC;
    }
} //close class
