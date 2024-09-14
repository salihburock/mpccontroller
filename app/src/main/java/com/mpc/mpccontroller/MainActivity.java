package com.mpc.mpccontroller;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.   view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;



public class MainActivity extends AppCompatActivity {


        public TextView mpcInfo;
        public Handler handler = new Handler();
        public int counter = 0;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                setContentView(R.layout.main_activity);

                Button tButton = (Button)findViewById(R.id.button);
                Button nButton = (Button)findViewById(R.id.button2);
                Button pButton = (Button)findViewById(R.id.button3);
                mpcInfo = findViewById(R.id.textView2);

                handler.post(updateTextViewTask);





                tButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                String result = ShellUtils.runAsRoot(toTermuxIntent("mpc","toggle"));

                        }
                });

                nButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                String result = ShellUtils.runAsRoot(toTermuxIntent("mpc","next"));

                        }
                });


                pButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                String result = ShellUtils.runAsRoot(toTermuxIntent("mpc","prev"));

                        }
                });


                super.onCreate(savedInstanceState);
                try {
                        // Replace "your-command" with the shell command you want to run
                        Process process = Runtime.getRuntime().exec("input keyevent KEYCODE_SLEEP");
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));
                        int read;
                        char[] buffer = new char[4096];
                        StringBuffer output = new StringBuffer();
                        while ((read = reader.read(buffer)) > 0) {
                                output.append(buffer, 0, read);
                        }
                        reader.close();
                        process.waitFor();
                        // You can log or handle the output here
                } catch (Exception e) {
                        e.printStackTrace();
                }
                EdgeToEdge.enable(this);



                // Register receiver dynamically


        }

        protected void onDestroy() {
                super.onDestroy();
                handler.removeCallbacks(updateTextViewTask);
        }

        public Runnable updateTextViewTask = new Runnable() {

                @Override
                public void run() {
                        counter++;
                        String result = ShellUtils.runAsRoot("cat /sdcard/mpc");
                        Log.d("TermuxCommunication", result);
                        mpcInfo.setText(result.toString());
                        handler.postDelayed(this, 1000);
                }
        };

        public static String toTermuxIntent(String bin, String argument) {
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



}

