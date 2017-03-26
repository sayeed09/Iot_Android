package com.example.ap.zbnf;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Ref;

import static android.R.attr.value;


// other code

public class BluetoothActivity extends Activity
{
    //firebase Code
    int i=0;
    private Button mSendData;
    private EditText mValue;
    private Firebase mRef;


    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Whenever a remote Bluetooth device is found
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                adapter.add(bluetoothDevice.getName() + "\n"
                        + bluetoothDevice.getAddress());



           /*     Toast.makeText(getApplicationContext(), "COunt" + adapter.getCount() + " " + adapter.getItem(0),
                        Toast.LENGTH_SHORT).show();
                        */

         /*       for(int i=0 ; i<adapter.getCount() ; i++){
                    Object obj = adapter.getItem(i);
                }  */
                for(int j=0 ; i<adapter.getCount() ; j++)
                {
                  //  final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                 //   DatabaseReference rootRefChild = rootRef.child("Name");
                   // rootRefChild.push().setValue(adapter.getItem(i));
                    final DatabaseReference rootRef = FirebaseDatabase.getInstance()
                            .getReferenceFromUrl("https://android-33260.firebaseio.com/Macid");

                    i++;
                    DatabaseReference rootRefChild = rootRef.child(String.valueOf(i));
                    rootRefChild.setValue(adapter.getItem(j));

                }


            }
        }
    };
    private BluetoothAdapter bluetoothAdapter;
    private ToggleButton toggleButton;
    private ListView listview;
    private ArrayAdapter adapter;
    private static final int ENABLE_BT_REQUEST_CODE = 1;
    private static final int DISCOVERABLE_BT_REQUEST_CODE = 2;
    private static final int DISCOVERABLE_DURATION = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        listview = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter
                (this,android.R.layout.simple_list_item_1);
        listview.setAdapter(adapter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //BUtton firebase CODE
        //FIrebase CODE
        Firebase.setAndroidContext(this);
        //disks my side
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
       // final DatabaseReference rootRefB = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://android-33260.firebaseio.com/Macid");
        //Completion of FIREBASE CODE


        mValue =(EditText) findViewById(R.id.AddValue);
        mSendData =(Button) findViewById(R.id.AddData);

        mSendData.setOnClickListener(new View.OnClickListener()
        {
          @Override
          public void onClick(View view)
          {
              String value = mValue.getText().toString();
              //int i=1;

              i++;
               DatabaseReference rootRefChild = rootRef.child("MacId"+String.valueOf(i));
              rootRefChild.setValue(value);
              String key =rootRefChild.getKey();
              Toast.makeText(getApplicationContext(),key,
                      Toast.LENGTH_SHORT).show();


              final DatabaseReference rootRefB = FirebaseDatabase.getInstance()
                      .getReferenceFromUrl("https://android-33260.firebaseio.com");
                 DatabaseReference rootRefChildB=rootRefB.child("count");
               rootRefChildB.setValue(String.valueOf(i));








          }
                                     }
        );

    }

    public void onToggleClicked(View view) {

        adapter.clear();

        ToggleButton toggleButton = (ToggleButton) view;

        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "Oop! Your device does not support Bluetooth",
                    Toast.LENGTH_SHORT).show();
            toggleButton.setChecked(false);
        } else
            {

            if (toggleButton.isChecked())
            { // to turn on bluetooth
                if (!bluetoothAdapter.isEnabled())
                {
                    // A dialog will appear requesting user permission to enable Bluetooth
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, ENABLE_BT_REQUEST_CODE);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Your device has already been enabled." +
                                    "\n" + "Scanning for remote Bluetooth devices...",
                            Toast.LENGTH_SHORT).show();
                    // To discover remote Bluetooth devices
                    discoverDevices();
                    // Make local device discoverable by other devices
                    makeDiscoverable();
                }
            }
            else
            { // Turn off bluetooth

                bluetoothAdapter.disable();
                adapter.clear();
                Toast.makeText(getApplicationContext(), "Your device is now disabled.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENABLE_BT_REQUEST_CODE) {

            // Bluetooth successfully enabled!
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Ha! Bluetooth is now enabled." +
                                "\n" + "Scanning for remote Bluetooth devices...",
                        Toast.LENGTH_SHORT).show();

                // Make local device discoverable by other devices
                makeDiscoverable();

                // To discover remote Bluetooth devices
                discoverDevices();

            } else { // RESULT_CANCELED as user refused or failed to enable Bluetooth
                Toast.makeText(getApplicationContext(), "Bluetooth is not enabled.",
                        Toast.LENGTH_SHORT).show();

                // Turn off togglebutton
                toggleButton.setChecked(false);
            }
        } else if (requestCode == DISCOVERABLE_BT_REQUEST_CODE){

            if (resultCode == DISCOVERABLE_DURATION){
                Toast.makeText(getApplicationContext(), "Your device is now discoverable by other devices for " +
                                DISCOVERABLE_DURATION + " seconds",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Fail to enable discoverability on your device.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void discoverDevices()
    {
        // To scan for remote Bluetooth devices
        if (bluetoothAdapter.startDiscovery()) {
            Toast.makeText(getApplicationContext(), "Discovering other bluetooth devices...",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Discovery failed to start.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void makeDiscoverable()
    {
        // Make local device discoverable
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
        startActivityForResult(discoverableIntent, DISCOVERABLE_BT_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the BroadcastReceiver for ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(broadcastReceiver);
    }

// other code
}