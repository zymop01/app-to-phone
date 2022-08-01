package fm.icelink.chat.websync4;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class NewService5 extends Service {
    private NewService5 parent = this;
    private App app;
    private Context mContext;

    public NewService5() {
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
            NewService5.Thread1Service5 thread1Service5 = new NewService5.Thread1Service5(parent);
            thread1Service5.start();
        }
        return START_STICKY;
    }

    public void ToForeground() {
        Intent bringToForegroundIntent = new Intent(this, DummyActivity.class);
        bringToForegroundIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        //bringToForegroundIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(bringToForegroundIntent);
    }

    class Thread1Service5 extends Thread {
        NewService5 parent;

        public Thread1Service5(NewService5 parent) {
            this.parent = parent;
        }

        public void run() {
            while (true) {

            }
        }
    }
}