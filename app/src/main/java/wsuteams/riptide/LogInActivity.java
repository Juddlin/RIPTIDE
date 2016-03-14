package wsuteams.riptide;

import android.content.Intent;
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
        userNameTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameTextField.getText().clear();
            }
        });

        // for some reason its not deleting text, takes two clicks?
        final EditText passwordTextField = (EditText) findViewById(R.id.password2TextField);
        passwordTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordTextField.getText().clear();
            }
        });

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userNameTextField.getText().length() == 0 || passwordTextField.getText().length() == 0) {
                    running();
                } else {
                    // -check if valid login information, than proceed, else error message.
                    startActivity(new Intent(getApplicationContext(), RunningActivity.class));
                }
            }
        });
    }
}
