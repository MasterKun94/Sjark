package pool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PointerIndexer {
    private int[] hashcodeArray;
    private int[] pointerArray;

//    public static <T> PointerIndexer get(Map<T, Integer> map) {
//        List<T> list = new ArrayList<>(map.keySet());
//        list.sort(Comparator.comparingInt(Object::hashCode));
//        int listSize = list.size();
//        PointerIndexer indexer = new PointerIndexer();
//        indexer.hashcodeArray = new int[listSize];
//        indexer.pointerArray = new int[listSize];
//        int[] hArray = indexer.hashcodeArray;
//        int[] pArray = indexer.pointerArray;
//        T o;
//        for (int i = 0; i < listSize; i++) {
//            o = list.get(i);
//            hArray[i] = o.hashCode();
//            pArray[i] = map.get(o);
//        }
//        return indexer;
//    }
//
//    public <T> int findPointer(T object) {
//        int reqHash = object.hashCode();
//        int capacity = pointerArray.length;
//        int minHash = hashcodeArray[0];
//        int maxHash = hashcodeArray[capacity - 1];
//        int get;
//        do {
//            get = pointerArray[reqHash / (maxHash - minHash) * capacity];
//            if (reqHash > get) minHash = get;
//            else if (reqHash < get) maxHash = get;
//        } while (reqHash != get);
//
//        return get;
//    }
}
