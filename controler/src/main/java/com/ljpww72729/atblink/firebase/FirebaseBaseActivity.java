package com.ljpww72729.atblink.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ljpww72729.atblink.R;
import com.ljpww72729.atblink.data.Constants;
import com.ljpww72729.atblink.utils.SPUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by LinkedME06 on 2017/9/7.
 */

public class FirebaseBaseActivity extends AppCompatActivity {

    public boolean isFirebaseAddress = true;
    public DatabaseReference databaseFireRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirebaseAddress = SPUtils.getInt(this, Constants.SP_FILE, Constants.DATABASE_ADDRESS, Constants.DATABASE_FIREBASE) == Constants.DATABASE_FIREBASE;
//        isFirebaseAddress = false;
        // TODO: 2017/9/7 lipeng 这里可不可以精简呢？？？
        if (isFirebaseAddress) {
            databaseFireRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        showFirebaseConnectedStatus(true);
                    } else {
                        showFirebaseConnectedStatus(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.err.println("Listener was cancelled");
                }
            });
        }

    }

    /**
     * 本地离线则不可更改数据
     *
     * @param connected 状态
     */
    private void showFirebaseConnectedStatus(boolean connected) {
        ViewGroup content = getWindow().findViewById(android.R.id.content);
        if (connected) {
            ViewGroup relativeLayout = content.findViewWithTag("firebase_connect");
            if (relativeLayout != null) {
                content.getChildAt(0).setVisibility(View.VISIBLE);
                content.removeViewAt(content.getChildCount() - 1);
            }
        } else {
            RelativeLayout relativeLayout = new RelativeLayout(FirebaseBaseActivity.this);
            relativeLayout.setTag("firebase_connect");
            relativeLayout.setBackgroundColor(Color.GRAY);
            TextView info = new TextView(FirebaseBaseActivity.this);
            info.setText(R.string.no_connected);
            info.setTextColor(Color.WHITE);
            RelativeLayout.LayoutParams infoLP = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeLayout.addView(info, infoLP);
            ViewGroup.LayoutParams relativeLayoutLP = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            content.addView(relativeLayout, content.getChildCount(), relativeLayoutLP);
            content.getChildAt(0).setVisibility(View.GONE);
        }
    }

}
