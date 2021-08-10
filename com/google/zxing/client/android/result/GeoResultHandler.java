package com.google.zxing.client.android.result;

import android.app.Activity;
import barcodescanner.xservices.nl.barcodescanner.R;
import com.google.zxing.client.result.GeoParsedResult;
import com.google.zxing.client.result.ParsedResult;

public final class GeoResultHandler extends ResultHandler {
    private static final int[] buttons = {R.string.button_show_map, R.string.button_get_directions};

    public GeoResultHandler(Activity activity, ParsedResult result) {
        super(activity, result);
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getButtonCount() {
        return buttons.length;
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getButtonText(int index) {
        return buttons[index];
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public void handleButtonPress(int index) {
        GeoParsedResult geoResult = (GeoParsedResult) getResult();
        if (index == 0) {
            openMap(geoResult.getGeoURI());
        } else if (index == 1) {
            getDirections(geoResult.getLatitude(), geoResult.getLongitude());
        }
    }

    @Override // com.google.zxing.client.android.result.ResultHandler
    public int getDisplayTitle() {
        return R.string.result_geo;
    }
}
