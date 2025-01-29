import java.util.*;

public class MyHashSet<E> implements MySet<E> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private Node<E>[] buckets;
    private int size;
    private float loadFactor;
    private int modCount;

    public MyHashSet() {
        this(DEFAULT_INITIAL_CAPACITY,DEFAULT_LOAD_FACTOR);
    }

    public MyHashSet(int initialCapacity, float loadFactor){
        if (initialCapacity < 0){
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }

        if (loadFactor <= 0 || Float.isNaN(loadFactor)){
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        this.loadFactor = loadFactor;
        this.buckets = new Node[tableSizeFor(initialCapacity)];
    }

    public static class Node<E> {
        final  E data;
        Node<E> next;

        Node(E data, Node<E> next){
            this.data = data;
            this.next = next;
        }
    }

    private static int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    private int getBucketIndex(Object element, int length) {
        if (element == null){
            return 0;
        }

        int hashCode = element.hashCode();
        return (hashCode & 0x7FFFFFFF) & (length - 1);
    }

    private void resize() {
        int oldCapacity = buckets.length;
        int newCapacity = oldCapacity << 1;

        if (newCapacity > MAXIMUM_CAPACITY){
            return;
        }
        Node<E>[] newBuckets = new Node[newCapacity];
        for (Node<E> node : buckets){
            while (node != null){
                Node<E> next = node.next;
                int index = getBucketIndex(node.data, newCapacity);
                node.next = newBuckets[index];
                newBuckets[index] = node;
                node = next;
            }
        }
        buckets = newBuckets;

    }

    @Override
    public boolean add(E element) {
        if (size >= loadFactor * buckets.length) {
            resize();
        }
        int index = getBucketIndex(element, buckets.length);
        Node<E> current = buckets[index];

        while (current != null) {
            if (Objects.equals(current.data,element)){
                return false;
            }
            current = current.next;
        }

        buckets[index] = new Node<>(element, buckets[index]);
        size++;
        modCount++;

        return true;
    }

    @Override
    public boolean remove(Object element){
        int index = getBucketIndex(element , buckets.length);
        Node<E> current = buckets[index];

        Node<E> prev = null;

        while (current != null){
            if (Objects.equals(current.data, element)){
                if (prev == null){
                    buckets[index] = current.next;
                }

                else {
                    prev.next = current.next;
                }

                size--;
                modCount++;

                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean contains(Object element){
        int index = getBucketIndex(element , buckets.length);
        Node<E> current = buckets[index];

        while (current != null){
            if (Objects.equals(current.data, element)){
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        Arrays.fill(buckets,null);
        size = 0;
        modCount++;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int currentBucket = 0;
            private Node<E> current;
            private Node<E> next ;
            private int expectedModCount = modCount;

            {
                advanceToNextElement();
            }

            private void advanceToNextElement() {
                while (currentBucket < buckets.length) {
                    if (buckets[currentBucket] != null){
                        next = buckets[currentBucket];
                        currentBucket++;
                        break;
                    }
                    currentBucket++;
                }
            }

            @Override
            public boolean hasNext() {
                checkForModification();
                return next != null;
            }

            @Override
            public  E next() {
                checkForModification();
                if (!hasNext()) {
                    throw new NoSuchElementException("No element found");
                }

                current = next;
                next = current.next;
                if (next == null) {
                    advanceToNextElement();
                }

                return current.data;
            }

            private void checkForModification() {
                if (modCount != expectedModCount) {
                    throw  new ConcurrentModificationException("Modification is done while iterating");
                }
            }
        };


    }
    @Override
    public boolean addAll(Collection<? extends  E> c){
        boolean modified = false;
        for (E element : c) {
            if (add(element)){
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object element : c) {
            while (remove(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainsAll(Collection<?> c){
        boolean modified = false;
        Iterator<E> it = iterator();

        while (it.hasNext()){
            E element = it.next();
            if (!c.contains(element)){
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /* these lines are added by my IDE I have no idea why, GPT is
     not giving response also.

     * @param c
     * @return
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int i = 0;

        for (E element : this){
            array[i] = element;
            i++;
        }
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a){
        if (a.length < size){
            a = (T[]) Arrays.copyOf(a, size, a.getClass());
        }

        int i = 0;
        for (E element : this) {
            a[i] = (T) element;
            i++;
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (! (o instanceof MySet)) {
            return false;
        }

        MySet<?> other = (MySet<?>) o;
        if (size != other.size()) {
            return false;
        }

        try {
            for (E element : this) {
                if (!other.contains(element)) {
                    return false;
                }
            }
        }

        catch (ClassCastException | NullPointerException unused) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (E element : this){
            if (element != null) {
                 h = h + element.hashCode();
            }
        }

        return h;
    }
}