package ca.uqac.sylvain.p2pplayer;

import android.os.AsyncTask;
import android.widget.Toast;

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
                Toast.makeText(activity, "Socket connected", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(activity, "Not connected", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(activity, "Exception in task", Toast.LENGTH_LONG).show();
        }

        return null;
    }
}
