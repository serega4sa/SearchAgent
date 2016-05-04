package com.google.search;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

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
    private JCheckBox youtubeCheckBox;
    private JCheckBox megogoCheckBox;
    private JCheckBox TVZavrCheckBox;

    private MyPathInputVerifier verifier1;
    private MyPagesInputVerifier verifier2;

    public Interface() {
        super("Search Agent v1.0");
        JFrame.setDefaultLookAndFeelDecorated(true);

        setContentPane(rootPanel);

        pack();
        this.setLocationRelativeTo(null);
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

        comboBoxqDur.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                status.setText("");
            }
        });

        comboBoxvDur.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                status.setText("");
            }
        });

        comboBoxLocation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                status.setText("");
            }
        });

        youtubeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                status.setText("");
            }
        });

        megogoCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                status.setText("");
            }
        });

        TVZavrCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                status.setText("");
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifier1 = new MyPathInputVerifier();
                verifier2 = new MyPagesInputVerifier();
                if (verifier1.verifyEmpty(textFieldPath)) {
                    if (verifier2.verifyEmpty(textFieldPages)) {
                        if (verifier1.verify(textFieldPath)) {
                            if (verifier2.verify(textFieldPages)) {
                                status.setText("processing...");
                                status.setForeground(Color.BLUE);

                                String file = textFieldPath.getText();
                                SearchAgent.prog.setFileInputName(file);
                                SearchAgent.prog.setNumberOfPages(Integer.parseInt(textFieldPages.getText()));
                                SearchAgent.prog.setqDuration(String.valueOf(comboBoxqDur.getSelectedItem()));
                                SearchAgent.prog.setvDuration(String.valueOf(comboBoxvDur.getSelectedItem()));
                                SearchAgent.prog.setGoogleLocation(String.valueOf(comboBoxLocation.getSelectedItem()));

                                ArrayList<String> list = new ArrayList<>();
                                if (youtubeCheckBox.isSelected()) list.add("youtube");
                                if (megogoCheckBox.isSelected()) list.add("megogo");
                                if (TVZavrCheckBox.isSelected()) list.add("tvzavr");
                                if (!list.isEmpty()) SearchAgent.prog.setWhiteList(list);
                                else SearchAgent.prog.setWhiteList(null);

                                try {
                                    SearchAgent.prog.runProgram();
                                } catch (IOException e1) {
                                    status.setText("Something went wrong. Try again");
                                    status.setForeground(Color.RED);
                                }
                            } else {
                                status.setText("Invalid data format. Number of pages should be integer");
                                status.setForeground(Color.RED);
                                textFieldPages.setText("");
                            }
                        } else {
                            status.setText("Invalid path format. Use next format: C:/data.txt");
                            status.setForeground(Color.RED);
                            textFieldPath.setText("");
                        }
                    } else {
                        status.setText("Number of pages is not specified. Please, fill it");
                        status.setForeground(Color.RED);
                    }
                } else {
                    status.setText("Path is not specified. Please, fill it");
                    status.setForeground(Color.RED);
                }
            }
        });
    }

    public JLabel getStatus() {
        return status;
    }

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
            if (text.matches("[a-zA-Z]:[/[a-zA-Z0-9_-]+]+.txt")) {
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
            if (text.matches("\\d+")) {
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

    public class setText extends JLabel {
        @Override
        public void setText(String toBeDisplayed) {
            super.setText(toBeDisplayed);
        }
    }
}
