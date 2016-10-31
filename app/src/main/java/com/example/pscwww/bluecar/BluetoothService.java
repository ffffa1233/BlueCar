package com.example.pscwww.bluecar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by pscwww on 2015-08-16.
 */
public class BluetoothService {
    private static final String TAG = "BluetoothService";
    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private static final int CONNECTING = 101;
    private static final int CONNECTED = 102;
    private static final int CONNECT_FAIL = 103;

    private static final int GET_DATA = 104;

    // RFCOMM Protocol
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter btAdapter;

    private Activity mActivity;

    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private int mState;


    private static final int STATE_NONE = 0; // we're doing nothing
    private static final int STATE_LISTEN = 1; // now listening for incoming
    private static final int STATE_CONNECTING = 2; // now initiating an outgoing
    private static final int STATE_CONNECTED = 3; // now connected to a remote

    // 생성자
    public BluetoothService(Activity ac) {


        mActivity = ac;
        // BluetoothAdapter 기본값
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    //어플이 깔린 스마트폰의 블루투스 사용가능 여부 확인
    public boolean getDeviceState() {
        Log.i(TAG, "Check the Bluetooth support");
        if (btAdapter == null) {
            Log.d(TAG, "Bluetooth is not available");
            return false;
        } else {
            Log.d(TAG, "Bluetooth is available");
            return true;
        }
    }

    //블루투스 온오프 상태 확인
    public void enableBluetooth() {
        Log.i(TAG, "Check the enabled Bluetooth");
        if (btAdapter.isEnabled()) { //블루투스 켜져 있는 상태
            Log.d(TAG, "Bluetooth Enable Now");
            // Next Step
            scanDevice();
        } else { //블루투스 꺼져 있는 상태
            Log.d(TAG, "Bluetooth Enable Request");
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(i, REQUEST_ENABLE_BT);
        }
    }

    //장비 스캔
    public void scanDevice() {
        Log.d(TAG, "Scan Device");
        Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
        mActivity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    //스캔 이후 data변수로 장치정보 가져오기
    public void getDeviceInfo(Intent data) {
        //MAC 주소 가져오기
        String address = data.getExtras().getString(
                DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //객체 받아와 연결
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        Log.d(TAG, "Get Device Info \n" + "address : " + address);

        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        mActivity.startActivityForResult(i, CONNECTING);

        connect(device);
    }

    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void start() {
        Log.d(TAG, "start");
        // Cancel any thread attempting to make a connection
        if (mConnectThread == null) {

        } else {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread == null) {

        } else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device);
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread == null) {

            } else {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread == null) {

        } else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);

        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device) {
        Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread == null) {

        } else {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread == null) {

        } else {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        Log.d(TAG, "stop");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }

    public void write(byte[] out) { // Create temporary object
        ConnectedThread r; // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        //connectThread클래스 write메소드로 값 전달
        r.write(out);
    }

    private void connectionFailed() {
        setState(STATE_LISTEN);
    }

    private void connectionLost() {
        setState(STATE_LISTEN);

    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

			/*
			 * / // Get a BluetoothSocket to connect with the given
			 * BluetoothDevice try { // MY_UUID is the app's UUID string, also
			 * used by the server // code tmp =
			 * device.createRfcommSocketToServiceRecord(MY_UUID);
			 *
			 * try { Method m = device.getClass().getMethod(
			 * "createInsecureRfcommSocket", new Class[] { int.class }); try {
			 * tmp = (BluetoothSocket) m.invoke(device, 15); } catch
			 * (IllegalArgumentException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } catch (IllegalAccessException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } catch
			 * (InvocationTargetException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); }
			 *
			 * } catch (NoSuchMethodException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); } } catch (IOException e) { } /
			 */

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");
            btAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                Log.d(TAG, "Connect Success");
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(i, CONNECTED);
            } catch (IOException e) {
                connectionFailed();
                Log.d(TAG, "Connect Fail");
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(i, CONNECT_FAIL);
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG,
                            "unable to close() socket during connection failure",
                            e2);
                }

                BluetoothService.this.start();
                return;
            }

            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    if(bytes != 0){
                        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        mActivity.startActivityForResult(i, GET_DATA);
                    }

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    mActivity.startActivityForResult(i, CONNECT_FAIL);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer
         *            The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                Log.d(TAG, "write data");
                mmOutStream.write(buffer);

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

}