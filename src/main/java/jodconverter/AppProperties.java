package jodconverter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppProperties {
    private static final String CONFIG_PROPERTIES = "config.properties";
    private static final AppProperties INSTANCE = new AppProperties();
    Properties property = null;

    public static AppProperties getInstance() {
        return INSTANCE;
    }

    public AppProperties() {
        property = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        FileInputStream fis;

        try {
            fis = new FileInputStream(CONFIG_PROPERTIES);
            property.load(fis);
        } catch (IOException e) {
            System.err.println("������: ���� ������� ����������!");
            try {
                save();
            } catch (IOException e1) {
                System.err.println("������: ���������� ������� ������������!");
            }
        }

    }

    public String getLastDirectory() {
        String lastDirectory = property.getProperty("lastSelectedDirectory");
        if (lastDirectory == null) {
            lastDirectory = "outputFiles";
            property.setProperty("lastSelectedDirectory", lastDirectory);
        }
        return lastDirectory;
    }

    public AppProperties setLastDirectory(String lastDirectory) {
        property.setProperty("lastSelectedDirectory", lastDirectory);
        return INSTANCE;
    }

    public void save() throws IOException {
        property.store(new FileOutputStream(CONFIG_PROPERTIES), CONFIG_PROPERTIES);
    }
}
