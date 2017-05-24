package edu.nccu.mis.passpair.Chat.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;

import edu.nccu.mis.passpair.R;

public class ChatDialogAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<QBChatDialog> qbChatDialogs;

    public ChatDialogAdapter(Context context, ArrayList<QBChatDialog> qbChatDialogs) {
        this.context = context;
        this.qbChatDialogs = qbChatDialogs;
    }


    @Override
    public int getCount() {
        return qbChatDialogs.size();
    }

    @Override
    public Object getItem(int position) {
        return qbChatDialogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_chat_dialog_layout,null);
            TextView txtTitle,txtMessage;
            ImageView imageView;
            txtTitle = (TextView) view.findViewById(R.id.list_chat_dialog_title);
            txtMessage = (TextView) view.findViewById(R.id.list_chat_dialog_message);
            imageView = (ImageView) view.findViewById(R.id.image_chatDialog);

            txtMessage.setText(qbChatDialogs.get(position).getLastMessage());
            txtTitle.setText(qbChatDialogs.get(position).getName());

            //Random color from Material color gallery(amulyakhare套件)
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int randomColor = generator.getRandomColor();
            //Build round drawable (amulyakhare套件)
            TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();
            //建立文字大頭貼(User的第一個字)
            //set text and color for drawable
            //builder.build(文字, color)
            TextDrawable drawable = builder.build(txtTitle.getText().toString().substring(0,1).toUpperCase(),randomColor);
            imageView.setImageDrawable(drawable);
        }
        return view;
    }
}
