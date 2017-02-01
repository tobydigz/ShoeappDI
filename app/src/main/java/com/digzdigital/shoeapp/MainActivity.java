package com.digzdigital.shoeapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.digzdigital.shoeapp.adapter.BluetoothListAdapter;
import com.digzdigital.shoeapp.databinding.ActivityMainBinding;
import com.digzdigital.shoeapp.navigation.NavigationActivity;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private ActivityMainBinding binding;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.skip.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!checkBTState()) {

            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                bluetoothDevices.add(device);
            }
            doRest();
        }
    }

    private boolean checkBTState() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Snackbar.make(binding.activityMain, "Device doesn't support bluetooth", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, 1);
        }

        return bluetoothAdapter.isEnabled();
    }


    protected void doRest() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.pairedDevices.setLayoutManager(linearLayoutManager);
        if (bluetoothDevices == null) return;
        if (bluetoothDevices.size() > 0) {
            BluetoothListAdapter bluetoothListAdapter = new BluetoothListAdapter(bluetoothDevices);
            binding.pairedDevices.setAdapter(bluetoothListAdapter);

            bluetoothListAdapter.setOnItemClickListener(new BluetoothListAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    BluetoothDevice device = bluetoothDevices.get(position);
                    Intent i = new Intent(getApplicationContext(), NavigationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("device", device);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
        }

    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, NavigationActivity.class));
    }
}

