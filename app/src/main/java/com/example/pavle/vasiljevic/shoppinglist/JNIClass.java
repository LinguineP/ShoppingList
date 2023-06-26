package com.example.pavle.vasiljevic.shoppinglist;

public class JNIClass {

    static {
        System.loadLibrary("MyLib");

    }

    public native int increment(int x);

}
