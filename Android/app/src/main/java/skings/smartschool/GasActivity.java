package skings.smartschool;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class GasActivity extends AppCompatActivity {

    private ImageView co2_iv,ch4_iv;
    private TextView co2_tv,ch4_tv;
    private String[] rClass = {"http://thingtalk.ir/channels/118/feed.json?key=CSL9Y4UV0XPJSOB7","http://thingtalk.ir/channels/121/feed.json?key=U7AHKXY0WV7IS91F"};

    private int mInterval = 1000; // 1 seconds by default, can be changed later
    private Handler mHandler;
    private  Runnable mStatusChecker;

    private int classNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas);

        co2_iv = (ImageView) findViewById(R.id.co2_iv);
        ch4_iv = (ImageView) findViewById(R.id.ch4_iv);
        moveUp(co2_iv);
        moveUp(ch4_iv);

        co2_tv = (TextView) findViewById(R.id.co2_tv);
        ch4_tv = (TextView) findViewById(R.id.ch4_tv);

        classNumber = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            classNumber = extras.getInt("classno");
            //The key argument here must match that used in the other activity
        }

        //starting repeat task for reading from channel
        mHandler = new Handler();

        mStatusChecker = new Runnable() {
            @Override
            public void run() {
                try {
                    ClassAsyncTask fat = new ClassAsyncTask();
                    fat.execute(rClass[classNumber]);
                } finally {
                    // 100% guarantee that this always happens, even if
                    // your update method throws an exception
                    mHandler.postDelayed(mStatusChecker, mInterval);
                }
            }
        };
        startRepeatingTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    public void moveUp(View view){
        final View v = view;
        YoYo.with(Techniques.FadeIn).duration(500).playOn(v);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.FadeOutUp).duration(1000).playOn(v);
            }
        },500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.FadeInUp).duration(1000).playOn(v);
            }
        },1500);
    }

    private class ClassAsyncTask extends AsyncTask<String,String,String> {
        ThingTalkJSON ttj;
        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(params[0]);

                urlConnection = (HttpURLConnection) url
                        .openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);

                String input = br.readLine();
                // thing talk json maker
                ttj = new ThingTalkJSON(input);

                br.close();
                isr.close();
                in.close();
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                int size = ttj.getFeeds().size() - 1;
                co2_tv.setText(ttj.getFeeds().get(size).get("field3") + " ppm");
                ch4_tv.setText(ttj.getFeeds().get(size).get("field4") + " ppm");
            } catch(Exception e) {
                co2_tv.setText("Data not found");
                ch4_tv.setText("Data not found");
            }
        }
    }
}
