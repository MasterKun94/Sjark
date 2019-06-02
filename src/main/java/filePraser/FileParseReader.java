package filePraser;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Properties;

public class FileParseReader {
    public static PropertiesReader properties(String url) throws IOException {
        Properties properties = new Properties();
        properties.load(new BufferedReader(new FileReader(url)));
        return new PropertiesReader(properties);
    }

    public static YamlReader yaml(String url) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        LinkedHashMap map = yaml.loadAs(new BufferedReader(new FileReader(url)), LinkedHashMap.class);
        return new YamlReader(map);
    }

    public static FileParseReader xml(String url) {
        return null;
    }
}
