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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.agromall.agromall.R;
import com.agromall.agromall.activity.MainActivity;
import com.agromall.agromall.database.DbHelper;
import com.agromall.agromall.model.Farm;
import com.agromall.agromall.model.Farmer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.agromall.agromall.constant.DbContract.FARM_OWNER_ID;
import static com.agromall.agromall.constant.DbContract.FARM_UID;

public class FarmFragment extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private int farmID, farmerID;
    private static final int RequestPermissionCode = 99;
    private DbHelper dbHelper;
    private FragmentActivity activity;
    private LinearLayout editHolder, detailsHolder;
    private Button editFarmBtn;
    private TextView tvFarmName;
    private TextView tvFarmLocation;
    private TextView tvFarmCoordinates;
    private EditText etFarmName, etFarmLocation, etFarmCoordinates;
    private Farm farm;
    private ProgressDialog progressDialog;
    private GoogleMap mMap;
    private Location gps_loc;
    private Location network_loc;

    public static FarmFragment newInstance() {
        return new FarmFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            farmID = bundle.getInt(FARM_UID, 0);
            farmerID = bundle.getInt(FARM_OWNER_ID, 0);
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farm, container, false);

        if (activity == null) activity = getActivity();

        dbHelper = new DbHelper(activity);
        farm = dbHelper.getFarm(farmID, farmerID);

        if (farm.getFarmID() <= 0) {
            ((MainActivity) activity).toastMessage("Sorry, that farm does not exist.");
            ((MainActivity) activity).navController.popBackStack();
            return view;
        }

        MapView mMapView = view.findViewById(R.id.mapView);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminateDrawable(activity.getDrawable(R.drawable.custom_progress_bar_indeterminate));
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating farm info. Please wait...");

        TextView tvRetrieveAutomatically = view.findViewById(R.id.retrieve_automatically);
        TextView tvFarmOwner = view.findViewById(R.id.farmer_name);
        tvFarmName = view.findViewById(R.id.farm_name);
        tvFarmLocation = view.findViewById(R.id.farm_location);
        tvFarmCoordinates = view.findViewById(R.id.farm_coordinates);

        Farmer farmer = dbHelper.getFarmer(farm.getFarmOwnerID());
        tvFarmOwner.setText(String.format("%s %s's Farm", farmer.getLastName(), farmer.getFirstName()));
        tvFarmName.setText(farm.getName());
        tvFarmLocation.setText(farm.getLocation());
        tvFarmCoordinates.setText(farm.getCoordinates());

        editHolder = view.findViewById(R.id.edit_holder);
        editFarmBtn = view.findViewById(R.id.edit_btn);
        detailsHolder = view.findViewById(R.id.details);


        etFarmName = view.findViewById(R.id.et_farm_name);
        etFarmLocation = view.findViewById(R.id.et_farm_location);
        etFarmCoordinates = view.findViewById(R.id.et_farm_coordinates);
        Button btnSave = view.findViewById(R.id.save_btn);
        Button btnCancel = view.findViewById(R.id.cancel_btn);

        etFarmCoordinates.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    updateFarm();
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

        editFarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFarm();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFarm();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeEdit("Cancelled farm update");
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

    private void editFarm() {

        preFillEditText();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(etFarmName, InputMethodManager.SHOW_IMPLICIT);
        editFarmBtn.setVisibility(View.GONE);
        detailsHolder.setVisibility(View.GONE);
        editHolder.setVisibility(View.VISIBLE);
        ((MainActivity) activity).floatingBtn.setVisibility(View.GONE);
    }

    private void closeEdit(String snackMessage) {
        clearFocus();
        editFarmBtn.setVisibility(View.VISIBLE);
        detailsHolder.setVisibility(View.VISIBLE);
        editHolder.setVisibility(View.GONE);
        if (!snackMessage.isEmpty())
            Snackbar.make(requireView(), snackMessage, Snackbar.LENGTH_LONG).show();
    }

    private void preFillEditText() {
        etFarmName.setText(farm.getName());
        etFarmLocation.setText(farm.getLocation());
        etFarmCoordinates.setText(farm.getCoordinates());
    }

    private void clearFocus() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(etFarmName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etFarmLocation.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etFarmCoordinates.getWindowToken(), 0);
    }

    private void updateFarm() {

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

            clearFocus();

            toggleProgress(true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    toggleProgress(false);
                    Farm farm = new Farm();
                    farm.setFarmID(farmID);
                    farm.setFarmOwnerID(farmerID);
                    farm.setName(farmName);
                    farm.setLocation(farmLocation);
                    farm.setCoordinates(farmCoordinates);

                    if (dbHelper.updateFarm(farm)) {

                        updateFarm(farm);
                        tvFarmName.setText(farm.getName());
                        tvFarmLocation.setText(farm.getLocation());
                        tvFarmCoordinates.setText(farm.getCoordinates());
                        setMap();

                        closeEdit("Farm info updated successfully");

                    } else toastMessage("Failed to update farm info at this time");

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

    private void updateFarm(Farm farm) {

        this.farm = farm;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        setMap();
    }

    private void requestPermission() {
        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, RequestPermissionCode);
    }

    private void setMap() {

        if (farm.getCoordinates().equals("") || !farm.getCoordinates().contains(",")) return;

        String[] coordinates = farm.getCoordinates().split(",");

        LatLng point = new LatLng(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]));

        if (mMap != null) {

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
                return;
            }

            MapsInitializer.initialize(activity);
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            mMap.clear();
            Marker marker = mMap.addMarker(new MarkerOptions().position(point).title("Farm Location"));
            marker.showInfoWindow();
            CameraPosition cameraPosition = new CameraPosition.Builder().target(point).zoom(16).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.setMaxZoomPreference(50);
        }
    }
}