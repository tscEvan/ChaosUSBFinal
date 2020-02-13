package com.example.xo337.try201804;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity {
    Handler handler;
    Runnable runnable;
    private AutoCompleteTextView editEmail;
    private EditText editPassword;
    private View mProgressView, mLoginFormView;
    private String dataTableEmail, dataTablePassword, dataTableUser, dataTableUsb, passWordData, emailData;
    private MyDBHelper helper;
    FirebaseDatabase firebaseConnect;
    DatabaseReference firebaseData;
    SharedPreferences autoMemory;
    SharedPreferences.Editor editor;
    int i;
    Query queryDynamicViewNun;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        editEmail = (AutoCompleteTextView) findViewById(R.id.intputData_email);
        editPassword = (EditText) findViewById(R.id.intputData_password);

        mLoginFormView = findViewById(R.id.Layout_login);
        mProgressView = findViewById(R.id.login_progress);

        helper = MyDBHelper.getInstance(LoginActivity.this);

        helper.getWritableDatabase().delete("usbDateList", null, null);

        autoMemory = getSharedPreferences("loginInformation", MODE_PRIVATE);
        editor = autoMemory.edit();
        editor.putString("loginInformationEmail", "");
        editor.putString("loginInformationPhone", "");
        editor.putString("loginInformationUserName", "");
        editor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void loginButton(View view) {
        boolean cancel = false;
        View focusView = null;
        emailData = editEmail.getText().toString().trim();
        passWordData = editPassword.getText().toString().trim();
        if ("".equals(passWordData)) {
            editPassword.setError("Can't be empty");
            focusView = editPassword;
            cancel = true;
        } else {
            if (passWordData.indexOf('~') != -1 || passWordData.indexOf('!') != -1 || passWordData.indexOf('@') != -1
                    || passWordData.indexOf('#') != -1 || passWordData.indexOf('$') != -1 || passWordData.indexOf('%') != -1
                    || passWordData.indexOf('^') != -1 || passWordData.indexOf('&') != -1 || passWordData.indexOf('*') != -1
                    || passWordData.indexOf('(') != -1 || passWordData.indexOf(')') != -1 || passWordData.indexOf('_') != -1
                    || passWordData.indexOf('+') != -1 || passWordData.indexOf('-') != -1 || passWordData.indexOf('`') != -1
                    || passWordData.indexOf('ˇ') != -1 || passWordData.indexOf('ˋ') != -1 || passWordData.indexOf('ˊ') != -1
                    || passWordData.indexOf('˙') != -1 || passWordData.indexOf('<') != -1 || passWordData.indexOf('>') != -1
                    || passWordData.indexOf('=') != -1 || passWordData.indexOf('.') != -1 || passWordData.indexOf(',') != -1) {
                editPassword.setError("Con't be !、&、$ or like this more");
                focusView = editPassword;
                cancel = true;
            }
        }
        if ("".equals(emailData)) {
            editEmail.setError("Can't be empty");
            focusView = editEmail;
            cancel = true;
        } else {
            if (emailData.indexOf('@') == -1 || emailData.indexOf('.') == -1 || emailData.indexOf('/') != -1
                    || emailData.indexOf('|') != -1 || emailData.indexOf('%') != -1 || emailData.indexOf('#') != -1
                    || emailData.indexOf('$') != -1 || emailData.indexOf('&') != -1 || emailData.indexOf('*') != -1
                    || emailData.indexOf('!') != -1 || emailData.indexOf('~') != -1 || emailData.indexOf('(') != -1
                    || emailData.indexOf(')') != -1 || emailData.indexOf('^') != -1 || emailData.indexOf('+') != -1
                    || emailData.indexOf('-') != -1 || emailData.indexOf('=') != -1 || emailData.indexOf(';') != -1
                    || emailData.indexOf(':') != -1 || emailData.indexOf('\"') != -1 || emailData.indexOf('\'') != -1
                    || emailData.indexOf('\\') != -1 || emailData.indexOf('<') != -1 || emailData.indexOf('>') != -1
                    || emailData.indexOf('{') != -1 || emailData.indexOf('}') != -1 || emailData.indexOf(',') != -1) {
                editEmail.setError("Wrong format");
                focusView = editEmail;
                cancel = true;
            }
            if (emailData.length() <= 6) {
                editEmail.setError("Input too short");
                focusView = editEmail;
                cancel = true;
            }
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            connectFirebase();
        }
    }

    public void connectFirebase() {
        i = 0;
        firebaseConnect = FirebaseDatabase.getInstance();
        firebaseData = firebaseConnect.getReference("User_list");
        Query query = firebaseData.orderByChild("email").equalTo(emailData);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final userDataFormat userDataFormat = dataSnapshot.getValue(userDataFormat.class);
                String ReturnPassWord = userDataFormat.getPassword();
                if (ReturnPassWord.equals(passWordData)) {
                    SharedPreferences autoMemory = getSharedPreferences("loginInformation", MODE_PRIVATE);
                    SharedPreferences.Editor editor = autoMemory.edit();
                    editor.putString("loginInformationEmail", emailData);
                    editor.putString("loginInformationPhone", userDataFormat.getPhone());
                    editor.putString("loginInformationUserName", userDataFormat.getUserName());
                    editor.putString("loginInformationPassword", passWordData);
                    editor.commit();
                    queryDynamicViewNun = firebaseConnect.getReference("USB_list").orderByChild("userName").equalTo(userDataFormat.getUserName());
                    getUSBData();
                    Intent page = new Intent(LoginActivity.this, ScrollingActivity.class);
                    startActivity(page);
                } else{
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();
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

    private void getUSBData() {
//        handler = new Handler();
//        runnable = new Runnable() {
//            boolean isGetData = false;
//            @Override
//            public void run() {
                // TODO Auto-generated method stub
                //要做的事情
                queryDynamicViewNun.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final dataFormat data = dataSnapshot.getValue(dataFormat.class);
                        final String usbnameStr = data.getUsbName();
                        String checkOffline = data.getWebVct();
                        if (checkOffline.equals("0")) {
                            ++i;
                            Log.i("初始化設備資訊", "取得離線設備第" + i + "個、離線按鈕：" + usbnameStr);
                            ContentValues values = new ContentValues();
                            values.put("usbName", usbnameStr);
                            values.put("linkID", data.getLinkID());
                            values.put("synValue", data.getSynValue());
                            values.put("usbKey", data.getUsbKey());
                            long id = helper.getWritableDatabase().insert("usbDateList", null, values);
                            Log.i("SQLiteThings", "資料庫資料數量共：" + id + "筆");
//                            isGetData = true;
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
//                if (isGetData) {
//                    handler.removeCallbacks(runnable);
//                }else {
//                    handler.postDelayed(this, 200);
//                }
//            }
//        };
//        handler.postDelayed(runnable, 200);//每两秒执行一次runnable.
}
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
