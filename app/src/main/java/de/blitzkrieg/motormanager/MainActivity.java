package de.blitzkrieg.motormanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private long mAnzParcels = 0L;

    private BTManager mBTManager;
    private BTMsgHandler mBTHandler;
    private TextView mBluetoothStatus;
    private TextView mTextAnzParcels;
    private Switch mConnect;
    private ListView mDeviceList;

    private TextView mKmh;
    private TextView mRpm;

    public static String EXTRA_MESSAGE;
    public String msg;


    public void setMsg(String msg) {
        this.msg = msg;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TextView for speed and rpm
        mKmh = (TextView) findViewById(R.id.textKmh);
        mRpm = (TextView) findViewById(R.id.textRpm);

        //TextView for BT and received parcels
        mBluetoothStatus = (TextView) findViewById(R.id.bluetoothStatus);
        mTextAnzParcels = (TextView) findViewById(R.id.text_anzParcels);
        mDeviceList = (ListView) findViewById(R.id.listView);

        mConnect = (Switch) findViewById(R.id.switch1);

        mKmh.setText("0.0");
        mRpm.setText("0000");

        setConnectButtons(false);

        mBTHandler = new BTMsgHandler() {
            @Override
            void receiveMessage(String msg) {
                mBluetoothStatus.setText(msg);
                mAnzParcels++;
                mTextAnzParcels.setText(mAnzParcels + "");
                setMsg(msg);
                int msgZ = Integer.parseInt(msg);
                do {
                    if (msgZ < 300) {
                        mKmh.setText(msg);
                    } else {
                        mRpm.setText(msg);
                    }

                } while (msgZ != 0);
            }

            @Override
            void receiveConnectStatus(boolean isConnected) {

                if (isConnected) {
                    mBluetoothStatus.setText(R.string.connect);
                } else {
                    mBluetoothStatus.setText(R.string.conn_fail);
                }
                setConnectButtons(isConnected);
            }

            @Override
            void handleException(Exception e) {
                mBluetoothStatus.setText("-");
                setConnectButtons(false);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        //create a new BTManager to handle the connections to BTdevices
        try {
            mBTManager = new BTManager(this, mBTHandler);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }


        //disconnect the BT-device
        mConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mBTManager != null) {
                    mBTManager.cancel();
                    setConnectButtons(false);
                }
            }
        });


        //click event to show the paired devices
        mConnect.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (mDeviceList.getVisibility() != View.VISIBLE) {
                    pairedDevicesList();

                } else {
                    if (mBTManager != null) {
                        mBTManager.cancel();
                        setConnectButtons(false);
                    }
                    mDeviceList.setVisibility(View.GONE);

                }
                ;
            }
        });

        //we don't want see the keyboard emulator on start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void pairedDevicesList() {
        mDeviceList.setVisibility(View.VISIBLE);
        ArrayList<String> list = mBTManager.getPairedDeviceInfos();

        if (list.size() > 0) {
            final ArrayAdapter adapter;
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            mDeviceList.setAdapter(adapter);
            mDeviceList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
    }

    //reaction after selection of an BT-Device to connect
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);

            mBluetoothStatus.setText(R.string.connecting);
            mConnect.setText(R.string.connecting);
            mDeviceList.setVisibility(View.INVISIBLE);
            mBTManager.connect(address);
        }
    };

    public void setConnectButtons(boolean isConnected){
            mConnect.setChecked(isConnected);

            if (!isConnected) {
                mBluetoothStatus.setText("-");
                mAnzParcels=0L;
                mTextAnzParcels.setText(mAnzParcels + "");
                mConnect.setText(R.string.connect);
            }
    }


    public void changeScreen(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        String message = "0000";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
    }

