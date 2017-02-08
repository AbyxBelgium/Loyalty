package com.abyx.loyalty.contents;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.abyx.loyalty.exceptions.MakeDirException;
import com.google.zxing.BarcodeFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class IO {
    private Context context;
    private String filename;

    public IO(Context context){
        this.context = context;
        //Default savefile
        this.filename = "save.ly";
    }

    public IO(Context context, String filename){
        this.context = context;
        this.filename = filename;
    }

    public void save(List<Card> data){
        //Write all the app's data
        String start = getDataToWrite(data);

        try {
            OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            writer.write(start);
            writer.close();
        } catch (IOException e){
            throw new RuntimeException("Something went wrong while saving the data", e);
        }
    }

    public void backup(List<Card> data) throws MakeDirException, FileNotFoundException, IOException{
        //backup the app's data to the SD-card so that it can be restored later (by another device)
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Loyalty/");
        if (!file.exists()){
            if (!file.mkdirs()){
                throw new MakeDirException("Could not create directory to save backup files!");
            }
        }

        file = new File(file.getAbsolutePath() + "/" + filename);
        file.createNewFile();

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write(getDataToWrite(data));
        writer.close();
    }

    private String getDataToWrite(List<Card> data){
        String start = "";
        for (Card content: data){
            start += content.getSaveRepresentation();
            start += "\n";
        }
        return start;
    }

    public ArrayList<Card> load(){
        return restore(context.getFileStreamPath(filename));
    }

    /**
     * Restore the data contained in the given file.
     * @param file The file containing the data to be restored
     * @return List containing all Card-objects contained in the stream
     */
    public ArrayList<Card> restore(File file){
        ArrayList<Card> output = new ArrayList<>();
        if (file != null && file.exists()) {
            //Load all the app's data
            try {
                BufferedReader buffered = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line = buffered.readLine();
                while (line != null) {
                    String[] rawData = line.split("\t");
                    Card temp = new Card(rawData[0], rawData[1], rawData[2], BarcodeFormat.valueOf(rawData[3]));
                    output.add(temp);
                    line = buffered.readLine();
                }
                buffered.close();
            } catch (IOException e) {
                throw new RuntimeException("Something went wrong while reading the file", e);
            }
        }
        return output;
    }

    /**
     * Restore the data contained in the given InputStream.
     * @param stream InputStream containing the data to be restored
     * @return List containing all Card-objects contained in the stream
     */
    public ArrayList<Card> restore(InputStream stream){
        ArrayList<Card> output = new ArrayList<>();
        InputStreamReader is = new InputStreamReader(stream);
        try {
            BufferedReader buffer = new BufferedReader(is);
            String line = buffer.readLine();
            while (line != null){
                String[] rawData = line.split("\t");
                Card temp = new Card(rawData[0], rawData[1], rawData[2], BarcodeFormat.valueOf(rawData[3]));
                output.add(temp);
                line = buffer.readLine();
            }
            buffer.close();
        } catch (IOException e){
            throw new RuntimeException("Something went wrong while reading the file", e);
        }
        return output;
    }

    /**
     * This method saves the given data object to the external storage and prepares it to be send
     * to another device with NFC.
     *
     * @param data Object which needs to be saved to the external storage
     * @return Uri location of the object on the SD-card
     */
    public Uri copyToExternalStorage(Card data) throws MakeDirException, IOException {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Loyalty/Cache/");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new MakeDirException("Could not create directory to save backup files!");
            }
        }

        file = new File(file.getAbsolutePath() + "/" + data.hashCode() + ".lyc");
        file.createNewFile();
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write(data.getSaveRepresentation());
        writer.close();
        file.setReadable(true, false);
        return Uri.parse("file:/" + file.getAbsolutePath());
    }

    public void removeData(String bitmap){
        File file = context.getFileStreamPath(bitmap);
        if (file != null && file.exists()){
            file.delete();
        }
    }

    /**
     * Clear the app's cache folder on the external storage so that we don't waste memory.
     */
    public void clearCache(){
        DeleteRecursive(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Loyalty/Cache/"));
    }

    private void DeleteRecursive(File file){
        if (file.isDirectory())
            for (File child : file.listFiles())
                DeleteRecursive(child);
        file.delete();
    }

    public boolean hasData() {
        return context.getFileStreamPath(this.filename).exists();
    }

    /**
     * Remove the file containing all card data.
     *
     * @return true If deletion was successful.
     */
    public boolean clearData() {
        return context.deleteFile(filename);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

}
