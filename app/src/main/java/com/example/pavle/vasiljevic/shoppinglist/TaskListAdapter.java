package com.example.pavle.vasiljevic.shoppinglist;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class TaskListAdapter extends BaseAdapter {



    private Context mContext;
    private ArrayList<TaskListItem> mTasks;
    private String BASE_URL ;
    private Boolean sharedList;

    boolean dbb;

    public TaskListAdapter(Context Context,Boolean s) {
        this.mContext = Context;
        this.mTasks=new ArrayList<TaskListItem>();
        this.sharedList=s;
    }

    public ArrayList<TaskListItem> getmTasks() {
        return mTasks;
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public Object  getItem(int position) {
        TaskListItem rv=null;

        try{
            rv=mTasks.get(position);
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();

        }


        return rv;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private class ViewHolder{
        TextView mTextViewItem=null;
        CheckBox mCheckBox=null;
    }


    public void addItem(TaskListItem item)
    {

        mTasks.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(TaskListItem item)
    {
        mTasks.remove(item);
        notifyDataSetChanged();
    }

    public void updateTasks(TaskListItem[] tasks) {
        mTasks.clear();
        if(tasks != null) {
            for(TaskListItem task : tasks) {
                mTasks.add(task);
            }
        }

        notifyDataSetChanged();
    }


    public void updateExtra(TaskListItem[] lists, ArrayList<TaskListItem> server) {
        Log.d("USLI SMO U FUNKCIJU", "onCreate: hello napravili smo");
        mTasks.clear();
        if(server != null) {
            for(TaskListItem zterate : server) {
                this.mTasks.add(zterate);
            }
        }
        this.notifyDataSetChanged();

        if(lists != null) {
            for(TaskListItem list : lists) {
                mTasks.add(list);
            }
        }
        notifyDataSetChanged();
        Log.d("IZASLI SMO IZ FUNCK", "onCreate: hello napravili smo");
    }


    TaskListItem get_model_by_id(String taskItemId){
        for(TaskListItem iterate : mTasks) {
           // Log.d("listOfTasks", "mtasks: "+iterate.getmTaskName());
            if(taskItemId.compareTo(iterate.getId())==0){
               // Log.d("item", "get_model_by_id: "+iterate.getmTaskName());
                return iterate;
            }
        }
        return null;
    }


    void updateServer(TaskListItem item){

        new Thread(new Runnable() {
            public void run() {
                HttpHelper helper = new HttpHelper();


                String itemID = "/" +item.getId();
                String checkBoxState="/"+item.getmTaskDone();
                //Log.d("heyy", ((TaskListItem) adapter.getItem(position)).getId());
                //Log.d("ispis ceo", BASE_URL+itemID);
                boolean proceed = false;
                try {
                    proceed = helper.httpPut(BASE_URL+itemID+checkBoxState);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }



            }
        }).start();



    }




    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        View view=convertView;
        BASE_URL=  mContext.getResources().getString(R.string.localIP)+"tasks";
        if(view == null) {
            //inflate the layout for each list row
            LayoutInflater inflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view= inflater.inflate(R.layout.task_item, null);
            viewHolder=new ViewHolder();
            viewHolder.mTextViewItem=view.findViewById(R.id.taskName);
            viewHolder.mCheckBox=view.findViewById(R.id.taskCheckBox);
            view.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder) view.getTag();
        }
        //get current item to be displayed


        //Log.d("showingItem", "getView:one item is shown ");
        String taskItemId=((TaskListItem) this.getItem(position)).getId();
        Log.d("taskItemID;", "taskitemID output: "+taskItemId);

        if(sharedList==false) {
            TaskListItem taskModel = MainActivity.dbHelper.findTaskItem(taskItemId);


            TextView taskname = viewHolder.mTextViewItem;
            CheckBox checkbox = viewHolder.mCheckBox;

            if (checkbox.isChecked()) {
                taskname.setPaintFlags(taskname.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                taskname.setPaintFlags(taskname.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    taskModel.setmCheckboxBool(b);
                    MainActivity.dbHelper.changeCheckbox(taskModel);

                    if (checkbox.isChecked()) {
                        taskname.setPaintFlags(taskname.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        taskname.setPaintFlags(taskname.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            });


            taskname.setText(taskModel.getmTaskName());
            checkbox.setChecked(taskModel.boolTaskDone());
        }
        else{
            TaskListItem taskModel = get_model_by_id(taskItemId);


            if(taskModel==null){
                return view;
            }

            TextView taskname = viewHolder.mTextViewItem;
            CheckBox checkbox = viewHolder.mCheckBox;

            if (checkbox.isChecked()) {
                taskname.setPaintFlags(taskname.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                taskname.setPaintFlags(taskname.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    taskModel.setmCheckboxBool(b);
                    updateServer(taskModel);

                    if (checkbox.isChecked()) {
                        taskname.setPaintFlags(taskname.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        taskname.setPaintFlags(taskname.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            });


            taskname.setText(taskModel.getmTaskName());
            checkbox.setChecked(taskModel.boolTaskDone());
        }


        return view;
    }










}
