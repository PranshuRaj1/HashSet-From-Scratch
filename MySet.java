import java.util.Collection;
import java.util.Iterator;

public interface MySet<E> extends Iterable<E>{
    boolean add(E element);

    boolean remove(Object element);

    boolean contains(Object element);

    int size();

    boolean isEmpty();

    void clear();

    Iterator<E> iterator();

    boolean addAll(Collection<?  extends E> c);

    boolean removeAll(Collection<?> c);

    boolean retainsAll(Collection<?> c);

    boolean retainAll(Collection<?> c);

    Object[] toArray();

    <T> T[] toArray(T[] a);

    boolean equals(Object o);

    int hashCode();
}

