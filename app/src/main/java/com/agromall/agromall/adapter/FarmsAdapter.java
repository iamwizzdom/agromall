package com.agromall.agromall.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;

import com.agromall.agromall.R;
import com.agromall.agromall.activity.MainActivity;
import com.agromall.agromall.fragment.farms.FarmsFragment;
import com.agromall.agromall.model.Farm;
import com.agromall.agromall.model.Farmer;
import com.agromall.agromall.utility.CircleTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Objects;

import static com.agromall.agromall.constant.DbContract.FARMER_UID;
import static com.agromall.agromall.constant.DbContract.FARM_OWNER_ID;
import static com.agromall.agromall.constant.DbContract.FARM_UID;


/**
 * Created by Wisdom Emenike.
 * Date: 6/15/2017
 * Time: 12:33 AM
 */

public class FarmsAdapter extends BaseAdapter {

    private final Context mContext;
    private final FarmsFragment farmsFragment;
    private final List<Farm> farms;

    //Constructor

    public FarmsAdapter(Context context, FarmsFragment farmsFragment, List<Farm> farms) {
        this.mContext = context;
        this.farmsFragment = farmsFragment;
        this.farms = farms;
    }

    @Override
    public int getCount() {
        return farms.size();
    }

    @Override
    public Object getItem(int position) {
        return farms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View v = View.inflate(mContext, R.layout.fragment_farm_item, null);

        TextView farmName = v.findViewById(R.id.farm_name);
        TextView farmLocation = v.findViewById(R.id.farm_location);
        ImageView optionBtn = v.findViewById(R.id.option_btn);

        //set text for TextView
        farmName.setText(farms.get(position).getName());
        farmLocation.setText(farms.get(position).getLocation());

        optionBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ViewHolder")
            @Override
            public void onClick(View v) {

                final PopupMenu popup = new PopupMenu(mContext, v);

                popup.inflate(R.menu.item_options);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.view_item:

                                Bundle bundle = new Bundle();
                                bundle.putInt(FARM_UID, farms.get(position).getFarmID());
                                bundle.putInt(FARM_OWNER_ID, farms.get(position).getFarmOwnerID());
                                ((MainActivity) mContext).loadFragment(R.id.nav_farm, bundle);

                                break;
                            case R.id.delete_item:

                                farmsFragment.deleteFarm(farms.get(position).getFarmID());
                                break;
                            default:
                                popup.dismiss();
                                break;
                        }
                        return false;
                    }
                });

                popup.show();

            }
        });

        return v;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Objects.requireNonNull(((MainActivity) mContext).getSupportActionBar()).setSubtitle(getCount() > 0 ? getCount() + " Farm(s) found" : "");
        farmsFragment.contentIsEmpty(!(getCount() > 0));
    }
}
