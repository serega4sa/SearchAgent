package com.google.search;

/**
 * Created by Sergey.Chmihun on 03/29/2016.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class gui {
    static JFrame frame = new JFrame("Search Agent v1.0");

    private static JLabel title;
    private JTextField textField;
    private JPanel panel;
    private JButton buttonSearch;
    private JButton buttonCancel;

    public gui() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        BoxLayout boxLayout = new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS);
        frame.setLayout(boxLayout);
        frame.pack();
        frame.setVisible(true);

        title = new JLabel("Enter path to the input file. Example: D:/data.txt");
        title.setSize(-1, 100);
        title.setFont(new Font("Helevtica", Font.PLAIN, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setAlignmentY(Component.TOP_ALIGNMENT);
        frame.add(title);

        textField = new JTextField();
        textField.setSize(-1, 100);
        textField.setFont(new Font("Helevtica", Font.PLAIN, 16));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setAlignmentY(Component.TOP_ALIGNMENT);
        frame.add(textField);

        panel = new JPanel();
        frame.add(panel);

        buttonSearch = new JButton("Search");
        panel.add(buttonSearch);
        buttonSearch.addActionListener(new Action());

        buttonCancel = new JButton("Cancel");
        panel.add(buttonCancel);

        frame.setLocationRelativeTo(null);
        frame.setSize(600, 300);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    static class Action implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            title.setText("Action has been performed");
            title.setVisible(true);
        }
    }

    public class setText extends JLabel {
        @Override
        public void setText(String toBeDisplayed) {
            super.setText(toBeDisplayed);
        }
    }
}
