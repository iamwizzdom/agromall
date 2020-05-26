package com.agromall.agromall.fragment.farmer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.agromall.agromall.R;
import com.agromall.agromall.activity.MainActivity;
import com.agromall.agromall.adapter.FarmersAdapter;
import com.agromall.agromall.database.DbHelper;
import com.agromall.agromall.interfaces.OnCustomDialogClickListener;
import com.agromall.agromall.model.Farmer;
import com.agromall.agromall.utility.CustomAlertDialog;

import java.util.ArrayList;

public class FarmersFragment extends Fragment {

    private LinearLayout contentHolder, noRecordHolder;
    private Activity activity;
    private ArrayList<Farmer> farmers = new ArrayList<>();
    private ProgressDialog progressDialog;
    private DbHelper dbHelper;
    private FarmersAdapter farmersListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_item_list, container, false);
        ListView listView = view.findViewById(R.id.farmers_list_view);
        if (activity == null) activity = getActivity();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminateDrawable(activity.getDrawable(R.drawable.custom_progress_bar_indeterminate));
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Deleting farmer. Please wait...");

        contentHolder = view.findViewById(R.id.content_holder);
        noRecordHolder = view.findViewById(R.id.no_record_holder);
        farmersListAdapter = new FarmersAdapter(activity, this, farmers);
        listView.setAdapter(farmersListAdapter);

        dbHelper = new DbHelper(activity);
        farmers.clear();
        farmers.addAll(dbHelper.getFarmers());
        farmersListAdapter.notifyDataSetChanged();

        ((MainActivity) activity).fab.show();
        ((MainActivity) activity).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) activity).loadFragment(R.id.nav_add_farmer);
            }
        });
        return view;
    }

    public void deleteFarmer(final int farmerID) {

        CustomAlertDialog dialog = new CustomAlertDialog(activity);
        dialog.setTitle("Delete Farmer");
        dialog.setMessage("Are you sure you want to delete this farmer?");
        dialog.setPositiveButton("Yes, proceed", new OnCustomDialogClickListener() {
            @Override
            public void onClick(CustomAlertDialog dialog) {
                toggleProgress(true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        toggleProgress(false);
                        if (dbHelper.deleteFarmer(farmerID)) {
                            removeFarmerFromList(farmerID);
                            toastMessage("Farmer deleted successfully.");
                        } else toastMessage("Failed to delete farmer at this time.");
                    }
                }, 1500);
                dialog.hide();
            }
        });
        dialog.setNegativeButton("No, cancel", null);
        dialog.show();
    }

    private void removeFarmerFromList(int farmerID) {
        for (int i = 0; i < farmers.size(); i++) {
            Farmer farm = farmers.get(i);
            if (farm.getFarmerID() == farmerID) {
                farmers.remove(i);
                farmersListAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public void contentIsEmpty(boolean status) {
        if (status) {
            contentHolder.setVisibility(View.GONE);
            noRecordHolder.setVisibility(View.VISIBLE);
        } else {
            noRecordHolder.setVisibility(View.GONE);
            contentHolder.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Shows the progress UI.
     */
    private void toggleProgress(final boolean show) {
        if (show) progressDialog.show();
        else progressDialog.dismiss();
    }

    private void toastMessage(String message) {

        ((MainActivity) activity).toastMessage(message);
    }
}