package com.example.grupo7.daam.myexsteerience;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.io.IOException;
import java.util.Calendar;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grupo7.daam.myexsteerience.Objects.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class NewPostActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private int year;
    private int month;
    private int day;

    private EditText editTitle;
    private EditText editSubTitle;
    private TextView editCity;
    private TextView editClass;
    private TextView editData;

    static final int DIALOG_DATE_ID = 999;
    static final int DIALOG_CITY_ID = 998;
    static final int DIALOG_CLASS_ID = 997;

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int Year,
                              int Month, int Day) {
            year = Year;
            month = Month;
            day = Day;

            AppCompatTextView editData = (AppCompatTextView) findViewById(R.id.textData);
            editData.setText(getString(R.string.anuncio_dataPick)+ " " + day + "/" + (month + 1) + "/" + year);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.anuncio_title));

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        editTitle =(EditText)findViewById(R.id.textTitulo);
        editSubTitle =(EditText)findViewById(R.id.textAnuncio);
        editCity =(TextView)findViewById(R.id.textCity);
        editClass =(TextView)findViewById(R.id.textcategoria);
        editData =(TextView)findViewById(R.id.textData);

        ListennersButton();
    }

    private void ListennersButton() {

        ImageButton back = (ImageButton) findViewById(R.id.backActivity);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        Button buttonPost = (Button) findViewById(R.id.Post);
        buttonPost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                methodInternet();
            }
        });

        LinearLayout editData =(LinearLayout)findViewById(R.id.selectData);
        editData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                hideSoftKeyboard(NewPostActivity.this);
                showDialog(DIALOG_DATE_ID);
            }
        });

        LinearLayout editCity =(LinearLayout)findViewById(R.id.selectCity);
        editCity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(NewPostActivity.this);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                showDialog(DIALOG_CITY_ID);
            }
        });

        LinearLayout editClass=(LinearLayout)findViewById(R.id.selectCategoria);
        editClass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(NewPostActivity.this);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                showDialog(DIALOG_CLASS_ID);
            }
        });
    }

    private void Post() {

        boolean formularioValido = verificacaoFormulario();
        if(formularioValido){
            if(mUser != null){
                //escreve na basedados um novo anuncio
                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                final DatabaseReference ref = mDatabase.child("post");
                ref.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long intId = 0;
                        for (DataSnapshot data : dataSnapshot.getChildren()){
                            if(data.exists()){
                               intId = Long.parseLong(data.getKey());
                            }
                        }
                        String id = String.valueOf(intId + 1);
                        writeData(id, mDatabase);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

            }else{
                mAuth.signOut();
                startActivity( new Intent(NewPostActivity.this, LoginActivity.class));
                finish();
            }
        }
    }

    private void writeData(String id, DatabaseReference mDatabase) {
        final DatabaseReference ref  = mDatabase.child("post");
        String title =  editTitle.getText().toString();
        String subTitle = editSubTitle.getText().toString();
        String autorName = mUser.getDisplayName();
        String categoria = editClass.getText().toString();
        String date = day + "/" + (month + 1)  + "/" + year;
        String city = editCity.getText().toString();
        String uidAutor = mUser.getUid();

        String categoriaBaseDados = transformCategoria(categoria);
        final Post post = new Post(title, subTitle, autorName, categoriaBaseDados, date, city, uidAutor, id);
        ref.child(id).setValue(post);

        final DatabaseReference ref1  = mDatabase.child("user-info").child(uidAutor);
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //protege a app de ir a baixo quando é criado uma conta, reconhece o valor obtido como null
                try {
                    String nrPost;
                    Double nrPostdouble;
                    nrPost = String.valueOf(dataSnapshot.child("nrPost").getValue());
                    nrPostdouble =  Double.parseDouble(nrPost);

                    ref1.child("nrPost").setValue(nrPostdouble + 1);

                } catch (NumberFormatException e) {
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private String transformCategoria(String categoria) {

        if(categoria.equals(getString(R.string.anuncio_subject_Sun)) || categoria.equals("Sun and Sea")){
            return "Sun and Sea";
        }else if(categoria.equals(getString(R.string.anuncio_subject_Gastronomy))|| categoria.equals("Gastronomy and Wine")){
            return "Gastronomy and Wine";
        }else if(categoria.equals(getString(R.string.anuncio_subject_culture))|| categoria.equals("Art and Culture")){
            return "Art and Culture";
        }else if(categoria.equals(getString(R.string.anuncio_subject_Family))|| categoria.equals("Family")){
            return "Family";
        }else if(categoria.equals(getString(R.string.anuncio_subject_Sports))|| categoria.equals("Sports")){
            return "Sports";
        }else if(categoria.equals(getString(R.string.anuncio_subject_Nature))|| categoria.equals("Nature")){
            return "Nature";
        }else if(categoria.equals(getString(R.string.anuncio_subject_Romance))|| categoria.equals("Romance")){
            return "Romance";
        }else if(categoria.equals(getString(R.string.anuncio_subject_Outdoors))|| categoria.equals("Outdoors")){
            return "Outdoors";
        }else if(categoria.equals(getString(R.string.anuncio_subject_Well_being))|| categoria.equals("Well-being")){
                return "Well-being";
        }else if(categoria.equals(getString(R.string.anuncio_subject_Retail))|| categoria.equals("Retail")){
                return "Retail";
        }else if(categoria.equals(getString(R.string.anuncio_subject_AcessableTurism))|| categoria.equals("Acessable Turism")){
                return "Acessable Turism";
        }else {
            return "Hotels";
        }
    }

    @Override
    protected Dialog onCreateDialog(int i) {
        switch (i) {

            case DIALOG_DATE_ID:
                // set date picker as current date

                DatePickerDialog datePicker = new DatePickerDialog(NewPostActivity.this,  android.R.style.Theme_Holo_Light_Dialog_NoActionBar, datePickerListener,
                        year, month, day)    {
                    @Override
                    public void onCreate(Bundle savedInstanceState)
                    {
                        super.onCreate(savedInstanceState);
                        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }
                };

                datePicker.setCancelable(false);
                return datePicker;

            case DIALOG_CITY_ID:

                 AlertDialog.Builder builderCity = new AlertDialog.Builder(this);
                builderCity.setTitle(getString(R.string.anuncio_dialog_city));

                final ArrayAdapter<String> arrayAdapterCity = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        View v = (View) super.getView(position, convertView, parent);
                        v.getLayoutParams().height = 90;
                        TextView t = (TextView) v.findViewById(textView.getId());
                        t.setTextSize(18);
                        return v;
                    }
                };

                arrayAdapterCity.add("Aveiro");
                arrayAdapterCity.add("Beja");
                arrayAdapterCity.add("Braga");
                arrayAdapterCity.add("Bragança");
                arrayAdapterCity.add("Castelo Branco");
                arrayAdapterCity.add("Coimbra");
                arrayAdapterCity.add("Évora");
                arrayAdapterCity.add("Faro");
                arrayAdapterCity.add("Guarda");
                arrayAdapterCity.add("Leiria");
                arrayAdapterCity.add("Lisboa");
                arrayAdapterCity.add("Portalegre");
                arrayAdapterCity.add("Porto");
                arrayAdapterCity.add("Santarém");
                arrayAdapterCity.add("Setúbal");
                arrayAdapterCity.add("Viana do Castelo");
                arrayAdapterCity.add("Vila Real");
                arrayAdapterCity.add("Viseu");
                arrayAdapterCity.add("Madeira");
                arrayAdapterCity.add("Açores");

                builderCity.setNegativeButton(getString(R.string.bot_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                 });

                builderCity.setAdapter(arrayAdapterCity, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String strName = arrayAdapterCity.getItem(which);
                    AppCompatTextView editCity = (AppCompatTextView) findViewById(R.id.textCity);
                    editCity.setText(strName);
                            dialog.dismiss();
                }
            });
                builderCity.show();
            return  null;

            case DIALOG_CLASS_ID:
                AlertDialog.Builder buildClass = new AlertDialog.Builder(this);
                buildClass.setTitle(getString(R.string.anuncio_dialog_subject));

                final ArrayAdapter<String> arrayAdapterClass = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        View v = (View) super.getView(position, convertView, parent);
                        v.getLayoutParams().height = 90;
                        TextView t = (TextView) v.findViewById(textView.getId());
                        t.setTextSize(18);
                        return v;
                    }
                };

                arrayAdapterClass.add(getString(R.string.anuncio_subject_culture));
                arrayAdapterClass.add(getString(R.string.anuncio_subject_Family));
                arrayAdapterClass.add(getString(R.string.anuncio_subject_Sports));
                arrayAdapterClass.add(getString(R.string.anuncio_subject_Hotels));
                arrayAdapterClass.add(getString(R.string.anuncio_subject_Gastronomy));
                arrayAdapterClass.add(getString(R.string.anuncio_subject_Nature));
                arrayAdapterClass.add(getString(R.string.anuncio_subject_Romance));
                arrayAdapterClass.add(getString(R.string.anuncio_subject_Sun));
                arrayAdapterClass.add(getString(R.string.anuncio_subject_AcessableTurism));
                arrayAdapterClass.add(getString(R.string.anuncio_subject_Outdoors));
                arrayAdapterClass.add(getString(R.string.anuncio_subject_Well_being));
                arrayAdapterClass.add(getString(R.string.anuncio_subject_Retail));

                buildClass.setNegativeButton(getString(R.string.bot_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                buildClass.setAdapter(arrayAdapterClass, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapterClass.getItem(which);
                        AppCompatTextView editClass = (AppCompatTextView) findViewById(R.id.textcategoria);
                        editClass.setText(strName);
                        dialog.dismiss();
                    }
                });
                buildClass.show();
                return  null;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideSoftKeyboard(NewPostActivity.this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private boolean verificacaoFormulario() {

        Toast toast = null;

        if(!editTitle.getText().toString().equals("")){

            if(!editCity.getText().toString().equals(getString(R.string.anuncio_city))){

                if(!editClass.getText().toString().equals(getString(R.string.anuncio_subject))){

                    Calendar toDate = Calendar.getInstance();
                    Calendar nowDate = Calendar.getInstance();
                    toDate.set(year,month,day);

                    if(!toDate.before(nowDate)){

                        return true;

                    }else{
                        toast = Toast.makeText(this,getString(R.string.erro_day), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }else{
                    toast = Toast.makeText(this, getString(R.string.erro_subjects), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }else{
                toast = Toast.makeText(this, getString(R.string.erro_city), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }else{
            toast = Toast.makeText(this, getString(R.string.erro_title), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        return false;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void methodInternet() {

        if(!verifyInternet()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.net_title));

            final TextView text = new TextView(this);
            text.setText("       "  + getString(R.string.net_msg));

            builder.setView(text);
            builder.setPositiveButton(getString(R.string.net_botao), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    methodInternet();
                }
            });
            builder.show();

        }else{
            Post();
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

