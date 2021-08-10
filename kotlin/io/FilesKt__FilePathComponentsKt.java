package kotlin.io;

import java.io.File;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u001a\u0011\u0010\u000b\u001a\u00020\f*\u00020\bH\u0002¢\u0006\u0002\b\r\u001a\u001c\u0010\u000e\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\fH\u0000\u001a\f\u0010\u0011\u001a\u00020\u0012*\u00020\u0002H\u0000\"\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0000\u0010\u0003\"\u0018\u0010\u0004\u001a\u00020\u0002*\u00020\u00028@X\u0004¢\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006\"\u0018\u0010\u0007\u001a\u00020\b*\u00020\u00028@X\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\n¨\u0006\u0013"}, d2 = {"isRooted", "", "Ljava/io/File;", "(Ljava/io/File;)Z", "root", "getRoot", "(Ljava/io/File;)Ljava/io/File;", "rootName", "", "getRootName", "(Ljava/io/File;)Ljava/lang/String;", "getRootLength", "", "getRootLength$FilesKt__FilePathComponentsKt", "subPath", "beginIndex", "endIndex", "toComponents", "Lkotlin/io/FilePathComponents;", "kotlin-stdlib"}, k = 5, mv = {1, 1, 15}, xi = 1, xs = "kotlin/io/FilesKt")
/* compiled from: FilePathComponents.kt */
class FilesKt__FilePathComponentsKt {
    /*  JADX ERROR: JadxRuntimeException in pass: InlineMethods
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to process method for inline
        	at jadx.core.dex.visitors.InlineMethods.processInvokeInsn(InlineMethods.java:68)
        	at jadx.core.dex.visitors.InlineMethods.visit(InlineMethods.java:43)
        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Class not yet loaded at codegen stage
        	at jadx.core.dex.nodes.ClassNode.reloadAtCodegenStage(ClassNode.java:589)
        	at jadx.core.dex.visitors.InlineMethods.processInvokeInsn(InlineMethods.java:60)
        	... 1 more
        */
    private static final int getRootLength$FilesKt__FilePathComponentsKt(java.lang.String r8) {
        /*
            r6 = r8
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            char r1 = java.io.File.separatorChar
            r2 = 0
            r3 = 0
            r4 = 4
            r5 = 0
            r0 = r6
            int r0 = kotlin.text.StringsKt.indexOf$default(r0, r1, r2, r3, r4, r5)
            r7 = 1
            if (r0 != 0) goto L_0x0042
            int r0 = r8.length()
            if (r0 <= r7) goto L_0x0041
            char r0 = r8.charAt(r7)
            char r1 = java.io.File.separatorChar
            if (r0 != r1) goto L_0x0041
            char r1 = java.io.File.separatorChar
            r2 = 2
            r3 = 0
            r4 = 4
            r5 = 0
            r0 = r6
            int r0 = kotlin.text.StringsKt.indexOf$default(r0, r1, r2, r3, r4, r5)
            if (r0 < 0) goto L_0x0041
            char r1 = java.io.File.separatorChar
            int r2 = r0 + 1
            r3 = 0
            r4 = 4
            r5 = 0
            r0 = r6
            int r0 = kotlin.text.StringsKt.indexOf$default(r0, r1, r2, r3, r4, r5)
            if (r0 < 0) goto L_0x003c
            int r0 = r0 + r7
            return r0
        L_0x003c:
            int r8 = r8.length()
            return r8
        L_0x0041:
            return r7
        L_0x0042:
            r1 = 58
            if (r0 <= 0) goto L_0x0050
            int r2 = r0 + -1
            char r2 = r8.charAt(r2)
            if (r2 != r1) goto L_0x0050
            int r0 = r0 + r7
            return r0
        L_0x0050:
            r2 = -1
            r3 = 0
            if (r0 != r2) goto L_0x0061
            r0 = 2
            r2 = 0
            boolean r0 = kotlin.text.StringsKt.endsWith$default(r6, r1, r3, r0, r2)
            if (r0 == 0) goto L_0x0061
            int r8 = r8.length()
            return r8
        L_0x0061:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FilePathComponentsKt.getRootLength$FilesKt__FilePathComponentsKt(java.lang.String):int");
    }

    public static final String getRootName(File file) {
        Intrinsics.checkParameterIsNotNull(file, "$this$rootName");
        String path = file.getPath();
        Intrinsics.checkExpressionValueIsNotNull(path, "path");
        String path2 = file.getPath();
        Intrinsics.checkExpressionValueIsNotNull(path2, "path");
        int rootLength$FilesKt__FilePathComponentsKt = getRootLength$FilesKt__FilePathComponentsKt(path2);
        if (path != null) {
            String substring = path.substring(0, rootLength$FilesKt__FilePathComponentsKt);
            Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
            return substring;
        }
        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
    }

    public static final File getRoot(File file) {
        Intrinsics.checkParameterIsNotNull(file, "$this$root");
        return new File(FilesKt.getRootName(file));
    }

    public static final boolean isRooted(File file) {
        Intrinsics.checkParameterIsNotNull(file, "$this$isRooted");
        String path = file.getPath();
        Intrinsics.checkExpressionValueIsNotNull(path, "path");
        return getRootLength$FilesKt__FilePathComponentsKt(path) > 0;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: InlineMethods
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to process method for inline
        	at jadx.core.dex.visitors.InlineMethods.processInvokeInsn(InlineMethods.java:68)
        	at jadx.core.dex.visitors.InlineMethods.visit(InlineMethods.java:43)
        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Class not yet loaded at codegen stage
        	at jadx.core.dex.nodes.ClassNode.reloadAtCodegenStage(ClassNode.java:589)
        	at jadx.core.dex.visitors.InlineMethods.processInvokeInsn(InlineMethods.java:60)
        	... 1 more
        */
    public static final kotlin.io.FilePathComponents toComponents(java.io.File r9) {
        /*
            java.lang.String r0 = "$this$toComponents"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r9, r0)
            java.lang.String r9 = r9.getPath()
            java.lang.String r0 = "path"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r9, r0)
            int r0 = getRootLength$FilesKt__FilePathComponentsKt(r9)
            r1 = 0
            java.lang.String r2 = r9.substring(r1, r0)
            java.lang.String r3 = "(this as java.lang.Strin…ing(startIndex, endIndex)"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r2, r3)
            java.lang.String r9 = r9.substring(r0)
            java.lang.String r0 = "(this as java.lang.String).substring(startIndex)"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r9, r0)
            r3 = r9
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            int r9 = r3.length()
            r0 = 1
            if (r9 != 0) goto L_0x0031
            r9 = 1
            goto L_0x0032
        L_0x0031:
            r9 = 0
        L_0x0032:
            if (r9 == 0) goto L_0x0039
            java.util.List r9 = kotlin.collections.CollectionsKt.emptyList()
            goto L_0x0072
        L_0x0039:
            char[] r4 = new char[r0]
            char r9 = java.io.File.separatorChar
            r4[r1] = r9
            r5 = 0
            r6 = 0
            r7 = 6
            r8 = 0
            java.util.List r9 = kotlin.text.StringsKt.split$default(r3, r4, r5, r6, r7, r8)
            java.lang.Iterable r9 = (java.lang.Iterable) r9
            java.util.ArrayList r0 = new java.util.ArrayList
            r1 = 10
            int r1 = kotlin.collections.CollectionsKt.collectionSizeOrDefault(r9, r1)
            r0.<init>(r1)
            java.util.Collection r0 = (java.util.Collection) r0
            java.util.Iterator r9 = r9.iterator()
        L_0x005a:
            boolean r1 = r9.hasNext()
            if (r1 == 0) goto L_0x006f
            java.lang.Object r1 = r9.next()
            java.lang.String r1 = (java.lang.String) r1
            java.io.File r3 = new java.io.File
            r3.<init>(r1)
            r0.add(r3)
            goto L_0x005a
        L_0x006f:
            r9 = r0
            java.util.List r9 = (java.util.List) r9
        L_0x0072:
            kotlin.io.FilePathComponents r0 = new kotlin.io.FilePathComponents
            java.io.File r1 = new java.io.File
            r1.<init>(r2)
            r0.<init>(r1, r9)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.io.FilesKt__FilePathComponentsKt.toComponents(java.io.File):kotlin.io.FilePathComponents");
    }

    public static final File subPath(File file, int i, int i2) {
        Intrinsics.checkParameterIsNotNull(file, "$this$subPath");
        return FilesKt.toComponents(file).subPath(i, i2);
    }
}
