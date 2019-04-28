package com.the_finder_group.tutorfinder;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.the_finder_group.tutorfinder.ConnManager.TFClientImple;
import com.the_finder_group.tutorfinder.ConnManager.UserMessageDTO;
import com.the_finder_group.tutorfinder.Helper.MessageListAdapter;
import com.the_finder_group.tutorfinder.Helper.SQLiteHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MessageListActivity extends AppCompatActivity {
    //Definim les variables
    private static final String TAG = MessageListActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private MessageListAdapter mMessageAdapter;
    private List<UserMessageDTO> messageList;
    private SQLiteHandler db;
    private TFClientImple tfClientImple;
    private Button send;
    private ProgressDialog pDialog;
    private Integer  db_user_id;
    private String db_user_name, message, timestamp;
    private EditText chat_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        recyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        messageList = new ArrayList<>();
        mMessageAdapter = new MessageListAdapter(this, messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chat_box = (EditText)findViewById(R.id.edittext_chatbox);

        //TFClient implementation
        tfClientImple = new TFClientImple();

        db = new SQLiteHandler(getApplicationContext());
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        //Id de l'usuari que esta realitzant lel registre
        db_user_id = Integer.parseInt(user.get("user_id"));
        db_user_name = user.get("name");

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mMessageAdapter);

        send = (Button)findViewById(R.id.button_chatbox_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = chat_box.getText().toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                timestamp = simpleDateFormat.format(new Date());
                sendMessage();
            }
        });
    }

    public void sendMessage(){
        Log.d(TAG, "Send message");
        //Validem que les dades tinguin el format definit. En cas contrari informem a l'usuari/a

        new sendMessage().execute(String.valueOf(db_user_id), db_user_name, message, timestamp);

    }

    private class sendMessage extends AsyncTask<String, Void, Boolean> {
        String  message, userName, time;
        Integer userId;

        @Override
        protected void onPreExecute(){
            pDialog.setMessage("Publishing");
            showDialog();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            userId = Integer.parseInt(strings[0]);
            userName = strings[1];
            message = strings[2];
            time = strings[3];

            boolean publish = tfClientImple.createMessage(userId, userName, message, time , getApplicationContext());
            return publish;
        }

        @Override
        protected void onPostExecute(Boolean result){
            super.onPostExecute(result);
            hideDialog();
            chat_box.setText("");
        }
    }

    //Mostrem el progres dialog
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    //Amaguem el progres dialog
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed(){
        //desabilitamos volver atras de la MainActivity
        moveTaskToBack(true);
    }
}
