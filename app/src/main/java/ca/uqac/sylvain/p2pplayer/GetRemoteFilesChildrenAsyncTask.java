package ca.uqac.sylvain.p2pplayer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by .Sylvain on 26/04/2016.
 */
public class GetRemoteFilesChildrenAsyncTask extends GetRemoteFilesAsyncTask {
    private RemoteFilesFragment fragment;
    private int position;
    private int depth;

    public GetRemoteFilesChildrenAsyncTask(MainActivity activity, InetAddress address, RemoteFilesFragment fragment, int position, int depth) {
        super(activity, address);
        this.fragment = fragment;
        this.position = position;
        this.depth = depth;
    }

    @Override
    protected void onPostExecute(ArrayList<String> s) {

    }
}
