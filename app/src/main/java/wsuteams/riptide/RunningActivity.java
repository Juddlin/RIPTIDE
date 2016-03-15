package wsuteams.riptide;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.os.Handler;
import android.widget.TextView;

public class RunningActivity extends AppCompatActivity {
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        Button logOffButton = (Button) findViewById(R.id.logOffButton);
        logOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reset the application back to Main Activity one user logs off.
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
            }
        });

        Button beginButton = (Button) findViewById(R.id.beginButton);
        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handlerTask.run();
                sendSMS();
            }
        });

        SeekBar interval = (SeekBar) findViewById(R.id.timeIntervalSeekBar);
        interval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView label = (TextView) findViewById(R.id.timeIntervalLabel);
                label.setText("Time Interval: " + (progress * 5) + " Minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    Runnable handlerTask = new Runnable() {
        @Override
        public void run() {
            sendSMS();
            final SeekBar interval = (SeekBar) findViewById(R.id.timeIntervalSeekBar);
            handler.postDelayed(handlerTask, interval.getProgress() * 5 * 60000);
        }
    };

    private void sendSMS() {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage("6245", null, "goodman.27@hotmail.com (Subject) Test email from SMS", null, null);
    }

}
