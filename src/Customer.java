public class Customer {

    private  double lat;
    private  double lon;

    private final String ID;

    private int numShip = 0;

    public Customer(double lat, double lon, String ID, int num){
        this.lat = lat;
        this.lon = lon;
        this.ID = ID;
        this.numShip = num;
    }
    public Customer(double lat, double lon, String ID){
        this.lat = lat;
        this.lon = lon;
        this.ID = ID;
    }
    public Customer(String ID) {
        this.lat = 0;
        this.lon = 0;
        this.ID = ID;
    }

    public double distance(Customer that){
        double r = 6372.8;
        double dLat = Math.toRadians(this.lat - that.lat);
        double dLon = Math.toRadians(this.lon - that.lon);
        double lat1 = Math.toRadians(this.lat);
        double lat2 = Math.toRadians(that.lat);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2*Math.asin(Math.sqrt(a));

        return r*c;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getID() {
        return ID;
    }

    public void incrementNum() {
        this.numShip += 1;
    }

    public void setNumShip(int numShip) {
        this.numShip = numShip;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "lat = " + lat +
                ", lon = " + lon +
                ", ID = '" + ID + '\'' +
                ", numShip = " + numShip +
                '}';
    }

    public int getNumShip() {
        return numShip;
    }
} //close class
