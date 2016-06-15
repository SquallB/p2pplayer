package ca.uqac.sylvain.p2pplayer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class GetServerSongTask extends AsyncTask<String, Void, CustomFile> {
    private static final String METHOD_NAME = "getSong";
    private static final String NAMESPACE = "http://192.168.0.40/";
    private static final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
    private static final String URL = "http://192.168.0.40/soapserver/index.php";

    private MainActivity activity;

    public GetServerSongTask(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected CustomFile doInBackground(String... params) {
        CustomFile file = null;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("songPath", params[0]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = false;
        HttpTransportSE httpRequest = new HttpTransportSE(URL);
        httpRequest.debug = true;

        try {
            httpRequest.call(SOAP_ACTION, envelope);
            Log.d("Files task", httpRequest.responseDump);
            SoapObject response = (SoapObject) envelope.getResponse();
            SoapPrimitive data = (SoapPrimitive)response.getProperty("return");
            FileOutputStream fos = activity.openFileOutput("tmp.mp3", Context.MODE_PRIVATE);
            fos.write(data.toString().getBytes());
            fos.close();
            file = new CustomFile(activity.getFilesDir().getAbsolutePath(), "tmp.mp3");

        } catch (Exception e) {
            Log.d("Files task", e.getMessage());
            e.printStackTrace();
        }

        return file;
    }

    @Override
    protected void onPostExecute(CustomFile file) {
        final MusicService musicSrv = activity.getMusicSrv();
        if(activity.isMusicBound()) {
            musicSrv.setList(new ArrayList<CustomFile>());
            musicSrv.playSong(file);
            FragmentManager fm = activity.getFragmentManager();
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
