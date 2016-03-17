package wsuteams.riptide;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Timer;

public class RunningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        // Button Listener to go back to the main screen intent to restart the entire process.
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

        // Button listener for the begin button, when pressed it starts a timer that sends the
        // SMS message at certain intervals, also changes the text on the button to "Stop".
        // When the button is in the "Stop" form, when pressed again it simply restarts the current
        // intent completly in order to avoid crashes and exceptions.
        // Created a Context object in order to pass into the MyTimerTask object for usage.
        final Button beginButton = (Button) findViewById(R.id.beginButton);
        final Timer myTimer = new Timer();
        final Context mContext = this;
        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (beginButton.getText().toString().equals("Begin")) {
                    beginButton.setText("Stop");
                    EditText email = (EditText) findViewById(R.id.serverTextField);
                    MyTimerTask myTimerTask = new MyTimerTask(email.getText().toString(), mContext);
                    if (interval.getProgress() == 0) {
                        myTimer.scheduleAtFixedRate(myTimerTask, 0, (1000 * 60 * (interval.getProgress() + 1)));
                    } else {
                        myTimer.scheduleAtFixedRate(myTimerTask, 0, (1000 * 60 * (interval.getProgress() * 5)));
                    }
                } else {
                    beginButton.setText("Begin");
                    myTimer.cancel();

                    // Restart the running screen, fixes crash.
                    Intent intent = new Intent(getApplicationContext(), RunningActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                }
            }
        });
    }

}
