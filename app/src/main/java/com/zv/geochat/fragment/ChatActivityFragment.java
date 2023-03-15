package com.zv.geochat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.zv.geochat.Constants;
import com.zv.geochat.R;
import com.zv.geochat.adapter.ChatBubbleAdapter;
import com.zv.geochat.model.ChatMessage;
import com.zv.geochat.service.ChatService;

import java.util.ArrayList;

public class ChatActivityFragment extends Fragment {
    private static final String TAG = "ChatActivityFragment";
    private EditText edtMessage;
    private ListView messageListView;
    private ChatBubbleAdapter adapter;
    private LinearLayout layoutSessionClosed;
    private TableLayout tableLayout;
    private TextView textMessageClosed;
    private Button btnClosed;

    public ChatActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab_send_message);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(edtMessage.getText().toString());
                edtMessage.getText().clear();
            }
        });

        edtMessage = (EditText) v.findViewById(R.id.edtMessage);
        messageListView = (ListView) v.findViewById(R.id.messageList);
        layoutSessionClosed = (LinearLayout) v.findViewById(R.id.layout_session_closed);
        tableLayout = (TableLayout) v.findViewById(R.id.tableLayout);
        textMessageClosed = (TextView) v.findViewById(R.id.text_view_message);
        btnClosed = (Button) v.findViewById(R.id.button_close);

        btnClosed.setOnClickListener(view -> {
            getActivity().finish();
        });
        adapter = new ChatBubbleAdapter(getActivity(), new ArrayList<ChatMessage>());
        messageListView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerServiceStateChangeReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mServiceStateChangeReceiver);
    }

    private void sendMessage(String messageText) {
        Bundle data = new Bundle();
        data.putInt(ChatService.CMD, ChatService.CMD_SEND_MESSAGE);
        data.putString(ChatService.KEY_MESSAGE_TEXT, messageText);
        Intent intent = new Intent(getContext(), ChatService.class);
        intent.putExtras(data);
        getActivity().startService(intent);
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messageListView.setSelection(messageListView.getCount() - 1);
    }


    //------- listening broadcasts from service
    /**
     * Listens for service state change broadcasts
     */
    private final BroadcastReceiver mServiceStateChangeReceiver = new BroadcastReceiver() {
        private static final String TAG = "BroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle data = intent.getExtras();
            Log.d(TAG, "received broadcast message from service: " + action);

            if (Constants.BROADCAST_SERVER_CONNECTED.equals(action)) {
                ChatMessage chatMessage = new ChatMessage("Status: ", "Connected", true);
                displayMessage(chatMessage);
            } else if (Constants.BROADCAST_SERVER_NOT_CONNECTED.equals(action)) {
                ChatMessage chatMessage = new ChatMessage("Status: ", "Disconnected", true);
                displayMessage(chatMessage);
            } else if (Constants.BROADCAST_USER_JOINED.equals(action)) {
                String userName = data.getString(Constants.CHAT_USER_NAME);
                int userCount = data.getInt(Constants.CHAT_USER_COUNT, 0);
                ChatMessage chatMessage = new ChatMessage(userName, " joined. Users: " + userCount, true);
                displayMessage(chatMessage);
            } else if (Constants.BROADCAST_USER_LEFT.equals(action)) {
                String userName = data.getString(Constants.CHAT_USER_NAME);
                int userCount = data.getInt(Constants.CHAT_USER_COUNT, 0);
                ChatMessage chatMessage = new ChatMessage(userName, " left. Users: " + userCount, true);
                displayMessage(chatMessage);
            } else if (Constants.BROADCAST_NEW_MESSAGE.equals(action)) {
                String userName = data.getString(Constants.CHAT_USER_NAME);
                String message = data.getString(Constants.CHAT_MESSAGE);
                ChatMessage chatMessage = new ChatMessage(userName, message);
                displayMessage(chatMessage);
            } else if (Constants.BROADCAST_USER_TYPING.equals(action)) {
                // TODO
            } else if (Constants.BROADCAST_CHAT_MESSAGE_LIMIT.equals(action)) {
                String userName = data.getString(Constants.CHAT_USER_NAME);
                String message = data.getString(Constants.CHAT_MESSAGE);
                textMessageClosed.setText(message);
                tableLayout.setVisibility(View.GONE);
                layoutSessionClosed.setVisibility(View.VISIBLE);
                ChatMessage chatMessage = new ChatMessage(userName, message);
                displayMessage(chatMessage);

            } else {
                Log.v(TAG, "do nothing for action: " + action);
            }
        }
    };


    private void registerServiceStateChangeReceiver() {
        Log.d(TAG, "registering service state change receiver...");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_NEW_MESSAGE);
        intentFilter.addAction(Constants.BROADCAST_USER_TYPING);
        intentFilter.addAction(Constants.BROADCAST_SERVER_CONNECTED);
        intentFilter.addAction(Constants.BROADCAST_SERVER_NOT_CONNECTED);
        intentFilter.addAction(Constants.BROADCAST_USER_JOINED);
        intentFilter.addAction(Constants.BROADCAST_USER_LEFT);
        intentFilter.addAction(Constants.BROADCAST_CHAT_MESSAGE_LIMIT);
        getActivity().registerReceiver(mServiceStateChangeReceiver, intentFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ChatService.sessionMessageLimit = 0;
    }
}
