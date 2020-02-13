package com.example.xo337.try201804;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Set;
import java.util.Timer;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.view.View.GONE;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ScrollingActivity extends AppCompatActivity {
    private MyDBHelper helper;
    private static final String log = "missionReport";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    DatabaseReference database, userDatabase;
    Button tryButton;
    Cursor c;
    TextView tryMission;
    LinearLayout tryLayout, nothingTextBox;
    ArrayAdapter<String> adapter;
    BluetoothAdapter btAdapter;
    BluetoothSocket socket;
    String getBTName;
    ProgressDialog progressDialog;
    Thread getBtConnect;
    private BroadcastReceiver mReceiver;
    String strSet = "0", userName = "", userEmail = "", userPhone = "", userPassword = "";
    int floatSet = Integer.parseInt(strSet), doLoopState, feelBackKey1, feelBackKey2, feelBackKey3,i, j;
    float u1, x1, x2, x3, x1s, x2s, x3s, ax1, ax2, ax3, dx1, dx2, dx3, g1, g2, g3, g4, h1, h2, j1, j2, c1, c2, A, USBKey1, USBKey2, USBKey3;
    InputStream tmpIn = null;
    int[] usbNameID = {};
    String TEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences autoMemory = this.getSharedPreferences("loginInformation", MODE_PRIVATE);
        userName = autoMemory.getString("loginInformationUserName", "");
        userEmail = autoMemory.getString("loginInformationEmail", "");
        userPhone = autoMemory.getString("loginInformationPhone", "");
        userPassword = autoMemory.getString("loginInformationPassword", "");
        if ("".equals(userName) && "".equals(userEmail) && "".equals(userPhone) && "".equals(userPassword.trim())) {
            Intent page = new Intent(this, LoginActivity.class);
            startActivity(page);
        } else {
            checkUserDataSafe();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (socket != null) {
                        socket.close();
                        socket = null;
                        Snackbar.make(view, "已斷開連線", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, "未連線", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //BT connect
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        //base`s Set
        tryMission = findViewById(R.id.tryMission);
        tryLayout = findViewById(R.id.tryLayout);
//        tryButton = findViewById(R.id.tryButton);
//        tryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e(log, userName);
//                tryMission.setText("");
//                try {
//                    //將IvAES、KeyAES、TextAES轉成byte[]型態帶入EncryptAES進行加密，再將回傳值轉成字串
//                    byte[] TextByte = EncryptAES(IvAES.getBytes("UTF-8"), KeyAES.getBytes("UTF-8"), TextAES.getBytes("UTF-8"));
//                    TEXT = Base64.encodeToString(TextByte, Base64.DEFAULT);
//                    tryMission.setText(TEXT+"\n");
//                    //加密字串結果為 : xq/WqrKuXIqLxw1BM4GJoAqPQp6Zh+vqLykVAj2GHFY=
//                } catch (Exception e) {
//                }
//                try {
//                    Log.e(log, decrypt("97a0f43fd456b6f8d43e", "1f7c369c37548c418a34da4edc1627d0da4eef077ab0b005a50f02e0e8500123"));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        database = FirebaseDatabase.getInstance().getReference("USB_list");
        adapter = new ArrayAdapter<String>(ScrollingActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1);
        nothingTextBox = findViewById(R.id.nothingTextBox);
    }

    //    private static final String CipherMode = "AES/ECB/PKCS5Padding";//使用ECB加密，不需要设置IV，但是不安全
//    private static final String CipherMode = "AES/CFB/NoPadding";//使用CFB加密，需要设置IV、
//
//    public static String decrypt(String key, String data) throws Exception {
//        try {
//            byte[] encrypted1 = Base64.decode(data.getBytes(), Base64.DEFAULT);
//            Cipher cipher = Cipher.getInstance(CipherMode);
//            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
//            cipher.init(Cipher.DECRYPT_MODE, keyspec, new IvParameterSpec(
//                    new byte[cipher.getBlockSize()]));
//            byte[] original = cipher.doFinal(encrypted1);
//            return new String(original, "UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
        addDynamicView(userName);
    }

    private void checkUserDataSafe() {
        userDatabase = FirebaseDatabase.getInstance().getReference("User_list");
        Query queryuserDatabase = userDatabase.orderByChild("email").equalTo(userEmail);
        queryuserDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userDataFormat formatData = dataSnapshot.getValue(userDataFormat.class);
                String ReturnPassWord = formatData.getPassword();
                if (!(ReturnPassWord.equals(userPassword))) {
                    Intent page = new Intent(ScrollingActivity.this, LoginActivity.class);
                    startActivity(page);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                userDataFormat formatData = dataSnapshot.getValue(userDataFormat.class);
                String ReturnPassWord = formatData.getPassword();
                if (!(ReturnPassWord.equals(userPassword))) {
                    LogOut();
                    Log.e(log, "password is change");
                    Intent page = new Intent(ScrollingActivity.this, LoginActivity.class);
                    startActivity(page);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addDynamicView(final String str) {
        i = 0;
        j = 0;
//        addFromFirebase(str);
        tryLayout.post(new Runnable() {
            public void run() {
                addFromFirebase(str);
            }
        });
        waitingForSQLite();
    }

    private void addFromFirebase(String str) {
        Log.i(log, "正在透過Firebase動態新增按鈕.." + str);
        Query queryDynamicViewNun = database.orderByChild("userName").equalTo(str);
        queryDynamicViewNun.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final dataFormat data = dataSnapshot.getValue(dataFormat.class);
                String checkOffline = data.getWebVct();
                if (checkOffline.equals("1")) {
                    final Button newButton = new Button(ScrollingActivity.this);
                    final String usbnameStr = data.getUsbName();
//                    newButton.setId(Integer.parseInt(usbnameStr));
                    newButton.setText(usbnameStr);
                    ++i;
                    ++j;
                    Log.i(log, "初始化按鈕..新增第" + i + "個、網路驗證按鈕：" + usbnameStr);
                    newButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (socket != null) {
                                    socket.close();
                                    socket = null;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            showProgressToWait("Connecting...");
                            String[] keyArray = data.getUsbKey().split("/");
                            USBKey1 = Float.valueOf(keyArray[0]);
                            USBKey2 = Float.valueOf(keyArray[1]);
                            USBKey3 = Float.valueOf(keyArray[2]);
                            getMacAddress(usbnameStr);
                        }
                    });
                    newButton.setId(j);
                    nothingTextBox.setVisibility(GONE);
                    tryLayout.addView(newButton);
                }else{

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                dataFormat data = dataSnapshot.getValue(dataFormat.class);
                for (int val = 1; val <= j; val++) {
                    Button reMove = findViewById(val);
                    if (reMove.getText().toString().trim().equals(data.getUsbName())) {
                        reMove.setVisibility(GONE);
                    }
                }
                if (data.getWebVct().equals("0")) {
                    showProgressToWait(data.getUsbName() + "刪除本地USB");
                    helper = MyDBHelper.getInstance(ScrollingActivity.this);
                    c = helper.getReadableDatabase().query("usbDateList", null, null,
                            null, null, null, null);
                    if (c.getCount() > 0) {
                        c.moveToFirst();//移動到第一筆
                        for (int i = 0; i < c.getCount(); i++) {
                            if (c.getString(2).equals(data.getLinkID())) {
                                Log.d(log, String.valueOf(helper.getWritableDatabase().delete("usbDateList", "_id =" + c.getString(0), null) > 0));
                            }
                            c.moveToNext();
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        if (i != 0) {
            nothingTextBox.setVisibility(GONE);
        }
    }

    private void waitingForSQLite(){
        helper = MyDBHelper.getInstance(this);
        c = helper.getReadableDatabase().query("usbDateList", null, null,
                null, null, null, null);
        if (c.getCount() > 0) {
            addFromLocal();
        }
    }

    public void addFromLocal() {
            String st1 = "";
            c.moveToFirst();//移動到第一筆
            for (int i = 0; i < c.getCount(); i++) {
                ++j;
                st1 = ("Id: " + c.getString(0) + " usbName: " + c.getString(1)
                        + " MAC: " + c.getString(2) + " synValue: " + c.getString(3)
                        + " usbKey: " + c.getString(4) + "\n\n");
                final Button newButton = new Button(ScrollingActivity.this);
                newButton.setText(c.getString(1));
                final int num = i;
                newButton.setId(j);
                newButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (socket != null) {
                                socket.close();
                                socket = null;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        showProgressToWait("Connecting...");
                        c.moveToPosition(num);
                        String[] keyArray = c.getString(4).split("/");
                        USBKey1 = Float.valueOf(keyArray[0]);
                        USBKey2 = Float.valueOf(keyArray[1]);
                        USBKey3 = Float.valueOf(keyArray[2]);
                        callBluetoothLink(c.getString(2).toString().trim());
                    }
                });
                tryLayout.addView(newButton);
                //移動到下一筆
                c.moveToNext();
                Log.i(log, st1);
            }
        if (j != 0) {
            nothingTextBox.setVisibility(GONE);
        }
    }

    private void getMacAddress(String usb) {
        Log.i(log, "網路驗證按鈕，選擇" + usb);
        Query query = database.orderByChild("usbName").equalTo(usb);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                dataFormat data = dataSnapshot.getValue(dataFormat.class);
                if (data.getUserName().trim().equals(userName)) {
                    String str = data.getLinkID();
                    Log.i(log, "getFirebaseData：" + str);
                    callBluetoothLink(str);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void callBluetoothLink(String str) {
        Log.i(log, "callBluetooth ：" + str);
        if (str.length() == 17) {
            getBTName = str;
            new Thread(getBluetoothConnect).start();
        } else {
            Toast.makeText(this, "MAC_ADDRESS_ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable getBluetoothConnect = new Runnable() {
        @Override
        public void run() {
            BluetoothDevice device = btAdapter.getRemoteDevice(getBTName);
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                tryLayout.post(new Runnable() {//View.post(Runnable)
                    public void run() {
                        if ("null".equals(socket.toString().trim())) {
//                            Toast.makeText(ScrollingActivity.this, "連接失敗", Toast.LENGTH_SHORT).show();
                            progressDialog.setMessage("Connecting...ERROR");
                        } else {
//                            Toast.makeText(ScrollingActivity.this, "連線成功!!", Toast.LENGTH_SHORT).show();
                            progressDialog.setMessage("Connecting...OK\n" + "Chaos is unlocking...");
                            new Thread(unChaoslock).start();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                tryLayout.post(new Runnable() {
                    public void run() {
                        Toast.makeText(ScrollingActivity.this, "搜尋不到此裝置", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        }
    };

    private void showProgressToWait(String str) {
        progressDialog = new ProgressDialog(ScrollingActivity.this);
        progressDialog.setMessage(str); // Setting Message STYLE_SPINNER
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Spinner
        progressDialog.setMax(100);
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(true);
    }

    //-----------------------unChaoslock------------------------
    //------------------------公式
    public void chaosMath() { //手機端的混沌公式
        g1 = -(ax1 / (ax2 * ax2));
        g2 = (float) 2 * ax1 * dx2 / (ax2 * ax2);
        g3 = (float) -0.1 * ax1 / ax3;
        g4 = (float) (ax1 * (1.76 - (dx2 * dx2) / (ax2 * ax2) + 0.1 * ax1 * dx3 / ax3) + dx1);

        h1 = ax2 / ax1;
        h2 = -(ax2 * dx1) / ax1 + dx2;

        j1 = ax3 / ax2;
        j2 = -(ax3 * dx2) / ax2 + dx3;

        u1 = x2 * x2 * g1 + x2 * g2 + x3 * g3 + x1 * c1 * h1 + x2 * c2 * j1 - x1 * A - x2 * c1 * A - x3 * c2 * A;

        x1s = g1 * x2 * x2 + g2 * x2 + g3 * x3 + g4;
        x2s = h1 * x1 + h2;
        x3s = j1 * x2 + j2;
        x1 = x1s;
        x2 = x2s;
        x3 = x3s;
    }

    // 設置、傳送混沌運算同步控制器參數
    public void sendChaosUs() {
        int sendUm = Float.floatToIntBits(u1);
        Log.i(log, "sendUm = " + sendUm);
        int f1 = Integer.parseInt(strSet), f2 = Integer.parseInt(strSet), f3 = Integer.parseInt(strSet), f4 = Integer.parseInt(strSet);
        byte us[] = new byte[4];
        us[0] = (byte) ((sendUm & 0xff000000) >>> 24);
        f1 = us[0];
        us[1] = (byte) ((sendUm & 0x00ff0000) >>> 16);
        f2 = us[1];
        us[2] = (byte) ((sendUm & 0x0000ff00) >>> 8);
        f3 = us[2];
        us[3] = (byte) ((sendUm & 0x0ff));
        f4 = us[3];
        try {
            OutputStream os = socket.getOutputStream();
            os.write(f1);
            Log.i(log, "f1 = " + us[0]);
            os.write(f2);
            Log.i(log, "f2 = " + us[1]);
            os.write(f3);
            Log.i(log, "f3 = " + us[2]);
            os.write(f4);
            Log.i(log, "f4 = " + us[3]);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //設置、傳送混沌運算參數
    public void setIEEE754First() {
        while (++doLoopState <= 150) {
            chaosMath();
            sendChaosUs();
            Log.d("Password test:", "First: "+USBKey1);
            sendChaosKey(USBKey1);
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                int x = tmpIn.read();
                if (x == 65) {
                    Log.d(log, "First 65");
                    feelBackKey1 = getMCUreturn();
                    tryLayout.post(new Runnable() {
                        public void run() {
                            progressDialog.setMessage("Connecting...OK\n" +
                                    "Chaos is unlocking...\n" +
                                    "渾沌狀態x1 = " + Float.toString(x1s) + "\n" +
                                    "渾沌狀態x2 = " + Float.toString(x2s) + "\n" +
                                    "渾沌狀態x3 = " + Float.toString(x3s) + "\n" +
                                    "--------------------" + "\n" +
                                    "未加密金鑰(1)：" + Float.toString(USBKey1) + "\n" +
                                    "已加密金鑰(1)：" + Float.toString((1 + (x1s * x1s)) * USBKey1) + "\n" +
                                    "MCU回傳解密金鑰(1)：" + Integer.toBinaryString(feelBackKey1));

                            progressDialog.incrementProgressBy(2);
                        }
                    });
                    setIEEE754First();
                } else if (x == 66) {
                    Log.d(log, "First 66");
                    feelBackKey1 = getMCUreturn();
                    tryLayout.post(new Runnable() {
                        public void run() {
                            progressDialog.setMessage("Connecting...OK\n" +
                                    "Chaos is unlocking...\n" +
                                    "渾沌狀態x1 = " + Float.toString(x1s) + "\n" +
                                    "渾沌狀態x2 = " + Float.toString(x2s) + "\n" +
                                    "渾沌狀態x3 = " + Float.toString(x3s) + "\n" +
                                    "--------------------" + "\n" +
                                    "未加密金鑰(1)：" + Float.toString(USBKey1) + "\n" +
                                    "已加密金鑰(1)：" + Float.toString((1 + (x1s * x1s)) * USBKey1) + "\n" +
                                    "MCU回傳解密金鑰(1)：" + Integer.toBinaryString(feelBackKey1) +
                                    "...Key1 OK");
                            progressDialog.setProgress(50);
                        }
                    });
                    setIEEE754Second();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        tryLayout.post(new Runnable() {
            public void run() {
                if (doLoopState++ == 151) {
                    progressDialog.setMessage("Connecting...OK\n" + "Chaos is unlocking...ERROR");
                }
            }
        });
    }

    //設置、傳送混沌運算參數
    public void setIEEE754Second() {
        while (++doLoopState <= 150) {
            chaosMath();
            sendChaosUs();
            Log.d("Password test:", "First: "+USBKey2);
            sendChaosKey(USBKey2);
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                int x = tmpIn.read();
                if (x == 65) {
                    Log.d(log, "Second 65");
                    feelBackKey2 = getMCUreturn();
                    tryLayout.post(new Runnable() {
                        public void run() {
                            progressDialog.setMessage("Connecting...OK\n" +
                                    "Chaos is unlocking...\n" +
                                    "渾沌狀態x1 = " + Float.toString(x1s) + "\n" +
                                    "渾沌狀態x2 = " + Float.toString(x2s) + "\n" +
                                    "渾沌狀態x3 = " + Float.toString(x3s) + "\n" +
                                    "--------------------" + "\n" +
                                    "未加密金鑰(1)：" + Float.toString(USBKey1) + "\n" +
                                    "已加密金鑰(1)：" + Float.toString((1 + (x1s * x1s)) * USBKey1) + "\n" +
                                    "MCU回傳解密金鑰(1)：" + Integer.toBinaryString(feelBackKey1) +
                                    "...Key1 OK\n" +
                                    "--------------------" + "\n" +
                                    "未加密金鑰(2)：" + Float.toString(USBKey2) + "\n" +
                                    "已加密金鑰(2)：" + Float.toString((1 + (x1s * x1s)) * USBKey2) + "\n" +
                                    "MCU回傳解密金鑰(1)：" + Integer.toBinaryString(feelBackKey2));
                        }
                    });
                    setIEEE754Second();
                } else if (x == 67) {
                    Log.d(log, "Second 67");
                    feelBackKey2 = getMCUreturn();
                    tryLayout.post(new Runnable() {
                        public void run() {
                            progressDialog.setMessage("Connecting...OK\n" +
                                    "Chaos is unlocking...\n" +
                                    "渾沌狀態x1 = " + Float.toString(x1s) + "\n" +
                                    "渾沌狀態x2 = " + Float.toString(x2s) + "\n" +
                                    "渾沌狀態x3 = " + Float.toString(x3s) + "\n" +
                                    "--------------------" + "\n" +
                                    "未加密金鑰(1)：" + Float.toString(USBKey1) + "\n" +
                                    "已加密金鑰(1)：" + Float.toString((1 + (x1s * x1s)) * USBKey1) + "\n" +
                                    "MCU回傳解密金鑰(1)：" + Integer.toBinaryString(feelBackKey1) +
                                    "...Key1 OK\n" +
                                    "--------------------" + "\n" +
                                    "未加密金鑰(2)：" + Float.toString(USBKey2) + "\n" +
                                    "已加密金鑰(2)：" + Float.toString((1 + (x1s * x1s)) * USBKey2) + "\n" +
                                    "MCU回傳解密金鑰(1)：" + Integer.toBinaryString(feelBackKey2) +
                                    "...Key2 OK\n");
                            progressDialog.setProgress(75);
                        }
                    });
                    setIEEE754Three();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        tryLayout.post(new Runnable() {
            public void run() {
                if (doLoopState == 151) {
                    progressDialog.setMessage("Connecting...OK\n" + "Chaos is unlocking...ERROR");
                    doLoopState++;
                }
            }
        });
    }

    //設置、傳送混沌運算參數
    public void setIEEE754Three() {
        Log.d("Password test:", "setIEEE754Three");
        while (++doLoopState <= 150) {
            chaosMath();
            sendChaosUs();
            Log.d("Password test:", "Three: "+USBKey3);
            sendChaosKey(USBKey3);
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                int x = tmpIn.read();
                Log.d("Password test:", "x:"+x);
                if (x == 65) {
                    Log.d(log, "Three 65");
                    feelBackKey3 = getMCUreturn();
                    tryLayout.post(new Runnable() {
                        public void run() {
                            progressDialog.setMessage("Connecting...OK\n" +
                                    "Chaos is unlocking...\n" +
                                    "渾沌狀態x1 = " + Float.toString(x1s) + "\n" +
                                    "渾沌狀態x2 = " + Float.toString(x2s) + "\n" +
                                    "渾沌狀態x3 = " + Float.toString(x3s) + "\n" +
                                    "--------------------" + "\n" +
                                    "未加密金鑰(1)：" + Float.toString(USBKey1) + "\n" +
                                    "已加密金鑰(1)：" + Float.toString((1 + (x1s * x1s)) * USBKey1) + "\n" +
                                    "MCU回傳解密金鑰(1)：" + Integer.toBinaryString(feelBackKey1) +
                                    "...Key1 OK\n" +
                                    "--------------------" + "\n" +
                                    "未加密金鑰(2)：" + Float.toString(USBKey2) + "\n" +
                                    "已加密金鑰(2)：" + Float.toString((1 + (x1s * x1s)) * USBKey2) + "\n" +
                                    "MCU回傳解密金鑰(1)：" + Integer.toBinaryString(feelBackKey2) +
                                    "...Key2 OK\n" +
                                    "--------------------" + "\n" +
                                    "未加密金鑰(3)：" + Float.toString(USBKey3) + "\n" +
                                    "已加密金鑰(3)：" + Float.toString((1 + (x1s * x1s)) * USBKey3) + "\n" +
                                    "MCU回傳解密金鑰(3)：" + Integer.toBinaryString(feelBackKey3));
                        }
                    });
                    setIEEE754Three();
                } else if (x == 68) {
                    Log.d(log, "Three 68");
                    feelBackKey3 = getMCUreturn();
                    doLoopState = 301;
                    tryLayout.post(new Runnable() {
                        public void run() {
                            progressDialog.setMessage("Connecting...OK\n" +
                                    "Chaos is unlocking...\n" +
                                    "渾沌狀態x1 = " + Float.toString(x1s) + "\n" +
                                    "渾沌狀態x2 = " + Float.toString(x2s) + "\n" +
                                    "渾沌狀態x3 = " + Float.toString(x3s) + "\n" +
                                    "--------------------" + "\n" +
                                    "未加密金鑰(1)：" + Integer.toBinaryString(Float.floatToIntBits(USBKey1)) + "\n" +
                                    "已加密金鑰(1)：" + Integer.toBinaryString(Float.floatToIntBits((1 + (x1s * x1s)) * USBKey1)) + "\n" +
                                    "MCU回傳解密金鑰(1)：" + Integer.toBinaryString(feelBackKey1) +
                                    "...Key1 OK\n" +
                                    "--------------------" + "\n" +
                                    "未加密金鑰(2)：" + Integer.toBinaryString(Float.floatToIntBits(USBKey2)) + "\n" +
                                    "已加密金鑰(2)：" + Integer.toBinaryString(Float.floatToIntBits((1 + (x1s * x1s)) * USBKey2)) + "\n" +
                                    "MCU回傳解密金鑰(1)：" + Integer.toBinaryString(feelBackKey2) +
                                    "...Key2 OK\n" +
                                    "--------------------" + "\n" +
                                    "未加密金鑰(3)：" + Integer.toBinaryString(Float.floatToIntBits(USBKey3)) + "\n" +
                                    "已加密金鑰(3)：" + Integer.toBinaryString(Float.floatToIntBits((1 + (x1s * x1s)) * USBKey3)) + "\n" +
                                    "MCU回傳解密金鑰(3)：" + Integer.toBinaryString(feelBackKey3) +
                                    "...Key3 OK\n" +
                                    "Chaos is unlocking...OK");
                            progressDialog.setProgress(100);
                        }
                    });
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        tryLayout.post(new Runnable() {
            public void run() {
                if (doLoopState == 151) {
                    progressDialog.setMessage("Connecting...OK\n" + "Chaos is unlocking...ERROR");
                    doLoopState++;
                }
            }
        });
    }

    //傳送第一道IEEE754格式的金鑰
    private void sendChaosKey(float USBKey) {
        float makeChaosKey = (1 + (x1s * x1s)) * USBKey;
        Log.i(log, "chaosKey = " + makeChaosKey);
        int chaosKey = Float.floatToIntBits(makeChaosKey);
        byte key[] = new byte[4];
        key[0] = (byte) ((chaosKey & 0xff000000) >>> 24);
        key[1] = (byte) ((chaosKey & 0x00ff0000) >>> 16);
        key[2] = (byte) ((chaosKey & 0x0000ff00) >>> 8);
        key[3] = (byte) ((chaosKey & 0x000000ff));
        for (int i = 0; i < 4; i++) {
            try {
                OutputStream os = socket.getOutputStream();
                os.write(key[i]);
                Log.i(log, "chaos"+i+1+" = " + key[i]);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //解鎖程式進入點，使用另外一個執行續執行解鎖
    private Runnable unChaoslock = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            doLoopState = 0;
            x1 = (float) 0.5;
            x2 = (float) -0.3;
            x3 = (float) 0.4;
            ax1 = 1;
            ax2 = 1;
            ax3 = 1;
            dx1 = 1;
            dx2 = 1;
            dx3 = 1;
            c1 = (float) -0.5;
            c2 = (float) 0.06;
            A = (float) 0.1;
//            USBKey1 = -12345;
//            USBKey2 = (float) -543.21;
//            USBKey3 = (float) 21.354;
            setIEEE754First();
        }
    };

    public int getMCUreturn() {
        int floatS = Integer.parseInt(strSet);
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            floatS |= ((tmpIn.read() & 0x07)) << 29;
            floatS |= ((tmpIn.read() & 0x07)) << 26;
            floatS |= ((tmpIn.read() & 0x07)) << 23;
            floatS |= ((tmpIn.read() & 0x07)) << 20;
            floatS |= ((tmpIn.read() & 0x07)) << 17;
            floatS |= ((tmpIn.read() & 0x07)) << 14;
            floatS |= ((tmpIn.read() & 0x07)) << 11;
            floatS |= ((tmpIn.read() & 0x07)) << 8;
            floatS |= ((tmpIn.read() & 0x07)) << 5;
            floatS |= ((tmpIn.read() & 0x07)) << 2;
            floatS |= ((tmpIn.read() & 0x03));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return floatS;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
            Log.i(log, "使用者** 登出 **");
            LogOut();
            Intent loginPage = new Intent(this, LoginActivity.class);
            startActivity(loginPage);
        }
        return super.onOptionsItemSelected(item);
    }

    private void LogOut() {
        SharedPreferences autoMemory = getSharedPreferences("loginInformation", MODE_PRIVATE);
        SharedPreferences.Editor editor = autoMemory.edit();
        editor.putString("loginInformationEmail", "");
        editor.putString("loginInformationPhone", "");
        editor.putString("loginInformationUserName", "");
        editor.putString("loginInformationPassword", "");
        editor.commit();

        helper.getWritableDatabase().delete("usbDateList", null, null);
//        Intent page = new Intent(ScrollingActivity.this, LoginActivity.class);
//        startActivity(page);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }

    @Override
    protected void onPause() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onPause();
        finish();
    }
}
