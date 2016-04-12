package tw.kits.voicein.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import tw.kits.voicein.R;

public class GroupAddActivity extends AppCompatActivity {
    EditText mNameText;
    View mMainView;
    RecyclerView mMemberListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);
        mNameText = (EditText) findViewById(R.id.group_add_et_group_name);
        mMainView = findViewById(R.id.group_add_ll_main);
        mMemberListView = (RecyclerView) findViewById(R.id.group_add_rl_contact_list);
    }
}
