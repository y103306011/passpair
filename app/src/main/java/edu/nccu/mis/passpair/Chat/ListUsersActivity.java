package edu.nccu.mis.passpair.Chat;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import edu.nccu.mis.passpair.Chat.Adapter.ListUserAdapter;
import edu.nccu.mis.passpair.Chat.Common.Common;
import edu.nccu.mis.passpair.Chat.Holder.QBUsersHolder;
import edu.nccu.mis.passpair.R;

public class ListUsersActivity extends AppCompatActivity {
    ListView lstUsers;
    Button btnCreateChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);
        retrieveAllUser();
        lstUsers = (ListView) findViewById(R.id.lstUsers);
        // 設定 ListView 選擇的方式 :
        // 單選 : ListView.CHOICE_MODE_SINGLE
        // 多選 : ListView.CHOICE_MODE_MULTIPLE
        lstUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        btnCreateChat = (Button) findViewById(R.id.btn_create_chat);
        btnCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countChoice = lstUsers.getCount();
                //getCheckedItemPositions(): Returns the set of checked items in the list.
                if (lstUsers.getCheckedItemPositions().size() == 1){
                    createPrivateChat(lstUsers.getCheckedItemPositions());
                }else if (lstUsers.getCheckedItemPositions().size() > 1){
                    createGroupChat(lstUsers.getCheckedItemPositions());
                }else {
                    Toast.makeText(ListUsersActivity.this,"Please select friend to chat",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createGroupChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mDialog = new ProgressDialog(ListUsersActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        int countChoice = lstUsers.getCount();
        ArrayList<Integer> occupantIdsList = new ArrayList<>();
        //loop all users, and if user has been selected , we will build chat dialog with this user
        for (int i= 0;i<countChoice;i++){
            //True表示該位置是選取的地方
            if (checkedItemPositions.get(i)){
                QBUser user = (QBUser) lstUsers.getItemAtPosition(i);
                occupantIdsList.add(user.getId());
            }
        }
        //Create Chat Dialog
        //QBChatDialog:  is responsible for all chat related operations
        QBChatDialog dialog = new QBChatDialog();
        dialog.setName(Common.createChatDialogName(occupantIdsList));
        //GROUP: The group dialog type. Users from dialog's occupants_ids will be able to chat in this dialog.
        //PRIVATE: The private dialog type.
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdsList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mDialog.dismiss();
                Toast.makeText(getBaseContext(),"Create chat dialog successfully",Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error",e.getMessage());
            }
        });
    }

    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mDialog = new ProgressDialog(ListUsersActivity.this);
        mDialog.setMessage("Please waiting...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        int countChoice = lstUsers.getCount();
        ArrayList<Integer> occupantIdsList = new ArrayList<>();
        //loop all users, and if user has been selected , we will build chat dialog with this user
        for (int i= 0;i<countChoice;i++){
            if (checkedItemPositions.get(i)){
                QBUser user = (QBUser) lstUsers.getItemAtPosition(i);
                //buildPrivateDialog(): Creates private dialog.
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());
                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        mDialog.dismiss();
                        Toast.makeText(getBaseContext(),"Create private chat dialog successfully",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("Error",e.getMessage());
                    }
                });
            }
        }
    }

    private void retrieveAllUser() {
        //getUsers() :Retrieve users
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                //add to cache
                //將先前讀進來的資料留著，預備下一次讀取
                QBUsersHolder.getInstance().putUsers(qbUsers);
                //Create new Arraylist to add all user from web services without current user logined
                ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<QBUser>();
                for (QBUser user : qbUsers){
                    if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin())){
                        qbUserWithoutCurrent.add(user);
                    }
                }
                ListUserAdapter adapter = new ListUserAdapter(getBaseContext(),qbUserWithoutCurrent);
                lstUsers.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error",e.getMessage());
            }
        });
    }
}
