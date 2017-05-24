package edu.nccu.mis.passpair.Chat.Holder;


import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QBChatMessageHolder {
    private static QBChatMessageHolder instance;
    private HashMap<String,ArrayList<QBChatMessage>> qbChatMessageArray;
    public static synchronized QBChatMessageHolder getInstance(){
        QBChatMessageHolder qbChatMessageHolder;
        synchronized (QBChatMessageHolder.class){
            if (instance == null) {
                instance = new QBChatMessageHolder();
            }
            qbChatMessageHolder = instance;
        }
        return qbChatMessageHolder;
    }

    private QBChatMessageHolder(){
        this.qbChatMessageArray = new HashMap<>();
    }
    public void putMessages(String dialogId,ArrayList<QBChatMessage> qbChatMessages){
        this.qbChatMessageArray.put(dialogId,qbChatMessages);
    }
    public void putMessage(String dialogId,QBChatMessage qbChatMessage){
        //取得同一聊天對象舊有聊天紀錄資料
        List<QBChatMessage> lstResult = (List) this.qbChatMessageArray.get(dialogId);
        //新增新聊天紀錄
        lstResult.add(qbChatMessage);
        ArrayList<QBChatMessage> lstAdded = new ArrayList<>(lstResult.size());
        //將新與舊的聊天紀錄結合成一Arraylist
        lstAdded.addAll(lstResult);
        //放入聊天紀錄的Arraylist
        putMessages(dialogId,lstAdded);
    }
    public ArrayList<QBChatMessage> getChatMessageById(String dialogId){
        return (ArrayList<QBChatMessage>)this.qbChatMessageArray.get(dialogId);
    }
}
