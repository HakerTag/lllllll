package kotlin.collections;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

class ReversedListReadOnly<T> extends AbstractList<T> {
    private final List<T> delegate;

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: java.util.List<? extends T> */
    /* JADX WARN: Multi-variable type inference failed */
    public ReversedListReadOnly(List<? extends T> list) {
        Intrinsics.checkParameterIsNotNull(list, "delegate");
        this.delegate = list;
    }

    @Override // kotlin.collections.AbstractList, kotlin.collections.AbstractCollection
    public int getSize() {
        return this.delegate.size();
    }

    @Override // java.util.List, kotlin.collections.AbstractList
    public T get(int i) {
        return this.delegate.get(CollectionsKt__ReversedViewsKt.reverseElementIndex$CollectionsKt__ReversedViewsKt(this, i));
    }
}
