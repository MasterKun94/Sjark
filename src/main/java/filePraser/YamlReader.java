package filePraser;

import java.util.List;
import java.util.Map;

public class YamlReader {
    private final static String SPLIT = ".";
    private Map map;

    public YamlReader(Map map) {
        this.map = map;
    }

    public String getString(String key) {
        Object object = switchNode(key);
        if (object instanceof String) {
            return (String) object;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getArray(String key) {
        Object object = switchNode(key);
        if (object instanceof List) {
            return (List<String>) object;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public YamlReader get(String key) {
        Object object = switchNode(key);
        if (object instanceof Map) {
            return new YamlReader((Map) object);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private Object switchNode(String key) {
        String[] strings = key.split(SPLIT);
        int length = strings.length;
        Object tem;
        for (int i = 0; i < length - 1; i++) {
            tem = map.get(strings[i]);
            if (tem instanceof Map) {
                map = (Map) tem;
            } else {
                throw new IllegalArgumentException();
            }
        }
        return map.get(strings[length - 1]);
    }
}
