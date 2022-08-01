package fm.icelink.chat.websync4;

import android.content.Context;

import fm.icelink.AudioConfig;
import fm.icelink.AudioEncoder;
import fm.icelink.AudioFormat;
import fm.icelink.AudioSink;
import fm.icelink.AudioSource;
import fm.icelink.RtcLocalMedia;
import fm.icelink.VideoEncoder;
import fm.icelink.VideoFormat;
import fm.icelink.VideoPipe;
import fm.icelink.VideoSink;
import fm.icelink.android.AudioRecordSource;

public abstract class LocalMedia<TView> extends RtcLocalMedia<TView> {

    private boolean enableSoftwareH264;
    protected Context context;


    @Override
    protected AudioSink createAudioRecorder(AudioFormat audioFormat) {
        return new fm.icelink.matroska.AudioSink(context.getExternalFilesDir(null) + getId() + "-local-audio-" + audioFormat.getName().toLowerCase() + ".mkv");
    }

    @Override
    protected VideoSink createVideoRecorder(VideoFormat videoFormat) {
        return new fm.icelink.matroska.VideoSink(context.getExternalFilesDir(null) + getId() + "-local-video-" + videoFormat.getName().toLowerCase() + ".mkv");
    }

    @Override
    protected VideoPipe createImageConverter(VideoFormat videoFormat) {
        return new fm.icelink.yuv.ImageConverter(videoFormat);
    }

    @Override
    protected AudioSource createAudioSource(AudioConfig audioConfig) {
        return new AudioRecordSource(context, audioConfig);
    }

    @Override
    protected AudioEncoder createOpusEncoder(AudioConfig audioConfig) {
        return new fm.icelink.opus.Encoder(audioConfig);
    }

    @Override
    protected VideoEncoder createH264Encoder() {
        if (enableSoftwareH264) {
            return new fm.icelink.openh264.Encoder();
        } else {
            return null;
        }
    }

    @Override
    protected VideoEncoder createVp8Encoder() {
        return new fm.icelink.vp8.Encoder();
    }

    @Override
    protected VideoEncoder createVp9Encoder() {
        return new fm.icelink.vp9.Encoder();
    }

    public LocalMedia(Context context, boolean enableSoftwareH264, boolean disableAudio, boolean disableVideo, AecContext aecContext) {
        super(disableAudio, disableVideo, aecContext);
        this.enableSoftwareH264 = enableSoftwareH264;
        this.context = context;
    }
}
