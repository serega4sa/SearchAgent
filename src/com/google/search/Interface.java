package com.google.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Sergey.Chmihun on 03/30/2016.
 */
public class Interface extends JFrame{
    private static final Logger logger = LoggerFactory.getLogger(Interface.class.getName());
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
    private JCheckBox iviCheckBox;
    private Thread tRun;
    private Thread tStatus;
    private boolean isStopped;
    private  boolean isSuspended;
    private static ResourceBundle res = ResourceBundle.getBundle(SearchAgent.RESOURCE_PATH + "common_en");

    private MyPathInputVerifier verifier1;
    private MyPagesInputVerifier verifier2;

    public void setStopped(boolean stopped) {
        logger.debug("Set stopped = " + stopped);
        isStopped = stopped;
    }

    public void setSuspended(boolean suspended) {
        logger.debug("Set suspended = " + suspended);
        isSuspended = suspended;
    }

    public Interface() {
        super(res.getString("version"));
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

        iviCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                status.setText("");
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.debug("Action performed = " + e.getActionCommand());
                status.setText("");
                verifier1 = new MyPathInputVerifier();
                verifier2 = new MyPagesInputVerifier();
                if (verifier1.verifyEmpty(textFieldPath)) {
                    if (verifier2.verifyEmpty(textFieldPages)) {
                        if (verifier1.verify(textFieldPath)) {
                            if (verifier2.verify(textFieldPages)) {
                                isStopped = false;
                                isSuspended = false;
                                tStatus = new StatusThread();

                                String file = textFieldPath.getText();
                                SearchAgent.prog.setFileInputName(file);
                                SearchAgent.prog.setNumberOfPages(Integer.parseInt(textFieldPages.getText()));
                                SearchAgent.prog.setqDuration(String.valueOf(comboBoxqDur.getSelectedItem()));
                                SearchAgent.prog.setvDuration(String.valueOf(comboBoxvDur.getSelectedItem()));
                                SearchAgent.prog.setGoogleLocation(String.valueOf(comboBoxLocation.getSelectedItem()));

                                ArrayList<String> list = new ArrayList<>();
                                if (youtubeCheckBox.isSelected()) list.add("youtube");
                                if (iviCheckBox.isSelected()) list.add("ivi.ru");
                                if (megogoCheckBox.isSelected()) list.add("megogo");
                                if (TVZavrCheckBox.isSelected()) list.add("tvzavr");
                                if (!list.isEmpty()) SearchAgent.prog.setWhiteList(list);
                                else SearchAgent.prog.setWhiteList(null);

                                tRun = new SearchThread();
                            } else {
                                status.setText(res.getString("invalid.number"));
                                status.setForeground(Color.RED);
                                textFieldPages.setText("");
                            }
                        } else {
                            status.setText(res.getString("invalid.file.name"));
                            status.setForeground(Color.RED);
                            textFieldPath.setText("");
                        }
                    } else {
                        status.setText(res.getString("empty.number"));
                        status.setForeground(Color.RED);
                    }
                } else {
                    status.setText(res.getString("empty.file.name"));
                    status.setForeground(Color.RED);
                }
            }
        });
    }

    public Thread gettStatus() {
        return tStatus;
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
            if (text.matches("[a-zA-Z0-9_-]+.txt")) {
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

    public class SetText extends JLabel {
        @Override
        public void setText(String toBeDisplayed) {
            super.setText(toBeDisplayed);
        }
    }

    public class StatusThread extends Thread {
        public StatusThread() {
            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            StringBuilder str = new StringBuilder("processing");
            while (!isStopped) {
                if (!isSuspended) {
                    if (str.length() > 50) str.setLength(10);
                    else str.append(".");
                    status.setText(str.toString());
                    status.setForeground(Color.BLUE);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        logger.error("Interrupted Exception", e);
                    }
                } else {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("Interrupted Exception", e);
                    }
                }
            }

            if (!tRun.isInterrupted()) {
                status.setText(res.getString("the.end"));
                status.setForeground(Color.GREEN);
            }
        }
    };

    public class SearchThread extends Thread implements Thread.UncaughtExceptionHandler{
        public SearchThread() {
            start();
        }

        @Override
        public void run() {
            try {
                SearchAgent.prog.runProgram();
                isStopped = true;
            } catch (IOException e) {
                logger.error("General issue", e);
                isStopped = true;
                status.setText(res.getString("general.issue"));
                status.setForeground(Color.RED);
            }
        }

        /** Logger that writes all uncaught exceptions to the log file */
        private final Logger log = LoggerFactory.getLogger(SearchThread.class);

        public void uncaughtException(Thread t, Throwable ex) {
            log.error("Uncaught exception in thread: " + t.getName(), ex);
        }
    };
}
