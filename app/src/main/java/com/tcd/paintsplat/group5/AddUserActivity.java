package com.tcd.paintsplat.group5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class AddUserActivity extends AppCompatActivity {
    EditText userNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.add_user_page);

        userNameEditText = (EditText) findViewById(R.id.userName);
        Button addUserName = (Button) findViewById(R.id.addUserNamebutton);

        addUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference  ref = database.getReference("users");
                //ref.updateChildren("user_"+uniqueID);
                String android_id_user = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                ref = database.getReference("users/"+android_id_user);
                ref.setValue(userNameEditText.getText().toString());
                backToMainPage();
            }
        });
    }

    private void backToMainPage() {
        String userName = userNameEditText.getText().toString();

        if (userName.isEmpty()) {
            Toast.makeText(this, "Enter a valid UserName",
                    Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("my_user_name", userName);
            myEdit.commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

}
