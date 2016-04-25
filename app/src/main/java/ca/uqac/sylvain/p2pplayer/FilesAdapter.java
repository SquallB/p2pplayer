package ca.uqac.sylvain.p2pplayer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesViewHolder> {

    private List<CustomFile> mDataset;

    public FilesAdapter(List<CustomFile> dataset) {
        mDataset = dataset;
    }

    @Override
    public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_file, parent, false);
        return new FilesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FilesViewHolder holder, int position) {
       if(holder != null) {
            CustomFile file = mDataset.get(position);
            int iconName;
            int arrowVisibility;

            if (file.isDirectory()) {
                iconName = R.drawable.ic_folder_24dp;
                arrowVisibility = View.VISIBLE;
            }
            else {
                iconName = R.drawable.music_note;
                arrowVisibility = View.INVISIBLE;
            }

            holder.getvIcon().setImageResource(iconName);
            holder.getvName().setText(file.getName());
            holder.getvArrow().setVisibility(arrowVisibility);
            holder.getvLayout().setPadding(50 * file.getDepth(), 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public CustomFile getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, CustomFile data) {
        mDataset.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(CustomFile data) {
        int position = mDataset.indexOf(data);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }
}
