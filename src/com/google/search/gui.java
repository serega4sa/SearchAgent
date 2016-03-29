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

    private JLabel title1;
    private static JLabel title2;
    private JLabel infoPath;
    private JLabel infoPages;
    private JTextField textFieldPath;
    private JTextField textFieldPages;
    private JPanel panel1;
    private JPanel panel2;
    private JButton buttonSearch;
    private JButton buttonCancel;

    public gui() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        BoxLayout boxLayout = new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS);
        frame.setLayout(boxLayout);
        frame.pack();
        frame.setVisible(true);

        panel1 = new JPanel();
        frame.add(panel1);

        title1 = new JLabel("Status bar:");
        //title1.setSize(-1, 100);
        title1.setFont(new Font("Helevtica", Font.PLAIN, 16));
        title1.setAlignmentX(Component.RIGHT_ALIGNMENT);
        title1.setVisible(true);
        panel1.add(title1);

        title1 = new JLabel("enter data to the empty fields");
        //title1.setSize(-1, 100);
        title1.setFont(new Font("Helevtica", Font.PLAIN, 16));
        title1.setAlignmentX(Component.LEFT_ALIGNMENT);
        title1.setVisible(true);
        panel1.add(title2);

        infoPath = new JLabel("<html>Enter path to the input file. <font color='#808080'>Example: D:/data.txt</font></html>");
        infoPath.setSize(-1, 100);
        infoPath.setFont(new Font("Helevtica", Font.PLAIN, 16));
        infoPath.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(infoPath);

        textFieldPath = new JTextField();
        textFieldPath.setSize(500, 50);
        textFieldPath.setFont(new Font("Helevtica", Font.PLAIN, 16));
        textFieldPath.setAlignmentX(Component.CENTER_ALIGNMENT);
        textFieldPath.setAlignmentY(Component.TOP_ALIGNMENT);
        frame.add(textFieldPath);

        infoPages = new JLabel("<html>Enter number of pages to be parsed for every request</html>");
        infoPages.setSize(-1, 100);
        infoPages.setFont(new Font("Helevtica", Font.PLAIN, 16));
        infoPages.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(infoPages);

        textFieldPages = new JTextField();
        textFieldPages.setSize(500, 50);
        textFieldPages.setFont(new Font("Helevtica", Font.PLAIN, 16));
        textFieldPages.setAlignmentX(Component.CENTER_ALIGNMENT);
        textFieldPages.setAlignmentY(Component.TOP_ALIGNMENT);
        frame.add(textFieldPages);

        panel2 = new JPanel();
        frame.add(panel2);

        buttonSearch = new JButton("Search");
        panel2.add(buttonSearch);
        buttonSearch.addActionListener(new Action());

        buttonCancel = new JButton("Cancel");
        panel2.add(buttonCancel);

        frame.setLocationRelativeTo(null);
        frame.setSize(600, 300);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    static class Action implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            title2.setText("action has been performed");
            title2.setForeground(Color.GREEN);
        }
    }

    public class setText extends JLabel {
        @Override
        public void setText(String toBeDisplayed) {
            super.setText(toBeDisplayed);
        }
    }
}
