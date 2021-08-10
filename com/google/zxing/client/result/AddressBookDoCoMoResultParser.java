package com.google.zxing.client.result;

import com.google.zxing.Result;

public final class AddressBookDoCoMoResultParser extends AbstractDoCoMoResultParser {
    @Override // com.google.zxing.client.result.ResultParser
    public AddressBookParsedResult parse(Result result) {
        String[] rawName;
        String birthday;
        String rawText = getMassagedText(result);
        if (!rawText.startsWith("MECARD:") || (rawName = matchDoCoMoPrefixedField("N:", rawText, true)) == null) {
            return null;
        }
        String name = parseName(rawName[0]);
        String pronunciation = matchSingleDoCoMoPrefixedField("SOUND:", rawText, true);
        String[] phoneNumbers = matchDoCoMoPrefixedField("TEL:", rawText, true);
        String[] emails = matchDoCoMoPrefixedField("EMAIL:", rawText, true);
        String note = matchSingleDoCoMoPrefixedField("NOTE:", rawText, false);
        String[] addresses = matchDoCoMoPrefixedField("ADR:", rawText, true);
        String birthday2 = matchSingleDoCoMoPrefixedField("BDAY:", rawText, true);
        if (!isStringOfDigits(birthday2, 8)) {
            birthday = null;
        } else {
            birthday = birthday2;
        }
        return new AddressBookParsedResult(maybeWrap(name), null, pronunciation, phoneNumbers, null, emails, null, null, note, addresses, null, matchSingleDoCoMoPrefixedField("ORG:", rawText, true), birthday, null, matchDoCoMoPrefixedField("URL:", rawText, true), null);
    }

    private static String parseName(String name) {
        int comma = name.indexOf(44);
        if (comma < 0) {
            return name;
        }
        return name.substring(comma + 1) + ' ' + name.substring(0, comma);
    }
}
