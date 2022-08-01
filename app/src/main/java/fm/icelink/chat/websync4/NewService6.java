package fm.icelink.chat.websync4;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class NewService6 extends Service {
    private NewService6 parent = this;
    private App app;
    private Context mContext;

    public NewService6() {
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
            NewService6.Thread1Service6 thread1Service6 = new NewService6.Thread1Service6(parent);
            thread1Service6.start();
        }
        return START_STICKY;
    }

    public void ToForeground() {
        Intent bringToForegroundIntent = new Intent(this, DummyActivity.class);
        bringToForegroundIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        //bringToForegroundIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(bringToForegroundIntent);
    }

    class Thread1Service6 extends Thread {
        NewService6 parent;

        public Thread1Service6(NewService6 parent) {
            this.parent = parent;
        }

        public void run() {
            while (true) {

            }
        }
    }
}