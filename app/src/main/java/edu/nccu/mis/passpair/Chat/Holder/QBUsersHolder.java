package edu.nccu.mis.passpair.Chat.Holder;


import android.util.SparseArray;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class QBUsersHolder {
    private static QBUsersHolder instance;
    private SparseArray<QBUser> qbUserSparseArray;
    //Synchronized使用時，需指定一個物件，系統會Lock此物件，當程式進入Synchrnoized區塊或Method時，
    // 該物件會被Lock，直到離開Synchronized時才會被釋放。
    // 在Lock期間，鎖定同一物件的其他Synchronized區塊，會因為無法取得物件的Lock而等待。
    public static synchronized QBUsersHolder getInstance(){
        if (instance == null) {
            instance = new QBUsersHolder();
        }
        return instance;
    }
    private QBUsersHolder(){
        qbUserSparseArray = new SparseArray<>();
    }
    public void putUsers(List<QBUser> users){
        for (QBUser user : users){
            putUsers(user);
        }
    }

    private void putUsers(QBUser user) {
        qbUserSparseArray.put(user.getId(),user);
    }
    public QBUser getUserById(int id){
        return qbUserSparseArray.get(id);
    }
    public List<QBUser> getUsersById(List<Integer> ids){
        List<QBUser> qbUser = new ArrayList<>();
        for (Integer id : ids){
            QBUser user = getUserById(id);
            if (user != null){
                qbUser.add(user);
            }
        }
        return qbUser;
    }

}
