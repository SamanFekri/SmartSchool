package skings.smartschool;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {
   /* private String[] rCooler = {"http://thingtalk.ir/channels/117/feed.json?key=7RU9LTSI8X4E8Y0G", "http://thingtalk.ir/channels/120/feed.json?key=ZJ7SX8RSFEGX2HFK"};
    private String[] wCooler = {"http://thingtalk.ir/update?key=7RU9LTSI8X4E8Y0G", "http://thingtalk.ir/update?key=ZJ7SX8RSFEGX2HFK"};
    private String[] rClass = {"http://thingtalk.ir/channels/118/feed.json?key=CSL9Y4UV0XPJSOB7", "http://thingtalk.ir/channels/121/feed.json?key=U7AHKXY0WV7IS91F"};
    private String[] wClass = {"http://thingtalk.ir/update?key=CSL9Y4UV0XPJSOB7", "http://thingtalk.ir/update?key=U7AHKXY0WV7IS91F"};*/

    //private String rCooler2 = "http://thingtalk.ir/channels/120/feed.json?key=ZJ7SX8RSFEGX2HFK", wCooler2 = "http://thingtalk.ir/update?key=ZJ7SX8RSFEGX2HFK";
    //private String rClass2 = "http://thingtalk.ir/channels/121/feed.json?key=U7AHKXY0WV7IS91F", wClass2 = "http://thingtalk.ir/update?key=U7AHKXY0WV7IS91F";
    //private String rFlow2 = "http://thingtalk.ir/channels/122/feed.json?key=CP8NZ29DJ9KUX1TS";

    private int state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initial fragment and state
        YardFragment yardFragment = new YardFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_layout, yardFragment)
                .commit();
        state = 0;


        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.binoculars));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew).setLayoutParams(new FloatingActionButton.LayoutParams(150, 150))
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);

        rLSubBuilder.setLayoutParams(new FloatingActionButton.LayoutParams(72, 72));
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);

        //SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.bird));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.one));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.two));

        SubActionButton yard_btn = rLSubBuilder.setContentView(rlIcon1).build();
        yard_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"1",Toast.LENGTH_SHORT).show();
                changeState(0);
            }
        });

        SubActionButton class0 = rLSubBuilder.setContentView(rlIcon2).build();
        class0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"2",Toast.LENGTH_SHORT).show();
                changeState(1);
            }
        });

        SubActionButton class1 = rLSubBuilder.setContentView(rlIcon3).build();
        class1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"3",Toast.LENGTH_SHORT).show();
                changeState(2);
            }
        });


        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(yard_btn)
                .addSubActionView(class0)
                .addSubActionView(class1)
                .attachTo(rightLowerButton)
                .build();

    }

    public void changeState(int newState) {
        if (state != newState) {
            ClassFragment classFragment;
            Bundle args;
            switch (newState) {
                case 0:
                    YardFragment yardFragment = new YardFragment();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_layout, yardFragment)
                            .commit();
                    break;
                case 1:
                    classFragment = new ClassFragment();
                    args = new Bundle();
                    args.putInt("classno", 0);
                    classFragment.setArguments(args);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_layout, classFragment)
                            .commit();
                    break;
                case 2:
                    classFragment = new ClassFragment();
                    args = new Bundle();
                    args.putInt("classno", 1);
                    classFragment.setArguments(args);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_layout, classFragment)
                            .commit();
                    break;
            }
            state = newState;
        }
    }
}
