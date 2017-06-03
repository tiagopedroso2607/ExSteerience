package com.example.grupo7.daam.myexsteerience.Accessory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RatingBar;
import android.widget.TextView;


import com.example.grupo7.daam.myexsteerience.Objects.Comment;
import com.example.grupo7.daam.myexsteerience.Objects.Post;
import com.example.grupo7.daam.myexsteerience.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tiago on 03/05/2017.
 */

public class RateMyPostAdapter extends BaseAdapter {


    private final ArrayList<Object> data;
    LayoutInflater inflater;
    Context context;

    private FirebaseUser mUser;
    private String PostID;

    private double ratingUserdouble;
    private double UsernrVotosRecebidosdouble;
    private double UsernrVotosFeitosdouble;
    private double UserMediaRatingDadodouble;

    private int year;
    private int month;
    private int day;


    @Override public int getCount() {
        return data.size();
    }

    @Override public Object getItem(int position) {
        return data.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }


    public RateMyPostAdapter(Context context, ArrayList<Object> data, FirebaseUser mUser, String PostID) {
        this.data=data;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mUser = mUser;
        this.PostID = PostID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolderPost viewHolderPost;
        final RateMyPostAdapter.ViewHolderComment viewHolderComment;

        if (convertView == null) {

            if (position == 0 ) {
                convertView = inflater.inflate(R.layout.row_rate_mypost_header, parent, false);
                viewHolderPost = new ViewHolderPost(convertView);
                convertView.setTag(viewHolderPost);

                Post currentListData = (Post) getItem(position);
                String titulo = currentListData.getTitulo();
                String descricao = currentListData.getDescricao();
                String autor = currentListData.getAutor();
                String categoria = currentListData.getCategoria();
                String data = currentListData.getData();
                String cidade = currentListData.getCidade();

                final String dataPost = data;
                viewHolderPost.changeDateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(verifyInternet()){
                            changeDate(dataPost, viewHolderPost);
                        }else{
                            methodInternet();
                        }
                    }
                });

                viewHolderPost.deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(verifyInternet()){
                            closePost();
                        }else{
                            methodInternet();
                        }
                    }
                });

                viewHolderPost.tituloRow.setText(Html.fromHtml("<b>" + context.getString(R.string.comment_title) + "</b> "  + " " + titulo));
                viewHolderPost.descricaoRow.setText(Html.fromHtml("<b>" + context.getString(R.string.comment_description) + "</b> " + " "  + descricao));
                viewHolderPost.autorRow.setText(context.getString(R.string.comment_author)+ " " + autor );

                viewHolderPost.dataRow.setText(Html.fromHtml("<b>" + context.getString(R.string.comment_date)+ "</b> "+ " " + data)  );
                viewHolderPost.cidadeRow.setText(Html.fromHtml("<b>" + context.getString(R.string.comment_city)  + "</b> " + " "+ cidade ) );

                String categoriaFinal = transformCategoria(categoria);
                viewHolderPost.categoriaRow.setText(Html.fromHtml("<b>" + context.getString(R.string.comment_subject) + "</b> " + " " + categoriaFinal )  );

            } else  {
                convertView = inflater.inflate(R.layout.row_rate_mypost_comments, parent, false);
                viewHolderComment = new RateMyPostAdapter.ViewHolderComment(convertView);
                convertView.setTag(viewHolderComment);

                final Comment currentListData = (Comment) getItem(position);
                String AutorRating = currentListData.getRatingAutor();
                final String AutorComment = currentListData.getNomeAutor();
                String Comment = currentListData.getComentario();
                String CommentRating = currentListData.getRatingComentario();

                viewHolderComment.AutorRating.setText(context.getString(R.string.comment_geral_rating)+ " " + AutorRating);
                viewHolderComment.AutorComment.setText(AutorComment);
                viewHolderComment.Comment.setText( Html.fromHtml("<b>" + context.getString(R.string.comment_comment) + "</b> " + Comment));

                if (CommentRating.equals("0.0")){
                    viewHolderComment.CommentRating.setText(Html.fromHtml("<i>" + context.getString(R.string.comment_click_rate)+ "</i> " ) );
                    viewHolderComment.CommentRating.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(verifyInternet()){
                                ratingPost(viewHolderComment, AutorComment, currentListData);
                            }else{
                                methodInternet();
                            }
                        }
                    });

                }else {
                    viewHolderComment.CommentRating.setText(context.getString(R.string.comment_rating) + " "+ CommentRating );
                }

            }
        } else {
            if (position == 0  ) {
                viewHolderPost = (ViewHolderPost) convertView.getTag();
            }else{
                viewHolderComment =  (RateMyPostAdapter.ViewHolderComment) convertView.getTag();
            }
        }

        return convertView;
    }

    private String transformCategoria(String categoria) {

        if(categoria.equals("Sun and Sea")){
            return context.getString(R.string.anuncio_subject_Sun);
        }else if(categoria.equals("Gastronomy and Wine")){
            return context.getString(R.string.anuncio_subject_Gastronomy);
        }else if(categoria.equals("Art and Culture")){
            return context.getString(R.string.anuncio_subject_culture);
        }else if(categoria.equals("Family")){
            return context.getString(R.string.anuncio_subject_Family);
        }else if( categoria.equals("Sports")){
            return context.getString(R.string.anuncio_subject_Sports);
        }else if(categoria.equals("Nature")){
            return context.getString(R.string.anuncio_subject_Nature);
        }else if( categoria.equals("Romance")){
            return context.getString(R.string.anuncio_subject_Romance);
        }else if( categoria.equals("Outdoors")){
            return context.getString(R.string.anuncio_subject_Outdoors);
        }else if(categoria.equals("Well-being")){
            return context.getString(R.string.anuncio_subject_Well_being);
        }else if( categoria.equals("Retail")){
            return context.getString(R.string.anuncio_subject_Retail);
        }else if( categoria.equals("Acessable Turism")){
            return context.getString(R.string.anuncio_subject_AcessableTurism);
        }else {
            return context.getString(R.string.anuncio_subject_Hotels);
        }
    }

    private void closePost() {

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("post");
        ref.child(PostID).setValue(null);

        DatabaseReference ref1 = mDatabase.child("post-comment");
        ref1.child(PostID).setValue(null);

        ((Activity)context).finish();
    }

    private void changeDate(String data, final ViewHolderPost viewHolderPost) {

        String[] parts = data.split("/");
        String dayS = parts[0]; // day
        String monthS = parts[1]; // month
        String yearS = parts[2]; //year

        year = Integer.parseInt(yearS);
        month = Integer.parseInt(monthS) - 1;
        day = Integer.parseInt(dayS);

        DatePickerDialog.OnDateSetListener datePickerListener
                = new DatePickerDialog.OnDateSetListener() {
            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int Year,
                                  int Month, int Day) {
                year = Year;
                month = Month;
                day = Day;

                String date = day + "/" + (month + 1)  + "/" + year;

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                DatabaseReference ref = mDatabase.child("post");


                ref.child(PostID).child("data").setValue(date);

                viewHolderPost.dataRow.setText(context.getString(R.string.comment_date) + " " + date );
            }
        };

        DatePickerDialog datePicker = new DatePickerDialog(context,  android.R.style.Theme_Holo_Light_Dialog_NoActionBar, datePickerListener,
                year, month, day)    {
            @Override
            public void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        };
        datePicker.setCancelable(false);
        datePicker.show();

    }

    private void ratingPost(final ViewHolderComment viewHolderComment, final String nomeAutor, final Comment comment){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        // Get the layout inflater

        View v = inflater.inflate(R.layout.rating_screen, null);

        final RatingBar ratingBar = (RatingBar)v.findViewById(R.id.ratingBar);
        builder.setView(v);

        // Set up the buttons
        builder.setPositiveButton(context.getString(R.string.bot_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(ratingBar.getRating() > 0.0){

                viewHolderComment.CommentRating.setText(context.getString(R.string.comment_rating) + " " + ratingBar.getRating() );
                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference ref = mDatabase.child("post-comment").child(PostID);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                                Comment c = snapshot.getValue(Comment.class);
                                if (c.getNomeAutor().equals(comment.getNomeAutor()) && c.getComentario().equals(comment.getComentario())
                                        && c.getRatingAutor().equals(comment.getRatingAutor())
                                        && c.getUidAutor().equals(comment.getUidAutor())
                                        && c.getRatingComentario().equals(comment.getRatingComentario())
                                        ){
                                    ref.child(snapshot.getKey()).child("ratingComentario").setValue(Double.toString(ratingBar.getRating()));
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                DatabaseReference ref1  = mDatabase.child("users").child(comment.getUidAutor());
                ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            String ratingUser;
                            ratingUser = String.valueOf(dataSnapshot.child("rating").getValue());
                            ratingUserdouble =  Double.parseDouble(ratingUser);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                DatabaseReference ref2  = mDatabase.child("user-info").child(comment.getUidAutor());
                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            String UsernrVotosRecebidos;
                            UsernrVotosRecebidos = String.valueOf(dataSnapshot.child("nrVotosRecebidos").getValue());
                            UsernrVotosRecebidosdouble =  Double.parseDouble(UsernrVotosRecebidos);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                    DatabaseReference ref3  = mDatabase.child("user-info").child(mUser.getUid());
                    ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                String UsernrVotosFeitos;
                                UsernrVotosFeitos = String.valueOf(dataSnapshot.child("nrVotosFeitos").getValue());
                                UsernrVotosFeitosdouble =  Double.parseDouble(UsernrVotosFeitos);

                                String UserMediaRatingDado;
                                UserMediaRatingDado = String.valueOf(dataSnapshot.child("mediaRatingDado").getValue());
                                UserMediaRatingDadodouble =  Double.parseDouble(UserMediaRatingDado);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Thread background = new Thread() {
                        public void run() {

                            try {
                                sleep(750);

                                double newValueRating = (((UsernrVotosRecebidosdouble * ratingUserdouble) + ratingBar.getRating()) / (UsernrVotosRecebidosdouble + 1));
                                double newMediaRating =(((UserMediaRatingDadodouble * UsernrVotosFeitosdouble) + ratingBar.getRating()) / (UsernrVotosFeitosdouble + 1));
                                mDatabase.child("users").child(comment.getUidAutor()).child("rating").setValue(newValueRating);
                                mDatabase.child("user-info").child(comment.getUidAutor()).child("nrVotosRecebidos").setValue(UsernrVotosRecebidosdouble + 1);
                                mDatabase.child("user-info").child(mUser.getUid()).child("nrVotosFeitos").setValue(UsernrVotosFeitosdouble + 1);
                                mDatabase.child("user-info").child(mUser.getUid()).child("mediaRatingDado").setValue(newMediaRating);
                            } catch (Exception e) {

                            }
                        }
                    };

                    background.start();

                }
            }
        });
        builder.setNegativeButton(context.getString(R.string.bot_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private class ViewHolderPost {

        private final TextView tituloRow;
        private final TextView descricaoRow;
        private final TextView autorRow ;
        private final TextView categoriaRow;
        private final TextView dataRow;
        private final TextView cidadeRow ;
        private final Button changeDateButton;
        private final Button deletePost;

        public ViewHolderPost(View item) {
            autorRow = (TextView) item.findViewById(R.id.rate_mypost_header_autor);
            tituloRow = (TextView) item.findViewById(R.id.rate_mypost_header_titulo);
            descricaoRow = (TextView) item.findViewById(R.id.rate_mypost_header_descricao);
            categoriaRow = (TextView) item.findViewById(R.id.rate_mypost_header_categoria);
            dataRow = (TextView) item.findViewById(R.id.rate_mypost_header_data);
            cidadeRow = (TextView) item.findViewById(R.id.rate_mypost_header_cidade);
            changeDateButton = (Button)item.findViewById(R.id.rate_mypost_header_changedate);
            deletePost = (Button)item.findViewById(R.id.rate_mypost_header_delete);
        }
    }

    private class ViewHolderComment {

        private final TextView AutorComment;
        private final TextView AutorRating;
        private final TextView Comment ;
        private final TextView CommentRating;

        public ViewHolderComment(View item) {
            AutorComment = (TextView) item.findViewById(R.id.rate_mypost_comments_AutorComentario);
            AutorRating = (TextView) item.findViewById(R.id.rate_mypost_comments_AutorRating);
            Comment = (TextView) item.findViewById(R.id.rate_mypost_comments_Comentario);
            CommentRating = (TextView) item.findViewById(R.id.rate_mypost_comments_rating);
        }
    }

    private void methodInternet() {

        if(!verifyInternet()){

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setTitle(context.getString(R.string.net_title));

            final TextView text = new TextView(context);
            text.setText("       " + context.getString(R.string.net_msg));

            builder.setView(text);

            builder.setPositiveButton(context.getString(R.string.net_botao), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    methodInternet();
                }
            });
            builder.show();
        }else{

            System.exit(0);

        }
    }

    private boolean verifyInternet() {
        try {
            if (isConnected() )
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isConnected() throws InterruptedException, IOException
    {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();

        if(isWifiConn || isMobileConn){
            return true;
        }else{
            return  false;
        }

    }
}
