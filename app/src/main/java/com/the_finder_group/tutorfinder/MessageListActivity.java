package com.the_finder_group.tutorfinder;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.the_finder_group.tutorfinder.ConnManager.TFClientImple;
import com.the_finder_group.tutorfinder.ConnManager.UserMessageDTO;
import com.the_finder_group.tutorfinder.Helper.Helper;
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
    private Helper helper;
    private Button send;
    private Integer  db_user_id, ad_owner_id;
    private String db_user_name, db_loggedUserType, message, timestamp, ad_owner_name;
    private EditText chat_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        messageList = new ArrayList<>();
        mMessageAdapter = new MessageListAdapter(this, messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chat_box = (EditText)findViewById(R.id.edittext_chatbox);

        //TFClient implementation
        tfClientImple = new TFClientImple();

        Intent intentGetAdDetails = getIntent();

        if(intentGetAdDetails.hasExtra("ad_owner_id")) {
            ad_owner_id = Integer.parseInt(intentGetAdDetails.getStringExtra("ad_owner_id"));
            ad_owner_name = intentGetAdDetails.getStringExtra("ad_owner_name");
        }

        helper = new Helper(getApplicationContext());

        db = new SQLiteHandler(getApplicationContext());
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        //Id de l'usuari que esta realitzant lel registre
        db_user_id = Integer.parseInt(user.get("user_id"));
        db_user_name = user.get("name");
        db_loggedUserType = user.get(getResources().getString(R.string.user_type));

        send = (Button)findViewById(R.id.button_chatbox_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = chat_box.getText().toString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM hh:mm");
                timestamp = simpleDateFormat.format(new Date());
                sendMessage();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mMessageAdapter);

    }

    public void sendMessage(){
        Log.d(TAG, "Send message");
        //Validem que les dades tinguin el format definit. En cas contrari informem a l'usuari/a

        new sendMessage().execute(String.valueOf(db_user_id),
                db_user_name, message, timestamp,
                String.valueOf(ad_owner_id), ad_owner_name);

    }

    private class sendMessage extends AsyncTask<String, Void, Boolean> {
        String  message, userName, time, adOwnerName;
        Integer userId, adOwnerId;

        @Override
        protected void onPreExecute(){ }

        @Override
        protected Boolean doInBackground(String... strings) {
            userId = Integer.parseInt(strings[0]);
            userName = strings[1];
            message = strings[2];
            time = strings[3];
            adOwnerId = Integer.parseInt(strings[4]);
            adOwnerName = strings [5];

            boolean publish = tfClientImple.createMessage(userId, userName, message, time,
                    adOwnerId, adOwnerName, getApplicationContext());

            return publish;
        }

        @Override
        protected void onPostExecute(Boolean result){
            super.onPostExecute(result);
            chat_box.setText("");
            if (result){
                UserMessageDTO userMessageDTO = new UserMessageDTO();
                userMessageDTO.setMessageUserId(userId);
                userMessageDTO.setMessageUserName(userName);
                userMessageDTO.setMessageText(message);
                userMessageDTO.setMessageDate(time);
                messageList.add(userMessageDTO);
            }
            // refreshing recycler view
            mMessageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            helper.redirectUserTypeAct(db_loggedUserType);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed(){
        //desabilitamos volver atras de la MainActivity
        moveTaskToBack(true);
    }
}
