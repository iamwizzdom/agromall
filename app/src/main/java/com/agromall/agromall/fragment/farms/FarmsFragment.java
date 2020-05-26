package com.agromall.agromall.fragment.farms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.agromall.agromall.R;
import com.agromall.agromall.activity.MainActivity;
import com.agromall.agromall.adapter.FarmsAdapter;
import com.agromall.agromall.database.DbHelper;
import com.agromall.agromall.interfaces.OnCustomDialogClickListener;
import com.agromall.agromall.model.Farm;
import com.agromall.agromall.utility.CustomAlertDialog;

import java.util.ArrayList;

import static com.agromall.agromall.constant.DbContract.FARMER_UID;
import static com.agromall.agromall.constant.DbContract.FARM_OWNER_ID;

public class FarmsFragment extends Fragment {

    private int farmerID;
    private LinearLayout contentHolder, noRecordHolder;
    private Activity activity;
    private ArrayList<Farm> farms = new ArrayList<>();
    private DbHelper dbHelper;
    private ProgressDialog progressDialog;
    private FarmsAdapter farmsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            farmerID = bundle.getInt(FARMER_UID, 0);
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_item_list, container, false);
        ListView listView = view.findViewById(R.id.farmers_list_view);
        if (activity == null) activity = getActivity();

        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminateDrawable(activity.getDrawable(R.drawable.custom_progress_bar_indeterminate));
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Deleting farm. Please wait...");

        contentHolder = view.findViewById(R.id.content_holder);
        noRecordHolder = view.findViewById(R.id.no_record_holder);
        farmsAdapter = new FarmsAdapter(activity, this, farms);
        listView.setAdapter(farmsAdapter);

        dbHelper = new DbHelper(activity);
        farms.clear();
        farms.addAll(dbHelper.getFarms(farmerID, 0));
        farmsAdapter.notifyDataSetChanged();

        ((MainActivity) activity).fab.show();
        ((MainActivity) activity).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(FARM_OWNER_ID, farmerID);
                ((MainActivity) activity).loadFragment(R.id.nav_add_farm, bundle);
            }
        });

        return view;
    }

    public void deleteFarm(final int farmID) {

        CustomAlertDialog dialog = new CustomAlertDialog(activity);
        dialog.setTitle("Delete Farm");
        dialog.setMessage("Are you sure you want to delete this farm?");
        dialog.setPositiveButton("Yes, proceed", new OnCustomDialogClickListener() {
            @Override
            public void onClick(CustomAlertDialog dialog) {
                toggleProgress(true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        toggleProgress(false);
                        if (dbHelper.deleteFarm(farmID)) {
                            removeFarmFromList(farmID);
                            toastMessage("Farm deleted successfully.");
                        } else toastMessage("Failed to delete farm at this time.");
                    }
                }, 1500);
                dialog.hide();
            }
        });
        dialog.setNegativeButton("No, cancel", null);
        dialog.show();
    }

    private void removeFarmFromList(int farmID) {
        for (int i = 0; i < farms.size(); i++) {
            Farm farm = farms.get(i);
            if (farm.getFarmID() == farmID) {
                farms.remove(i);
                farmsAdapter.notifyDataSetChanged();
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