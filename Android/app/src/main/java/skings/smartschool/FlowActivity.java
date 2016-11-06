package skings.smartschool;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class FlowActivity extends AppCompatActivity {

    private LineChart chart;
    private LineDataSet dataSet;
    private LineData lineData;

    private final long DAY = 24*60*60*1000, WEEK = DAY * 7, MONTH = DAY * 30, YEAR = DAY * 365;
    private long range = DAY;

    private String[] rFlow = {"http://thingtalk.ir/channels/119/fields/1.json?results=24000","http://thingtalk.ir/channels/119/fields/1.json?results=24000"};
    private TextView day_tv,week_tv,month_tv,year_tv;

    private int mInterval = 1000; // 1 seconds by default, can be changed later
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);

        chart = (LineChart) findViewById(R.id.elec_chart);
        chart.setDescription("Electricity");
        chart.setDescriptionColor(Color.YELLOW);
        chart.setDescriptionTextSize(14);

        List<Entry> entries = new ArrayList<Entry>();
        dataSet = new LineDataSet(entries, "flow");

        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawCircles(false);
        dataSet.setDrawFilled(true);

        lineData = new LineData(dataSet);

        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinValue(0);
        leftAxis.setDrawGridLines(false);


        chart.notifyDataSetChanged();
        chart.invalidate(); // refresh
        //
        day_tv = (TextView) findViewById(R.id.flow_day);
        week_tv = (TextView) findViewById(R.id.flow_week);
        month_tv = (TextView) findViewById(R.id.flow_month);
        year_tv = (TextView) findViewById(R.id.flow_year);

        day_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Pulse).duration(500).playOn(v);
                changeRange(DAY);
            }
        });

        week_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Pulse).duration(500).playOn(v);
                changeRange(WEEK);
            }
        });

        month_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Pulse).duration(500).playOn(v);
                changeRange(MONTH);
            }
        });

        year_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Pulse).duration(500).playOn(v);
                changeRange(YEAR);
            }
        });
        //

        changeRange(DAY);

        //starting repeat task for reading from channel
        mHandler = new Handler();
        startRepeatingTask();
    }

    public void changeRange(long newRange){
        range = newRange;
        day_tv.setTextColor(Color.GRAY);
        week_tv.setTextColor(Color.GRAY);
        month_tv.setTextColor(Color.GRAY);
        year_tv.setTextColor(Color.GRAY);

        if (newRange == DAY) {
            day_tv.setTextColor(Color.WHITE);
        }
        if (newRange == WEEK) {
            week_tv.setTextColor(Color.WHITE);
        }
        if (newRange == MONTH) {
            month_tv.setTextColor(Color.WHITE);
        }
        if (newRange == YEAR) {
            year_tv.setTextColor(Color.WHITE);
        }
        dataSet.clear();
        FlowAsyncTask fat = new FlowAsyncTask();
        fat.execute(rFlow[0]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                FlowAsyncTask fat = new FlowAsyncTask();
                fat.execute(rFlow[0]);
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    private class FlowAsyncTask extends AsyncTask<String,String,String> {
        ThingTalkJSON thingTalkJSON;

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

                Log.v("SKINGSSS",input);

                //thing talk json maker
                thingTalkJSON = new ThingTalkJSON(input);


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
            //remove additional
            removeAdditionalFeeds();
            //fill chart with data in things talk
            chartFiller();
        }

        public void removeAdditionalFeeds()  {
            try {
                ArrayList<HashMap> feeds = thingTalkJSON.getFeeds();
                for (int i = feeds.size() - 1; i >= 0; i--) {
                    String tmp = (feeds.get(i).get("created_at") + "");
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

                    Date dateTime = formatter.parse(tmp);

                    Date curdate = new Date();
                    curdate = cvtToGmt(curdate);

                    //Log.d("Skings-tme",curdate+" " + dateTime + " -> " + (curdate.getTime() - dateTime.getTime()) + " " +range);
                    if((curdate.getTime() - dateTime.getTime()) > range){
                        feeds.remove(i);
                    }
                }
                thingTalkJSON.setFeeds(feeds);
            } catch (Exception e){

            }
        }

        public void chartFiller(){
            int count = dataSet.getEntryCount();

            chart.getXAxis().setAxisMaxValue(thingTalkJSON.getFeeds().size()-1);
            for (int i = count; i < thingTalkJSON.getFeeds().size(); i++){
                int y = Integer.parseInt(thingTalkJSON.getFeeds().get(i).get("field1")+"");
                //long x = ttjTime(thingTalkJSON.getFeeds().get(i)) - ttjTime(thingTalkJSON.getFeeds().get(0));
                dataSet.addEntry(new Entry(i,y));
                lineData.notifyDataChanged();

            }
            chart.notifyDataSetChanged();
            chart.invalidate();
        }

        public long ttjTime(HashMap h){
            String tmp = (h.get("created_at") + "");
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                Date dateTime = formatter.parse(tmp);
                return dateTime.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }

        private  Date cvtToGmt( Date date ){
            TimeZone tz = TimeZone.getDefault();
            Date ret = new Date( date.getTime() - tz.getRawOffset() );

            // if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
            if ( tz.inDaylightTime( ret )){
                Date dstDate = new Date( ret.getTime() - tz.getDSTSavings() );

                // check to make sure we have not crossed back into standard time
                // this happens when we are on the cusp of DST (7pm the day before the change for PDT)
                if ( tz.inDaylightTime( dstDate )){
                    ret = dstDate;
                }
            }
            return ret;
        }
    }
}
