package ca.uqac.sylvain.p2pplayer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class GetServerFilesAsyncTask extends AsyncTask<String, Void, List<CustomFile>> {
    private static final String METHOD_NAME = "getDirectory";
    private static final String NAMESPACE = "http://192.168.0.40/";
    private static final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
    private static final String URL = "http://192.168.0.40/soapserver/index.php";

    private CustomFile parent;
    private FilesAdapter adapter;
    private int position;
    private MainActivity activity;

    public GetServerFilesAsyncTask(CustomFile parent, FilesAdapter adapter, int position) {
        this(parent, adapter, position, null);
    }

    public GetServerFilesAsyncTask(CustomFile parent, FilesAdapter adapter, int position, MainActivity activity) {
        this.activity = activity;
        this.parent = parent;
        this.adapter = adapter;
        this.position = position;
    }

    @Override
    protected List<CustomFile> doInBackground(String... params) {
        List<CustomFile> files = new ArrayList<>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("directoryPath", params[0]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = false;
        HttpTransportSE httpRequest = new HttpTransportSE(URL);

        int depth = 0;
        String parentPath = "";
        if(parent != null) {
            depth = parent.getDepth() + 1;
            parentPath = parent.getParentPath() + '/' + parent.getPath();
        }

        try {
            httpRequest.call(SOAP_ACTION, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            SoapObject elements = (SoapObject)response.getProperty("directories");
            for(int i = 0; i < elements.getPropertyCount(); i++) {
                files.add(new CustomFile(elements.getProperty(i).toString(), depth, true, parentPath));
            }
            elements = (SoapObject)response.getProperty("files");
            for(int i = 0; i < elements.getPropertyCount(); i++) {
                files.add(new CustomFile(elements.getProperty(i).toString(), depth, false, parentPath));
            }

        } catch (Exception e) {
            Log.d("Files task", e.getMessage());
            e.printStackTrace();
        }

        return files;
    }

    @Override
    protected void onPostExecute(List<CustomFile> files) {
        if(activity == null) {
            if (parent != null) {
                parent.setmChildrenList(files);
            }
            for (CustomFile file : files) {
                position++;
                adapter.insert(position, file);
            }
        }
        else {
            FragmentManager fm = activity.getFragmentManager();
            Fragment fragment = fm.findFragmentByTag(activity.SERVER_FILES_FRAGMENT);
            if (fragment == null) {
                fragment = new ServerFilesFragment();
            }
            if(fragment instanceof ServerFilesFragment) {
                ((ServerFilesFragment)fragment).setFiles(files);
            }
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.main_fragment, fragment, activity.SERVER_FILES_FRAGMENT);
            ft.addToBackStack(activity.SERVER_FILES_FRAGMENT);
            ft.commit();
        }
    }
}
