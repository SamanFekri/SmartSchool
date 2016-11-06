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
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

/**
 * Created by SKings on 10/13/2016.
 */
public class ClassFragment extends Fragment {

    private ImageView door_iv,cooler_iv,gas_iv;
    private int classNumber;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classNumber = getArguments().getInt("classno");
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class, container, false);
        door_iv = (ImageView) view.findViewById(R.id.door_iv);
        cooler_iv = (ImageView) view.findViewById(R.id.cooler_iv);
        gas_iv = (ImageView) view.findViewById(R.id.gas_iv);
        makeViewPulse(door_iv);
        makeViewPulse(cooler_iv);
        makeViewPulse(gas_iv);

        gas_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeViewPulse(v);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(),GasActivity.class);
                        intent.putExtra("classno",classNumber);
                        startActivity(intent);
                    }
                },700);

            }
        });

        door_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeViewPulse(v);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(),DoorActivity.class);
                        intent.putExtra("classno",classNumber);
                        startActivity(intent);
                    }
                },700);

            }
        });

        cooler_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeViewPulse(v);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(),CoolerActivity.class);
                        intent.putExtra("classno",classNumber);
                        startActivity(intent);
                    }
                },700);

            }
        });

        ImageView cn = (ImageView) view.findViewById(R.id.class_number);
        switch (classNumber){
            case 0:
                cn.setImageDrawable(getResources().getDrawable(R.drawable.one));
                break;
            case 1:
                cn.setImageDrawable(getResources().getDrawable(R.drawable.two));
                break;
        }

        return view;

    }

    public void makeViewPulse(View v){
        YoYo.with(Techniques.Pulse).duration(700).playOn(v);
    }
}
