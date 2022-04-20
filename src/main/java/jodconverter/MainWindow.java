package jodconverter;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainWindow extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final int MW_WIDTH = 1100;
    private static final int MW_HEIGHT = 800;
    private static final int LABEL_WIDTH = 150;


    JPanel mainPanel;
    JButton convertButton;
    JTextPane outputA;
    String startOutputTemplate = "<html><head><meta charset=\"utf-8\"><style>p {font-size: 6pt;} a {text-decoration: none;}</style></head><body>%s</body></html>";
    String outputText = "";


    String file;

    private final JLabel baseOutDirLabel = new JLabel("Base storage folder:");
    private final JTextField baseOutputDirArea;

    private final JLabel additionOutDirLabel = new JLabel("Addition subfolder:");
    private final JTextField additionOutputDirArea;
    private String baseOutoutDir;

    public MainWindow() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(MW_WIDTH, MW_HEIGHT));

        baseOutoutDir = initBaseOutputDir(AppProperties.getInstance().getLastDirectory());
        JButton loadButton = initLoadButton();
        this.convertButton = initConvertButton();
        JButton clearButton = initClearButton();
        JButton selectBaseDir = initSelectBaseDirButton();

        baseOutDirLabel.setSize(LABEL_WIDTH, 20);
        baseOutDirLabel.setPreferredSize(new Dimension(LABEL_WIDTH, 15));
        baseOutputDirArea = new JTextField(baseOutoutDir);
        baseOutputDirArea.setEditable(false);
        baseOutputDirArea.setFont(baseOutputDirArea.getFont().deriveFont(25));

        additionOutDirLabel.setPreferredSize(new Dimension(LABEL_WIDTH, 15));
        additionOutputDirArea = new JTextField(".");
        additionOutputDirArea.setFont(additionOutputDirArea.getFont().deriveFont(25));

        Box controlPanelBoxH = Box.createHorizontalBox();
        controlPanelBoxH.add(loadButton);
        controlPanelBoxH.add(Box.createHorizontalStrut(5));
        controlPanelBoxH.add(convertButton);
        controlPanelBoxH.add(Box.createHorizontalStrut(5));
        controlPanelBoxH.add(clearButton);
        controlPanelBoxH.add(Box.createHorizontalStrut(5));
        controlPanelBoxH.add(selectBaseDir);
        controlPanelBoxH.add(Box.createHorizontalGlue());


        Box baseDirPanelBoxH = Box.createHorizontalBox();
        baseDirPanelBoxH.add(baseOutDirLabel);
        baseDirPanelBoxH.add(baseOutputDirArea);

//		controlPanelBoxH.add(Box.createHorizontalStrut(20));
//		controlPanelBoxH.add(outDirLabel);
//		controlPanelBoxH.add(outputDirArea);

        Box additionDirPanelBoxH = Box.createHorizontalBox();
        additionDirPanelBoxH.add(additionOutDirLabel);
        additionDirPanelBoxH.add(additionOutputDirArea);

        Box controlVert = Box.createVerticalBox();
        controlVert.add(controlPanelBoxH);
        controlVert.add(baseDirPanelBoxH);
        controlVert.add(additionDirPanelBoxH);


        outputA = new JTextPane();
        outputA.setEditable(false);
        ((DefaultCaret) outputA.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        outputA.setContentType("text/html");
        outputA.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        if (!Desktop.isDesktopSupported()) {
                            System.out.println("Desktop is not supported");
                            return;
                        }

                        Desktop.getDesktop().open(new File(e.getDescription()));
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        System.out.println("Can't Open file:" + e1);
                    }
                }
            }
        });
        JScrollPane outputATextArea = new JScrollPane(outputA);

        Box mainVert = Box.createVerticalBox();
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, controlVert, outputATextArea);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(80);
        mainVert.add(splitPane);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        mainPanel.add(mainVert);

        pack();

        add(mainPanel);
        ExcelToPdfConverter.getInstance().registerStatusAction(new IPrintAction() {

            @Override
            public void print(String str) {
                appendOut(str);
            }
        });
    }

    private String initBaseOutputDir(String basePath) {
        SimpleDateFormat sdf = new SimpleDateFormat("_yyyy-MM-dd_HH.mm.ss");
        File baseOutputDir = new File(basePath);
        while (true) {
            if (!baseOutputDir.exists()) {
                if (baseOutputDir.mkdirs()) {
                    return baseOutputDir.getAbsolutePath();
                }
                return null;
            } else if (!baseOutputDir.isDirectory()) {
                baseOutputDir = new File(basePath + sdf.format(new Date()));
            } else {
                return baseOutputDir.getAbsolutePath();
            }
        }
    }

    private JButton initSelectBaseDirButton() {
        JButton chooseButton = new JButton("Choose base out folder");
        chooseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new java.io.File(baseOutoutDir)); // start at application current directory
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fc.showSaveDialog(mainPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File yourFolder = fc.getSelectedFile();
                    baseOutoutDir = yourFolder.getAbsolutePath();
                    try {
                        AppProperties.getInstance().setLastDirectory(baseOutoutDir).save();
                    } catch (IOException e1) {
                        appendOut("ERROR: Can,t save config file " + e1);
                    }

                    baseOutputDirArea.setText(baseOutoutDir);
                }
            }
        });

        return chooseButton;
    }

    private JButton initLoadButton() {
        JButton loadButton = new JButton("Open File");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                file = openFile();
                if (file != null) {
                    loadExcel(file);
                    appendOut(HtmlUtils.getLinkFromPath(file) + " was loaded <hr>");
                    convertButton.setEnabled(true);
                }
            }
        });
        return loadButton;
    }

    private JButton initClearButton() {
        JButton clearButton = new JButton("Clear log");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputA.setText("");
                outputText = "";
            }

        });
        return clearButton;
    }

    private JButton initConvertButton() {
        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // ExcelToHTMLConverter.POIconvertFile(new File(file), new File("outputFiles"));
                // ExcelToHTMLConverter.AsposeConvertFile(new File(file), new
                // File("outputFiles"));
                File outdir = new File(baseOutoutDir + File.separatorChar + additionOutputDirArea.getText());
                if (!outdir.exists()) {
                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    JOptionPane.showConfirmDialog(null, "Directory is not exist. Would You Like to create the '"
                            + additionOutputDirArea.getText() + "' directory and continue?", "create directory?", dialogButton);
                    if (dialogButton == JOptionPane.YES_OPTION) {
                        boolean result = outdir.mkdirs();
                        if (!result) {
                            appendOut("Cant't create directory. Check file system manually");
                            return;
                        }
                    } else {
                        return;
                    }

                }
                convertButton.setEnabled(false);
                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            appendOut("Converting started, please wait...");
                            ExcelToPdfConverter.getInstance().JodConvertFileToSheetFiles(new File(file),
                                    outdir);
                            appendOut("Converting was complete");
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block\
                            appendOut(e1.getMessage());
                        } finally {
                            convertButton.setEnabled(true);
                        }
                    }
                });
                t.start();


            }
        });
        convertButton.setEnabled(false);
        return convertButton;
    }

    //
    private String openFile() {
        File file = getTemplateFolder();
        JFileChooser c = new JFileChooser();
        if (file != null) {
            c.setCurrentDirectory(file);
        }
        //c.setFileFilter();
        c.addChoosableFileFilter(new FileFilter() {

            @Override
            public String getDescription() {
                return "Excel File";
            }

            @Override
            public boolean accept(File f) {
                return f.getName().matches(".+\\.xlsx");
            }
        });
        int rVal = c.showOpenDialog(MainWindow.this);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            return c.getCurrentDirectory().toString() + File.separatorChar + c.getSelectedFile().getName();
        }

        return null;
    }

    private File getTemplateFolder() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("inputFiles");
        File file = null;
        if (url != null) {
            file = new File(classLoader.getResource("inputFiles").getFile());
        }
        if (file == null || !file.isDirectory()) {
            file = new File("inputFiles");
        }

        return file;
    }

    private void appendOut(String string) {
        outputText += "<br>" + string;
        outputA.setText(String.format(startOutputTemplate, outputText));
        //outputA.setCaretPosition(0);


    }

    public void loadExcel(String File) {
    }

}
