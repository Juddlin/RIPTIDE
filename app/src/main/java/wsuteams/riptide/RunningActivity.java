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
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import java.util.List;
import java.util.UUID;

public class RunningActivity extends AppCompatActivity {
    // GPS related variables
    private Location gpsLocation = null;
    private Handler handler;
    private LocationManager locationManager;
    private static final int GPS_TIME_INTERVAL = 60000; // Get GPS location every 1 min
    private static final int GPS_DISTANCE = 5; // Set the distance value in meter

    // Bluetooth related variables
    private static final UUID HEART_RATE_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    private static final UUID HEART_RATE_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    private static final String deviceAddress = "00:22:D0:9E:9E:57";
    private static final long SCAN_PERIOD = 10000;
    private static BluetoothGattDescriptor descriptor;
    private static BluetoothGattCharacteristic cc;
    private Button scanButton;
    private TextView heartRateStatusView;
    private Handler mHandler = new Handler();
    private boolean scanning;
    private boolean connected = false;
    private BluetoothDevice finalDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic heartRateCharacteristic;
    private int theHeartRate = -1;

    // Generic variables
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TextView serverTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        // Initialize text view for showing connected device
        heartRateStatusView = (TextView) findViewById(R.id.heartRateMonitorStatusTextView);

        // Fill the server field on create
        serverTextField = (EditText) findViewById(R.id.serverTextField);
        serverTextField.setText(pref.getString("key_server", ""));

        // Button Listener to go back to the main screen intent to restart the entire process.
        Button logOffButton = (Button) findViewById(R.id.logOffButton);
        logOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the server address for later usage
                editor.putString("key_server", serverTextField.getText().toString());
                editor.commit();

                // Disconnect the device
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.disconnect();
                    mBluetoothGatt = null;
                    connected = false;
                    finalDevice = null;
                }

                // Reset the application back to Main Activity one user logs off.
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
            }
        });

        // Button for starting the scan for BLE devices
        scanButton = (Button) findViewById(R.id.heartRateScanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan(true);
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
                            double latitude = gpsLocation.getLatitude();
                            double longitude = gpsLocation.getLongitude();

                            sms.sendTextMessage("6245", null, email.getText().toString() + " " + formatHeart(theHeartRate) + formatLatLong(latitude) + formatLatLong(longitude), null, null);
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
     * Method triggers once the current app intent closes. It disconnects the Gatt profile and the device.
     */
    @Override
    protected void onStop(){
        super.onStop();
//        if(mBluetoothGatt != null){
//            mBluetoothGatt.disconnect();
//            mBluetoothGatt = null;
//            connected = false;
//            finalDevice = null;
//        }

        // Save the server address for later usage
        editor.putString("key_server", serverTextField.getText().toString());
        editor.commit();
    }

    /**
     * Method scans for Bluetooth Low Energy Devices.
     */
    private void scan(final boolean enable){
        final BluetoothAdapter adapter = getmBluetoothAdapter();
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    adapter.stopLeScan(callback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            adapter.startLeScan(callback);
        } else {
            scanning = false;
            adapter.stopLeScan(callback);
        }
    }

    /**
     * Variable callback is used for the Override method "onLeScan" which will get the result of the scan
     * and connect the device if it is the correct one that RIPTIDE is looking for.
     */
    private final BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(device.getAddress().equals("00:22:D0:9E:9E:57")) {
                        // list.add(device);
                        finalDevice = device;
                        connectDevice();
                    }
                }
            });
        }
    };

    /**
     * Helper method to connect the Bluetooth device to the RIPTIDE app.
     */
    private void connectDevice(){
        if(connected == false) {
            connected = true;
            mBluetoothGatt = finalDevice.connectGatt(this, false, mGattCallback);
            mBluetoothGatt.connect();
            heartRateStatusView.setText("Device: " + finalDevice.getName());
        }
    }

    /**
     * mGattCallBack variable is used for the Override methds onConnectionStateChange, onServicesDiscovered, and onCharacteristicChanged.
     * onConnectionStateChange - is called when the device detects a state change triggered from the app. The initial call will trigger
     *                           the discovery of the services available on the device.
     * onServicesDiscovered - is called from the device directly after the services have been discovered from onConnectionStateChange.
     *                        This method will write a characteristic to the device telling it to start transmitting the heart rate.
     * onCharacteristicChanged - is called whenever the device transmits the heart rate. It will update a global member variable with
     *                          the current heart rate.
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){
            String intentAction;
            if(newState == BluetoothProfile.STATE_CONNECTED){
                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){
            BluetoothGattService service = gatt.getService(HEART_RATE_SERVICE_UUID);
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            for (BluetoothGattCharacteristic cc : characteristics){
                for (BluetoothGattDescriptor descriptor : cc.getDescriptors()){
                    RunningActivity.descriptor = descriptor;
                    RunningActivity.cc = cc;
                    gatt.setCharacteristicNotification(cc, true);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                }
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic){
            byte[] data = characteristic.getValue();
            int bmp = data[1] & 0xFF; // Unsign the return value
            theHeartRate = bmp;
        }
    };

    /**
     * Method returns the Bluetooth adapter from the BluetoothManager object
     */
    private BluetoothAdapter getmBluetoothAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager.getAdapter();
    }

    /**
     * Method returns a formatted double in the form of a String truncated to 10 digits, 0 padded if necessary.
     */
    private String formatLatLong(double value){
        String formatted = "";

        if(value >= 100 || value <= -100){
            formatted = String.format("%03.7f", value);
        }else if((value < 100 && value >= 10) || ( value > -100 && value <= -10)){
            formatted = String.format("%02.8f", value);
        }else{
            formatted = String.format("%01.9f", value);
        }

        return formatted;
    }

    /**
     * Method returns a formatted int in the form of a String truncated to 3 digits, 0 padded if necessary.
     */
    private String formatHeart(int value){
        String formatted = "";

        if(value >= 1000){
            value = 999;
            formatted = String.format("%03d", value);
        }else if(value < 0){
            value = 0;
            formatted = String.format("%03d", value);
        }else{
            formatted = String.format("%03d", value);
        }

        return formatted;
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
