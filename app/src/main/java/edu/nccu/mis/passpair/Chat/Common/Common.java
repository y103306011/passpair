package edu.nccu.mis.passpair.Chat.Common;


import com.quickblox.users.model.QBUser;

import java.util.List;

import edu.nccu.mis.passpair.Chat.Holder.QBUsersHolder;

public class Common {
    public static final String DIALOG_EXTRA = "Dialogs";

    public static String createChatDialogName(List<Integer> qbUsers){
        List<QBUser> qbUsers1 = QBUsersHolder.getInstance().getUsersById(qbUsers);
        StringBuilder name = new StringBuilder();
        for (QBUser user : qbUsers1){
            name.append(user.getFullName()).append(" ");
        }
        if (name.length() > 30){
            name = name.replace(30,name.length()-1,"...");
        }
        return name.toString();
    }
}
