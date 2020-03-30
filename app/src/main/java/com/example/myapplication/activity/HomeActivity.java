package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.CoronaPojo;
import com.example.myapplication.GetDataService;
import com.example.myapplication.R;
import com.example.myapplication.RetrofitClientInstance;
import com.example.myapplication.adapter.CountryNameRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView myRecycler;
    private CountryNameRecyclerAdapter adapter;
    private ArrayList<String>countryName;//=new ArrayList<String>();
    private ArrayList<String>searchCountry;
    private ProgressDialog progressDoalog;
    private TextView totalInfect,totalRecover,totalDeath;
    private ImageView shareApp;
    private Toolbar toolbar;
    private EditText searchET;
    private TextView titleTV;
    boolean search=false;
    private static final String FB_RC_KEY_LATEST_VERSION = "app_current_version";
    private static final String FB_RC_KEY_APP_URL = "app_play_store_url";
    ImageView searchImg,searchCloseImg;
    FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        myRecycler=findViewById(R.id.recycler);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));
        titleTV=findViewById(R.id.titleTV);
        totalInfect=findViewById(R.id.totalInfect);
        totalRecover=findViewById(R.id.totalRecover);
        totalDeath=findViewById(R.id.totalDeath);
        countryName=new ArrayList<String>();
        shareApp=findViewById(R.id.shareApp);
        toolbar=findViewById(R.id.toolbar);
        searchET=findViewById(R.id.searchET);
        searchImg=findViewById(R.id.searchImg);
        searchCloseImg=findViewById(R.id.searchCloseImg);
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleTV.setVisibility(View.GONE);
                searchImg.setVisibility(View.GONE);
                searchET.setVisibility(View.VISIBLE);
                searchCloseImg.setVisibility(View.VISIBLE);
            }
        });
        searchCloseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleTV.setVisibility(View.VISIBLE);
                searchImg.setVisibility(View.VISIBLE);
                searchET.setVisibility(View.GONE);
                searchCloseImg.setVisibility(View.GONE);
                adapter.filterList(countryName);
                searchET.setText("");
            }
        });
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length()>0){
                    searchCountry=new ArrayList<>();
                    for(int i=0;i<countryName.size();i++) {
                        if (((ArrayList) ((Object) countryName.get(i))).get(0).toString().toLowerCase().contains(s.toString().toLowerCase())) {
                            //Toast.makeText(HomeActivity.this, "Avail", Toast.LENGTH_SHORT).show();
                            searchCountry.add(countryName.get(i));
                        }
                    }
                    adapter.filterList(searchCountry);
                }
                if(s.length()<=0){
                    adapter.filterList(countryName);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setSupportActionBar(toolbar);

        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });
        forceUpdateConfig();
    }

    public void shareApp(){
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Corona Live");
            String shareMessage= "\nCorona Live Update.\n\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {

        }
    }

    public void fetchData(){
        progressDoalog = new ProgressDialog(HomeActivity.this);
        progressDoalog.setMessage("Live Corona Data.....");
        progressDoalog.setCanceledOnTouchOutside(false);
        progressDoalog.show();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<CoronaPojo> call = service.getCoronaData();
        call.enqueue(new Callback<CoronaPojo>() {
            @Override
            public void onResponse(Call<CoronaPojo> call, Response<CoronaPojo> response) {

                try {
                    if(response.isSuccessful() && response.code()==200){
                        totalDeath.setText(response.body().getCorona_cases().get(1).trim());
                        totalInfect.setText(response.body().getCorona_cases().get(0).trim());
                        totalRecover.setText(response.body().getCorona_cases().get(2).trim());
                        setAdapter(((ArrayList) response.body().getCorona()));
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<CoronaPojo> call, Throwable t) {
                progressDoalog.dismiss();
                Log.e("asss", t.getMessage());
                Toast.makeText(HomeActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean dataExists=false;
    public void setAdapter(ArrayList<String> country_list){
        try {
            countryName.add(country_list.get(0));
            for(int i=0;i<country_list.size();i++){
                dataExists=false;
                int countryLength=countryName.size();
                for(int country=0;country<countryLength;country++){
                    if(((ArrayList) ((Object) country_list.get(i))).get(0).toString().trim().toLowerCase().
                            equals(((ArrayList) ((Object) countryName.get(country))).get(0).toString().trim().toLowerCase())){
                        dataExists=true;
                    }
                }
                if(!dataExists){
                    countryName.add(country_list.get(i));
                }
            }
            adapter = new CountryNameRecyclerAdapter(this, countryName);
            myRecycler.setAdapter(adapter);
            progressDoalog.dismiss();
        }catch (Exception ex){
            Log.e("adapter exception",ex.toString());
            progressDoalog.dismiss();
            Toast.makeText(this, "Please try after some time.", Toast.LENGTH_SHORT).show();
        }

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
                        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
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
                    Toast.makeText(HomeActivity.this, "Fetch Failed",
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

}
