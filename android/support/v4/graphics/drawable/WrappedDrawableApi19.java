package android.support.v4.graphics.drawable;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.WrappedDrawableApi14;

class WrappedDrawableApi19 extends WrappedDrawableApi14 {
    WrappedDrawableApi19(Drawable drawable) {
        super(drawable);
    }

    WrappedDrawableApi19(WrappedDrawableApi14.DrawableWrapperState state, Resources resources) {
        super(state, resources);
    }

    public void setAutoMirrored(boolean mirrored) {
        this.mDrawable.setAutoMirrored(mirrored);
    }

    public boolean isAutoMirrored() {
        return this.mDrawable.isAutoMirrored();
    }

    /* access modifiers changed from: package-private */
    @Override // android.support.v4.graphics.drawable.WrappedDrawableApi14
    public WrappedDrawableApi14.DrawableWrapperState mutateConstantState() {
        return new DrawableWrapperStateKitKat(this.mState, null);
    }

    private static class DrawableWrapperStateKitKat extends WrappedDrawableApi14.DrawableWrapperState {
        DrawableWrapperStateKitKat(WrappedDrawableApi14.DrawableWrapperState orig, Resources res) {
            super(orig, res);
        }

        @Override // android.support.v4.graphics.drawable.WrappedDrawableApi14.DrawableWrapperState
        public Drawable newDrawable(Resources res) {
            return new WrappedDrawableApi19(this, res);
        }
    }
}
