package wsuteams.riptide;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Calendar;

public class RunningActivity extends AppCompatActivity {
    private Location gpsLocation = null;
    private Handler handler;
    private LocationManager locationManager;
    private static final int GPS_TIME_INTERVAL = 60000; // Get GPS location every 1 min
    private static final int GPS_DISTANCE = 5; // Set the distance value in meter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        // Fill the server field on create
        final EditText serverTextField = (EditText) findViewById(R.id.serverTextField);
        serverTextField.setText(pref.getString("key_server", ""));

        // Button Listener to go back to the main screen intent to restart the entire process.
        Button logOffButton = (Button) findViewById(R.id.logOffButton);
        logOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the server address for later usage
                editor.putString("key_server", serverTextField.getText().toString());
                editor.commit();

                // Reset the application back to Main Activity one user logs off.
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
            }
        });

        // Listener for the Seek Bar to change the label about it whenever the value changes.
        final SeekBar interval = (SeekBar) findViewById(R.id.timeIntervalSeekBar);
        interval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView label = (TextView) findViewById(R.id.timeIntervalLabel);
                if (interval.getProgress() == 0) {
                    label.setText("Time Interval: " + (progress + 1) + " Minutes");
                } else {
                    label.setText("Time Interval: " + (progress * 5) + " Minutes");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Button listener for the begin button, when pressed it starts a handler loop that sends the
        // SMS message at certain intervals, also changes the text on the button to "Stop".
        // When the button is in the "Stop" form, when pressed again it simply restarts the current
        // intent completely in order to avoid crashes and exceptions.
        final Button beginButton = (Button) findViewById(R.id.beginButton);
        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beginButton.getText().toString().equals("Begin")) {
                    beginButton.setText("Stop");
                    final EditText email = (EditText) findViewById(R.id.serverTextField);
                    final int HANDLER_DELAY;
                    if (interval.getProgress() == 0) {
                        HANDLER_DELAY = (1000 * 60 * (interval.getProgress() + 1));
                    } else {
                        HANDLER_DELAY = (1000 * 60 * (interval.getProgress() * 5));
                    }

                    // Loop thread for getting the GPS
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            obtainLocation();
                            SmsManager sms = SmsManager.getDefault();
                            String myTime = java.text.DateFormat.getTimeInstance().format(Calendar.getInstance().getTime());
                            sms.sendTextMessage("6245", null, email.getText().toString() + " Latitude: " + gpsLocation.getLatitude() + " Longitude: " + gpsLocation.getLongitude() + " Timestamp: " + myTime, null, null);
                            handler.postDelayed(this, HANDLER_DELAY);
                        }
                    }, HANDLER_DELAY);
                } else {
                    beginButton.setText("Begin");

                    // End the handler loop
                    handler.removeCallbacksAndMessages(null);

                    // Restart the running screen, fixes crash.
                    Intent intent = new Intent(getApplicationContext(), RunningActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                }
            }
        });
    }

    /**
     * Method gets the GPS location and saves it to a Member Variable.
     */
    private void obtainLocation() {
        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_TIME_INTERVAL, GPS_DISTANCE, GPSListener);
            } catch (SecurityException e) {

            }
        }
    }

    /**
     * Implementation for LocationListener that removes the most recent update
     * to the GPS into the locationManager Member Variable.
     */
    private LocationListener GPSListener = new LocationListener(){
        public void onLocationChanged(Location location) {
            try{
                locationManager.removeUpdates(GPSListener); // remove this listener
            } catch (SecurityException e) {

            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
}
