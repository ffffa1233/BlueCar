package com.example.pscwww.bluecar;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private Button bt_connect, bt_headlight, bt_forward, bt_reverse;
    private TextView tv_headline, tv_value;

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
                break;
            case R.id.bt_headlight:
                break;
            case R.id.bt_forward :
                break;
            case R.id.bt_reverse :
                break;
        }
    }
}
