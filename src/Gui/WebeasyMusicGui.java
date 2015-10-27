package Gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

import Music.Music;
import Player.Player;

/**
 * Created by inksmallfrog on 10/26/15.
 */
//main frame
public class WebeasyMusicGui extends JFrame{
    private int window_width = 1200;
    private int window_height = 720;
    private String window_name = "网易云音乐-小蛙试做";

    //music_table
    private JScrollPane scrollPane;
    private JTable music_table;
    private DefaultTableModel music_table_model;

    //control_toolbar
    private JToolBar control_toolbar;
    private JButton play_pause;
    private JButton select_previous;
    private JButton select_next;
    private JSlider music_time_control;
    private JLabel music_time_current;
    private JLabel music_time_length;

    List<Music> music_list;
    private int current_select_music = -1;

    private Player player;
    long current_music_mircosecond_length;

    private Thread refreshTimer;
    private boolean quit_refreshTimer = false;

    public WebeasyMusicGui(){
        setSize(window_width, window_height);
        setTitle(window_name);
        setUndecorated(true);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CreateAllCompnent();
        music_list = new LinkedList<>();

        player = new Player();
    }

    private void CreateAllCompnent(){
        CreateMusicTable();
        CreateControlBar();
    }

    private void CreateMusicTable(){
        String[] headers = {
                "曲名", "歌手"
        };

        music_table_model = new DefaultTableModel(null, headers){
            public boolean IsCellEditable(int row, int column){
                return false;
            }
        };
        music_table = new JTable(music_table_model);

        scrollPane = new JScrollPane(music_table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void CreateControlBar(){
        control_toolbar = new JToolBar();
        select_previous = new JButton("上一首");
        select_previous.addActionListener(e -> {
            --current_select_music;
            SelectMusic(music_list.get(current_select_music).getFileName());
            PlayMusic();
        });
        play_pause = new JButton("播放");
        play_pause.addActionListener(e -> {
            if(player.getPlayerState() == Player.ePlayerState.PLAYER_STATE_PLAY){
                player.Pause();

                play_pause.setText("播放");
            }
            else{
                PlayMusic();
            }
        });
        select_next = new JButton("下一首");
        select_next.addActionListener(e -> {
            ++current_select_music;
            SelectMusic(music_list.get(current_select_music).getFileName());
            PlayMusic();
        });

        music_time_control = new JSlider();
        music_time_control.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    player.SetMusicPosition(music_time_control.getValue() * 1000000);
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            }
        );

        music_time_current = new JLabel("00:00");
        music_time_length = new JLabel(" / 00:00");

        control_toolbar.add(select_previous);
        control_toolbar.add(play_pause);
        control_toolbar.add(select_next);
        control_toolbar.add(music_time_control);
        control_toolbar.add(music_time_current);
        control_toolbar.add(music_time_length);

        getContentPane().add(control_toolbar, BorderLayout.SOUTH);
    }

    public void AddMusic(Music music){
        String[] data = {
                music.getTitle(), music.getArtist()
        };
        music_table_model.addRow(data);
        music_list.add(music);
    }

    private void SelectMusic(String fileName){
        player.Select(fileName);
        current_music_mircosecond_length = player.getMicroSecondLength();
        int music_second_length = (int)current_music_mircosecond_length / 1000000;

        music_time_control.setMinimum(0);
        music_time_control.setMaximum(music_second_length);
        music_time_control.setValue(0);

        music_time_length.setText(" / " + String.format("%02d", music_second_length / 60)
                + ":" + String.format("%02d", music_second_length % 60));

        RefreshTimer();
    }

    private void PlayMusic(){
        player.Play();

        refreshTimer = new Thread(() -> {
            while(!quit_refreshTimer) {
                RefreshTimer();
                try {
                    Thread.sleep(500);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        refreshTimer.start();

        play_pause.setText("暂停");
    }

    private void RefreshTimer() {
        int music_second_current = (int) player.getMicroSecond() / 1000000;
        music_time_current.setText(String.format("%02d", music_second_current / 60)
                + ":" + String.format("%02d", music_second_current % 60));
        music_time_control.setValue(music_second_current);
    }
}
