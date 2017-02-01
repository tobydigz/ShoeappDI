package com.digzdigital.shoeapp.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothModule implements DeviceConnector{

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String macAddress;
    private Handler bluetoothIn;
    private int handlerState = 0;
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket bluetoothSocket = null;
    private ConnectedThread connectedThread;
    private BluetoothDevice bluetoothDevice;
    private Context context;

    public BluetoothModule(Context context) {
        this.context = context;

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice bluetoothDevice) throws IOException {
        return bluetoothDevice.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private boolean checkBTState() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            // Snackbar.make(binding.activityDirection, "Device doesn't support bluetooth", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) {
            //Prompt to turn on bluetooth
            // Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // startActivityForResult(enableBtIntent, 1);
        }
        return true;
    }

    @Override
    public void initialiseDevice() {
        if (checkBTState()) return;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void setDevice(Object object) {
        this.bluetoothDevice = (BluetoothDevice)object;
    }

    @Override
    public void initiateConnectionToDevice() {
        try {
            if (macAddress == null) macAddress = bluetoothDevice.getAddress();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
            bluetoothSocket = createBluetoothSocket(device);
        } catch (IOException ignore) {

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            bluetoothSocket.connect();
        } catch (IOException e) {
            try {
                bluetoothSocket.connect();
            } catch (IOException e2) {

            }
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        }

        try {
            connectedThread = new ConnectedThread(bluetoothSocket);
            connectedThread.start();
            //Sending a character to check if the device is connected
            connectedThread.write("L");
            Toast.makeText(context, "Connection success, device connected", Toast.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void endConnectionToDevice() {
        try {
            bluetoothSocket.close();
        } catch (IOException | NullPointerException ignore) {
            // TODO: 28/11/2016 insert code to deal with this
        }
    }

    @Override
    public void sendLeftToDevice() {
        connectedThread.write("L");
        Log.d("DIGZ:Device direction", "Left");
    }

    @Override
    public void sendRightToDevice() {
        connectedThread.write("R");
        Log.d("DIGZ:Device direction", "Right");
    }

    private class ConnectedThread extends Thread {

        private final InputStream inputStream;
        private final OutputStream outputStream;

        ConnectedThread(BluetoothSocket socket) {
            InputStream tempInputStream = null;
            OutputStream tempOutputStream = null;

            try {
                tempInputStream = socket.getInputStream();
                tempOutputStream = socket.getOutputStream();
            } catch (IOException ignore) {

            }

            inputStream = tempInputStream;
            outputStream = tempOutputStream;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //write method
        void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                outputStream.write(msgBuffer);                //write bytes over BT connection via outstream

            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(context, "Connection Failure, device not found", Toast.LENGTH_LONG).show();

            }
        }
    }
}
