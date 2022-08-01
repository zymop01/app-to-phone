package fm.icelink.chat.websync4;


import android.content.Context;
import android.view.View;

import fm.icelink.LayoutScale;
import fm.icelink.VideoConfig;
import fm.icelink.VideoSource;
import fm.icelink.ViewSink;
import fm.icelink.android.Camera2Source;
import fm.icelink.android.CameraPreview;

public class CameraLocalMedia extends LocalMedia<View> {
    private CameraPreview viewSink;
    private VideoConfig videoConfig = new VideoConfig(640, 480, 30);

    @Override
    protected ViewSink<View> createViewSink() {
        return null;
    }

    @Override
    protected VideoSource createVideoSource() {
        return new Camera2Source(viewSink, videoConfig);
    }

    public CameraLocalMedia(Context context, boolean enableSoftwareH264, boolean disableAudio, boolean disableVideo, AecContext aecContext) {
        super(context, enableSoftwareH264, disableAudio, disableVideo, aecContext);
        this.context = context;

        viewSink = new CameraPreview(context, LayoutScale.Contain);

        super.initialize();
    }

    public View getView()
    {
        return viewSink.getView();
    }
}