package com.example.grupo7.daam.myexsteerience.Accessory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Tiago on 21/04/2017.
 */

public class CommentPostAdapter extends BaseAdapter  {

    private final ArrayList<Object> data;
    LayoutInflater inflater;
    Context context;
    String ratingUser;

    private FirebaseUser mUser;
    private String PostID;

    private String m_Text = "";

    private String autorName;
    private String uidAutor;
    private Double nrCommentdouble;


    @Override public int getCount() {
        return data.size();
    }

    @Override public Object getItem(int position) {
        return data.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }


    public CommentPostAdapter(Context context, ArrayList<Object> data, FirebaseUser mUser, String ratingUser, String PostID) {
        this.data=data;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mUser = mUser;
        this.autorName = mUser.getDisplayName();
        this.uidAutor = mUser.getUid();
        this.PostID = PostID;

        Double ratingDouble =  Double.parseDouble(ratingUser);

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        this.ratingUser = df.format(ratingDouble);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderPost viewHolderPost;
        ViewHolderComment viewHolderComment;

        if (convertView == null) {

            if (position == 0 ) {

                convertView = inflater.inflate(R.layout.row_comment_post_header, parent, false);
                viewHolderPost = new ViewHolderPost(convertView);
                convertView.setTag(viewHolderPost);

                Post currentListData = (Post) getItem(position);
                String titulo = currentListData.getTitulo();
                String descricao = currentListData.getDescricao();
                String autor = currentListData.getAutor();
                String categoria = currentListData.getCategoria();
                String data = currentListData.getData();
                String cidade = currentListData.getCidade();
                String uidAutor = currentListData.getUidAutor();


                viewHolderPost.commentButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                       if(verifyInternet()){
                           comentar();
                       }else{
                           methodInternet();
                       }
                    }
                });

                viewHolderPost.tituloRow.setText(Html.fromHtml("<b>" + context.getString(R.string.comment_title) + "</b> "  + " " + titulo));
                viewHolderPost.descricaoRow.setText(Html.fromHtml("<b>" + context.getString(R.string.comment_description) + "</b> " + " "  + descricao));
                viewHolderPost.autorRow.setText(context.getString(R.string.comment_author) + " "+ autor );
                viewHolderPost.categoriaRow.setText(Html.fromHtml("<b>" + context.getString(R.string.comment_subject) + "</b> " + " " + categoria )  );
                viewHolderPost.dataRow.setText(Html.fromHtml("<b>" + context.getString(R.string.comment_date)+ "</b> "+ " " + data)  );
                viewHolderPost.cidadeRow.setText(Html.fromHtml("<b>" + context.getString(R.string.comment_city)  + "</b> "+ " " + cidade ) );

            } else  {
                convertView = inflater.inflate(R.layout.row_comment_post_comments, parent, false);
                viewHolderComment = new CommentPostAdapter.ViewHolderComment(convertView);
                convertView.setTag(viewHolderComment);

                Comment currentListData = (Comment) getItem(position);
                String AutorRating = currentListData.getRatingAutor();
                String AutorComment = currentListData.getNomeAutor();
                String Comment = currentListData.getComentario();
                String CommentRating = currentListData.getRatingComentario();

                viewHolderComment.AutorRating.setText(context.getString(R.string.comment_geral_rating)+ " " + AutorRating);
                viewHolderComment.AutorComment.setText(AutorComment);
                viewHolderComment.Comment.setText( Html.fromHtml("<b>" + context.getString(R.string.comment_comment) + "</b> "+ " " + Comment));


                if (CommentRating.equals("0.0")){
                    viewHolderComment.CommentRating.setText(context.getString(R.string.comment_notRated) );
                }else {
                    viewHolderComment.CommentRating.setText(context.getString(R.string.comment_rating) + " "+ CommentRating );
                }

            }
        } else {
            if (position == 0  ) {
                viewHolderPost = (ViewHolderPost) convertView.getTag();
            }else{
                viewHolderComment =  (ViewHolderComment) convertView.getTag();
            }
        }

        return convertView;
    }

    private void comentar() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref1  = mDatabase.child("user-info").child(uidAutor);
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String nrComment;
                nrComment = String.valueOf(dataSnapshot.child("nrComentarios").getValue());
                nrCommentdouble =  Double.parseDouble(nrComment);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.comment_msg) + " ");
        builder.setCancelable(false);

        final EditText input = new EditText(context);
        builder.setView(input);


        // Set up the buttons
        builder.setPositiveButton(context.getString(R.string.bot_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

    m_Text = input.getText().toString();

    if (!m_Text.equals("")) {

        double ratingDouble = 0.0;
        uidAutor = mUser.getUid();
        autorName = mUser.getDisplayName();

        Comment comment;

        if (Double.parseDouble(ratingUser) <= 0.0) {
            comment = new Comment(uidAutor, autorName, m_Text, Double.toString(ratingDouble), Double.toString(ratingDouble));
        } else {
            comment = new Comment(uidAutor, autorName, m_Text, ratingUser, Double.toString(ratingDouble));
        }

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mDatabase.child("post-comment");

        ref.child(PostID).push().setValue(comment);

        DatabaseReference ref2 = mDatabase.child("user-info").child(uidAutor);
        ref2.child("nrComentarios").setValue(nrCommentdouble + 1);

    } else {
        Toast.makeText(context, context.getString(R.string.erro_comment),
                Toast.LENGTH_SHORT).show();
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
        private final Button commentButton;

        public ViewHolderPost(View item) {
            autorRow = (TextView) item.findViewById(R.id.comment_header_autor);
            tituloRow = (TextView) item.findViewById(R.id.comment_header_titulo);
            descricaoRow = (TextView) item.findViewById(R.id.comment_header_descricao);
            categoriaRow = (TextView) item.findViewById(R.id.comment_header_categoria);
            dataRow = (TextView) item.findViewById(R.id.comment_header_data);
            cidadeRow = (TextView) item.findViewById(R.id.comment_header_cidade);
            commentButton = (Button)item.findViewById(R.id.comment_header_Commentar);

        }

    }

    private class ViewHolderComment {

        private final TextView AutorComment;
        private final TextView AutorRating;
        private final TextView Comment ;
        private final TextView CommentRating;


        public ViewHolderComment(View item) {
            AutorComment = (TextView) item.findViewById(R.id.comment_comments_AutorComentario);
            AutorRating = (TextView) item.findViewById(R.id.comment_comments_AutorRating);
            Comment = (TextView) item.findViewById(R.id.comment_comments_Comentario);
            CommentRating = (TextView) item.findViewById(R.id.comment_comments_rating);
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