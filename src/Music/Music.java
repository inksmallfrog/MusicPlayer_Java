package Music;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

/**
 * Created by inksmallfrog on 10/26/15.
 */
public class Music {
    private static String[] fieldDescribtion = {
            "曲名", "歌手"
    };

    private static FieldKey[] fieldKeysOrder = {
            FieldKey.TITLE,
            FieldKey.ARTIST
    };

    AudioFile file;
    Tag tag;

    String fileName = null;
    String urlName = null;
    boolean isOffLine;

    public Music(String _fileName){
        fileName = _fileName;
        isOffLine = true;
        AnalyzeMusicOffLine();
    }

    public Music(File _file){
        fileName = _file.getName();
        isOffLine = true;
        AnalyzeMusicOffLine(_file);
    }

    public Music(String _name, boolean _isOffLine){
        isOffLine = _isOffLine;
        if(_isOffLine) {
            fileName = _name;
            AnalyzeMusicOffLine();
        }
        else {
            urlName = _name;
            //AnalyzeMusicOnLine();
        }
    }

    public static String[] getHeaders(){
        return fieldDescribtion;
    }

    public String getDescribtion(int i){
        String result = tag.getFirst(fieldKeysOrder[i]);
        return (result == null)? "未知" : result;
    }

    public String getFileName(){
        return fileName;
    }

    public static int getItemDescribtionLength(){
        return fieldKeysOrder.length;
    }

    public static String getItemDescibtion(int i){
        if(i < 0 || i >= fieldKeysOrder.length){
            return null;
        }
        return fieldDescribtion[i];
    }

    @Override
    public boolean equals(Object other){
        if(other.getClass() != this.getClass()){
            return false;
        }
        if(tag.getFirst(FieldKey.TITLE)
                .equals(((Music) other).tag.getFirst(FieldKey.TITLE))){
            return true;
        }
        return false;
    }

    public void AnalyzeMusicOffLine(){
        File file = new File(fileName);
        AnalyzeMusicOffLine(file);
    }

    private void AnalyzeMusicOffLine(File _file){
        try {
            file = AudioFileIO.read(_file);
            tag = file.getTagOrCreateDefault();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public AudioFile getAudioFile(){
        return file;
    }

}
