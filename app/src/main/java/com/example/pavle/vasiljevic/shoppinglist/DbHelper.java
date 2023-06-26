package com.example.pavle.vasiljevic.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DbHelper  extends SQLiteOpenHelper {


    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }





    private final String TABLE_USER = "user";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";


    private final String TABLE_LIST = "list";
    public static final String COLUMN_LIST_NAME = "list_name";
    public static final String COLUMN_SHARED = "shared";


    private final String TABLE_TASKS = "tasks";
    public static final String COLUMN_TICKED = "ticked";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_ITEM_ID= "id";




         @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_USER +
                " (" + COLUMN_USERNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_LIST +
                " (" + COLUMN_LIST_NAME + " TEXT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_SHARED + " TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_TASKS +
                " (" + COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_LIST_NAME + " TEXT, " +
                COLUMN_ITEM_ID + " TEXT, " +
                COLUMN_TICKED + " TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    // user table methods
    public void insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());

        db.insert(TABLE_USER, null, values);
        close();
    }

    public void deleteUser(String username) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_USER, COLUMN_USERNAME + " =?", new String[] {username});
        close();
    }



    public User readUser(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_USERNAME + " =?", new String[] {username}, null, null, null);

        if (cursor.getCount() <= 0) {
            close();
            return null;
        }

        cursor.moveToFirst();

        User user = createUser(cursor);

        close();
        return user;
    }

    public boolean checkUserEmailExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, null, null, null, null, null);

        if (cursor.getCount() <= 0) {
            close();
            return false;
        }


        int i = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if(0==email.compareTo(
                    cursor.getString((cursor.getColumnIndexOrThrow(COLUMN_EMAIL)))));
            {
                return true;
            }

        }

        close();
        return false;
    }


    private User createUser(Cursor cursor) {
        String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));

        return new User(username,email,password);
    }



    public WelcomeListItem[] readUserListItems(String user) {
        SQLiteDatabase db = getReadableDatabase();
        //Cursor cursor = db.query(TABLE_LIST, null,COLUMN_USERNAME + " =?" , new String[]{user}, null, null, null);
        Cursor cursor=db.rawQuery("SELECT * FROM list WHERE shared='false' AND username=?;",new String[]{user});
        if (cursor.getCount() <= 0) {
            close();
            return null;
        }
        WelcomeListItem[] list = new WelcomeListItem[cursor.getCount()];
        int i = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            //if(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SHARED)).compareTo("false")==0) {
                list[i++] = createListItem(cursor);
            //}
        }

       // close();
        return list;
    }


    public boolean sharableExist(String title,String username){
             WelcomeListItem[] sharable=readSharedListItems(username);
             if(sharable!=null) {
                 for (WelcomeListItem i : sharable) {
                     if (i.getmListTitle().compareTo(title) == 0) {
                         return true;
                     }
                 }
             }
             return false;

    }

    public boolean personalExist(String title,String user){
        WelcomeListItem[] personal=readUserListItems(user);
        if(personal!=null) {
            for (WelcomeListItem i : personal) {
                if (i.getmListTitle().compareTo(title) == 0) {
                    return true;
                }
            }
        }
        return false;

    }

    //welcome list methods
    public WelcomeListItem[] readSharedListItems(String user) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM list WHERE shared='true' AND username=?;",new String[]{user});

        if (cursor.getCount() <= 0) {
            close();
            return null;
        }
        WelcomeListItem[] list = new WelcomeListItem[cursor.getCount()];
        int i = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list[i++] = createListItem(cursor);
            Log.d("shared", "readSharedListItems: "+cursor.toString());
        }

        close();
        return list;
    }

    public ArrayList<WelcomeListItem> readSharedNotUsersListItems(String user) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM list WHERE shared='true' AND NOT username=?;",new String[]{user});

        if (cursor.getCount() <= 0) {
            close();
            return null;
        }
        ArrayList<WelcomeListItem> list = new ArrayList<WelcomeListItem>();
        int i = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list.add(createListItem(cursor));
            //Log.d("shared", "readSharedListItems: "+cursor.toString());
        }

        close();
        return list;
    }

    public void deleteListItem(String title) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_LIST, COLUMN_LIST_NAME + " =?", new String[] {title});
        close();
    }

    private WelcomeListItem createListItem(Cursor cursor) {
        String owner = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_NAME));
        String  sharable= cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SHARED));

        return new WelcomeListItem(title,sharable,owner);
    }


    public void insertList(@NonNull WelcomeListItem l) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_LIST_NAME, l.getmListTitle());
        values.put(COLUMN_USERNAME, l.getmListOwner());
        values.put(COLUMN_SHARED, l.getmListSharable());
        Log.w("insert", "insertList: ");
        db.insert(TABLE_LIST, null, values);
        close();
    }

    public void updateSharedLists(ArrayList<WelcomeListItem> WebLoad){
        if(WebLoad!=null) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLE_LIST, COLUMN_SHARED + " =?", new String[]{"true"});


            for (WelcomeListItem l : WebLoad) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_LIST_NAME, l.getmListTitle());
                values.put(COLUMN_USERNAME, l.getmListOwner());
                values.put(COLUMN_SHARED, l.getmListSharable());
                Log.w("insert", "insertList: ");
                db.insert(TABLE_LIST, null, values);
            }

            close();
        }


    }



    //task list methods

    public TaskListItem[] readTaskListItems(String list_name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null,COLUMN_LIST_NAME + " =?" , new String[] {list_name}, null, null, null);

        if (cursor.getCount() <= 0) {
            close();
            return null;
        }
        TaskListItem[] list = new TaskListItem[cursor.getCount()];
        int i = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            list[i++] = createTaskItem(cursor);
            Log.d("shared", "readSharedListItems: "+cursor.toString());
        }

        close();
        return list;
    }

    private TaskListItem createTaskItem(Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_NAME));
        String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID));
        String ticked = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TICKED));
        String listTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_NAME));

        return new TaskListItem(title,ticked,id,listTitle);
    }

    public void insertTask(@NonNull TaskListItem l) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_LIST_NAME, l.getContainingTitle());
        values.put(COLUMN_TICKED, l.getmTaskDone());
        values.put(COLUMN_ITEM_NAME, l.getmTaskName());
        values.put(COLUMN_ITEM_ID, l.getId());

        Log.w("insert", "insertList: ");
        db.insert(TABLE_TASKS, null, values);
        close();
    }

    public void deleteTaskItem(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ITEM_ID+ " =?", new String[] {id});
        close();
    }



    public TaskListItem findTaskItem(String id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COLUMN_ITEM_ID + " =?", new String[] {id}, null, null, null);

        if (cursor.getCount() <= 0) {
            close();
            return null;
        }

        cursor.moveToFirst();

        TaskListItem task = createTaskItem(cursor);

        close();
        return task;
    }


    public void changeCheckbox(TaskListItem updater) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_LIST_NAME, updater.getContainingTitle());
        values.put(COLUMN_TICKED, updater.getmTaskDone());
        values.put(COLUMN_ITEM_NAME, updater.getmTaskName());
        values.put(COLUMN_ITEM_ID, updater.getId());


        db.update(TABLE_TASKS, values, COLUMN_ITEM_ID + " =?", new String[] {updater.getId()});
        Log.d("OVO Isto radi", "onCreate: hello napravili smo");

        close();
    }


}
