import java.util.*;
    public class City{

        private final String cityName;
        private final int inhabitants;
        private final double latitude;
        private final double longitude;


        public City(String cityName, int inhabitants, double latitude, double longitude){

            this.cityName = cityName;
            this.inhabitants = inhabitants;
            this.latitude = latitude;
            this.longitude = longitude;

        }

        public void printName(){
            System.out.println(this.cityName);
        }

        public int getInhabitants(){
            return this.inhabitants;
        }

        public double getLongitude(){
            return this.longitude;
        }

        public double getLatitude(){
            return this.latitude;
        }

        // returns the distance between two cities
        public double distance(City city){
            double r = 6372.8;
            double dLat = Math.toRadians(city.latitude - this.latitude);
            double dLon = Math.toRadians(city.longitude - this.longitude);
            double lat1 = Math.toRadians(this.latitude);
            double lat2 = Math.toRadians(city.latitude);
            double a = Math.pow(Math.sin(dLat / 2), 2)
                    + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
            double c = 2*Math.asin(Math.sqrt(a));

            return r*c;

        }
}
