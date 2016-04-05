package wsuteams.riptide;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if GPS is enabled
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            showGPSDisabledAlertToUser();
        }

        // Check if Bluetooth is enabled
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled()){
            showBlueToothDisabledAlertToUser();
        }

        // Button listener for going to the setup intent
        Button setupButton = (Button) findViewById(R.id.setupButton);
        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SetupActivity.class));
            }
        });

        // Button listener for goign to the log in intent
        Button logInButton = (Button) findViewById(R.id.logInButton);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LogInActivity.class));
            }
        });
    }

    /**
     * Method will force the user to turn on Bluetooth Services.
     */
    private void showBlueToothDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Bluetooth is disabled on your device, it must be enabled for RIPTIDE to function.");
        alertDialogBuilder.setCancelable(false);

        // Open the Settings intent to turn on the Bluetooth
        alertDialogBuilder.setPositiveButton("Go to settings page to enable Bluetooth.", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callBluetoothSettingIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(callBluetoothSettingIntent);
            }
        });

        // Create and show the alert
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**
     * Method will force the user to turn on Location GPS Services.
     */
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled on your device, it must be enabled for RIPTIDE to function.");
        alertDialogBuilder.setCancelable(false);

        // Open the Settings intent to turn on the GPS
        alertDialogBuilder.setPositiveButton("Go to settings page to enable the GPS.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGpsSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGpsSettingIntent);
            }
        });

        // Create and show the alert
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
