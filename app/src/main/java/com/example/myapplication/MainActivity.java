package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ForceUpdateChecker.OnUpdateNeededListener {
    ProgressDialog progressDoalog;
    private TextView mTotal, deaths, recovered;
    private static final String FB_RC_KEY_LATEST_VERSION = "app_current_version";
    private static final String FB_RC_KEY_APP_URL = "app_play_store_url";
    String TAG = "HomeActivity";


    FirebaseRemoteConfig mFirebaseRemoteConfig;

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTotal = findViewById(R.id.total_cases);
        deaths = findViewById(R.id.deaths);
        recovered = findViewById(R.id.recovered);
        //forceUpdateConfig();
        fetchData();

    }

    private void fetchData() {
        progressDoalog = new ProgressDialog(MainActivity.this);
        progressDoalog.setMessage("Live Corona Data.....");
        progressDoalog.setCanceledOnTouchOutside(false);
        progressDoalog.show();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<CoronaPojo> call = service.getAllPhotos();
        call.enqueue(new Callback<CoronaPojo>() {
            @Override
            public void onResponse(Call<CoronaPojo> call, Response<CoronaPojo> response) {
                progressDoalog.dismiss();
                mTotal.setText(response.body().getCorona_cases().get(0).trim());
                deaths.setText(response.body().getCorona_cases().get(1).trim());
                recovered.setText(response.body().getCorona_cases().get(2).trim());
                try {
                    Gson gson = new Gson();
                    String successResponse = gson.toJson(response.body());
                    JSONObject object = new JSONObject(successResponse);
                    JSONObject docs  = object.getJSONObject("data");
                    JSONArray array  = docs.getJSONArray("results");

                    File file = new File(Environment.getExternalStorageDirectory() + java.io.File.separator +"smdridhi.csv");
                    String csv = CDL.toString(array);
                   // Log.e("Getting CSV",csv);
                    FileUtils.writeStringToFile(file, csv);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<CoronaPojo> call, Throwable t) {
                progressDoalog.dismiss();
                Log.e("asss", t.getMessage());
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void forceUpdateConfig() {
        final int versionCode = BuildConfig.VERSION_CODE;

        final HashMap<String, Object> defaultMap = new HashMap<>();
        defaultMap.put(FB_RC_KEY_LATEST_VERSION, "" + versionCode);
        defaultMap.put(FB_RC_KEY_APP_URL, FB_RC_KEY_APP_URL);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mFirebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build());

        mFirebaseRemoteConfig.setDefaults(defaultMap);

        Task<Void> fetchTask = mFirebaseRemoteConfig.fetch(BuildConfig.DEBUG ? 0 : TimeUnit.HOURS.toSeconds(4));

        fetchTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // After config data is successfully fetched, it must be activated before newly fetched
                    // values are returned.
                    mFirebaseRemoteConfig.activateFetched();

                    int latestAppVersion = Integer.parseInt(getValue(FB_RC_KEY_LATEST_VERSION, defaultMap));
                    String appUrl = getValue(FB_RC_KEY_APP_URL,defaultMap);

                    if (latestAppVersion > versionCode) {
                        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("App Update")
                                .setCancelable(false)
                                .setMessage("Please Update app for latest update.")
                                .setPositiveButton("Update",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }
                                            }
                                        }).setNegativeButton("No, thanks",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                .show();
                    }
                    else{
                        fetchData();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Fetch Failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public String getValue(String parameterKey, HashMap<String, Object> defaultMap) {
        String value = mFirebaseRemoteConfig.getString(parameterKey);
        if (TextUtils.isEmpty(value))
            value = (String) defaultMap.get(parameterKey);

        return value;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onUpdateNeeded(String updateUrl) {
        Toast.makeText(this, updateUrl, Toast.LENGTH_LONG).show();

    }
}
