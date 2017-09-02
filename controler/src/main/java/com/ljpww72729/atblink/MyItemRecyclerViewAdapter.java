package com.ljpww72729.atblink;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljpww72729.atblink.DeviceScanFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {
    private ArrayList<BluetoothDevice> mLeDevices;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mLeDevices = new ArrayList<BluetoothDevice>();
        mListener = listener;
    }

    public void addDevice(BluetoothDevice device) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BluetoothDevice device = getDevice(position);

        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            holder.mDeviceName.setText(deviceName);
        else
            holder.mDeviceName.setText(R.string.unknown_device);
        holder.mDeviceAddress.setText(mLeDevices.get(position).getAddress());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(device);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLeDevices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDeviceName;
        public final TextView mDeviceAddress;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDeviceName = (TextView) view.findViewById(R.id.device_name);
            mDeviceAddress = (TextView) view.findViewById(R.id.device_address);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDeviceAddress.getText() + "'";
        }
    }
}
