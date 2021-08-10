package com.google.zxing.client.android.encode;

import com.google.zxing.client.android.Intents;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* access modifiers changed from: package-private */
public final class VCardContactEncoder extends ContactEncoder {
    private static final char TERMINATOR = '\n';

    VCardContactEncoder() {
    }

    @Override // com.google.zxing.client.android.encode.ContactEncoder
    public String[] encode(List<String> names, String organization, List<String> addresses, List<String> phones, List<String> phoneTypes, List<String> emails, List<String> urls, String note) {
        StringBuilder newContents = new StringBuilder(100);
        newContents.append("BEGIN:VCARD");
        newContents.append(TERMINATOR);
        newContents.append("VERSION:3.0");
        newContents.append(TERMINATOR);
        StringBuilder newDisplayContents = new StringBuilder(100);
        Formatter fieldFormatter = new VCardFieldFormatter();
        appendUpToUnique(newContents, newDisplayContents, "N", names, 1, null, fieldFormatter, TERMINATOR);
        append(newContents, newDisplayContents, "ORG", organization, fieldFormatter, TERMINATOR);
        appendUpToUnique(newContents, newDisplayContents, "ADR", addresses, 1, null, fieldFormatter, TERMINATOR);
        List<Map<String, Set<String>>> phoneMetadata = buildPhoneMetadata(phones, phoneTypes);
        appendUpToUnique(newContents, newDisplayContents, "TEL", phones, Integer.MAX_VALUE, new VCardTelDisplayFormatter(phoneMetadata), new VCardFieldFormatter(phoneMetadata), TERMINATOR);
        appendUpToUnique(newContents, newDisplayContents, "EMAIL", emails, Integer.MAX_VALUE, null, fieldFormatter, TERMINATOR);
        appendUpToUnique(newContents, newDisplayContents, "URL", urls, Integer.MAX_VALUE, null, fieldFormatter, TERMINATOR);
        append(newContents, newDisplayContents, "NOTE", note, fieldFormatter, TERMINATOR);
        newContents.append("END:VCARD");
        newContents.append(TERMINATOR);
        return new String[]{newContents.toString(), newDisplayContents.toString()};
    }

    private static List<Map<String, Set<String>>> buildPhoneMetadata(Collection<String> phones, List<String> phoneTypes) {
        if (phoneTypes == null || phoneTypes.isEmpty()) {
            return null;
        }
        List<Map<String, Set<String>>> metadataForIndex = new ArrayList<>();
        for (int i = 0; i < phones.size(); i++) {
            if (phoneTypes.size() <= i) {
                metadataForIndex.add(null);
            } else {
                Map<String, Set<String>> metadata = new HashMap<>();
                metadataForIndex.add(metadata);
                Set<String> typeTokens = new HashSet<>();
                metadata.put(Intents.WifiConnect.TYPE, typeTokens);
                String typeString = phoneTypes.get(i);
                Integer androidType = maybeIntValue(typeString);
                if (androidType == null) {
                    typeTokens.add(typeString);
                } else {
                    String purpose = vCardPurposeLabelForAndroidType(androidType.intValue());
                    String context = vCardContextLabelForAndroidType(androidType.intValue());
                    if (purpose != null) {
                        typeTokens.add(purpose);
                    }
                    if (context != null) {
                        typeTokens.add(context);
                    }
                }
            }
        }
        return metadataForIndex;
    }

    private static Integer maybeIntValue(String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String vCardPurposeLabelForAndroidType(int androidType) {
        if (androidType == 4 || androidType == 5) {
            return "fax";
        }
        if (androidType == 6) {
            return "pager";
        }
        if (androidType == 13) {
            return "fax";
        }
        if (androidType == 16) {
            return "textphone";
        }
        if (androidType == 18) {
            return "pager";
        }
        if (androidType != 20) {
            return null;
        }
        return "text";
    }

    private static String vCardContextLabelForAndroidType(int androidType) {
        if (androidType == 10 || androidType == 17 || androidType == 18) {
            return "work";
        }
        switch (androidType) {
            case 1:
            case 2:
            case 5:
            case 6:
                return "home";
            case 3:
            case 4:
                return "work";
            default:
                return null;
        }
    }
}
