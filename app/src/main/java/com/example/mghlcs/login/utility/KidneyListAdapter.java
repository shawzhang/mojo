package com.example.mghlcs.login.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mghlcs.login.Objects.KidneyHotList;
import com.example.mghlcs.login.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by mghlcs on 11/3/16.
 */

public class KidneyListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<KidneyHotList> mDataSource;

    public KidneyListAdapter(Context context, ArrayList<KidneyHotList> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.content_transplant, parent, false);

        // Get title element
        TextView titleTextView =
                (TextView) rowView.findViewById(R.id.patient_list_title);

// Get subtitle element
        TextView subtitleTextView =
                (TextView) rowView.findViewById(R.id.patient_list_subtitle);

// Get thumbnail element
        ImageView thumbnailImageView =
                (ImageView) rowView.findViewById(R.id.patient_list_thumbnail);

//Get Other Organ
        TextView otherOrganTextView =
                (TextView) rowView.findViewById(R.id.other_organs);


        // 1
        KidneyHotList recipe = (KidneyHotList) getItem(position);

        // 2
        titleTextView.setText(recipe.getPatientName());
        subtitleTextView.setText(recipe.HotListSubTitle());
        otherOrganTextView.setText(recipe.getOtherOrgans());

    // 3
    if (recipe.getReadiness().equalsIgnoreCase("Ready")) {
            Picasso.with(mContext).load(R.drawable.ready_state).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView);
        } else if (recipe.getReadiness().equalsIgnoreCase("On-Deck")) {
            Picasso.with(mContext).load(R.drawable.on_deck).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView);
        } else if (recipe.getReadiness().equalsIgnoreCase("Not Ready")) {
            Picasso.with(mContext).load(R.drawable.not_ready).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView);
        }

        return rowView;
    }
}
