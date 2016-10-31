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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/*
http://dsnight.tistory.com/36에서 제공된 코드들을
기반으로 공부하고 작성된 코드입니다.
*/
public class MainActivity extends ActionBarActivity implements View.OnClickListener, View.OnTouchListener {

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private static final int CONNECTING = 101;
    private static final int CONNECTED = 102;
    private static final int CONNECT_FAIL = 103;

    private static final int GET_DATA = 104;

    private Button bt_connect, bt_accel, bt_break;
    private ImageButton bt_stop, bt_forward, bt_backward, bt_left, bt_right;
    private TextView tv_headline, tv_bluetoothState, tv_leftVel, tv_rightVel;

    private BluetoothService bluetoothService = null;

    AlertDialog.Builder builder;

    private int leftVel = 0;
    private int rightVel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_connect = (Button) findViewById(R.id.bt_connect);
        bt_connect.setOnClickListener(this);

        bt_stop = (ImageButton) findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(this);

        bt_forward = (ImageButton) findViewById(R.id.bt_forward);
        bt_forward.setOnClickListener(this);

        bt_backward = (ImageButton) findViewById(R.id.bt_backward);
        bt_backward.setOnClickListener(this);

        bt_left = (ImageButton) findViewById(R.id.bt_left);
        bt_left.setOnClickListener(this);

        bt_right = (ImageButton) findViewById(R.id.bt_right);
        bt_right.setOnClickListener(this);

        bt_accel = (Button) findViewById(R.id.bt_accel);
        bt_accel.setOnTouchListener(this);

        bt_break = (Button) findViewById(R.id.bt_break);
        bt_break.setOnTouchListener(this);

        if(bluetoothService == null){
            bluetoothService = new BluetoothService(this);
        }

        tv_headline = (TextView) findViewById(R.id.tv_headline);
        tv_bluetoothState = (TextView) findViewById(R.id.tv_bluetooteState);
        tv_leftVel = (TextView) findViewById(R.id.tv_leftVel);
        tv_rightVel = (TextView) findViewById(R.id.tv_rightVel);
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
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(view.getId()){
            /*case R.id.bt_left:
                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    leftVel *= 1;
                    rightVel = (int)((double)rightVel * 1.01);
                    tv_leftVel.setText(Integer.toString(leftVel));
                    tv_rightVel.setText(Integer.toString(rightVel));
                }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    leftVel = (int)((leftVel+rightVel)/2);
                    rightVel = leftVel;
                    tv_leftVel.setText(Integer.toString(leftVel));
                    tv_rightVel.setText(Integer.toString(rightVel));
                }
                break;
            case R.id.bt_right:
                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    leftVel = (int)((double)leftVel * 1.01);
                    rightVel *= 1;
                    tv_leftVel.setText(Integer.toString(leftVel));
                    tv_rightVel.setText(Integer.toString(rightVel));
                }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    leftVel = (int)((leftVel+rightVel)/2);
                    rightVel = leftVel;
                    tv_leftVel.setText(Integer.toString(leftVel));
                    tv_rightVel.setText(Integer.toString(rightVel));
                }
                break;*/
            case R.id.bt_accel:
                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    bluetoothService.write("u".getBytes());
                    leftVel++;
                    rightVel++;
                    if(leftVel > 255)
                        leftVel = 255;
                    if(rightVel > 255)
                        rightVel = 255;
                    tv_leftVel.setText(Integer.toString(leftVel));
                    tv_rightVel.setText(Integer.toString(rightVel));
                }
                break;
            case R.id.bt_break:
                bluetoothService.write("d".getBytes());
                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    leftVel--;
                    rightVel--;
                    if(leftVel < 0)
                        leftVel = 0;
                    if(rightVel < 0)
                        rightVel = 0;
                    tv_leftVel.setText(Integer.toString(leftVel));
                    tv_rightVel.setText(Integer.toString(rightVel));
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_connect:
                if(bluetoothService.getDeviceState()==true){
                    //블루투스 사용가능 장비
                    tv_bluetoothState.setText(R.string.title_bluetooth_available);
                    bluetoothService.enableBluetooth();
                }else{
                    //블루투스 사용불가 장비, 종료알림창, 종료
                    tv_bluetoothState.setText(R.string.title_bluetooth_unavailable);
                    builder = new AlertDialog.Builder(this);
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
            case R.id.bt_stop:
                bluetoothService.write("s".getBytes());
                leftVel = 0;
                rightVel = 0;
                tv_leftVel.setText(Integer.toString(leftVel));
                tv_rightVel.setText(Integer.toString(rightVel));
                break;
            case R.id.bt_forward :
                bluetoothService.write("f".getBytes());
                //tv_leftVel.setText(Integer.toString(leftVel));
                //tv_rightVel.setText(Integer.toString(rightVel));
                break;
            case R.id.bt_backward :
                bluetoothService.write("b".getBytes());
                //tv_leftVel.setText(Integer.toString(leftVel));
                //tv_rightVel.setText(Integer.toString(rightVel));
                break;
            case R.id.bt_left:
                bluetoothService.write("l".getBytes());
                break;
            case R.id.bt_right:
                bluetoothService.write("r".getBytes());
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                //DeviceListActivity에서 연결된 장치 반환했을 때
                if (resultCode == Activity.RESULT_OK) {
                    //장치 정보 받아옴
                    bluetoothService.getDeviceInfo(data);
                }else{

                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    //블루투스 켜짐
                    tv_bluetoothState.setText(R.string.bluetooth_on);
                    bluetoothService.scanDevice();
                } else {
                    //블루투스 안켜짐
                    tv_bluetoothState.setText(R.string.bluetooth_off);
                }
                break;
            case CONNECTING :
                tv_bluetoothState.setText("connecting!!!");
                break;
            case CONNECTED :
                tv_bluetoothState.setText("connected!!!");
                break;
            case CONNECT_FAIL :
                tv_bluetoothState.setText("connect fail!!!");
                break;
            case GET_DATA :
                bluetoothService.write("c".getBytes());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_app_exit)        // 제목 설정
                .setMessage(R.string.content_app_exit)        // 메세지 설정
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //프로그램 종료
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                    //없음
                    }
                });
        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();
    }

}
