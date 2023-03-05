package com.zv.geochat.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.zv.geochat.MainActivity;
import com.zv.geochat.R;

public class MyNotificationManager {

    private final Context context;
    private final String channelId = "my_channel_id";
    private final String channelName = "My Channel";
    private final String channelDesc = "My channel description";
    private final NotificationManagerCompat notificationManager;

    public MyNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDesc);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void displayCustomNotification(String desc,String message,String name,  String date) {
        RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.list_item_chat_message);
        notificationLayout.setTextViewText(R.id.txtUser, name);
        notificationLayout.setTextViewText(R.id.txtMessage, message);
        notificationLayout.setTextViewText(R.id.txtInfo, date);
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.my_avatar);
        notificationLayout.setImageViewBitmap(R.id.notification_image, largeIcon);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("GeoChat")
                .setContentText(desc)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
    }
}
