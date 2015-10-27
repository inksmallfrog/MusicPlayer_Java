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
    String fileName = null;
    String UrlName = null;
    String title = "";
    String artist = "";
    boolean isOffLine;

    public Music(String _fileName){
        fileName = _fileName;
        isOffLine = true;
        AnalyzeMusicOffLine();
    }

    public Music(String _name, boolean _isOffLine){
        isOffLine = _isOffLine;
        if(_isOffLine) {
            fileName = _name;
            AnalyzeMusicOffLine();
        }
        else {
            UrlName = _name;
            //AnalyzeMusicOnLine();
        }
    }

    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }

    public String getFileName(){
        return fileName;
    }

    private void AnalyzeMusicOffLine(){
        File file = new File(fileName);
        String file_name = file.getName();

        try{
            AudioFile file_tag = AudioFileIO.read(file);
            Tag tag = file_tag.getTag();
            title = tag.getFirst(FieldKey.TITLE);
            artist = tag.getFirst(FieldKey.ARTIST);
            if("" == title){
                int sperator_pos = file_name.indexOf('-');
                int suffix_pos = file_name.lastIndexOf('.');
                if(-1 == sperator_pos){
                    title = file_name.substring(0, suffix_pos);
                }
                else{
                    title = file_name.substring(0, sperator_pos);
                }
            }
            if("" == artist){
                int sp_pos = file_name.indexOf('-');
                if(-1 == sp_pos){
                    artist = "未知";
                }
                else{
                    artist = file_name.substring(sp_pos, file_name.length());
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
