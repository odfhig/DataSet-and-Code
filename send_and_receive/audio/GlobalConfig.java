

import android.media.AudioFormat;


public class GlobalConfig {

    /**
     * 采样率
     */
    public static final int SAMPLE_RATE_INHZ = 48000;

    /**
     * 声道数。
     */
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
}
