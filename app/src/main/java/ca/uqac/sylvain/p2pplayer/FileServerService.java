package ca.uqac.sylvain.p2pplayer;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by .Sylvain on 25/04/2016.
 */
public class FileServerService extends Service implements Runnable {
    private boolean serviceEnabled;
    private Thread thread;
    private Handler updateConversationHandler;

    public FileServerService() {
        this.serviceEnabled = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.updateConversationHandler = new Handler();
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
            ServerSocket serverSocket = new ServerSocket(); // <-- create an unbound socket first
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(8888)); // <-- now bind it
            Socket client;

            while(serviceEnabled) {
                client = serverSocket.accept();

                try {
                    //String read = input.readLine();
                    String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
                    BufferedWriter output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                    File file = new File(dirPath);
                    if(file.exists()) {
                        if (file.isDirectory()) {
                            FileFilter filter = new FileFilter() {
                                @Override
                                public boolean accept(File pathname) {
                                    if (pathname.isDirectory()) {
                                        return true;
                                    }

                                    return pathname.getName().toLowerCase().endsWith(".mp3");
                                }
                            };
                            File[] files = file.listFiles(filter);
                            Arrays.sort(files);

                            for (int i = 0; i < files.length; i++) {
                                File dirFile = files[i];
                                output.write(dirFile.getName());
                            }
                            client.shutdownOutput();
                            client.shutdownInput();
                        } else {

                        }
                    }
                }
                catch(Exception e) {
                    Log.e("Server", e.getMessage());
                }
            }
        }
        catch(Exception e) {
            Log.e("FileServerService", e.getMessage());
        }
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
