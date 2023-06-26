package com.example.pavle.vasiljevic.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ShowListActivity extends AppCompatActivity implements View.OnClickListener {

    private Button add;
    private EditText addTaskTitle;
    private TextView listTitle;
    private ListView listTask;


    private String BASE_URL;
    private Intent backIntent;

    String title;
    String shared;
    private TaskListItem[] tasks;
    private GoHomeFragment homeFragment;
    TaskListAdapter adapter;
    ArrayList<TaskListItem> server;

    TaskListItem[] serverLoad;
    private Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        BASE_URL=getResources().getString(R.string.localIP)+"tasks";
        backIntent=new Intent(ShowListActivity.this,WelcomeActivity.class);

        add=findViewById(R.id.addButton);
        refresh=findViewById(R.id.refreshTasks);
        addTaskTitle=findViewById(R.id.addTaskTitle);
        listTitle=findViewById(R.id.showListTitle);
        listTask=findViewById(R.id.taskListView);

        title="No title available";
        server=new ArrayList<>();

        Bundle passed = getIntent().getExtras();
        if(passed!=null) {
            title=passed.getString("Title");
            shared= passed.getString("shared");
        }

        listTitle.setText(title);

        homeFragment=GoHomeFragment.newInstance("param1","param2");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.HomeFragmentContainerShowList, homeFragment)
                .addToBackStack(null)
                .commit();



        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                startActivity(backIntent);
            }
        };
        ShowListActivity.this.getOnBackPressedDispatcher().addCallback(ShowListActivity.this, callback);

        boolean defaultShared=false;

        if(0==shared.compareTo("true")){
            defaultShared=true;
        }
        adapter = new TaskListAdapter(this,defaultShared);

        listTask.setAdapter(adapter);

        add.setOnClickListener(this);
        refresh.setOnClickListener(this);

        if(shared.compareTo("false") == 0) {
            refresh.setEnabled(false);
            refresh.setVisibility(View.GONE);

        }



       listTask.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                if(shared.compareTo("true") == 0) {
                    new Thread(new Runnable() {
                        public void run() {
                            HttpHelper helper = new HttpHelper();

                            TaskListItem ite=(TaskListItem)adapter.getItem(position);
                            String itemID = "/" +ite.getId();
                            //Log.d("heyy", ((TaskListItem) adapter.getItem(position)).getId());
                            //Log.d("ispis ceo", BASE_URL+itemID);
                            boolean proceed = false;
                            try {
                                proceed = helper.httpDelete(BASE_URL+itemID);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            if(proceed) {
                                //MainActivity.dbHelper.deleteTaskItem(((TaskListItem) adapter.getItem(position)).getId());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.removeItem((TaskListItem) adapter.getItem(position));
                                    }
                                });
                            }
                        }
                    }).start();
                } else {
                    MainActivity.dbHelper.deleteTaskItem(((TaskListItem) adapter.getItem(position)).getId());
                    adapter.removeItem((TaskListItem) adapter.getItem(position));
                    //tasks = MainActivity.dbHelper.readTaskListItems(title);
                }

                //adapter.updateTasks(tasks);
                return false;
            }
    });









/*
        adapter.addItem(new TaskListItem("zadatak1"));
        adapter.addItem(new TaskListItem("zadatak2"));
        adapter.addItem(new TaskListItem("zadatak3"));
        adapter.addItem(new TaskListItem("zadatak4"));
*/

        //listTask.setAdapter(adapter);
/*
        tasks = MainActivity.dbHelper.readTaskListItems(title);


        adapter.updateTasks(tasks);*/

        if (shared.compareTo("true")==0) {
            fetch_tasks();
        }else{
            tasks = MainActivity.dbHelper.readTaskListItems(title);


            adapter.updateTasks(tasks);
        }
    }




    void fetch_tasks(){
        new Thread(new Runnable() {
            public void run() {
                HttpHelper helper = new HttpHelper();
                boolean proceed = true;
                try {

                    if(server!=null){
                        server.clear();
                    }

                    String Title = "/"+title;
                    JSONArray MyList = helper.getJSONArrayFromURL(BASE_URL+Title);
                    List<Integer> toExpell = new ArrayList<>();

                    for (int i = 0; i < MyList.length(); i++) {
                        JSONObject current = MyList.getJSONObject(i);

                        String currentTitle = (String) current.get("name");
                       // String currentList = (String) current.get("list");
                        String currentCheckbox = "false";
                        if((Boolean) current.get("done")) {
                            currentCheckbox = "true";
                        }
                        String currentId = (String) current.get("taskId");

                        TaskListItem c =new TaskListItem(currentTitle,currentCheckbox, currentId,title);

                        server.add(c);
                    }


                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if(proceed) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            adapter.updateExtra(tasks, server);
                        }
                    });
                }

            }
        }).start();

    }


    @Override
    public void onClick(View view) {
       if (view.getId()==R.id.addButton) {
           String taskTitle = addTaskTitle.getText().toString();
           if (taskTitle.compareTo("") != 0) {
               addTaskTitle.setText("");
               if (shared.compareTo("true") == 0) {

                   new Thread(new Runnable() {
                       public void run() {

                           TaskListItem toMake = new TaskListItem(taskTitle, title);

                           HttpHelper helper = new HttpHelper();
                           boolean proceed = false;

                           try {

                               JSONObject pass = new JSONObject();


                               pass.put("name", toMake.getmTaskName());
                               pass.put("list", toMake.getContainingTitle());
                               pass.put("done", toMake.boolTaskDone());
                               pass.put("taskId", toMake.getId());


                               // Log.d("JSON", pass.toString(2));


                               proceed = helper.postJSONObjectFromURL(BASE_URL, pass);

                           } catch (IOException e) {
                               throw new RuntimeException(e);
                           } catch (JSONException e) {
                               throw new RuntimeException(e);
                           }

                           if (proceed) {
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       //TaskListItem task = new TaskListItem(taskTitle,title);
                                       //MainActivity.dbHelper.insertTask(task);
                                       //tasks = MainActivity.dbHelper.readTaskListItems(title);
                                       adapter.updateTasks(tasks);
                                       fetch_tasks();
                                   }
                               });
                           }

                       }
                   }).start();

               } else {
                   TaskListItem task = new TaskListItem(taskTitle, title);
                   MainActivity.dbHelper.insertTask(task);
                   tasks = MainActivity.dbHelper.readTaskListItems(title);
                   adapter.updateTasks(tasks);
               }
           }
         }else if (view.getId()==R.id.refreshTasks){
                fetch_tasks();
            }
    }

}