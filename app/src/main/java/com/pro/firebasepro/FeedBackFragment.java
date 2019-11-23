package com.pro.firebasepro;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedBackFragment extends Fragment {

    EditText editTextSubject, editTextMessage;
    Button send;
    String to, subject, message;
    private Context context;
    public FeedBackFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed_back, container, false);
        editTextSubject = view.findViewById(R.id.emailsub);
        editTextMessage = view.findViewById(R.id.emailmessage);
        send = view.findViewById(R.id.emailsend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextSubject.getText().toString().isEmpty() || editTextMessage.getText().toString().isEmpty()) {
                    if (editTextSubject.getText().toString().isEmpty()) {
                        editTextSubject.setError("Enter Subject!");
                    }
                    if (editTextMessage.getText().toString().isEmpty()) {
                        editTextMessage.setError("Enter Message!");
                    }
                }
                else {
                    to = "prodipghoshjoy@gmail.com";
                    subject = editTextSubject.getText().toString();
                    message = editTextMessage.getText().toString();
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                    email.putExtra(Intent.EXTRA_SUBJECT, subject);
                    email.putExtra(Intent.EXTRA_TEXT, message);
                    email.setType("message/rfc822");
                    startActivity(Intent.createChooser(email, "Choose An Email Client"));
                }
            }
        });
        return view;
    }

}
