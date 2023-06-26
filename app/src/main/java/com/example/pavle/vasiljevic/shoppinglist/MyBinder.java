package com.example.pavle.vasiljevic.shoppinglist;

public class MyBinder {
/*
    private static boolean mUpdate = true;
    private static DbHelper db;
    private static HttpHelper http;
    private static String currentserverURL;

    public static boolean ismUpdate() {
        return mUpdate;
    }

    public static String getCurrentserverURL() {
        return currentserverURL;
    }

    public MyBinder(){
        mUpdate=true;
        currentserverURL="";
        db=new DbHelper(this, DB_NAME, null, 1);
    }

/*
    private ArrayList<WelcomeListItem> fetch_lists(){

                    ArrayList<WelcomeListItem> serverFetch;


                   /* String BASE_URL=getResources().getString(R.string.localIP)+"lists";
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


                    }


                 return


    }*/

/*

    private void updateSharedLists(){






    }



    @Override
    public void startUpdatingLists() throws RemoteException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mUpdate=true;
                while (mUpdate){


                        try {
                            Log.d("ServiceTAG", "Hello from service"  + Boolean.toString(mUpdate)+currentserverURL);
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

        }

        }).start();



    }

    @Override
    public void stopUpdatingLists() throws RemoteException {
        mUpdate=false;
    }


    @Override
    public boolean updateSharedTasks() throws RemoteException {
        return false;
    }

    @Override
    public void stopUpdatingTasks() throws RemoteException {

    }

    @Override
    public void setURL(String passedURL) throws RemoteException {
        this.currentserverURL=passedURL;
    }*/
}
