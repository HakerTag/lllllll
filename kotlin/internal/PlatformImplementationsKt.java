package kotlin.internal;

import kotlin.KotlinVersion;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

public final class PlatformImplementationsKt {
    public static final PlatformImplementations IMPLEMENTATIONS;

    static {
        PlatformImplementations platformImplementations;
        Object newInstance;
        Object newInstance2;
        int javaVersion = getJavaVersion();
        if (javaVersion >= 65544) {
            try {
                Object newInstance3 = Class.forName("kotlin.internal.jdk8.JDK8PlatformImplementations").newInstance();
                Intrinsics.checkExpressionValueIsNotNull(newInstance3, "Class.forName(\"kotlin.in…entations\").newInstance()");
                if (newInstance3 != null) {
                    try {
                        platformImplementations = (PlatformImplementations) newInstance3;
                        IMPLEMENTATIONS = platformImplementations;
                    } catch (ClassCastException e) {
                        ClassLoader classLoader = newInstance3.getClass().getClassLoader();
                        ClassLoader classLoader2 = PlatformImplementations.class.getClassLoader();
                        Throwable initCause = new ClassCastException("Instance classloader: " + classLoader + ", base type classloader: " + classLoader2).initCause(e);
                        Intrinsics.checkExpressionValueIsNotNull(initCause, "ClassCastException(\"Inst…baseTypeCL\").initCause(e)");
                        throw initCause;
                    }
                } else {
                    throw new TypeCastException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                }
            } catch (ClassNotFoundException unused) {
                try {
                    newInstance2 = Class.forName("kotlin.internal.JRE8PlatformImplementations").newInstance();
                    Intrinsics.checkExpressionValueIsNotNull(newInstance2, "Class.forName(\"kotlin.in…entations\").newInstance()");
                    if (newInstance2 != null) {
                        platformImplementations = (PlatformImplementations) newInstance2;
                    } else {
                        throw new TypeCastException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                    }
                } catch (ClassNotFoundException unused2) {
                }
            } catch (ClassCastException e2) {
                ClassLoader classLoader3 = newInstance2.getClass().getClassLoader();
                ClassLoader classLoader4 = PlatformImplementations.class.getClassLoader();
                Throwable initCause2 = new ClassCastException("Instance classloader: " + classLoader3 + ", base type classloader: " + classLoader4).initCause(e2);
                Intrinsics.checkExpressionValueIsNotNull(initCause2, "ClassCastException(\"Inst…baseTypeCL\").initCause(e)");
                throw initCause2;
            }
        }
        if (javaVersion >= 65543) {
            try {
                Object newInstance4 = Class.forName("kotlin.internal.jdk7.JDK7PlatformImplementations").newInstance();
                Intrinsics.checkExpressionValueIsNotNull(newInstance4, "Class.forName(\"kotlin.in…entations\").newInstance()");
                if (newInstance4 != null) {
                    try {
                        platformImplementations = (PlatformImplementations) newInstance4;
                        IMPLEMENTATIONS = platformImplementations;
                    } catch (ClassCastException e3) {
                        ClassLoader classLoader5 = newInstance4.getClass().getClassLoader();
                        ClassLoader classLoader6 = PlatformImplementations.class.getClassLoader();
                        Throwable initCause3 = new ClassCastException("Instance classloader: " + classLoader5 + ", base type classloader: " + classLoader6).initCause(e3);
                        Intrinsics.checkExpressionValueIsNotNull(initCause3, "ClassCastException(\"Inst…baseTypeCL\").initCause(e)");
                        throw initCause3;
                    }
                } else {
                    throw new TypeCastException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                }
            } catch (ClassNotFoundException unused3) {
                try {
                    newInstance = Class.forName("kotlin.internal.JRE7PlatformImplementations").newInstance();
                    Intrinsics.checkExpressionValueIsNotNull(newInstance, "Class.forName(\"kotlin.in…entations\").newInstance()");
                    if (newInstance != null) {
                        platformImplementations = (PlatformImplementations) newInstance;
                    } else {
                        throw new TypeCastException("null cannot be cast to non-null type kotlin.internal.PlatformImplementations");
                    }
                } catch (ClassNotFoundException unused4) {
                }
            } catch (ClassCastException e4) {
                ClassLoader classLoader7 = newInstance.getClass().getClassLoader();
                ClassLoader classLoader8 = PlatformImplementations.class.getClassLoader();
                Throwable initCause4 = new ClassCastException("Instance classloader: " + classLoader7 + ", base type classloader: " + classLoader8).initCause(e4);
                Intrinsics.checkExpressionValueIsNotNull(initCause4, "ClassCastException(\"Inst…baseTypeCL\").initCause(e)");
                throw initCause4;
            }
        }
        platformImplementations = new PlatformImplementations();
        IMPLEMENTATIONS = platformImplementations;
    }

    private static final /* synthetic */ <T> T castToBaseType(Object obj) {
        try {
            Intrinsics.reifiedOperationMarker(1, "T");
            return (T) obj;
        } catch (ClassCastException e) {
            ClassLoader classLoader = obj.getClass().getClassLoader();
            Intrinsics.reifiedOperationMarker(4, "T");
            ClassLoader classLoader2 = Object.class.getClassLoader();
            Throwable initCause = new ClassCastException("Instance classloader: " + classLoader + ", base type classloader: " + classLoader2).initCause(e);
            Intrinsics.checkExpressionValueIsNotNull(initCause, "ClassCastException(\"Inst…baseTypeCL\").initCause(e)");
            throw initCause;
        }
    }

    private static final int getJavaVersion() {
        String property = System.getProperty("java.specification.version");
        if (property == null) {
            return 65542;
        }
        String str = property;
        int indexOf$default = StringsKt.indexOf$default((CharSequence) str, '.', 0, false, 6, (Object) null);
        if (indexOf$default < 0) {
            try {
                return Integer.parseInt(property) * 65536;
            } catch (NumberFormatException unused) {
                return 65542;
            }
        } else {
            int i = indexOf$default + 1;
            int indexOf$default2 = StringsKt.indexOf$default((CharSequence) str, '.', i, false, 4, (Object) null);
            if (indexOf$default2 < 0) {
                indexOf$default2 = property.length();
            }
            if (property != null) {
                String substring = property.substring(0, indexOf$default);
                Intrinsics.checkExpressionValueIsNotNull(substring, "(this as java.lang.Strin…ing(startIndex, endIndex)");
                if (property != null) {
                    String substring2 = property.substring(i, indexOf$default2);
                    Intrinsics.checkExpressionValueIsNotNull(substring2, "(this as java.lang.Strin…ing(startIndex, endIndex)");
                    try {
                        return (Integer.parseInt(substring) * 65536) + Integer.parseInt(substring2);
                    } catch (NumberFormatException unused2) {
                        return 65542;
                    }
                } else {
                    throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
                }
            } else {
                throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
            }
        }
    }

    public static final boolean apiVersionIsAtLeast(int i, int i2, int i3) {
        return KotlinVersion.CURRENT.isAtLeast(i, i2, i3);
    }
}
