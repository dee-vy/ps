package com.tcd.paintsplat.group5;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JoinRoomActivity extends AppCompatActivity {
    EditText joinCodeEditTxt;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_room_layout);

        Button joinBtn = (Button) findViewById(R.id.join_button);
        joinCodeEditTxt = (EditText) findViewById(R.id.join_code);

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinValidation();
            }
        });
    }

    private void joinValidation() {
        Boolean success = false;
        String code = joinCodeEditTxt.getText().toString();
        if (code.isEmpty()){
            Toast.makeText(this, "Join code is empty",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Join game",
                    Toast.LENGTH_LONG).show();
            DatabaseReference ref = database.getReference("room/"+code);

            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.getResult().getValue() != null){
                        //Log.d("Room id Exists",);
                       Map<String,String> value = (HashMap)task.getResult().getValue();
                       int numberOfPlayers = value.size();
                        DatabaseReference roomDbRef = database.getReference("room/"+code);
                        String android_id_user = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                        Set<String> playersJoined = value.keySet();
                        for(String player:playersJoined){
                            if(!player.equals("host")){
                                String playerId = value.get(player);
                                if(!playerId.equals(android_id_user)){
                                    value.put("player"+String.valueOf(numberOfPlayers),android_id_user);
                                    roomDbRef.setValue(value);
                                    Intent intent = new Intent(getApplicationContext(), CreateRoomActivity.class);
                                    intent.putExtra("roomcode",code);
                                    startActivity(intent);
                                }
                            }
                        }

                    }else{
                        System.out.println("Room id doesnt exist");
                    }

                }
            });



        }
    }
}
