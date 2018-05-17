/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.phantombot.phantomupdater;

import tv.phantombot.phantomupdater.util.CommandExecutor;
import tv.phantombot.phantomupdater.util.LaunchTypes.Type;

/**
 *
 * @author ScaniaTV
 */
public final class Main {
    // Type of way to launch the bot.
    private static Type launchType;
    // Location where to store the backup.
    private static String backupLocation = null;
    
    /**
     * Class constructor.
     */
    private Main() {
        
    }
    
    /**
     * Main method where everything starts.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            // Get the launch type.
            // This is used once the update is done and we launch the bot again.
            switch (args[0]) {
                case "service":
                    launchType = Type.SERVICE;
                    break;
                case "batch":
                    launchType = Type.BATCH;
                    break;
                case "manual":
                    launchType = Type.MANUAL;
                    break;
                default:
                    // Return main without any arguments.
                    main(new String[0]);
            }
                
            // Get the backup location, if any.
            if (args.length > 1) {
                backupLocation = args[1];
            }
            
            // Start the update.
            PhantomUpdater.update(backupLocation);
            
            // Our command exec.
            CommandExecutor ce = null;
            // Start PhantomBot again.
            switch (launchType) {
                case BATCH:
                    ce = new CommandExecutor("cmd /c start launch.bat");
                    break;
                case SHELL:
                    ce = new CommandExecutor("sudo ./launch.sh");
                    break;
                case SERVICE:
                    ce = new CommandExecutor("sudo systemctl start phantombot");
                    break;
            }
            
            // This should only null if it's a manual update.
            if (ce != null) {
                // Run the command.
                if (ce.exec()) {
                    System.out.println("Launching PhantomBot!");
                } else {
                    System.out.println("Failed to launch PhantomBot...");
                }
            }
        } else {
            // Print error.
            System.err.println("Failed to get launch type.");
        }
        // Shutdown the updater.
        System.exit(0);
    }
}
