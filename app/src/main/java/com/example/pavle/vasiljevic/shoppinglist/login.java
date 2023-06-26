package com.example.pavle.vasiljevic.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class login extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private Button submitLogin;
    private EditText loginPassword;
    private EditText loginUsername;

    public static int attempts;




    Intent intentLogin;

    private String BASE_URL;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment login.
     */
    // TODO: Rename and change types and number of parameters
    public static login newInstance(String param1, String param2) {
        login fragment = new login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login,container , false);

        intentLogin = new Intent(getContext(),
                WelcomeActivity.class);
        BASE_URL=getActivity().getResources().getString(R.string.localIP)+"login";
        loginUsername=view.findViewById(R.id.loginUsername);
        loginPassword=view.findViewById(R.id.loginPassword);
        submitLogin = view.findViewById(R.id.submitLogin);
        submitLogin.setEnabled(true);
        attempts=0;




        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button
                MainActivity.mainActivityLogin.setVisibility(View.VISIBLE);
                MainActivity.mainActivityLogin.setEnabled(true);
                MainActivity.mainActivityRegister.setVisibility(View.VISIBLE);
                MainActivity.mainActivityRegister.setEnabled(true);
                loginUsername.setText("");
                loginPassword.setText("");
                getFragmentManager().beginTransaction()
                        .remove(login.this).commit();

            }
        };


        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        submitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle button click
                String enteredUsername;
                String enteredPassword;

                enteredUsername = loginUsername.getText().toString();
                enteredPassword = loginPassword.getText().toString();

                loginUsername.setText("");
                loginPassword.setText("");

                User user = MainActivity.dbHelper.readUser(enteredUsername);
                //Log.d("password", "password: "+user.getPassword());



                new Thread(new Runnable() {
                    public void run() {

                        HttpHelper helper = new HttpHelper();
                        boolean proceed = false;

                        if(user!=null && enteredPassword.compareTo(user.getPassword())==0){

                            intentLogin.putExtra("username",enteredUsername);
                            startActivity(intentLogin);

                        }

                        try {
                            JSONObject pass = new JSONObject();
                            pass.put("username", enteredUsername);
                            pass.put("password", enteredPassword);


                            proceed = helper.postJSONObjectFromURL(BASE_URL, pass);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        if(proceed) {

                            intentLogin.putExtra("username",enteredUsername);
                            startActivity(intentLogin);
                        }else{

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JNIClass jni=new JNIClass();
                                    attempts=jni.increment(attempts);
                                    Toast.makeText(getActivity(), "Invalid username or password ", Toast.LENGTH_SHORT).show();
                                    if(attempts>5){
                                        submitLogin.setEnabled(false);
                                    }
                                }
                            });

                        }


                    }



                }).start();
                /*
                if(user!=null && enteredPassword.compareTo(user.getPassword())==0){

                    intentLogin.putExtra("username",enteredUsername);
                    startActivity(intentLogin);

                }*/
            }
        });
        return view;

    }






}