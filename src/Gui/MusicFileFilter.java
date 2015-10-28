package Gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Created by inksmallfrog on 10/27/15.
 */
public class MusicFileFilter extends FileFilter {
    private String[] supported = {
            ".mp3",
            ".wav"
    };

    @Override
    public boolean accept(File f) {
        for(String ends : supported){
            if(f.getName().endsWith(ends)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        String allsupported = "";
        for( String ends : supported){
            allsupported += ends + " ";
        }
        return allsupported;
    }
}
