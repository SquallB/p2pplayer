package ca.uqac.sylvain.p2pplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by .Sylvain on 25/04/2016.
 */
public class RemoteFilesFragment extends FilesFragment {
    public static final String FILES_NAMES_KEY = "filesNames";

    private List<String> fileNames;

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public RemoteFilesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        Object o = bundle.get(FILES_NAMES_KEY);
        if(o instanceof List) {
            fileNames = (List)o;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private List<CustomFile> getFiles(File directory, int depth) {
        List<CustomFile> files = new ArrayList<>();

        if(fileNames != null) {
            for(String fileName: fileNames) {
                files.add(new CustomFile(fileName, depth));
            }

            Collections.sort(files);
        }

        return files;
    }
}
