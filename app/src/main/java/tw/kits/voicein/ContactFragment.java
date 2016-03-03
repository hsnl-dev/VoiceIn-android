package tw.kits.voicein;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tw.kits.voicein.model.Contact;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {


    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        RecyclerView rvContact = (RecyclerView) view.findViewById(R.id.contact_rv_items);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        rvContact.addItemDecoration(itemDecoration);
        ContactAdapter ca = new ContactAdapter(Contact.genFakeLists());
        rvContact.setAdapter(ca);
        rvContact.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

}
