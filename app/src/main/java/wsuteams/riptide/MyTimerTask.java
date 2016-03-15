package wsuteams.riptide;

import android.telephony.SmsManager;

import java.util.TimerTask;

/**
 * Created by Juddlin on 3/14/2016.
 */
public class MyTimerTask extends TimerTask {

    private String address;

    public MyTimerTask(String address){
        this.address = address;
    }

    @Override
    public void run() {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("6245", null, this.address + " Test email from SMS", null, null);
    }

    public String getAddress(){
        return this.address;
    }

    public void setAddress(String address){
        this.address = address;
    }
}
