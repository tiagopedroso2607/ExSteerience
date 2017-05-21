package com.example.grupo7.daam.myexsteerience;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.grupo7.daam.myexsteerience.Accessory.CommentPostAdapter;
import com.example.grupo7.daam.myexsteerience.Objects.Comment;
import com.example.grupo7.daam.myexsteerience.Objects.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CommentPostActivity extends AppCompatActivity {


    private CommentPostAdapter commentPostAdapter;
    private List<Object> listCommentPost = new ArrayList<>();

    private ChildEventListener mChildEventListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String Userautor ;
    private String Usertitulo;
    private String Userdescricao;
    private String Usercategoria;
    private String Userdata;
    private String Usercity;
    private String UserauidAutor;

    private String PostID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypost_and_commentpost);

        ImageButton back = (ImageButton) findViewById(R.id.backActivity);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        Intent intent = getIntent();
        Userautor = intent.getStringExtra("Userautor");
        Usertitulo = intent.getStringExtra("Usertitulo");
        Userdescricao = intent.getStringExtra("Userdescricao");
        Usercategoria = intent.getStringExtra("Usercategoria");
        Userdata = intent.getStringExtra("Userdata");
        Usercity = intent.getStringExtra("Usercity");
        UserauidAutor = intent.getStringExtra("UserauidAutor");
        PostID = intent.getStringExtra("IdAnuncio");

        if(verifyInternet()){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            getHeader();
            getComments();
        }else{
            methodInternet();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void getHeader() {

        Post post = new Post(Usertitulo, Userdescricao, Userautor, Usercategoria, Userdata, Usercity, UserauidAutor, PostID);
        listCommentPost.add(post);
    }

    private void getComments() {
        String ratingUser = getIntent().getStringExtra("RatingUser");

        commentPostAdapter = new CommentPostAdapter(CommentPostActivity.this, (ArrayList<Object>) listCommentPost, mUser, ratingUser, PostID);
        ListView lView=(ListView) findViewById(R.id.list_view);
        lView.setAdapter(commentPostAdapter);

            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    verifyComment(comment);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mDatabase.child("post-comment").child(PostID).addChildEventListener(mChildEventListener);
    }

    private void verifyComment(Comment comment) {

            if (listCommentPost.size() == 1){
                listCommentPost.add(comment);
                commentPostAdapter.notifyDataSetChanged();
            }else{
                if(!replyPost(comment)){
                    listCommentPost.add(comment);
                    commentPostAdapter.notifyDataSetChanged();
                }
            }
    }

    private boolean replyPost(Comment comment) {
        boolean have = false;
        for(int i = 0; i< listCommentPost.size(); i++){
            if(listCommentPost.get(i).equals(comment)){
                have = true;
            }
        }
        if (have){
            return  true;
        }
        return false;

    }

    private void methodInternet() {

        if(!verifyInternet()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);

            builder.setTitle(getString(R.string.net_title));
            final TextView text = new TextView(this);
            text.setText("       " + getString(R.string.net_msg));

            builder.setView(text);
            builder.setPositiveButton(getString(R.string.net_botao), new DialogInterface.OnClickListener(){

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
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
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
