package tw.kits.voicein.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tw.kits.voicein.R;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.ServiceManager;
import tw.kits.voicein.util.UserAccessStore;

public class ContactEditActivity extends AppCompatActivity {
    public static final String ARG_CONTACT = "contact";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        TextView iCompany = (TextView)findViewById(R.id.contact_edit_tv_com);
        TextView iLocation = (TextView)findViewById(R.id.contact_edit_tv_loc);
        TextView iName = (TextView)findViewById(R.id.contact_edit_tv_name);
        TextView iProfile = (TextView)findViewById(R.id.contact_edit_tv_profile);
        ImageView iAvatar = (ImageView)findViewById(R.id.contact_edit_img_avatar);
        Contact contact = (Contact)getIntent().getSerializableExtra(ARG_CONTACT);
        Picasso picasso = ServiceManager.getPicassoDowloader(this, UserAccessStore.getToken());
        picasso.load(ServiceManager.API_BASE + "api/v1/avatars/" + contact.getProfilePhotoId() + "?size=large")
                .into(iAvatar);
        iCompany.setText(contact.getCompany());
        iLocation.setText(contact.getLocation());
        iName.setText(contact.getUserName());
        iProfile.setText(contact.getProfile());
    }

}
