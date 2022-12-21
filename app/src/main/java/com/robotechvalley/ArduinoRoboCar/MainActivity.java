package com.robotechvalley.ArduinoRoboCar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SwitchCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private String deviceName = null;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;
    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message

    public boolean Pressed;
    SwitchCompat simpleSwitch;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility", "MissingPermission", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        setContentView(R.layout.activity_main);

        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, 101);

        final AppCompatImageButton fButton = findViewById(R.id.fButtonId);
        final AppCompatImageButton bButton = findViewById(R.id.bButtonId);
        final AppCompatImageButton lButton = findViewById(R.id.lButtonId);
        final AppCompatImageButton rButton = findViewById(R.id.rButtonId);
        final TextView connectionStat = findViewById(R.id.connectionStatId);
        final AppCompatImageButton buttonConnect = findViewById(R.id.buttonConnect);
        final AppCompatImageButton infoButton = findViewById(R.id.infoButtonId);

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        simpleSwitch = findViewById(R.id.switchId);// initiate Switch
        simpleSwitch.setTextOn("ROBOT"); // displayed text of the Switch whenever it is in checked or on state
        simpleSwitch.setTextOff("CAR"); // displayed text of the Switch whenever it is in unchecked i.e. off state

        simpleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                Log.d("TAG", "Switched to Robot Mode");
                Toast.makeText(getApplicationContext(), "Robot Mode", Toast.LENGTH_SHORT).show();
                fButton.setOnTouchListener((v, event) -> {

                    fButton.setOnClickListener(v1 -> {
                        try {
                            connectedThread.write("F");
                            Log.d("TAG", "Connected:F button is pressed");
                            vib.vibrate(50);
                        } catch (Exception e) {
                            Log.d("TAG", "Not Connected:F button is pressed");
                            vib.vibrate(50);
//                            e.printStackTrace();
                        }
                    });
                    return false;
                });
                bButton.setOnTouchListener((v, event) -> {
                    bButton.setOnClickListener(v1 -> {
                        try {
                            connectedThread.write("B");
                            Log.d("TAG", "Connected:B button is pressed");
                            vib.vibrate(50);

                        } catch (Exception e) {
                            Log.d("TAG", "Not Connected:B button is pressed");
                            vib.vibrate(50);
//                            e.printStackTrace();
                        }
                    });
                    return false;
                });
                lButton.setOnTouchListener((v, event) -> {
                    lButton.setOnClickListener(v1 -> {
                        try {
                            connectedThread.write("L");
                            Log.d("TAG", "Connected:L button is pressed");
                            vib.vibrate(50);
                        } catch (Exception e) {
                            Log.d("TAG", "Not Connected:L button is pressed");
                            vib.vibrate(50);
//                            e.printStackTrace();
                        }
                    });
                    return false;
                });
                rButton.setOnTouchListener((v, event) -> {
                    rButton.setOnClickListener(v1 -> {
                        try {
                            connectedThread.write("R");
                            Log.d("TAG", "Connected:R button is pressed");
                            vib.vibrate(50);
                        } catch (Exception e) {
                            Log.d("TAG", "Not Connected:R button is pressed");
                            vib.vibrate(50);
//                            e.printStackTrace();
                        }
                    });
                    return false;
                });
            } else {
                Toast.makeText(getApplicationContext(), "Car Mode", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "Switched to Car Mode");
                fButton.setOnTouchListener((v, event) -> {
                    try {
                        connectedThread.write("F");
                        Log.d("TAG", "Connected:F button is pressed");
                    } catch (Exception e) {
                        Log.d("TAG", "Not Connected:F button is pressed");
//                        e.printStackTrace();
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Pressed = false;
                        try {
                            connectedThread.write("S");
                            Log.d("TAG", "Connected:S value passing");
                        } catch (Exception e) {
                            Log.d("TAG", "Not Connected:S value passing");
//                            e.printStackTrace();
                        }
                    }
                    return true;
                });


                bButton.setOnTouchListener((v, event) -> {
                    try {
                        connectedThread.write("B");
                        Log.d("TAG", "Connected:B button is pressed");

                    } catch (Exception e) {
                        Log.d("TAG", "Not Connected:B button is pressed");
//                        e.printStackTrace();
                    }

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Pressed = false;
                        try {
                            connectedThread.write("S");
                            Log.d("TAG", "Connected:S value passing");
                        } catch (Exception e) {
                            Log.d("TAG", "Not Connected:S value passing");
//                            e.printStackTrace();
                        }
                    }

                    return true;
                });


                lButton.setOnTouchListener((v, event) -> {
                    try {
                        connectedThread.write("L");
                        Log.d("TAG", "Connected:L button is pressed");
                    } catch (Exception e) {
                        Log.d("TAG", "Not Connected:L button is pressed");
//                        e.printStackTrace();
                    }

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Pressed = false;
                        try {
                            connectedThread.write("S");
                            Log.d("TAG", "Connected:S value passing");
                        } catch (Exception e) {
                            Log.d("TAG", "Not Connected:S value passing");
//                            e.printStackTrace();
                        }
                    }
                    return true;
                });

                rButton.setOnTouchListener((v, event) -> {
                    try {
                        connectedThread.write("R");
                        Log.d("TAG", "Connected:R button is pressed");
                    } catch (Exception e) {
                        Log.d("TAG", "Not Connected:R button is pressed");
//                        e.printStackTrace();
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Pressed = false;
                        try {
                            connectedThread.write("S");
                            Log.d("TAG", "Connected:S value passing");
                        } catch (Exception e) {
                            Log.d("TAG", "Not Connected:S value passing");
//                            e.printStackTrace();
                        }
                    }
                    return true;
                });
            }
        });

        infoButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setMessage(R.string.button_values)
                    .setIcon(R.drawable.ic_arduino_icon)
                    .setTitle(R.string.alert_title)
                    .setCancelable(false)
                    .setPositiveButton("Back", (dialog, id) -> dialog.cancel());

            // Creating the AlertDialog object and return it
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            ((TextView)alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

        });

       /* final int[] clickCount = {0};
        connectionStat.setOnClickListener(v ->
        {
            clickCount[0]++;
            if (clickCount[0] == 50) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage("Iqbal Hossen Riaz || Robo Tech Valley || GitHub: iqbalriiaz")
                        .setTitle("Developer Info")
                        .setCancelable(false)
                        .setPositiveButton("Back", (dialog, id) -> dialog.cancel());
                clickCount[0] = 0;
                // Creating the AlertDialog object and return it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }

        });*/


        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null) {
            // Get the device address to make BT Connection
            String deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show connection status
            connectionStat.setText("Connecting to " + deviceName);
            buttonConnect.setEnabled(false);

            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceAddress);
            createConnectThread.start();
        }

        /*
        Second most important piece of Code. GUI Handler
         */

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d("TAG", "handleMessage: " + msg.toString());
                if (msg.what == CONNECTING_STATUS) {
                    switch (msg.arg1) {
                        case 1:
                            Log.d("TAG", "handleMessage: Connected");
                            break;
                        case -1:
                            Log.d("TAG", "handleMessage: Failed to connect");
                            break;
                    }
                }
            }
        };
        handler = new Handler(Looper.getMainLooper()) {
            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == CONNECTING_STATUS) {
                    switch (msg.arg1) {
                        case 1:
                            connectionStat.setText("Connected to : " + deviceName);
                            connectionStat.setTextColor(Color.parseColor("#02B34B"));
                            buttonConnect.setEnabled(true);

                            break;
                        case -1:
                            connectionStat.setText("Device fails to connect");
                            connectionStat.setTextColor(Color.parseColor("#A30404"));
                            buttonConnect.setEnabled(true);

                            break;
                    }
                }
            }
        };

        // Select Bluetooth Device
        buttonConnect.setOnClickListener(view -> {
            // Move to adapter list
            Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
            startActivity(intent);
        });

    }


    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        @SuppressLint("MissingPermission")
        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            @SuppressLint("MissingPermission") UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e("TAG", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e("TAG", "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("TAG", "Could not close the client socket", e);
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n') {
                        readMessage = new String(buffer, 0, bytes);
                        Log.e("Arduino Message", readMessage);
                        handler.obtainMessage(MESSAGE_READ, readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error", "Unable to send message", e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException ignored) {
            }
        }
    }



    /* ============================ Terminate Connection at BackPress ====================== */

    @Override
    public void onBackPressed() {

//         Terminate Bluetooth Connection and close app
        if (createConnectThread != null) {
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
}