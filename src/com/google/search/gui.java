package com.google.search;

/**
 * Created by Sergey.Chmihun on 03/29/2016.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class gui {
    JFrame frame = new JFrame("Search Agent v1.0");

    private JLabel title1;
    private JLabel title2;
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
        panel1.setSize(-1, 100);
        frame.add(panel1);

        title1 = new JLabel("Status bar:");
        //title1.setSize(-1, 100);
        title1.setFont(new Font("Helevtica", Font.PLAIN, 16));
        //title1.setAlignmentX(Component.RIGHT_ALIGNMENT);
        title1.setVisible(true);
        title1.setHorizontalAlignment(SwingConstants.RIGHT);
        panel1.add(title1);

        title2 = new JLabel("enter data to the empty fields");
        //title1.setSize(-1, 100);
        title2.setFont(new Font("Helevtica", Font.PLAIN, 16));
        //title1.setAlignmentX(Component.LEFT_ALIGNMENT);
        title2.setVisible(true);
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
        buttonSearch.addActionListener(new ActionSearch());

        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(new ActionCancel());
        panel2.add(buttonCancel);

        frame.setLocationRelativeTo(null);
        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class ActionSearch implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            MyInputVerifier verifier = new MyInputVerifier();
            if (verifier.verify(textFieldPath) && verifier.verify(textFieldPages)) {
                title2.setText("action has been performed");
                title2.setForeground(Color.GREEN);
            } else {
                title2.setText("not all data had been specified");
                title2.setForeground(Color.RED);
            }
        }
    }

    class ActionCancel implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public class MyInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField) input).getText();
            try {
                BigDecimal value = new BigDecimal(text);
                return (value.scale() <= Math.abs(4));
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    public class setText extends JLabel {
        @Override
        public void setText(String toBeDisplayed) {
            super.setText(toBeDisplayed);
        }
    }
}
