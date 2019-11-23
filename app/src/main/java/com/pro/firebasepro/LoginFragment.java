package com.pro.firebasepro;



import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.design.widget.Snackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private EditText emailET, passET;
    private Button loginBtn, reglBtn;
    private TextView statusTV;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Context context;
    private UserAuthListener authListener;
    private ProgressBar progressBar;
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        authListener = (UserAuthListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        emailET = view.findViewById(R.id.emailInput);
        passET = view.findViewById(R.id.passwordInput);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        //statusTV = view.findViewById(R.id.statusTV);
        loginBtn = view.findViewById(R.id.loginBtn);
        reglBtn = view.findViewById(R.id.regLBtn);
        reglBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authListener.onAuthForRegComplete();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
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
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            progressBar.setVisibility(View.GONE);
                                            authListener.onAuthComplete();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //emailET.setError(e.getMessage());
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
            authListener.onAuthComplete();
        }
    }

    interface UserAuthListener{
        void onAuthComplete();
        void onAuthForRegComplete();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
