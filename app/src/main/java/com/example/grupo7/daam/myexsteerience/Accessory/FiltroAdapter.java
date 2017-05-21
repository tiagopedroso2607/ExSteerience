package com.example.grupo7.daam.myexsteerience.Accessory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.example.grupo7.daam.myexsteerience.R;

import java.util.ArrayList;

/**
 * Created by Tiago on 20/05/2017. retirado de http://android-thirumal-experience.blogspot.pt/2015/07/multi-select-list-view-with-custom.html
 */

    public class FiltroAdapter extends BaseAdapter {

    private String[] cityList;
    private String[] filtro ;
    private Context context;
    private boolean isSelected[];

    public FiltroAdapter(Context context, String[] cityList, String[] filtroSubjects){
        this.context = context;
        this.cityList = cityList;
        this.filtro = filtroSubjects;
        isSelected = new boolean[cityList.length];



        for (int i = 0; i< filtro.length; i++) {
            if (filtro[i].equals(cityList[i])){
                isSelected[i] = true;
            }
        }
    }


    @Override
    public int getCount() {
        return cityList.length;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row_check, null);
            holder = new ViewHolder();
            holder.linearLayout = (LinearLayout) view.findViewById(R.id.linear_row_filtro);
            holder.checkedTextView = (CheckedTextView) view.findViewById(R.id.check1);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.checkedTextView.setText(cityList[position]);



            if (filtro[position].equals(cityList[position])){
                holder.checkedTextView.setChecked(true);
            }


        holder.checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set the check text view
                boolean flag = holder.checkedTextView.isChecked();
                holder.checkedTextView.setChecked(!flag);

                if(!flag){
                    isSelected[position] = true;
                }else{
                    isSelected[position] = false;
                }

            }
        });

        return view;
    }

    public boolean[] getSelectedFlags(){
        return isSelected;
    }

    private class ViewHolder {
        LinearLayout linearLayout;
        CheckedTextView checkedTextView;

    }

}
