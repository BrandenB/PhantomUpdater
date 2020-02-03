package tv.phantombot.phantomupdater;


/**
 *
 * @author BrandenB
 */
public final class Main {
    // Version to download.
    private static String version = null;
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
    public static void main(final String[] args) {
        if (args.length > 0) {
            // Get the version.
            version = args[0];
            
            // Get the backup location, if any.
            if (args.length > 1) {
                backupLocation = args[1];
            }
            
            // Start the update.
            PhantomUpdater.update(version, backupLocation);
        } else {
            // Print error.
            System.err.println("Failed to update due to missing params.");
        }
        // Shutdown the updater.
        //System.exit(0);
    }
}
