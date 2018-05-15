/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tv.phantombot.phantomupdater;

import tv.phantombot.phantomupdater.api.GitHubAPI;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.zeroturnaround.zip.ZipUtil;

/**
 *
 * @author ScaniaTV
 */
public final class PhantomUpdater {
    // File name to save the zip as.
    private static final String DOWNLOAD_FILE_NAME = "PhantomBot.zip";
    // Location of where to save the file.
    private static final String DOWNLOAD_LOCATION = "./";
    // Backup folder prefix.
    private static final String BACKUP_FILE_PREFIX = "PhantomBot_Backup_";
    
    /**
     * Method that is used to start the bot update.
     * 
     * @param backupLocation Location to backup the file too.
     */
    public static void update(final String backupLocation) {
        System.out.println("Fetching latest version of PhantomBot...");
        // Get the download URL.
        String downloadUrl = GitHubAPI.getLatestDownloadUrl();
            
        // If the URL is null, try again.
        if (downloadUrl == null) {
            update(backupLocation);
        }
                
        // Try to backup, if it fails, don't update.
        if (backupFiles(".", backupLocation)) {
            // If downloaded, extract the files.
            if (downloadFile(downloadUrl)) {
                System.out.println("Starting update...");
                System.out.println();
                // Unpack the zip.
                unpackDownload(DOWNLOAD_LOCATION + DOWNLOAD_FILE_NAME, DOWNLOAD_LOCATION);
                // Print the success message.
                System.out.println("Successfully updated PhantomBot!");
            } else {
                System.out.println("Failed to backup... Aborting update.");
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
     * Method that unpacks the download.
     * 
     * @param downloadFile Location of where the downloaded file is.
     * @param unpackLocation Location where to unpack everything.
     */
    private static void unpackDownload(final String downloadFile, final String unpackLocation) {
        ZipUtil.unpack(new File(downloadFile), new File(unpackLocation), (String fileName) -> {
            // Extract in the main folder to replace current files.
            fileName = fileName.substring(fileName.indexOf("/"));
            
            // Print what we are extracting.
            System.out.println("Unpacking " + fileName + "...");
            
            // Ignore audio hooks and gif alerts.
            return (fileName.contains("config/audio-hooks") ||
                    fileName.contains("config/gif-alerts") ? null : fileName);
        });
    }
    
    /**
     * Method that backups a file(s).
     * 
     * @param fileLocation File which to be backed up.
     * @param backupLocation  Where to store the backup.
     * @return
     */
    private static boolean backupFiles(final String fileLocation, String backupLocation) {
        if (fileLocation != null) {
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
