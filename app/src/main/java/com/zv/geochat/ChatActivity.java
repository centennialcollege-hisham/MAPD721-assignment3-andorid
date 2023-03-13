package com.zv.geochat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import com.zv.geochat.fragment.MapActivity;
import com.zv.geochat.service.ChatService;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_history) {
            Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_leave) {
            leaveChat();
            finish(); // return to login screen
            return true;
        } else if (id == R.id.action_map) {
            startActivity(new Intent(this, MapActivity.class));

            return true;
        } else if (id == R.id.action_join_test) { // test only
            joinChat();
            return true;
        } else if (id == R.id.action_receive_message_test) { // test only
            simulateOnMessage();
            return true;
        } else if (id == R.id.action_leave_test) { // test only
            leaveChat();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void joinChat() {
        Bundle data = new Bundle();
        data.putInt(ChatService.CMD, ChatService.CMD_JOIN_CHAT);
        Intent intent = new Intent(this, ChatService.class);
        intent.putExtras(data);
        startService(intent);
    }


    private void leaveChat() {
        Bundle data = new Bundle();
        data.putInt(ChatService.CMD, ChatService.CMD_LEAVE_CHAT);
        Intent intent = new Intent(this, ChatService.class);
        intent.putExtras(data);
        startService(intent);
    }

    private void sendMessage(String messageText) {
        Bundle data = new Bundle();
        data.putInt(ChatService.CMD, ChatService.CMD_SEND_MESSAGE);
        data.putString(ChatService.KEY_MESSAGE_TEXT, messageText);
        Intent intent = new Intent(this, ChatService.class);
        intent.putExtras(data);
        startService(intent);
    }

    private void simulateOnMessage() {
        Bundle data = new Bundle();
        data.putInt(ChatService.CMD, ChatService.CMD_RECEIVE_MESSAGE);
        Intent intent = new Intent(this, ChatService.class);
        intent.putExtras(data);
        startService(intent);
    }


}
