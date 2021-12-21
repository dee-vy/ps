package com.tcd.paintsplat.group5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class CreateRoomActivity extends AppCompatActivity {
    EditText userNameEditText;
    ArrayList<String> players = new ArrayList<String>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userName = sharedPreferences.getString("my_user_name","");

        players.add(userName);

        setContentView(R.layout.create_room_layout);
        Bundle Extras  = getIntent().getExtras();
        String roomCode = "";
        if(Extras!=null){
            roomCode = Extras.getString("roomcode");
        }
        TextView roomCodeTextView = (TextView) findViewById(R.id.room_code);
        roomCodeTextView.setText(roomCode);

        Button startGame = (Button) findViewById(R.id.start_game);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCanavas();
            }
        });

        DatabaseReference referenceCanvas = database.getReference("room/"+roomCode);
        referenceCanvas.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String newPlayer = snapshot.getKey();
                System.out.println(newPlayer);
                if(!newPlayer.equals( "host")) {
                    LinearLayout playerLayout = (LinearLayout) findViewById(R.id.player_layout);
                    TextView player = new TextView(getApplicationContext());
                    player.setTextColor(Color.RED);
                    player.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    player.setText(newPlayer);
                    playerLayout.addView(player);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    private void showCanavas()
    {
        Intent intent = new Intent(this, CanvasActivity.class);
        startActivity(intent);
    }




}
