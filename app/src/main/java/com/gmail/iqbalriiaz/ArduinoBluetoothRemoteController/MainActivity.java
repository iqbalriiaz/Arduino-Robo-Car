package com.gmail.iqbalriiaz.ArduinoBluetoothRemoteController;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public String check="";


    public static final String TAG = "MainActivity";

    private String deviceName = null;
    private String deviceAddress;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;

    private final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message
    private boolean Pressed;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        final Button fButton = findViewById(R.id.fButtonId);
        final Button bButton = findViewById(R.id.bButtonId);
        final Button lButton = findViewById(R.id.lButtonId);
        final Button rButton = findViewById(R.id.rButtonId);
        final TextView connectionStat = findViewById(R.id.connectionStatId);
        final ImageButton buttonConnect = findViewById(R.id.buttonConnect);


        fButton.setOnClickListener(v -> {
            try {
                connectedThread.write("F");
                Log.d("TAG", "Connected:F button is pressed");
            } catch (Exception e) {
                Log.d("TAG", "Not Connected:F button is pressed");
                e.printStackTrace();
            }
        });

        bButton.setOnClickListener(v -> {
            try {
                connectedThread.write("B");
                Log.d("TAG", "Connected:B button is pressed");

            } catch (Exception e) {
                Log.d("TAG", "Not Connected:B button is pressed");
                e.printStackTrace();
            }
        });

        lButton.setOnClickListener(v -> {
            try {
                connectedThread.write("L");
                Log.d("TAG", "Connected:L button is pressed");
            } catch (Exception e) {
                Log.d("TAG", "Not Connected:L button is pressed");
                e.printStackTrace();
            }
        });

        rButton.setOnClickListener(v -> {
            try {
                connectedThread.write("R");
                Log.d("TAG", "Connected:R button is pressed");
            } catch (Exception e) {
                Log.d("TAG", "Not Connected:R button is pressed");
                e.printStackTrace();
            }
        });



//            fButton.setOnTouchListener((v, event) -> {
//                try {
//                    connectedThread.write("F");
//                    Log.d("TAG", "Connected:F button is pressed");
//                } catch (Exception e) {
//                    Log.d("TAG", "Not Connected:F button is pressed");
//                    e.printStackTrace();
//                }
//                        if (event.getAction() == MotionEvent.ACTION_UP) {
//                            Pressed = false;
//                            try {
//                                connectedThread.write("S");
//                                Log.d("TAG", "Connected:S value passing");
//                            } catch (Exception e) {
//                                Log.d("TAG", "Not Connected:S value passing");
//                                e.printStackTrace();
//                            }
//                        }
//                return false;
//            });
//
//
//
//
//        bButton.setOnTouchListener((v, event) -> {
//            try {
//                connectedThread.write("B");
//                Log.d("TAG", "Connected:B button is pressed");
//
//            } catch (Exception e) {
//                Log.d("TAG", "Not Connected:B button is pressed");
//                e.printStackTrace();
//            }
//
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                Pressed = false;
//                try {
//                    connectedThread.write("S");
//                    Log.d("TAG", "Connected:S value passing");
//                } catch (Exception e) {
//                    Log.d("TAG", "Not Connected:S value passing");
//                    e.printStackTrace();
//                }
//            }
//
//            return false;
//        });
//
//
//        lButton.setOnTouchListener((v, event) -> {
//            try {
//                connectedThread.write("L");
//                Log.d("TAG", "Connected:L button is pressed");
//            } catch (Exception e) {
//                Log.d("TAG", "Not Connected:L button is pressed");
//                e.printStackTrace();
//            }
//
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                Pressed = false;
//                try {
//                    connectedThread.write("S");
//                    Log.d("TAG", "Connected:S value passing");
//                } catch (Exception e) {
//                    Log.d("TAG", "Not Connected:S value passing");
//                    e.printStackTrace();
//                }
//            }
//            return false;
//        });
//
//        rButton.setOnTouchListener((v, event) -> {
//            try {
//                connectedThread.write("R");
//                Log.d("TAG", "Connected:R button is pressed");
//            } catch (Exception e) {
//                Log.d("TAG", "Not Connected:R button is pressed");
//                e.printStackTrace();
//            }
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                Pressed = false;
//                try {
//                    connectedThread.write("S");
//                    Log.d("TAG", "Connected:S value passing");
//                } catch (Exception e) {
//                    Log.d("TAG", "Not Connected:S value passing");
//                    e.printStackTrace();
//                }
//            }
//            return false;
//        });

        // If a bluetooth device has been selected from SelectDeviceActivity
        deviceName = getIntent().getStringExtra("deviceName");
        if (deviceName != null){
            // Get the device address to make BT Connection
            deviceAddress = getIntent().getStringExtra("deviceAddress");
            // Show connection status
            connectionStat.setText("Connecting to " + deviceName + "...");
            buttonConnect.setEnabled(false);

            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();
        }

        /*
        Second most important piece of Code. GUI Handler
         */

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d("TAG", "handleMessage: " + msg.toString());
                switch (msg.what) {
                    case CONNECTING_STATUS:
                        switch (msg.arg1) {
                            case 1:
                                Log.d("TAG", "handleMessage: Connected");
                                break;
                            case -1:
                                Log.d("TAG", "handleMessage: Failed to connect");
                                break;
                        }
                        break;
                }
            }
        };
        handler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                connectionStat.setText("Connected to " + deviceName);
                                connectionStat.setTextColor(Color.GREEN);
                                buttonConnect.setEnabled(true);

                                break;
                            case -1:
                                connectionStat.setText("Device fails to connect");
                                connectionStat.setTextColor(Color.RED);
                                buttonConnect.setEnabled(true);

                                break;
                        }
                        break;


//                   case MESSAGE_READ:
//                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
//                        switch (arduinoMsg.toLowerCase()){
//                            case "led is turned on":
//                                imageView.setBackgroundColor(getResources().getColor(R.color.colorOn));
//                                textViewInfo.setText("Arduino Message : " + arduinoMsg);
//                                break;
//                            case "led is turned off":
//                                imageView.setBackgroundColor(getResources().getColor(R.color.colorOff));
//                                textViewInfo.setText("Arduino Message : " + arduinoMsg);
//                                break;
//                        }
//                        break;
                }
            }
        };

        // Select Bluetooth Device
        buttonConnect.setOnClickListener(view -> {
            // Move to adapter list
            Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
            startActivity(intent);
        });

        // Button to ON/OFF LED on Arduino Board
//        aButton.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onClick(View view) {
//                String cmdText = null;
//                String btnState = aButton.getText().toString().toLowerCase();
//                System.out.println(btnState);
//                if ("a is pressed".equals(btnState)) {
//                    aButton.setText("A Button Pressed");
//                    // Command to Pass A Button value on Arduino. Must match with the command in Arduino code
//                    cmdText = "<a is pressed>";
//                    //    case "turn off":
////                        buttonToggle.setText("Turn On");
////                        // Command to turn off LED on Arduino. Must match with the command in Arduino code
////                        cmdText = "<turn off>";
////                        break;
//                }
//                // Send command to Arduino board
//                connectedThread.write(cmdText);
//            }
//        });
    }


    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

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
            } catch (IOException ignored) { }

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
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.e("Arduino Message",readMessage);
                        handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
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
                Log.e("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException ignored) { }
        }
    }

    //Arduino Command
    public void intentAction(String intent_action) throws InterruptedException {
        if (intent_action == null || TextUtils.isEmpty(intent_action)) {
            return;
        }
        switch (intent_action) {
            case "F":
                connectedThread.write("F");
                check="b";
                break;
            case "B":
                connectedThread.write("B");
                check="b";
                break;
            case "L":
                connectedThread.write("L");
                check="b";
                break;
            case "R":
                connectedThread.write("R");
                check="b";
                break;

        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */
    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}