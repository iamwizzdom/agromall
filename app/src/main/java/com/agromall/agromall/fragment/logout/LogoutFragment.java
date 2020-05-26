package com.agromall.agromall.fragment.logout;

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
import com.agromall.agromall.adapter.FarmsAdapter;
import com.agromall.agromall.database.DbHelper;
import com.agromall.agromall.interfaces.OnCustomDialogClickListener;
import com.agromall.agromall.model.Farm;
import com.agromall.agromall.utility.CustomAlertDialog;

import java.util.ArrayList;

import static com.agromall.agromall.constant.DbContract.FARMER_UID;
import static com.agromall.agromall.constant.DbContract.FARM_OWNER_ID;

public class LogoutFragment extends Fragment {

    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (activity == null) activity = getActivity();

        if (activity != null) {
            ((MainActivity) activity).logout();
        }
    }

}