package com.agromall.agromall.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import com.agromall.agromall.R;
import com.agromall.agromall.activity.MainActivity;
import com.agromall.agromall.fragment.farmer.FarmersFragment;
import com.agromall.agromall.model.Farmer;
import com.agromall.agromall.utility.CircleTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Objects;

import static com.agromall.agromall.constant.DbContract.FARMER_UID;


/**
 * Created by Wisdom Emenike.
 * Date: 6/15/2017
 * Time: 12:33 AM
 */

public class FarmersAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Farmer> farmers;
    private FarmersFragment farmersFragment;

    //Constructor

    public FarmersAdapter(Context context, FarmersFragment farmersFragment, List<Farmer> farmers) {
        this.mContext = context;
        this.farmersFragment = farmersFragment;
        this.farmers = farmers;
    }

    @Override
    public int getCount() {
        return farmers.size();
    }

    @Override
    public Object getItem(int position) {
        return farmers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View v = View.inflate(mContext, R.layout.fragment_farmer_item, null);

        ImageView farmerPicture = v.findViewById(R.id.farmer_picture);
        TextView farmerName = v.findViewById(R.id.farmer_name);
        TextView farmerEmail = v.findViewById(R.id.farmer_email);
        TextView farmerPhone = v.findViewById(R.id.farmer_phone);
        ImageView optionBtn = v.findViewById(R.id.option_btn);

        // Loading profile image
        Glide.with(mContext).load(farmers.get(position).getPicture())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(farmerPicture);

        //set text for TextView
        farmerName.setText(String.format("%s %s", farmers.get(position).getLastName(),
                farmers.get(position).getFirstName()));
        farmerEmail.setText(farmers.get(position).getEmail());
        farmerPhone.setText(farmers.get(position).getPhone());

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
                                bundle.putInt(FARMER_UID, farmers.get(position).getFarmerID());
                                ((MainActivity) mContext).loadFragment(R.id.nav_farmer, bundle);

                                break;
                            case R.id.delete_item:

                                farmersFragment.deleteFarmer(farmers.get(position).getFarmerID());
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
        farmersFragment.contentIsEmpty(!(getCount() > 0));
    }
}
