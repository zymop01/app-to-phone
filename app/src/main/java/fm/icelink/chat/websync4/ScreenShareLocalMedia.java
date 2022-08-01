package fm.icelink.chat.websync4;

import android.content.Context;
import android.media.projection.MediaProjection;
import android.widget.FrameLayout;

import fm.icelink.VideoSource;
import fm.icelink.ViewSink;
import fm.icelink.android.MediaProjectionSource;
import fm.icelink.android.OpenGLSink;

public class ScreenShareLocalMedia extends LocalMedia<FrameLayout> {

    private MediaProjectionSource projectionSource;

    @Override
    protected ViewSink<FrameLayout> createViewSink() {
        return new OpenGLSink(context);
    }

    @Override
    protected VideoSource createVideoSource() {
        return projectionSource;
    }

    public ScreenShareLocalMedia(MediaProjection projection, Context context, boolean enableSoftwareH264, boolean disableAudio, boolean disableVideo, AecContext aecContext) {
        super(context, enableSoftwareH264, disableAudio, disableVideo, aecContext);
        this.context = context;
        projectionSource = new MediaProjectionSource(projection, context, 1);

        super.initialize();
    }
}