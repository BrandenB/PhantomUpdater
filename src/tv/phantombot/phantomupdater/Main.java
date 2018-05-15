/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.phantombot.phantomupdater;

import tv.phantombot.phantomupdater.types.LaunchType.Type;

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
                case "shell":
                    launchType = Type.SHELL;
                    break;
                case "batch":
                    launchType = Type.BATCH;
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
            // Start PhantomBot again.
        } else {
            // Print error.
            System.err.println("Failed to get launch type.");
            // Shutdown the updater.
            System.exit(0);
        }
    }
}
