package com.tcd.paintsplat.group5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    String userName;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        ImageButton addUsrBtn = (ImageButton) findViewById(R.id.addUserButton);
        Button createRoomBtn = (Button) findViewById(R.id.createRoomBtn);
        Button joinRoomBtn = (Button) findViewById(R.id.joinBtn);
        TextView userNameTxtView = (TextView) findViewById(R.id.uerNameTextView);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userName = sharedPreferences.getString("my_user_name","");
        if (!userName.isEmpty()) {
            userNameTxtView.setText(userName);
            //addUsrBtn.setEnabled(false);
        } else {
            Toast.makeText(this, "UserName is empty",
                    Toast.LENGTH_LONG).show();
            userNameTxtView.setText("SET USER NAME");
            addUsrBtn.setEnabled(true);
        }

        addUsrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lauchAddUserActivity();
            }
        });

        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCreateRoomActivity();
            }
        });

        joinRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchJoinRoomActivity();
            }
        });
    }

    private void lauchAddUserActivity() {
        Intent intent = new Intent(this, AddUserActivity.class);
        startActivity(intent);
    }

    private void launchCreateRoomActivity() {
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please set the user name before creating room",
                    Toast.LENGTH_LONG).show();
        } else {
            String roomcode = String.valueOf(genRandomRoomCode());
            DatabaseReference roomDbRef = database.getReference("room/"+roomcode);
            String android_id_user = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Map<String,Object> userid = new HashMap<>();
            userid.put("host",android_id_user);
            userid.put("player1",android_id_user);
            roomDbRef.setValue(userid);

            DatabaseReference ref = database.getReference("canvas");
            ref = database.getReference("canvas/canvas_1");
            HashMap<String, Boolean> canvasSections = new HashMap<String, Boolean>() ;
            for(int i=1;i<101;i++){
                canvasSections.put(String.valueOf(i),false);
            }
            ref.setValue(canvasSections);

            Intent intent = new Intent(this, CreateRoomActivity.class);
            intent.putExtra("roomcode",roomcode);
            startActivity(intent);
        }
    }

    private void launchJoinRoomActivity() {
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please set the user name before creating room",
                    Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, JoinRoomActivity.class);
            startActivity(intent);
        }
    }

    public int genRandomRoomCode() {
        Random roomCode = new Random( System.currentTimeMillis() );
        return ((1 + roomCode.nextInt(2)) * 10000 + roomCode.nextInt(10000));
    }
}
