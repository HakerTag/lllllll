package com.google.zxing.client.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.google.zxing.DecodeHintType;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

final class DecodeHintManager {
    private static final Pattern COMMA = Pattern.compile(",");
    private static final String TAG = DecodeHintManager.class.getSimpleName();

    private DecodeHintManager() {
    }

    private static Map<String, String> splitQuery(String query) {
        String text;
        String name;
        Map<String, String> map = new HashMap<>();
        int pos = 0;
        while (true) {
            if (pos >= query.length()) {
                break;
            } else if (query.charAt(pos) == '&') {
                pos++;
            } else {
                int amp = query.indexOf(38, pos);
                int equ = query.indexOf(61, pos);
                if (amp < 0) {
                    if (equ < 0) {
                        name = Uri.decode(query.substring(pos).replace('+', ' '));
                        text = "";
                    } else {
                        String name2 = Uri.decode(query.substring(pos, equ).replace('+', ' '));
                        text = Uri.decode(query.substring(equ + 1).replace('+', ' '));
                        name = name2;
                    }
                    if (!map.containsKey(name)) {
                        map.put(name, text);
                    }
                } else if (equ < 0 || equ > amp) {
                    String name3 = Uri.decode(query.substring(pos, amp).replace('+', ' '));
                    if (!map.containsKey(name3)) {
                        map.put(name3, "");
                    }
                    pos = amp + 1;
                } else {
                    String name4 = Uri.decode(query.substring(pos, equ).replace('+', ' '));
                    String text2 = Uri.decode(query.substring(equ + 1, amp).replace('+', ' '));
                    if (!map.containsKey(name4)) {
                        map.put(name4, text2);
                    }
                    pos = amp + 1;
                }
            }
        }
        return map;
    }

    static Map<DecodeHintType, ?> parseDecodeHints(Uri inputUri) {
        String parameterText;
        String parameterText2;
        String query = inputUri.getEncodedQuery();
        if (query == null || query.isEmpty()) {
            return null;
        }
        Map<String, String> parameters = splitQuery(query);
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        DecodeHintType[] values = DecodeHintType.values();
        int length = values.length;
        int i = 0;
        int i2 = 0;
        while (i2 < length) {
            DecodeHintType hintType = values[i2];
            if (!(hintType == DecodeHintType.CHARACTER_SET || hintType == DecodeHintType.NEED_RESULT_POINT_CALLBACK || hintType == DecodeHintType.POSSIBLE_FORMATS || (parameterText = parameters.get(hintType.name())) == null)) {
                if (hintType.getValueType().equals(Object.class)) {
                    hints.put(hintType, parameterText);
                } else if (hintType.getValueType().equals(Void.class)) {
                    hints.put(hintType, Boolean.TRUE);
                } else if (hintType.getValueType().equals(String.class)) {
                    hints.put(hintType, parameterText);
                } else if (hintType.getValueType().equals(Boolean.class)) {
                    if (parameterText.isEmpty()) {
                        hints.put(hintType, Boolean.TRUE);
                    } else if ("0".equals(parameterText) || "false".equalsIgnoreCase(parameterText) || "no".equalsIgnoreCase(parameterText)) {
                        hints.put(hintType, Boolean.FALSE);
                    } else {
                        hints.put(hintType, Boolean.TRUE);
                    }
                } else if (hintType.getValueType().equals(int[].class)) {
                    if (parameterText.isEmpty() || parameterText.charAt(parameterText.length() - 1) != ',') {
                        parameterText2 = parameterText;
                    } else {
                        parameterText2 = parameterText.substring(i, parameterText.length() - 1);
                    }
                    String[] values2 = COMMA.split(parameterText2);
                    int[] array = new int[values2.length];
                    for (int i3 = 0; i3 < values2.length; i3++) {
                        try {
                            array[i3] = Integer.parseInt(values2[i3]);
                        } catch (NumberFormatException e) {
                            Log.w(TAG, "Skipping array of integers hint " + hintType + " due to invalid numeric value: '" + values2[i3] + '\'');
                            array = null;
                        }
                    }
                    if (array != null) {
                        hints.put(hintType, array);
                    }
                } else {
                    Log.w(TAG, "Unsupported hint type '" + hintType + "' of type " + hintType.getValueType());
                }
            }
            i2++;
            i = 0;
        }
        Log.i(TAG, "Hints from the URI: " + hints);
        return hints;
    }

    static Map<DecodeHintType, Object> parseDecodeHints(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null || extras.isEmpty()) {
            return null;
        }
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        DecodeHintType[] values = DecodeHintType.values();
        for (DecodeHintType hintType : values) {
            if (!(hintType == DecodeHintType.CHARACTER_SET || hintType == DecodeHintType.NEED_RESULT_POINT_CALLBACK || hintType == DecodeHintType.POSSIBLE_FORMATS)) {
                String hintName = hintType.name();
                if (extras.containsKey(hintName)) {
                    if (hintType.getValueType().equals(Void.class)) {
                        hints.put(hintType, Boolean.TRUE);
                    } else {
                        Object hintData = extras.get(hintName);
                        if (hintType.getValueType().isInstance(hintData)) {
                            hints.put(hintType, hintData);
                        } else {
                            Log.w(TAG, "Ignoring hint " + hintType + " because it is not assignable from " + hintData);
                        }
                    }
                }
            }
        }
        Log.i(TAG, "Hints from the Intent: " + hints);
        return hints;
    }
}
