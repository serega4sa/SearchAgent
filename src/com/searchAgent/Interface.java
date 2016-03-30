package com.searchAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Created by Sergey.Chmihun on 03/30/2016.
 */
public class Interface extends JFrame{

    JFrame frame = new JFrame();

    private JComboBox comboBoxqDur;
    private JTextField textFieldPath;
    private JTextField textFieldPages;
    private JComboBox comboBoxvDur;
    private JComboBox comboBoxLocation;
    private JPanel rootPanel;
    private JButton searchButton;
    private JButton cancelButton;
    private JLabel status;
    private JLabel CopyRight;

    public Interface() {
        super("Search Agent v1.0");
        JFrame.setDefaultLookAndFeelDecorated(true);

        setContentPane(rootPanel);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);

        textFieldPath.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                status.setText("");
            }
        });

        textFieldPages.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                status.setText("");
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Add code
            }
        });
    }

    /*class ActionSearch implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            verifier1 = new MyPathInputVerifier();
            verifier2 = new MyPagesInputVerifier();
            verifier3 = new MyTimeInputVerifier();
            if (verifier1.verifyEmpty(textFieldPath)) {
                if (verifier2.verifyEmpty(textFieldPages)) {
                    if (verifier3.verifyEmpty(textFieldTime)) {
                        fileInputName = textFieldPath.getText();
                        numberOfPages = Integer.parseInt(textFieldPages.getText());
                        textFieldPath.setText("");
                        textFieldPages.setText("");
                        title2.setText("wait until program finish parsing...");
                        title2.setForeground(Color.BLUE);

                        try {
                            SearchAgent.prog.runProgram(fileInputName, numberOfPages);
                        } catch (IOException e1) {
                            title2.setText("Invalid data format");
                            title2.setForeground(Color.RED);
                        }

                        title2.setText("action has been performed successfully");
                        title2.setForeground(Color.GREEN);

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }

                        System.exit(0);
                    } else {
                        title2.setText("Time interval is empty. Please, specify it");
                        title2.setForeground(Color.RED);
                        textFieldPages.setText("");
                    }
                } else {
                    title2.setText("Field with number of pages is empty. Please, specify it");
                    title2.setForeground(Color.RED);
                    textFieldPages.setText("");
                }
            } else {
                title2.setText("Path field is empty. Please, specify it");
                title2.setForeground(Color.RED);
                textFieldPath.setText("");
            }
        }
    }*/

    class ActionCancel implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public class MyPathInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField) input).getText();
            if (text.matches("[A-Z]:\\[a-zA-Z]+.txt")) {
                return true;
            } else {
                return false;
            }
        }

        public boolean verifyEmpty(JComponent input) {
            String text = ((JTextField) input).getText();
            if (!text.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public class MyPagesInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField) input).getText();
            if (text.matches("\\d")) {
                return true;
            } else {
                return false;
            }
        }

        public boolean verifyEmpty(JComponent input) {
            String text = ((JTextField) input).getText();
            if (!text.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public class MyqDurationInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String[] pattern = {"hour", "day", "week", "month", "year"};
            String text = ((JTextField) input).getText();
            for (String item : pattern) {
                if (text.equals(item)) {
                    return true;
                }
            }
            return false;
        }

        public boolean verifyEmpty(JComponent input) {
            String text = ((JTextField) input).getText();
            if (!text.isEmpty()) {
                return true;
            } else {
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
