package com.example.grupo7.daam.myexsteerience;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.grupo7.daam.myexsteerience.Accessory.BaseActivity;
import com.example.grupo7.daam.myexsteerience.Accessory.FeedPostAdapter;
import com.example.grupo7.daam.myexsteerience.Accessory.FiltroAdapter;
import com.example.grupo7.daam.myexsteerience.Accessory.MyPostAdapter;
import com.example.grupo7.daam.myexsteerience.Accessory.RoundedTransformation;
import com.example.grupo7.daam.myexsteerience.Objects.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private ChildEventListener mChildEventListener;

    private List<Post> listFeedPost = new ArrayList<>();
    private List<Post> listMyPost = new ArrayList<>();

    private FeedPostAdapter feedPostAdapter;
    private MyPostAdapter myPostAdapter;

    private String state = "feedPost";

    private String[] filtroSubjects;
    private String filtroCities = "";

    //info for activity userInfo
    private String ratingUser;
    private String nrVotosRecebidos;
    private String nrVotosFeitos;
    private String nrComentarios;
    private String mediaRatingDado;
    private String nrPost;
    private String datacriacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if(mUser == null) {
                    startActivity( new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }

        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        desenharDrawerLayout(toolbar);
        feedPost();
        filtroSubjects = getResources().getStringArray(R.array.subjects_names);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!verifyInternet()){
            methodInternet();
        }else {

            showProgressDialog();

            mAuth.addAuthStateListener(mAuthListener);

            ImageButton filtro = (ImageButton) findViewById(R.id.filtro);
            filtro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filtro();
                }
            });
            TextView filtroText = (TextView) findViewById(R.id.filtroText);
            filtroText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filtro();
                }
            });

            FloatingActionButton post = (FloatingActionButton) findViewById(R.id.post);
            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    state = "newPost";
                    startActivity(new Intent(MainActivity.this, NewPostActivity.class));
                }
            });

            //redraw navigationView
            NavigationView navigationView = DrawNavigationView();
            View v = navigationView.getHeaderView(0);
            preencherDadosUser(v);
            getData();
        }
    }

    private void filtro() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.filtro_nome) + " ");
        builder.setCancelable(false);

        // Get the layout inflater

        View v = getLayoutInflater().inflate(R.layout.filtro, null);
        builder.setView(v);

        final AutoCompleteTextView text;
        text=(AutoCompleteTextView)v.findViewById(R.id.autoCompleteTextView1);
        final String[] city = getResources().getStringArray(R.array.city_names);
        ArrayAdapter adapter = new
                ArrayAdapter(this,android.R.layout.simple_list_item_1,city);
        text.setAdapter(adapter);
        text.setThreshold(1);

        Button b = (Button)v.findViewById(R.id.buttonSubejcts);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle(getString(R.string.filtro_nome) + " ");
                builder1.setCancelable(false);

                // Get the layout inflater

                View v1 = getLayoutInflater().inflate(R.layout.activity_list_filtro, null);
                builder1.setView(v1);

               // Button button = (Button) findViewById(R.id.postbuttonSubejcts);
                ListView listView = (ListView) v1.findViewById(R.id.list);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                final String[] list  = getResources().getStringArray(R.array.subjects_names);

                final FiltroAdapter adapter = new FiltroAdapter(getApplicationContext(), list, filtroSubjects);
                listView.setAdapter(adapter);


                // Set up the buttons
                builder1.setPositiveButton(getString(R.string.bot_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isSelected[] = adapter.getSelectedFlags();
                        ArrayList<String> selectedItems = new ArrayList<String>();

                        for(int i=0; i<isSelected.length; i++){
                            if(isSelected[i]){
                                selectedItems.add(list[i]);
                                filtroSubjects[i] = list[i];
                            }else{
                                filtroSubjects[i] = "fail";
                            }
                        }
                    }
                });

                builder1.show();
            }
        });

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.bot_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!text.getText().equals("")){
                    filtroCities = String.valueOf(text.getText());
                }
                if (state.equals("feedPost")){
                   listFeedPost.removeAll(listFeedPost);
                    state = "feedPost";
                    closeListenners();
                    feedPost();

                }else if(state.equals("myPost")){
                    closeListenners();
                    listMyPost.removeAll(listMyPost);
                    state = "myPost";
                    myPost();
                }
            }
        });

        builder.setNegativeButton(getString(R.string.bot_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void desenharDrawerLayout(Toolbar toolbar) {

        //setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle );
        toggle.syncState();
        NavigationView navigationView = DrawNavigationView();
        //trocar R.id.nav_gallery pelo id do primeiro item
        navigationView.setCheckedItem(R.id.nav_Post);
        View v = navigationView.getHeaderView(0);
        preencherDadosUser(v);
    }

    private NavigationView DrawNavigationView() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        return navigationView;
    }

    private void preencherDadosUser(View v) {

        ImageView imgUser = (ImageView) v.findViewById(R.id.nav_header_fotoUser);

                    DatabaseReference ref  = mDatabase.child("users").child(mUser.getUid());
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            double ratingDouble;

                            //protege a app de ir a baixo quando é criado uma conta, reconhece o valor obtido como null
                           try {
                                ratingUser = String.valueOf(dataSnapshot.child("rating").getValue());
                                ratingDouble =  Double.parseDouble(ratingUser);
                            } catch (NumberFormatException e) {
                                ratingDouble = 0.0; // your default value
                            }

                            TextView ratingNumerico = (TextView)MainActivity.this.findViewById(R.id.RatingNumerico);
                            DecimalFormat df = new DecimalFormat();
                            df.setMaximumFractionDigits(2);
                            ratingNumerico.setText( getString(R.string.userInfo_rate) + " " + df.format(ratingDouble));

                            float ratingFloat = (float)ratingDouble;
                            RatingBar ratingBar = (RatingBar) MainActivity.this.findViewById(R.id.ratingBar);
                            ratingBar.setRating(ratingFloat);

                            TextView username = (TextView)MainActivity.this.findViewById(R.id.nav_header_username);
                            username.setText(mUser.getDisplayName());

                            hideProgressDialog();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        Picasso.with(MainActivity.this)
                .load(mUser.getPhotoUrl())
                .transform(new RoundedTransformation(50, 5))
                .resize(100, 100)
                .centerCrop()
                .into(imgUser);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_Post) {
            listFeedPost.removeAll(listFeedPost);
            state = "feedPost";
            closeListenners();

            feedPost();

        } else if (id == R.id.nav_myPost) {
            closeListenners();
            listMyPost.removeAll(listMyPost);
            state = "myPost";
            myPost();

        } else if (id == R.id.nav_InfoUser) {
            userInfo();

        } else if (id == R.id.nav_signOut) {
            signOut();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void feedPost() {
        if(!verifyInternet()){
            methodInternet();
        }else{

        //redraw navigationView
        NavigationView navigationView = DrawNavigationView();
        View v = navigationView.getHeaderView(0);
        preencherDadosUser(v);

        feedPostAdapter = new FeedPostAdapter(MainActivity.this, (ArrayList<Post>) listFeedPost);
        ListView lView=(ListView) findViewById(R.id.list_view);
        lView.setAdapter(feedPostAdapter);


        mChildEventListener = new ChildEventListener() {
            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post anuncio = dataSnapshot.getValue(Post.class);

               verifyFeedPost(anuncio);
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override public void onCancelled(DatabaseError databaseError) {
            }
        };

        mDatabase.child("post").addChildEventListener(mChildEventListener);

        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Object o = parent.getItemAtPosition(position);
                Post post =(Post) o;

                Intent intent = new Intent(MainActivity.this, CommentPostActivity.class);
                intent.putExtra("Userautor", post.getAutor());
                intent.putExtra("Usertitulo", post.getTitulo());
                intent.putExtra("Userdescricao", post.getDescricao());
                intent.putExtra("Usercategoria", post.getCategoria());
                intent.putExtra("Userdata", post.getData());
                intent.putExtra("Usercity", post.getCidade());
                intent.putExtra("UserauidAtor", post.getUidAutor());
                intent.putExtra("IdAnuncio", post.getIdPost());
                intent.putExtra("RatingUser", ratingUser);
                startActivity(intent);
            }
        });
        }

    }

    private void verifyFeedPost(Post anuncio) {

        String string = anuncio.getData();
        String[] parts = string.split("/");
        String day = parts[0]; // day
        String month = parts[1]; // month
        String year = parts[2]; //year

        if(goodSubject(anuncio)){

        if(!anuncio.getUidAutor().equals(mUser.getUid().toString()) ){

            Calendar toDate = Calendar.getInstance();
            Calendar nowDate = Calendar.getInstance();
            toDate.set(Integer.parseInt(year),(Integer.parseInt(month) - 1) ,Integer.parseInt(day));

            if(!toDate.before(nowDate)){
                if(!filtroCities.equals("")){
                    if(anuncio.getCidade().equals(filtroCities)){
                        if (listFeedPost.size() == 0){
                            listFeedPost.add(anuncio);
                            feedPostAdapter.notifyDataSetChanged();
                        }else{
                            boolean have = false;
                            for(int i = 0; i< listFeedPost.size(); i++){
                                if(listFeedPost.get(i).equals(anuncio)
                                        && listMyPost.get(i).equals(anuncio.getCategoria())
                                        && listMyPost.get(i).equals(anuncio.getCidade())
                                        && listMyPost.get(i).equals(anuncio.getData())
                                        && listMyPost.get(i).equals(anuncio.getDescricao())
                                        && listMyPost.get(i).equals(anuncio.getUidAutor())
                                        && listMyPost.get(i).equals(anuncio.getTitulo())
                                        && listMyPost.get(i).equals(anuncio.getUidAutor())
                                        ){
                                    have = true;
                                }
                            }
                            if(!have){
                                listFeedPost.add(anuncio);
                                feedPostAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }else{
                    if (listFeedPost.size() == 0){
                        listFeedPost.add(anuncio);
                        feedPostAdapter.notifyDataSetChanged();
                    }else{
                        boolean have = false;
                        for(int i = 0; i< listFeedPost.size(); i++){
                            if(listFeedPost.get(i).equals(anuncio)
                                    && listMyPost.get(i).equals(anuncio.getCategoria())
                                    && listMyPost.get(i).equals(anuncio.getCidade())
                                    && listMyPost.get(i).equals(anuncio.getData())
                                    && listMyPost.get(i).equals(anuncio.getDescricao())
                                    && listMyPost.get(i).equals(anuncio.getUidAutor())
                                    && listMyPost.get(i).equals(anuncio.getTitulo())
                                    && listMyPost.get(i).equals(anuncio.getUidAutor())
                                    ){
                                have = true;
                            }
                        }
                        if(!have){
                            listFeedPost.add(anuncio);
                            feedPostAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

        }
        }
    }

    private void myPost() {
        if(!verifyInternet()){
            methodInternet();
        }else {
            //redraw navigationView
            NavigationView navigationView = DrawNavigationView();
            View v = navigationView.getHeaderView(0);
            preencherDadosUser(v);
            ListView lView = (ListView) findViewById(R.id.list_view);

            closeListenners();
            listMyPost.removeAll(listMyPost);
            state = "myPost";

            myPostAdapter = new MyPostAdapter(MainActivity.this, (ArrayList<Post>) listMyPost);
            lView.setAdapter(myPostAdapter);

            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Post anuncio = dataSnapshot.getValue(Post.class);
                    verifyMyPost(anuncio);
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
            mDatabase.child("post").addChildEventListener(mChildEventListener);

            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Object o = parent.getItemAtPosition(position);
                    Post post = (Post) o;

                    Intent intent = new Intent(MainActivity.this, MyPosts.class);
                    intent.putExtra("Userautor", post.getAutor());
                    intent.putExtra("Usertitulo", post.getTitulo());
                    intent.putExtra("Userdescricao", post.getDescricao());
                    intent.putExtra("Usercategoria", post.getCategoria());
                    intent.putExtra("Userdata", post.getData());
                    intent.putExtra("Usercity", post.getCidade());
                    intent.putExtra("UserauidAtor", post.getUidAutor());
                    intent.putExtra("IdAnuncio", post.getIdPost());

                    startActivity(intent);
                }
            });
        }
    }

    private void verifyMyPost(Post anuncio) {
        if(goodSubject(anuncio)) {
            if (anuncio.getUidAutor().equals(mUser.getUid().toString())) {
                if(filtroCities.equals("")) {
                    if (listMyPost.size() == 0) {
                        listMyPost.add(anuncio);
                        myPostAdapter.notifyDataSetChanged();
                    } else {
                        boolean have = false;
                        for (int i = 0; i < listMyPost.size(); i++) {
                            if (listMyPost.get(i).equals(anuncio)
                                    && listMyPost.get(i).equals(anuncio.getCategoria())
                                    && listMyPost.get(i).equals(anuncio.getCidade())
                                    && listMyPost.get(i).equals(anuncio.getData())
                                    && listMyPost.get(i).equals(anuncio.getDescricao())
                                    && listMyPost.get(i).equals(anuncio.getUidAutor())
                                    && listMyPost.get(i).equals(anuncio.getTitulo())
                                    && listMyPost.get(i).equals(anuncio.getUidAutor())

                                    ) {

                                have = true;
                            }
                        }
                        if (!have) {
                            listMyPost.add(anuncio);
                            myPostAdapter.notifyDataSetChanged();
                        }
                    }

                }else{
                    if(anuncio.getCidade().equals(filtroCities)){
                        if (listMyPost.size() == 0) {
                            listMyPost.add(anuncio);
                            myPostAdapter.notifyDataSetChanged();
                        } else {
                            boolean have = false;
                            for (int i = 0; i < listMyPost.size(); i++) {
                                if (listMyPost.get(i).equals(anuncio)
                                        && listMyPost.get(i).equals(anuncio.getCategoria())
                                        && listMyPost.get(i).equals(anuncio.getCidade())
                                        && listMyPost.get(i).equals(anuncio.getData())
                                        && listMyPost.get(i).equals(anuncio.getDescricao())
                                        && listMyPost.get(i).equals(anuncio.getUidAutor())
                                        && listMyPost.get(i).equals(anuncio.getTitulo())
                                        && listMyPost.get(i).equals(anuncio.getUidAutor())

                                        ) {

                                    have = true;
                                }
                            }
                            if (!have) {
                                listMyPost.add(anuncio);
                                myPostAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean goodSubject(Post anuncio) {
        boolean goodSubject = false;

        List<String> test = new ArrayList<String>(Arrays.asList(filtroSubjects));

        if(anuncio.getCategoria().equals(getString(R.string.anuncio_subject_Gastronomy)) || anuncio.getCategoria().equals("Gastronomy and Wine")){
            if (test.contains(getString(R.string.anuncio_subject_Gastronomy)));
            goodSubject = true;
        }else if (anuncio.getCategoria().equals(getString(R.string.anuncio_subject_Sun)) || anuncio.getCategoria().equals("Sun and Sea")){
            if (test.contains(getString(R.string.anuncio_subject_Sun))){
                goodSubject = true;
            }
        }else if (anuncio.getCategoria().equals(getString(R.string.anuncio_subject_culture)) || anuncio.getCategoria().equals("Art and Culture")){
            if (test.contains(getString(R.string.anuncio_subject_culture))){
                goodSubject = true;
            }
        }else if (anuncio.getCategoria().equals(getString(R.string.anuncio_subject_Family)) || anuncio.getCategoria().equals("Family")){
            if (test.contains(getString(R.string.anuncio_subject_Family))){
                goodSubject = true;
            }
        }else if (anuncio.getCategoria().equals(getString(R.string.anuncio_subject_Sports)) || anuncio.getCategoria().equals("Sports")){
            if (test.contains(getString(R.string.anuncio_subject_Sports))){
                goodSubject = true;
            }
        }else if (anuncio.getCategoria().equals(getString(R.string.anuncio_subject_Hotels)) || anuncio.getCategoria().equals("Hotels")){
            if (test.contains(getString(R.string.anuncio_subject_Hotels))){
                goodSubject = true;
            }
        }else if (anuncio.getCategoria().equals(getString(R.string.anuncio_subject_Nature)) || anuncio.getCategoria().equals("Nature")){
            if (test.contains(getString(R.string.anuncio_subject_Nature))){
                goodSubject = true;
            }
        }else if (anuncio.getCategoria().equals(getString(R.string.anuncio_subject_Romance)) || anuncio.getCategoria().equals("Romance")){
            if (test.contains(getString(R.string.anuncio_subject_Romance))){
                goodSubject = true;
            }
        }else if (anuncio.getCategoria().equals(getString(R.string.anuncio_subject_AcessableTurism)) || anuncio.getCategoria().equals("Acessable Turism")){
            if (test.contains(getString(R.string.anuncio_subject_AcessableTurism))){
                goodSubject = true;
            }
        }
        else if (anuncio.getCategoria().equals(getString(R.string.anuncio_subject_Outdoors)) || anuncio.getCategoria().equals("Outdoors")){
            if (test.contains(getString(R.string.anuncio_subject_Outdoors))){
                goodSubject = true;
            }
        }
        else if (anuncio.getCategoria().equals(getString(R.string.anuncio_subject_Well_being)) || anuncio.getCategoria().equals("Well-being")){
            if (test.contains(getString(R.string.anuncio_subject_Well_being))){
                goodSubject = true;
            }
        }else if (anuncio.getCategoria().equals(getString(R.string.anuncio_subject_Retail)) || anuncio.getCategoria().equals("Retail")){
            if (test.contains(getString(R.string.anuncio_subject_Retail))){
                goodSubject = true;
            }
        }
        return goodSubject;
    }

    private void userInfo() {

        state = "userInfo";

        Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
        intent.putExtra("RatingUser", ratingUser);
        intent.putExtra("nrPost", nrPost);
        intent.putExtra("nrVotosRecebidos", nrVotosRecebidos);
        intent.putExtra("nrVotosFeitos", nrVotosFeitos);
        intent.putExtra("nrComentarios", nrComentarios);
        intent.putExtra("mediaRatingDado", mediaRatingDado);
        intent.putExtra("dataCriacao", datacriacao);
        startActivity(intent);
    }

    private void getData() {

        DatabaseReference ref  = mDatabase.child("user-info").child(mUser.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //protege a app de ir a baixo quando é criado uma conta, reconhece o valor obtido como null
                try {

                    nrPost = String.valueOf(dataSnapshot.child("nrPost").getValue());
                    datacriacao = String.valueOf(dataSnapshot.child("dataCriacao").getValue());
                    nrVotosRecebidos = String.valueOf(dataSnapshot.child("nrVotosRecebidos").getValue());
                    nrVotosFeitos = String.valueOf(dataSnapshot.child("nrVotosFeitos").getValue());
                    nrComentarios = String.valueOf(dataSnapshot.child("nrComentarios").getValue());
                    mediaRatingDado = String.valueOf(dataSnapshot.child("mediaRatingDado").getValue());

                } catch (NumberFormatException e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void signOut() {
        mAuth.signOut();
        finish();
    }

    private void closeListenners() {

        if (state.equals("feedPost") || state.equals("myPost")){
           try{
               mDatabase.child("post").removeEventListener(mChildEventListener);
           }catch (RuntimeException e){}
        }
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
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
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
