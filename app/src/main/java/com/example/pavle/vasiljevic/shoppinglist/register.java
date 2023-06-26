package com.example.pavle.vasiljevic.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class register extends Fragment implements Runnable {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private Button submitRegister;


    private EditText registerUsername;
    private EditText registerEmail;
    private EditText registerPassword;


    Intent intentRegister;
    private String BASE_URL;




    public register() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment register.
     */
    // TODO: Rename and change types and number of parameters
    public static register newInstance(String param1, String param2) {
        register fragment = new register();
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
        View view = inflater.inflate(R.layout.fragment_register,container , false);

        intentRegister = new Intent(getContext(),
                WelcomeActivity.class);

        BASE_URL=getActivity().getResources().getString(R.string.localIP)+"users";
        submitRegister=view.findViewById(R.id.submitRegister);
        //containers

        //Edit texts
        registerUsername=view.findViewById(R.id.registerUsername);
        registerEmail=view.findViewById(R.id.registerEmail);
        registerPassword=view.findViewById(R.id.registerPassword);


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                MainActivity.mainActivityLogin.setVisibility(View.VISIBLE);
                MainActivity.mainActivityLogin.setEnabled(true);
                MainActivity.mainActivityRegister.setVisibility(View.VISIBLE);
                MainActivity.mainActivityRegister.setEnabled(true);
                registerPassword.setText("");
                registerEmail.setText("");
                registerUsername.setText("");
                getFragmentManager().beginTransaction()
                        .remove(register.this).commit();

            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        submitRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle button click
                String enteredUsername;
                String enteredPassword;
                String enteredEmail;
                enteredUsername = registerUsername.getText().toString();
                enteredPassword = registerPassword.getText().toString();
                enteredEmail=registerEmail.getText().toString();

                registerPassword.setText("");
                registerEmail.setText("");
                registerUsername.setText("");

                if(enteredPassword.compareTo("")!=0
                        && enteredUsername.compareTo("")!=0
                        && isEmailValid(enteredEmail)) {

                    //User user = MainActivity.dbHelper.readUser(enteredUsername);
                    HttpHelper helper=new HttpHelper();
                    //boolean procced=false;





                    new Thread(new Runnable() {
                        public void run() {

                            //HttpHelper helper = new HttpHelper();
                            boolean proceed = false;

                            try {
                                JSONObject pass = new JSONObject();
                                pass.put("username", enteredUsername);
                                pass.put("password", enteredPassword);
                                pass.put("email", enteredEmail);

                                proceed = helper.postJSONObjectFromURL(BASE_URL, pass);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            if(proceed) {

                                MainActivity.dbHelper.insertUser(new User(enteredUsername,enteredEmail,enteredPassword));
                                intentRegister.putExtra("username", enteredUsername);
                                startActivity(intentRegister);

                            }
                        }
                    }).start();
/*
                    if (procced) {
                        MainActivity.dbHelper.insertUser(new User(enteredUsername,enteredEmail,enteredPassword));
                        intentRegister.putExtra("username", enteredUsername);
                        startActivity(intentRegister);
                    }*/
                }
            }
        });

        return view;

    }










    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    @Override
    public void run() {

    }
}