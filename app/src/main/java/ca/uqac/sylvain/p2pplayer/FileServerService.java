package ca.uqac.sylvain.p2pplayer;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by .Sylvain on 25/04/2016.
 */
public class FileServerService extends Service implements Runnable {
    private boolean serviceEnabled;
    private Thread thread;

    public FileServerService() {
        this.serviceEnabled = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceEnabled = true;
        thread = new Thread(this);
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = null;

            while(serviceEnabled) {
                client = serverSocket.accept();
                String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String line;
                while((line = reader.readLine()) != null) {
                    dirPath += line;
                }

                File file = new File(dirPath);
                if(file.exists()) {
                    if(file.isDirectory()) {
                        FileFilter filter = new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                if(pathname.isDirectory()) {
                                    return true;
                                }

                                return pathname.getName().toLowerCase().endsWith(".mp3");
                            }
                        };
                        File[] files = file.listFiles(filter);
                        Arrays.sort(files);
                        OutputStream os = client.getOutputStream();
                        PrintWriter pw = new PrintWriter(os);
                        for(int i = 0; i < files.length; i++) {
                            File dirFile = files[i];
                            pw.write(file.getPath().replace(dirPath, ""));
                        }
                        pw.close();
                    }
                    else {

                    }
                }

                client.close();
            }
        }
        catch(Exception e) {}
    }

    @Override
    public void onDestroy() {
        serviceEnabled = false;
        thread.interrupt();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
