package tw.kits.voicein.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import tw.kits.voicein.R;


public class AppFeatureFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int drawableId;
    private String title;
    private String body;
    private int backgroundColor;



    public AppFeatureFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AppFeatureFragment newInstance(String title, String body,int drawableId, int backgroundColor) {
        Bundle args = new Bundle();
        args.putInt("drawable",drawableId);
        args.putInt("color",backgroundColor);
        args.putString("title",title);
        args.putString("body",body);
        AppFeatureFragment fragment = new AppFeatureFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            body = getArguments().getString("body");
            backgroundColor = getArguments().getInt("color");
            drawableId = getArguments().getInt("drawable");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_app_intro, container, false);
        TextView titleText = (TextView) view.findViewById(R.id.app_intro_tv_title);
        TextView bodyText = (TextView)view.findViewById(R.id.app_intro_tv_body);
        ImageView imgView = (ImageView)view.findViewById(R.id.app_intro_img);
        View  main = view.findViewById(R.id.app_intro_v_main);

        titleText.setText(title);
        bodyText.setText(body);
        imgView.setImageDrawable(ContextCompat.getDrawable(getContext(),drawableId));
        main.setBackgroundColor(backgroundColor);


        return view;
    }


}
