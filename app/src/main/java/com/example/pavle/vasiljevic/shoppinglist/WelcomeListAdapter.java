package com.example.pavle.vasiljevic.shoppinglist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WelcomeListAdapter extends BaseAdapter {


    private Context mContext;
    private ArrayList<WelcomeListItem> mListItems;



    public WelcomeListAdapter(Context Context) {
        this.mContext = Context;
        this.mListItems=new ArrayList<WelcomeListItem>();
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }




    @Override
    public Object getItem(int position) {
        WelcomeListItem rv=null;

        try{
            rv=mListItems.get(position);
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


    public void addItem(WelcomeListItem listItem)
    {
        mListItems.add(listItem);
        MainActivity.dbHelper.insertList(listItem);
        notifyDataSetChanged();
    }

    public void removeItem(WelcomeListItem listItem)
    {
        mListItems.remove(listItem);
        MainActivity.dbHelper.deleteListItem(listItem.getmListTitle());
        notifyDataSetChanged();
    }

    public void update(WelcomeListItem[] personal, WelcomeListItem[] shared, ArrayList<WelcomeListItem> server) {
        this.mListItems.clear();
        if(personal != null) {
            for(WelcomeListItem iterate : personal) {
                this.mListItems.add(iterate);
            }
        }
        Log.d("here", "update: here");
        if(shared != null) {
            for(WelcomeListItem jterate : shared) {
                this.mListItems.add(jterate);
            }
        }

        if(server != null) {
            for(WelcomeListItem zterate : server) {
                this.mListItems.add(zterate);
            }
        }

        this.notifyDataSetChanged();
    }

    public void updateShowing(){
        notifyDataSetChanged();
    }

    private class ViewHolder{
        TextView mTextViewTitle=null;
        TextView mTextViewShared=null;
    }



    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        View view=convertView;
        if(view == null) {
            //inflate the layout for each list row
            LayoutInflater inflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view= inflater.inflate(R.layout.welcome_list_item, null);
            viewHolder=new ViewHolder();
            viewHolder.mTextViewShared=view.findViewById(R.id.listWelcomeShared);
            viewHolder.mTextViewTitle=view.findViewById(R.id.listWelcomeTitle);
            view.setTag(viewHolder);
        }
        else {
            viewHolder=(ViewHolder) view.getTag();
        }
        //get current item to be displayed
        WelcomeListItem listItem=(WelcomeListItem) getItem(position);

        //get textviews
        TextView title = viewHolder.mTextViewTitle;
        TextView shared = viewHolder.mTextViewShared;


        viewHolder.mTextViewTitle.setText(listItem.getmListTitle());
        viewHolder.mTextViewShared.setText(listItem.getmListSharable());




        return view;
    }






}
