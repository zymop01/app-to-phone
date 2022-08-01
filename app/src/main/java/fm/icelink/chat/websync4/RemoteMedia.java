package fm.icelink.chat.websync4;

import android.content.Context;
import android.widget.FrameLayout;

import fm.icelink.AudioConfig;
import fm.icelink.AudioDecoder;
import fm.icelink.AudioFormat;
import fm.icelink.AudioSink;
import fm.icelink.RtcRemoteMedia;
import fm.icelink.VideoDecoder;
import fm.icelink.VideoFormat;
import fm.icelink.VideoPipe;
import fm.icelink.VideoSink;
import fm.icelink.ViewSink;
import fm.icelink.android.AudioTrackSink;
import fm.icelink.android.OpenGLSink;
import fm.icelink.yuv.ImageConverter;

public class RemoteMedia extends RtcRemoteMedia<FrameLayout> {

    private boolean enableSoftwareH264;
    private Context context;

    @Override
    protected ViewSink<FrameLayout> createViewSink() {
        return new OpenGLSink(context);
    }

    @Override
    protected AudioSink createAudioRecorder(AudioFormat audioFormat) {
        return new fm.icelink.matroska.AudioSink(context.getExternalFilesDir(null) + getId() + "-remote-audio-" + audioFormat.getName().toLowerCase() + ".mkv");
    }

    @Override
    protected VideoSink createVideoRecorder(VideoFormat videoFormat) {
        return new fm.icelink.matroska.VideoSink(context.getExternalFilesDir(null) + getId() + "-remote-video-" + videoFormat.getName().toLowerCase() + ".mkv");
    }

    @Override
    protected VideoPipe createImageConverter(VideoFormat videoFormat) {
        return new ImageConverter(videoFormat);
    }

    @Override
    protected AudioDecoder createOpusDecoder(AudioConfig audioConfig) {
        return new fm.icelink.opus.Decoder(audioConfig);
    }

    @Override
    protected AudioSink createAudioSink(AudioConfig audioConfig) {
        return new AudioTrackSink(audioConfig);
    }

    @Override
    protected VideoDecoder createH264Decoder() {
        if (enableSoftwareH264) {
            return new fm.icelink.openh264.Decoder();
        } else {
            return null;
        }
    }

    @Override
    protected VideoDecoder createVp8Decoder() {
        return new fm.icelink.vp8.Decoder();
    }

    @Override
    protected VideoDecoder createVp9Decoder() {
        return new fm.icelink.vp9.Decoder();
    }

    public RemoteMedia(final Context context, boolean enableSoftwareH264, boolean disableAudio, boolean disableVideo, AecContext aecContext) {
        super(disableAudio, disableVideo, aecContext);
        this.context = context;
        this.enableSoftwareH264 = enableSoftwareH264;

        super.initialize();
    }
}
