package com.yooha.linkpc;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


// server

public class MainActivity extends AppCompatActivity {
    public static ServerSocket serverSocket = null;
    private static Button mBtnSend;
    private static Button mBtnRead;
    private static EditText mEdtSend;
    private static JSONObject mResponse;
    private static DataInputStream mInputStream;
    private static DataOutputStream mOutputStream;
    private static Socket mSocket;


    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what==0x11) {
                try {
                    mEdtSend.setText(msg.getData().toString());
                    mResponse = new JSONObject(msg.getData().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEdtSend = findViewById(R.id.edt_send);
        mBtnSend = findViewById(R.id.btn_send);
        mBtnRead = findViewById(R.id.btn_read);


        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() { //网络通信不能写在主线程
                        Bundle bundle = new Bundle();
                        bundle.clear();
                        OutputStream output;
                        String str = "通信成功";
                        try {
                            serverSocket = new ServerSocket(30000);
                            mSocket = serverSocket.accept();
                            while (true) {
                                Message msg = new Message();
                                msg.what = 0x11;
                                try {
                                    //mSocket = serverSocket.accept();
                                    output = mSocket.getOutputStream();
                                    output.write(str.getBytes("UTF-8"));
                                    output.flush();
                                    //mSocket.shutdownOutput();

                                    output = mSocket.getOutputStream();
                                    output.write(str.getBytes("UTF-8"));
                                    output.flush();
                                    //mSocket.shutdownOutput();


                                    BufferedReader bff = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                                    String line = null;
                                    String buffer = "";
                                    while ((line = bff.readLine())!=null) {
                                        buffer = line + buffer;
                                    }
                                    bundle.putString("msg", buffer.toString());
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                    bff.close();
                                    output.close();
                                    //mSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    };
                }.start();
            }
        });

        mBtnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        OutputStream output;
                        try {
                            //网络通信不能在主线程中
                            //Toast.makeText(MainActivity.this, "click", Toast.LENGTH_LONG).show();
                            output = mSocket.getOutputStream();
                            //Toast.makeText(MainActivity.this, mSocket.toString(), Toast.LENGTH_LONG).show();
                            output.write("server主动发送".getBytes("UTF-8"));
                            output.flush();
                            //mSocket.shutdownOutput();
                            //Toast.makeText(MainActivity.this, "click", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {

                            Log.d("yoohaa", e.toString());
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }.start();


            }
        });
    }
}

