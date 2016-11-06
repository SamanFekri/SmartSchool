package skings.smartschool;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.BoolRes;
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

public class DoorActivity extends AppCompatActivity {

    private ImageView lamp;
    private TextView numIn;
    private int classNumber;
    private String[] rClass = {"http://thingtalk.ir/channels/118/feed.json?key=CSL9Y4UV0XPJSOB7", "http://thingtalk.ir/channels/121/feed.json?key=U7AHKXY0WV7IS91F"};
    private String[] wClass = {"http://thingtalk.ir/update?key=CSL9Y4UV0XPJSOB7", "http://thingtalk.ir/update?key=U7AHKXY0WV7IS91F"};


    private int mInterval = 1000; // 1 seconds by default, can be changed later
    private Handler mHandler;
    private Runnable mStatusChecker;

    private boolean isLampOn;
    private String[] lastFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door);

        lamp = (ImageView) findViewById(R.id.lamp_iv);
        numIn = (TextView) findViewById(R.id.number_in_ckass_tv);
        classNumber = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            classNumber = extras.getInt("classno");
            //The key argument here must match that used in the other activity
        }
        isLampOn = false;
        lastFields = new String[]{"0", "0", "0", "0"};

/*        lamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                change state of lamp
                isLampOn = !isLampOn;

                if (isLampOn) {
//                    lamp.setImageDrawable(getResources().getDrawable(R.drawable.led_on));
                    lastFields[1] = "1";
                } else {
//                    lamp.setImageDrawable(getResources().getDrawable(R.drawable.led_off));
                    lastFields[1] = "0";
                }
                String writeUrl = wClass[classNumber];
                for (int i = 0; i < 4; i++) {
                    writeUrl += "&field"+(i+1)+"="+lastFields[i];
                }
//                Log.e("SKingss",writeUrl);
//                Log.d("SKingss",writeUrl);
                WriteAsyncTask wat = new WriteAsyncTask();
                wat.execute(writeUrl);

                ClassAsyncTask cat = new ClassAsyncTask();
                cat.execute();
            }
        });*/

        //starting repeat task for reading from channel
        mHandler = new Handler();

        mStatusChecker = new Runnable() {
            @Override
            public void run() {
                try {
                    ClassAsyncTask cat = new ClassAsyncTask();
                    cat.execute(rClass[classNumber]);
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


    private class ClassAsyncTask extends AsyncTask<String, String, String> {
        ThingTalkJSON ttj;

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
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
            } catch (Exception e) {
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
                numIn.setText(ttj.getFeeds().get(size).get("field1") + "");
                if (Integer.parseInt(ttj.getFeeds().get(size).get("field2") + "") == 0) {
                    lamp.setImageDrawable(getResources().getDrawable(R.drawable.led_off));
                    isLampOn = false;
                } else {
                    lamp.setImageDrawable(getResources().getDrawable(R.drawable.led_on));
                    isLampOn = true;
                }
                lastFields[0] = ttj.getFeeds().get(size).get("field1") + "";
                lastFields[1] = ttj.getFeeds().get(size).get("field2") + "";
                lastFields[2] = ttj.getFeeds().get(size).get("field3") + "";
                lastFields[3] = ttj.getFeeds().get(size).get("field4") + "";
            } catch (Exception e) {
            }
        }
    }
}
