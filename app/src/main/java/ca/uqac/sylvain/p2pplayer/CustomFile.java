package ca.uqac.sylvain.p2pplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomFile extends File {
    private boolean expanded;
    private List<CustomFile> mChildrenList;
    private int depth;

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

    public CustomFile(String path, int depth) {
        super(path);
        this.expanded = false;
        this.mChildrenList = new ArrayList<>();
        this.depth = depth;
    }
}