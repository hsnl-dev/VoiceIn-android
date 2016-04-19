package tw.kits.voicein;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.kits.voicein.model.DeviceInfoEntity;
import tw.kits.voicein.model.Provider;

/**
 * Created by Henry on 2016/4/19.
 */
public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            Log.d(TAG, "GCM Registration Token: " + token);

            // pass along this data
            sendRegistrationToServer(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) {
        String userUuid = ((G8penApplication)getApplication()).getUserUuid();
        DeviceInfoEntity infoEntity = new DeviceInfoEntity();
        infoEntity.setDeviceOS("android");
        infoEntity.setDeviceId(token);
        ((G8penApplication)getApplication()).getAPIService().updateDeviceId(userUuid,infoEntity).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "GCM Registration Token: " );
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }
}
