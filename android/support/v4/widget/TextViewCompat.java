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

        public void setCompoundDrawablesRelative(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
            textView.setCompoundDrawables(start, top, end, bottom);
        }

        public void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
            textView.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
        }

        public void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, int start, int top, int end, int bottom) {
            textView.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
        }

        private static Field retrieveField(String fieldName) {
            Field field = null;
            try {
                field = TextView.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                Log.e(LOG_TAG, "Could not retrieve " + fieldName + " field.");
                return field;
            }
        }

        private static int retrieveIntFromField(Field field, TextView textView) {
            try {
                return field.getInt(textView);
            } catch (IllegalAccessException e) {
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

        public void setTextAppearance(TextView textView, int resId) {
            textView.setTextAppearance(textView.getContext(), resId);
        }

        public Drawable[] getCompoundDrawablesRelative(TextView textView) {
            return textView.getCompoundDrawables();
        }

        public void setAutoSizeTextTypeWithDefaults(TextView textView, int autoSizeTextType) {
            if (textView instanceof AutoSizeableTextView) {
                ((AutoSizeableTextView) textView).setAutoSizeTextTypeWithDefaults(autoSizeTextType);
            }
        }

        public void setAutoSizeTextTypeUniformWithConfiguration(TextView textView, int autoSizeMinTextSize, int autoSizeMaxTextSize, int autoSizeStepGranularity, int unit) throws IllegalArgumentException {
            if (textView instanceof AutoSizeableTextView) {
                ((AutoSizeableTextView) textView).setAutoSizeTextTypeUniformWithConfiguration(autoSizeMinTextSize, autoSizeMaxTextSize, autoSizeStepGranularity, unit);
            }
        }

        public void setAutoSizeTextTypeUniformWithPresetSizes(TextView textView, int[] presetSizes, int unit) throws IllegalArgumentException {
            if (textView instanceof AutoSizeableTextView) {
                ((AutoSizeableTextView) textView).setAutoSizeTextTypeUniformWithPresetSizes(presetSizes, unit);
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
            if (textView instanceof AutoSizeableTextView) {
                return ((AutoSizeableTextView) textView).getAutoSizeTextAvailableSizes();
            }
            return new int[0];
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
        public void setCompoundDrawablesRelative(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
            boolean rtl = true;
            if (textView.getLayoutDirection() != 1) {
                rtl = false;
            }
            textView.setCompoundDrawables(rtl ? end : start, top, rtl ? start : end, bottom);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
            boolean rtl = true;
            if (textView.getLayoutDirection() != 1) {
                rtl = false;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(rtl ? end : start, top, rtl ? start : end, bottom);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, int start, int top, int end, int bottom) {
            boolean rtl = true;
            if (textView.getLayoutDirection() != 1) {
                rtl = false;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(rtl ? end : start, top, rtl ? start : end, bottom);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public Drawable[] getCompoundDrawablesRelative(TextView textView) {
            boolean rtl = true;
            if (textView.getLayoutDirection() != 1) {
                rtl = false;
            }
            Drawable[] compounds = textView.getCompoundDrawables();
            if (rtl) {
                Drawable start = compounds[2];
                Drawable end = compounds[0];
                compounds[0] = start;
                compounds[2] = end;
            }
            return compounds;
        }
    }

    static class TextViewCompatApi18Impl extends TextViewCompatApi17Impl {
        TextViewCompatApi18Impl() {
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl, android.support.v4.widget.TextViewCompat.TextViewCompatApi17Impl
        public void setCompoundDrawablesRelative(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
            textView.setCompoundDrawablesRelative(start, top, end, bottom);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl, android.support.v4.widget.TextViewCompat.TextViewCompatApi17Impl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl, android.support.v4.widget.TextViewCompat.TextViewCompatApi17Impl
        public void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, int start, int top, int end, int bottom) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
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
        public void setTextAppearance(TextView textView, int resId) {
            textView.setTextAppearance(resId);
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

                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        return callback.onCreateActionMode(mode, menu);
                    }

                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        recomputeProcessTextMenuItems(menu);
                        return callback.onPrepareActionMode(mode, menu);
                    }

                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return callback.onActionItemClicked(mode, item);
                    }

                    public void onDestroyActionMode(ActionMode mode) {
                        callback.onDestroyActionMode(mode);
                    }

                    private void recomputeProcessTextMenuItems(Menu menu) {
                        Method removeItemAtMethod;
                        Context context = textView.getContext();
                        PackageManager packageManager = context.getPackageManager();
                        if (!this.mInitializedMenuBuilderReferences) {
                            this.mInitializedMenuBuilderReferences = true;
                            try {
                                Class<?> cls = Class.forName("com.android.internal.view.menu.MenuBuilder");
                                this.mMenuBuilderClass = cls;
                                this.mMenuBuilderRemoveItemAtMethod = cls.getDeclaredMethod("removeItemAt", Integer.TYPE);
                                this.mCanUseMenuBuilderReferences = true;
                            } catch (ClassNotFoundException | NoSuchMethodException e) {
                                this.mMenuBuilderClass = null;
                                this.mMenuBuilderRemoveItemAtMethod = null;
                                this.mCanUseMenuBuilderReferences = false;
                            }
                        }
                        try {
                            if (!this.mCanUseMenuBuilderReferences || !this.mMenuBuilderClass.isInstance(menu)) {
                                removeItemAtMethod = menu.getClass().getDeclaredMethod("removeItemAt", Integer.TYPE);
                            } else {
                                removeItemAtMethod = this.mMenuBuilderRemoveItemAtMethod;
                            }
                            for (int i = menu.size() - 1; i >= 0; i--) {
                                MenuItem item = menu.getItem(i);
                                if (item.getIntent() != null && "android.intent.action.PROCESS_TEXT".equals(item.getIntent().getAction())) {
                                    removeItemAtMethod.invoke(menu, Integer.valueOf(i));
                                }
                            }
                            List<ResolveInfo> supportedActivities = getSupportedActivities(context, packageManager);
                            for (int i2 = 0; i2 < supportedActivities.size(); i2++) {
                                ResolveInfo info = supportedActivities.get(i2);
                                menu.add(0, 0, i2 + MENU_ITEM_ORDER_PROCESS_TEXT_INTENT_ACTIONS_START, info.loadLabel(packageManager)).setIntent(createProcessTextIntentForResolveInfo(info, textView)).setShowAsAction(1);
                            }
                        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e2) {
                        }
                    }

                    private List<ResolveInfo> getSupportedActivities(Context context, PackageManager packageManager) {
                        List<ResolveInfo> supportedActivities = new ArrayList<>();
                        if (!(context instanceof Activity)) {
                            return supportedActivities;
                        }
                        for (ResolveInfo info : packageManager.queryIntentActivities(createProcessTextIntent(), 0)) {
                            if (isSupportedActivity(info, context)) {
                                supportedActivities.add(info);
                            }
                        }
                        return supportedActivities;
                    }

                    private boolean isSupportedActivity(ResolveInfo info, Context context) {
                        if (context.getPackageName().equals(info.activityInfo.packageName)) {
                            return true;
                        }
                        if (!info.activityInfo.exported) {
                            return false;
                        }
                        if (info.activityInfo.permission == null || context.checkSelfPermission(info.activityInfo.permission) == 0) {
                            return true;
                        }
                        return false;
                    }

                    private Intent createProcessTextIntentForResolveInfo(ResolveInfo info, TextView textView) {
                        return createProcessTextIntent().putExtra("android.intent.extra.PROCESS_TEXT_READONLY", !isEditable(textView)).setClassName(info.activityInfo.packageName, info.activityInfo.name);
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
        public void setAutoSizeTextTypeWithDefaults(TextView textView, int autoSizeTextType) {
            textView.setAutoSizeTextTypeWithDefaults(autoSizeTextType);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public void setAutoSizeTextTypeUniformWithConfiguration(TextView textView, int autoSizeMinTextSize, int autoSizeMaxTextSize, int autoSizeStepGranularity, int unit) throws IllegalArgumentException {
            textView.setAutoSizeTextTypeUniformWithConfiguration(autoSizeMinTextSize, autoSizeMaxTextSize, autoSizeStepGranularity, unit);
        }

        @Override // android.support.v4.widget.TextViewCompat.TextViewCompatBaseImpl
        public void setAutoSizeTextTypeUniformWithPresetSizes(TextView textView, int[] presetSizes, int unit) throws IllegalArgumentException {
            textView.setAutoSizeTextTypeUniformWithPresetSizes(presetSizes, unit);
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

    public static void setCompoundDrawablesRelative(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
        IMPL.setCompoundDrawablesRelative(textView, start, top, end, bottom);
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
        IMPL.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, start, top, end, bottom);
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, int start, int top, int end, int bottom) {
        IMPL.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, start, top, end, bottom);
    }

    public static int getMaxLines(TextView textView) {
        return IMPL.getMaxLines(textView);
    }

    public static int getMinLines(TextView textView) {
        return IMPL.getMinLines(textView);
    }

    public static void setTextAppearance(TextView textView, int resId) {
        IMPL.setTextAppearance(textView, resId);
    }

    public static Drawable[] getCompoundDrawablesRelative(TextView textView) {
        return IMPL.getCompoundDrawablesRelative(textView);
    }

    public static void setAutoSizeTextTypeWithDefaults(TextView textView, int autoSizeTextType) {
        IMPL.setAutoSizeTextTypeWithDefaults(textView, autoSizeTextType);
    }

    public static void setAutoSizeTextTypeUniformWithConfiguration(TextView textView, int autoSizeMinTextSize, int autoSizeMaxTextSize, int autoSizeStepGranularity, int unit) throws IllegalArgumentException {
        IMPL.setAutoSizeTextTypeUniformWithConfiguration(textView, autoSizeMinTextSize, autoSizeMaxTextSize, autoSizeStepGranularity, unit);
    }

    public static void setAutoSizeTextTypeUniformWithPresetSizes(TextView textView, int[] presetSizes, int unit) throws IllegalArgumentException {
        IMPL.setAutoSizeTextTypeUniformWithPresetSizes(textView, presetSizes, unit);
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
