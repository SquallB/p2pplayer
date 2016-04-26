package ca.uqac.sylvain.p2pplayer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class GetRemoteFilesAsyncTask extends AsyncTask<String, Void, String> {
    private MainActivity activity;
    private InetAddress address;

    public GetRemoteFilesAsyncTask(MainActivity activity, InetAddress address) {
        this.activity = activity;
        this.address = address;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";

        try {
            SocketAddress sockaddr = new InetSocketAddress(address, 8888);
            Socket socket = new Socket();
            socket.setReuseAddress(true);
            socket.bind(null);
            socket.connect(sockaddr, 5000); //10 second connection timeout
            if (socket.isConnected()) {
                Log.e("AsyncTask C", "Connected");
                Thread.sleep(1000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();
                if(line != null) {
                    Log.e("AsyncTask", line);
                    result += line;
                }
            }
            else {
                Log.e("AsyncTask C", "Not connected");
            }
        }
        catch (Exception e) {
            Log.e("AsyncTaskException", e.getMessage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e("AsyncTask", "onPostExecute : " + s);
    }
}
