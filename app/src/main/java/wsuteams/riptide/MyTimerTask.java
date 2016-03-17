package wsuteams.riptide;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import java.util.TimerTask;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;


/**
 * Created by Juddlin on 3/14/2016.
 */
public class MyTimerTask extends TimerTask implements LocationListener{
    private String address;
    private int lat;
    private int lng;
    private LocationManager locationManager;
    private Context mContext;
    private Handler mHandler;

    /**
     * Constructor for MyTimerTask object.
     * @param address - Address for the server email
     */
    public MyTimerTask(String address, Context mContext){
        this.address = address;
        this.mContext = mContext;
    }

    /**
     * Method sends an SMS message to the service providers server with a pre-formatted text body,
     * then the service providers server will send the text to the email address specified.
     * Looper is used to allow compiling, not 100% sure on what its for but it works.
     */
    @Override
    public void run() {
        try {
           // Looper.prepare();
            SmsManager sms = SmsManager.getDefault();
           // locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            sms.sendTextMessage("6245", null, this.address + " Latitude: " + this.lat + " Longitude: " + this.lng, null, null);
            //System.out.println(" Latitude: " + this.lat + " Longitude: " + this.lng);
           // Looper.loop();
        } catch (SecurityException e){

        }
    }

    /**
     * Method returns the email address for the server.
     * @return - The email address of the server
     */
    public String getAddress(){
        return this.address;
    }

    /**
     * Method sets the email address for the server.
     * @param address - The email address of the server
     */
    public void setAddress(String address){
        this.address = address;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lat = (int) (location.getLatitude() * 1E6);
        this.lng = (int) (location.getLongitude() * 1E6);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}


