/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.phantombot.phantomupdater.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author ScaniaTV
 */
public class CommandExecutor {
    // Process builder to run command.
    private final ProcessBuilder builder;
    
    /**
     * Class constructor.
     * 
     * @param command 
     */
    public CommandExecutor(String command) {
        builder = new ProcessBuilder(command.split(" "));
        
        // Redirect the error stream to show in our console.
        builder.redirectErrorStream(true);
        // Set our working dir.
        builder.directory(new File(System.getProperty("user.dir")));
    }
    
    /**
     * Method that runs the command.
     */
    public boolean exec() {
        try {
            // Start our process.
            Process process = builder.start();
            // Get the output.
            BufferedReader rd = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // Variable to read line.
            String line = "";
            
            // Read all lines.
            while ((line = rd.readLine()) != null) {
                System.out.println("[PROCESS OUT] " + line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // Command did not run.
            return false;
        }
        
        // Command ran.
        return true;
    }
}
