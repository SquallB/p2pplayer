package ca.uqac.sylvain.p2pplayer;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by .Sylvain on 25/04/2016.
 */
public class GetRemoteFilesAsyncTask extends AsyncTask<String, Void, String> {
    private MainActivity activity;
    private InetAddress address;

    public GetRemoteFilesAsyncTask(MainActivity activity, InetAddress address) {
        this.activity = activity;
        this.address = address;
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            SocketAddress sockaddr = new InetSocketAddress(address, 8888);
            Socket socket = new Socket();
            socket.connect(sockaddr, 5000); //10 second connection timeout
            if (socket.isConnected()) {
                Log.e("AsyncTask", "Connected");
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while((line = reader.readLine()) != null) {
                    Log.i("AsyncTask", line);
                }
                reader.close();
            }
            else {
                Log.e("AsyncTask", "Not connected");
            }
            socket.close();
        }
        catch (Exception e) {
            Log.e("AsyncTask", e.getMessage());
        }

        return null;
    }
}
