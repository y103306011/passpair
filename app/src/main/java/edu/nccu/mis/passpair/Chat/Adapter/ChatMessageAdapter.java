package edu.nccu.mis.passpair.Chat.Adapter;


import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.nccu.mis.passpair.Chat.Holder.QBUsersHolder;
import edu.nccu.mis.passpair.R;

public class ChatMessageAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<QBChatMessage> qbChatMessages;

    public ChatMessageAdapter(Context context, ArrayList<QBChatMessage> qbChatMessages) {
        this.context = context;
        this.qbChatMessages = qbChatMessages;
    }

    @Override
    public int getCount() {
        return qbChatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Long time = qbChatMessages.get(position).getDateSent() * 1000;
            String send_time_calendar ="";
            if (time == 0){
                send_time_calendar = "最近";
            }else {
                send_time_calendar = getDate(time);
            }
            ////判斷是送出方的訊息還是接收方的
            if (qbChatMessages.get(position).getSenderId().equals(QBChatService.getInstance().getUser().getId())){
                view = inflater.inflate(R.layout.list_send_message,null);
                TextView sendtime = (TextView) view.findViewById(R.id.messager_send_time);
                BubbleTextView bubbleTextView = (BubbleTextView) view.findViewById(R.id.message_content);
                //聊天內容
                bubbleTextView.setText(qbChatMessages.get(position).getBody());
                sendtime.setText(send_time_calendar);
            }else {
                view = inflater.inflate(R.layout.list_receive_message,null);
                BubbleTextView bubbleTextView = (BubbleTextView) view.findViewById(R.id.message_content);
                bubbleTextView.setText(qbChatMessages.get(position).getBody());
                TextView username = (TextView) view.findViewById(R.id.message_user);
                TextView sendtime = (TextView) view.findViewById(R.id.message_time);
                ImageView user_image = (ImageView) view.findViewById(R.id.message_rec_image);
                String sender_name = "未知使用者";
                if (QBUsersHolder.getInstance().getUserById(qbChatMessages.get(position).getSenderId()).getFullName() != null){
                    sender_name = QBUsersHolder.getInstance().getUserById(qbChatMessages.get(position).getSenderId()).getFullName();
                }
                username.setText(sender_name);
                sendtime.setText(send_time_calendar);

                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                int color2 = generator.getColor(sender_name);
                TextDrawable.IBuilder builder = TextDrawable.builder()
                        .beginConfig()
                        .withBorder(4)
                        .endConfig()
                        .round();
                TextDrawable image = builder.build(sender_name.substring(0,1).toUpperCase(), color2);
                user_image.setImageDrawable(image);
            }
        }
        return view;
    }
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.TAIWAN);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd HH:mm:ss", cal).toString();
        return date;
    }
}
