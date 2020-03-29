package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.CountryDetails;

import java.util.ArrayList;

public class CountryNameRecyclerAdapter extends RecyclerView.Adapter<CountryNameRecyclerAdapter.MyHolder> {

    private ArrayList<String>country_Name=new ArrayList<String>();
    private Context context;

    public CountryNameRecyclerAdapter(Context context, ArrayList country_Name){
        this.context=context;
        this.country_Name=country_Name;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView;
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView=inflater.inflate(R.layout.country_card, parent,false);
        MyHolder myHolder=new MyHolder(rowView);
        return myHolder;
    }
    public void filterList(ArrayList<String> country_Name){
        this.country_Name=country_Name;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
       final ArrayList <Object> cList =new ArrayList<>();
       // ArrayList<String>arrData=new ArrayList<>();
        cList.add(country_Name.get(position));
       // arrData.add(cList.get(0).toString());
        holder.countryName.setText(((ArrayList) cList.get(0)).get(0).toString());
        holder.countryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context, ((ArrayList) cList.get(0)).toString(), Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context, CountryDetails.class);
                intent.putStringArrayListExtra("country_info",((ArrayList) cList.get(0)));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return country_Name.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        TextView countryName;
        public MyHolder(View itemView) {
            super(itemView);
            countryName=itemView.findViewById(R.id.countryName);
        }
    }
}
