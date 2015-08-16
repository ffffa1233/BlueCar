package com.example.pscwww.bluecar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int BLUETOOTH_ON = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private Button bt_connect, bt_headlight, bt_forward, bt_reverse;
    private TextView tv_headline, tv_bluetoothState, tv_value;

    private BluetoothService bluetoothService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_connect = (Button) findViewById(R.id.bt_connect);
        bt_connect.setOnClickListener(this);

        bt_headlight = (Button) findViewById(R.id.bt_headlight);
        bt_headlight.setOnClickListener(this);

        bt_forward = (Button) findViewById(R.id.bt_forward);
        bt_forward.setOnClickListener(this);

        bt_reverse = (Button) findViewById(R.id.bt_reverse);
        bt_reverse.setOnClickListener(this);

        if(bluetoothService == null){
            bluetoothService = new BluetoothService(this);
        }

        tv_headline = (TextView) findViewById(R.id.tv_headline);
        tv_bluetoothState = (TextView) findViewById(R.id.tv_bluetooteState);
        tv_value = (TextView) findViewById(R.id.tv_value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_connect:
                if(bluetoothService.getDeviceState()==true){  //블루투스 사용가능 장비
                    tv_bluetoothState.setText(R.string.title_bluetooth_available);
                    bluetoothService.enableBluetooth();

                }else{ //블루투스 사용불가 장비
                    tv_bluetoothState.setText(R.string.title_bluetooth_unavailable);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setTitle(R.string.title_bluetooth_unavailable)        // 제목 설정
                            .setMessage(R.string.content_bluetooth_unavailable)        // 메세지 설정
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                }
                            });

                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();
                }
                break;
            case R.id.bt_headlight:
                break;
            case R.id.bt_forward :
                break;
            case R.id.bt_reverse :
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BLUETOOTH_ON:
                tv_bluetoothState.setText(R.string.bluetooth_on);
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    tv_bluetoothState.setText(R.string.bluetooth_on);
                } else {
                    tv_bluetoothState.setText(R.string.bluetooth_off);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.title_app_exit)        // 제목 설정
                .setMessage(R.string.content_app_exit)        // 메세지 설정
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();
    }


}
