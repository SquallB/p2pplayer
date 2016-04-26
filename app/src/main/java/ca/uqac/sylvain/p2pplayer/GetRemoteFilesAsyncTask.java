package ca.uqac.sylvain.p2pplayer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class GetRemoteFilesAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {
    private MainActivity activity;
    private InetAddress address;

    public GetRemoteFilesAsyncTask(MainActivity activity, InetAddress address) {
        this.activity = activity;
        this.address = address;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        ArrayList<String> result = new ArrayList<>();

        try {
            SocketAddress sockaddr = new InetSocketAddress(address, 8888);
            Socket socket = new Socket();
            socket.setReuseAddress(true);
            socket.connect(sockaddr, 5000); //10 second connection timeout
            if (socket.isConnected()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while((line = reader.readLine()) != null) {
                    result.add(line);
                }
            }
        }
        catch (Exception e) {}

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<String> s) {
        super.onPostExecute(s);
        FragmentManager fm = activity.getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(MainActivity.REMOTE_FILES_FRAGMENT);
        if (fragment == null) {
            fragment = new RemoteFilesFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(RemoteFilesFragment.FILES_NAMES_KEY, s);
            fragment.setArguments(bundle);
        }
        else if(fragment instanceof RemoteFilesFragment) {
            RemoteFilesFragment remoteFragment = (RemoteFilesFragment)fragment;
            remoteFragment.setFileNames(s);
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_fragment, fragment, MainActivity.REMOTE_FILES_FRAGMENT);
        ft.addToBackStack(MainActivity.REMOTE_FILES_FRAGMENT);
        ft.commit();
    }
}
