package com.example.pavle.vasiljevic.shoppinglist;

public class WelcomeListItem {



    private String mListTitle;
    private String mListSharable;
    private String mListOwner;



    public WelcomeListItem(String mListTitle, String mListSharable,String mListOwner) {
        this.mListTitle = mListTitle;
        this.mListSharable = mListSharable;
        this.mListOwner=mListOwner;
    }

    public String getmListSharable() {
        return mListSharable;
    }

    public void setmListSharable(String mListSharable) {
        this.mListSharable = mListSharable;
    }

    public String getmListTitle() {
        return mListTitle;
    }

    public void setmListTitle(String mListTitle) {
        this.mListTitle = mListTitle;
    }

    public String getmListOwner() {
        return mListOwner;
    }

    public void setmListOwner(String mListOwner) {
        this.mListOwner = mListOwner;
    }
}
