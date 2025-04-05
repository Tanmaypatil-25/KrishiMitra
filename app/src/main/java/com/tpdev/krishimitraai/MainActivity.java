package com.tpdev.krishimitraai;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText queryEditText;
    private ImageView btnSend, logo, appIcon;
    private LinearLayout chatResponse;

    FloatingActionButton btnShowMessageDialog;

    private ProgressBar progressBar;



    private ChatFutures chatModel;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.message_dialog);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            //dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
        }

        btnSend = dialog.findViewById(R.id.btn_send);
        queryEditText = dialog.findViewById(R.id.queryEditText);
        progressBar = findViewById(R.id.progressBar);
        chatResponse = findViewById(R.id.chatResponse);
        btnShowMessageDialog = findViewById(R.id.showMessageDialog);
        appIcon = findViewById(R.id.homeIcon);

        chatModel = getChatModel();

        //appIcon.setVisibility(View.VISIBLE);

        btnShowMessageDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        dialog.setOnKeyListener((dialogInterface, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                // Handle back button press
                dialogInterface.dismiss(); // Optionally dismiss the dialog
                return true; // Consume the event
            }
            return false; // Let the default handling occur
        });



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                //appIcon.setVisibility(View.VISIBLE);
                String query = queryEditText.getText().toString();
                queryEditText.setText("");

                chatBody("You", query,getDrawable(R.drawable.user));

                geminiResp.getResponse(chatModel, query, new ResponseCallback(){
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        appIcon.setVisibility(View.VISIBLE);
                        chatBody("AI", response, getDrawable(R.drawable.generative));
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        progressBar.setVisibility(View.INVISIBLE);
                        //appIcon.setVisibility(View.VISIBLE);
                        chatBody("AI", "Sorry, I couldn't understand that.", getDrawable(R.drawable.generative));
                    }
                });
            }


        });



    }
    private ChatFutures getChatModel(){
        geminiResp model = new geminiResp();
        GenerativeModelFutures modelFutures = model.getModel();
        return modelFutures.startChat();
    }

    private void chatBody(String username, String query, Drawable drawable) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_message, null);
        ImageView logo = view.findViewById(R.id.logo);
        TextView name = view.findViewById(R.id.name);
        TextView message = view.findViewById(R.id.message);
        logo.setImageDrawable(drawable);
        name.setText(username);
        message.setText(query);
        chatResponse.addView(view);
        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

    }

}