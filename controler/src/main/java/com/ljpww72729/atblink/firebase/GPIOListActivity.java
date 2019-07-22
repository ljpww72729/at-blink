package com.ljpww72729.atblink.firebase;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.ljpww72729.atblink.R;
import com.ljpww72729.atblink.data.GPIO;
import com.ljpww72729.atblink.data.RaspberryIotInfo;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by LinkedME06 on 2017/9/5.
 */

public class GPIOListActivity extends FirebaseBaseActivity {
    private static final String TAG = "GPIOListActivity";
    // [START define_database_reference]
    private DatabaseReference gpioDeviceFireRef;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<GPIO, GPIOViewHolder> mFireAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private String deviceId;
    private GPIO operateGpio;
    // 标识是否点击了切换开关
    private boolean switchClick = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        // [START create_database_reference]
        getSupportActionBar().setTitle(R.string.gpio_list_title);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            deviceId = bundle.getString(RaspberryIotInfo.DID);
            Log.i(TAG, "onCreate: deviceId=" + deviceId);
        } else {
            finish();
        }
        if (isFirebaseAddress) {
            gpioDeviceFireRef = databaseFireRef.child(RaspberryIotInfo.GPIO).child(deviceId);
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
            Query gpioQuery = getQuery(gpioDeviceFireRef);
            mFireAdapter = new FirebaseRecyclerAdapter<GPIO, GPIOViewHolder>(GPIO.class, R.layout.gpio_item,
                    GPIOViewHolder.class, gpioQuery) {
                @Override
                protected void populateViewHolder(final GPIOViewHolder viewHolder, final GPIO model, final int position) {
                    final DatabaseReference postRef = getRef(position);

                    // Set click listener for the whole post view
                    final String gpioKey = postRef.getKey();
                    populateView(viewHolder, model, gpioKey);

                }
            };
            mRecycler.setAdapter(mFireAdapter);
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createCallback());
        itemTouchHelper.attachToRecyclerView(mRecycler);
    }

    private void populateView(GPIOViewHolder viewHolder, final GPIO model, final String gpioKey) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch PostDetailActivity
                Bundle bundle = new Bundle();
                bundle.putString(RaspberryIotInfo.OPERATE, RaspberryIotInfo.QUERY);
                bundle.putParcelable(RaspberryIotInfo.GPIO, model);
                bundle.putString(RaspberryIotInfo.DID, deviceId);
                GPIOAddActivity.start(GPIOListActivity.this, bundle);
            }
        });

        viewHolder.bindToGPIO(model, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!switchClick) {
                    if (isFirebaseAddress) {
                        switchFireChanged(gpioDeviceFireRef.child(gpioKey).child(GPIO.P_STATUS));
                    }
                    switchClick = true;
                }
            }
        });
    }

    private void switchFireChanged(DatabaseReference database) {
        database.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Object value = mutableData.getValue();
                if (value == null) {
                    return Transaction.success(mutableData);
                }
                boolean status = (boolean) value;
                status = !status;
                mutableData.setValue(status);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                switchClick = false;
                if (databaseError == null) {
                    // TODO: 2017/9/29 lipeng 此处设置在极端情况下存在不成功的问题，但下面snackbar提示成功
                    databaseFireRef.child("/" + RaspberryIotInfo.DEVICE + "/" + deviceId + "/changed").setValue(1);
                    Snackbar.make(findViewById(R.id.constraint_layout), R.string.operate_succeed, Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(findViewById(R.id.constraint_layout),
                            getString(R.string.operate_failed_msg, databaseError.getMessage()),
                            Snackbar.LENGTH_SHORT).show();
                }
                Log.i(TAG, "postTransaction:onComplete:" + databaseError);
            }
        }, false);
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
        operateGpio = ((GPIOViewHolder) viewHolder).gpioEntry;
        if (direction == ItemTouchHelper.START) {
            // 左滑编辑
            Bundle bundle = new Bundle();
            bundle.putString(RaspberryIotInfo.OPERATE, RaspberryIotInfo.UPDATE);
            bundle.putParcelable(RaspberryIotInfo.GPIO, operateGpio);
            bundle.putString(RaspberryIotInfo.DID, deviceId);
            GPIOAddActivity.start(this, bundle);
            // 刷新列表，使其不变更
            if (isFirebaseAddress) {
                mFireAdapter.notifyDataSetChanged();
            }
        } else {
            // 右滑去数据
            String gpioId = operateGpio == null ? "" : operateGpio.getGpioId();
            if (isFirebaseAddress) {
                gpioDeviceFireRef.child(gpioId).removeValue();
            }
            Snackbar snackbar = Snackbar.make(findViewById(R.id.constraint_layout), getString(R.string.delete_ok), Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //撤销删除
                    if (isFirebaseAddress) {
                        gpioDeviceFireRef.child(operateGpio.getGpioId()).setValue(operateGpio);
                    }
                }
            });
            snackbar.show();
        }
    }

    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.orderByKey();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFireAdapter != null) {
            mFireAdapter.cleanup();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.device_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.add:
                Bundle bundle = new Bundle();
                bundle.putString(RaspberryIotInfo.DID, deviceId);
                GPIOAddActivity.start(this, bundle);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void start(Context context, Bundle bundle) {
        Intent starter = new Intent(context, GPIOListActivity.class);
        if (bundle != null) {
            starter.putExtras(bundle);
        }
        context.startActivity(starter);
    }

}
