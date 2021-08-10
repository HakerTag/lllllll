package com.google.zxing.client.android.encode;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

abstract class ContactEncoder {
    /* access modifiers changed from: package-private */
    public abstract String[] encode(List<String> list, String str, List<String> list2, List<String> list3, List<String> list4, List<String> list5, List<String> list6, String str2);

    ContactEncoder() {
    }

    static String trim(String s) {
        if (s == null) {
            return null;
        }
        String result = s.trim();
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    static void append(StringBuilder newContents, StringBuilder newDisplayContents, String prefix, String value, Formatter fieldFormatter, char terminator) {
        String trimmed = trim(value);
        if (trimmed != null) {
            newContents.append(prefix);
            newContents.append(fieldFormatter.format(trimmed, 0));
            newContents.append(terminator);
            newDisplayContents.append(trimmed);
            newDisplayContents.append('\n');
        }
    }

    static void appendUpToUnique(StringBuilder newContents, StringBuilder newDisplayContents, String prefix, List<String> values, int max, Formatter displayFormatter, Formatter fieldFormatter, char terminator) {
        if (values != null) {
            int count = 0;
            Collection<String> uniques = new HashSet<>(2);
            for (int i = 0; i < values.size(); i++) {
                String trimmed = trim(values.get(i));
                if (trimmed != null && !trimmed.isEmpty() && !uniques.contains(trimmed)) {
                    newContents.append(prefix);
                    newContents.append(fieldFormatter.format(trimmed, i));
                    newContents.append(terminator);
                    newDisplayContents.append(displayFormatter == null ? trimmed : displayFormatter.format(trimmed, i));
                    newDisplayContents.append('\n');
                    count++;
                    if (count != max) {
                        uniques.add(trimmed);
                    } else {
                        return;
                    }
                }
            }
        }
    }
}
