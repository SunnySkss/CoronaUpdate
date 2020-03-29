package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class CountryDetails extends AppCompatActivity {

    private TextView total_cases,new_cases,total_Deaths,new_Deaths,total_Recovered,active_Cases,serious,tot_Cases_1M_pop,tot_Deaths_1M_pop;
    private TextView country_Name,firstCase;
    ArrayList<String> countryInfo=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_details);
        country_Name=findViewById(R.id.country_Name);
        total_cases=findViewById(R.id.total_cases);
        new_cases=findViewById(R.id.new_cases);
        total_Deaths=findViewById(R.id.total_Deaths);
        new_Deaths=findViewById(R.id.new_Deaths);
        total_Recovered=findViewById(R.id.total_Recovered);
        active_Cases=findViewById(R.id.active_Cases);
        serious=findViewById(R.id.serious);
        tot_Cases_1M_pop=findViewById(R.id.tot_Cases_1M_pop);
        tot_Deaths_1M_pop=findViewById(R.id.tot_Deaths_1M_pop);
        firstCase=findViewById(R.id.firstCase);
        Bundle extras = getIntent().getExtras();
        if(extras !=null){

            countryInfo=extras.getStringArrayList("country_info");
            String data="Corona Detail's Of "+countryInfo.get(0);
            SpannableString content = new SpannableString(data);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            country_Name.setText(content);
            total_cases.setText(countryInfo.get(1).trim().equals("")?"-":countryInfo.get(1).trim());
            new_cases.setText(countryInfo.get(2).trim().equals("")?"-":countryInfo.get(2).trim());
            total_Deaths.setText(countryInfo.get(3).trim().equals("")?"-":countryInfo.get(3).trim());
            new_Deaths.setText(countryInfo.get(4).trim().equals("")?"-":countryInfo.get(4).trim());
            total_Recovered.setText(countryInfo.get(5).trim().equals("")?"-":countryInfo.get(5).trim());
            active_Cases.setText(countryInfo.get(6).trim().equals("")?"-":countryInfo.get(6).trim());
            serious.setText(countryInfo.get(7).trim().equals("")?"-":countryInfo.get(7).trim());
            tot_Cases_1M_pop.setText(countryInfo.get(8).trim().equals("")?"-":countryInfo.get(8).trim());
            tot_Deaths_1M_pop.setText(countryInfo.get(9).trim().equals("")?"-":countryInfo.get(9).trim());
            firstCase.setText(countryInfo.get(10).trim().equals("")?"NA":countryInfo.get(10).trim());
        }
    }
}
