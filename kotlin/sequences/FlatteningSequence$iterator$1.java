package kotlin.sequences;

import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

public final class FlatteningSequence$iterator$1 implements Iterator<E>, KMappedMarker {
    private Iterator<? extends E> itemIterator;
    private final Iterator<T> iterator;
    final /* synthetic */ FlatteningSequence this$0;

    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    FlatteningSequence$iterator$1(FlatteningSequence flatteningSequence) {
        this.this$0 = flatteningSequence;
        this.iterator = flatteningSequence.sequence.iterator();
    }

    public final Iterator<T> getIterator() {
        return this.iterator;
    }

    /* JADX DEBUG: Type inference failed for r0v0. Raw type applied. Possible types: java.util.Iterator<? extends E>, java.util.Iterator<E> */
    public final Iterator<E> getItemIterator() {
        return (Iterator<? extends E>) this.itemIterator;
    }

    public final void setItemIterator(Iterator<? extends E> it) {
        this.itemIterator = it;
    }

    @Override // java.util.Iterator
    public E next() {
        if (ensureItemIterator()) {
            Iterator<? extends E> it = this.itemIterator;
            if (it == null) {
                Intrinsics.throwNpe();
            }
            return (E) it.next();
        }
        throw new NoSuchElementException();
    }

    public boolean hasNext() {
        return ensureItemIterator();
    }

    private final boolean ensureItemIterator() {
        Iterator<? extends E> it = this.itemIterator;
        if (it != null && !it.hasNext()) {
            this.itemIterator = null;
        }
        while (true) {
            if (this.itemIterator == null) {
                if (this.iterator.hasNext()) {
                    Iterator<? extends E> it2 = (Iterator) this.this$0.iterator.invoke(this.this$0.transformer.invoke(this.iterator.next()));
                    if (it2.hasNext()) {
                        this.itemIterator = it2;
                        break;
                    }
                } else {
                    return false;
                }
            } else {
                break;
            }
        }
        return true;
    }
}
