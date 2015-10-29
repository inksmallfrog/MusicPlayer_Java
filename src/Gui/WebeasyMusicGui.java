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

import MessageManager.MessageManager;
import Music.Music;
import Music.PlayingVector;

/**
 * Created by inksmallfrog on 10/26/15.
 */
//main frame
public class WebeasyMusicGui extends JFrame{
    //Application Setting
    private final int window_width = 1200;
    private final int window_height = 720;
    private final String window_name = "网易云音乐-小蛙试做";

    //table_music_list
    private JScrollPane table_scroll_pane;
    private JTable table_music_list;
    private DefaultTableModel table_music_list_model;
    private DefaultCellEditor table_music_button_editor;
    private DefaultTableCellRenderer table_music_list_renderer;

    //left_bar
    private JToolBar left_bar;
    private JButton left_button_addFiles;
    private JButton left_button_addDirectory;

    //bottom_bar
    private JToolBar bottom_bar;
    private JButton bottom_button_control;
    private JButton bottom_button_previous;
    private JButton bottom_button_next;
    private JSlider bottom_slider_time;
    private JLabel bottom_label_timeNow;
    private JLabel bottom_label_timeAll;
    private JComboBox<String> bottom_combobox_playMode;

    //for drag_window
    private Point drag_initPos;

    //for save music
    //private Vector<Music> music_list;
    private PlayingVector music_list;

    //for player
    //private Player player;
    //private long current_music_mircosecond_length;

    //for show time
    private Thread thread_refreshTime;
    private boolean thread_refreshTime_quit;


    public WebeasyMusicGui(){
        setSize(window_width, window_height);
        setTitle(window_name);
        setUndecorated(true);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        drag_initPos = new Point(0, 0);

        music_list = new PlayingVector();
        MessageManager.getMessageManager().setPlayingVector(music_list);

        thread_refreshTime = new Thread();
        thread_refreshTime_quit = false;

        CreateAllCompnent();

        //player = new Player();
        //current_music_mircosecond_length = 0;

        /*if(!music_list.isEmpty()){
            SelectMusic(music_list.get(current_select_music).getFileName());
        }
        if(music_list.isSelected()){
            SelectMusic(music_list.getSelectedFileName());
        }*/
        music_list.SelectMusic(0);
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
        CreateMusicCellEditor();
        CreateMusicTableCellRenderer();

        table_music_list = new JTable(table_music_list_model);
        table_music_list.getColumnModel().getColumn(2).setCellEditor(table_music_button_editor);
        table_music_list.setDefaultRenderer(Object.class, table_music_list_renderer);
        table_music_list.setShowGrid(false);
        table_music_list.setIntercellSpacing(new Dimension(0, 0));
        table_music_list.setAutoCreateRowSorter(true);
        table_music_list.setColumnSelectionAllowed(false);
        table_music_list.setRowSelectionAllowed(false);

        table_scroll_pane = new JScrollPane(table_music_list);
        getContentPane().add(table_scroll_pane, BorderLayout.EAST);
    }

    private void CreateMusicTableModel(){
        table_music_list_model = new MusicTableModel();
        table_music_list_model.setColumnIdentifiers(Music.getHeaders());
        table_music_list_model.addColumn("button");
    }

    private void CreateMusicCellEditor(){
        table_music_button_editor = new MusicTableEditor(music_list);
    }

    private void CreateMusicTableCellRenderer(){
        table_music_list_renderer = new MusicTableRenderer();
    }

    private void CreateLeftBar(){
        left_bar = new JToolBar();
        left_bar.setFloatable(false);

        left_button_addFiles = new JButton("添加音乐");
        left_button_addDirectory = new JButton("添加文件夹");

        left_bar.add(left_button_addFiles);
        left_bar.add(left_button_addDirectory);
        getContentPane().add(left_bar, BorderLayout.WEST);
    }

    private void CreateControlBar(){
        bottom_bar = new JToolBar();
        bottom_bar.setFloatable(false);

        bottom_button_previous = new JButton("上一首");

        bottom_button_control = new JButton("播放");

        bottom_button_next = new JButton("下一首");


        bottom_slider_time = new JSlider();
        bottom_slider_time.setValue(0);


        bottom_label_timeNow = new JLabel("00:00");
        bottom_label_timeAll = new JLabel(" / 00:00");

        bottom_combobox_playMode = new JComboBox<>();
        bottom_combobox_playMode.addItem("单曲循环");
        bottom_combobox_playMode.addItem("顺序循环");
        bottom_combobox_playMode.addItem("随机播放");

        bottom_bar.add(bottom_button_previous);
        bottom_bar.add(bottom_button_control);
        bottom_bar.add(bottom_button_next);
        bottom_bar.add(bottom_slider_time);
        bottom_bar.add(bottom_label_timeNow);
        bottom_bar.add(bottom_label_timeAll);
        bottom_bar.add(bottom_combobox_playMode);

        getContentPane().add(bottom_bar, BorderLayout.SOUTH);
    }

    public void AddMusic(Music music){
        /*boolean hasSelected = true;
        if(music_list.isEmpty()){
            hasSelected = false;
        }

        if(music_list.contains(music)){
            return;
        }*/
        music_list.AddMusic(music);
        table_music_list_model.setDataVector(music_list.getMusicVector(), null);
        table_music_list_model.fireTableRowsInserted(music_list.getMusicSize() - 1, music_list.getMusicSize() - 1);
        table_music_list.updateUI();

        /*if(!hasSelected){
            SelectMusic(music_list.get(current_select_music).getFileName());
        }*/
    }

    public void SetTimer(){
        /*player.Select(fileName);
        current_music_mircosecond_length = player.getMicroSecondLength();
        int music_second_length = (int)current_music_mircosecond_length / 1000000;*/

        bottom_slider_time.setMinimum(0);
        bottom_slider_time.setMaximum(music_list.getSelectedMusicMicroSecond());
        bottom_slider_time.setValue(0);

        bottom_label_timeAll.setText(" / " + music_list.getSelectedMusicMicroSecondString());

        RefreshTimer();

        thread_refreshTime = new Thread(() -> {
            while(!thread_refreshTime_quit) {
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

        bottom_button_control.setText("播放");
    }

    /*private void Pause(){
        music_list.Pause();
        bottom_button_control.setText("播放");
    }

    private void Play(){
        music_list.Play();
    }*/

    private void RefreshTimer() {
        bottom_label_timeNow.setText(music_list.getCurrentMusicMicroSecondString());
        bottom_slider_time.setValue(music_list.getCurrentMusicMicroSecond());

        if(bottom_slider_time.getValue() >= bottom_slider_time.getMaximum()){
            switch ((String)bottom_combobox_playMode.getSelectedItem()){
                case "单曲循环":
                    music_list.PlayLoop();
                    break;

                case "顺序循环":
                    music_list.PlayLine();
                    break;

                case "随机播放":
                    music_list.PlayRandom();
                    break;

                default:
                    break;
            }
            return;
        }
    }

    public void RefreshTable(int row){
        table_music_list_model.fireTableRowsUpdated(row, row);
    }

    private void AddFrameListener(){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                super.mousePressed(e);
                drag_initPos.x = e.getX();
                drag_initPos.y = e.getY();
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
                setLocation(last_location.x + e.getX() - drag_initPos.x, last_location.y + e.getY() - drag_initPos.y);
            }
        });
    }

    private void AddMusicTableListener(){
        table_scroll_pane.setDropTarget(new DropTarget(this, new DropTargetAdapter() {
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
        left_button_addFiles.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("./");
            fileChooser.setFileFilter(new MusicFileFilter());
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.showDialog(new JLabel(), "添加");
            File[] files = fileChooser.getSelectedFiles();
            for(File file : files){
                AddMusic(new Music(file));
            }
        });

        left_button_addDirectory.addActionListener(e -> {
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
        bottom_button_previous.addActionListener(e -> {
            music_list.GoPrevious();
        });

        bottom_button_control.addActionListener(e -> {
            if(bottom_button_control.getText() == "暂停"){
                music_list.Pause();
                bottom_button_control.setText("播放");
            }
            else{
                music_list.Play();
                bottom_button_control.setText("暂停");
            }
        });

        bottom_button_next.addActionListener(e -> {
            music_list.GoNext();
        });

        bottom_slider_time.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    music_list.SetMusicPosition(bottom_slider_time.getValue() * 1000000);
                }
            }
        );
    }
}
