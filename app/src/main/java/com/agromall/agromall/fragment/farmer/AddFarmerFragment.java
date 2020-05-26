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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.agromall.agromall.R;
import com.agromall.agromall.activity.MainActivity;
import com.agromall.agromall.database.DbHelper;
import com.agromall.agromall.model.Farmer;
import com.agromall.agromall.utility.Utility;
import com.agromall.agromall.utility.Validator;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class AddFarmerFragment extends Fragment {

    private ImageView profileImg;
    private EditText etFirstname, etMiddlename, etLastname, etEmail, etPhone, etAddress;
    private Spinner spGender;
    private Activity activity;
    private Bitmap img;
    private String gender;
    private ProgressDialog progressDialog;
    private DbHelper dbHelper;

    public static AddFarmerFragment newInstance() {
        return new AddFarmerFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_farmer, container, false);
        profileImg = view.findViewById(R.id.profile_pic);
        ImageView pickImageBtn = view.findViewById(R.id.pick_image_btn);
        etFirstname = view.findViewById(R.id.firstname);
        etMiddlename = view.findViewById(R.id.middlename);
        etLastname = view.findViewById(R.id.lastname);
        etEmail = view.findViewById(R.id.email);
        etPhone = view.findViewById(R.id.phone);
        spGender = view.findViewById(R.id.gender);
        etAddress = view.findViewById(R.id.address);
        Button btnSave = view.findViewById(R.id.save_btn);

        if (activity == null) activity = getActivity();

        dbHelper = new DbHelper(activity);

        progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminateDrawable(activity.getDrawable(R.drawable.custom_progress_bar_indeterminate));
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving farmer. Please wait...");


        etAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    saveFarmer();
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFarmer();
            }
        });
        return view;
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
            profileImg.setImageBitmap(img);

        }

    }

    private void saveFarmer() {

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

        else if (dbHelper.farmerEmailExist(email)) {
            etEmail.setError("That email is already taken");
            focusView = etEmail;
            cancel = true;
        }

        else if (isInValidInput(phone, 11)) {
            etPhone.setError(getString(R.string.error_invalid_phone));
            focusView = etPhone;
            cancel = true;
        }

        else if (dbHelper.farmerPhoneExist(phone)) {
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

            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(etFirstname.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etMiddlename.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etLastname.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etPhone.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etAddress.getWindowToken(), 0);

            toggleProgress(true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toggleProgress(false);
                    Farmer farmer = new Farmer();
                    farmer.setFirstName(firstName);
                    farmer.setMiddleName(middleName);
                    farmer.setLastName(lastName);
                    farmer.setEmail(email);
                    farmer.setPhone(phone);
                    farmer.setGender(gender);
                    farmer.setAddress(address);
                    farmer.setPicture(Utility.toByteArray(img));
                    toastMessage(dbHelper.saveFarmer(farmer) ? "Farmer saved successfully" : "Failed to save farmer at this time");
                    ((MainActivity) activity).navController.popBackStack();
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
}