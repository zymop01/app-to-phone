package fm.icelink.chat.websync4;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class NewService1 extends Service {
    private String notificationChannelId = "In Coming Call Notification";
    private NewService1 parent = this;
    private App app;
    private String sessionId = "";
    boolean allowToJoin = false, askToLeave = false, askToRejoinConference1 = false;
    boolean allowingToJoin = false;
    boolean askToLeaveConferenceThree = false, askToInviteToConference2 = false, askToRejoinConference2 = false;
    boolean askToChangeIdOfConference1 = false;
    private Context mContext;
    public boolean GroupChatStarted = false, LeaveChatStarted = false;
    boolean resetConference1 = false, resetConference1Finished = false;
    boolean askToRejoinConference1Phase2 = false;
    //private Thread2Service1 thread2Service1;
    //private Thread3Service1 thread3Service1;
    //test

    public NewService1() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        app = App.getInstance(this);
        mContext=getApplicationContext();
        if (intent != null && intent.getAction() != null && intent.getAction().equals("YOUR_ACTION")) {
            ToForeground(); // here you invoke the service method
        }
        else if (intent != null && intent.getAction() != null && intent.getAction().equals("allow_to_join_conference2")) {
            joinConference2();
        }
        else if (intent != null && intent.getAction() != null && intent.getAction().equals("leave_current_conference")) {
            leaveConference();
        }
        else if (intent != null && intent.getAction() != null && intent.getAction().equals("leave_conference_Three")) {
            leaveConferenceThree();
        }
        else if (intent != null && intent.getAction() != null && intent.getAction().equals("ask_to_rejoin_conference1")) {
            rejoinConference1();
        }
        else if (intent != null && intent.getAction() != null && intent.getAction().equals("ask_to_change_Id")) {
            changedIdOfConference1();
        }
        else if (intent != null && intent.getAction() != null && intent.getAction().equals("ask_to_rejoin_conference2")) {
            rejoinConference2();
        }
        else if (intent != null && intent.getAction() != null && intent.getAction().equals("ask_to_invite_to_conference2")) {
            inviteToConference2();
        }
        else if (intent != null && intent.getAction() != null && intent.getAction().equals("start_group_chat")) {
            joinGroupConference();
        }
        else if (intent != null && intent.getAction() != null && intent.getAction().equals("leave_group_chat")) {
            leaveGroupConference();
        }
        else {
            Thread1Service1 thread1Service1 = new Thread1Service1(parent);
            thread1Service1.start();

            Thread2Service1 thread2Service1 = new Thread2Service1(parent);
            thread2Service1.start();

            Thread3Service1 thread3Service1 = new Thread3Service1(parent);
            thread3Service1.start();

            Thread1Service3 thread1Service3 = new Thread1Service3(parent);
            thread1Service3.start();
            Thread4Service1 thread4Service1 = new Thread4Service1(parent);
            thread4Service1.start();
        }
        return START_STICKY;
    }

    public void ToForeground() {
        Intent notificationIntent = new Intent(this, SessionSelectorActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, notificationChannelId)
                .setContentTitle("In Coming Call")
                .setContentText("Someone is trying to call you")
                .setSmallIcon(R.drawable.notification)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();

        startForeground(1, notification);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationChannelId, "In Coming Call Notification", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void joinConference2() {
        allowToJoin = true;
    }

    //public void allowedToJoinConference2() {allowingToJoin = true; }

    public void leaveConference() {
        askToLeave = true;
    }

    public void leaveConferenceThree() {
        askToLeaveConferenceThree = true;
    }

    public void rejoinConference1() {
        askToRejoinConference1 = true;
    }

    public void changedIdOfConference1() {askToChangeIdOfConference1 = true;}

    public void rejoinConference2() {
        askToRejoinConference2 = true;
    }

    public void inviteToConference2() {
        askToInviteToConference2 = true;
    }

    public void joinGroupConference() {
        GroupChatStarted = true;
    }

    public void leaveGroupConference() {
        LeaveChatStarted = true;
    }

    public void joinAsync() {
        try {
            //if (!app.getSessionId().equals(app.lastJoinedConferenceId))
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

    void showToast(String str) {
        if (mContext != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class Thread1Service1 extends Thread {
        NewService1 parent;

        public Thread1Service1(NewService1 parent) {
            this.parent = parent;
        }

        public void run() {
            joinAsync();
            app.lastSignalling = app.signalling;
            while (true) {
                if (askToRejoinConference1) {
                    if(!app.threadTwoOn && !app.threadFourOn && !app.threadThreeOn) {
                        app.signalling = app.lastSignalling;
                        showToast("signalling back");
                        app.leaveAsync();
                        app.setSessionId(app.getCurrentId()+"1");
                        if (app.startInviting)
                            app.inviteGoBackConference1 = true;
                        askToRejoinConference1Phase2 = true;
                        joinAsync();
                        app.lastSignalling = app.signalling;
                        app.callingCounter = 0;
                        askToRejoinConference1 = false;
                    }
                }
                if (askToRejoinConference1Phase2) {

                }
                if (askToChangeIdOfConference1) {
                    app.signalling = app.lastSignalling;
                    app.leaveAsync();
                    app.setSessionId(app.getCurrentId()+"1");
                    app.setName(app.getCurrentId());
                    app.SetSelfName(app.getCurrentId());
                    joinAsync();
                    app.lastSignalling = app.signalling;
                    askToChangeIdOfConference1 = false;
                }
                //if (app.threadTwoOn) showToast("Thread 1 is running");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread2Service1 extends Thread {
        NewService1 parent;
        boolean leaveAsyncStarted = false;

        public Thread2Service1(NewService1 parent) {
            this.parent = parent;
        }

        public void run() {
            while (true) {
                if (askToLeave) {
                    app.leaveAsyncFinished = false;
                    app.leaveAsyncFinished();
                    leaveAsyncStarted = true;
                    askToLeave = false;
                    //app.threadTwoOn = false;
                }
                if (leaveAsyncStarted) {
                    if (app.leaveAsyncFinished) {
                        leaveAsyncStarted = false;
                        app.threadTwoOn = false;
                    }
                }
                if (allowToJoin && !app.threadTwoOn) {
                    app.setSessionId(app.conference2Id);
                    joinAsync();
                    app.threadTwoOn = true;
                    app.callingCounter = 0;
                    char lastLetter = app.getSessionId().charAt(app.getSessionId().length()-1);
                    if (lastLetter == '1') {
                        app.writeLine("COMMAND@ calling " + app.getCurrentId() + " .");
                        app.callingMsgSentOut = true;
                    }
                    app.invitingCounter = 0;
                    allowToJoin = false;
                }
                //showToast("2 on");
                /*
                if (askToRejoinConference2) {
                    if (!app.threadThreeOn) {
                        askToRejoinConference2 = false;
                        app.signalling = app.lastSignalling2;
                        app.setSessionId(app.conference2Id);
                        app.invitingCounter = 0;
                        app.writeLine("COMMAND@ finished inviting " + app.invitedId + " .");
                    }
                }

                 */
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class Thread3Service1 extends Thread {
        NewService1 parent;
        boolean leaveAsyncStarted = false;

        public Thread3Service1(NewService1 parent) {
            this.parent = parent;
        }

        public void run() {

            while (true) {
                if (askToLeaveConferenceThree) {
                    app.leaveAsyncTwoFinished = false;
                    app.leaveAsyncTwoFinished();
                    leaveAsyncStarted = true;
                    //app.leaveAsync();
                    askToLeaveConferenceThree = false;
                }
                if (leaveAsyncStarted) {
                    if (app.leaveAsyncTwoFinished) {
                        leaveAsyncStarted = false;
                        app.threadThreeOn = false;
                    }
                }
                if (askToInviteToConference2) {
                    app.lastSignalling2 = app.signalling;
                    //app.setSessionId(app.invitedConferenceId);
                    joinAsync();
                    app.threadThreeOn = true;
                    if (app.getSessionId().charAt(app.getSessionId().length()-1) == '1') {
                        app.writeLine("COMMAND@ inviting " + app.invitedConferenceId + " .");
                        Log.d("+++++","inviting");
                    }
                    askToInviteToConference2 = false;
                }
                //showToast("3 on");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread1Service3 extends Thread {
        NewService1 parent;
        boolean leaveAsyncGroupStarted = false;

        public Thread1Service3(NewService1 parent) {
            this.parent = parent;
        }

        public void run() {
            while (true) {
                if (GroupChatStarted && !app.threadFourOn) {
                    app.lastSignalling3 = app.signalling;
                    app.threadFourOn = true;
                    joinAsync();
                    GroupChatStarted = false;
                }
                if (LeaveChatStarted) {
                    app.leaveAsyncGroupFinished = false;
                    leaveAsyncGroupStarted = true;
                    app.leaveAsyncGroupFinished();

                    LeaveChatStarted = false;
                }
                if (leaveAsyncGroupStarted) {
                    if (app.leaveAsyncGroupFinished) {
                        //app.leaveAsync();
                        leaveAsyncGroupStarted = false;
                        app.threadFourOn = false;
                    }
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread4Service1 extends Thread {
        NewService1 parent;

        public Thread4Service1(NewService1 parent) {
            this.parent = parent;
        }

        public void run() {
            while (true) {

                if (app.foregroundMethodCalled) {
                    ToForeground();
                    app.foregroundMethodCalled = false;
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