package fm.icelink.chat.websync4;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import fm.icelink.AudioStream;
import fm.icelink.Connection;
import fm.icelink.ConnectionState;
import fm.icelink.Future;
import fm.icelink.IAction1;
import fm.icelink.IAction2;
import fm.icelink.IFunction0;
import fm.icelink.IFunction1;
import fm.icelink.IceServer;
import fm.icelink.Log;
import fm.icelink.LogLevel;
import fm.icelink.MediaSourceState;
import fm.icelink.PathUtility;
import fm.icelink.Promise;
import fm.icelink.Stream;
import fm.icelink.VideoSource;
import fm.icelink.VideoStream;
import fm.icelink.android.Camera2Source;
import fm.icelink.android.LayoutManager;
import fm.icelink.websync4.PeerClient;
import fm.websync.Record;

public class App {

    // This flag determines the signalling mode used.
    // Note that Manual and Auto signalling do not Interop.
    private final static boolean SIGNAL_MANUALLY = false;
    public Signalling signalling;
    public Signalling lastSignalling, lastSignalling2, lastSignalling3;
    public Signalling signalling1, signalling2, signalling3;

    public OnReceivedTextListener textListener;

    private static volatile String sessionId;
    public String getSessionId() {
        return this.sessionId;
    }
    public synchronized void setSessionId(String sid) {
        this.sessionId = sid;
    }

    private String name;
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private String IP;
    public String getIP() {
        return this.IP;
    }
    public void setIP(String ip) {
        this.IP = ip;
    }

    private boolean enableAudioSend;
    public boolean getEnableAudioSend() {
        return this.enableAudioSend;
    }
    public void setEnableAudioSend(boolean enable) {
        this.enableAudioSend = enable;
    }

    private boolean enableAudioReceive;
    public boolean getEnableAudioReceive() {
        return this.enableAudioReceive;
    }
    public void setEnableAudioReceive(boolean enable) {
        this.enableAudioReceive = enable;
    }

    private boolean enableVideoSend;
    public boolean getEnableVideoSend() {
        return this.enableVideoSend;
    }
    public void setEnableVideoSend(boolean enable) {
        this.enableVideoSend = enable;
    }

    private boolean enableVideoReceive;
    public boolean getEnableVideoReceive() {
        return this.enableVideoReceive;
    }
    public void setEnableVideoReceive(boolean enable) {
        this.enableVideoReceive = enable;
    }

    private boolean enableScreenShare;
    public boolean getEnableScreenShare() {
        return this.enableScreenShare;
    }
    public void setEnableScreenShare(boolean enable) {
        this.enableScreenShare = enable;
    }

    private MediaProjection mediaProjection;
    public MediaProjection getMediaProjection() {
        return this.mediaProjection;
    }
    public void setMediaProjection(MediaProjection mediaProjection) {
        this.mediaProjection = mediaProjection;
    }


    private IceServer[] iceServers = new IceServer[]
    {
  /*      new IceServer("stun:turn.frozenmountain.com:3478"),
        //NB: url "turn:turn.icelink.fm:443" implies that the relay server supports both TCP and UDP
        //if you want to restrict the network protocol, use "turn:turn.icelink.fm:443?transport=udp"
        //or "turn:turn.icelink.fm:443?transport=tcp". For further info, refer to RFC 7065 3.1 URI Scheme Syntax
        new IceServer("turn:turn.frozenmountain.com:80", "test", "pa55w0rd!"),
        new IceServer("turns:turn.frozenmountain.com:443", "test", "pa55w0rd!")
*/
            new IceServer("67.43.170.9:4007"),
            new IceServer("67.43.170.9:4007", "cloudkiller", "Jackie01!"),
            //NB: url "turn:turn.icelink.fm:443" implies that the relay server supports both TCP and UDP
            //if you want to restrict the network protocol, use "turn:turn.icelink.fm:443?transport=udp"
            //or "turn:turn.icelink.fm:443?transport=tcp". For further info, refer to RFC 7065 3.1 URI Scheme Syntax
            //new IceServer("67.43.170.11:4007", "test", "pa55w0rd!"),
            //new IceServer("67.43.170.11:4007", "test", "pa55w0rd!")

    };

    private HashMap<View, RemoteMedia> mediaTable;
    public HashMap<String, RemoteMedia> remoteMediaTable;
    //public HashMap<String, Integer> remoteMediaCount;
    public RemoteMedia currRemoteMedia = null;

    //private String websyncServerUrl = "https://www.lgtech.mobi:3007/websync.ashx"; // WebSync On-Demand
    private String websyncServerUrl = "https://ads123.net:3007/websync.ashx";

    public LocalMedia localMedia = null;
    private LayoutManager layoutManager = null;

    private AecContext aecContext;
    private boolean enableH264 = false;

    private Context context = null;
    private boolean usingFrontVideoDevice = true;
    private String selfName = "";

    public HashSet<String> names = new HashSet<String>();
    //public HashMap<String, Connection> connections = new HashMap<>();
    //public HashSet<String> names = new HashSet<String>();
    private HashSet<String> namesBeforeInvite = new HashSet<>();

    public static volatile boolean activity4ToForegroundCalled = false;
    public boolean foregroundMethodCalled = false;
    public boolean newService1Started = false;
    public boolean fromDummy = false;
    public boolean threadTwoOn = false, threadThreeOn = false, threadFourOn = false;
    public String m_androidId = "", lastConferenceId = "";

    //--
    public boolean localViewReady = false, localViewStarting = false, localViewStopping = false, localViewStopped = true;
    public View localView;
    public String invitedConferenceId = "";
    public boolean callingMsgSentOut = false;
    public int callingMsgSentOutTimeCount = 0;
    public boolean leaveAsyncFinished = false, leaveAsyncTwoFinished = false, leaveAsyncGroupFinished;
    public boolean groupChatStarted = false;
    public boolean zoomInNow = false;

    public boolean Service1Thread1On = false, Service1Thread2On = false, Service1Thread3On = false;
    public boolean Service3On = false;
    public boolean Service1Thread1Active = false, Service1Thread2Active = false, Service1Thread3Active = false;
    public boolean Service3Active = false;
    public boolean onPauseState = false;
    public int Service1Thread1Counter = 0, Service1Thread3Counter = 0;
    public int Service1Thread2RemoteMediaCounter = 0, Service1Thread2InactiveCounter = 0, Service1Thread2BackgroundCounter = 0;
    public int Service3RemoteMediaCounter = 0, Service3InactiveCounter = 0, Service3BackgroundCounter = 0;
    public boolean timeOut = false;
    public boolean sameConnectionOccurred = false;
    public String storedConferenceId = "";
    public int Service3Counter = 0;
    public String invitedId = "";
    public boolean needToRejoinConferenceTwo = false;
    public int invitingCounter = 0, callingCounter = 0, testCounter = 0;
    public int newInvitingCounter = 0;
    public boolean testCondition = false;
    public HashSet<String> isInvitedNames = new HashSet<>();
    public String conference2Id = "";
    public HashMap<String, Integer> connectionsCount = new HashMap<>();
    public HashMap<String, Connection> connections = new HashMap<>();
    public String neededConferenceId = "", neededName = "";
    public boolean neededToRejoinConference2 = false, neededToRejoinGroupChat = false;
    public boolean neededToRejoinGroupChatCan = false;
    public boolean ANeededToJoinConference2, BNeededToJoinConference2 = false;
    public boolean neededToRejoinConference1 = false, invitedToJoinConference2 = false;
    public boolean hasInternetConnection = false;
    public int cantLeaveCounter = 0, passInviteCounter = 0, neededToJoinConference2Counter = 0;
    public int neededToRejoinConference2Counter = 0 ;
    public boolean neededToJoinConference2TimeOut = false;
    public boolean neededToReJoinConference2TimeOut = false;
    public boolean neededToJoinConference2TimeOutBackConference1 = false;
    public boolean neededToReJoinConference2TimeOutBackConference1 = false;
    public String neededToJoinConference2TimeOutConferenceId = "";
    public String neededToReJoinConference2TimeOutConferenceId = "";
    public boolean getInvitedToConference2 = false, getCalledToConference2 = false;
    public boolean startInviting = false, inviteGoBackConference1 = false;
    public boolean finishedGoBackConference1 = false;
    public boolean inviteCalled = false;
    public String inviteStr = "";
    public String lastJoinedConferenceId = "";
    public boolean neededToStartLocalMedia = false, neededExit = false, forcedExit = false;
    public int neededToStartLocalMediaCounter = 0;
    public boolean duplicateConnectionsOccurred = false, neededToRejoinCurrentConference = false;
    public boolean rejoinedConference1 = false;
    public String currentConferenceId = "";
    public boolean needResetRemoteMedias = false;
    public boolean beingModifiedConnections = false;
    public HashSet<String> missingCallNames = new HashSet<>();

    public boolean newConnectionOccupied = false, newConnectionFinished = false;
    public int newConnectionCounter = 0;
    public boolean ErrorRemoteMedia = false;
    public boolean neededToRejoinConference2Can = false, ANeededToJoinConference2Can = false;
    public boolean doStartLocalMedia = false, didStartLocalMedia = false;
    public boolean neededToInvite = false;
    public boolean goEmptyFragment = false;
    public boolean isInConference = false;
    public boolean lastAudioMuted = false, neededToChangeAudio = false;
    //public boolean startHeartBeat = false, inviteCheck = false;
    //--
    public synchronized void setActivity4ToForegroundCalled(boolean b) {
        activity4ToForegroundCalled = b;
    }
    public synchronized boolean getActivity4ToForegroundCalled() {
        return activity4ToForegroundCalled;
    }

    //public App parent = App.this;


    static {
        Log.setLogLevel(LogLevel.Debug);
        Log.setProvider(new fm.icelink.android.LogProvider(LogLevel.Debug));
    }

    private App(Context context) {
        this.context = context.getApplicationContext();

        mediaTable = new HashMap<>();
        remoteMediaTable = new HashMap<>();
        //remoteMediaCount = new HashMap<>();
        //remoteMediaTable = null;

        enableAudioSend = true;
        enableAudioReceive = true;
        enableVideoSend = true;
        enableVideoReceive = true;

    }

    private static App app;
    static String thumbprint= "";
    public static synchronized App getInstance(Context context) {
        if (app == null) {
            app = new App(context);
        }
        return app;
    }

    public void SetSelfName(String str) {
        if (!names.contains(str)) names.add(str);
    }

    public void clearNames() {
        names.clear();
    }

    public void removeName(String str) {
        if (names.contains(str)) names.remove(str);
    }

    public int getNamesCount() {
        return names.size();
    }

    public void leaveConference(String str) {
        signalling.leaveConference(str);
    }

    public void joinConference(String str) {
        signalling.joinConference(str);
    }


    /**
     * Convenience: allow registry for local and remote views for context menu.
     */
    /*
    public void registerAvailableViewsForContextMenu(final VideoChatFragment fragment) {
        if (fragment == null) {
            String e = "Cannot register for context menus on a null object.";
            Log.debug(e, new Exception(e));
        }

        // Register local.
        if (localMedia != null && localMedia.getView() != null) {
            fragment.registerForContextMenu((View)localMedia.getView());
        }

        // Register any remotes.
        if (mediaTable != null && !mediaTable.isEmpty()) {
            Iterator<Map.Entry<View, RemoteMedia>> i = mediaTable.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<View, RemoteMedia> e = i.next();
                fragment.registerForContextMenu(e.getKey());

            }
        }
    }

     */

    public boolean getLocalViewReady() {
        return localViewReady;
    }

    public View getLocalView() {
        return localView;
    }

    public Future<LocalMedia> startLocalMedia() {
        localViewStarting = true;
        return Promise.wrapPromise(new IFunction0<Future<LocalMedia>>() {
            public Future<LocalMedia> invoke() {
                enableH264 = false;
                if (fm.icelink.openh264.Utility.isSupported()) {
                    final String downloadPath = context.getFilesDir().getPath();
                    fm.icelink.openh264.Utility.downloadOpenH264(downloadPath).waitForResult();

                    System.load(PathUtility.combinePaths(downloadPath, fm.icelink.openh264.Utility.getLoadLibraryName()));
                    enableH264 = true;
                }

                // Set up the local media.
                aecContext = new AecContext();

                if (enableScreenShare) {
                    localMedia = new ScreenShareLocalMedia(mediaProjection, context, enableH264, !enableAudioSend, !enableVideoSend, aecContext);
                } else {
                    localMedia = new CameraLocalMedia(context, enableH264, !enableAudioSend, !enableVideoSend, aecContext);
                }

                localView = (View)localMedia.getView();


                localViewReady = true;
                localViewStopped = false;
                localMedia.setAudioMuted(true);
                // Start the local media.
                return localMedia.start();
            }
        });
    }

    public Future<LocalMedia> justStopLocalMedia() {

        return Promise.wrapPromise(new IFunction0<Future<LocalMedia>>() {
            public Future<LocalMedia> invoke() {
                if (localMedia == null) {
                    throw new RuntimeException("Local media has already been stopped.");
                }

                // Stop the local media.
                return localMedia.stop().then(new IAction1<LocalMedia>() {
                    public void invoke(LocalMedia o) {
                        LayoutManager lm = layoutManager;
                        // Tear down the layout manager.
                        if (lm != null) {
                            lm.removeRemoteViews();
                            lm.unsetLocalView();
                            layoutManager = null;
                        }

                        // Tear down the local media.
                        if (localMedia != null) {
                            localMedia.destroy(); // localMedia.destroy() will also destroy AecContext
                            localMedia = null;
                        }
                        localViewReady = false;
                        localViewStopped = true;

                    }
                });
            }
        });
    }

    public Future<LocalMedia> stopLocalMedia() {

        return Promise.wrapPromise(new IFunction0<Future<LocalMedia>>() {
            public Future<LocalMedia> invoke() {
                if (localMedia == null) {
                    throw new RuntimeException("Local media has already been stopped.");
                }

                // Stop the local media.
                return localMedia.stop().then(new IAction1<LocalMedia>() {
                    public void invoke(LocalMedia o) {
                        LayoutManager lm = layoutManager;
                        // Tear down the layout manager.
                        if (lm != null) {
                            lm.removeRemoteViews();
                            lm.unsetLocalView();
                            layoutManager = null;
                        }

                        // Tear down the local media.
                        if (localMedia != null) {
                            localMedia.destroy(); // localMedia.destroy() will also destroy AecContext
                            localMedia = null;
                        }
                        localViewReady = false;
                        localViewStopped = true;


                        neededToStartLocalMedia = true;
                    }
                });
            }
        });
    }

    public Future<Object> joinAsync() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException
    {
        CryptLib _crypt = new CryptLib();
        //String ipAddr ="67.43.170.11:4007";
        String ipAddr ="67.43.170.9:4007";
        iceServers[0] = new IceServer(ipAddr);
        iceServers[1] = new IceServer(ipAddr, "cloudkiller", "Jackie01!");

        if (SIGNAL_MANUALLY) {
            signalling = manualSignalling();
        }
        else {
            signalling = autoSignalling();
        }
        android.util.Log.d("tset", "doJoin: " + app.getSessionId());
        if (app.getSessionId().equals(app.getCurrentId()+"1") && app.inviteGoBackConference1) {
            app.inviteGoBackConference1 = false;
            app.finishedGoBackConference1 = true;
            android.util.Log.d("+++++", "0");
        }
        if (app.getSessionId().equals(app.getCurrentId()+"1") && app.neededToJoinConference2TimeOut) {
            neededToJoinConference2TimeOutBackConference1 = true;
        }
        if (app.getSessionId().equals(app.getCurrentId()+"1") && app.neededToReJoinConference2TimeOut) {
            neededToReJoinConference2TimeOutBackConference1 = true;
        }
        if (app.getSessionId().equals(app.getCurrentId()+"1") && app.neededToRejoinCurrentConference) {
            rejoinedConference1 = true;
        }
        if (app.getSessionId().equals(app.getCurrentId()+"1") &&  neededToRejoinConference2) {
            neededToRejoinConference2Can = true;
        }
        if (app.getSessionId().equals(app.getCurrentId()+"1") && ANeededToJoinConference2) {
            ANeededToJoinConference2Can = true;
        }
        if (app.getSessionId().equals(app.getCurrentId()+"1") && neededToRejoinGroupChat) {
            neededToRejoinGroupChatCan = true;
        }

        lastJoinedConferenceId = app.getSessionId();
        return signalling.joinAsync();
    }

    private Signalling autoSignalling() {
        return new AutoSignalling(websyncServerUrl, getSessionId(), name, new IFunction1<PeerClient, Connection>() {
            @Override
            public Connection invoke(PeerClient remoteClient) {
                return connection(remoteClient);
            }
        }, new IAction2<String, String> () {
            @Override
            public void invoke(String n, String m) {
                textListener.onReceivedText(n, m);
            }
        });
    }



    private Signalling manualSignalling() {
        return new ManualSignalling(websyncServerUrl, getSessionId(), name, new IFunction1<PeerClient, Connection>(){
            @Override
            public Connection invoke(PeerClient remoteClient) {
                return connection(remoteClient);
            }
        }, new IAction2<String, String> () {
            @Override
            public void invoke(String n, String m) {
                textListener.onReceivedText(n, m);
            }
        });
    }

    private Connection connection(final PeerClient remoteClient) {
        //beingModifiedConnections = true;
        newConnectionFinished = false;
        newConnectionOccupied = true;

        newConnectionCounter = -1;
        String n = "Unknown";
        if (remoteClient.getBoundRecords() != null)
        {
            Record r = remoteClient.getBoundRecords().get("userName");

            if (r != null && r.getValueJson() != null)
            {
                String x = r.getValueJson();
                if(x.length() > 2)
                {
                    n = x.substring(1, x.length() - 1);
                }
            }
        }

        final String peerName = n;
        android.util.Log.d("testName", n);

        final Connection connection;

        // Create connection to remote client.
        //final RemoteMedia remoteMedia;
        //try{
        final RemoteMedia remoteMedia = new RemoteMedia(context, enableH264, false, false, aecContext);
        //}
        //catch (Exception e) {
            //ErrorRemoteMedia = true;
            //return null;
        //}
        final AudioStream audioStream = new AudioStream(localMedia, remoteMedia);
        audioStream.setLocalSend(enableAudioSend);
        audioStream.setLocalReceive(enableAudioReceive);

        final VideoStream videoStream = new VideoStream(localMedia, remoteMedia);
        videoStream.setLocalSend(enableVideoSend);
        videoStream.setLocalReceive(enableVideoReceive);

        connection = new Connection(new Stream[]{audioStream, videoStream});
        connection.setIceServers(iceServers);

        if (!names.contains(peerName)) {
            names.add(peerName);
            android.util.Log.d("2pppp+ :", "not close connection");
            android.util.Log.d("2pppp+ :", "" + getSessionId());
        }
        else {
            android.util.Log.d("2pppp+ :", "close connection");
            duplicateConnectionsOccurred = true;
        }

        if (remoteMedia.getView() != null) {
            // Add the remote view to the layout.
            remoteMedia.getView().setContentDescription("remoteView_" + remoteMedia.getId());
            mediaTable.put(remoteMedia.getView(), remoteMedia);
            if (sessionId.charAt(app.getSessionId().length()-1) == '2' || groupChatStarted) {
                if (!remoteMediaTable.containsKey(peerName)) {
                    remoteMediaTable.put(peerName, remoteMedia);
                    int i = remoteMediaTable.size();
                    android.util.Log.d("pppp+: i", Integer.toString(i));
                }
                else {
                    remoteMediaTable.remove(peerName);
                    SessionSelectorActivity.remoteMediaTable.remove(peerName);

                    //needResetRemoteMedias = true;
                    remoteMediaTable.put(peerName, remoteMedia);

                }
                android.util.Log.d("ppppp+: Add", peerName);
            }
            else {
                android.util.Log.d("ppppp+: lastLetter", "" + sessionId);
            }
        }
        else {
            android.util.Log.d("ppppp+: null", peerName);
        }

        if (!connectionsCount.containsKey(peerName)) connectionsCount.put(peerName, 0);
        connectionsCount.put(peerName, connectionsCount.get(peerName)+1);
        connections.put(peerName, connection);
        android.util.Log.d("ppppp+: connectionsCount", ""+peerName);
        android.util.Log.d("ppppp+: connectionsCount", ""+Integer.toString(connectionsCount.get(peerName)));


        connection.addOnStateChange(new IAction1<Connection>() {
            public void invoke(Connection c) {
                if (c.getState() == ConnectionState.Connected)
                {
                    textListener.onPeerJoined(peerName);
                    android.util.Log.d("addRemote", "new");

                }
                else if (c.getState() == ConnectionState.Closing ||
                        c.getState() == ConnectionState.Failing) {
                    // Remove the remote view from the layout.
                    //LayoutManager lm = layoutManager;
                    //if (lm != null) {
                        //lm.removeRemoteView(remoteMedia.getId());
                    //}
                    //mediaTable.remove(remoteMedia.getView());


                }
                else if (c.getState() == ConnectionState.Closed) {
                    textListener.onPeerLeft(peerName);

                    char lastLetter = sessionId.charAt(app.getSessionId().length()-1);
                    if (lastLetter == '2' || groupChatStarted) {
                        android.util.Log.d("ppppp1: lastLetter", ""+lastLetter);
                        if (zoomInNow)
                            SessionSelectorActivity.newZoomInFragment.remoteLayoutManager.removeRemoteView(remoteMedia.getId());
                        else
                            SessionSelectorActivity.newZoomOutFragment.remoteLayoutManager.removeRemoteView(remoteMedia.getId());


                    }
                    if (connectionsCount.containsKey(peerName)) {
                        if (connectionsCount.get(peerName) > 0)
                            connectionsCount.put(peerName, connectionsCount.get(peerName)-1);
                        android.util.Log.d("ppppp+: connectionsCount", ""+peerName);
                        android.util.Log.d("ppppp+: connectionsCount", ""+Integer.toString(connectionsCount.get(peerName)));
                        if (connectionsCount.get(peerName) == 0) {
                            remoteMediaTable.remove(peerName);
                            SessionSelectorActivity.remoteMediaTable.remove(peerName);

                            android.util.Log.d("ppppp+: remove", peerName);
                        }
                    }
                    //if (connections.containsKey(peerName)) connections.remove(peerName);
                }
                else if (c.getState() == ConnectionState.Failed) {
                    char lastLetter = sessionId.charAt(app.getSessionId().length()-1);
                    if (lastLetter == '2' || groupChatStarted) {
                        android.util.Log.d("ppppp1: lastLetter", ""+lastLetter);
                        if (zoomInNow)
                            SessionSelectorActivity.newZoomInFragment.remoteLayoutManager.removeRemoteView(remoteMedia.getId());
                        else
                            SessionSelectorActivity.newZoomOutFragment.remoteLayoutManager.removeRemoteView(remoteMedia.getId());
                        //SessionSelectorActivity.remoteMediaTable.remove(peerName);
                        //remoteMediaTable.remove(peerName);

                    }
                    textListener.onPeerLeft(peerName);
                    if (connectionsCount.containsKey(peerName)) {
                        if (connectionsCount.get(peerName) > 0)
                            connectionsCount.put(peerName, connectionsCount.get(peerName)-1);
                        android.util.Log.d("ppppp+: connectionsCount", ""+peerName);
                        android.util.Log.d("ppppp+: connectionsCount", ""+Integer.toString(connectionsCount.get(peerName)));
                        if (connectionsCount.get(peerName) == 0) {
                            remoteMediaTable.remove(peerName);
                            SessionSelectorActivity.remoteMediaTable.remove(peerName);

                            android.util.Log.d("ppppp+: remove", peerName);
                        }
                    }
                    //if (connections.containsKey(peerName)) connections.remove(peerName);
                    if (!SIGNAL_MANUALLY)
                        signalling.reconnect(remoteClient, c);
                }
            }
        });

        newConnectionFinished = true;
        //beingModifiedConnections = false;
        return connection;
    }

    public void leaveAsyncFinished() {
        names.clear();
        names.add(app.getCurrentId());
        remoteMediaTable.clear();
        groupChatStarted = false;
        //leaveConference(app.getSessionId());
        leaveAsync();
        leaveAsyncFinished = true;
    }

    public void leaveAsyncGroupFinished() {
        names.clear();
        names.add(app.getCurrentId());
        remoteMediaTable.clear();
        groupChatStarted = false;
        //leaveConference(app.getSessionId());
        leaveAsync();
        leaveAsyncGroupFinished = true;
    }

    public void leaveAsyncTwoFinished() {
        names.clear();
        //names.add(app.getCurrentId());
        //remoteMediaTable.clear();
        groupChatStarted = false;
        //leaveConference(app.getSessionId());
        leaveAsync();
        //connections.get(invitedId).close();
        leaveAsyncTwoFinished = true;
    }

    public Future<Object> leaveAsync() {
        return signalling.leaveAsync();
    }

    public void useNextVideoDevice() {
        if (localMedia != null && localMedia.getVideoSource() != null) {

            localMedia.changeVideoSourceInput(usingFrontVideoDevice ?
                    ((Camera2Source) localMedia.getVideoSource()).getBackInput() :
                    ((Camera2Source) localMedia.getVideoSource()).getFrontInput());

            usingFrontVideoDevice = !usingFrontVideoDevice;
        }
    }

    public Future<Object> pauseLocalVideo() {
        if (localMedia != null && !enableScreenShare) {
            VideoSource videoSource = localMedia.getVideoSource();
            if (videoSource != null) {
                if (videoSource.getState() == MediaSourceState.Started) {
                    return videoSource.stop();
                }
            }
        }
        return Promise.resolveNow();
    }

    public Future<Object> resumeLocalVideo() {
        if (localMedia != null) {
            VideoSource videoSource = localMedia.getVideoSource();
            if (videoSource != null) {
                if (videoSource.getState() == MediaSourceState.Stopped) {
                    return videoSource.start();
                }
            }
        }
        return Promise.resolveNow();
    }

    public void setIsRecordingAudio(View v, boolean record)
    {
        if (localMedia.getView() == v) {
            if (localMedia.getIsRecordingAudio() != record) {
                localMedia.toggleAudioRecording();
            }
        } else {
            RemoteMedia remote = mediaTable.get(v);
            if (remote.getIsRecordingAudio() != record) {
                remote.toggleAudioRecording();
            }
        }
    }

    public boolean getIsRecordingAudio(View v)
    {
        if (localMedia != null && localMedia.getView() != null && localMedia.getView() == v) {
            return localMedia.getIsRecordingAudio();
        }
        else if (mediaTable.get(v) != null) {
            return mediaTable.get(v).getIsRecordingAudio();
        }
        else return false;
    }

    public void setIsRecordingVideo(View v, boolean record)
    {
        if (localMedia.getView() == v) {
            if (localMedia.getIsRecordingVideo() != record) {
                localMedia.toggleVideoRecording();
            }
        } else {
            RemoteMedia remote = mediaTable.get(v);
            if (remote.getIsRecordingVideo() != record) {
                remote.toggleVideoRecording();
            }
        }
    }

    public boolean getIsRecordingVideo(View v)
    {
        if (localMedia != null && localMedia.getView() != null && localMedia.getView() == v) {
            return localMedia.getIsRecordingVideo();
        }
        else if (mediaTable.get(v) != null) {
            return mediaTable.get(v).getIsRecordingVideo();
        }
        else return false;
    }

    public void setAudioMuted(View v, boolean mute)
    {
        if (localMedia.getView() == v) {
            localMedia.setAudioMuted(mute);
        } else {
            mediaTable.get(v).setAudioMuted(mute);
        }
    }

    public boolean getAudioMuted(View v)
    {
        if (localMedia != null && localMedia.getView() != null && localMedia.getView() == v) {
            return localMedia.getAudioMuted();
        }
        else if (mediaTable.get(v) != null) {
            return mediaTable.get(v).getAudioMuted();
        }
        else return false;
    }

    public void setVideoMuted(View v, boolean mute)
    {
        if (localMedia.getView() == v) {
            localMedia.setVideoMuted(mute);
        } else {
            mediaTable.get(v).setVideoMuted(mute);
        }
    }

    public boolean getVideoMuted(View v)
    {
        if (localMedia != null && localMedia.getView() != null && localMedia.getView() == v) {
            return localMedia.getVideoMuted();
        }
        else if (mediaTable.get(v) != null) {
            return mediaTable.get(v).getVideoMuted();
        }
        else return false;
    }

    public void writeLine(String message)
    {
        String msg = message;

        signalling.writeLine(msg);
    }

    public String getCurrentId() {
        return SessionSelectorActivity.getCurrentId();
    }

    public interface OnReceivedTextListener {
        void onReceivedText(String name, String message);
        void onPeerJoined(String name);
        void onPeerLeft(String name);
    }
}
