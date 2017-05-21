package com.example.grupo7.daam.myexsteerience.Accessory;

/**
 * Created by Tiago on 07/04/2017.
 */

import android.content.Context;
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

public class FeedPostAdapter extends BaseAdapter {


    private final ArrayList<Post> data;
    LayoutInflater inflater;
    Context context;

    public FeedPostAdapter(MainActivity mainActivity, ArrayList<Post> data) {
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
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_post, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Post currentListData = getItem(position);

        String autorPost = currentListData.getAutor();
        String tituloPost = currentListData.getTitulo();
        String localPost = currentListData.getCidade();

        mViewHolder.autorRow.setText(autorPost );
        mViewHolder.tituloRow.setText(tituloPost);
        mViewHolder.localRow.setText(localPost);

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

    private class MyViewHolder {
        private final TextView autorRow;
        private final TextView tituloRow;
        private final TextView localRow ;
        private  ImageView img = null;

        public MyViewHolder(View item) {
            autorRow = (TextView) item.findViewById(R.id.nomeUtilizadorPost);
            tituloRow = (TextView) item.findViewById(R.id.tituloPost);
            localRow = (TextView) item.findViewById(R.id.localPost);
            img = (ImageView) item.findViewById(R.id.imagemPost);

        }
    }
}