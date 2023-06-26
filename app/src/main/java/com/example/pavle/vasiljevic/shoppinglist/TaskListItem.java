package com.example.pavle.vasiljevic.shoppinglist;

import android.util.Log;

import java.util.Date;

public class TaskListItem {


    private String mTaskName;
    private String mTaskDone;
    private String id;
    private String containingTitle;


    public TaskListItem(String mTaskName,String listTitle) {
        this.mTaskName = mTaskName;
        this.mTaskDone="false";
        int unique_id= (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        int id_prefix=(int) ((new Date().getTime() / unique_id) % Integer.MAX_VALUE);
        this.id=Integer.toHexString(id_prefix)+Integer.toHexString(unique_id);
        this.containingTitle=listTitle;
    }

    public TaskListItem(String mTaskName,String mTaskDone, String id, String containingTitle) {
        this.mTaskName = mTaskName;
        this.mTaskDone = mTaskDone;
        this.id=id;
        this.containingTitle = containingTitle;
    }


    public String getmTaskName() {
        return mTaskName;
    }

    public void setmTaskName(String mTaskName) {
        this.mTaskName = mTaskName;
    }



    public boolean boolTaskDone(){
        if(mTaskDone.compareTo("false")==0){
            return false;
        }
        return true;
    }

    public String setmTaskDone(String mTaskDone) {
        this.mTaskDone = mTaskDone;
        return mTaskDone;
    }

    public void setmCheckboxBool(boolean b){
        if(b){
            this.mTaskDone = "true";
        }
        else{
            this.mTaskDone = "false";
        }
    }

    public String IDGetter(){
        Log.d("idC", "IDGetter: "+id);
        return  id;
    }



    public String getmTaskDone() {
        return mTaskDone;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContainingTitle() {
        return containingTitle;
    }

    public void setContainingTitle(String containingTitle) {
        this.containingTitle = containingTitle;
    }
}
