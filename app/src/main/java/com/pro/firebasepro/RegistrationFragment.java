package com.pro.firebasepro;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationFragment extends Fragment {
    private EditText emailET, passET;
    private Button regBtn;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Context context;
    private UserAuthRegListener authListener;
    private ProgressBar progressBar;
    public RegistrationFragment() {
        // Required empty public constructor

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        authListener = (UserAuthRegListener) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        emailET = view.findViewById(R.id.emailInputR);
        passET = view.findViewById(R.id.passwordInputR);
        regBtn = view.findViewById(R.id.registerBtn);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(emailET.getText().toString().isEmpty()){
                    emailET.setError("Enter Email!");
                }
                else if(passET.getText().toString().isEmpty()){
                    passET.setError("Enter Password!");
                }

                else{

                    String email = emailET.getText().toString();
                    String pass = passET.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    auth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        authListener.onAuthRegComplete();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();

                        }
                    });

                }

            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user != null){
            authListener.onAuthRegComplete();
        }
    }

    interface UserAuthRegListener{
        void onAuthRegComplete();
    }
    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
