package android.support.v4.media;

import android.media.MediaDescription;
import android.net.Uri;
import android.support.v4.media.MediaDescriptionCompatApi21;

/* access modifiers changed from: package-private */
public class MediaDescriptionCompatApi23 extends MediaDescriptionCompatApi21 {
    MediaDescriptionCompatApi23() {
    }

    public static Uri getMediaUri(Object descriptionObj) {
        return ((MediaDescription) descriptionObj).getMediaUri();
    }

    /* access modifiers changed from: package-private */
    public static class Builder extends MediaDescriptionCompatApi21.Builder {
        Builder() {
        }

        public static void setMediaUri(Object builderObj, Uri mediaUri) {
            ((MediaDescription.Builder) builderObj).setMediaUri(mediaUri);
        }
    }
}
