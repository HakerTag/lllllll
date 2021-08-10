package android.support.v4.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.os.BuildCompat;
import android.text.Editable;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class TextViewCompat {
    public static final int AUTO_SIZE_TEXT_TYPE_NONE = 0;
    public static final int AUTO_SIZE_TEXT_TYPE_UNIFORM = 1;
    static final TextViewCompatBaseImpl IMPL;

    @Retention(RetentionPolicy.SOURCE)
    public @interface AutoSizeTextType {
    }

    private TextViewCompat() {
    }

    /* access modifiers changed from: package-private */
    public static class TextViewCompatBaseImpl {
        private static final int LINES = 1;
        private static final String LOG_TAG = "TextViewCompatBase";
        private static Field sMaxModeField;
        private static boolean sMaxModeFieldFetched;
        private static Field sMaximumField;
        private static boolean sMaximumFieldFetched;
        private static Field sMinModeField;
        private static boolean sMinModeFieldFetched;
        private static Field sMinimumField;
        private static boolean sMinimumFieldFetched;

        TextViewCompatBaseImpl() {
        }

        public void setCompoundDrawablesRelative(TextView textView, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
            textView.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
        }

        public void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        }

        public void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, int i, int i2, int i3, int i4) {
            textView.setCompoundDrawablesWithIntrinsicBounds(i, i2, i3, i4);
        }

        private static Field retrieveField(String str) {
            Field field = null;
            try {
                field = TextView.class.getDeclaredField(str);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException unused) {
                Log.e(LOG_TAG, "Could not retrieve " + str + " field.");
                return field;
            }
        }

        private static int retrieveIntFromField(Field field, TextView textView) {
            try {
                return field.getInt(textView);
            } catch (IllegalAccessException unused) {
                Log.d(LOG_TAG, "Could not retrieve value of " + field.getName() + " field.");
                return -1;
            }
        }

        public int getMaxLines(TextView textView) {
            if (!sMaxModeFieldFetched) {
                sMaxModeField = retrieveField("mMaxMode");
                sMaxModeFieldFetched = true;
            }
            Field field = sMaxModeField;
            if (field == null || retrieveIntFromField(field, textView) != 1) {
                return -1;
            }
            if (!sMaximumFieldFetched) {
                sMaximumField = retrieveField("mMaximum");
                sMaximumFieldFetched = true;
            }
            Field field2 = sMaximumField;
            if (field2 != null) {
                return retrieveIntFromField(field2, textView);
            }
            return -1;
        }

        public int getMinLines(TextView textView) {
            if (!sMinModeFieldFetched) {
                sMinModeField = retrieveField("mMinMode");
                sMinModeFieldFetched = true;
            }
            Field field = sMinModeField;
            if (field == null || retrieveIntFromField(field, textView) != 1) {
                return -1;
            }
            if (!sMinimumFieldFetched) {
                sMinimumField = retrieveField("mMinimum");
                sMinimumFieldFetched = true;
            }
            Field field2 = sMinimumField;
            if (field2 != null) {
                return retrieveIntFromField(field2, textView);
            }
            return -1;
        }

        public void setTextAppearance(TextView textView, int i) {
            textView.setTextAppearance(textView.getContext(), i);
        }

        public Drawable[] getCompoundDrawablesRelative(TextView textView) {
            return textView.getCompoundDrawables();
        }

        public void setAutoSizeTextTypeWithDefaults(TextView textView, int i) {
            if (textView instanceof AutoSizeableTextView) {
                ((AutoSizeableTextView) textView).setAutoSizeTextTypeWithDefaults(i);
            }
        }

        public void setAutoSizeTextTypeUniformWithConfiguration(TextView textView, int i, int i2, int i3, int i4) throws IllegalArgumentException {
            if (textView instanceof AutoSizeableTextView) {
                ((AutoSizeableTextView) textView).setAutoSizeTextTypeUniformWithConfiguration(i, i2, i3, i4);
            }
        }

        public void setAutoSizeTextTypeUniformWithPresetSizes(TextView textView, int[] iArr, int i) throws IllegalArgumentException {
            if (textView instanceof AutoSizeableTextView) {
                ((AutoSizeableTextView) textView).setAutoSizeTextTypeUniformWithPresetSizes(iArr, i);
            }
        }

        public int getAutoSizeTextType(TextView textView) {
            if (textView instanceof AutoSizeableTextView) {
                return ((AutoSizeableTextView) textView).getAutoSizeTextType();
            }
            return 0;
        }

        public int getAutoSizeStepGranularity(TextView textView) {
            if (textView instanceof AutoSizeableTextView) {
                return ((AutoSizeableTextView) textView).getAutoSizeStepGranularity();
            }
            return -1;
        }

        public int getAutoSizeMinTextSize(TextView textView) {
            if (textView instanceof AutoSizeableTextView) {
                return ((AutoSizeableTextView) textView).getAutoSizeMinTextSize();
            }
            return -1;
        }

        public int getAutoSizeMaxTextSize(TextView textView) {
            if (textView instanceof AutoSizeableTextView) {
                return ((AutoSizeableTextView) textView).getAutoSizeMaxTextSize();
            }
            return -1;
        }

        public int[] getAutoSizeTextAvailableSizes(TextView textView) {
            return textView instanceof AutoSizeableTextView ? ((AutoSizeableTextView) textView).getAutoSizeTextAvailableSizes() : new int[0];
        }

        public void setCustomSelectionActionModeCallback(TextView textView, ActionMode.Callback callback) {
            textView.setCustomSelectionActionModeCallback(callback);
        }
    }

    static class TextViewCompatApi16Impl extends TextViewCompatBaseImpl {
        TextViewCompatApi16Impl() {
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public int getMaxLines(TextView textView) {
            return textView.getMaxLines();
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public int getMinLines(TextView textView) {
            return textView.getMinLines();
        }
    }

    static class TextViewCompatApi17Impl extends TextViewCompatApi16Impl {
        TextViewCompatApi17Impl() {
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public void setCompoundDrawablesRelative(TextView textView, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
            boolean z = true;
            if (textView.getLayoutDirection() != 1) {
                z = false;
            }
            Drawable drawable5 = z ? drawable3 : drawable;
            if (!z) {
                drawable = drawable3;
            }
            textView.setCompoundDrawables(drawable5, drawable2, drawable, drawable4);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
            boolean z = true;
            if (textView.getLayoutDirection() != 1) {
                z = false;
            }
            Drawable drawable5 = z ? drawable3 : drawable;
            if (!z) {
                drawable = drawable3;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable5, drawable2, drawable, drawable4);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, int i, int i2, int i3, int i4) {
            boolean z = true;
            if (textView.getLayoutDirection() != 1) {
                z = false;
            }
            int i5 = z ? i3 : i;
            if (!z) {
                i = i3;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(i5, i2, i, i4);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public Drawable[] getCompoundDrawablesRelative(TextView textView) {
            boolean z = true;
            if (textView.getLayoutDirection() != 1) {
                z = false;
            }
            Drawable[] compoundDrawables = textView.getCompoundDrawables();
            if (z) {
                Drawable drawable = compoundDrawables[2];
                Drawable drawable2 = compoundDrawables[0];
                compoundDrawables[0] = drawable;
                compoundDrawables[2] = drawable2;
            }
            return compoundDrawables;
        }
    }

    static class TextViewCompatApi18Impl extends TextViewCompatApi17Impl {
        TextViewCompatApi18Impl() {
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl, android.support.v4.widget.TextViewCompat.TextViewCompatApi17Impl
        public void setCompoundDrawablesRelative(TextView textView, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
            textView.setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl, android.support.v4.widget.TextViewCompat.TextViewCompatApi17Impl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl, android.support.v4.widget.TextViewCompat.TextViewCompatApi17Impl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, int i, int i2, int i3, int i4) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(i, i2, i3, i4);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl, android.support.v4.widget.TextViewCompat.TextViewCompatApi17Impl
        public Drawable[] getCompoundDrawablesRelative(TextView textView) {
            return textView.getCompoundDrawablesRelative();
        }
    }

    static class TextViewCompatApi23Impl extends TextViewCompatApi18Impl {
        TextViewCompatApi23Impl() {
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public void setTextAppearance(TextView textView, int i) {
            textView.setTextAppearance(i);
        }
    }

    static class TextViewCompatApi26Impl extends TextViewCompatApi23Impl {
        TextViewCompatApi26Impl() {
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public void setCustomSelectionActionModeCallback(final TextView textView, final ActionMode.Callback callback) {
            if (Build.VERSION.SDK_INT == 26 || Build.VERSION.SDK_INT == 27) {
                textView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    /* class android.support.v4.widget.TextViewCompat.TextViewCompatApi26Impl.AnonymousClass1 */
                    private static final int MENU_ITEM_ORDER_PROCESS_TEXT_INTENT_ACTIONS_START = 100;
                    private boolean mCanUseMenuBuilderReferences;
                    private boolean mInitializedMenuBuilderReferences = false;
                    private Class mMenuBuilderClass;
                    private Method mMenuBuilderRemoveItemAtMethod;

                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        return callback.onCreateActionMode(actionMode, menu);
                    }

                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        recomputeProcessTextMenuItems(menu);
                        return callback.onPrepareActionMode(actionMode, menu);
                    }

                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        return callback.onActionItemClicked(actionMode, menuItem);
                    }

                    public void onDestroyActionMode(ActionMode actionMode) {
                        callback.onDestroyActionMode(actionMode);
                    }

                    private void recomputeProcessTextMenuItems(Menu menu) {
                        Method method;
                        Context context = textView.getContext();
                        PackageManager packageManager = context.getPackageManager();
                        if (!this.mInitializedMenuBuilderReferences) {
                            this.mInitializedMenuBuilderReferences = true;
                            try {
                                Class<?> cls = Class.forName("com.android.internal.view.menu.MenuBuilder");
                                this.mMenuBuilderClass = cls;
                                this.mMenuBuilderRemoveItemAtMethod = cls.getDeclaredMethod("removeItemAt", Integer.TYPE);
                                this.mCanUseMenuBuilderReferences = true;
                            } catch (ClassNotFoundException | NoSuchMethodException unused) {
                                this.mMenuBuilderClass = null;
                                this.mMenuBuilderRemoveItemAtMethod = null;
                                this.mCanUseMenuBuilderReferences = false;
                            }
                        }
                        try {
                            if (!this.mCanUseMenuBuilderReferences || !this.mMenuBuilderClass.isInstance(menu)) {
                                method = menu.getClass().getDeclaredMethod("removeItemAt", Integer.TYPE);
                            } else {
                                method = this.mMenuBuilderRemoveItemAtMethod;
                            }
                            for (int size = menu.size() - 1; size >= 0; size--) {
                                MenuItem item = menu.getItem(size);
                                if (item.getIntent() != null && "android.intent.action.PROCESS_TEXT".equals(item.getIntent().getAction())) {
                                    method.invoke(menu, Integer.valueOf(size));
                                }
                            }
                            List<ResolveInfo> supportedActivities = getSupportedActivities(context, packageManager);
                            for (int i = 0; i < supportedActivities.size(); i++) {
                                ResolveInfo resolveInfo = supportedActivities.get(i);
                                menu.add(0, 0, i + MENU_ITEM_ORDER_PROCESS_TEXT_INTENT_ACTIONS_START, resolveInfo.loadLabel(packageManager)).setIntent(createProcessTextIntentForResolveInfo(resolveInfo, textView)).setShowAsAction(1);
                            }
                        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused2) {
                        }
                    }

                    private List<ResolveInfo> getSupportedActivities(Context context, PackageManager packageManager) {
                        ArrayList arrayList = new ArrayList();
                        if (!(context instanceof Activity)) {
                            return arrayList;
                        }
                        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(createProcessTextIntent(), 0)) {
                            if (isSupportedActivity(resolveInfo, context)) {
                                arrayList.add(resolveInfo);
                            }
                        }
                        return arrayList;
                    }

                    private boolean isSupportedActivity(ResolveInfo resolveInfo, Context context) {
                        if (context.getPackageName().equals(resolveInfo.activityInfo.packageName)) {
                            return true;
                        }
                        if (!resolveInfo.activityInfo.exported) {
                            return false;
                        }
                        if (resolveInfo.activityInfo.permission == null || context.checkSelfPermission(resolveInfo.activityInfo.permission) == 0) {
                            return true;
                        }
                        return false;
                    }

                    private Intent createProcessTextIntentForResolveInfo(ResolveInfo resolveInfo, TextView textView) {
                        return createProcessTextIntent().putExtra("android.intent.extra.PROCESS_TEXT_READONLY", !isEditable(textView)).setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                    }

                    private boolean isEditable(TextView textView) {
                        return (textView instanceof Editable) && textView.onCheckIsTextEditor() && textView.isEnabled();
                    }

                    private Intent createProcessTextIntent() {
                        return new Intent().setAction("android.intent.action.PROCESS_TEXT").setType("text/plain");
                    }
                });
            } else {
                super.setCustomSelectionActionModeCallback(textView, callback);
            }
        }
    }

    static class TextViewCompatApi27Impl extends TextViewCompatApi26Impl {
        TextViewCompatApi27Impl() {
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public void setAutoSizeTextTypeWithDefaults(TextView textView, int i) {
            textView.setAutoSizeTextTypeWithDefaults(i);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public void setAutoSizeTextTypeUniformWithConfiguration(TextView textView, int i, int i2, int i3, int i4) throws IllegalArgumentException {
            textView.setAutoSizeTextTypeUniformWithConfiguration(i, i2, i3, i4);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public void setAutoSizeTextTypeUniformWithPresetSizes(TextView textView, int[] iArr, int i) throws IllegalArgumentException {
            textView.setAutoSizeTextTypeUniformWithPresetSizes(iArr, i);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public int getAutoSizeTextType(TextView textView) {
            return textView.getAutoSizeTextType();
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public int getAutoSizeStepGranularity(TextView textView) {
            return textView.getAutoSizeStepGranularity();
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public int getAutoSizeMinTextSize(TextView textView) {
            return textView.getAutoSizeMinTextSize();
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public int getAutoSizeMaxTextSize(TextView textView) {
            return textView.getAutoSizeMaxTextSize();
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public int[] getAutoSizeTextAvailableSizes(TextView textView) {
            return textView.getAutoSizeTextAvailableSizes();
        }
    }

    static {
        if (BuildCompat.isAtLeastOMR1()) {
            IMPL = new TextViewCompatApi27Impl();
        } else if (Build.VERSION.SDK_INT >= 26) {
            IMPL = new TextViewCompatApi26Impl();
        } else if (Build.VERSION.SDK_INT >= 23) {
            IMPL = new TextViewCompatApi23Impl();
        } else if (Build.VERSION.SDK_INT >= 18) {
            IMPL = new TextViewCompatApi18Impl();
        } else if (Build.VERSION.SDK_INT >= 17) {
            IMPL = new TextViewCompatApi17Impl();
        } else if (Build.VERSION.SDK_INT >= 16) {
            IMPL = new TextViewCompatApi16Impl();
        } else {
            IMPL = new TextViewCompatBaseImpl();
        }
    }

    public static void setCompoundDrawablesRelative(TextView textView, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        IMPL.setCompoundDrawablesRelative(textView, drawable, drawable2, drawable3, drawable4);
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        IMPL.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, drawable, drawable2, drawable3, drawable4);
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, int i, int i2, int i3, int i4) {
        IMPL.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, i, i2, i3, i4);
    }

    public static int getMaxLines(TextView textView) {
        return IMPL.getMaxLines(textView);
    }

    public static int getMinLines(TextView textView) {
        return IMPL.getMinLines(textView);
    }

    public static void setTextAppearance(TextView textView, int i) {
        IMPL.setTextAppearance(textView, i);
    }

    public static Drawable[] getCompoundDrawablesRelative(TextView textView) {
        return IMPL.getCompoundDrawablesRelative(textView);
    }

    public static void setAutoSizeTextTypeWithDefaults(TextView textView, int i) {
        IMPL.setAutoSizeTextTypeWithDefaults(textView, i);
    }

    public static void setAutoSizeTextTypeUniformWithConfiguration(TextView textView, int i, int i2, int i3, int i4) throws IllegalArgumentException {
        IMPL.setAutoSizeTextTypeUniformWithConfiguration(textView, i, i2, i3, i4);
    }

    public static void setAutoSizeTextTypeUniformWithPresetSizes(TextView textView, int[] iArr, int i) throws IllegalArgumentException {
        IMPL.setAutoSizeTextTypeUniformWithPresetSizes(textView, iArr, i);
    }

    public static int getAutoSizeTextType(TextView textView) {
        return IMPL.getAutoSizeTextType(textView);
    }

    public static int getAutoSizeStepGranularity(TextView textView) {
        return IMPL.getAutoSizeStepGranularity(textView);
    }

    public static int getAutoSizeMinTextSize(TextView textView) {
        return IMPL.getAutoSizeMinTextSize(textView);
    }

    public static int getAutoSizeMaxTextSize(TextView textView) {
        return IMPL.getAutoSizeMaxTextSize(textView);
    }

    public static int[] getAutoSizeTextAvailableSizes(TextView textView) {
        return IMPL.getAutoSizeTextAvailableSizes(textView);
    }

    public static void setCustomSelectionActionModeCallback(TextView textView, ActionMode.Callback callback) {
        IMPL.setCustomSelectionActionModeCallback(textView, callback);
    }
}
