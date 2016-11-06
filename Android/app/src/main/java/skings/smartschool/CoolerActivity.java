package skings.smartschool;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.TimePickerDialog;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class CoolerActivity extends AppCompatActivity {

    private String[] rCooler = {"http://thingtalk.ir/channels/117/feed.json?key=7RU9LTSI8X4E8Y0G","http://thingtalk.ir/channels/120/feed.json?key=ZJ7SX8RSFEGX2HFK"};
    private String[] wCooler = {"http://thingtalk.ir/update?key=7RU9LTSI8X4E8Y0G","http://thingtalk.ir/update?key=ZJ7SX8RSFEGX2HFK"};

    private TextView start,end,temperture;

    private int classNumber;

    private int mInterval = 400;
    private Handler mHandler;
    private Runnable mStatusChecker;

    private String[] lastFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooler);

        start = (TextView) findViewById(R.id.cooler_start_tv);
        end = (TextView) findViewById(R.id.cooler_end_tv);
        temperture = (TextView) findViewById(R.id.degree_tv);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(true);
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(false);
            }
        });
        findViewById(R.id.static_day_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(true);
            }
        });
        findViewById(R.id.static_night_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(false);
            }
        });

        classNumber = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            classNumber = extras.getInt("classno");
            //The key argument here must match that used in the other activity
        }

        lastFields = new String[]{"0", "0", "0", "0"};

        //starting repeat task for reading from channel
        mHandler = new Handler();

        mStatusChecker = new Runnable() {
            @Override
            public void run() {
                try {
                    CoolerAsyncTask cat = new CoolerAsyncTask();
                    cat.execute(rCooler[classNumber]);
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

    private class CoolerAsyncTask extends AsyncTask<String, String, String> {
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

                lastFields[0] = ttj.getFeeds().get(size).get("field1") + "";
                lastFields[1] = ttj.getFeeds().get(size).get("field2") + "";
                lastFields[2] = ttj.getFeeds().get(size).get("field3") + "";

                start.setText(lastFields[0]);
                end.setText(lastFields[1]);
                temperture.setText(lastFields[2]+" c");

            } catch (Exception e) {
            }
        }
    }

    private void showTimePickerDialog(final boolean isStart) {
        Dialog.Builder builder = new TimePickerDialog.Builder(R.style.Material_App_Dialog_TimePicker, 24, 00) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();


                if(isStart){
                    lastFields[0] = dialog.getHour()+":"+String.format("%02d", getMinute());
                }else{
                    lastFields[1] = dialog.getHour()+":"+String.format("%02d", getMinute());
                }

                String writeUrl = wCooler[classNumber];
                for (int i = 0; i < 3; i++) {
                    writeUrl += "&field"+(i+1)+"="+lastFields[i];
                }

                WriteAsyncTask wat = new WriteAsyncTask();
                wat.execute(writeUrl);
                //Toast.makeText(getApplication(),lastFields[0],Toast.LENGTH_SHORT).show();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {

                super.onNegativeActionClicked(fragment);
            }
        };
        builder.positiveAction("OK").negativeAction("CANCEL");
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(),null);

    }
}
