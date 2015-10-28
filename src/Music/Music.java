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
    private static String[] itemDescribtion = {
            "曲名", "歌手"
    };

    public enum eItemDescribtion{
        TITLE, ARTIST
    }

    String fileName = null;
    String urlName = null;
    String[] describtions = new String[itemDescribtion.length];
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

    public String getDescribtion(int i){
        return describtions[i];
    }

    public String getFileName(){
        return fileName;
    }

    public static int getItemDescribtionLength(){
        return itemDescribtion.length;
    }

    public static String getItemDescibtion(int i){
        if(i < 0 || i >= itemDescribtion.length){
            return null;
        }
        return itemDescribtion[i];
    }

    @Override
    public boolean equals(Object other){
        if(other.getClass() != this.getClass()){
            return false;
        }
        if(describtions[eItemDescribtion.TITLE.ordinal()]
                .equals(((Music) other).describtions[eItemDescribtion.TITLE.ordinal()])){
            return true;
        }
        return false;
    }

    private void AnalyzeMusicOffLine(){
        File file = new File(fileName);
        AnalyzeMusicOffLine(file);
    }

    private void AnalyzeMusicOffLine(File file){
        String file_name = file.getName();

        try{
            AudioFile file_tag = AudioFileIO.read(file);
            Tag tag = file_tag.getTag();
            if(null != tag) {
                describtions[eItemDescribtion.TITLE.ordinal()] = tag.getFirst(FieldKey.TITLE);
                describtions[eItemDescribtion.ARTIST.ordinal()] = tag.getFirst(FieldKey.ARTIST);
                if ("" == describtions[eItemDescribtion.TITLE.ordinal()]) {
                    int sperator_pos = file_name.indexOf('-');
                    int suffix_pos = file_name.lastIndexOf('.');
                    if (-1 == sperator_pos) {
                        describtions[eItemDescribtion.TITLE.ordinal()] = file_name.substring(0, suffix_pos);
                    } else {
                        describtions[eItemDescribtion.TITLE.ordinal()] = file_name.substring(0, sperator_pos);
                    }
                }
                if ("" == describtions[eItemDescribtion.ARTIST.ordinal()]) {
                    int sp_pos = file_name.indexOf('-');
                    if (-1 == sp_pos) {
                        describtions[eItemDescribtion.ARTIST.ordinal()] = "未知";
                    } else {
                        describtions[eItemDescribtion.ARTIST.ordinal()] = file_name.substring(sp_pos, file_name.length());
                    }
                }
            }
            else{
                int sperator_pos = file_name.indexOf('-');
                int suffix_pos = file_name.lastIndexOf('.');
                if (-1 == sperator_pos) {
                    describtions[eItemDescribtion.TITLE.ordinal()]  = file_name.substring(0, suffix_pos);
                    describtions[eItemDescribtion.ARTIST.ordinal()] = "未知";
                } else {
                    describtions[eItemDescribtion.TITLE.ordinal()]  = file_name.substring(0, sperator_pos);
                    describtions[eItemDescribtion.ARTIST.ordinal()] = file_name.substring(sperator_pos, file_name.length());
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
