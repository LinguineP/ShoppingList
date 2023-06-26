package com.example.pavle.vasiljevic.shoppinglist;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView userID;

    private Button newList;
    private Button seeLists;

    static  String username;



    private Intent intentWelcome;
    private Intent backIntent;
    private Intent showListIntent;


    private boolean mylists;

    HttpHelper helper;

    AlertDialog.Builder builder;


    ListView listWelcome;

    public static WelcomeListAdapter adapter;

    WelcomeListItem[] shared;
    WelcomeListItem[] personal;
    ArrayList<WelcomeListItem> server;

    GoHomeFragment homeFragment;







    private String BASE_URL;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        BASE_URL=getResources().getString(R.string.localIP)+"lists";
        adapter = new WelcomeListAdapter(this);

        mylists = true;

        newList = findViewById(R.id.newListButton);
        seeLists = findViewById(R.id.seeListsButton);
        listWelcome = findViewById(R.id.listWelcome);
        userID = findViewById(R.id.userID);



        intentWelcome = new Intent(WelcomeActivity.this,
                NewList.class);

        backIntent = new Intent(WelcomeActivity.this, MainActivity.class);

        showListIntent = new Intent(WelcomeActivity.this,
                ShowListActivity.class);

        //seeLists.setEnabled(false);
        homeFragment=GoHomeFragment.newInstance("param1","param2");



        getSupportFragmentManager().beginTransaction()
                .replace(R.id.HomeFragmentContainerWelcome, homeFragment)
                .addToBackStack(null)
                .commit();

        //homeFragment=GoHomeFragment.newInstance("param1","param2");

        helper=new HttpHelper();

        Bundle passed = getIntent().getExtras();
        if (passed != null) {
            username = passed.getString("username");
        }
        /*else{
            onSaveInstanceState(passed);
        }
            */

        userID.setText(username);



        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                startActivity(backIntent);
            }
        };
        WelcomeActivity.this.getOnBackPressedDispatcher().addCallback(WelcomeActivity.this, callback);


        newList.setOnClickListener(this);
        seeLists.setOnClickListener(this);


        builder = new AlertDialog.Builder(WelcomeActivity.this);

        listWelcome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                WelcomeListItem a1 = (WelcomeListItem) adapter.getItem(position);

                showListIntent.putExtra("Title", a1.getmListTitle());
                showListIntent.putExtra("shared", a1.getmListSharable());
                startActivity(showListIntent);
            }

        });

        server=new ArrayList<>();

        listWelcome.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                WelcomeListItem a = (WelcomeListItem) adapter.getItem(position);
                if(a.getmListSharable().compareTo("true") == 0) {
                    new Thread(new Runnable() {
                        public void run() {
                            HttpHelper helper = new HttpHelper();
                            String Username = "/" + username;
                            String Name = "/" + a.getmListTitle();
                            boolean proceed = false;
                            try {
                                proceed = helper.httpDelete(BASE_URL+Username+Name);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            if(proceed) {
                                MainActivity.dbHelper.deleteListItem(((WelcomeListItem) adapter.getItem(position)).getmListTitle());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.removeItem((WelcomeListItem) adapter.getItem(position));
                                    }
                                });
                            }
                        }
                    }).start();
                } else {
                    MainActivity.dbHelper.deleteListItem(((WelcomeListItem) adapter.getItem(position)).getmListTitle());
                    adapter.removeItem((WelcomeListItem) adapter.getItem(position));
                }
                return true;

            }
        });


         shared = MainActivity.dbHelper.readSharedListItems(username);

         personal = MainActivity.dbHelper.readUserListItems(username);




        if(personal!=null) {
            for (WelcomeListItem i : personal) {
                Log.d("personal", "onCreate: personal" + i.getmListTitle());
            }
        }else{
            Log.d("personal", "onCreate: personal not found"  );
        }
        /*
        adapter.addItem(new WelcomeListItem("Naslov 8","false"));
        adapter.addItem(new WelcomeListItem("Naslov 9","true"));
        adapter.addItem(new WelcomeListItem("Naslov 10","true"));
        adapter.addItem(new WelcomeListItem("Naslov 11","true"));*/
        //adapter.addItem(new WelcomeListItem("Naslov 12","true","hel"));

        listWelcome.setAdapter(adapter);



        adapter.update(personal,shared,null);





    }






    void fetch_lists(){
        new Thread(new Runnable() {
            public void run() {
                /*try {


                    if(server!=null){
                        server.clear();
                    }

                    JSONArray sharedItemsList = helper.getJSONArrayFromURL(BASE_URL);
                    List<Integer> toExpell = new ArrayList<>();

                    for (int i = 0; i < sharedItemsList.length(); i++)
                    {
                        JSONObject current = sharedItemsList.getJSONObject(i);



                        String currentName= (String) current.get("creator");
                        if(currentName.compareTo(username)==0){
                            toExpell.add(i);
                        }
                    }

                    for (int i = 0; i < toExpell.size(); i++)
                    {
                        sharedItemsList.remove(toExpell.get(i));

                    }





                    for (int i = 0; i < sharedItemsList.length(); i++)
                    {
                        JSONObject current = sharedItemsList.getJSONObject(i);

                        String currentName= (String) current.get("name");
                        String currentShared=Boolean.toString((boolean)current.get("shared"));
                        String currentCreator=(String) current.get("creator");
                        WelcomeListItem c =new WelcomeListItem(currentName,currentShared,currentCreator);

                        server.add(c);


                    }*/
                    personal = MainActivity.dbHelper.readUserListItems(username);
                    shared = MainActivity.dbHelper.readSharedListItems(username);
                    server = MainActivity.dbHelper.readSharedNotUsersListItems(username);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            adapter.update(personal,shared,server);
                        }
                    });
                /*} catch (IOException e) {
                    e.printStackTrace();
               } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        }).start();

    }


    private void areYouSurePrompt(){
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                intentWelcome.putExtra("Username",username);
                startActivity(intentWelcome);
            }
        });
        AlertDialog alert = builder.create();
        alert.setTitle("New List Dialog");
        alert.setMessage("Are you sure you want to create new list?");
        alert.show();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.newListButton:
                areYouSurePrompt();
                break;
            case R.id.seeListsButton:

                if(mylists==false){
                    mylists=true;
                    personal = MainActivity.dbHelper.readUserListItems(username);
                    shared = MainActivity.dbHelper.readSharedListItems(username);
                    adapter.update(personal,shared,null);
                }
                else{
                    /*
                    shared = MainActivity.dbHelper.readSharedListItems(username);
                    personal = MainActivity.dbHelper.readUserListItems(username);
                    adapter.update(personal,shared,server);
                    */
                    fetch_lists();
                    mylists=false;
                }

                break;
            default:

        }
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("username",username);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        username=savedInstanceState.getString("username");
    }


}