package com.ljpww72729.atblink;

import com.google.firebase.database.DatabaseReference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by LinkedME06 on 2017/9/1.
 */

public class BlinkFirebaseControlActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // Write a message to the database
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        databaseReference = database.getReference();
//        databaseReference.child("gpio").child(pinName).setValue(blink);
////        databaseReference.setValue(blink.isStatus());
//        // Read from the database
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                Blink value = dataSnapshot.child("gpio").child(pinName).getValue(Blink.class);
////                blink.setStatus((Boolean) dataSnapshot.getValue());
//                gpioServer.notifyBlinkDataChanged(pinName, value);
//                Log.i(TAG, "Value is: " + value.isStatus());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
//            }
//        });

    }

    public static void start(Context context) {
        Intent starter = new Intent(context, BlinkFirebaseControlActivity.class);
        context.startActivity(starter);
    }
}
