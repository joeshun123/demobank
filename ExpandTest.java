package com.example.testbed;

import com.mifmif.common.regex.Generex;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ExpandTest {
    public void test() {
        List<Map<String, String>> data = new LinkedList<>();
        Map<String, String> d1 = new LinkedHashMap<>();
        d1.put("F1", "Q");
        d1.put("F2", "[0-5]");
        d1.put("F3", "A");
        //d1.put("F3", "[A-C]");
        //d1.put("F4", "[a-b]1");
        //d1.put("F5", "[1-2]R");
        data.add(d1);

        Generex generex = new Generex("[a-b][0-1]{2}");
        //Generex generex = new Generex("(a|b)");
        List<String> matchedStrs = generex.getAllMatchedStrings();
        log.info(String.valueOf(matchedStrs));

        expand(data);
        data.forEach(d->{
            log.info(d.toString());
        });

    }
    private boolean expand(final List<Map<String, String>> data, final int index, final Map<String, String> cur,
                        final String key, final String value) {
        boolean isExpand = false;
        List<String> seq = getSequence(value);
        for (String seqChar : seq) {
            Map<String, String> cloned = cloneMap(cur);
            cloned.put(key, seqChar);
            if (!isExpand) {
                data.remove(index);
            }
            data.add(cloned);
            isExpand = true;
        }
        return isExpand;
    }
    private void expand(final List<Map<String, String>> data) {
        int i=0;
        while (i<data.size()) {
            boolean isExpand = false;
            Map<String, String> cur = data.get(i);
            for (Map.Entry<String, String> entry : cur.entrySet()) {
                isExpand = expand(data, i, cur, entry.getKey(), entry.getValue());
                if (isExpand) {
                    break;
                }
            }
            if (isExpand) {
                i=0;
            } else {
                i++;
            }
        }
    }

    private Map<String, String> cloneMap(final Map<String, String> source) {
        Map<String, String> map = new LinkedHashMap<>();
        source.forEach(map::put);
        return map;
    }
    private List<String> getSequence(final String seq) {
        if (seq.contains("[")) {
            Generex generex = new Generex(seq);
            return generex.getAllMatchedStrings();
        } else {
            return new ArrayList<>();
        }
    }
    public static void main(String args[]) {
        ExpandTest expandTest = new ExpandTest();
        expandTest.test();
    }
}
