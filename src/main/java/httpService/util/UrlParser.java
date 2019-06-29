package httpService.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UrlParser {
    private String[] url;
    private List<Integer> index;

    private UrlParser(String path) {
        url = path.split("/");
        index = new ArrayList<>();
        for (int i = 0; i < url.length; i++) {
            String s = url[i];
            if (s.startsWith("{") && s.endsWith("}")) {
                index.add(i);
                url[i] = url[i].substring(1, s.length() - 1);
            }
        }
    }

    public static UrlParser of(String url) {
        return new UrlParser(url);
    }

    public int pathVariableNumber() {
        return index.size();
    }

    public String[] getPathVariable() {
        String[] variable = new String[index.size()];
        for (int i = 0; i < index.size(); i++) {
            variable[i] = url[index.get(i)];
        }
        return variable;
    }

    public void addPathVariable(List<String> names, List<String> values) {

        for (int i = 0; i < index.size(); i++) {
            int idx = index.get(i);
            String varName = url[idx];
            for (String name : names) {
                if (name.equals(varName)) {
                    url[idx] = values.get(i);
                }
            }
        }
    }

    public String parsePath(Map<String, String> pathVarMap) {
        String[] parseUrl = url.clone();
        for (int i = 0; i < index.size(); i++) {
            int idx = index.get(i);
            parseUrl[idx] = pathVarMap.get(parseUrl[idx]);
        }
        boolean isFirst = true;
        StringBuilder finalUrl = new StringBuilder();
        for (String s : parseUrl) {
            if (isFirst) {
                finalUrl.append(s.trim());
                isFirst = false;
            } else {
                finalUrl.append("/").append(s.trim());
            }
        }
        return finalUrl.toString();
    }

//    @Override
//    public String toString() {
//        boolean isFirst = true;
//        StringBuilder finalUrl = new StringBuilder();
//        for (String s : url) {
//            if (isFirst) {
//                finalUrl.append(s.trim());
//                isFirst = false;
//            } else {
//                finalUrl.append("/").append(s.trim());
//            }
//        }
//        return finalUrl.toString();
//    }
}
