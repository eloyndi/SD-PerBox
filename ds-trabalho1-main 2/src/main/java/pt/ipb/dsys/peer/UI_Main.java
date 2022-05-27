package pt.ipb.dsys.peer;

import pt.ipb.dsys.peer.box.Repository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI_Main {
    public JPanel root;
    private JButton button_save;
    private JButton button_download;
    private JButton button_update;
    private JButton button_delete;
    private JSpinner spinner_replicas;
    private JTextField textField_pathname;

    private final JFrame frame;
    private final Repository repository;

    public UI_Main(Repository repository, JFrame frame1) {
        this.repository = repository;
        this.frame = frame1;

        button_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pathname = textField_pathname.getText();
                int replicas = (Integer)spinner_replicas.getValue();
                repository.SaveFile(pathname, replicas);
            }
        });

        button_download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pathname = textField_pathname.getText();
                repository.DownloadFile(pathname);
            }
        });

        button_update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pathname = textField_pathname.getText();
                int replicas = (Integer)spinner_replicas.getValue();
                repository.UpdateFile(pathname, replicas);
            }
        });

        button_delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pathname = textField_pathname.getText();
                repository.DeleteFile(pathname);
            }
        });
    }
}
