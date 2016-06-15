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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilesFragment extends Fragment {
    protected FilesAdapter adapter;

    protected static final float INITIAL_POSITION = 0.0f;
    protected static final float ROTATED_POSITION = 90.0f;

    protected static final String ARG_COLUMN_COUNT = "column-count";
    protected int mColumnCount = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    protected void initAdapter() {
        final File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        adapter = new FilesAdapter(getFiles(musicDirectory, 0));
    }

    protected List<CustomFile> getFiles(File directory, int depth) {
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.isDirectory()) {
                    return true;
                }

                return pathname.getName().toLowerCase().endsWith(".mp3");
            }
        };
        File[] files = directory.listFiles(filter);
        Arrays.sort(files);
        List<CustomFile> list = new ArrayList<>();

        for(int i = 0; i < files.length; i++) {
            list.add(new CustomFile(files[i].getPath(), depth, files[i].isDirectory()));
        }

        return list;
    }

    public void removeChildren(FilesAdapter adapter, CustomFile item) {
        List<CustomFile> files = item.getmChildrenList();
        for (CustomFile file : files) {
            if(file.isDirectory() && file.isExpanded()) {
                removeChildren(adapter, file);
            }
            adapter.remove(file);
            file.setExpanded(false);
        }
    }

    protected void expandDirectory(CustomFile item, int position) {
        List<CustomFile> files = getFiles(item, item.getDepth() + 1);
        item.setmChildrenList(files);
        for (CustomFile file : files) {
            position++;
            adapter.insert(position, file);
        }
    }

    protected void playSong(CustomFile item) {
        if(getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity)getActivity();
            final MusicService musicSrv = activity.getMusicSrv();
            if(activity.isMusicBound()) {
                String dirPath = item.getParent();
                if(dirPath != null) {
                    List<CustomFile> songs = getFiles(new File(dirPath), 0);
                    musicSrv.setList(songs);
                    int songPosition = -1;
                    int i = 0;
                    while(songPosition == -1 && i < songs.size()) {
                        CustomFile song = songs.get(i);
                        if(song.getName().equals(item.getName())) {
                            songPosition = i;
                        }
                        else {
                            i++;
                        }
                    }
                    if(songPosition > -1) {
                        musicSrv.playSong(songPosition);
                    }
                }
                else {
                    musicSrv.playSong(item);
                }
                FragmentManager fm = getFragmentManager();
                Fragment fragment = fm.findFragmentByTag(MainActivity.PLAYER_FRAGMENT);
                if (fragment == null) {
                    fragment = new PlayerFragment();
                }
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.main_fragment, fragment, MainActivity.PLAYER_FRAGMENT);
                ft.addToBackStack(MainActivity.PLAYER_FRAGMENT);
                ft.commit();
            }
            else {
                Log.e("MUSIC SERVICE", "Not bound");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);
        view.setBackgroundColor(Color.WHITE);

        initAdapter();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            recyclerView.setAdapter(adapter);
            recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        CustomFile item = adapter.getItem(position);

                        if(item.isDirectory()) {
                            ImageButton arrow = (ImageButton)view.findViewById(R.id.parent_list_item_expand_arrow);
                            RotateAnimation rotateAnimation;

                            if(item.isExpanded()) {
                                rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                                        INITIAL_POSITION,
                                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                                removeChildren(adapter, item);
                            }
                            else {
                                rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                                        INITIAL_POSITION,
                                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                                expandDirectory(item, position);
                            }

                            rotateAnimation.setDuration(200);
                            rotateAnimation.setFillAfter(true);
                            arrow.startAnimation(rotateAnimation);

                            item.setExpanded(!item.isExpanded());
                        }
                        else {
                            playSong(item);
                        }
                    }
                })
            );

        }
        return view;
    }
}
