package Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.RapCodeTechnologies.Quiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Models.MessageModel;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private ArrayList<MessageModel> messageList;
    private Context context;
    private final int MSG_TYPE_RIGHT = 1;
    private String roomId;
    private final int MSG_TYPE_LEFT = 0;

    public ChatAdapter(Context context, ArrayList<MessageModel> messageList, String roomId) {
        this.context = context;
        this.messageList = messageList;
        this.roomId = roomId; // Initialize roomId
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ChatViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        holder.showMessage.setText(message.getMessage());

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String time = sdf.format(new Date(message.getTimestamp()));
        holder.showTime.setText(time);

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Report Message")
                    .setMessage("Do you want to report this message?")
                    .setPositiveButton("Report", (dialog, which) -> {
                        // Add the message to the reported messages in the database
                        reportMessage(message, roomId);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
            return true;
        });
    }

    private void reportMessage(MessageModel message, String roomId) {
        DatabaseReference reportedRef = FirebaseDatabase.getInstance().getReference("reportedMessages");
        String reportId = reportedRef.push().getKey();

        if (reportId != null) {
            reportedRef.child(reportId).setValue(new ReportedMessage(
                    roomId,
                    message.getSenderId(),
                    message.getMessage(),
                    message.getTimestamp()
            )).addOnSuccessListener(aVoid ->
                    Toast.makeText(context, "Message reported successfully.", Toast.LENGTH_SHORT).show()
            ).addOnFailureListener(e ->
                    Toast.makeText(context, "Failed to report message.", Toast.LENGTH_SHORT).show()
            );
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView showMessage, showTime;

        public ChatViewHolder(View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.chat_message);
            showTime = itemView.findViewById(R.id.chat_time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    class ReportedMessage {
        private String roomId;
        private String senderId;
        private String messageContent;
        private long timestamp;

        public ReportedMessage( String roomId, String senderId, String messageContent, long timestamp) {
            this.roomId = roomId;
            this.senderId = senderId;
            this.messageContent = messageContent;
            this.timestamp = timestamp;
        }



        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public void setMessageContent(String messageContent) {
            this.messageContent = messageContent;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}