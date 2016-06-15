package ca.uqac.sylvain.p2pplayer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerFilesFragment extends FilesFragment {
    public static final String FILES_NAMES_KEY = "files";
    List<CustomFile> files;

    public List<CustomFile> getFiles() {
        return files;
    }

    public void setFiles(List<CustomFile> files) {
        this.files = files;
    }

    @Override
    protected void initAdapter() {
        adapter = new FilesAdapter(files);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void expandDirectory(CustomFile item, int position) {
        GetServerFilesAsyncTask task = new GetServerFilesAsyncTask(item, adapter, position);
        String path = item.getParentPath();
        if(!path.equals("")) {
            path += "/";
        }
        path += item.getName();
        task.execute(path);
    }

    @Override
    protected void playSong(CustomFile item) {
        if (getActivity() instanceof MainActivity) {
            GetServerSongTask task = new GetServerSongTask((MainActivity) getActivity());
            task.execute(item.getParentPath() + "/" + item.getPath());
        }
    }
}
