package com.digzdigital.shoeapp.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digzdigital.shoeapp.R;

import java.util.ArrayList;

/**
 * Created by Digz on 19/01/2017.
 */

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.ViewHolder> {
    private static MyClickListener myClickListener;
    ArrayList<BluetoothDevice> pairedDevices;

    public BluetoothListAdapter(ArrayList<BluetoothDevice> pairedDevices) {
        this.pairedDevices = pairedDevices;
    }

    public BluetoothDevice getItem(int position) {
        return pairedDevices.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_paired_device, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BluetoothDevice device = getItem(position);

        holder.deviceName.setText(device.getName());
        holder.deviceAddress.setText(device.getAddress());
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return pairedDevices.size();
    }


    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView deviceName, deviceAddress;
        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv_paired_devices);
            deviceAddress = (TextView) itemView.findViewById(R.id.deviceAddress);
            deviceName = (TextView) itemView.findViewById(R.id.deviceName);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);

        }


    }
    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }
}

