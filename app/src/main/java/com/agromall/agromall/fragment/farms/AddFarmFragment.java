package com.agromall.agromall.fragment.farms;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.agromall.agromall.R;
import com.agromall.agromall.activity.MainActivity;
import com.agromall.agromall.database.DbHelper;
import com.agromall.agromall.model.Farm;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.agromall.agromall.constant.DbContract.FARM_OWNER_ID;

public class AddFarmFragment extends Fragment {

    private int farmerID;
    private EditText etFarmName, etFarmLocation, etFarmCoordinates;
    private FragmentActivity activity;
    private ProgressDialog progressDialog;
    private DbHelper dbHelper;
    private static final int RequestPermissionCode = 99;

    private Location gps_loc;
    private Location network_loc;

    public static AddFarmFragment newInstance() {
        return new AddFarmFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            farmerID = bundle.getInt(FARM_OWNER_ID, 0);
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_farm, container, false);

        etFarmName = view.findViewById(R.id.farm_name);
        etFarmLocation = view.findViewById(R.id.farm_location);
        etFarmCoordinates = view.findViewById(R.id.farm_coordinates);
        TextView tvRetrieveAutomatically = view.findViewById(R.id.retrieve_automatically);
        Button btnSave = view.findViewById(R.id.save_btn);

        if (activity == null) activity = getActivity();

        dbHelper = new DbHelper(activity);

        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminateDrawable(activity.getDrawable(R.drawable.custom_progress_bar_indeterminate));
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving farm. Please wait...");

        etFarmCoordinates.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    saveFarm();
                    return true;
                }
                return false;
            }
        });

        tvRetrieveAutomatically.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                retrieveCoordinates();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFarm();
            }
        });
        return view;
    }

    private void retrieveCoordinates() {

        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        try {

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
                return;
            }

            if (locationManager != null) {
                gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (locationManager != null) {
                network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        double longitude;
        double latitude;

        if (gps_loc != null) {
            latitude = gps_loc.getLatitude();
            longitude = gps_loc.getLongitude();
        } else if (network_loc != null) {
            latitude = network_loc.getLatitude();
            longitude = network_loc.getLongitude();
        } else {
            latitude = 0.0;
            longitude = 0.0;
        }

        etFarmCoordinates.setText(String.format("%s,%s", latitude, longitude));
        etFarmCoordinates.requestFocus();
    }

    private void requestPermission() {
        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, RequestPermissionCode);
    }

    private void saveFarm() {

        final String farmName = etFarmName.getText().toString();
        final String farmLocation = etFarmLocation.getText().toString();
        final String farmCoordinates = etFarmCoordinates.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (isInValidInput(farmName)) {
            etFarmName.setError(getString(R.string.error_invalid_farm_name));
            focusView = etFarmName;
            cancel = true;
        }

        else if (isInValidInput(farmLocation)) {
            etFarmLocation.setError(getString(R.string.error_invalid_farm_location));
            focusView = etFarmLocation;
            cancel = true;
        }

        else if (isInValidInput(farmCoordinates, true)) {
            etFarmCoordinates.setError(getString(R.string.error_invalid_coordinates));
            focusView = etFarmCoordinates;
            cancel = true;
        }


        if (cancel) {
            // There was an error;
            // focus form field with an error.
            if (focusView != null) focusView.requestFocus();
        } else {

            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(etFarmName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etFarmLocation.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etFarmCoordinates.getWindowToken(), 0);

            toggleProgress(true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toggleProgress(false);
                    Farm farm = new Farm();
                    farm.setFarmOwnerID(farmerID);
                    farm.setName(farmName);
                    farm.setLocation(farmLocation);
                    farm.setCoordinates(farmCoordinates);
                    toastMessage(dbHelper.saveFarm(farm) ? "Farm saved successfully" : "Failed to save farm at this time");
                    ((MainActivity) activity).navController.popBackStack();
                }
            }, 1500);
        }
    }

    private boolean isInValidInput(String input) {
        return isInValidInput(input, false);
    }

    private boolean isInValidInput(String input, boolean isCoordinate) {
        if (!isCoordinate) return input.length() < 2;
        else return input.length() < 3 || !input.contains(",") ||
                !input.matches("-?[1-9][0-9]*(\\.[0-9]+)?,\\s*-?[1-9][0-9]*(\\.[0-9]+)?");
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