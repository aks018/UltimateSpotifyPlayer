package aviee.develop.music.myultimatemusicexperienceforspotify.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public  void onReceive(Context context, Intent intent){

        if (intent.getAction().equals(NotificationGenerator.NOTIFY_PLAY)){
            Toast.makeText(context, "NOTIFY_PLAY", Toast.LENGTH_LONG).show();
        } else if (intent.getAction().equals(NotificationGenerator.NOTIFY_PAUSE)){
            Toast.makeText(context, "NOTIFY_PAUSE", Toast.LENGTH_LONG).show();
        }else if (intent.getAction().equals(NotificationGenerator.NOTIFY_NEXT)){
            Toast.makeText(context, "NOTIFY_NEXT", Toast.LENGTH_LONG).show();
        }else if (intent.getAction().equals(NotificationGenerator.NOTIFY_DELETE)){
            Toast.makeText(context, "NOTIFY_DELETE", Toast.LENGTH_LONG).show();
        }else if (intent.getAction().equals(NotificationGenerator.NOTIFY_PREVIOUS)){
            Toast.makeText(context, "NOTIFY_PREVIOUS", Toast.LENGTH_LONG).show();
        }
    }
}