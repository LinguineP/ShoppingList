package com.example.pavle.vasiljevic.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

// login activity

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

     static Button mainActivityLogin;
     static Button mainActivityRegister;
    private final String DB_NAME = "shared_list_app.db";

    public static DbHelper dbHelper;

    //fragments
    login loginfragment;
    register registerfragment;

    public static  String BASE_URL;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //buttons
        mainActivityLogin=findViewById(R.id.mainActivityLogin);
        mainActivityRegister=findViewById(R.id.mainActivityRegister);

        BASE_URL=getResources().getString(R.string.localIP);

        mainActivityLogin.setOnClickListener(this);
        mainActivityRegister.setOnClickListener(this);



       loginfragment=login.newInstance("param1","param2");
       registerfragment=register.newInstance("param1","param2");



        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                finishAffinity();
                System.exit(0);

            }
        };

        MainActivity.this.getOnBackPressedDispatcher().addCallback(MainActivity.this, callback);

        dbHelper = new DbHelper(this, DB_NAME, null, 1);


        Intent intent = new Intent(this, AndroidService.class);
        startService(intent);

        /*dbHelper.insertUser(new User("","",""));
        dbHelper.insertTask(new TaskListItem("",""));*/



    }


    @Override
    public void onClick(View view) {

        String enteredUsername;
        String enteredPassword;

        switch (view.getId()) {

            case R.id.mainActivityLogin:
                mainActivityLogin.setVisibility(View.INVISIBLE);
                mainActivityLogin.setEnabled(false);
                mainActivityRegister.setVisibility(View.INVISIBLE);
                mainActivityRegister.setEnabled(false);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, loginfragment)
                        .commit();

                break;

            case R.id.mainActivityRegister:

                mainActivityLogin.setVisibility(View.INVISIBLE);
                mainActivityLogin.setEnabled(false);
                mainActivityRegister.setVisibility(View.INVISIBLE);
                mainActivityRegister.setEnabled(false);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, registerfragment)
                        .commit();

                break;



            default:
                break;
        }

    }











}