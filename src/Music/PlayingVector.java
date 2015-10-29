package Music;

import java.util.Vector;

import MessageManager.MessageManager;
import Player.Player;
import org.jaudiotagger.audio.AudioFile;

/**
 * Created by inksmallfrog on 10/28/15.
 */
public class PlayingVector {
    private Player player;

    private Vector<Music> music;
    private int selected;

    public PlayingVector(){
        music = new Vector<>();
        music.clear();

        player = new Player();

        selected = -1;
    }

    public void SelectMusic(int i){
        if(music.isEmpty()){
            selected = -1;
            return;
        }
        if(i < 0){
            i = music.size() - 1;
        }
        else if(i > music.size() - 1){
            i = 0;
        }
        selected = i;

        player.Select(getSelectedFileName());
        player.getMicroSecondLength();

        MessageManager.getMainGui().SetTimer();
    }

    public void AddMusic(Music _music){
        if(music.contains(_music)){
            return;
        }

        music.add(_music);
        if(!isSelected()){
            SelectMusic(0);
        }
    }

    public void Pause(){
        player.Pause();
    }

    public void Play(){
        player.Play();
    }

    public void PlayLoop(){
        player.Select(getSelectedFileName());
        player.Play();
    }

    public void PlayLine(){
        ++selected;
        SelectMusic(selected);
        player.Play();
    }

    public void PlayRandom(){

    }

    public void GoPrevious(){
        --selected;
        SelectMusic(selected);
        player.Play();
    }

    public void GoNext(){
        ++selected;
        SelectMusic(selected);
        player.Play();
    }

    public void SetMusicPosition(long microSecond){
        player.SetMusicPosition(microSecond);
    }

    public Vector getMusicVector(){
        return music;
    }

    public int getSelected(){
        return selected;
    }

    public int getMusicSize(){
        return music.size();
    }

    public String getSelectedFileName(){
        if(isSelected()){
            return music.get(selected).getFileName();
        }
        else{
            return "没有音乐";
        }
    }

    public AudioFile getAudioFile(int number){
        return music.get(number).getAudioFile();
    }

    public boolean isSelected(){
        return selected != -1;
    }

    public int getSelectedMusicMicroSecond(){
        return (int)player.getMicroSecondLength() / 1000000;
    }

    public String getSelectedMusicMicroSecondString(){
        long second = player.getMicroSecondLength() / 1000000;
        return String.format("%02d : %02d", second / 60, second % 60);
    }

    public int getCurrentMusicMicroSecond(){
        return (int)player.getMicroSecond() / 1000000;
    }

    public String getCurrentMusicMicroSecondString(){
        long second = player.getMicroSecond() / 1000000;
        return String.format("%02d : %02d", second / 60, second % 60);
    }
}
