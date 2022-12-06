package com.github.msemitkin.chat.client.ui.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.msemitkin.chat.client.R;
import com.github.msemitkin.chat.client.tools.model.Message;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {

    TextView messageText;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.text_message_body);
    }

    public void bind(Message message) {
        messageText.setText(message.getText());
    }
}
