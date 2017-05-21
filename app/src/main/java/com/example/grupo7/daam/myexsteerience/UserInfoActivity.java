package com.example.grupo7.daam.myexsteerience;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.grupo7.daam.myexsteerience.Accessory.RoundedTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class UserInfoActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Nullable
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        ImageButton back = (ImageButton) findViewById(R.id.backActivity);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        putData();
    }

    private void putData() {
        ImageView imgUser = (ImageView) findViewById(R.id.userinfo_fotoUser);

        Intent intent = getIntent();
        TextView ratingNumerico = (TextView)findViewById(R.id.userinfo_RatingNumerico);
        Double ratingDouble =  Double.parseDouble(intent.getStringExtra("RatingUser"));
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        ratingNumerico.setText( getString(R.string.userInfo_rate) + " "+ df.format(ratingDouble));

        float rating = (float) Double.parseDouble(intent.getStringExtra("RatingUser"));

        RatingBar ratingBar = (RatingBar)findViewById(R.id.userinfo_ratingBar);
        ratingBar.setRating(rating);

        Picasso.with(UserInfoActivity.this)
                .load(mUser.getPhotoUrl())
                .transform(new RoundedTransformation(50, 5))
                .resize(100, 100)
                .centerCrop()
                .into(imgUser);

        TextView Nome = (TextView)findViewById(R.id.userinfo_username);
        Nome.setText( mUser.getDisplayName());

        TextView date = (TextView)findViewById(R.id.userinfo_datacriacao);
        date.setText( getString(R.string.userInfo_datecreate) + " "+ intent.getStringExtra("dataCriacao"));

        TextView nrPost = (TextView)findViewById(R.id.userinfo_nrPost);
        if (intent.getStringExtra("nrPost").equals("0.0")){
            nrPost.setText(getString(R.string.userInfo_nr_published) + " " + 0 );
        }else{
            nrPost.setText(getString(R.string.userInfo_nr_published) + " "+ intent.getStringExtra("nrPost") );
        }

        TextView nrComentarios = (TextView)findViewById(R.id.userinfo_nrComment);
        if (intent.getStringExtra("nrComentarios").equals("0.0")){
            nrComentarios.setText(getString(R.string.userInfo_nr_comments) + " "+ 0 );
        }else{
            nrComentarios.setText(getString(R.string.userInfo_nr_comments) + " "+ intent.getStringExtra("nrComentarios") );
        }

        TextView nrVotosRecebidos = (TextView)findViewById(R.id.userinfo_nrAvaliacoes_recebidas);
        if (intent.getStringExtra("nrVotosRecebidos").equals("0.0")){
            nrVotosRecebidos.setText(getString(R.string.userInfo_nr_votes) + " "+ 0 );
        }else{
            nrVotosRecebidos.setText(getString(R.string.userInfo_nr_votes) + " "+ intent.getStringExtra("nrVotosRecebidos") );
        }

        TextView nrVotosFeitos = (TextView)findViewById(R.id.userinfo_nrAvaliacoes_feitas);
        if (intent.getStringExtra("nrVotosFeitos").equals("0.0")){
            nrVotosFeitos.setText(getString(R.string.userInfo_nr_votes_cast) + " "+ 0 );
        }else{
            nrVotosFeitos.setText(getString(R.string.userInfo_nr_votes_cast)+ " " + intent.getStringExtra("nrVotosFeitos") );
        }

        TextView mediaRatingDado = (TextView)findViewById(R.id.userinfo_media_classificacoes_feitas);
        if (intent.getStringExtra("mediaRatingDado").equals("0.0")){
            mediaRatingDado.setText( getString(R.string.userInfo_averages_votes_cast)+ " "+ 0 );
        }else{
            mediaRatingDado.setText(getString(R.string.userInfo_averages_votes_cast) + " "+ intent.getStringExtra("mediaRatingDado") );
        }



    }
}
