package com.agromall.agromall.fragment.farmer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.agromall.agromall.R;
import com.agromall.agromall.activity.MainActivity;
import com.agromall.agromall.database.DbHelper;
import com.agromall.agromall.model.Farmer;
import com.agromall.agromall.utility.CircleTransform;
import com.agromall.agromall.utility.Utility;
import com.agromall.agromall.utility.Validator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;


import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.agromall.agromall.constant.DbContract.FARMER_UID;

public class FarmerFragment extends Fragment {

    private int farmerID;
    private DbHelper dbHelper;
    private Activity activity;
    private LinearLayout editHolder, detailsHolder;
    private Button editProfileBtn;
    private TextView tvName, tvFirstname, tvMiddlename, tvLastname, tvEmail, tvPhone, tvGender, tvAddress;
    private ImageView mvPicture, pickImageBtn;
    private EditText etFirstname, etMiddlename, etLastname, etEmail, etPhone, etAddress;
    private String gender;
    private Spinner spGender;
    private Bitmap img;
    private Farmer farmer;
    private ProgressDialog progressDialog;

    public static FarmerFragment newInstance() {
        return new FarmerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) farmerID = bundle.getInt(FARMER_UID, 0);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer, container, false);

        if (activity == null) activity = getActivity();

        dbHelper = new DbHelper(activity);
        farmer = dbHelper.getFarmer(farmerID);

        if (farmer.getFarmerID() <= 0) {
            ((MainActivity) activity).toastMessage("Sorry, that farmer does not exist.");
            ((MainActivity) activity).navController.popBackStack();
            return view;
        }

        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminateDrawable(activity.getDrawable(R.drawable.custom_progress_bar_indeterminate));
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating farmer info. Please wait...");

        mvPicture = view.findViewById(R.id.profile_pic);
        tvName = view.findViewById(R.id.name);
        tvFirstname = view.findViewById(R.id.first_name);
        tvMiddlename = view.findViewById(R.id.middle_name);
        tvLastname = view.findViewById(R.id.last_name);
        tvEmail = view.findViewById(R.id.email);
        tvPhone = view.findViewById(R.id.phone);
        tvGender = view.findViewById(R.id.gender);
        tvAddress = view.findViewById(R.id.address);

        tvName.setText(String.format("%s %s", farmer.getLastName(), farmer.getFirstName()));
        tvFirstname.setText(farmer.getFirstName());
        tvMiddlename.setText(farmer.getMiddleName());
        tvLastname.setText(farmer.getLastName());
        tvEmail.setText(farmer.getEmail());
        tvPhone.setText(farmer.getPhone());
        tvGender.setText(farmer.getGender());
        tvAddress.setText(farmer.getAddress());
        gender = farmer.getGender();
        img = Utility.toBitmap(farmer.getPicture());

        // Loading profile image
        Glide.with(activity).load(farmer.getPicture())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(activity))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mvPicture);

        editHolder = view.findViewById(R.id.edit_holder);
        editProfileBtn = view.findViewById(R.id.edit_profile_btn);
        pickImageBtn = view.findViewById(R.id.pick_image_btn);
        detailsHolder = view.findViewById(R.id.details);


        etFirstname = view.findViewById(R.id.etFirstname);
        etMiddlename = view.findViewById(R.id.etMiddlename);
        etLastname = view.findViewById(R.id.etLastname);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        spGender = view.findViewById(R.id.spGender);
        etAddress = view.findViewById(R.id.etAddress);
        Button btnSave = view.findViewById(R.id.save_btn);
        Button btnCancel = view.findViewById(R.id.cancel_btn);


        etAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    updateFarmer();
                    return true;
                }
                return false;
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                R.array.genderType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spGender.setAdapter(adapter);

        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) gender = "Male";
                else if (position == 2) gender = "Female";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pickImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFarmerImage();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFarmer();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeEdit("Cancelled profile update");
            }
        });

        ((MainActivity) activity).floatingBtn.setVisibility(View.VISIBLE);
        ((MainActivity) activity).floatingBtn.setText(getString(R.string.view_farms));
        ((MainActivity) activity).floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt(FARMER_UID, farmerID);
                ((MainActivity) activity).loadFragment(R.id.nav_farms, bundle);
            }
        });

        return view;
    }

    private void editProfile() {

        preFillEditText();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(etFirstname, InputMethodManager.SHOW_IMPLICIT);
        editProfileBtn.setVisibility(View.GONE);
        detailsHolder.setVisibility(View.GONE);
        pickImageBtn.setVisibility(View.VISIBLE);
        editHolder.setVisibility(View.VISIBLE);
        ((MainActivity) activity).floatingBtn.setVisibility(View.GONE);
    }

    private void closeEdit(String snackMessage) {
        clearFocus();
        editProfileBtn.setVisibility(View.VISIBLE);
        detailsHolder.setVisibility(View.VISIBLE);
        pickImageBtn.setVisibility(View.GONE);
        editHolder.setVisibility(View.GONE);
        ((MainActivity) activity).floatingBtn.setVisibility(View.VISIBLE);
        if (!snackMessage.isEmpty())
            Snackbar.make(requireView(), snackMessage, Snackbar.LENGTH_LONG).show();
    }

    private void preFillEditText() {
        etFirstname.setText(farmer.getFirstName());
        etMiddlename.setText(farmer.getMiddleName());
        etLastname.setText(farmer.getLastName());
        etPhone.setText(farmer.getPhone());
        etEmail.setText(farmer.getEmail());
        etAddress.setText(farmer.getAddress());
        spGender.setSelection(Objects.equals(farmer.getGender(), "Male") ? 1 : 2);
    }

    private void clearFocus() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(etFirstname.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etMiddlename.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etLastname.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etPhone.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etEmail.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etAddress.getWindowToken(), 0);
    }

    private void pickFarmerImage() {

        try {
            PackageManager packageManager = activity.getPackageManager();
            boolean hasCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);

            if (hasCamera) {
                // start the image capture Intent
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, intent.getData());
                startActivityForResult(intent, 1800);

            } else toastMessage("This device seems not to have a camera");
        } catch (Exception ex) {

            toastMessage("There was an error with the camera.");
        }

    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            toastMessage("Sorry an unexpected error occurred, please try again later.");
            return;
        }

        if (requestCode == 1800 && data != null) {

            // The picture was returned
            Bundle extras = data.getExtras();
            assert extras != null;
            img = (Bitmap) extras.get("data");
            mvPicture.setImageBitmap(img);

        }

    }

    private void updateFarmer() {

        final String firstName = etFirstname.getText().toString();
        final String middleName = etMiddlename.getText().toString();
        final String lastName = etLastname.getText().toString();
        final String email = etEmail.getText().toString();
        final String phone = etPhone.getText().toString();
        final String address = etAddress.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (isInValidInput(firstName)) {
            etFirstname.setError(getString(R.string.error_invalid_firstname));
            focusView = etFirstname;
            cancel = true;
        }

        else if (isInValidInput(middleName)) {
            etMiddlename.setError(getString(R.string.error_invalid_middlename));
            focusView = etMiddlename;
            cancel = true;
        }

        else if (isInValidInput(lastName)) {
            etLastname.setError(getString(R.string.error_invalid_lastname));
            focusView = etLastname;
            cancel = true;
        }

        else if (isInValidInput(email, 4) || !Validator.isValidEmail(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = true;
        }

        else if (dbHelper.farmerEmailExist(email, farmer.getFarmerID())) {
            etEmail.setError("That email is already taken");
            focusView = etEmail;
            cancel = true;
        }

        else if (isInValidInput(phone, 11)) {
            etPhone.setError(getString(R.string.error_invalid_phone));
            focusView = etPhone;
            cancel = true;
        }

        else if (dbHelper.farmerPhoneExist(phone, farmer.getFarmerID())) {
            etPhone.setError("That phone number is already taken");
            focusView = etPhone;
            cancel = true;
        }

        else if (isInValidInput(address, 5)) {
            etAddress.setError(getString(R.string.error_invalid_address));
            focusView = etAddress;
            cancel = true;
        }

        else if (!Objects.equals(gender, "Male") && !Objects.equals(gender, "Female")) {
            focusView = spGender;
            toastMessage("Please select farmer gender");
            cancel = true;
        }

        else if (img == null) {
            toastMessage("Please capture farmer picture");
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
                    Farmer farmer = new Farmer();
                    farmer.setFarmerID(farmerID);
                    farmer.setFirstName(firstName);
                    farmer.setMiddleName(middleName);
                    farmer.setLastName(lastName);
                    farmer.setEmail(email);
                    farmer.setPhone(phone);
                    farmer.setGender(gender);
                    farmer.setAddress(address);
                    farmer.setPicture(Utility.toByteArray(img));

                    if (dbHelper.updateFarmer(farmer)) {

                        updateFarmer(farmer);
                        tvName.setText(String.format("%s %s", farmer.getLastName(), farmer.getFirstName()));
                        tvFirstname.setText(farmer.getFirstName());
                        tvMiddlename.setText(farmer.getMiddleName());
                        tvLastname.setText(farmer.getLastName());
                        tvEmail.setText(farmer.getEmail());
                        tvPhone.setText(farmer.getPhone());
                        tvGender.setText(farmer.getGender());
                        tvAddress.setText(farmer.getAddress());

                        // Loading profile image
                        Glide.with(activity).load(farmer.getPicture())
                                .crossFade()
                                .thumbnail(0.5f)
                                .bitmapTransform(new CircleTransform(activity))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(mvPicture);

                        closeEdit("Farmer info updated successfully");

                    } else toastMessage("Failed to update farmer info at this time");

                }
            }, 1500);
        }
    }

    private boolean isInValidInput(String input) {
        return isInValidInput(input, 2);
    }

    private boolean isInValidInput(String input, int length) {
        return input.length() < length;
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

    private void updateFarmer(Farmer farmer) {

        this.farmer = farmer;
    }
}