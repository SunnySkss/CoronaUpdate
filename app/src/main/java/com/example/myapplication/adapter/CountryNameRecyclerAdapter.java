package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
//                Intent intent=new Intent(context, CountryDetails.class);
//                intent.putStringArrayListExtra("country_info",((ArrayList) cList.get(1)));
//                context.startActivity(intent);
                showDialog(((ArrayList)cList.get(0)));
            }
        });

    }

    private AlertDialog countryInfoDialog;
    void showDialog(ArrayList arrayList){
        try {
            String countryNameTVTxt = arrayList.get(0).toString().trim().equals("") ? "-" : arrayList.get(0).toString().trim();
            String totCase = arrayList.get(1).toString().trim().equals("") ? "-" : arrayList.get(1).toString().trim();
            String total_Death = arrayList.get(3).toString().trim().equals("") ? "-" : arrayList.get(3).toString().trim();
            String totalRecovered = arrayList.get(5).toString().trim().equals("") ? "-" : arrayList.get(5).toString().trim();
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View myView = layoutInflater.inflate(R.layout.custom_dialog, null);
            final TextView totalCase = myView.findViewById(R.id.totalInfect);
            final TextView totalDeath = myView.findViewById(R.id.totalDeath);
            final TextView totalRecove = myView.findViewById(R.id.totalRecover);
            final TextView countryNameTV = myView.findViewById(R.id.countryNameTV);
            final ImageView closeDialog = myView.findViewById(R.id.closeDialog);
            //myView.setBackgroundColor(Color.TRANSPARENT);
            builder.setView(myView);
            countryInfoDialog = builder.create();
            countryInfoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            countryNameTV.setText("Corona Current Status Of: " + countryNameTVTxt);
            totalCase.setText(totCase);
            totalDeath.setText(total_Death);
            totalRecove.setText(totalRecovered);
            countryInfoDialog.show();
            closeDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countryInfoDialog.dismiss();
                }
            });

        }catch (Exception ex){
            Log.e("Dialog error",ex.toString());
        }

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
