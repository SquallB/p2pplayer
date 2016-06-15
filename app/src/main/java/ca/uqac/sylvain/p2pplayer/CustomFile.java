package ca.uqac.sylvain.p2pplayer;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomFile extends File {
    private boolean expanded;
    private List<CustomFile> mChildrenList;
    private int depth;
    private boolean directory;
    private String parentPath;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public List<CustomFile> getmChildrenList() {
        return mChildrenList;
    }

    public void setmChildrenList(List<CustomFile> mChildrenList) {
        this.mChildrenList = mChildrenList;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public String getParentPath() {
        return  parentPath;
    }

    public void setParentPath(String fullPath) {
        this. parentPath = fullPath;
    }

    public CustomFile(String dir, String path) {
        super(dir, path);
    }

    public CustomFile(String path, int depth, boolean isDirectory) {
        this(path, depth, isDirectory, "");
    }

    public CustomFile(String path, int depth, boolean isDirectory, String parentPath) {
        super(path);
        this.expanded = false;
        this.mChildrenList = new ArrayList<>();
        this.depth = depth;
        this.directory = isDirectory;
        this.parentPath = parentPath;
    }
}