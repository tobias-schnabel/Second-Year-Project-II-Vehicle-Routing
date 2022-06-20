import java.time.*;
import java.util.*;

public class Shipment {

    private final LocalDate PDate;
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
}
