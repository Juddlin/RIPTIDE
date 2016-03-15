package wsuteams.riptide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        running();
    }

    protected void running() {
        final EditText userNameTextField = (EditText) findViewById(R.id.username2TextField);
        final EditText passwordTextField = (EditText) findViewById(R.id.password2TextField);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userNameTextField.getText().length() == 0 || passwordTextField.getText().length() == 0) {
                    userNameTextField.getText().clear();
                    passwordTextField.getText().clear();
                } else {
                    String username = pref.getString("key_username", null);
                    String password = pref.getString("key_password", null);
                    if (userNameTextField.getText().toString().equals(username) && passwordTextField.getText().toString().equals(password)) {
                        startActivity(new Intent(getApplicationContext(), RunningActivity.class));
                    } else {
                        userNameTextField.getText().clear();
                        passwordTextField.getText().clear();
                    }
                }
            }
        });

        // Button to go back to initial screen
        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
