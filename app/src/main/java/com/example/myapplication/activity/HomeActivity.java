package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.CoronaPojo;
import com.example.myapplication.GetDataService;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.RetrofitClientInstance;
import com.example.myapplication.adapter.CountryNameRecyclerAdapter;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        myRecycler=findViewById(R.id.recycler);
        myRecycler.setLayoutManager(new LinearLayoutManager(this));
        totalInfect=findViewById(R.id.totalInfect);
        totalRecover=findViewById(R.id.totalRecover);
        totalDeath=findViewById(R.id.totalDeath);
        countryName=new ArrayList<String>();
        shareApp=findViewById(R.id.shareApp);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });
        fetchData();


    }

    boolean searchClose=false;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashbord, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setBackgroundColor(Color.parseColor("#ffffff"));
        mSearchView.setQueryHint("Search");
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter.filterList(countryName);
                //setAdapter(countryName);
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {


                if(newText.length()>0){
                    searchCountry=new ArrayList<>();
                    for(int i=0;i<countryName.size();i++) {
                        if (((ArrayList) ((Object) countryName.get(i))).get(0).toString().toLowerCase().contains(newText.toLowerCase())) {

                            //Toast.makeText(HomeActivity.this, "Avail", Toast.LENGTH_SHORT).show();
                            searchCountry.add(countryName.get(i));

                        }
                    }
                    adapter.filterList(searchCountry);
                    //setSearchAdapter(searchCountry);
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
        Call<CoronaPojo> call = service.getAllPhotos();
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
                int country=0;
                int countryLength=countryName.size();
                for(country=0;country<countryLength;country++){
                    if(((ArrayList) ((Object) country_list.get(i))).get(0).toString().trim().toLowerCase().
                            equals(((ArrayList) ((Object) countryName.get(country))).get(0).toString().trim().toLowerCase())){
                        dataExists=true;
                    }
                }
                if(!dataExists){
                    countryName.add(country_list.get(i));

                }
            }
//            countryName=country_list;
            adapter = new CountryNameRecyclerAdapter(this, countryName);
            myRecycler.setAdapter(adapter);
            progressDoalog.dismiss();
        }catch (Exception ex){
            Log.e("adapter exception",ex.toString());
            progressDoalog.dismiss();
            Toast.makeText(this, "Please try after some time.", Toast.LENGTH_SHORT).show();
        }

    }

}
