package tv.phantombot.phantomupdater;

import tv.phantombot.phantomupdater.api.GitHubAPI;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import org.zeroturnaround.zip.ZipUtil;

/**
 *
 * @author BrandenB
 */
public final class PhantomUpdater {
    // File name to save the zip as.
    private static final String DOWNLOAD_FILE_NAME = "PhantomBot.zip";
    // Nightly download URL.
    private static final String NIGHTLY_DOWNLOAD_URL = "https://github.com/PhantomBot/nightly-build/blob/master/PhantomBot-nightly.zip?raw=true";
    // Location of where to save the file.
    private static final String DOWNLOAD_LOCATION = "./";
    // Backup folder prefix.
    private static final String BACKUP_FILE_PREFIX = "PhantomBot_Backup_";
    // New files downloaded.
    private static final ArrayList<String> NEW_FILES = new ArrayList<>();
    // Max fails before quit.
    private static final int MAX_FAILS = 10;
    // Failed updates.
    private static int fails = 0;
    // Last fail time.
    private static long lastFail = 0l;
    
    /**
     * Class constructor.
     */
    private PhantomUpdater() {
        
    }
    
    /**
     * Method that checks how many times we failed to update.
     * 
     * @return 
     */
    private static boolean failCheck() {
        if (fails > MAX_FAILS) {
            return false;
        } else {
            fails++;
            
            if (System.currentTimeMillis() > (lastFail + 2000)) {
                try {
                    Thread.sleep((long)2000);
                } catch (InterruptedException ex) {
                    // Ignore.
                    //ex.printStackTrace();
                }
            }
            
            lastFail = System.currentTimeMillis();
        }
        
        return true;
    }
    
    /**
     * Method that is used to start the bot update.
     * 
     * @param version Nightly or Stable.
     * @param backupLocation Location to backup the file to.
     */
    public static void update(final String version, final String backupLocation) {
        System.out.println("Fetching latest version of PhantomBot...");
        String downloadUrl;
        
        // Get the download URL.
        if (version.equalsIgnoreCase("nightly")) {
            downloadUrl = NIGHTLY_DOWNLOAD_URL;
        } else {
            downloadUrl = GitHubAPI.getLatestDownloadUrl();
        }
            
        // If the URL is null, try again.
        if (downloadUrl == null) {
            if (failCheck()) {
                update(version, backupLocation);
            } else {
                System.out.println("Failed to update due to max error attempts.");
            }
            return;
        }
                
        // Try to backup, if it fails, don't update.
        if (backupFiles(".", backupLocation)) {
            // If downloaded, extract the files.
            if (downloadFile(downloadUrl)) {
                System.out.println("Starting update...\n");
                // Unpack the zip.
                unpackDownload(DOWNLOAD_LOCATION + DOWNLOAD_FILE_NAME, DOWNLOAD_LOCATION);
                // Print the success message.
                System.out.println("Successfully updated PhantomBot!");
            } else {
                System.out.println("Failed to download PhantomBot.");
            }
            
            // Delete the zip.
            File zipFile = new File(DOWNLOAD_LOCATION + DOWNLOAD_FILE_NAME);
            if (zipFile.exists()) {
                zipFile.delete();
            }
        } else {
            // Print failed message.
            System.out.println("Failed to update PhantomBot.");
        }
    }
    
    /**
     * Method that downloads a file from a URL.
     * 
     * @param url URL to download from.
     * @return
     */
    private static boolean downloadFile(final String url) {
        // Print info messages.
        System.out.println("Downloading the latest version of PhantomBot...");
        try {
            FileUtils.copyURLToFile(new URL(url), 
                    new File(DOWNLOAD_LOCATION + DOWNLOAD_FILE_NAME));
            return true;
        } catch (IOException ex) {
            System.out.println("Failed to download PhantomBot [IOException] " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * Method that makes sure that we are allowed to remove the file.
     * 
     * @param file
     * @return 
     */
    private static boolean canRemoveFile(File file) {
        String fileName = file.getName();
        String filePath = file.getPath();
        
        return !fileName.endsWith(".txt") && !fileName.contains(".db") &&
               !filePath.contains("config") && !filePath.contains("addons") &&
               !filePath.contains("custom") && !filePath.contains(".zip");
    }
    
    /**
     * Method that removes old files that no longer exist.
     */
    private static void removeOldFiles() {
        Collection<File> files = FileUtils.listFiles(new File("."), null, true);
        
        System.out.println("Removing old files...");
        
        files.forEach((file) -> {
            String fileName = file.getPath().substring(1).replaceAll("\\\\", "/");
        
            if (canRemoveFile(file)) {
                if (!NEW_FILES.contains(fileName)) {
                    file.delete();
                    System.out.println("Remove old file " + fileName);
                }
            }
        });
    }
    
    /**
     * Method that unpacks the download.
     * 
     * @param downloadFile Location of where the downloaded file is.
     * @param unpackLocation Location where to unpack everything.
     */
    private static void unpackDownload(final String downloadFile, final String unpackLocation) {
        ZipUtil.unpack(new File(downloadFile), new File(unpackLocation), (String fileName) -> {
            // Extract in the main folder to replace current files.
            fileName = fileName.substring(fileName.indexOf("/"));
            
            // Save the file for later.
            NEW_FILES.add(fileName);
            
            // Print what we are extracting.
            System.out.println("Unpacking " + fileName + "...");
            
            // Ignore audio hooks and gif alerts.
            return (fileName.contains("config/audio-hooks") ||
                    fileName.contains("config/gif-alerts") ? null : fileName);
        });
        
        // Remove all old files.
        removeOldFiles();
    }
    
    /**
     * Method that backups a file(s).
     * 
     * @param fileLocation File which to be backed up.
     * @param backupLocation  Where to store the backup.
     * @return
     */
    private static boolean backupFiles(final String fileLocation, String backupLocation) {
        if (fileLocation != null && backupLocation != null) {
            System.out.println("Creating a backup of PhantomBot...");
            
            // Replace all \ with /
            backupLocation = backupLocation.replaceAll("\\\\", "/");

            // Make sure the location exists.
            if (!new File(backupLocation).isDirectory()) {
                System.out.println("Failed to backup PhantomBot since the backup location doesn't exist...");
                return false;
            }
            
            // Add extra path location, if needed.
            if (!backupLocation.endsWith("/")) {
                backupLocation += "/";
            }
            
            // Add the file name.
            backupLocation += (BACKUP_FILE_PREFIX + System.currentTimeMillis() + ".zip");
            
            // Backup the files.
            ZipUtil.pack(new File(fileLocation), new File(backupLocation));
            
            System.out.println("Successfully backed up PhantomBot!");
        }
        return true;
    }
}
