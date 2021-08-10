package kotlin.time;

import java.util.concurrent.TimeUnit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.functions.Function5;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.DoubleCompanionObject;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

public final class Duration implements Comparable<Duration> {
    public static final Companion Companion = new Companion(null);
    private static final double INFINITE = m928constructorimpl(DoubleCompanionObject.INSTANCE.getPOSITIVE_INFINITY());
    private static final double ZERO = m928constructorimpl(0.0d);
    private final double value;

    /* renamed from: box-impl */
    public static final /* synthetic */ Duration m926boximpl(double d) {
        return new Duration(d);
    }

    /* renamed from: constructor-impl */
    public static double m928constructorimpl(double d) {
        return d;
    }

    /* renamed from: div-LRDsOJo */
    public static final double m929divLRDsOJo(double d, double d2) {
        return d / d2;
    }

    /* renamed from: equals-impl */
    public static boolean m932equalsimpl(double d, Object obj) {
        return (obj instanceof Duration) && Double.compare(d, ((Duration) obj).m971unboximpl()) == 0;
    }

    /* renamed from: equals-impl0 */
    public static final boolean m933equalsimpl0(double d, double d2) {
        throw null;
    }

    /* renamed from: hashCode-impl */
    public static int m946hashCodeimpl(double d) {
        long doubleToLongBits = Double.doubleToLongBits(d);
        return (int) (doubleToLongBits ^ (doubleToLongBits >>> 32));
    }

    public static /* synthetic */ void hoursComponent$annotations() {
    }

    /* renamed from: isNegative-impl */
    public static final boolean m949isNegativeimpl(double d) {
        return d < ((double) 0);
    }

    /* renamed from: isPositive-impl */
    public static final boolean m950isPositiveimpl(double d) {
        return d > ((double) 0);
    }

    public static /* synthetic */ void minutesComponent$annotations() {
    }

    public static /* synthetic */ void nanosecondsComponent$annotations() {
    }

    /* renamed from: precision-impl */
    private static final int m953precisionimpl(double d, double d2) {
        if (d2 < ((double) 1)) {
            return 3;
        }
        if (d2 < ((double) 10)) {
            return 2;
        }
        return d2 < ((double) 100) ? 1 : 0;
    }

    public static /* synthetic */ void secondsComponent$annotations() {
    }

    /* renamed from: compareTo-LRDsOJo */
    public int m970compareToLRDsOJo(double d) {
        return m927compareToLRDsOJo(this.value, d);
    }

    public boolean equals(Object obj) {
        return m932equalsimpl(this.value, obj);
    }

    public int hashCode() {
        return m946hashCodeimpl(this.value);
    }

    public String toString() {
        return m966toStringimpl(this.value);
    }

    /* renamed from: unbox-impl */
    public final /* synthetic */ double m971unboximpl() {
        return this.value;
    }

    private /* synthetic */ Duration(double d) {
        this.value = d;
    }

    @Override // java.lang.Comparable
    public /* bridge */ /* synthetic */ int compareTo(Duration duration) {
        return m970compareToLRDsOJo(duration.m971unboximpl());
    }

    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final double getZERO() {
            return Duration.ZERO;
        }

        public final double getINFINITE() {
            return Duration.INFINITE;
        }

        public final double convert(double d, TimeUnit timeUnit, TimeUnit timeUnit2) {
            Intrinsics.checkParameterIsNotNull(timeUnit, "sourceUnit");
            Intrinsics.checkParameterIsNotNull(timeUnit2, "targetUnit");
            return DurationUnitKt.convertDurationUnit(d, timeUnit, timeUnit2);
        }
    }

    /* renamed from: unaryMinus-impl */
    public static final double m969unaryMinusimpl(double d) {
        return m928constructorimpl(-d);
    }

    /* renamed from: plus-LRDsOJo */
    public static final double m952plusLRDsOJo(double d, double d2) {
        return m928constructorimpl(d + d2);
    }

    /* renamed from: minus-LRDsOJo */
    public static final double m951minusLRDsOJo(double d, double d2) {
        return m928constructorimpl(d - d2);
    }

    /* renamed from: times-impl */
    public static final double m955timesimpl(double d, int i) {
        return m928constructorimpl(d * ((double) i));
    }

    /* renamed from: times-impl */
    public static final double m954timesimpl(double d, double d2) {
        return m928constructorimpl(d * d2);
    }

    /* renamed from: div-impl */
    public static final double m931divimpl(double d, int i) {
        return m928constructorimpl(d / ((double) i));
    }

    /* renamed from: div-impl */
    public static final double m930divimpl(double d, double d2) {
        return m928constructorimpl(d / d2);
    }

    /* renamed from: isInfinite-impl */
    public static final boolean m948isInfiniteimpl(double d) {
        return Double.isInfinite(d);
    }

    /* renamed from: isFinite-impl */
    public static final boolean m947isFiniteimpl(double d) {
        return !Double.isInfinite(d) && !Double.isNaN(d);
    }

    /* renamed from: getAbsoluteValue-impl */
    public static final double m934getAbsoluteValueimpl(double d) {
        return m949isNegativeimpl(d) ? m969unaryMinusimpl(d) : d;
    }

    /* renamed from: compareTo-LRDsOJo */
    public static int m927compareToLRDsOJo(double d, double d2) {
        return Double.compare(d, d2);
    }

    /* renamed from: toComponents-impl */
    public static final <T> T m959toComponentsimpl(double d, Function5<? super Integer, ? super Integer, ? super Integer, ? super Integer, ? super Integer, ? extends T> function5) {
        Intrinsics.checkParameterIsNotNull(function5, "action");
        return (T) function5.invoke(Integer.valueOf((int) m936getInDaysimpl(d)), Integer.valueOf(m935getHoursComponentimpl(d)), Integer.valueOf(m943getMinutesComponentimpl(d)), Integer.valueOf(m945getSecondsComponentimpl(d)), Integer.valueOf(m944getNanosecondsComponentimpl(d)));
    }

    /* renamed from: toComponents-impl */
    public static final <T> T m958toComponentsimpl(double d, Function4<? super Integer, ? super Integer, ? super Integer, ? super Integer, ? extends T> function4) {
        Intrinsics.checkParameterIsNotNull(function4, "action");
        return (T) function4.invoke(Integer.valueOf((int) m937getInHoursimpl(d)), Integer.valueOf(m943getMinutesComponentimpl(d)), Integer.valueOf(m945getSecondsComponentimpl(d)), Integer.valueOf(m944getNanosecondsComponentimpl(d)));
    }

    /* renamed from: toComponents-impl */
    public static final <T> T m957toComponentsimpl(double d, Function3<? super Integer, ? super Integer, ? super Integer, ? extends T> function3) {
        Intrinsics.checkParameterIsNotNull(function3, "action");
        return (T) function3.invoke(Integer.valueOf((int) m940getInMinutesimpl(d)), Integer.valueOf(m945getSecondsComponentimpl(d)), Integer.valueOf(m944getNanosecondsComponentimpl(d)));
    }

    /* renamed from: toComponents-impl */
    public static final <T> T m956toComponentsimpl(double d, Function2<? super Long, ? super Integer, ? extends T> function2) {
        Intrinsics.checkParameterIsNotNull(function2, "action");
        return (T) function2.invoke(Long.valueOf((long) m942getInSecondsimpl(d)), Integer.valueOf(m944getNanosecondsComponentimpl(d)));
    }

    /* renamed from: getHoursComponent-impl */
    public static final int m935getHoursComponentimpl(double d) {
        return (int) (m937getInHoursimpl(d) % ((double) 24));
    }

    /* renamed from: getMinutesComponent-impl */
    public static final int m943getMinutesComponentimpl(double d) {
        return (int) (m940getInMinutesimpl(d) % ((double) 60));
    }

    /* renamed from: getSecondsComponent-impl */
    public static final int m945getSecondsComponentimpl(double d) {
        return (int) (m942getInSecondsimpl(d) % ((double) 60));
    }

    /* renamed from: getNanosecondsComponent-impl */
    public static final int m944getNanosecondsComponentimpl(double d) {
        return (int) (m941getInNanosecondsimpl(d) % 1.0E9d);
    }

    /* renamed from: toDouble-impl */
    public static final double m960toDoubleimpl(double d, TimeUnit timeUnit) {
        Intrinsics.checkParameterIsNotNull(timeUnit, "unit");
        return DurationUnitKt.convertDurationUnit(d, DurationKt.getStorageUnit(), timeUnit);
    }

    /* renamed from: toLong-impl */
    public static final long m963toLongimpl(double d, TimeUnit timeUnit) {
        Intrinsics.checkParameterIsNotNull(timeUnit, "unit");
        return (long) m960toDoubleimpl(d, timeUnit);
    }

    /* renamed from: toInt-impl */
    public static final int m961toIntimpl(double d, TimeUnit timeUnit) {
        Intrinsics.checkParameterIsNotNull(timeUnit, "unit");
        return (int) m960toDoubleimpl(d, timeUnit);
    }

    /* renamed from: getInDays-impl */
    public static final double m936getInDaysimpl(double d) {
        return m960toDoubleimpl(d, TimeUnit.DAYS);
    }

    /* renamed from: getInHours-impl */
    public static final double m937getInHoursimpl(double d) {
        return m960toDoubleimpl(d, TimeUnit.HOURS);
    }

    /* renamed from: getInMinutes-impl */
    public static final double m940getInMinutesimpl(double d) {
        return m960toDoubleimpl(d, TimeUnit.MINUTES);
    }

    /* renamed from: getInSeconds-impl */
    public static final double m942getInSecondsimpl(double d) {
        return m960toDoubleimpl(d, TimeUnit.SECONDS);
    }

    /* renamed from: getInMilliseconds-impl */
    public static final double m939getInMillisecondsimpl(double d) {
        return m960toDoubleimpl(d, TimeUnit.MILLISECONDS);
    }

    /* renamed from: getInMicroseconds-impl */
    public static final double m938getInMicrosecondsimpl(double d) {
        return m960toDoubleimpl(d, TimeUnit.MICROSECONDS);
    }

    /* renamed from: getInNanoseconds-impl */
    public static final double m941getInNanosecondsimpl(double d) {
        return m960toDoubleimpl(d, TimeUnit.NANOSECONDS);
    }

    /* renamed from: toLongNanoseconds-impl */
    public static final long m965toLongNanosecondsimpl(double d) {
        return m963toLongimpl(d, TimeUnit.NANOSECONDS);
    }

    /* renamed from: toLongMilliseconds-impl */
    public static final long m964toLongMillisecondsimpl(double d) {
        return m963toLongimpl(d, TimeUnit.MILLISECONDS);
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x009f  */
    /* renamed from: toString-impl */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String m966toStringimpl(double r8) {
        /*
            boolean r0 = m948isInfiniteimpl(r8)
            if (r0 == 0) goto L_0x000c
            java.lang.String r8 = java.lang.String.valueOf(r8)
            goto L_0x00c0
        L_0x000c:
            r0 = 0
            int r2 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r2 != 0) goto L_0x0016
            java.lang.String r8 = "0s"
            goto L_0x00c0
        L_0x0016:
            double r0 = m934getAbsoluteValueimpl(r8)
            double r0 = m941getInNanosecondsimpl(r0)
            r2 = 4517329193108106637(0x3eb0c6f7a0b5ed8d, double:1.0E-6)
            r4 = 0
            r5 = 1
            int r6 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r6 >= 0) goto L_0x002e
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.SECONDS
        L_0x002b:
            r1 = 0
            r4 = 1
            goto L_0x008f
        L_0x002e:
            double r2 = (double) r5
            int r6 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r6 >= 0) goto L_0x0037
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.NANOSECONDS
            r1 = 7
            goto L_0x008f
        L_0x0037:
            r2 = 4652007308841189376(0x408f400000000000, double:1000.0)
            int r6 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r6 >= 0) goto L_0x0044
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.NANOSECONDS
        L_0x0042:
            r1 = 0
            goto L_0x008f
        L_0x0044:
            r2 = 4696837146684686336(0x412e848000000000, double:1000000.0)
            int r6 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r6 >= 0) goto L_0x0050
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.MICROSECONDS
            goto L_0x0042
        L_0x0050:
            r2 = 4741671816366391296(0x41cdcd6500000000, double:1.0E9)
            int r6 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r6 >= 0) goto L_0x005c
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.MILLISECONDS
            goto L_0x0042
        L_0x005c:
            r2 = 4786511204640096256(0x426d1a94a2000000, double:1.0E12)
            int r6 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r6 >= 0) goto L_0x0068
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.SECONDS
            goto L_0x0042
        L_0x0068:
            r2 = 4813020802404319232(0x42cb48eb57e00000, double:6.0E13)
            int r6 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r6 >= 0) goto L_0x0074
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.MINUTES
            goto L_0x0042
        L_0x0074:
            r2 = 4839562400168542208(0x4329945ca2620000, double:3.6E15)
            int r6 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r6 >= 0) goto L_0x0080
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.HOURS
            goto L_0x0042
        L_0x0080:
            r2 = 4920018990336211136(0x44476b344f2a78c0, double:8.64E20)
            int r6 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r6 >= 0) goto L_0x008c
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.DAYS
            goto L_0x0042
        L_0x008c:
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.DAYS
            goto L_0x002b
        L_0x008f:
            double r2 = m960toDoubleimpl(r8, r0)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            if (r4 == 0) goto L_0x009f
            java.lang.String r8 = kotlin.time.FormatToDecimalsKt.formatScientific(r2)
            goto L_0x00b2
        L_0x009f:
            if (r1 <= 0) goto L_0x00a6
            java.lang.String r8 = kotlin.time.FormatToDecimalsKt.formatUpToDecimals(r2, r1)
            goto L_0x00b2
        L_0x00a6:
            double r6 = java.lang.Math.abs(r2)
            int r8 = m953precisionimpl(r8, r6)
            java.lang.String r8 = kotlin.time.FormatToDecimalsKt.formatToExactDecimals(r2, r8)
        L_0x00b2:
            r5.append(r8)
            java.lang.String r8 = kotlin.time.DurationUnitKt.shortName(r0)
            r5.append(r8)
            java.lang.String r8 = r5.toString()
        L_0x00c0:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.time.Duration.m966toStringimpl(double):java.lang.String");
    }

    /* renamed from: toString-impl$default */
    public static /* synthetic */ String m968toStringimpl$default(double d, TimeUnit timeUnit, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        return m967toStringimpl(d, timeUnit, i);
    }

    /* renamed from: toString-impl */
    public static final String m967toStringimpl(double d, TimeUnit timeUnit, int i) {
        String str;
        Intrinsics.checkParameterIsNotNull(timeUnit, "unit");
        if (!(i >= 0)) {
            throw new IllegalArgumentException(("decimals must be not negative, but was " + i).toString());
        } else if (m948isInfiniteimpl(d)) {
            return String.valueOf(d);
        } else {
            double r6 = m960toDoubleimpl(d, timeUnit);
            StringBuilder sb = new StringBuilder();
            if (Math.abs(r6) < 1.0E14d) {
                str = FormatToDecimalsKt.formatToExactDecimals(r6, RangesKt.coerceAtMost(i, 12));
            } else {
                str = FormatToDecimalsKt.formatScientific(r6);
            }
            sb.append(str);
            sb.append(DurationUnitKt.shortName(timeUnit));
            return sb.toString();
        }
    }

    /* renamed from: toIsoString-impl */
    public static final String m962toIsoStringimpl(double d) {
        StringBuilder sb = new StringBuilder();
        if (m949isNegativeimpl(d)) {
            sb.append('-');
        }
        sb.append("PT");
        double r7 = m934getAbsoluteValueimpl(d);
        int r1 = (int) m937getInHoursimpl(r7);
        int r2 = m943getMinutesComponentimpl(r7);
        int r3 = m945getSecondsComponentimpl(r7);
        int r72 = m944getNanosecondsComponentimpl(r7);
        boolean z = true;
        boolean z2 = r1 != 0;
        boolean z3 = (r3 == 0 && r72 == 0) ? false : true;
        if (r2 == 0 && (!z3 || !z2)) {
            z = false;
        }
        if (z2) {
            sb.append(r1);
            sb.append('H');
        }
        if (z) {
            sb.append(r2);
            sb.append('M');
        }
        if (z3 || (!z2 && !z)) {
            sb.append(r3);
            if (r72 != 0) {
                sb.append('.');
                String padStart = StringsKt.padStart(String.valueOf(r72), 9, '0');
                if (r72 % 1000000 == 0) {
                    sb.append((CharSequence) padStart, 0, 3);
                } else if (r72 % 1000 == 0) {
                    sb.append((CharSequence) padStart, 0, 6);
                } else {
                    sb.append(padStart);
                }
            }
            sb.append('S');
        }
        String sb2 = sb.toString();
        Intrinsics.checkExpressionValueIsNotNull(sb2, "StringBuilder().apply(builderAction).toString()");
        return sb2;
    }
}
