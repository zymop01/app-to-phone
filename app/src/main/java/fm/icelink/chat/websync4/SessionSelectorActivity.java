package fm.icelink.chat.websync4;


import android.Manifest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import fm.icelink.IAction0;
import fm.icelink.android.LayoutManager;
import fm.icelink.websync4.LeaveConferenceArgs;
import fm.icelink.chat.websync4.Contact;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Set;


public class SessionSelectorActivity extends AppCompatActivity implements App.OnReceivedTextListener, MediaPlayer.OnPreparedListener {
    private App app;
    //private OnTextReadyListener listener;
    private boolean enableH264 = false;
    private Context context = null;
    public static RelativeLayout container;

    private boolean onPauseState = false;
    private boolean zoomInLocalFlag = false, zoomOutLocalFlag = false;
    private boolean zoomInRemoteFlag = false, zoomOutRemoteFlag = false;

    MediaPlayer customMp;
    boolean mpIsPlaying = false;
    private int ringtoneSoundTimer = 0;
    private String m_androidId;

    private boolean inviteModeOn = false, beInvitedModeOn = false;
    private String invitedConferenceId = "", invitedId = "", IdTalkTo = "";
    public static HashMap<String, RemoteMedia> remoteMediaTable = new HashMap<>();
    public ArrayList<Contact> contactList = new ArrayList<>();

    private boolean setCurrentIdFinished = false, setCurrentIdSuccess = false;
    public String callingNumber = "";
    public int lastRingtoneType = 0;
    public boolean callingNumberClicked = false;

    Contacts contacts;
    ArrayAdapter contactArrayAdapter, ringtoneArrayAdapter;
    ContactDB contactDB;
    static IdDB idDB;

    private FrameLayout mMainFrame;
    private FirstPageFragment firstPageFragment;
    public static NewZoomInFragment newZoomInFragment;
    public static NewZoomOutFragment newZoomOutFragment;
    private MakingCallFragment makingCallFragment;
    private InComingCallFragment inComingCallFragment;
    private EmptyFragment emptyFragment;

    public boolean zoomUpdated = false;
    public ImageView captureImageView;
    private boolean requireUpdateContactList = false;
    private boolean missingCallsFlag = false;
    private String inviter;
    private boolean getContactListFinished = false;
    private boolean groupChatOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = App.getInstance(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //setContentView(R.layout.nine_screens);
        //setContentView(R.layout.nine_screens);
        setContentView(R.layout.new_main_screen);

        /*
        if (!app.fromDummy) {
            Intent DummyIntent = new Intent(this, DummyActivity.class);
            startActivity(DummyIntent);
        }

         */

        contactDB = new ContactDB(SessionSelectorActivity.this);
        idDB = new IdDB(SessionSelectorActivity.this);
        //checkPermission();
        isPermissionGranted();

        mMainFrame = (FrameLayout) findViewById(R.id.newMainFrame);

        firstPageFragment = new FirstPageFragment();
        newZoomInFragment = new NewZoomInFragment();
        newZoomOutFragment = new NewZoomOutFragment();
        makingCallFragment = new MakingCallFragment();
        inComingCallFragment = new InComingCallFragment();
        emptyFragment = new EmptyFragment();

        setFragment(R.id.newMainFrame, firstPageFragment);

        lgtechKey a = new lgtechKey();
        if(!a.lgtechKeyFun())
        {
            alert("invalid Lgtech key");
        }
        if (idDB.isEmpty()) {
            m_androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            setCurrentId(m_androidId);
        }
        String currentId = idDB.getId();
        app.setSessionId(currentId+"1");
        app.setName(currentId);
        app.SetSelfName(currentId);

        if (!app.newService1Started) {
            app.newService1Started = true;

            startService(new Intent(this, NewService1.class));
            //startService(new Intent(this, NewService3.class));
            startService(new Intent(this, NewService4.class));
            app.textListener = SessionSelectorActivity.this;
            startMedia();



            UpdateThread updateThread = new UpdateThread();
            updateThread.start();
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void setFragment(int i, androidx.fragment.app.Fragment fragment) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(i, fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        SystemClock.sleep(2000);
        super.onDestroy();
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 42 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (data == null) {
                alert("Must allow screen sharing before the chat can start.");
            } else {
                MediaProjectionManager manager = (MediaProjectionManager) this.getSystemService(MEDIA_PROJECTION_SERVICE);
                app.setMediaProjection(manager.getMediaProjection(resultCode, data));
            }
        }
        if (requestCode == 100) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            captureImageView.setImageBitmap(bitmap);
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "capture" , "test");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onPauseState = true;
        app.onPauseState = true;
        //callService();
    }

    @Override
    public void onResume() {
        super.onResume();
        onPauseState = false;
        app.onPauseState = false;
    }

    public void alert(String format, Object... args) {
        final String text = String.format(format, args);
        final Activity self = this;
        self.runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (!isFinishing())
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(self);
                    alert.setMessage(text);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        { }
                    });
                    alert.show();
                }
            }
        });
    }

    public void onReceivedText(final String name, String message) {
        final String concatMessage = name + ": " + message + "\n\n";

        writeLine(new SpannableString(concatMessage)
        {{
            setSpan(new StyleSpan(Typeface.BOLD), 0, name.length(), 0);
        }});
    }

    public void onPeerJoined(String name) {
        final String message = "COMMAND@ " + name + " has joined.\n";
        writeLine(new SpannableString(message)
        {{
            setSpan(new StyleSpan(Typeface.BOLD), 0, message.length(), 0);
        }});
    }

    public void onPeerLeft(String name) {
        final String message = "COMMAND@ " + name + " has left.\n";
        app.removeName(name);

        //if (app.getSessionId().equals("9619889a64f2a4332") || app.getSessionId().equals("d252badfc5e6cef62") || app.getSessionId().equals("f4b1292eba0c54f72") || app.getSessionId().equals("e1cf30f7e9b5775b2")) {
        char lastLetter = app.getSessionId().charAt(app.getSessionId().length()-1);
        if (lastLetter == '2') {
            remoteMediaTable.remove(name);
        }

        writeLine(new SpannableString(message)
        {{
            setSpan(new StyleSpan(Typeface.BOLD), 0, message.length(), 0);
        }});
    }

    public void callService() {
        Intent i = new Intent(this, NewService1.class);
        i.setAction("YOUR_ACTION");
        startService(i);
    }

    public void allowedToJoinConference2(String str) {
        app.conference2Id = str;
        Intent i = new Intent(this, NewService1.class);
        i.setAction("allow_to_join_conference2");
        startService(i);
    }

    /*
    public void allowToJoinConference2(String str) {
        app.setSessionId(str);
        Intent i = new Intent(this, NewService1.class);
        i.setAction("allowing_to_join_conference2");
        startService(i);
    }

     */

    public void askToLeave() {
        Intent i = new Intent(this, NewService1.class);
        i.setAction("leave_current_conference");
        startService(i);
    }

    public void askToLeaveConferenceThree() {
        Intent i = new Intent(this, NewService1.class);
        i.setAction("leave_conference_Three");
        startService(i);
    }

    public void askToLeaveGroupChat() {
        Intent i = new Intent(this, NewService1.class);
        i.setAction("leave_group_chat");
        startService(i);
    }

    public void askToInvite(String str) {
        app.setSessionId(str);
        Intent i = new Intent(this, NewService1.class);
        i.setAction("ask_to_invite_to_conference2");
        startService(i);
    }

    public void askToChangeId() {
        Intent i = new Intent(this, NewService1.class);
        i.setAction("ask_to_change_Id");
        startService(i);
    }

    public void askToRejoinConference1() {
        Intent i = new Intent(this, NewService1.class);
        i.setAction("ask_to_rejoin_conference1");
        startService(i);
    }

    public void askToRejoinConference2() {
        Intent i = new Intent(this, NewService1.class);
        i.setAction("ask_to_rejoin_conference2");
        startService(i);
    }

    public void startGroupConference(String str) {
        app.groupChatStarted = true;
        app.setSessionId(str);
        Intent i = new Intent(this, NewService1.class);
        i.setAction("start_group_chat");
        startService(i);
    }

    public void shortToast(String str) {
        if (getApplicationContext() != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void writeLine(final SpannableString str) {
        if (SessionSelectorActivity.this != null) {
            SessionSelectorActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (newZoomOutFragment.newChattingReady) {
                        //app.Service1Thread2Active = true;
                        //app.Service3Active = true;
                        newZoomOutFragment.textView.setText(TextUtils.concat(str, newZoomOutFragment.textView.getText()));
                    }

                    String s = "" + str;
                    Log.d("receivedMsg", s);
                    if (s.contains(app.getCurrentId()+"1")) {
                        if (app.threadTwoOn)
                            shortToast(s);
                    }
                    String[] separated = s.split(" ");
                    if (s.contains("COMMAND@ calling")) {
                        if (onPauseState)
                            callService();
                        invitedId = separated[3];
                        if (groupChatOn || app.threadTwoOn || app.threadThreeOn) {
                            createMissingCall(-1, invitedId);
                        }
                        else if (app.getSessionId().charAt(app.getSessionId().length()-1) == '1') {
                            writeString("COMMAND@ ringing");

                            lastRingtoneType = contactDB.getRingtoneById(invitedId);
                            inComingCallFragment.inComingId = separated[3];
                            setFragment(R.id.newMainFrame, inComingCallFragment);
                            setRingtone(true);
                            addOneHistory(invitedId);
                        }
                    }
                    if (s.contains("COMMAND@ received InvitingMsg")) {
                        app.invitingCounter = -1;

                        shortToast("inviting msg received");
                    }
                    if (s.contains("Command@ is inviting")) {
                        String tmpName = separated[4];
                        shortToast("name: " + tmpName);
                        app.isInvitedNames.add(tmpName);

                    }
                    if (s.contains("COMMAND@ finished inviting")) {
                        String tmpName = separated[4];
                        shortToast("remove name: " + tmpName);
                        app.isInvitedNames.remove(tmpName);

                    }

                    if (s.contains("COMMAND@ inviting")) {
                        if (onPauseState)
                            callService();
                        if (groupChatOn || app.threadTwoOn) {
                            createMissingCall(-1, separated[3]);
                        }
                        else if (app.getSessionId().charAt(app.getSessionId().length()-1) == '1') {
                            writeString("COMMAND@ received InvitingMsg");
                            setRingtone(true);
                            beInvitedModeOn = true;
                            inviter = separated[0].substring(0, separated[0].length()-1);
                            inComingCallFragment.inComingId = inviter;
                            setFragment(R.id.newMainFrame, inComingCallFragment);
                            invitedConferenceId = separated[3];

                            Log.d("+++++", invitedConferenceId);
                            addOneHistory(inviter);
                        }
                    }
                    if (s.contains("COMMAND@ ringing")) {
                        invitedId = "";
                        setRingtone(true);
                        app.callingMsgSentOut = false;
                        app.callingMsgSentOutTimeCount = 0;
                        makingCallFragment.IdCallTo = app.getSessionId().substring(0, app.getSessionId().length()-1);
                        setFragment(R.id.newMainFrame, makingCallFragment);
                    }
                    if (s.contains("COMMAND@ accepted")) {
                        setRingtone(false);
                        setFragment(R.id.newMainFrame, emptyFragment);
                        if (inviteModeOn) {
                            //app.setSessionId(app.conference2Id);
                            app.newInvitingCounter = -1;
                            askToLeaveConferenceThree();
                            app.neededName = app.getSessionId().substring(0,app.getSessionId().length()-1);
                            Log.d("neededName", app.neededName);
                            if (app.localViewReady = true)
                                app.stopLocalMedia();
                            app.neededToRejoinConference2 = true;
                            app.neededToRejoinConference1 = true;
                            inviteModeOn = false;
                        }
                        else {
                            app.Service1Thread2On = true;
                            askToLeave();
                            app.neededName = app.getSessionId().substring(0,app.getSessionId().length()-1);
                            if (app.localViewReady = true)
                                app.stopLocalMedia();
                            app.ANeededToJoinConference2 = true;
                            app.neededToRejoinConference1 = true;
                            Log.d("neededName", app.neededName);
                        }
                    }

                    if (s.contains("COMMAND@ declined")) {
                        setRingtone(false);
                        if (!app.getSessionId().equals(app.getCurrentId()+"1")) {
                            if (inviteModeOn) {
                                app.newInvitingCounter = -1;
                                setFragment(R.id.newMainFrame, emptyFragment);
                                askToLeaveConferenceThree();
                                app.neededName = app.getSessionId().substring(0,app.getSessionId().length()-1);
                                Log.d("neededName", app.neededName);
                                if (app.localViewReady = true)
                                    app.stopLocalMedia();
                                app.neededToRejoinConference2 = true;
                                app.neededToRejoinConference1 = true;

                                inviteModeOn = false;
                            }
                            else if (!app.isInConference && !groupChatOn) {
                                setFragment(R.id.newMainFrame, firstPageFragment);
                                askToLeave();
                                app.neededName = app.getSessionId().substring(0,app.getSessionId().length()-1);
                                Log.d("neededName", app.neededName);
                                if (app.localViewReady = true)
                                    app.stopLocalMedia();
                                app.neededToRejoinConference1 = true;
                            }
                        }
                        else {
                            //addOneHistory(invitedId);
                            setFragment(R.id.newMainFrame, firstPageFragment);
                        }
                    }
                    /*
                    if (s.contains("COMMAND@ exit") && !s.contains(app.getCurrentId()+"1")) {
                        if (app.threadTwoOn) {
                            if (app.localViewReady) {
                                app.stopLocalMedia();
                                app.groupChatStarted = false;
                                groupChatOn = false;
                                askToLeave();
                                askToRejoinConference1();
                                remoteMediaTable.clear();
                                app.remoteMediaTable.clear();
                                setRingtone(false);
                            }
                        }
                        //if (app.getSessionId().equals("groupChat")) {
                            //app.leaveAsync();
                        //}
                    }

                     */

                    if (!s.contains("COMMAND@")) {
                        addOneMsg(s);
                    }


                }
            });
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        customMp.start();
    }

    public interface OnTextReadyListener {
        void onNewMessage();
    }

    private void startMedia() {
        if (app.localViewStopped)
            app.startLocalMedia();
    }

    public void setZoomInLocalFlag(boolean b) {
        zoomInLocalFlag = b;
    }

    public void setZoomOutLocalFlag(boolean b) {
        zoomOutLocalFlag = b;
    }

    public void setZoomInRemoteFlag(boolean b) {
        zoomInRemoteFlag = b;
    }

    public void setZoomOutRemoteFlag(boolean b) {
        zoomOutRemoteFlag = b;
    }

    public boolean getZoomInLocalFlag() {
        return zoomInLocalFlag;
    }

    public boolean getZoomOutLocalFlag() {
        return zoomOutLocalFlag;
    }

    public boolean getZoomInRemoteFlag() {
        return zoomInRemoteFlag;
    }

    public boolean getZoomOutRemoteFlag() {
        return zoomOutRemoteFlag;
    }

    public void writeString(String msg) {
        app.writeLine(msg);
    }

    public void addOneMsg(String msg) {
        contactDB.addMsg(IdTalkTo, msg);
    }

    public void setRingtone(boolean b) {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (b) {
                    if (mpIsPlaying) {
                        mpIsPlaying = false;
                        customMp.stop();
                        customMp.reset();
                    }
                    if (lastRingtoneType == 1) {
                        customMp = MediaPlayer.create(getApplicationContext(), R.raw.by_the_seaside_ios);
                    }
                    else if (lastRingtoneType == 2) {
                        customMp = MediaPlayer.create(getApplicationContext(), R.raw.playtime);
                    }
                    else {
                        customMp = MediaPlayer.create(getApplicationContext(), R.raw.silk);
                    }
                    customMp.start();
                    mpIsPlaying = true;
                    customMp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mPlayer) {
                            //customMp.release();
                            //mpIsPlaying = false;
                            customMp.start();
                            //mpIsPlaying = true;
                        }

                    });
                }
                else {
                    if (mpIsPlaying) {
                        customMp.stop();
                        customMp.release();
                        mpIsPlaying = false;
                    }
                }
            }
        });
    }

    public void testRingtone(int i) {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (i == -1) {
                    if (mpIsPlaying) {
                        customMp.stop();
                        customMp.release();
                        mpIsPlaying = false;
                    }
                    return;
                }
                if (mpIsPlaying) {
                    mpIsPlaying = false;
                    customMp.stop();
                    customMp.reset();
                }
                customMp = MediaPlayer.create(getApplicationContext(), i);
                customMp.start();
                mpIsPlaying = true;

                customMp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mPlayer) {
                        customMp.release();
                        mpIsPlaying = false;
                    }
                });
            }
        });
    }

    private boolean checkLocalMediaReady() {
        boolean passed = false;
        if (app.localViewReady && app.localMedia != null) {
            try {
                app.localMedia.setAudioMuted(false);
                passed = true;
            }
            catch (Exception e) {
                //app.stopLocalMedia();
                shortToast("reset LocalMedia");
            }
        }
        if (passed) return true;
        return false;
    }

    public  boolean isPermissionGranted() {
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("In Coming Call Notification", "In Coming Call Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

         */

        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted");
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                boolean isPermissionForAllGranted = false;
                if (grantResults.length > 0 && permissions.length == grantResults.length) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isPermissionForAllGranted = true;
                        } else {
                            isPermissionForAllGranted = false;
                        }
                    }
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    isPermissionForAllGranted = true;
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                if (isPermissionForAllGranted) {
                    startMedia();
                }
                break;
        }
    }

    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private void getContactList() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getContactListFinished = false;
                ContentResolver cr = getContentResolver();

                Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                if (cursor != null) {
                    HashSet<String> mobileNoSet = new HashSet<String>();
                    try {
                        final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                        String name, number;
                        while (cursor.moveToNext()) {
                            name = cursor.getString(nameIndex);
                            number = cursor.getString(numberIndex);
                            number = number.replace(" ", "");
                            //contactListFragment.mCLArrayAdapter.add(number);
                            addOneContact(number);
                            //createMsgTable(number);
                            if (!mobileNoSet.contains(number)) {
                                contactList.add(new Contact(name, number));
                                mobileNoSet.add(number);
                                Log.d("hvy", "onCreaterrView  Phone Number: name = " + name
                                        + " No = " + number);
                            }
                        }
                    } finally {
                        cursor.close();
                    }
                }
                getContactListFinished = true;
            }
        });
    }

    private void createContacts(int id, String contact) {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                contacts = new Contacts(-1, contact, 1);
                contactDB.addOne(contacts);
            }
        });
    }

    private void updateContacts(int id, String contact) {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                contacts = new Contacts(-1, contact, 1);
                idDB.addOne(contacts);
                setCurrentIdFinished = true;
            }
        });
    }

    private void createHistory(int id, String contact) {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                contacts = new Contacts(-1, contact, R.raw.by_the_seaside_ios);
                contactDB.addOneToHistory(contacts);
            }
        });
    }

    private void createMsgTable(String str) {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (contactDB.createMsgTable(str)) {
                    shortToast("Table: " + str + " created");
                }
                else {
                    shortToast("Table: " + str + " a");
                }
            }
        });
    }

    private void createMissingCall(int id, String contact) {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                contacts = new Contacts(-1, contact, R.raw.by_the_seaside_ios);
                contactDB.addOneToMissingCalls(contacts);
            }
        });
    }


    public void deleteOneContact(Contacts clickedContact) {
        contactDB.deleteOne(clickedContact);
        showContactOnListView(contactDB);
        shortToast("Deleted: " + clickedContact);
    }

    public void deleteTable(String str) {
        contactDB.deleteTable(str);
    }

    public void deleteHistory(Contacts clickedContact) {
        contactDB.deleteHistory(clickedContact);
        showHistoryOnListView(contactDB);
        shortToast("Deleted: " + clickedContact);
    }

    public void deleteMissingCall(Contacts clickedContact) {
        contactDB.deleteMissingCall(clickedContact);
        showMissingCallsOnListView(contactDB);
        shortToast("Deleted: " + clickedContact);
    }

    public void deleteId(Contacts clickedContact) {
        idDB.deleteAll();
        showContactOnListView(idDB);
        shortToast("Deleted: " + clickedContact);
    }

    public void addOneContact(String str) {
        try{
            createContacts(-1, str);
        } catch (Exception e) {
            shortToast("Error creating customer");
        }

        //shortToast("Success= " + success);
        //showContactOnListView(contactDB);
    }

    public void addOneHistory(String str) {
        try{
            createHistory(-1, str);
        } catch (Exception e) {
            shortToast("Error creating customer");
        }

        //shortToast("Success= " + success);
        //showContactOnListView(contactDB);
    }

    public void setCurrentId(String str) {
        //boolean tmp = false;
        //if (!idDB.isEmpty()) tmp = true;
        //idDB.deleteAll();

        try{
            updateContacts(-1, str);
        } catch (Exception e) {
            shortToast("Error creating Id");
        }

    }

    private void showContactOnListView(IdDB idDB) {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                contactArrayAdapter = new ArrayAdapter<Contacts>(SessionSelectorActivity.this, android.R.layout.simple_list_item_1, idDB.getEveryone());
                //contactListFragment.mContactListView.setAdapter(contactArrayAdapter);
                //contactListButtonControlFragment.deviceId.setText("Conference Id: "+app.getCurrentId()+"1");
            }
        });
    }

    private void showContactOnListView(ContactDB contactDB) {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                contactArrayAdapter = new ArrayAdapter<Contacts>(SessionSelectorActivity.this, android.R.layout.simple_list_item_1, contactDB.getEveryone());
                ringtoneArrayAdapter = new ArrayAdapter<String>(SessionSelectorActivity.this, android.R.layout.simple_list_item_1, contactDB.getEveryRingtone());
                //contactListFragment.mContactListView.setAdapter(contactArrayAdapter);
                firstPageFragment.mContactListView.setAdapter(contactArrayAdapter);
                firstPageFragment.mRingtoneListView.setAdapter(ringtoneArrayAdapter);
                //mContactListView.setAdapter(contactArrayAdapter);
            }
        });
    }

    private void showHistoryOnListView(ContactDB contactDB) {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                contactArrayAdapter = new ArrayAdapter<Contacts>(SessionSelectorActivity.this, android.R.layout.simple_list_item_1, contactDB.getEveryoneInHistory());
                //contactListFragment.mContactListView.setAdapter(contactArrayAdapter);
                firstPageFragment.mContactListView.setAdapter(contactArrayAdapter);
                firstPageFragment.mRingtoneListView.setAdapter(null);
                //mContactListView.setAdapter(contactArrayAdapter);
            }
        });
    }

    private void showMissingCallsOnListView(ContactDB contactDB) {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                contactArrayAdapter = new ArrayAdapter<Contacts>(SessionSelectorActivity.this, android.R.layout.simple_list_item_1, contactDB.getEveryoneInMissingCalls());
                //contactListFragment.mContactListView.setAdapter(contactArrayAdapter);
                firstPageFragment.mContactListView.setAdapter(contactArrayAdapter);
                firstPageFragment.mRingtoneListView.setAdapter(null);
                //mContactListView.setAdapter(contactArrayAdapter);
            }
        });
    }

    public String getSessionId() {
        return app.getSessionId();
    }

    public static String getCurrentId() {
        String idNow = idDB.getId();
        return idNow;
    }

    public void callContactClicked(String str) {
        if (app.hasInternetConnection) {
            //shortToast("has internet connection");
            if (!str.equals(app.getCurrentId())
                    && !app.getSessionId().equals(str+"1")
                    && !app.isInvitedNames.contains(str)) {
                addOneHistory(str);
                callingNumber = str;
                app.invitedId = str;
                int ringtoneType = contactDB.getRingtoneById(str);
                lastRingtoneType = ringtoneType;
                callingNumberClicked = true;
            }
        }
        else {
            shortToast("Please check your internet connection");
        }
    }

    public void deleteContactClicked(Contacts contacts, String str) {
        if (str.equals("contactList")) {
            deleteOneContact(contacts);
            deleteTable(contacts.getContact());
        }
        else if (str.equals("history")) {
            deleteHistory(contacts);
        }
        else {
            deleteMissingCall(contacts);
        }
    }

    public void popUpDialogOfDelete(Contacts contacts, String str) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SessionSelectorActivity.this);
        builder1.setMessage("Are you sure to delete this entry?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteContactClicked(contacts, str);
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void setRingtoneClicked(Contacts contacts) {
        showPopUpRingtoneSelect(contacts);
    }


    public void showPopUpWindow() { //inviteButton
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_invite, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        EditText InviteEt = popupView.findViewById(R.id.popUpInviteET);
        ListView listView = popupView.findViewById(R.id.ListViewInInvite);
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                contactArrayAdapter = new ArrayAdapter<Contacts>(SessionSelectorActivity.this, android.R.layout.simple_list_item_1, contactDB.getEveryone());
                listView.setAdapter(contactArrayAdapter);
            }
        });
        ImageView addButton = popupView.findViewById(R.id.AddButtonInInvite);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InviteEt.getText().toString().length() > 0) {
                    addOneContact(InviteEt.getText().toString());
                    createMsgTable(InviteEt.getText().toString());
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            hideSoftKeyBoard();
                            contactArrayAdapter = new ArrayAdapter<Contacts>(SessionSelectorActivity.this, android.R.layout.simple_list_item_1, contactDB.getEveryone());
                            listView.setAdapter(contactArrayAdapter);
                        }
                    });
                }
            }
        });

        ImageView cancelButton = popupView.findViewById(R.id.CancelButtonInInvite);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contacts clickedContact = (Contacts) parent.getItemAtPosition(position);
                String isGroupChat = "groupchat_";
                String curr = clickedContact.getContact();
                if (curr.length() > isGroupChat.length()
                        && curr.startsWith(isGroupChat)) {
                    shortToast(curr+ " can not be invited.");
                }
                else if (!app.names.contains(curr)
                        && !curr.equals(app.getCurrentId())) {
                    app.lastAudioMuted = newZoomOutFragment.audioMuted;
                    app.forcedExit = true;
                    app.inviteStr = curr;
                    app.startInviting = true;

                    popupWindow.dismiss();
                }
                else {
                    shortToast(curr+ " is Already here.");
                }
            }
        });
    }

    public void showPopUpIdWindow() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_id, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        EditText IdEt = popupView.findViewById(R.id.popUpIdET);

        ImageView addButton = popupView.findViewById(R.id.AddButtonInId);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IdEt.getText().toString().length() > 0) {
                    setCurrentId(IdEt.getText().toString());
                }
                popupWindow.dismiss();
            }
        });

        ImageView cancelButton = popupView.findViewById(R.id.CancelButtonInId);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public void showPopUpAddWindow() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_add, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        EditText AddEt = popupView.findViewById(R.id.popUpAddET);

        ImageView addButton = popupView.findViewById(R.id.AddButtonInAdd);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AddEt.getText().toString().length() > 0) {
                    boolean checkPass = true;
                    for (char c : AddEt.getText().toString().toCharArray()) {
                        if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {

                        }
                        else {
                            checkPass = false;
                            break;
                        }
                    }
                    //if (checkPass) {
                        addOneContact(AddEt.getText().toString());
                        //createMsgTable(AddEt.getText().toString());
                        requireUpdateContactList = true;
                    //}
                    //else {
                        //shortToast("Invalid enter");
                    //}
                }
                popupWindow.dismiss();
            }
        });

        ImageView cancelButton = popupView.findViewById(R.id.CancelButtonInAdd);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public void showPopUpCallWindow() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_call, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        EditText AddEt = popupView.findViewById(R.id.popUpCallET);

        ImageView goButton = popupView.findViewById(R.id.GoButtonInCall);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AddEt.getText().toString().length() > 0) {
                    callContactClicked(AddEt.getText().toString());
                }
                popupWindow.dismiss();
            }
        });

        ImageView cancelButton = popupView.findViewById(R.id.CancelButtonInCall);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public void showPopUpCameraWindow() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_camera, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        final boolean[] captureBtnClicked = {false};

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        ImageView ImageView = popupView.findViewById(R.id.ivInCamera);
        captureImageView = ImageView;
        Button captureButton = popupView.findViewById(R.id.buttonInCamera);

        if (ActivityCompat.checkSelfPermission(SessionSelectorActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            }

        }

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.justStopLocalMedia();
                captureBtnClicked[0] = true;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);

                //popupWindow.dismiss();
            }
        });

        Button endButton = popupView.findViewById(R.id.endButtonInCamera);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (captureBtnClicked[0])
                    app.startLocalMedia();
                //saveToGallery(); // already saved
                popupWindow.dismiss();
            }
        });

    }

    public void showPopUpRingtoneSelect(Contacts contacts) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_ringtone_select, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
        final int[] lastClicked = {0};

        ListView listView = popupView.findViewById(R.id.ListViewInRingtoneSelect);
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                List<String> threeRingtoneList = Arrays.asList("By the seaside", "playtime", "silk");
                ArrayAdapter threeRingtoneAdapter = new ArrayAdapter<String>(SessionSelectorActivity.this, android.R.layout.simple_list_item_1, threeRingtoneList);
                listView.setAdapter(threeRingtoneAdapter);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(Color.parseColor("#FFEB3B"));
                for ( int i = 0 ; i < listView.getCount() ; i++){
                    if (i != position) {
                        View v = getViewByPosition(i, listView);
                        v.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }
                String clickedString = (String) parent.getItemAtPosition(position);

                if (clickedString.equals("By the seaside")) {
                    testRingtone(R.raw.by_the_seaside_ios);
                    lastClicked[0] = 1;
                }
                else if (clickedString.equals("playtime")) {
                    testRingtone(R.raw.playtime);
                    lastClicked[0] = 2;
                }
                else {
                    testRingtone(R.raw.silk);
                    lastClicked[0] = 3;
                }
            }
        });

        Button confirmButton = popupView.findViewById(R.id.confirmBtnInRingtoneSelect);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastClicked[0] != 0) {
                    contactDB.updateRingtone(contacts , lastClicked[0]);
                    requireUpdateContactList = true;
                    testRingtone(-1);
                }
                popupWindow.dismiss();
            }
        });

        Button cancelButton = popupView.findViewById(R.id.CancelBtnInRingtoneSelect);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testRingtone(-1);
                popupWindow.dismiss();
            }
        });
    }

    public void showPopUpChatHistory() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_chat_history, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken



        ListView listView = popupView.findViewById(R.id.ListViewInChatHistory);
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
                ArrayAdapter<String> chatHistoryArrayAdapter = new ArrayAdapter<String>(SessionSelectorActivity.this, android.R.layout.simple_list_item_1, contactDB.displayMsg(IdTalkTo));
                listView.setAdapter(chatHistoryArrayAdapter);
            }
        });
        ImageView exitButton = popupView.findViewById(R.id.exitButtonInChatHistory);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void hideSoftKeyBoard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            try {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            catch (Exception e) {

            }
        }
    }


    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void updateBGColorOfAudioButton() {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (newZoomOutFragment.audioMuted) {
                    newZoomOutFragment.MuteAudioButton.setBackgroundColor(Color.parseColor("#5A5656"));
                }
                else {
                    newZoomOutFragment.MuteAudioButton.setBackgroundColor(Color.parseColor("#3FE533"));
                }
            }
        });
    }

    public void updateBGColorOfVideoButton() {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (newZoomOutFragment.videoMuted) {
                    newZoomOutFragment.MuteVideoButton.setBackgroundColor(Color.parseColor("#5A5656"));
                }
                else {
                    newZoomOutFragment.MuteVideoButton.setBackgroundColor(Color.parseColor("#3FE533"));
                }
            }
        });
    }

    public void updateChattingRL() {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (newZoomOutFragment.chatOn) {
                    newZoomOutFragment.ChatButton.setBackgroundColor(Color.parseColor("#3FE533"));
                    newZoomOutFragment.NewTextContainer.setVisibility(View.VISIBLE);
                }
                else {
                    newZoomOutFragment.ChatButton.setBackgroundColor(Color.parseColor("#5A5656"));
                    newZoomOutFragment.NewTextContainer.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void zoomInAndOut() {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                zoomUpdated = !zoomUpdated;
                RelativeLayout.LayoutParams remoteParams = (RelativeLayout.LayoutParams) newZoomOutFragment.remoteContainer.getLayoutParams();
                RelativeLayout.LayoutParams localParams = (RelativeLayout.LayoutParams) newZoomOutFragment.localContainer.getLayoutParams();
                if (zoomUpdated) {
                    remoteParams.setMargins(0, 200, 0, 0);  // left, top, right, bottom
                    localParams.setMargins(0, 200, 0, 0);  // left, top, right, bottom
                    newZoomOutFragment.MuteAudioButton.setVisibility(View.VISIBLE);
                    newZoomOutFragment.MuteVideoButton.setVisibility(View.VISIBLE);
                    newZoomOutFragment.ChatButton.setVisibility(View.VISIBLE);
                    newZoomOutFragment.InviteButton.setVisibility(View.VISIBLE);
                    newZoomOutFragment.newZoomOutExitButton.setVisibility(View.VISIBLE);
                }
                else {
                    remoteParams.setMargins(0, 0, 0, 0);  // left, top, right, bottom
                    localParams.setMargins(0, 0, 0, 0);  // left, top, right, bottom
                    newZoomOutFragment.MuteAudioButton.setVisibility(View.INVISIBLE);
                    newZoomOutFragment.MuteVideoButton.setVisibility(View.INVISIBLE);
                    newZoomOutFragment.ChatButton.setVisibility(View.INVISIBLE);
                    newZoomOutFragment.InviteButton.setVisibility(View.INVISIBLE);
                    newZoomOutFragment.newZoomOutExitButton.setVisibility(View.INVISIBLE);
                }
                newZoomOutFragment.remoteContainer.setLayoutParams(remoteParams);
                newZoomOutFragment.remoteContainer.requestLayout();
                newZoomOutFragment.localContainer.setLayoutParams(localParams);
                newZoomOutFragment.localContainer.requestLayout();
            }
        });
    }

    public void SetFirstPageFragmentText() {
        runOnUiThread(new Runnable() {
            public void run() {
                firstPageFragment.currentIdTv.setText(app.getCurrentId());
            }
        });
    }

    public void clearAll() {
        app.neededExit = false;
        newZoomOutFragment.newZoomOutExitButtonClicked = false;
        app.timeOut = false;
        app.neededToRejoinConference1 = false;
        app.startInviting = false;
        app.finishedGoBackConference1 = false;
        app.inviteCalled = false;
        beInvitedModeOn = false;
        inviteModeOn = false;
        app.newInvitingCounter = -1;
        app.invitingCounter = 0;
        app.neededToRejoinConference2 = false;
        app.ANeededToJoinConference2 = false;
        app.BNeededToJoinConference2 = false;
        app.invitedToJoinConference2 = false;
        callingNumberClicked = false;
        app.neededToJoinConference2TimeOutBackConference1 = false;
        app.neededToReJoinConference2TimeOutBackConference1 = false;
    }


    class UpdateThread extends Thread {
        public void run() {
            while (true) {
                if (firstPageFragment.firstPageReady) {
                    SetFirstPageFragmentText();
                }
                if (firstPageFragment.contactButtonClicked) {
                    getContactList();
                    //showContactOnListView(contactDB);
                    firstPageFragment.contactButtonClicked = false;
                }
                if (getContactListFinished) {
                    //setContactListAdapter();
                    showContactOnListView(contactDB);
                    getContactListFinished = false;
                }


                if (firstPageFragment.historyButtonClicked) {
                    showHistoryOnListView(contactDB);
                    firstPageFragment.historyButtonClicked = false;
                }
                if (firstPageFragment.missingButtonClicked) {
                    showMissingCallsOnListView(contactDB);
                    firstPageFragment.missingButtonClicked = false;
                }
                if (firstPageFragment.idButtonClicked) {
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            showPopUpIdWindow();
                        }
                    });
                    firstPageFragment.idButtonClicked = false;
                }
                if (setCurrentIdFinished) {
                    //showContactOnListView(idDB);
                    askToChangeId();
                    setCurrentIdFinished = false;
                }
                if (firstPageFragment.addButtonClicked) {
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            showPopUpAddWindow();
                        }
                    });
                    firstPageFragment.addButtonClicked = false;
                }
                if (firstPageFragment.callButtonClicked) {
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            showPopUpCallWindow();
                        }
                    });
                    firstPageFragment.callButtonClicked = false;
                }
                if (firstPageFragment.cameraButtonClicked) {
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            showPopUpCameraWindow();
                        }
                    });
                    firstPageFragment.cameraButtonClicked = false;
                }
                if (firstPageFragment.settingButtonClicked) {
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            //showPopUpSettingWindow();
                        }
                    });
                    firstPageFragment.settingButtonClicked = false;
                }
                if (requireUpdateContactList) {
                    showContactOnListView(contactDB);
                    requireUpdateContactList = false;
                }
                if (mpIsPlaying) {
                    ringtoneSoundTimer++;
                    if (ringtoneSoundTimer > 60) {
                        customMp.stop();
                        customMp.release();
                        setFragment(R.id.newMainFrame, firstPageFragment);
                        if (!app.getSessionId().equals(app.getCurrentId()+"1")) {
                            askToLeave();
                            if (app.localViewReady = true)
                                app.stopLocalMedia();
                            app.neededToRejoinConference1 = true;
                        }
                        mpIsPlaying = false;
                        if (beInvitedModeOn) {
                            createMissingCall(-1, inviter);
                            writeString("COMMAND@ declined");
                        }
                        else if (!invitedId.equals("")) {
                            createMissingCall(-1, invitedId);
                            //addOneHistory(invitedId);
                        }
                    }
                } else {
                    ringtoneSoundTimer = 0;
                }
                if (newZoomOutFragment.textViewClicked) {
                    hideSoftKeyBoard();
                    app.Service1Thread2Active = true;
                    app.Service3Active = true;
                    newZoomOutFragment.textViewClicked = false;
                }
                if (newZoomOutFragment.editTextClicked) {
                    app.Service1Thread2Active = true;
                    app.Service3Active = true;
                    newZoomOutFragment.editTextClicked = false;
                }
                if (newZoomOutFragment.newZoomOutRelativeLayoutClicked) {
                    hideSoftKeyBoard();
                    app.Service1Thread2Active = true;
                    app.Service3Active = true;
                    zoomInAndOut();
                    newZoomOutFragment.newZoomOutRelativeLayoutClicked = false;
                }
                if (newZoomOutFragment.MuteAudioButtonClicked) {
                    hideSoftKeyBoard();
                    app.Service1Thread2Active = true;
                    app.Service3Active = true;
                    newZoomOutFragment.audioMuted = !newZoomOutFragment.audioMuted;
                    app.localMedia.setAudioMuted(newZoomOutFragment.audioMuted);
                    updateBGColorOfAudioButton();
                    newZoomOutFragment.MuteAudioButtonClicked = false;
                }
                if (newZoomOutFragment.MuteVideoButtonClicked) {
                    hideSoftKeyBoard();
                    app.Service1Thread2Active = true;
                    app.Service3Active = true;
                    newZoomOutFragment.videoMuted = !newZoomOutFragment.videoMuted;
                    app.localMedia.setVideoMuted(newZoomOutFragment.videoMuted);
                    updateBGColorOfVideoButton();
                    newZoomOutFragment.MuteVideoButtonClicked = false;
                }
                if (newZoomOutFragment.ChatButtonClicked) {
                    hideSoftKeyBoard();
                    app.Service1Thread2Active = true;
                    app.Service3Active = true;
                    newZoomOutFragment.chatOn = !newZoomOutFragment.chatOn;
                    updateChattingRL();
                    if (!IdTalkTo.equals(""))
                        createMsgTable(IdTalkTo);
                    else
                        shortToast("IdTalkTo is null");
                    newZoomOutFragment.ChatButtonClicked = false;
                }
                if (newZoomOutFragment.historyButtonClicked) {
                    hideSoftKeyBoard();
                    app.Service1Thread2Active = true;
                    app.Service3Active = true;
                    showPopUpChatHistory();
                    newZoomOutFragment.historyButtonClicked = false;
                }

                if (newZoomOutFragment.submitButtonClicked) {
                    hideSoftKeyBoard();
                    newZoomOutFragment.submitButtonClicked = false;
                }

                if (newZoomOutFragment.InviteButtonClicked) {
                    hideSoftKeyBoard();
                    if(app.invitingCounter == 0) {
                        //app.testCondition = true;
                        app.Service1Thread2Active = true;
                        app.Service3Active = true;
                        if (app.threadTwoOn) {
                            app.invitedConferenceId = app.getSessionId();
                            runOnUiThread(new Runnable()
                            {
                                public void run()
                                {
                                    showPopUpWindow();
                                }
                            });

                            inviteModeOn = true;
                        }
                    }
                    else {
                        shortToast("last invitation is not finished");

                    }

                    newZoomOutFragment.InviteButtonClicked = false;
                }

                if (newZoomOutFragment.newZoomOutExitButtonClicked) {
                    hideSoftKeyBoard();
                    app.Service1Thread2Active = true;
                    app.Service3On = false;
                    app.Service1Thread2On = false;
                    newZoomOutFragment.chatOn = false;
                    clearAll();
                    zoomUpdated = false;
                    newZoomOutFragment.localLayoutManager.unsetLocalView();
                    newZoomOutFragment.remoteLayoutManager.removeRemoteViews();
                    if (app.localViewReady = true)
                        app.stopLocalMedia();
                    if (app.threadThreeOn) askToLeaveConferenceThree();
                    if (!groupChatOn) {
                        askToLeave();
                    }
                    else {
                        groupChatOn = false;
                        askToLeaveGroupChat();
                        shortToast("leave group chat 1");
                    }
                    app.isInConference = false;
                    app.neededToRejoinConference1 = true;
                    newZoomOutFragment.newZoomOutExitButtonClicked = false;
                }
                /*
                if (app.duplicateConnectionsOccurred) {
                    app.currentConferenceId = app.getSessionId();
                    hideSoftKeyBoard();
                    app.Service1Thread2Active = true;
                    app.Service3Active = true;
                    app.Service1Thread2On = false;
                    newZoomOutFragment.chatOn = false;
                    clearAll();
                    if (newZoomOutFragment.zoomOutLayoutManagerReady) {
                        newZoomOutFragment.localLayoutManager.unsetLocalView();
                        newZoomOutFragment.remoteLayoutManager.removeRemoteViews();
                    }
                    if (app.localViewReady = true)
                        app.stopLocalMedia();
                    if (!groupChatOn) {
                        askToLeave();
                    }
                    else {
                        groupChatOn = false;
                        askToLeaveGroupChat();
                        shortToast("leave group chat 2");
                    }
                    app.neededToRejoinCurrentConference = true;
                    app.duplicateConnectionsOccurred = false;
                }

                 */

                if (app.forcedExit) {
                    hideSoftKeyBoard();
                    app.Service1Thread2Active = true;
                    app.Service3Active = true;
                    app.Service1Thread2On = false;
                    newZoomOutFragment.chatOn = false;
                    clearAll();
                    inviteModeOn = true;
                    app.startInviting = true;
                    zoomUpdated = false;
                    newZoomOutFragment.localLayoutManager.unsetLocalView();
                    newZoomOutFragment.remoteLayoutManager.removeRemoteViews();
                    if (app.localViewReady = true)
                        app.stopLocalMedia();
                    if (!groupChatOn) {
                        askToLeave();
                    }
                    else {
                        groupChatOn = false;
                        askToLeaveGroupChat();
                        shortToast("leave group chat 3");
                    }
                    app.neededToRejoinConference1 = true;
                    app.forcedExit = false;
                }

                if (app.neededExit) {
                    hideSoftKeyBoard();
                    app.Service1Thread2Active = true;
                    app.Service3On = false;
                    app.Service1Thread2On = false;
                    newZoomOutFragment.chatOn = false;
                    clearAll();
                    zoomUpdated = false;
                    newZoomOutFragment.localLayoutManager.unsetLocalView();
                    newZoomOutFragment.remoteLayoutManager.removeRemoteViews();
                    if (app.localViewReady = true)
                        app.stopLocalMedia();
                    if (!groupChatOn) {
                        askToLeave();
                    }
                    else {
                        groupChatOn = false;
                        askToLeaveGroupChat();
                        shortToast("leave group chat4");
                    }
                    app.neededToRejoinConference1 = true;
                    app.neededExit = false;
                }

                if (app.timeOut) {
                    hideSoftKeyBoard();
                    app.timeOut = false;
                    shortToast("timeOut");
                    app.neededExit = true;
                    app.isInConference = false;
                    app.Service1Thread2On = false;
                }
                if (app.neededToRejoinConference1 && !app.newConnectionOccupied && app.doStartLocalMedia && app.localViewReady) {
                    app.doStartLocalMedia = false;
                    app.names.clear();
                    app.connectionsCount.clear();
                    askToRejoinConference1();
                    remoteMediaTable.clear();
                    app.remoteMediaTable.clear();
                    setRingtone(false);
                    if (app.startInviting && !app.neededToRejoinConference2) {
                        newZoomInFragment.IdCallTo = app.inviteStr;
                        setFragment(R.id.newMainFrame, newZoomInFragment);
                    }
                    else if (app.neededToJoinConference2TimeOut)
                        setFragment(R.id.newMainFrame, emptyFragment);
                    else if (!app.neededToRejoinConference2 && !app.neededToInvite && !app.ANeededToJoinConference2)
                        setFragment(R.id.newMainFrame, firstPageFragment);
                    IdTalkTo = "";
                    app.neededToRejoinConference1 = false;
                }

                if (app.rejoinedConference1 && app.neededToRejoinCurrentConference) {
                    shortToast("!");
                    allowedToJoinConference2(app.currentConferenceId);
                    app.rejoinedConference1 = false;
                    app.neededToRejoinCurrentConference = false;
                }

                if (app.startInviting && app.finishedGoBackConference1 && !app.inviteCalled) {
                    app.inviteCalled = true;
                    callContactClicked(app.inviteStr);
                    Log.d("+++++", "1");
                }

                if (newZoomOutFragment.zoomOutLayoutManagerReady
                        && app.localViewReady && app.localMedia != null && checkLocalMediaReady()) {
                    newZoomOutFragment.zoomOutLayoutManagerReady = false; //?
                    app.zoomInNow = false;
                    newZoomOutFragment.localLayoutManager.setLocalView(app.localView);
                    shortToast("local view set");
                    if (app.neededToChangeAudio) {
                        app.localMedia.setAudioMuted(app.lastAudioMuted);
                        newZoomOutFragment.audioMuted = app.lastAudioMuted;
                        updateBGColorOfAudioButton();
                    }
                    else app.localMedia.setAudioMuted(false);
                }


                if (inComingCallFragment.acceptButtonClicked) {
                    setRingtone(false);
                    writeString("COMMAND@ accepted");
                    if (beInvitedModeOn) {
                        app.neededName = inviter;
                        Log.d("neededName", app.neededName);
                        app.invitedToJoinConference2 = true;
                        setFragment(R.id.newMainFrame, newZoomOutFragment);
                        beInvitedModeOn = false;
                        IdTalkTo = invitedConferenceId.substring(0,invitedConferenceId.length()-1);
                    } else {
                        writeString("COMMAND@ " + app.getSessionId().substring(0, app.getSessionId().length() - 1) + "2");
                        app.neededName = invitedId;
                        Log.d("neededName", app.neededName);
                        app.BNeededToJoinConference2 = true;
                        setFragment(R.id.newMainFrame, newZoomOutFragment);
                        IdTalkTo = invitedId;
                    }
                    //addOneHistory(invitedId);
                    inComingCallFragment.acceptButtonClicked = false;
                }


                if (inComingCallFragment.declineButtonClicked) {
                    setRingtone(false);
                    writeString("COMMAND@ declined");
                    //addOneHistory(invitedId);
                    setFragment(R.id.newMainFrame, firstPageFragment);
                    inComingCallFragment.declineButtonClicked = false;
                }

                if (newZoomInFragment.declineButtonClicked) {
                    writeString("COMMAND@ declined");
                    //addOneHistory(invitedId);
                    setFragment(R.id.newMainFrame, emptyFragment);
                    app.invitingCounter = 0;
                    app.newInvitingCounter = -1;
                    askToLeaveConferenceThree();
                    app.neededName = app.getSessionId().substring(0,app.getSessionId().length()-1);
                    Log.d("neededName", app.neededName);
                    if (app.localViewReady = true)
                        app.stopLocalMedia();
                    app.neededToRejoinConference2 = true;
                    app.neededToRejoinConference1 = true;

                    inviteModeOn = false;
                    newZoomInFragment.declineButtonClicked = false;
                }


                if (app.newInvitingCounter > 0) {
                    if (app.newInvitingCounter++ > 20) {
                        setFragment(R.id.newMainFrame, emptyFragment);
                        app.newInvitingCounter = -1;
                        askToLeaveConferenceThree();
                        app.neededName = app.getSessionId().substring(0,app.getSessionId().length()-1);
                        if (app.localViewReady = true)
                            app.stopLocalMedia();
                        Log.d("neededName", app.neededName);
                        app.neededToRejoinConference2 = true;
                        app.neededToRejoinConference1 = true;

                        inviteModeOn = false;
                    }
                }

                if (makingCallFragment.declineButtonClicked) {
                    setRingtone(false);
                    writeString("COMMAND@ declined");
                    setFragment(R.id.newMainFrame, firstPageFragment);
                    askToLeave();
                    if (app.localViewReady = true)
                        app.stopLocalMedia();
                    app.neededToRejoinConference1 = true;
                    makingCallFragment.declineButtonClicked = false;
                }

                if ((groupChatOn || app.getSessionId().charAt(app.getSessionId().length()-1) == '2' && !app.beingModifiedConnections)
                        && remoteMediaTable.size() < app.remoteMediaTable.size()
                        && newZoomOutFragment.isZoomOutLayoutManagerReady
                        && !app.newConnectionOccupied) {
                    int num = 0;
                    for (String str : app.remoteMediaTable.keySet()) {
                        if (!remoteMediaTable.containsKey(str)) {
                            newZoomOutFragment.remoteLayoutManager.addRemoteView(app.remoteMediaTable.get(str).getId(), app.remoteMediaTable.get(str).getView());
                            remoteMediaTable.put(str, app.remoteMediaTable.get(str));
                            num++;
                        }
                    }
                    Log.d("ppppp+: n2", Integer.toString(num));
                    Log.d("ppppp+: remoteMediaTableSize", Integer.toString(remoteMediaTable.size()));
                }
                if (app.needResetRemoteMedias) {
                    newZoomOutFragment.remoteLayoutManager.removeRemoteViews();
                }
                if (app.callingMsgSentOut) {
                    app.callingMsgSentOutTimeCount++;
                    shortToast("not receive ringing msg for " + app.callingMsgSentOutTimeCount + " second");
                    if (app.callingMsgSentOutTimeCount > 3) {
                        askToLeave();
                        if (app.localViewReady = true)
                            app.stopLocalMedia();
                        app.neededToRejoinConference1 = true;
                        //setFragment(R.id.ContactListButtonControlFragmentSection, contactListButtonControlFragment);
                        app.callingMsgSentOutTimeCount = 0;
                        app.callingMsgSentOut = false;
                    }
                }
                if (app.invitingCounter > 0) {
                    shortToast("inviting counter: " + Integer.toString(app.invitingCounter));
                    Log.d("+++++", "3");
                    if (app.invitingCounter++ == 5) {
                        app.invitingCounter = 0;
                        app.newInvitingCounter = -1;
                        setFragment(R.id.newMainFrame, emptyFragment);
                        askToLeaveConferenceThree();
                        app.neededName = app.getSessionId().substring(0,app.getSessionId().length()-1);
                        inviteModeOn = false;
                        if (app.localViewReady = true)
                            app.stopLocalMedia();
                        app.neededToRejoinConference2 = true;
                        app.neededToRejoinConference1 = true;

                        shortToast("return conference 2");
                    }
                }
                if (app.neededToRejoinConference2 && app.neededToRejoinConference2Can) {
                    setFragment(R.id.newMainFrame, newZoomOutFragment);
                    IdTalkTo = app.invitedConferenceId.substring(0, app.invitedConferenceId.length()-1);
                    allowedToJoinConference2(app.invitedConferenceId);
                    app.startInviting = false;
                    app.finishedGoBackConference1 = false;
                    app.inviteCalled = false;
                    app.neededToRejoinConference2 = false;
                    app.neededToRejoinConference2Can = false;
                    app.neededToChangeAudio = true;
                }
                if (app.ErrorRemoteMedia) {
                    app.Service1Thread2Active = true;
                    app.Service3On = false;
                    app.Service1Thread2On = false;
                    newZoomOutFragment.chatOn = false;
                    clearAll();
                    zoomUpdated = false;
                    app.ErrorRemoteMedia = false;
                    newZoomOutFragment.localLayoutManager.unsetLocalView();
                    newZoomOutFragment.remoteLayoutManager.removeRemoteViews();
                    shortToast("Error Remote Media");
                    if (app.localViewReady = true)
                        app.stopLocalMedia();

                    if (app.threadTwoOn) {
                        askToLeave();
                        //app.neededToRejoinConference2 = true;
                    }
                    else if (app.threadThreeOn) {
                        askToLeaveConferenceThree();
                        //app.neededToRejoinConference2 = true;
                    }
                    else if (groupChatOn) {
                        app.neededName = app.getSessionId();
                        askToLeaveGroupChat();
                        groupChatOn = false;
                        //app.neededToRejoinGroupChat = true;
                    }

                    app.names.clear();
                    app.connectionsCount.clear();
                    if (app.localViewReady = true)
                        app.stopLocalMedia();
                    app.neededToRejoinConference1 = true;
                    remoteMediaTable.clear();
                    app.remoteMediaTable.clear();
                    setRingtone(false);
                    setFragment(R.id.newMainFrame, firstPageFragment);
                    IdTalkTo = "";
                }
                if (app.neededToRejoinGroupChat && app.neededToRejoinGroupChatCan) {
                    startGroupConference(app.neededName);
                    app.neededToRejoinGroupChat = false;
                    app.neededToRejoinGroupChatCan = false;
                }
                /*
                if (app.neededToRejoinConference2) {
                    if (app.connectionsCount.containsKey(app.neededName)
                            && app.connectionsCount.get(app.neededName) == 0) {
                        IdTalkTo = app.invitedConferenceId.substring(0, app.invitedConferenceId.length()-1);
                        allowedToJoinConference2(app.invitedConferenceId);
                        app.startInviting = false;
                        app.finishedGoBackConference1 = false;
                        app.inviteCalled = false;
                        app.neededToRejoinConference2 = false;
                    }
                    else {
                        shortToast(""+app.neededName + " Did not leave");
                        if (app.neededToRejoinConference2Counter++ > 10) {
                            app.neededExit = true;
                            app.neededToRejoinConference2Counter = -1;
                            app.neededToReJoinConference2TimeOut = true;
                            app.neededToReJoinConference2TimeOutConferenceId = app.invitedConferenceId;
                            app.neededToRejoinConference2 = false;
                        }
                    }
                }

                 */

                if (app.ANeededToJoinConference2 && app.ANeededToJoinConference2Can) {
                    setFragment(R.id.newMainFrame, newZoomOutFragment);
                    allowedToJoinConference2(app.neededName+"2");
                    IdTalkTo = app.neededName;
                    app.isInConference = true;
                    app.ANeededToJoinConference2 = false;
                    app.ANeededToJoinConference2Can = false;
                }
                if (app.BNeededToJoinConference2) {
                    String tmp = app.getSessionId().substring(0, app.getSessionId().length()-1)+"2";
                    allowedToJoinConference2(tmp);
                    app.BNeededToJoinConference2 = false;
                }
                if (app.neededToInvite) {
                    askToInvite(callingNumber+"1");
                    app.neededToInvite = false;
                }
                /*
                if (app.neededToJoinConference2) {
                    String tmp = app.getSessionId().substring(0, app.getSessionId().length()-1)+"2";
                    if (app.connectionsCount.containsKey(app.neededName)
                            && app.connectionsCount.get(app.neededName) == 0) {
                        allowedToJoinConference2(tmp);
                        app.neededToJoinConference2 = false;
                    }
                    else {
                        shortToast(""+app.neededName + " did not leave");
                        if (app.neededToJoinConference2Counter++ > 10) {
                            app.neededExit = true;
                            app.neededToJoinConference2TimeOut = true;
                            app.neededToJoinConference2TimeOutConferenceId = tmp;
                            app.neededToJoinConference2Counter = -1;
                            app.neededToJoinConference2 = false;
                        }
                    }
                }

                 */
                if (app.invitedToJoinConference2) {
                    allowedToJoinConference2(invitedConferenceId);
                    app.invitedToJoinConference2 = false;
                }
                if (callingNumberClicked) {
                    if (app.callingCounter == 0) {
                        app.callingCounter = 1;
                        if (!app.getSessionId().equals(callingNumber+"1")) {
                            //setFragment(R.id.ContactListButtonControlFragmentSection, callWaitingButtonControlFragment);
                            String isGroupChat = "groupchat_";
                            if (callingNumber.length() > isGroupChat.length()) {
                                String prefix = callingNumber.substring(0,isGroupChat.length());
                                if (prefix.equals(isGroupChat)) {
                                    String rest = callingNumber.substring(isGroupChat.length());
                                    startGroupConference(rest);
                                    setFragment(R.id.newMainFrame, newZoomOutFragment);
                                    groupChatOn = true;
                                    app.Service3On = true;
                                    IdTalkTo = callingNumber;
                                }
                            }
                            else {
                                if (app.inviteCalled) {
                                    app.callingCounter = 0;
                                    //app.threadThreeOn = true;

                                    if (app.invitingCounter == 0) {
                                        writeString("Command@ is inviting " + app.invitedId + " .");
                                        shortToast("inviting");
                                        Log.d("+++++","inviting: " + callingNumber+"1");
                                        app.neededToInvite = true;
                                        app.newInvitingCounter = 1;
                                        app.invitingCounter = 1;
                                        Log.d("+++++", "2");
                                    }
                                }
                                else {
                                    allowedToJoinConference2(callingNumber+"1");
                                    IdTalkTo = callingNumber;
                                }
                            }
                        }
                    }
                    callingNumberClicked = false;
                }
                if (app.neededToStartLocalMedia && app.localMedia == null) {
                    app.startLocalMedia();
                    app.neededToStartLocalMedia = false;
                    app.doStartLocalMedia = true;
                    /*
                    if (app.neededToStartLocalMediaCounter++ > 1) {
                        try {

                        }
                        catch (Exception e) {

                        }
                        app.neededToStartLocalMediaCounter = 0;
                    }

                     */
                }
                if (app.neededToJoinConference2TimeOutBackConference1) {
                    setFragment(R.id.newMainFrame, newZoomOutFragment);
                    allowedToJoinConference2(app.neededToJoinConference2TimeOutConferenceId);
                    app.neededToJoinConference2TimeOut = false;
                    app.neededToJoinConference2TimeOutBackConference1 = false;
                }
                if (app.neededToReJoinConference2TimeOutBackConference1) {
                    setFragment(R.id.newMainFrame, newZoomOutFragment);
                    allowedToJoinConference2(app.neededToReJoinConference2TimeOutConferenceId);
                    app.neededToReJoinConference2TimeOut = false;
                    app.neededToReJoinConference2TimeOutBackConference1 = false;
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