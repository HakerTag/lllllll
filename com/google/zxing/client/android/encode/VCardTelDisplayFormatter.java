package com.google.zxing.client.android.encode;

import android.telephony.PhoneNumberUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class VCardTelDisplayFormatter implements Formatter {
    private final List<Map<String, Set<String>>> metadataForIndex;

    VCardTelDisplayFormatter() {
        this(null);
    }

    VCardTelDisplayFormatter(List<Map<String, Set<String>>> metadataForIndex2) {
        this.metadataForIndex = metadataForIndex2;
    }

    @Override // com.google.zxing.client.android.encode.Formatter
    public CharSequence format(CharSequence value, int index) {
        CharSequence value2 = PhoneNumberUtils.formatNumber(value.toString());
        List<Map<String, Set<String>>> list = this.metadataForIndex;
        return formatMetadata(value2, (list == null || list.size() <= index) ? null : this.metadataForIndex.get(index));
    }

    private static CharSequence formatMetadata(CharSequence value, Map<String, Set<String>> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return value;
        }
        StringBuilder withMetadata = new StringBuilder();
        for (Map.Entry<String, Set<String>> metadatum : metadata.entrySet()) {
            Set<String> values = metadatum.getValue();
            if (values != null && !values.isEmpty()) {
                Iterator<String> valuesIt = values.iterator();
                withMetadata.append(valuesIt.next());
                while (valuesIt.hasNext()) {
                    withMetadata.append(',');
                    withMetadata.append(valuesIt.next());
                }
            }
        }
        if (withMetadata.length() > 0) {
            withMetadata.append(' ');
        }
        withMetadata.append(value);
        return withMetadata;
    }
}
