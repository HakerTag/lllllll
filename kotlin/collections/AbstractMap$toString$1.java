package kotlin.collections;

import java.util.Map;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

final class AbstractMap$toString$1 extends Lambda implements Function1<Map.Entry<? extends K, ? extends V>, String> {
    final /* synthetic */ AbstractMap this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    AbstractMap$toString$1(AbstractMap abstractMap) {
        super(1);
        this.this$0 = abstractMap;
    }

    public final String invoke(Map.Entry<? extends K, ? extends V> entry) {
        Intrinsics.checkParameterIsNotNull(entry, "it");
        return this.this$0.toString((Map.Entry) entry);
    }
}
