package com.example.pavle.vasiljevic.shoppinglist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AndroidService extends Service {


    private boolean mRun = true;
    private HttpHelper httphelp;

    private DbHelper dbhelp;

    private String BASE_URL;

    public AndroidService() {
        this.httphelp = new HttpHelper();
        this.dbhelp = MainActivity.dbHelper;
        this.BASE_URL=MainActivity.BASE_URL;
    }

    @Override
    public void onCreate() {
        createNotificationChannel();

        new Thread(() -> {
            while (mRun) {
                try {
                    Log.d("SYNC", "Attempting sync with server");
                    Intent resultIntent = new Intent(this, MainActivity.class);
                    // Create the TaskStackBuilder and add the intent, which inflates the back stack
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    // Get the PendingIntent containing the entire back stack
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(0,
                                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    String textTitle="Sync";
                    String textContent="Syncing local db shared lists with server ones";
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_DF")
                            .setContentTitle(textTitle)
                            .setContentText(textContent)
                            .setPriority(NotificationCompat.PRIORITY_LOW)
                            .setSmallIcon(R.drawable.ic_launcher_foreground);
                    builder.setContentIntent(resultPendingIntent);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                    // notificationId is a unique int for each notification that you must define
                    int notif_id= (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                    notificationManager.notify(notif_id, builder.build());


                    updateLocalLists();



                    Thread.sleep(10000);

                    notificationManager.cancel(notif_id);



                    Thread.sleep(5000);



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service is starting", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRun = false;
        Toast.makeText(this, "service is done", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "default_channel";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_DF", name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void updateLocalLists() {
        JSONArray sharedItemsList = null;

        ArrayList<WelcomeListItem> serverLoad=new ArrayList<WelcomeListItem>();


        try {
            sharedItemsList = httphelp.getJSONArrayFromURL(BASE_URL+"lists");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(sharedItemsList!=null) {

            for (int i = 0; i < sharedItemsList.length(); i++) {
                try {


                    JSONObject current = null;

                    current = sharedItemsList.getJSONObject(i);


                    String currentName = (String) current.get("name");
                    String currentShared = Boolean.toString((boolean) current.get("shared"));
                    String currentCreator = (String) current.get("creator");
                    WelcomeListItem c = new WelcomeListItem(currentName, currentShared, currentCreator);

                    serverLoad.add(c);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.d("", "updateLocalLists: ");
            dbhelp.updateSharedLists(serverLoad);
        }
    }


    }


