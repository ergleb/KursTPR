package xfind;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hlib on 23.11.2015.
 */
public class Actions {
    public static List<List<Integer>> split(int length, List<Integer> needed){
        if (needed.size() == 1){
            List<List<Integer>> ans = new LinkedList<>();
            List<Integer> l = new LinkedList<>();
            l.add(length / needed.get(0));
            ans.add(l);
            return ans;
        }
        List<List<Integer>> ans = new LinkedList<>();
        for (int i = length / needed.get(0); i >= 0; i--) {
            int left = length - needed.get(0)*i;
            List<List<Integer>> temp = split(left,needed.subList(1, needed.size()));
            for (List list: temp) {
                List<Integer> l = new LinkedList<>();
                l.add(i);
                l.addAll(list);
                ans.add(l);
            }
        }

        return ans;
    }

    public static void main(String[] args) {
        List<Integer> needed = new LinkedList<>();
        needed.add(32);
        needed.add(18);
        needed.add(12);
        List<List<Integer>> list = split(92, needed);

    }
}
