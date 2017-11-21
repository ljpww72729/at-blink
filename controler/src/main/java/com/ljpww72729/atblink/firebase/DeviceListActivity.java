package com.ljpww72729.atblink.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.ArrayMap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.ljpww72729.atblink.R;
import com.ljpww72729.atblink.data.Device;
import com.ljpww72729.atblink.data.RaspberryIotInfo;
import com.ljpww72729.wilddog.ui.database.WildDogRecyclerAdapter;
import com.wilddog.client.SyncReference;

/**
 * Created by LinkedME06 on 2017/9/4.
 */

public class DeviceListActivity extends FirebaseBaseActivity {


    // [START define_database_reference]
    private DatabaseReference deviceFireRef;
    private SyncReference deviceWildRef;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Device, DeviceViewHolder> mFireAdapter;
    private WildDogRecyclerAdapter<Device, DeviceViewHolder> mWildAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private Device operateDevice;
    private ArrayMap<String, Boolean> checkedDeviceConnections = new ArrayMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        // [START create_database_reference]
        if (isFirebaseAddress) {
            deviceFireRef = databaseFireRef.child(RaspberryIotInfo.DEVICE);
        } else {
            deviceWildRef = databaseWildRef.child(RaspberryIotInfo.DEVICE);
        }
        // [END create_database_reference]

        mRecycler = findViewById(R.id.recycler_view);
        mRecycler.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        if (isFirebaseAddress) {
            // Set up FirebaseRecyclerAdapter with the Query
            Query deviceQuery = getQuery(deviceFireRef);
            mFireAdapter = new FirebaseRecyclerAdapter<Device, DeviceViewHolder>(Device.class, R.layout.device_item,
                    DeviceViewHolder.class, deviceQuery) {
                @Override
                protected void populateViewHolder(final DeviceViewHolder viewHolder, final Device model, final int position) {
                    final DatabaseReference postRef = getRef(position);

                    // Set click listener for the whole post view
                    final String deviceKey = postRef.getKey();
                    populateView(viewHolder, model, deviceKey);
                }
            };
            mRecycler.setAdapter(mFireAdapter);
        } else {
            com.wilddog.client.Query deviceQuery = getQuery(deviceWildRef);
            mWildAdapter = new WildDogRecyclerAdapter<Device, DeviceViewHolder>(Device.class, R.layout.device_item,
                    DeviceViewHolder.class, deviceQuery) {
                @Override
                protected void populateViewHolder(final DeviceViewHolder viewHolder, final Device model, final int position) {
                    final SyncReference postRef = getRef(position);

                    // Set click listener for the whole post view
                    final String deviceKey = postRef.getKey();
                    populateView(viewHolder, model, deviceKey);
                }
            };
            mRecycler.setAdapter(mWildAdapter);
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createCallback());
        itemTouchHelper.attachToRecyclerView(mRecycler);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkedDeviceConnections.clear();
    }

    private void populateView(DeviceViewHolder viewHolder, Device model, final String deviceKey) {
        //检查设备是否在线
        // TODO: 2017/9/29 lipeng 有待优化，同步更新数据
        if (checkedDeviceConnections.get(deviceKey) == null) {
            if (isFirebaseAddress) {
                databaseFireRef.child("/" + RaspberryIotInfo.DEVICE + "/" + deviceKey + "/connections").setValue(false);
                databaseFireRef.child("/" + RaspberryIotInfo.DEVICE + "/" + deviceKey + "/changed").setValue(1);
            } else {
                databaseWildRef.child("/" + RaspberryIotInfo.DEVICE + "/" + deviceKey + "/connections").setValue(false);
                databaseWildRef.child("/" + RaspberryIotInfo.DEVICE + "/" + deviceKey + "/changed").setValue(1);
            }
            checkedDeviceConnections.put(deviceKey, true);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch PostDetailActivity
                Bundle bundle = new Bundle();
                bundle.putString(RaspberryIotInfo.DID, deviceKey);
                GPIOListActivity.start(DeviceListActivity.this, bundle);
            }
        });
        viewHolder.bindToDevice(DeviceListActivity.this, model);
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
            if (isFirebaseAddress) {
                mFireAdapter.notifyDataSetChanged();
            } else {
                mWildAdapter.notifyDataSetChanged();
            }
        } else {
            // 右滑去数据
            String deviceId = operateDevice == null ? "" : operateDevice.getDeviceId();
            if (isFirebaseAddress) {
                deviceFireRef.child(deviceId).removeValue();
            } else {
                deviceWildRef.child(deviceId).removeValue();
            }
            Snackbar snackbar = Snackbar.make(findViewById(R.id.constraint_layout), getString(R.string.delete_ok), Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //撤销删除
                    if (isFirebaseAddress) {
                        deviceFireRef.child(operateDevice.getDeviceId()).setValue(operateDevice);
                    } else {
                        deviceWildRef.child(operateDevice.getDeviceId()).setValue(operateDevice);
                    }
                }
            });
            snackbar.show();
        }
    }

    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.orderByKey();
    }

    public com.wilddog.client.Query getQuery(SyncReference databaseReference) {
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
        if (mFireAdapter != null) {
            mFireAdapter.cleanup();
        }
        if (mWildAdapter != null) {
            mWildAdapter.cleanup();
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
