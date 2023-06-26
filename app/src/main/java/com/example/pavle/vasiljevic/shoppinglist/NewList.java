package com.example.pavle.vasiljevic.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class NewList extends AppCompatActivity implements View.OnClickListener {

    EditText naslovListe;
    Button okButton;
    Button saveButton;
    RadioButton yesRadio;
    RadioButton noRadio;
    RadioGroup shareListRadio;
    TextView naslov;

    private Intent intentNewList;
    String mCurrentTitle;
    String mSharable;
    String mUsername;
    private GoHomeFragment homeFragment;

    private String BASE_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BASE_URL=getResources().getString(R.string.localIP)+"lists";
        Bundle passed = getIntent().getExtras();
        if(passed!=null) {
            mUsername=passed.getString("Username");
        }

        setContentView(R.layout.activity_new_list);

        naslovListe=findViewById(R.id.naslovListe);
        okButton=findViewById(R.id.okButton);
        saveButton=findViewById(R.id.saveButton);
        yesRadio=findViewById(R.id.yesRadio);
        noRadio=findViewById(R.id.noRadio);
        naslov=findViewById(R.id.naslov);
        okButton.setOnClickListener(this);

        saveButton.setOnClickListener(this);
        mCurrentTitle= String.valueOf(naslov.getText());
        shareListRadio=findViewById(R.id.sharelistRadio);

        shareListRadio.clearCheck();
        mSharable="false";

        homeFragment=GoHomeFragment.newInstance("param1","param2");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.HomeFragmentContainerNewList, homeFragment)
                .addToBackStack(null)
                .commit();

        yesRadio.setOnClickListener(this);

        noRadio.setOnClickListener(this);

        intentNewList =new Intent(NewList.this,
                WelcomeActivity.class);

    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.okButton){
            mCurrentTitle= String.valueOf(naslovListe.getText());
            naslovListe.setText("");
            naslov.setText(mCurrentTitle);
        }
        else if(view.getId()==R.id.yesRadio) {
            mSharable="true";
        }
        else if(view.getId()==R.id.noRadio){
            mSharable="false";
        }
        else if(view.getId()==R.id.saveButton){
            if(!mCurrentTitle.trim().isEmpty() && mCurrentTitle.compareTo(getString(R.string.naslov))!=0){
                boolean existsPersonal = MainActivity.dbHelper.personalExist(mCurrentTitle, mUsername);
                boolean existsShared = MainActivity.dbHelper.sharableExist(mCurrentTitle,mUsername);

                //Log.d("exists", "personal: " + existsPersonal + " shared " + existsShared);







                 if(mSharable.compareTo("false") == 0 && (!existsPersonal)){
                     WelcomeListItem toInsert = new WelcomeListItem(mCurrentTitle, mSharable, mUsername);
                     MainActivity.dbHelper.insertList(toInsert);
                     startActivity(intentNewList);
                    }

                    if(mSharable.compareTo("true") == 0) {

                        new Thread(new Runnable() {
                            public void run() {

                                HttpHelper helper = new HttpHelper();
                                boolean proceed = false;

                                try {
                                    JSONObject pass = new JSONObject();
                                    pass.put("name", mCurrentTitle);
                                    pass.put("creator", WelcomeActivity.username);
                                    if(mSharable.compareTo("true") == 0) {
                                        pass.put("shared", true);
                                    } else if(mSharable.compareTo("false") == 0){
                                        pass.put("shared", false);
                                    }

                                    proceed = helper.postJSONObjectFromURL(BASE_URL,pass);

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                if(proceed) {

                                    if (mSharable.compareTo("true") == 0 && (!existsShared)) {
                                        WelcomeListItem toInsert = new WelcomeListItem(mCurrentTitle, mSharable, mUsername);
                                        MainActivity.dbHelper.insertList(toInsert);
                                        startActivity(intentNewList);
                                    }
                                    startActivity(intentNewList);
                                }
                            }
                        }).start();

                    }
                }
            }
        }
    }
