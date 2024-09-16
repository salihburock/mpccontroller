package com.mpc.mpccontroller;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {
        public static final int REQUEST_PERMISSION_CODE = 1;
        public TextView mpcInfo;
        public Handler handler = new Handler();
        public int counter = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                setContentView(R.layout.main_activity);
                Button tButton = findViewById(R.id.button);
                Button nButton = findViewById(R.id.button2);
                Button pButton = findViewById(R.id.button3);
                Switch mpdSwitch = findViewById(R.id.switch1);
                mpcInfo = findViewById(R.id.textView2);

                handler.post(updateTextViewTask);

                mpdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                        toTermuxIntent("mpd","--no-daemon");
                                } else {
                                        toTermuxIntent("mpd","--kill");
                                }
                        }
                });



                tButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                toTermuxIntent("mpc","toggle");
                                }
                });

                nButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                toTermuxIntent("mpc","next");

                        }
                });


                pButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                toTermuxIntent("mpc","prev");

                        }
                });


                super.onCreate(savedInstanceState);



        }

        protected void onDestroy() {
                super.onDestroy();
                handler.removeCallbacks(updateTextViewTask);
        }

        public Runnable updateTextViewTask = new Runnable() {



                @Override
                public void run() {
                        counter++;
                        String result;
                        if (hasRootAccess()) {
                                result = ShellUtils.runAsRoot("cat /sdcard/mpc");
                        } else {
                                result = readFromFile("mpc");
                        }
                        //Log.d("TermuxCommunication", result);
                        mpcInfo.setText(result.toString());
                        handler.postDelayed(this, 1000);
                }
        };

        public static String toTermuxIntentAsRoot(String bin, String argument) {
                String strintent;
                String base = "am startservice --user 0 -n com.termux/com.termux.app.RunCommandService \\\n" +
                        "-a com.termux.RUN_COMMAND \\\n" +
                        "--es com.termux.RUN_COMMAND_PATH '/data/data/com.termux/files/usr/bin/%s' \\\n" +
                        "--esa com.termux.RUN_COMMAND_ARGUMENTS '%s' \\\n" +
                        "--es com.termux.RUN_COMMAND_WORKDIR '/data/data/com.termux/files/home' \\\n" +
                        "--ez com.termux.RUN_COMMAND_BACKGROUND 'true' \\\n" +
                        "--es com.termux.RUN_COMMAND_SESSION_ACTION '0'";
                strintent = String.format(base, bin, argument);
                return strintent;
        }
        public void toTermuxIntent(String bin, String argument) {
                Intent intent = new Intent();
                intent.setClassName("com.termux", "com.termux.app.RunCommandService");
                intent.setAction("com.termux.RUN_COMMAND");
                intent.putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/" + bin);
                intent.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", new String[]{argument});
                intent.putExtra("com.termux.RUN_COMMAND_WORKDIR", "/data/data/com.termux/files/home");
                intent.putExtra("com.termux.RUN_COMMAND_BACKGROUND", true);

                startService(intent);

        }
        public static String pathRoot = "/sdcard/";
        public static String readFromFile(String nameFile) {
                String aBuffer = "";
                try {
                        File myFile = new File(pathRoot + nameFile);
                        FileInputStream fIn = new FileInputStream(myFile);
                        BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                        String aDataRow = "";
                        while ((aDataRow = myReader.readLine()) != null) {
                                aBuffer += aDataRow;
                        }
                        myReader.close();
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                Log.d("filereader",aBuffer);
                return aBuffer;
        }

        public boolean hasRootAccess() {
                try {
                        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","cd / && ls"}).getInputStream()).useDelimiter("\\A");
                        return !(s.hasNext() ? s.next() : "").equals("");
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return false;
        }



}

