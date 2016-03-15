package wsuteams.riptide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        Button finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Objects fot the text fields
                EditText firstName = (EditText) findViewById(R.id.firstNameTextField);
                EditText lastName = (EditText) findViewById(R.id.lastNameTextField);
                EditText age = (EditText) findViewById(R.id.ageTextField);
                EditText icsRole = (EditText) findViewById(R.id.icsTextField);
                EditText username = (EditText) findViewById(R.id.usernameTextField);
                EditText password = (EditText) findViewById(R.id.passwordTextField);

                // Make a key for all 6 fields of information for the apps Shared Preferences
                editor.putString("key_firstName", firstName.getText().toString());
                editor.putString("key_lastName", lastName.getText().toString());
                editor.putString("key_age", age.getText().toString());
                editor.putString("key_icsRole", icsRole.getText().toString());
                editor.putString("key_username", username.getText().toString());
                editor.putString("key_password", password.getText().toString());

                // Commit changes to the apps Shared Preferences
                editor.commit();
                finish();
            }
        });
    }

}
