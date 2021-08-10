package kotlin.text;

import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.internal.PlatformImplementationsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequencesKt;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u000b\u001a!\u0010\u0000\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0002H\u0002¢\u0006\u0002\b\u0004\u001a\u0011\u0010\u0005\u001a\u00020\u0006*\u00020\u0002H\u0002¢\u0006\u0002\b\u0007\u001a\u0014\u0010\b\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u001aJ\u0010\t\u001a\u00020\u0002*\b\u0012\u0004\u0012\u00020\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00062\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u00012\u0014\u0010\r\u001a\u0010\u0012\u0004\u0012\u00020\u0002\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u0001H\b¢\u0006\u0002\b\u000e\u001a\u0014\u0010\u000f\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0010\u001a\u00020\u0002\u001a\u001e\u0010\u0011\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0010\u001a\u00020\u00022\b\b\u0002\u0010\u0012\u001a\u00020\u0002\u001a\n\u0010\u0013\u001a\u00020\u0002*\u00020\u0002\u001a\u0014\u0010\u0014\u001a\u00020\u0002*\u00020\u00022\b\b\u0002\u0010\u0012\u001a\u00020\u0002¨\u0006\u0015"}, d2 = {"getIndentFunction", "Lkotlin/Function1;", "", "indent", "getIndentFunction$StringsKt__IndentKt", "indentWidth", "", "indentWidth$StringsKt__IndentKt", "prependIndent", "reindent", "", "resultSizeEstimate", "indentAddFunction", "indentCutFunction", "reindent$StringsKt__IndentKt", "replaceIndent", "newIndent", "replaceIndentByMargin", "marginPrefix", "trimIndent", "trimMargin", "kotlin-stdlib"}, k = 5, mv = {1, 1, 15}, xi = 1, xs = "kotlin/text/StringsKt")
/* compiled from: Indent.kt */
class StringsKt__IndentKt {
    public static /* synthetic */ String trimMargin$default(String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "|";
        }
        return StringsKt.trimMargin(str, str2);
    }

    public static final String trimMargin(String str, String str2) {
        Intrinsics.checkParameterIsNotNull(str, "$this$trimMargin");
        Intrinsics.checkParameterIsNotNull(str2, "marginPrefix");
        return StringsKt.replaceIndentByMargin(str, "", str2);
    }

    public static /* synthetic */ String replaceIndentByMargin$default(String str, String str2, String str3, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "";
        }
        if ((i & 2) != 0) {
            str3 = "|";
        }
        return StringsKt.replaceIndentByMargin(str, str2, str3);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v16, types: [java.lang.String] */
    /*  JADX ERROR: JadxRuntimeException in pass: InlineMethods
        jadx.core.utils.exceptions.JadxRuntimeException: Failed to process method for inline
        	at jadx.core.dex.visitors.InlineMethods.processInvokeInsn(InlineMethods.java:68)
        	at jadx.core.dex.visitors.InlineMethods.visit(InlineMethods.java:43)
        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Class not yet loaded at codegen stage
        	at jadx.core.dex.nodes.ClassNode.reloadAtCodegenStage(ClassNode.java:589)
        	at jadx.core.dex.visitors.InlineMethods.processInvokeInsn(InlineMethods.java:60)
        	... 1 more
        */
    public static final java.lang.String replaceIndentByMargin(java.lang.String r21, java.lang.String r22, java.lang.String r23) {
        /*
            r0 = r21
            r6 = r23
            java.lang.String r1 = "$this$replaceIndentByMargin"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r0, r1)
            java.lang.String r1 = "newIndent"
            r2 = r22
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r2, r1)
            java.lang.String r1 = "marginPrefix"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r6, r1)
            r1 = r6
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            boolean r1 = kotlin.text.StringsKt.isBlank(r1)
            r1 = r1 ^ 1
            if (r1 == 0) goto L_0x0100
            r1 = r0
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            java.util.List r1 = kotlin.text.StringsKt.lines(r1)
            int r0 = r21.length()
            int r3 = r22.length()
            int r4 = r1.size()
            int r3 = r3 * r4
            int r7 = r0 + r3
            kotlin.jvm.functions.Function1 r8 = getIndentFunction$StringsKt__IndentKt(r22)
            int r9 = kotlin.collections.CollectionsKt.getLastIndex(r1)
            java.lang.Iterable r1 = (java.lang.Iterable) r1
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r10 = r0
            java.util.Collection r10 = (java.util.Collection) r10
            java.util.Iterator r11 = r1.iterator()
            r12 = 0
            r0 = 0
        L_0x004f:
            boolean r1 = r11.hasNext()
            if (r1 == 0) goto L_0x00d2
            java.lang.Object r1 = r11.next()
            int r13 = r0 + 1
            if (r0 >= 0) goto L_0x0060
            kotlin.collections.CollectionsKt.throwIndexOverflow()
        L_0x0060:
            r14 = r1
            java.lang.String r14 = (java.lang.String) r14
            r15 = 0
            if (r0 == 0) goto L_0x0068
            if (r0 != r9) goto L_0x0073
        L_0x0068:
            r0 = r14
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            boolean r0 = kotlin.text.StringsKt.isBlank(r0)
            if (r0 == 0) goto L_0x0073
            r14 = r15
            goto L_0x00ca
        L_0x0073:
            r0 = r14
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            int r1 = r0.length()
            r2 = 0
        L_0x007b:
            r3 = -1
            if (r2 >= r1) goto L_0x008f
            char r4 = r0.charAt(r2)
            boolean r4 = kotlin.text.CharsKt.isWhitespace(r4)
            r4 = r4 ^ 1
            if (r4 == 0) goto L_0x008c
            r5 = r2
            goto L_0x0090
        L_0x008c:
            int r2 = r2 + 1
            goto L_0x007b
        L_0x008f:
            r5 = -1
        L_0x0090:
            if (r5 != r3) goto L_0x0093
            goto L_0x00bf
        L_0x0093:
            r3 = 0
            r4 = 4
            r16 = 0
            r0 = r14
            r1 = r23
            r2 = r5
            r17 = r5
            r5 = r16
            boolean r0 = kotlin.text.StringsKt.startsWith$default(r0, r1, r2, r3, r4, r5)
            if (r0 == 0) goto L_0x00bf
            int r0 = r23.length()
            int r5 = r17 + r0
            if (r14 == 0) goto L_0x00b7
            java.lang.String r15 = r14.substring(r5)
            java.lang.String r0 = "(this as java.lang.String).substring(startIndex)"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r15, r0)
            goto L_0x00bf
        L_0x00b7:
            kotlin.TypeCastException r0 = new kotlin.TypeCastException
            java.lang.String r1 = "null cannot be cast to non-null type java.lang.String"
            r0.<init>(r1)
            throw r0
        L_0x00bf:
            if (r15 == 0) goto L_0x00ca
            java.lang.Object r0 = r8.invoke(r15)
            java.lang.String r0 = (java.lang.String) r0
            if (r0 == 0) goto L_0x00ca
            r14 = r0
        L_0x00ca:
            if (r14 == 0) goto L_0x00cf
            r10.add(r14)
        L_0x00cf:
            r0 = r13
            goto L_0x004f
        L_0x00d2:
            java.util.List r10 = (java.util.List) r10
            r11 = r10
            java.lang.Iterable r11 = (java.lang.Iterable) r11
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>(r7)
            r12 = r0
            java.lang.Appendable r12 = (java.lang.Appendable) r12
            java.lang.String r0 = "\n"
            r13 = r0
            java.lang.CharSequence r13 = (java.lang.CharSequence) r13
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 124(0x7c, float:1.74E-43)
            r20 = 0
            java.lang.Appendable r0 = kotlin.collections.CollectionsKt.joinTo$default(r11, r12, r13, r14, r15, r16, r17, r18, r19, r20)
            java.lang.StringBuilder r0 = (java.lang.StringBuilder) r0
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "mapIndexedNotNull { inde…\"\\n\")\n        .toString()"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r1)
            return r0
        L_0x0100:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "marginPrefix must be non-blank string."
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            java.lang.Throwable r0 = (java.lang.Throwable) r0
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.text.StringsKt__IndentKt.replaceIndentByMargin(java.lang.String, java.lang.String, java.lang.String):java.lang.String");
    }

    public static final String trimIndent(String str) {
        Intrinsics.checkParameterIsNotNull(str, "$this$trimIndent");
        return StringsKt.replaceIndent(str, "");
    }

    public static /* synthetic */ String replaceIndent$default(String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "";
        }
        return StringsKt.replaceIndent(str, str2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v10, types: [java.lang.String] */
    public static final String replaceIndent(String str, String str2) {
        String invoke;
        Intrinsics.checkParameterIsNotNull(str, "$this$replaceIndent");
        Intrinsics.checkParameterIsNotNull(str2, "newIndent");
        List<String> lines = StringsKt.lines(str);
        List<String> list = lines;
        ArrayList arrayList = new ArrayList();
        for (T t : list) {
            if (!StringsKt.isBlank(t)) {
                arrayList.add(t);
            }
        }
        ArrayList<String> arrayList2 = arrayList;
        ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(arrayList2, 10));
        for (String str3 : arrayList2) {
            arrayList3.add(Integer.valueOf(indentWidth$StringsKt__IndentKt(str3)));
        }
        Integer num = (Integer) CollectionsKt.min((Iterable) arrayList3);
        int i = 0;
        int intValue = num != null ? num.intValue() : 0;
        int length = str.length() + (str2.length() * lines.size());
        Function1<String, String> indentFunction$StringsKt__IndentKt = getIndentFunction$StringsKt__IndentKt(str2);
        int lastIndex = CollectionsKt.getLastIndex(lines);
        ArrayList arrayList4 = new ArrayList();
        for (T t2 : list) {
            int i2 = i + 1;
            if (i < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            T t3 = t2;
            if ((i == 0 || i == lastIndex) && StringsKt.isBlank(t3)) {
                t3 = null;
            } else {
                String drop = StringsKt.drop((String) t3, intValue);
                if (!(drop == null || (invoke = indentFunction$StringsKt__IndentKt.invoke(drop)) == 0)) {
                    t3 = invoke;
                }
            }
            if (t3 != null) {
                arrayList4.add(t3);
            }
            i = i2;
        }
        String sb = ((StringBuilder) CollectionsKt.joinTo$default(arrayList4, new StringBuilder(length), "\n", null, null, 0, null, null, 124, null)).toString();
        Intrinsics.checkExpressionValueIsNotNull(sb, "mapIndexedNotNull { inde…\"\\n\")\n        .toString()");
        return sb;
    }

    public static /* synthetic */ String prependIndent$default(String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str2 = "    ";
        }
        return StringsKt.prependIndent(str, str2);
    }

    public static final String prependIndent(String str, String str2) {
        Intrinsics.checkParameterIsNotNull(str, "$this$prependIndent");
        Intrinsics.checkParameterIsNotNull(str2, "indent");
        return SequencesKt.joinToString$default(SequencesKt.map(StringsKt.lineSequence(str), new StringsKt__IndentKt$prependIndent$1(str2)), "\n", null, null, 0, null, null, 62, null);
    }

    private static final int indentWidth$StringsKt__IndentKt(String str) {
        String str2 = str;
        int length = str2.length();
        int i = 0;
        while (true) {
            if (i >= length) {
                i = -1;
                break;
            } else if (!CharsKt.isWhitespace(str2.charAt(i))) {
                break;
            } else {
                i++;
            }
        }
        return i == -1 ? str.length() : i;
    }

    private static final Function1<String, String> getIndentFunction$StringsKt__IndentKt(String str) {
        if (str.length() == 0) {
            return StringsKt__IndentKt$getIndentFunction$1.INSTANCE;
        }
        return new StringsKt__IndentKt$getIndentFunction$2(str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v8, types: [java.lang.String] */
    private static final String reindent$StringsKt__IndentKt(List<String> list, int i, Function1<? super String, String> function1, Function1<? super String, String> function12) {
        String invoke;
        int lastIndex = CollectionsKt.getLastIndex(list);
        ArrayList arrayList = new ArrayList();
        int i2 = 0;
        for (T t : list) {
            int i3 = i2 + 1;
            if (i2 < 0) {
                if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 3, 0)) {
                    CollectionsKt.throwIndexOverflow();
                } else {
                    throw new ArithmeticException("Index overflow has happened.");
                }
            }
            T t2 = t;
            if ((i2 == 0 || i2 == lastIndex) && StringsKt.isBlank(t2)) {
                t2 = null;
            } else {
                String invoke2 = function12.invoke(t2);
                if (!(invoke2 == null || (invoke = function1.invoke(invoke2)) == 0)) {
                    t2 = invoke;
                }
            }
            if (t2 != null) {
                arrayList.add(t2);
            }
            i2 = i3;
        }
        String sb = ((StringBuilder) CollectionsKt.joinTo$default(arrayList, new StringBuilder(i), "\n", null, null, 0, null, null, 124, null)).toString();
        Intrinsics.checkExpressionValueIsNotNull(sb, "mapIndexedNotNull { inde…\"\\n\")\n        .toString()");
        return sb;
    }
}
