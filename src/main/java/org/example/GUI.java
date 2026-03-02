package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GUI extends JFrame {
    File folder = new File("");

    public GUI() {
        setTitle("Statisfy");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton folderButton = new JButton("Open Folder");
        folderButton.addActionListener(action -> {
            JFileChooser folderChooser = new JFileChooser();
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            folderChooser.showOpenDialog(this);
            this.folder = folderChooser.getSelectedFile();
        });

        add(folderButton);
        setVisible(true);
    }
}