package wsuteams.riptide;

import android.telephony.SmsManager;
import java.util.TimerTask;

/**
 * Created by Juddlin on 3/14/2016.
 */
public class MyTimerTask extends TimerTask {
    private String address;

    /**
     * Constructor for MyTimerTask object.
     * @param address - Address for the server email
     */
    public MyTimerTask(String address){
        this.address = address;
    }

    /**
     * Method sends an SMS message to the service providers server with a pre-formatted text body,
     * then the service providers server will send the text to the email address specified.
     */
    @Override
    public void run() {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("6245", null, this.address + " Latitude: ..., Longitude: ..." , null, null);
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
}
