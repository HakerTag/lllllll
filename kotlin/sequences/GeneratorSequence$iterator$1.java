package kotlin.sequences;

import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

public final class GeneratorSequence$iterator$1 implements Iterator<T>, KMappedMarker {
    private T nextItem;
    private int nextState = -2;
    final /* synthetic */ GeneratorSequence this$0;

    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    GeneratorSequence$iterator$1(GeneratorSequence generatorSequence) {
        this.this$0 = generatorSequence;
    }

    public final T getNextItem() {
        return this.nextItem;
    }

    public final void setNextItem(T t) {
        this.nextItem = t;
    }

    public final int getNextState() {
        return this.nextState;
    }

    public final void setNextState(int i) {
        this.nextState = i;
    }

    private final void calcNext() {
        T t;
        T t2;
        if (this.nextState == -2) {
            t2 = (T) this.this$0.getInitialValue.invoke();
        } else {
            Function1 function1 = this.this$0.getNextValue;
            T t3 = this.nextItem;
            if (t3 == null) {
                Intrinsics.throwNpe();
            }
            t2 = (T) function1.invoke(t3);
        }
        this.nextItem = t;
        this.nextState = t == null ? 0 : 1;
    }

    @Override // java.util.Iterator
    public T next() {
        if (this.nextState < 0) {
            calcNext();
        }
        if (this.nextState != 0) {
            T t = this.nextItem;
            if (t != null) {
                this.nextState = -1;
                return t;
            }
            throw new TypeCastException("null cannot be cast to non-null type T");
        }
        throw new NoSuchElementException();
    }

    public boolean hasNext() {
        if (this.nextState < 0) {
            calcNext();
        }
        return this.nextState == 1;
    }
}
