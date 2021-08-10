package com.google.zxing.client.android.encode;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

final class VCardFieldFormatter implements Formatter {
    private static final Pattern NEWLINE = Pattern.compile("\\n");
    private static final Pattern RESERVED_VCARD_CHARS = Pattern.compile("([\\\\,;])");
    private final List<Map<String, Set<String>>> metadataForIndex;

    VCardFieldFormatter() {
        this(null);
    }

    VCardFieldFormatter(List<Map<String, Set<String>>> metadataForIndex2) {
        this.metadataForIndex = metadataForIndex2;
    }

    @Override // com.google.zxing.client.android.encode.Formatter
    public CharSequence format(CharSequence value, int index) {
        CharSequence value2 = NEWLINE.matcher(RESERVED_VCARD_CHARS.matcher(value).replaceAll("\\\\$1")).replaceAll("");
        List<Map<String, Set<String>>> list = this.metadataForIndex;
        return formatMetadata(value2, (list == null || list.size() <= index) ? null : this.metadataForIndex.get(index));
    }

    private static CharSequence formatMetadata(CharSequence value, Map<String, Set<String>> metadata) {
        StringBuilder withMetadata = new StringBuilder();
        if (metadata != null) {
            for (Map.Entry<String, Set<String>> metadatum : metadata.entrySet()) {
                Set<String> values = metadatum.getValue();
                if (values != null && !values.isEmpty()) {
                    withMetadata.append(';');
                    withMetadata.append(metadatum.getKey());
                    withMetadata.append('=');
                    if (values.size() > 1) {
                        withMetadata.append('\"');
                    }
                    Iterator<String> valuesIt = values.iterator();
                    withMetadata.append(valuesIt.next());
                    while (valuesIt.hasNext()) {
                        withMetadata.append(',');
                        withMetadata.append(valuesIt.next());
                    }
                    if (values.size() > 1) {
                        withMetadata.append('\"');
                    }
                }
            }
        }
        withMetadata.append(':');
        withMetadata.append(value);
        return withMetadata;
    }
}
