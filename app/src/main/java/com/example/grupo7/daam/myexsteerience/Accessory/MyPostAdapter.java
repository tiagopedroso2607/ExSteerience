package com.example.grupo7.daam.myexsteerience.Accessory;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.grupo7.daam.myexsteerience.MainActivity;
import com.example.grupo7.daam.myexsteerience.Objects.Post;
import com.example.grupo7.daam.myexsteerience.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Tiago on 12/04/2017.
 */

public class MyPostAdapter extends BaseAdapter {


    private final ArrayList<Post> data;
    LayoutInflater inflater;
    Context context;

    public MyPostAdapter(MainActivity mainActivity, ArrayList<Post> data) {
        this.data = data;
        this.context = mainActivity;
        inflater = LayoutInflater.from(context);
    }

    @Override public int getCount() {
        return data.size();
    }

    @Override public Post getItem(int position) {
        return data.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        MyPostAdapter.MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_post, parent, false);
            mViewHolder = new MyPostAdapter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyPostAdapter.MyViewHolder) convertView.getTag();
        }

        Post currentListData = getItem(position);

        String dataPost = currentListData.getData();
        String tituloPost = currentListData.getTitulo();
        String localPost = currentListData.getCidade();

        mViewHolder.localRow.setText(localPost );
        mViewHolder.tituloRow.setText(tituloPost);

        if (verifyData(dataPost)){
            mViewHolder.ActivePostRow.setText("Close ");
            mViewHolder.ActivePostRow.setTextColor(Color.RED);
        }else{
            mViewHolder.ActivePostRow.setText("Open ");
            mViewHolder.ActivePostRow.setTextColor(Color.GREEN);
        }


        String categoriaPost = currentListData.getCategoria();


        if(categoriaPost.equals(context.getString(R.string.anuncio_subject_Sun)) || categoriaPost.equals("Sun and Sea")){
            mViewHolder.img.setImageResource(R.drawable.sunsea);
        }else if(categoriaPost.equals(context.getString(R.string.anuncio_subject_Gastronomy))|| categoriaPost.equals("Gastronomy and Wine")){
            mViewHolder.img.setImageResource(R.drawable.food);
        }else if(categoriaPost.equals(context.getString(R.string.anuncio_subject_culture))|| categoriaPost.equals("Art and Culture")){
            mViewHolder.img.setImageResource(R.drawable.artculture);
        }else if(categoriaPost.equals(context.getString(R.string.anuncio_subject_Family))|| categoriaPost.equals("Family")){
            mViewHolder.img.setImageResource(R.drawable.family);
        }else if(categoriaPost.equals(context.getString(R.string.anuncio_subject_Sports))|| categoriaPost.equals("Sports")){
            mViewHolder.img.setImageResource(R.drawable.desporto);
        }else if(categoriaPost.equals(context.getString(R.string.anuncio_subject_Nature))|| categoriaPost.equals("Nature")){
            mViewHolder.img.setImageResource(R.drawable.natureza);
        }else if(categoriaPost.equals(context.getString(R.string.anuncio_subject_Romance))|| categoriaPost.equals("Romance")){
            mViewHolder.img.setImageResource(R.drawable.romance);
        }else if(categoriaPost.equals(context.getString(R.string.anuncio_subject_Outdoors))|| categoriaPost.equals("Outdoors")){
            mViewHolder.img.setImageResource(R.drawable.outdoors);
        }else if(categoriaPost.equals(context.getString(R.string.anuncio_subject_Well_being))|| categoriaPost.equals("Well-being")){
            mViewHolder.img.setImageResource(R.drawable.sauna);
        }else if(categoriaPost.equals(context.getString(R.string.anuncio_subject_Retail))|| categoriaPost.equals("Retail")){
            mViewHolder.img.setImageResource(R.drawable.price_tag);
        }else if(categoriaPost.equals(context.getString(R.string.anuncio_subject_AcessableTurism))|| categoriaPost.equals("Acessable Turism")){
            mViewHolder.img.setImageResource(R.drawable.acessable_turism);
        }else {
            mViewHolder.img.setImageResource(R.drawable.hotel);
        }

        return convertView;
    }

    private boolean verifyData(String string) {

        String[] parts = string.split("/");
        String day = parts[0]; // day
        String month = parts[1]; // month
        String year = parts[2]; //year

        Calendar toDate = Calendar.getInstance();
        Calendar nowDate = Calendar.getInstance();
        toDate.set(Integer.parseInt(year),(Integer.parseInt(month) - 1) ,Integer.parseInt(day) + 1);

        if(toDate.before(nowDate)){
          return true;
        }
        return false;
    }

    private class MyViewHolder {

        private final TextView localRow;
        private final TextView tituloRow;
        private final TextView ActivePostRow;
        private ImageView img = null;

        public MyViewHolder(View item) {
            localRow = (TextView) item.findViewById(R.id.nomeUtilizadorPost);
            tituloRow = (TextView) item.findViewById(R.id.tituloPost);
            ActivePostRow = (TextView) item.findViewById(R.id.localPost);
            img = (ImageView) item.findViewById(R.id.imagemPost);

            //put 110dp margin left
            ActivePostRow.setPadding(110, 0, 0, 0);

        }
    }
}
