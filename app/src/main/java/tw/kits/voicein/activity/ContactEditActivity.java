package tw.kits.voicein.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.G8penApplication;
import tw.kits.voicein.R;
import tw.kits.voicein.fragment.DeleteDialogFragment;
import tw.kits.voicein.fragment.ProgressFragment;
import tw.kits.voicein.fragment.TimePickerDialogFragment;
import tw.kits.voicein.model.CallForm;
import tw.kits.voicein.model.Contact;
import tw.kits.voicein.util.ColoredSnackBarUtil;
import tw.kits.voicein.util.ServiceConstant;
import tw.kits.voicein.util.TimeHandler;
import tw.kits.voicein.util.VoiceInService;

public class ContactEditActivity extends AppCompatActivity {
    public static final String ARG_CONTACT = "Contact";
    private static final String TAG = ContactEditActivity.class.getName();

    Contact mContact;
    LinearLayout mLayout;
    EditText mNickName;
    ProgressFragment progressDialog;
    TextView mCompany;
    TextView mLocation;
    TextView mName;
    TextView mProfile;
    ImageView mAvatar;
    Button mDelButton;
    Button mLikeButton;
    Button mCallButton;
    Picasso mPicasso;
    TextView mDisturbText;
    TextView mEnhanceText;
    TextView mAStartText;
    TextView mAEndText;
    SwitchCompat mDisturbSw;
    SwitchCompat mEnhanceSw;
    String mToken;
    String mUserUuid;
    VoiceInService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        mApiService = ((G8penApplication) getApplication()).getAPIService();
        mToken = ((G8penApplication) getApplication()).getToken();
        mUserUuid = ((G8penApplication) getApplication()).getUserUuid();

        progressDialog = new ProgressFragment();

        mNickName = (EditText) findViewById(R.id.contact_edit_et_nickname);
        mCompany = (TextView) findViewById(R.id.contact_edit_tv_com);
        mLocation = (TextView) findViewById(R.id.contact_edit_tv_loc);
        mName = (TextView) findViewById(R.id.contact_edit_tv_name);
        mProfile = (TextView) findViewById(R.id.contact_edit_tv_profile);
        mDisturbText = (TextView) findViewById(R.id.contact_edit_tv_disturb);
        mEnhanceText = (TextView) findViewById(R.id.contact_edit_tv_enhance);
        mAStartText = (TextView) findViewById(R.id.contact_edit_tv_start_time);
        mAEndText = (TextView) findViewById(R.id.contact_edit_tv_end_time);
        mDisturbSw = (SwitchCompat) findViewById(R.id.contact_edit_sw_disturb);
        mEnhanceSw = (SwitchCompat) findViewById(R.id.contact_edit_sw_enhance);
        mLikeButton = (Button) findViewById(R.id.contact_edit_btn_like);
        mAvatar = (ImageView) findViewById(R.id.contact_edit_img_avatar);
        mLayout = (LinearLayout) findViewById(R.id.contact_add_view);
        mContact = (Contact) getIntent().getSerializableExtra(ARG_CONTACT);
        mDelButton = (Button) findViewById(R.id.contact_edit_btn_del);
        mCallButton = (Button) findViewById(R.id.contact_edit_btn_call);
        mPicasso = ((G8penApplication) getApplication()).getImgLoader(this);
        mPicasso.load(ServiceConstant.getAvatarById(mContact.getProfilePhotoId(), ServiceConstant.PIC_SIZE_LARGE))
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .into(mAvatar);

        mNickName.setText(mContact.getNickName());
        mCompany.setText(mContact.getCompany());
        mLocation.setText(mContact.getLocation());
        mName.setText(mContact.getUserName());
        mProfile.setText(mContact.getProfile());

        mDisturbSw.setChecked(!mContact.getIsEnable());
        mEnhanceSw.setChecked(mContact.getIsHigherPriorityThanGlobal());
        mDisturbText.setText(!mContact.getIsEnable() ? getString(R.string.enabled)
                : getString(R.string.disabled));
        mEnhanceText.setText(mContact.getIsHigherPriorityThanGlobal() ? getString(R.string.enabled)
                : getString(R.string.disabled));
        mAStartText.setText(mContact.getAvailableStartTime());
        mAEndText.setText(mContact.getAvailableEndTime());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDisturbSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDisturbText.setText(isChecked ? getString(R.string.enabled)
                        : getString(R.string.disabled));
            }
        });
        mEnhanceSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEnhanceText.setText(isChecked ? getString(R.string.enabled)
                        : getString(R.string.disabled));
            }
        });
        mDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteDialogFragment fragment = DeleteDialogFragment.newInstance(
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                delContact();
                            }
                        }
                );
                fragment.show(getSupportFragmentManager(), "asking");
            }
        });
        mAStartText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimeHandler handler = new TimeHandler(mAStartText);
                DialogFragment fragment = TimePickerDialogFragment
                        .createInstance(handler, handler.getTextView());
                fragment.show(getSupportFragmentManager(), "timepicker_start");

            }
        });
        mAEndText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeHandler handler = new TimeHandler(mAEndText);
                DialogFragment fragment = TimePickerDialogFragment
                        .createInstance(handler, handler.getTextView());
                fragment.show(getSupportFragmentManager(), "timepicker_end");

            }
        });
        if(mContact.getProviderIsEnable()){
            mCallButton.setText(getString(R.string.call_action));
            Drawable drawable = ContextCompat.getDrawable(this,R.drawable.ic_phone_white_24dp);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
            mCallButton.setCompoundDrawables(drawable,null,null,null);

            mCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call();
                }
            });
        }else{
            mCallButton.setText(getString(R.string.forbidden_call));
            Drawable drawable = ContextCompat.getDrawable(this,R.drawable.ic_phone_locked_white_24dp);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
            mCallButton.setCompoundDrawables(drawable,null,null,null);
            mCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ColoredSnackBarUtil
                            .primary(Snackbar.make(mLayout, R.string.forbidden_call_hint, Snackbar.LENGTH_INDEFINITE))
                            .show();
                }
            });
        }


        refreshLike();
        mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLikePerson();
            }
        });
    }

    private void updateContact() {
        progressDialog.show(getSupportFragmentManager(), "update_wait");

        mApiService.updateQRcodeInfo(
                mContact.getId(),
                mNickName.getText().toString(),
                !mDisturbSw.isChecked(),
                mAStartText.getText().toString(),
                mAEndText.getText().toString(),
                mEnhanceSw.isChecked())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        progressDialog.dismiss();
                        if (response.isSuccess()) {
                            Intent data = new Intent();
                            setResult(RESULT_OK, data);
                            finish();
                        } else {
                            switch (response.code()) {
                                case 304:
                                    Log.e(TAG, "mLayout=" + (mLayout == null));
                                    ColoredSnackBarUtil
                                            .primary(Snackbar.make(mLayout, R.string.user_have_added, Snackbar.LENGTH_INDEFINITE))
                                            .show();
                                    break;
                                case 404:
                                    Log.e(TAG, "mLayout=" + (mLayout == null));
                                    ColoredSnackBarUtil
                                            .primary(Snackbar.make(mLayout, R.string.user_not_found, Snackbar.LENGTH_INDEFINITE))
                                            .show();
                                    break;
                                default:
                                    Log.e(TAG, "mLayout=" + (mLayout == null));
                                    ColoredSnackBarUtil
                                            .primary(Snackbar.make(mLayout, R.string.server_err, Snackbar.LENGTH_INDEFINITE))
                                            .show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        ColoredSnackBarUtil
                                .primary(Snackbar.make(mLayout, R.string.server_err, Snackbar.LENGTH_INDEFINITE))
                                .show();
                    }
                });

    }

    private void refreshLike() {
        if (mContact.getLike()) {
            mLikeButton.setText("取消常用聯絡人");

        } else {
            mLikeButton.setText("加入常用聯絡人");
        }

    }

    private void call() {
        progressDialog.show(getSupportFragmentManager(), "call_wait");
        CallForm callForm = new CallForm();
        callForm.setContactId(mContact.getId());
        mApiService.createCall(mUserUuid, callForm).enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccess()) {

                } else {
                    switch (response.code()) {
                        case 403:
                            ColoredSnackBarUtil.primary(
                                    Snackbar.make(mLayout, getString(R.string.forbidden_call_hint), Snackbar.LENGTH_SHORT)
                            ).show();
                            break;
                        case 401:
                            ColoredSnackBarUtil.primary(
                                    Snackbar.make(mLayout, getString(R.string.user_not_auth), Snackbar.LENGTH_SHORT)
                            ).show();
                            break;
                        default:
                            ColoredSnackBarUtil.primary(
                                    Snackbar.make(mLayout, getString(R.string.server_err), Snackbar.LENGTH_SHORT)
                            ).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ColoredSnackBarUtil.primary(
                        Snackbar.make(mLayout, getString(R.string.network_err), Snackbar.LENGTH_SHORT)
                ).show();
            }

        });
    }

    private void toggleLikePerson() {
        progressDialog.show(getSupportFragmentManager(), "like_wait");
        mApiService.updateQRcodeILike(mContact.getId(), !mContact.getLike()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccess()) {
                    progressDialog.dismiss();
                    mContact.setLike(!mContact.getLike());
                    refreshLike();

                } else {
                    ColoredSnackBarUtil
                            .primary(Snackbar.make(mLayout, R.string.user_not_found, Snackbar.LENGTH_LONG))
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ColoredSnackBarUtil
                        .primary(Snackbar.make(mLayout, R.string.server_err, Snackbar.LENGTH_LONG))
                        .show();
            }
        });

    }

    private void delContact() {
        progressDialog.show(getSupportFragmentManager(), "del_wait");
        mApiService.delContact(mContact.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccess()) {
                    Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    switch (response.code()) {
                        case 304:
                            ColoredSnackBarUtil
                                    .primary(Snackbar.make(mLayout, R.string.user_have_added, Snackbar.LENGTH_INDEFINITE))
                                    .show();
                            break;
                        case 404:
                            ColoredSnackBarUtil
                                    .primary(Snackbar.make(mLayout, R.string.user_not_found, Snackbar.LENGTH_INDEFINITE))
                                    .show();
                            break;
                        default:
                            ColoredSnackBarUtil
                                    .primary(Snackbar.make(mLayout, R.string.server_err, Snackbar.LENGTH_INDEFINITE))
                                    .show();

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ColoredSnackBarUtil
                        .primary(Snackbar.make(mLayout, R.string.server_err, Snackbar.LENGTH_INDEFINITE))
                        .show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        Log.e(TAG, "Home");
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.contact_edit_menu_confirm:
                updateContact();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
