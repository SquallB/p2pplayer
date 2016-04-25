package ca.uqac.sylvain.p2pplayer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FilesViewHolder extends RecyclerView.ViewHolder {
    protected ImageView vIcon;
    protected TextView vName;
    protected ImageButton vArrow;
    protected RelativeLayout vLayout;
    protected CustomFile file;

    public TextView getvName() {
        return vName;
    }

    public ImageView getvIcon() {
        return vIcon;
    }

    public ImageButton getvArrow() {
        return vArrow;
    }

    public RelativeLayout getvLayout() {
        return vLayout;
    }

    public CustomFile getFile() {
        return file;
    }

    public void setFile(CustomFile file) {
        this.file = file;
    }

    public FilesViewHolder(View v) {
        super(v);
        vIcon = (ImageView) v.findViewById(R.id.icon);
        vName = (TextView) v.findViewById(R.id.file_name);
        vArrow = (ImageButton) v.findViewById(R.id.parent_list_item_expand_arrow);
        vLayout = (RelativeLayout) v.findViewById(R.id.file_row);
        file = null;
    }
}
