package hnu.example.bttest;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private Button mButton_Connect;
    private Button mButton_Disconnect;

    private ListView mDeviceList;
    private TextView mBluetoothStatus;
    private TextView mTextAnzParcels;

    private long mAnzParcels = 0L;

    //for Bluetooth-connection
    private BTManager mBTManager;
    private BTMsgHandler mBTHandler; // Our main handler that will receive callback notifications




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton_Connect = (Button)findViewById(R.id.button_connect);
        mButton_Disconnect = (Button)findViewById(R.id.button_disconnect);

        mDeviceList = (ListView)findViewById(R.id.listView);
        mBluetoothStatus = (TextView) findViewById(R.id.bluetoothStatus);
        mTextAnzParcels = (TextView) findViewById(R.id.text_anzParcels);

        setConnectButtons(false);


        /**
         * Message-Handler to handle messages from BTManager: every new received Text from Bluetooth
         * will call the method receiveMessage --> here can start the UI activities
         */

        mBTHandler = new BTMsgHandler() {
            @Override
            void receiveMessage(String msg) {
                mBluetoothStatus.setText(msg);
                mAnzParcels++;
                mTextAnzParcels.setText(mAnzParcels + "");
            }

            @Override
            void receiveConnectStatus(boolean isConnected) {

                if (isConnected) {
                    mBluetoothStatus.setText("Connected");
                } else {
                    mBluetoothStatus.setText("Connection failed");
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
        mButton_Disconnect.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                if (mBTManager != null) {
                    mBTManager.cancel();
                    setConnectButtons(false);
                }
            }
        });

        //click event to show the paired devices
        mButton_Connect.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                pairedDevicesList();
            }
        });



        //we don't want see the keyboard emulator on start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void pairedDevicesList() {
        mDeviceList.setVisibility(View.VISIBLE);

        ArrayList list = mBTManager.getPairedDeviceInfos();

        if (list.size()>0) {
            final ArrayAdapter adapter;
            adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
            mDeviceList.setAdapter(adapter);
            mDeviceList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
    }

    //reaction after selection of an BT-Device to connect
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);

            mBluetoothStatus.setText("Connecting...");
            mDeviceList.setVisibility(View.INVISIBLE);
            mBTManager.connect(address);
        }
    };

    //adapt GUI corresponding to connect status
    private void setConnectButtons(boolean isConnected) {
        mButton_Disconnect.setEnabled(isConnected);
        mButton_Connect.setEnabled(!isConnected);

        if (isConnected == false) {
            mBluetoothStatus.setText("-");
            mAnzParcels=0L;
            mTextAnzParcels.setText(mAnzParcels + "");
        }

    }



}
