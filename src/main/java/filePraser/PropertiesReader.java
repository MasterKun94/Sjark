package filePraser;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesReader {
    private Map<Object, Object> map;

    public PropertiesReader(Properties properties) {
        this.map = new HashMap<>(properties);
    }

    public String getString(String key) {
        return (String) map.get(key);
    }

    public PropertiesReader get() {
        return null;
    }
}
