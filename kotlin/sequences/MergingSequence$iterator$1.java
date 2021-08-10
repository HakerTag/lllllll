package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.internal.markers.KMappedMarker;

public final class MergingSequence$iterator$1 implements Iterator<V>, KMappedMarker {
    private final Iterator<T1> iterator1;
    private final Iterator<T2> iterator2;
    final /* synthetic */ MergingSequence this$0;

    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    MergingSequence$iterator$1(MergingSequence mergingSequence) {
        this.this$0 = mergingSequence;
        this.iterator1 = mergingSequence.sequence1.iterator();
        this.iterator2 = mergingSequence.sequence2.iterator();
    }

    public final Iterator<T1> getIterator1() {
        return this.iterator1;
    }

    public final Iterator<T2> getIterator2() {
        return this.iterator2;
    }

    @Override // java.util.Iterator
    public V next() {
        return (V) this.this$0.transform.invoke(this.iterator1.next(), this.iterator2.next());
    }

    public boolean hasNext() {
        return this.iterator1.hasNext() && this.iterator2.hasNext();
    }
}
