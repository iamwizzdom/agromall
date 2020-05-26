package com.agromall.agromall.fragment.dashboad;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;

import com.agromall.agromall.R;
import com.agromall.agromall.activity.MainActivity;
import com.agromall.agromall.database.DbHelper;
import com.agromall.agromall.model.Farm;
import com.agromall.agromall.model.Farmer;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private Activity activity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        TextView tvTotalFarmers = view.findViewById(R.id.total_farmers);
        TextView tvTotalFarms = view.findViewById(R.id.total_farms);

        TableLayout recentFarmers = view.findViewById(R.id.recent_farmers);
        TableLayout recentFarms = view.findViewById(R.id.recent_farms);

        Button viewAllFarmerBtn = view.findViewById(R.id.view_all_farmers);

        if (activity == null) activity = getActivity();

        viewAllFarmerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).loadFragment(R.id.nav_farmers);
            }
        });

        DbHelper dbHelper = new DbHelper(activity);

        tvTotalFarmers.setText(String.valueOf(dbHelper.countFarmers()));
        tvTotalFarms.setText(String.valueOf(dbHelper.countFarms()));

        int count = 0;

        for (Farmer farmer: dbHelper.getFarmers(10)) {

            TableRow tableRow = new TableRow(activity);

            TextView tvCount = new TextView(new ContextThemeWrapper(activity,
                    R.style.text_view_style), null, 0);
            tvCount.setText(String.valueOf(++count));
            setTextViewStyle(tvCount, 1);
            tableRow.addView(tvCount);

            TextView tvFarmerName = new TextView(new ContextThemeWrapper(activity,
                    R.style.text_view_style), null, 0);
            tvFarmerName.setText(String.format("%s %s", farmer.getLastName(), farmer.getFirstName()));
            setTextViewStyle(tvFarmerName, 2);
            tableRow.addView(tvFarmerName);

            TextView tvFarmerEmail = new TextView(new ContextThemeWrapper(activity,
                    R.style.text_view_style), null, 0);
            tvFarmerEmail.setText(farmer.getEmail());
            setTextViewStyle(tvFarmerEmail, 3);
            tableRow.addView(tvFarmerEmail);

            TextView tvFarmerPhone = new TextView(new ContextThemeWrapper(activity,
                    R.style.text_view_style), null, 0);
            tvFarmerPhone.setText(farmer.getPhone());
            setTextViewStyle(tvFarmerPhone, 4);
            tableRow.addView(tvFarmerPhone);

            TextView tvFarmerGender = new TextView(new ContextThemeWrapper(activity,
                    R.style.text_view_style), null, 0);
            tvFarmerGender.setText(farmer.getGender());
            setTextViewStyle(tvFarmerGender, 5);
            tableRow.addView(tvFarmerGender);

            recentFarmers.addView(tableRow);
        }

        count = 0;

        for (Farm farm: dbHelper.getFarms(10)) {

            TableRow tableRow = new TableRow(activity);

            TextView tvCount = new TextView(new ContextThemeWrapper(activity,
                    R.style.text_view_style), null, 0);
            tvCount.setText(String.valueOf(++count));
            setTextViewStyle(tvCount, 1);
            tableRow.addView(tvCount);

            TextView tvFarmName = new TextView(new ContextThemeWrapper(activity,
                    R.style.text_view_style), null, 0);
            tvFarmName.setText(farm.getName());
            setTextViewStyle(tvFarmName, 2);
            tableRow.addView(tvFarmName);

            TextView tvFarmOwner = new TextView(new ContextThemeWrapper(activity,
                    R.style.text_view_style), null, 0);
            Farmer farmer = dbHelper.getFarmer(farm.getFarmOwnerID());
            tvFarmOwner.setText(String.format("%s %s", farmer.getLastName(), farmer.getFirstName()));
            setTextViewStyle(tvFarmOwner, 3);
            tableRow.addView(tvFarmOwner);

            TextView tvFarmLocation = new TextView(new ContextThemeWrapper(activity,
                    R.style.text_view_style), null, 0);
            tvFarmLocation.setText(farm.getLocation());
            setTextViewStyle(tvFarmLocation, 4);
            tableRow.addView(tvFarmLocation);

            recentFarms.addView(tableRow);
        }

        return view;
    }

    private void setTextViewStyle(TextView textView, int column) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.column = column;
        textView.setLayoutParams(params);
        textView.setPadding(10, 30, 10, 0);
        textView.setTextSize(14);
    }
}