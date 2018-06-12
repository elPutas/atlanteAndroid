package com.sietecerouno.atlantetransportador.sections;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sietecerouno.atlantetransportador.R;
import com.sietecerouno.atlantetransportador.fragments.InmediatoFragment;
import com.sietecerouno.atlantetransportador.manager.Manager;
import com.sietecerouno.atlantetransportador.utils.Chat;
import com.sietecerouno.atlantetransportador.utils.Message;
import com.sietecerouno.atlantetransportador.utils.SendNotification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity
{
    String TAG = "GIO";
    String roomID = "";
    String idRequest;
    String idUser;
    String nameUser;
    ImageView btnBack;

    ListView lvChat;
    ArrayList<Message> mMessages;
    ChatListAdapter mAdapter;
    List<Message> chat;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    Query sChatQuery;


    EditText mMessageEdit;
    TextView btSend;

    FirebaseFirestore db;
    DocumentReference docRefUser;
    DocumentReference docRefPedido;
    DocumentReference docRefTransportista;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Manager.getInstance().actualContext = this;

        //title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView title = (TextView) findViewById(R.id.action_bar_title);
        ImageView btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnMenu.setVisibility(View.INVISIBLE);
        title.setText("CHAT");

        db = FirebaseFirestore.getInstance();
        sChatQuery = db.collection("salas_chat").limit(50);

        docRefUser = db.collection("usuarios").document(Manager.getInstance().actualClientId_finish);
        docRefPedido = db.collection("pedidos").document(Manager.getInstance().idPersonalizado);
        docRefTransportista = db.collection("usuarios").document(Manager.getInstance().myID);

        // set USER info from db user
        //DocumentReference _docRefUser = db.collection("usuarios").document(Manager.getInstance().actualClientId_inmediato);
        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        if(document.getData().get("token")!=null)
                        {
                            Manager.getInstance().actualClientToken = document.getData().get("token").toString();

                        }

                    } else {
                        Log.i(TAG, "No such document inside");
                    }
                } else {
                    Log.i(TAG, "get failed with inside", task.getException());
                }
            }
        });



        setupMessagePosting();
        // check if is first roomChat
        db.collection("salas_chat")
                .whereEqualTo("transportista", docRefTransportista)
                .whereEqualTo("user", docRefUser)
                .whereEqualTo("pedido", docRefPedido)
                .addSnapshotListener(new EventListener<QuerySnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                        @Nullable FirebaseFirestoreException e)
                    {
                        if (e != null)
                        {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if(documentSnapshots.size() == 0)
                        {
                            createFisrtTimeRoomChat();
                            return;
                        }

                        chat = new ArrayList<>();
                        for (final DocumentSnapshot document : documentSnapshots.getDocuments())
                        {
                            Log.i(TAG, "found."+ document.getId().toString());
                            roomID = document.getId().toString();

                            db.collection("salas_chat").document(roomID).collection("chat").orderBy("time_stamp", Query.Direction.ASCENDING).limit(30)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>()
                                    {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                                            @Nullable FirebaseFirestoreException e)
                                        {
                                            if (e != null)
                                            {
                                                Log.w(TAG, "Listen failed.", e);
                                                return;
                                            }




                                            chat.clear();
                                            for (final DocumentSnapshot document : documentSnapshots.getDocuments())
                                            {

                                                chat.add(new Message(document.getData().get("msj").toString(), document.getData().get("escribe").toString(),"","",""));
                                            }

                                            if(chat.size() > 0)
                                            {
                                                mAdapter = new ChatListAdapter(getBaseContext(),Manager.getInstance().myID, chat);
                                                lvChat.setAdapter(mAdapter);
                                                scrollMyListViewToBottom();
                                            }
                                        }

                                    });


                            return;
                        }
                    }

                });






    }

    void createFisrtTimeRoomChat()
    {

        Map<String, Object> user = new HashMap<>();
        user.put("user", docRefUser);
        user.put("pedido", docRefPedido);
        user.put("transportista", docRefTransportista);
        user.put("fecha", new Date());

        db.collection("salas_chat")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference document) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + document.getId());
                        roomID = document.getId().toString();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }


    // Get the userId from the cached currentUser object
    void startWithCurrentUser()
    {
        setupMessagePosting();
    }

    void setupMessagePosting()
    {
        // Find the text field and button
        mMessageEdit = (EditText) findViewById(R.id.etMessage);
        mMessageEdit.setText("");
        mMessageEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND)
                {
                    if(!Objects.equals(mMessageEdit.getText().toString(), ""))
                    {
                        String name = Manager.getInstance().myID;

                        onAddMessage(new Chat(Manager.getInstance().user_name, mMessageEdit.getText().toString(), name));
                        sendPush();
                        mMessageEdit.setText("");
                    }
                }
                return false;
            }
        });

        btSend = (TextView) findViewById(R.id.btSend);
        lvChat = (ListView) findViewById(R.id.lvChat);
        mMessages = new ArrayList<>();
        // Automatically scroll to the bottom when a data set change notification is received and only if the last item is already visible on screen. Don't scroll to the bottom otherwise.
        lvChat.setTranscriptMode(1);
        mFirstLoad = true;


        mAdapter = new ChatListAdapter(ChatActivity.this, "", mMessages);
        lvChat.setAdapter(mAdapter);
        scrollMyListViewToBottom();

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if(!Objects.equals(mMessageEdit.getText().toString(), ""))
                {

                    String name = Manager.getInstance().myID;

                    onAddMessage(new Chat(Manager.getInstance().user_name, mMessageEdit.getText().toString(), name));
                    sendPush();
                    mMessageEdit.setText("");
                }
            }
        });
    }

    private void sendPush()
    {
        SendNotification sendNotification = new SendNotification();
        sendNotification._id = Manager.getInstance().actualClientToken;
        sendNotification._body = mMessageEdit.getText().toString();
        sendNotification._title = "Te hablan";
        sendNotification.execute();
    }

    private void scrollMyListViewToBottom() {
        lvChat.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lvChat.setSelection(mAdapter.getCount() - 1);
            }
        });
    }

    protected void onAddMessage(Chat chat)
    {
        Map<String, Object> chatObj = new HashMap<>();

        chatObj.put("time_stamp", new Date());
        chatObj.put("msj", chat.getMessage());
        chatObj.put("escribe", Manager.getInstance().myID);


        db.collection("salas_chat").document(roomID).collection("chat").document()
                .set(chatObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void document) {
                        //Log.d(TAG, "DocumentSnapshot written with ID: " + document.getId());
                        //roomID = document.getId().toString();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        if (mMessages != null) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Manager.getInstance().actualContext = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


class ChatListAdapter extends ArrayAdapter<Message>
{
    private String mUserId;

    public ChatListAdapter(Context context, String userId, List<Message> messages) {
        super(context, 0, messages);
        this.mUserId = userId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.chat_item, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.body = (TextView)convertView.findViewById(R.id.tvBody);
            holder.bodyR = (TextView)convertView.findViewById(R.id.tvBody2);
            convertView.setTag(holder);

        }
        final Message message = getItem(position);
        final ViewHolder holder = (ViewHolder)convertView.getTag();
        boolean isMe = Objects.equals(message.getWHO_WRITE() , Manager.getInstance().myID);
        // Show-hide image based on the logged-in user.
        // Display the profile image to the right for our user, left for other users.
        if (isMe) {
            holder.body.setVisibility(View.VISIBLE);
            holder.bodyR.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            holder.body.setText(message.getBODY_KEY());
        } else {
            holder.bodyR.setVisibility(View.VISIBLE);
            holder.body.setVisibility(View.GONE);
            holder.bodyR.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            holder.bodyR.setText(message.getBODY_KEY());
        }

        return convertView;

    }



    final class ViewHolder {
        public ImageView imageOther;
        public ImageView imageMe;
        public TextView body;
        public TextView bodyR;
    }
}