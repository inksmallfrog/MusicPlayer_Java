package Gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import java.util.List;

import Music.Music;
import Player.Player;

/**
 * Created by inksmallfrog on 10/26/15.
 */
//main frame
public class WebeasyMusicGui extends JFrame{
    //Application Setting
    private final int window_width = 1200;
    private final int window_height = 720;
    private final String window_name = "网易云音乐-小蛙试做";

    //music_table
    private JScrollPane scrollPane;
    private JTable music_table;
    private DefaultTableModel music_table_model;
    private DefaultTableCellRenderer music_table_renderer;

    //left_bar
    private JToolBar left_bar;
    private JButton add_files;
    private JButton add_directory;

    //control_toolbar
    private JToolBar control_toolbar;
    private JButton play_pause;
    private JButton select_previous;
    private JButton select_next;
    private JSlider music_time_control;
    private JLabel music_time_current;
    private JLabel music_time_length;
    private JComboBox<String> music_play_mode;

    //for drag_window
    private Point pressed_pos;

    //for save music
    private Vector<Music> music_list;
    private int current_select_music;

    //for player
    private Player player;
    private long current_music_mircosecond_length;

    //for show time
    private Thread thread_refreshTime;
    private boolean refreshTime_quit;


    public WebeasyMusicGui(){
        setSize(window_width, window_height);
        setTitle(window_name);
        setUndecorated(true);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CreateAllCompnent();

        pressed_pos = new Point(0, 0);
        music_list = new Vector<>();
        current_select_music = 0;

        player = new Player();
        current_music_mircosecond_length = 0;

        thread_refreshTime = new Thread();
        refreshTime_quit = false;

        if(!music_list.isEmpty()){
            SelectMusic(music_list.get(current_select_music).getFileName());
        }
    }

    private void CreateAllCompnent(){
        CreateMusicTable();
        CreateLeftBar();
        CreateControlBar();

        AddFrameListener();
        AddMusicTableListener();
        AddLeftBarListener();
        AddControlBarListener();
    }

    private void CreateMusicTable(){
        CreateMusicTableModel();
        CreateMusicTableCellRenderer();

        music_table = new JTable(music_table_model);
        music_table.setDefaultRenderer(Object.class, music_table_renderer);
        music_table.setShowGrid(false);
        music_table.setIntercellSpacing(new Dimension(0, 0));
        music_table.setAutoCreateRowSorter(true);

        scrollPane = new JScrollPane(music_table);
        getContentPane().add(scrollPane, BorderLayout.EAST);
    }

    private void CreateMusicTableModel(){
        music_table_model = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return Music.getItemDescibtion(columnIndex);
            }

            @Override
            public int getColumnCount(){
                return Music.getItemDescribtionLength();
            }

            @Override
            public int getRowCount(){
                return dataVector.size();
            }

            @Override
            public void setDataVector(Vector _dataVector, Vector _columnIdentifiers){
                dataVector = _dataVector;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return ((Music)dataVector.get(rowIndex)).getDescribtion(columnIndex);
            }
        };
    }

    private void CreateMusicTableCellRenderer(){
        music_table_renderer = new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                setText(value.toString());

                setForeground(Color.white);
                if(row % 2 == 0){
                    setBackground(new Color(93, 93, 93));
                }
                else{
                    setBackground(new Color(70, 70, 70));
                }

                return this;
            }
        };
    }

    private void CreateLeftBar(){
        left_bar = new JToolBar();
        left_bar.setFloatable(false);

        add_files = new JButton("添加音乐");
        add_directory = new JButton("添加文件夹");

        left_bar.add(add_files);
        left_bar.add(add_directory);
        getContentPane().add(left_bar, BorderLayout.WEST);
    }

    private void CreateControlBar(){
        control_toolbar = new JToolBar();
        control_toolbar.setFloatable(false);

        select_previous = new JButton("上一首");

        play_pause = new JButton("播放");

        select_next = new JButton("下一首");


        music_time_control = new JSlider();
        music_time_control.setValue(0);


        music_time_current = new JLabel("00:00");
        music_time_length = new JLabel(" / 00:00");

        music_play_mode = new JComboBox<>();
        music_play_mode.addItem("单曲循环");
        music_play_mode.addItem("顺序循环");
        music_play_mode.addItem("随机播放");

        control_toolbar.add(select_previous);
        control_toolbar.add(play_pause);
        control_toolbar.add(select_next);
        control_toolbar.add(music_time_control);
        control_toolbar.add(music_time_current);
        control_toolbar.add(music_time_length);
        control_toolbar.add(music_play_mode);

        getContentPane().add(control_toolbar, BorderLayout.SOUTH);
    }

    public void AddMusic(Music music){
        boolean hasSelected = true;
        if(music_list.isEmpty()){
            hasSelected = false;
        }

        if(music_list.contains(music)){
            return;
        }
        music_list.add(music);
        music_table_model.setDataVector(music_list, null);
        music_table.updateUI();

        if(!hasSelected){
            SelectMusic(music_list.get(current_select_music).getFileName());
        }
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

    private void Pause(){
        player.Pause();
        play_pause.setText("播放");
    }

    private void PlayMusic(){
        player.Play();

        thread_refreshTime = new Thread(() -> {
            while(!refreshTime_quit) {
                RefreshTimer();
                try {
                    Thread.sleep(500);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread_refreshTime.start();

        play_pause.setText("暂停");
    }

    private void RefreshTimer() {
        int music_second_current = (int) player.getMicroSecond() / 1000000;
        music_time_current.setText(String.format("%02d", music_second_current / 60)
                + ":" + String.format("%02d", music_second_current % 60));
        music_time_control.setValue(music_second_current);

        if(music_time_control.getValue() >= music_time_control.getMaximum()){
            switch ((String)music_play_mode.getSelectedItem()){
                case "单曲循环":
                    SelectMusic(music_list.get(current_select_music).getFileName());
                    PlayMusic();
                    break;

                case "顺序循环":
                    ++current_select_music;
                    if(current_select_music >= music_list.size()){
                        current_select_music = 0;
                    }
                    SelectMusic(music_list.get(current_select_music).getFileName());
                    PlayMusic();
                    break;

                case "随机播放":
                    //SelectMusic(music_list.get(Math.random() * ).getFileName());
                    //PlayMusic();
                    break;

                default:
                    break;
            }
            return;
        }
    }

    private void AddFrameListener(){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                super.mousePressed(e);
                pressed_pos.x = e.getX();
                pressed_pos.y = e.getY();
            }
            @Override
            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                Point last_location = getLocation();
                setLocation(last_location.x + e.getX() - pressed_pos.x, last_location.y + e.getY() - pressed_pos.y);
            }
        });
    }

    private void AddMusicTableListener(){
        scrollPane.setDropTarget(new DropTarget(this, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                dtde.getTransferable();
                if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
                    try {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                        List list = (List)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                        Iterator it = list.iterator();
                        while(it.hasNext()){
                            File file = (File)it.next();
                            MusicFileFilter filter = new MusicFileFilter();
                            if(filter.accept(file)){
                                AddMusic(new Music(file));
                            }
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }));
    }

    private void AddLeftBarListener(){
        add_files.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("./");
            fileChooser.setFileFilter(new MusicFileFilter());
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.showDialog(new JLabel(), "添加");
            File[] files = fileChooser.getSelectedFiles();
            for(File file : files){
                AddMusic(new Music(file));
            }
        });

        add_directory.addActionListener(e -> {
            JFileChooser directoryChooser = new JFileChooser("./");
            directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            directoryChooser.showDialog(new JLabel(), "选择");
            File dir = directoryChooser.getSelectedFile();
            String[] files = dir.list();
            for(String fileName : files){
                File file = new File(fileName);
                MusicFileFilter filter = new MusicFileFilter();
                if(filter.accept(file)){
                    AddMusic(new Music(file));
                }
            }
        });
    }

    private void AddControlBarListener(){
        select_previous.addActionListener(e -> {
            --current_select_music;
            SelectMusic(music_list.get(current_select_music).getFileName());
            PlayMusic();
        });

        play_pause.addActionListener(e -> {
            if(player.getPlayerState() == Player.ePlayerState.PLAYER_STATE_PLAY){
                Pause();
            }
            else{
                PlayMusic();
            }
        });

        select_next.addActionListener(e -> {
            ++current_select_music;
            if(current_select_music >= music_list.size()){
                current_select_music = 0;
            }
            SelectMusic(music_list.get(current_select_music).getFileName());
            PlayMusic();
        });

        music_time_control.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    player.SetMusicPosition(music_time_control.getValue() * 1000000);
                }
            }
        );
    }
}
