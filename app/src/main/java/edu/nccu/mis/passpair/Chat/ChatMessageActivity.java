package edu.nccu.mis.passpair.Chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;

import edu.nccu.mis.passpair.Chat.Adapter.ChatMessageAdapter;
import edu.nccu.mis.passpair.Chat.Common.Common;
import edu.nccu.mis.passpair.Chat.Holder.QBChatMessageHolder;
import edu.nccu.mis.passpair.R;

public class ChatMessageActivity extends AppCompatActivity {
    QBChatDialog qbChatDialog;
    ListView lstChatMessage;
    ImageButton submitButton;
    EditText edtContent;
    ChatMessageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        initViews();
        initChatDialogs();
        retrieveAllMessage();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //建立訊息物件
                QBChatMessage chatMessage = new QBChatMessage();
                chatMessage.setBody(edtContent.getText().toString());
                chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                chatMessage.setSaveToHistory(true);
                //送出訊息
                try {
                    qbChatDialog.sendMessage(chatMessage);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                //更新聊天內容
                QBChatMessageHolder.getInstance().putMessage(qbChatDialog.getDialogId(),chatMessage);
                ArrayList<QBChatMessage> messages = QBChatMessageHolder.getInstance().getChatMessageById(qbChatDialog.getDialogId());
                adapter = new ChatMessageAdapter(getBaseContext(),messages);
                lstChatMessage.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                //移除EditText內的輸入資料
                edtContent.setText("");
                edtContent.setFocusable(true);
            }
        });
    }

    private void retrieveAllMessage() {
        //Builder for retrieving chat messages by filters
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500);
        if (qbChatDialog != null){
            //Retrieves chat messages count associated with a dialog.
            QBRestChatService.getDialogMessages(qbChatDialog,messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    //Put message to cache
                    QBChatMessageHolder.getInstance().putMessages(qbChatDialog.getDialogId(),qbChatMessages);
                    if (qbChatMessages.size() > 0){
                        adapter = new ChatMessageAdapter(getBaseContext(),qbChatMessages);
                        lstChatMessage.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
//                    adapter = new ChatMessageAdapter(getBaseContext(),qbChatMessages);
//                    lstChatMessage.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
    }

    private void initChatDialogs() {
        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.DIALOG_EXTRA);
        //開始聊天
        //Initializing current dialog for using chat functions.
        qbChatDialog.initForChat(QBChatService.getInstance());

        //Regiser listener Incoming messages
        QBIncomingMessagesManager incomingMessages = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessages.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        });
        //新增新聊天監聽器
        //Adds the incoming messages listener.
        qbChatDialog.addMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                //將新聊天內容儲存到Holder中
                //save message to cache and refresh listview
                QBChatMessageHolder.getInstance().putMessage(qbChatMessage.getDialogId(),qbChatMessage);
                ArrayList<QBChatMessage> messages = QBChatMessageHolder.getInstance().getChatMessageById(qbChatMessage.getDialogId());
                adapter = new ChatMessageAdapter(getBaseContext(),messages);
                lstChatMessage.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
                Log.e("Error",e.getMessage());
            }
        });
    }

    private void initViews() {
        lstChatMessage = (ListView) findViewById(R.id.list_og_message);
        submitButton = (ImageButton) findViewById(R.id.send_button);
        edtContent = (EditText) findViewById(R.id.edt_content);
    }
}
