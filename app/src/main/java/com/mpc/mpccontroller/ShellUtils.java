package com.mpc.mpccontroller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class ShellUtils {

    public static String runAsRoot(String command) {
        Process process = null;
        DataOutputStream os = null;
        BufferedReader reader = null;
        try {
            // Start the root process
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());

            // Write the shell command to execute
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();

            // Get the output from the command
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to finish
            process.waitFor();
            return output.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            try {
                if (os != null) os.close();
                if (reader != null) reader.close();
                if (process != null) process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
