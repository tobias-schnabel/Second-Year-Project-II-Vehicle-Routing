import java.time.*;
import java.util.*;

public class Shipment {
    //Data has columns as follows:
    //Date | Weight | Nb | Volume | OriginCLusterLat | OriginCLusterLong | OriginLat |OriginLong
    private final Date PDate;
    private final double Weight;

    private final double OriginClusterLat;
    private final double OriginClusterLong;
    private final double OriginLat;
    private final double OriginLong;

    public Shipment(LocalDate PUDate, double OClat, double OClong, double Olat, double Olong){
       this.PDate = PUDate;
       this.OriginClusterLat = OClat;
       this.OriginClusterLong = OClong;
       this.OriginLat = Olat;
       this.OriginLong = Olong;
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

    public LocalDate getPDate() {
        return PDate;
    }
    public double getWeight() { return Weight; }
    public double getNb() { return Nb; }
    public double getVolume() { return Volume; }

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
}
