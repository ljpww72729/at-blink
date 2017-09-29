package com.ljpww72729.atblink.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.ljpww72729.atblink.R;
import com.ljpww72729.atblink.data.Device;
import com.ljpww72729.atblink.data.RaspberryIotInfo;

/**
 * Created by LinkedME06 on 2017/9/4.
 */

public class DeviceListActivity extends FirebaseBaseActivity {


    // [START define_database_reference]
    private DatabaseReference deviceRef;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Device, DeviceViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private Device operateDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        // [START create_database_reference]
        deviceRef = FirebaseDatabase.getInstance().getReference().child(RaspberryIotInfo.DEVICE);
        // [END create_database_reference]

        mRecycler = findViewById(R.id.recycler_view);
        mRecycler.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query deviceQuery = getQuery(deviceRef);
        mAdapter = new FirebaseRecyclerAdapter<Device, DeviceViewHolder>(Device.class, R.layout.device_item,
                DeviceViewHolder.class, deviceQuery) {
            @Override
            protected void populateViewHolder(final DeviceViewHolder viewHolder, final Device model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String deviceKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Bundle bundle = new Bundle();
                        bundle.putString(RaspberryIotInfo.DID, deviceKey);
                        GPIOListActivity.start(DeviceListActivity.this, bundle);
                    }
                });

//                // Determine if the current user has liked this post and set UI accordingly
//                if (model.stars.containsKey(getUid())) {
//                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
//                } else {
//                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
//                }

                viewHolder.bindToDevice(DeviceListActivity.this, model);

//                // Bind Post to ViewHolder, setting OnClickListener for the star button
//                viewHolder.bindToDevice(model, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View starView) {
//                        // Need to write to both places the post is stored
//                        DatabaseReference globalPostRef = gpioDeviceRef.child("posts").child(postRef.getKey());
//                        DatabaseReference userPostRef = gpioDeviceRef.child("user-posts").child(model.uid).child(postRef.getKey());
//
//                        // Run two transactions
//                        onStarClicked(globalPostRef);
//                        onStarClicked(userPostRef);
//                    }
//                });
            }
        };
        mRecycler.setAdapter(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createCallback());
        itemTouchHelper.attachToRecyclerView(mRecycler);

    }

    private ItemTouchHelper.Callback createCallback() {
        return new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                itemSwiped(viewHolder, direction);
            }
        };
    }

    private void itemSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        operateDevice = ((DeviceViewHolder) viewHolder).device;
        if (direction == ItemTouchHelper.START) {
            // 左滑编辑
            Bundle bundle = new Bundle();
            bundle.putParcelable(RaspberryIotInfo.DEVICE, operateDevice);
            DeviceAddActivity.start(this, bundle);
            // 刷新列表，使其不变更
            mAdapter.notifyDataSetChanged();
        } else {
            // 右滑去数据
            String deviceId = operateDevice == null ? "" : operateDevice.getDeviceId();
            deviceRef.child(deviceId).removeValue();
            Snackbar snackbar = Snackbar.make(findViewById(R.id.constraint_layout), getString(R.string.delete_ok), Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //撤销删除
                    deviceRef.child(operateDevice.getDeviceId()).setValue(operateDevice);
                }
            });
            snackbar.show();
        }
    }

    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.orderByKey();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.add) {
            DeviceAddActivity.start(this, null);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public static void start(Context context, Bundle bundle) {
        Intent starter = new Intent(context, DeviceListActivity.class);
        if (bundle != null) {
            starter.putExtras(bundle);
        }
        context.startActivity(starter);
    }

}
