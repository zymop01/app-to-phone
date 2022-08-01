package fm.icelink.chat.websync4;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class NewService4 extends Service {
    private NewService4 parent = this;
    private App app;
    private Context mContext;
    public NewService4() {
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
        else {
            NewService4.Thread1Service4 thread1Service4 = new NewService4.Thread1Service4(parent);
            thread1Service4.start();
            NewService4.Thread2Service4 thread2Service4 = new NewService4.Thread2Service4(parent);
            thread2Service4.start();
        }
        return START_STICKY;
    }

    public void ToForeground() {
        Intent bringToForegroundIntent = new Intent(this, DummyActivity.class);
        bringToForegroundIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        //bringToForegroundIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(bringToForegroundIntent);
    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
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

    class Thread1Service4 extends Thread {
        NewService4 parent;

        public Thread1Service4(NewService4 parent) {
            this.parent = parent;
        }

        public void run() {
            while (true) {
                if (app.Service1Thread2On) {
                    if (app.remoteMediaTable.size() == 0) {
                        app.Service1Thread2RemoteMediaCounter++;
                        if (app.Service1Thread2RemoteMediaCounter >= 60) {
                            app.Service1Thread2RemoteMediaCounter = 0;
                            app.timeOut = true;
                        }
                    }
                    else {
                        app.Service1Thread2RemoteMediaCounter = 0;
                    }
                    if (!app.Service1Thread2Active) {
                        app.Service1Thread2InactiveCounter++;
                        if (app.Service1Thread2InactiveCounter >= 7200) {
                            app.Service1Thread2InactiveCounter = 0;
                            app.timeOut = true;
                        }
                    }
                    else {
                        app.Service1Thread2Active = false;
                        app.Service1Thread2InactiveCounter = 0;
                    }
                    if (app.onPauseState) {
                        app.Service1Thread2BackgroundCounter++;

                        if (app.Service1Thread2BackgroundCounter >= 60) {
                            app.Service1Thread2BackgroundCounter = 0;
                            app.timeOut = true;
                        }
                    }
                    else {
                        app.Service1Thread2BackgroundCounter = 0;
                    }
                }
                else {
                    app.Service1Thread2RemoteMediaCounter = 0;
                    app.Service1Thread2InactiveCounter = 0;
                    app.Service1Thread2BackgroundCounter = 0;
                }
                if (app.Service3On) {
                    if (app.remoteMediaTable.size() == 0) {
                        app.Service3RemoteMediaCounter++;
                        showToast(String.valueOf(app.Service3RemoteMediaCounter));
                        if (app.Service3RemoteMediaCounter >= 60) {
                            app.Service3RemoteMediaCounter = 0;
                            app.timeOut = true;
                        }
                    }
                    else {
                        app.Service3RemoteMediaCounter = 0;
                    }
                    if (!app.Service3Active) {
                        app.Service3InactiveCounter++;
                        if (app.Service3InactiveCounter >= 7200) {
                            app.Service3InactiveCounter = 0;
                            app.timeOut = true;
                        }
                    }
                    else {
                        app.Service3Active = false;
                        app.Service3InactiveCounter = 0;
                    }
                    if (app.onPauseState) {
                        app.Service3BackgroundCounter++;
                        if (app.Service3BackgroundCounter >= 20) {
                            app.Service3BackgroundCounter = 0;
                            app.timeOut = true;
                        }
                    }
                    else {
                        app.Service3BackgroundCounter = 0;
                    }
                }
                else {
                    app.Service3RemoteMediaCounter = 0;
                    app.Service3InactiveCounter = 0;
                    app.Service3BackgroundCounter = 0;
                }
                if (app.sameConnectionOccurred) {
                    //app.timeOut = true;
                    app.sameConnectionOccurred = false;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread2Service4 extends Thread {
        NewService4 parent;

        public Thread2Service4(NewService4 parent) {
            this.parent = parent;
        }

        public void run() {
            while (true) {
                if (internetIsConnected()) {
                    app.hasInternetConnection = true;
                }
                else {
                    app.hasInternetConnection = false;
                }
                if (app.newConnectionFinished && app.newConnectionOccupied) {
                    if (app.newInvitingCounter++ > 5) {
                        app.newConnectionOccupied = false;
                        app.newInvitingCounter = -1;
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}