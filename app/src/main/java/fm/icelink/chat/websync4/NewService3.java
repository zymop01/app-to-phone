package fm.icelink.chat.websync4;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class NewService3 extends Service {
    private NewService3 parent = this;
    private App app;
    private Context mContext;
    public boolean GroupChatStarted = false, LeaveChatStarted = false;

    public NewService3() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        app = App.getInstance(this);
        mContext=getApplicationContext();
        if (intent != null && intent.getAction() != null && intent.getAction().equals("YOUR_ACTION")) {
            ToForeground(); // here you invoke the service method
        }
        else if (intent != null && intent.getAction() != null && intent.getAction().equals("start_group_chat")) {
            joinGroupConference();
        }
        else if (intent != null && intent.getAction() != null && intent.getAction().equals("leave_group_chat")) {
            leaveGroupConference();
        }
        else {
            Thread1Service3 thread1Service3 = new Thread1Service3(parent);
            thread1Service3.start();
        }
        return START_STICKY;
    }

    public void ToForeground() {
        Intent bringToForegroundIntent = new Intent(this, DummyActivity.class);
        bringToForegroundIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        //bringToForegroundIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(bringToForegroundIntent);
    }

    public void joinGroupConference() {
        GroupChatStarted = true;
    }

    public void leaveGroupConference() {
        LeaveChatStarted = true;
    }

    public void joinAsync() {
        try {
            app.joinAsync();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    class Thread1Service3 extends Thread {
        NewService3 parent;

        public Thread1Service3(NewService3 parent) {
            this.parent = parent;
        }

        public void run() {
            while (true) {
                if (GroupChatStarted) {
                    joinAsync();
                    GroupChatStarted = false;
                }
                if (LeaveChatStarted) {
                    app.leaveAsync();
                    LeaveChatStarted = false;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


