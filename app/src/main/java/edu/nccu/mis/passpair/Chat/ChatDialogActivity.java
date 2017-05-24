package edu.nccu.mis.passpair.Chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import edu.nccu.mis.passpair.Chat.Adapter.ChatDialogAdapter;
import edu.nccu.mis.passpair.Chat.Common.Common;
import edu.nccu.mis.passpair.Chat.Holder.QBUsersHolder;
import edu.nccu.mis.passpair.R;

public class ChatDialogActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    ListView lstChatDialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialog);

        createSessionForChat();
        lstChatDialogs = (ListView) findViewById(R.id.lstChatDialogs);
        lstChatDialogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBChatDialog qbChatDialog = (QBChatDialog)lstChatDialogs.getAdapter().getItem(position);
                Intent intent = new Intent(ChatDialogActivity.this,ChatMessageActivity.class);
                intent.putExtra(Common.DIALOG_EXTRA,qbChatDialog);
                startActivity(intent);
            }
        });
        loadChatDialogs();


        floatingActionButton = (FloatingActionButton) findViewById(R.id.chatdialog_adduser);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDialogActivity.this,ListUsersActivity.class);
                startActivity(intent);

            }
        });
    }
    private void loadChatDialogs() {
        // allows to set parameters for a get/search request
        QBRequestGetBuilder requestbuilder = new QBRequestGetBuilder();
        //will get 100 newest dialogs
        requestbuilder.setLimit(100);
        //QBRestChatService used for perform REST queries associated with chat
        //getChatDialogs(type, requestBuilder)Retrieves chat dialogs associated with the current currentUser.
        //type - The type of a dialog to retrieve. Pass null to retrieve all types.
        QBRestChatService.getChatDialogs(null,requestbuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {
                //getBaseContext() 返回由构造函数指定或setBaseContext()设置的上下文(一般情况下不推荐使用这种方法)
                ChatDialogAdapter adapter = new ChatDialogAdapter(getBaseContext(),qbChatDialogs);
                lstChatDialogs.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error",e.getMessage());
            }
        });
    }

    private void createSessionForChat() {
        final ProgressDialog mDialog = new ProgressDialog(ChatDialogActivity.this);
        mDialog.setMessage("Please waiting...");
        //設為true為可以點選dialog以外範圍的區域，false則是不行
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        String user,password;
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");

        //load all user and save to cache
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

        final QBUser qbuser = new QBUser(user,password);
        //取得使用者的ID
        //Session creation with user with login
        QBAuth.createSession(qbuser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                //qbSession.getUserId():Get the user identifier (回傳值類別為Integer)
                qbuser.setId(qbSession.getUserId());
                try {
                    //getBaseService():Obtain BaseService singleton instance
                    //getToken():Get the session token
                    qbuser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }
                //登入聊天伺服器
                //Parameters: user - The currentUser to log in with. Required currentUser's fields: ID(不同於username), password.
                QBChatService.getInstance().login(qbuser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("Error",""+e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChatDialogs();
    }
}
