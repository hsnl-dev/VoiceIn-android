package tw.kits.voicein.activity;


import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tw.kits.voicein.R;
import tw.kits.voicein.model.Contact;

public class ContactInfoFragment extends DialogFragment {


    private static final String ARG_CONTACT = "contact";


    public ContactInfoFragment() {

    }


    public static ContactInfoFragment newInstance(Contact contact) {
        ContactInfoFragment fragment = new ContactInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTACT, contact);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_DarkActionBar);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_contact_edit, container, false);
        TextView iCompany = (TextView)v.findViewById(R.id.contact_edit_tv_com);
        TextView iLocation = (TextView)v.findViewById(R.id.contact_edit_tv_loc);
        TextView iName = (TextView)v.findViewById(R.id.contact_edit_tv_name);
        TextView iProfile = (TextView)v.findViewById(R.id.contact_edit_tv_profile);
        Contact contact = (Contact)getArguments().getSerializable(ARG_CONTACT);
        iCompany.setText(contact.getCompany());
        iLocation.setText(contact.getLocation());
        iName.setText(contact.getUserName());
        iProfile.setText(contact.getProfile());
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(R.string.contact_profile_title);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


    }
}
