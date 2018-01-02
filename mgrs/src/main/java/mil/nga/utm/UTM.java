package mil.nga.utm;

import java.text.ParseException;

import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.wgs84.LatLng;

/**
 * Created by wnewman on 12/21/17.
 */

public class UTM {

    public enum Hemisphere {
        NORTH,
        SOUTH;
    }

    private int zoneNumber;
    private Hemisphere hemisphere;
    private double easting;
    private double northing;

    public UTM(int zoneNumber, Hemisphere hemisphere, double easting, double northing) {
        this.zoneNumber = zoneNumber;
        this.hemisphere = hemisphere;
        this.easting = easting;
        this.northing = northing;
    }

    public int getZoneNumber() {
        return zoneNumber;
    }

    public Hemisphere getHemisphere() {
        return hemisphere;
    }

    public double getEasting() {
        return easting;
    }

    public double getNorthing() {
        return northing;
    }

    public static UTM parse(String mgrs) throws ParseException {
        return MGRS.parse(mgrs).utm();
    }

    public static UTM from(LatLng latLng) {
        int zone = (int) Math.floor(latLng.longitude / 6 + 31);
        return from(latLng, zone);
    }

    public static UTM from(LatLng latLng, int zone) {
        Hemisphere hemisphere = latLng.latitude >= 0 ? Hemisphere.NORTH : Hemisphere.SOUTH;
        return from(latLng, zone, hemisphere);
    }

    public static UTM from(LatLng latLng, int zone, Hemisphere hemisphere) {
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        double easting = 0.5 * Math.log((1+Math.cos(latitude*Math.PI/180)*Math.sin(longitude*Math.PI/180-(6*zone-183)*Math.PI/180))/(1-Math.cos(latitude*Math.PI/180)*Math.sin(longitude*Math.PI/180-(6*zone-183)*Math.PI/180)))*0.9996*6399593.62/Math.pow((1+Math.pow(0.0820944379, 2)*Math.pow(Math.cos(latitude*Math.PI/180), 2)), 0.5)*(1+ Math.pow(0.0820944379,2)/2*Math.pow((0.5*Math.log((1+Math.cos(latitude*Math.PI/180)*Math.sin(longitude*Math.PI/180-(6*zone-183)*Math.PI/180))/(1-Math.cos(latitude*Math.PI/180)*Math.sin(longitude*Math.PI/180-(6*zone-183)*Math.PI/180)))),2)*Math.pow(Math.cos(latitude*Math.PI/180),2)/3)+500000;
        easting = Math.round(easting * 100) * 0.01;
        double northing = (Math.atan(Math.tan(latitude*Math.PI/180)/Math.cos((longitude*Math.PI/180-(6*zone -183)*Math.PI/180)))-latitude*Math.PI/180)*0.9996*6399593.625/Math.sqrt(1+0.006739496742*Math.pow(Math.cos(latitude*Math.PI/180),2))*(1+0.006739496742/2*Math.pow(0.5*Math.log((1+Math.cos(latitude*Math.PI/180)*Math.sin((longitude*Math.PI/180-(6*zone -183)*Math.PI/180)))/(1-Math.cos(latitude*Math.PI/180)*Math.sin((longitude*Math.PI/180-(6*zone -183)*Math.PI/180)))),2)*Math.pow(Math.cos(latitude*Math.PI/180),2))+0.9996*6399593.625*(latitude*Math.PI/180-0.005054622556*(latitude*Math.PI/180+Math.sin(2*latitude*Math.PI/180)/2)+4.258201531e-05*(3*(latitude*Math.PI/180+Math.sin(2*latitude*Math.PI/180)/2)+Math.sin(2*latitude*Math.PI/180)*Math.pow(Math.cos(latitude*Math.PI/180),2))/4-1.674057895e-07*(5*(3*(latitude*Math.PI/180+Math.sin(2*latitude*Math.PI/180)/2)+Math.sin(2*latitude*Math.PI/180)*Math.pow(Math.cos(latitude*Math.PI/180),2))/4+Math.sin(2*latitude*Math.PI/180)*Math.pow(Math.cos(latitude*Math.PI/180),2)*Math.pow(Math.cos(latitude*Math.PI/180),2))/3);

        if (hemisphere == Hemisphere.SOUTH) {
            northing = northing + 10000000;
        }

        northing = Math.round(northing * 100) * 0.01;

        return new UTM(zone, hemisphere, easting, northing);
    }

}
