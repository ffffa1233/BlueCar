package com.example.pscwww.bluecar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import java.util.logging.Handler;

/**
 * Created by pscwww on 2015-08-16.
 */
public class BluetoothService {
    private BluetoothAdapter btAdapter;
    private Activity mActivity;

    private static final int BLUETOOTH_ON = 1;
    private static final int REQUEST_ENABLE_AT = 2;

    public BluetoothService(Activity ac){
        mActivity = ac;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    public boolean getDeviceState(){
        if(btAdapter == null){
            //bluetooth is not available
            return false;
        }else{
            //bluetooth is available
            return true;
        }
    }

    public void enableBluetooth(){
        if(btAdapter.isEnabled()){
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(i, BLUETOOTH_ON);
        }else{
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(i, REQUEST_ENABLE_AT);
        }
    }

}
