package skings.smartschool;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SKings on 10/7/2016.
 */
public class WriteAsyncTask extends AsyncTask<String,String,String>{
    @Override
    protected String doInBackground(String... params) {
//        Log.e("SKings","here: "+ params[0]);
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            Log.d("SKings","url: "+ params[0]);
            url = new URL(params[0]);

            urlConnection = (HttpURLConnection) url
                    .openConnection();

            InputStream in = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);

            br.readLine();


            br.close();
            isr.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}
