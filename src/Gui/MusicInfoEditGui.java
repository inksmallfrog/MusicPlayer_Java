package Gui;

import MessageManager.MessageManager;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Created by inksmallfrog on 10/28/15.
 */
public class MusicInfoEditGui extends JFrame{
    private int row;

    private AudioFile file;
    private Tag tags;

    private JLabel title_label;
    private JTextField title_field;

    private JLabel artist_label;
    private JTextField artist_field;

    private JButton commit;

    public MusicInfoEditGui(AudioFile _file, int _row){
        setSize(400, 600);
        setLocation(0, 0);
        setTitle("信息修改");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        row = _row;
        file = _file;
        tags = _file.getTagOrCreateDefault();

        CreateComponents();

        setVisible(true);
    }

    public void CreateComponents(){
        title_label = new JLabel("曲名:");
        title_field = new JTextField(tags.getFirst(FieldKey.TITLE));
        title_label.setBounds(110, 60, 40, 20);
        title_field.setBounds(170, 60, 120, 20);
        title_field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                try {
                    tags.setField(FieldKey.TITLE, title_field.getText());
                }
                catch (Exception exc){
                    exc.printStackTrace();
                }
            }
        });

        artist_label = new JLabel("歌手");
        artist_field = new JTextField(tags.getFirst(FieldKey.ARTIST));
        artist_label.setBounds(110, 100, 40, 20);
        artist_field.setBounds(170, 100, 120, 20);
        artist_field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                try {
                    tags.setField(FieldKey.ARTIST, artist_field.getText());
                }
                catch (Exception exc){
                    exc.printStackTrace();
                }
            }
        });

        commit = new JButton("确认");
        commit.addActionListener(e -> {
            CommitChange();
        });
        commit.setBounds(260, 400, 60, 20);

        add(title_label);
        add(title_field);
        add(artist_label);
        add(artist_field);
        add(commit);
    }

    public void CommitChange(){
        try {
            AudioFileIO.write(file);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        MessageManager.getMainGui().RefreshTable(row);
        dispose();
    }
}
