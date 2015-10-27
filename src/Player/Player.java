package Player;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Created by inksmallfrog on 10/26/15.
 */
public class Player{
    private String fileName = null;
    private AudioInputStream inputStream = null;
    private AudioFormat format = null;
    private Clip clip = null;

    public enum ePlayerState{
        PLAYER_STATE_PLAY,
        PLAYER_STATE_PAUSE,
    }
    private ePlayerState state = ePlayerState.PLAYER_STATE_PAUSE;

    public Player(){
    }

    public ePlayerState getPlayerState(){
        return state;
    }

    public void Select(String _fileName){
        fileName = _fileName;
        if(null != clip){
            clip.stop();
            clip.drain();
            clip.flush();
        }
        run();
        clip.stop();
        state = ePlayerState.PLAYER_STATE_PAUSE;
    }

    public void Play(){
        if(ePlayerState.PLAYER_STATE_PAUSE == state){
            state = ePlayerState.PLAYER_STATE_PLAY;
            clip.start();
        }
    }

    public void Pause(){
        if(ePlayerState.PLAYER_STATE_PLAY == state) {
            state = ePlayerState.PLAYER_STATE_PAUSE;
            clip.stop();
        }
    }

    public void SetMusicPosition(long microSecond){
        clip.setMicrosecondPosition(microSecond);
    }

    public void run() {
        try {
            GetReady();
            if (clip != null) {
                clip.open(inputStream);
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getMicroSecond(){
        return clip.getMicrosecondPosition();
    }

    public long getMicroSecondLength(){
        return clip.getMicrosecondLength();
    }

    private AudioFormat getOutFormat(AudioFormat inFormat){
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();

        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void GetReady() throws Exception{
        File file = new File(fileName);
        AudioInputStream in = AudioSystem.getAudioInputStream(file);
        format = getOutFormat(in.getFormat());
        inputStream = AudioSystem.getAudioInputStream(format, in);
        clip = AudioSystem.getClip();
    }
}
