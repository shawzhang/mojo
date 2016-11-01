package com.example.mghlcs.login.Root;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mghlcs.login.R;

import java.util.ArrayList;

/**
 * Created by mghlcs on 4/14/16.
 */
public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemname;
    private final ArrayList<Integer> imgid;

    public CustomListAdapter(Activity context, ArrayList<String> itemname, ArrayList<Integer> imgid) {
        super(context, R.layout.root_apps, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.root_apps, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.sub_app_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.subapp_icon);
        //TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(itemname.get(position));
        imageView.setImageResource(imgid.get(position));
        //extratxt.setText("Description "+itemname[position]);
        return rowView;

    };
}