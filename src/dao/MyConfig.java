package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MyConfig {

    public static final String PATH_TO_PROPERTIES = "src/config.properties";

    public static String setConfig(String parametr) {

        Properties prop = new Properties();
        try {
            FileInputStream file = new FileInputStream(PATH_TO_PROPERTIES);
            prop.load(file);
        } catch (IOException e) {
            System.out.println("Ошибка в программе: файл " + PATH_TO_PROPERTIES + " не обнаружено");
            e.printStackTrace();
        }
        return prop.getProperty(parametr);
    }
}
