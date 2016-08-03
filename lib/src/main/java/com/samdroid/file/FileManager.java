package com.samdroid.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.samdroid.common.MyLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A static class used for all main file operations
 *
 * @author Sam Koch
 */
public class FileManager {

    public static final String tempFileName = "tempFile";

    private static final String TAG = "FileManager",
            directory = "ANDi";

    public static File getNewFile(Context context, String name) {
        File file = null;

        if (context == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), name);
            }

        } else {
            file = new File(context.getFilesDir(), name);
        }

        return file;
    }

    public static void clearDirectory(Context context, String name) {
        File file = null;

        if (context == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), name);
            }

        } else {
            file = new File(context.getFilesDir(), name);
        }

        if (file != null) file.delete();
    }


    /**
     * Send a broadcast to scan the device's storage media
     * to ensure the files appear in the directory when reading back
     *
     * @param directory directory to scan in
     */
    public static void callMediaScanner(Context context, File file) {
        if (context != null) {
            MediaScannerConnection.scanFile(context,
                    new String[]{file.toString()},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {

                        /**
                         * Once scan complete, log its completion
                         */
                        public void onScanCompleted(String path, Uri uri) {
                        }

                    });
        }
    }

    /**
     * Delete all the application files and all files in all sub-directories
     *
     * @param fileOrDirectory Root directory to delete from
     */
    public static void clearDirectory() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                deleteRecursive(getStorageDirectory());
            }

        }).run();
    }

    /**
     * Delete all files directory and all files in all sub-directories
     *
     * @param fileOrDirectory Root directory to delete from
     */
    private static void deleteRecursive(final File fileOrDirectory) {
        if (fileOrDirectory == null) return;

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                child.delete();
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    /**
     * Copy a file from src to dst
     *
     * @param src source file with contents to copy
     * @param dst destination file
     * @return if the copy was successful
     * @throws IOException
     */
    public static boolean copy(Context context, File src, File dst) throws IOException {
        if (src == null || dst == null) {
            return false;
        }

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // transfer bytes from in to out
        byte[] buf = new byte[(int) src.length()];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();

        // get the media scanner to scan the file space to discover the new file
        callMediaScanner(context, dst);
        return true;
    }

    /**
     * @return a File object with the directory of where to save local files for this app
     */
    public static File getStorageDirectory() {
        File dir = new File(Environment.getExternalStorageDirectory(), directory);

        // create the storage directory if it does not exist
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                MyLog.e(TAG, "Failed to create directory: " + Environment.getExternalStorageDirectory() + "/" + directory);
                return null;
            }
        }

        return dir;
    }

    /**
     * @return the list of file names of the storage images
     */
    public static List<File> getSavedFiles() {
        File mediaStorage = getStorageDirectory();

        // if unable to get the storage directory
        if (mediaStorage == null) return new ArrayList<File>(0);

        // else get the list of files and copy them into an array list
        File[] files = mediaStorage.listFiles();

        List<File> returnFiles = new ArrayList<File>(0);

        // add each file to the returned array list
        for (int i = 0; i < files.length; i++) {
            returnFiles.add(files[i]);
        }

        return returnFiles;
    }

    /**
     * Write clean to a file
     *
     * @param file File to write to
     * @param data text to write to the file from clean,
     *             array list with line by line text
     */
    public static void writeToFile(Context context, File file, ArrayList<String> data) {
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            for (int i = 0; i < data.size(); i++) {
                if (data.get(i) != null) {
                    bw.write(data.get(i) + "\n");
                } else {
                    bw.write("\n");
                }
            }

            bw.close();
            fw.close();
        } catch (IOException e) {
            MyLog.e("Exception", "File write failed: " + e.getLocalizedMessage());
            MyLog.printStackTrace(e);
        }

        callMediaScanner(context, file);
    }

    /**
     * Append text to a file
     *
     * @param file File to append to
     * @param data text to append on a new line
     */
    public static void appendToFile(Context context, File file, String data) {
        ArrayList<String> contents = new ArrayList<String>(0);
        contents.add(data);
        appendToFile(context, file, contents);
    }

    /**
     * Append text to a file
     *
     * @param file File to append to
     * @param data text to append, each item on a new line
     */
    public static void appendToFile(Context context, File file, ArrayList<String> data) {
        ArrayList<String> contents = readFromFile(file);

        for (int i = 0; i < data.size(); i++) {
            contents.add(data.get(i));
        }

        writeToFile(context, file, contents);

        callMediaScanner(context, file);
    }

    /**
     * Write clean to a file
     *
     * @param file File to write to
     * @param data text to write to the file from clean
     */
    public static void writeToFile(Context context, File file, String data) {
        ArrayList<String> contents = new ArrayList<String>();
        contents.add(data);
        writeToFile(context, file, contents);
    }

    /**
     * Read from a file and return the text line by line in an array list
     *
     * @param file File to read
     * @return array list with each line of the file contents
     */
    public static ArrayList<String> readFromFile(File file) {
        ArrayList<String> fileContents = new ArrayList<String>(0);

        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String nextLine = "";

            while ((nextLine = br.readLine()) != null) {
                fileContents.add(nextLine);
            }

            br.close();
            fr.close();

        } catch (FileNotFoundException e) {
            MyLog.e(TAG, "File not found: " + e.getLocalizedMessage());

        } catch (IOException e) {
            MyLog.e(TAG, "Can not read file: " + e.getLocalizedMessage());
        }

        return fileContents;
    }

    /**
     * Read and return the first line from a file
     *
     * @param file File to read from
     * @return a String with the first line
     */
    public static String readFromFileFirstLine(File file) {
        ArrayList<String> lines = readFromFile(file);

        if (lines.size() > 0) return lines.get(0);

        return "";
    }

    /**
     * Delete a file
     *
     * @param fileUri the URL of the file to delete
     * @return if the file was deleted
     */
    public static boolean deleteFile(String fileUri) {
        File file = new File(fileUri);
        return file.delete();
    }

    /**
     * Delete a file
     *
     * @param file
     * @return if the file was deleted
     */
    public static boolean deleteFile(File file) {
        return file != null && file.delete();
    }

    /**
     * Save a bitmap to a file
     *
     * @param file   file to write to
     * @param bitmap the bitmap to write
     * @return boolean if it was saved successfully
     */
    public static boolean writeBitmapToFile(Context context, File file, Bitmap bitmap) {
        boolean ok = false;

        try {

            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            ok = true;

        } catch (Exception e) {
            MyLog.e(TAG, "Save file error: " + e.getLocalizedMessage());
        }

        callMediaScanner(context, file);
        return ok;
    }

    /**
     * Cleans the content of a file (without deleting it)
     *
     * @param file
     */
    public static void writeClean(Context context, File file) {
        writeToFile(context, file, "");
    }

    /**
     * Read a file from the path
     *
     * @param path
     * @return the contents of the file in a string
     * @throws IOException
     */
    public static String read(String path) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        output.append(reader.readLine());
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            output.append('\n').append(line);
        }
        reader.close();
        return output.toString();
    }
}
