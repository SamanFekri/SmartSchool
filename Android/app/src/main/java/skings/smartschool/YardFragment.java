package skings.smartschool;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

/**
 * Created by SKings on 10/11/2016.
 */
public class YardFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yard, container, false);
        ImageView flow = (ImageView) view.findViewById(R.id.electricity_flow);

        //play animation
        YoYo.with(Techniques.Pulse).duration(700).delay(300).playOn(flow);
        //onclick listener
        flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //play animation
                YoYo.with(Techniques.Pulse).duration(500).playOn(v);
                //delayed on thread
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //change activity
                        Intent myIntent = new Intent(getActivity(), FlowActivity.class);
                        startActivity(myIntent);
                    }
                }, 500);
            }
        });

        return view;

    }
}
