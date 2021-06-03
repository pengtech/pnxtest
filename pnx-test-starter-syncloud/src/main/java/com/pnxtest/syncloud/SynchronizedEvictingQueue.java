package com.pnxtest.syncloud;



import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class SynchronizedEvictingQueue<E> implements Queue<E> {
    private final int maxSize;
    private final Queue<E> deque;

    public SynchronizedEvictingQueue(int maxSize) {
        //Preconditions.checkArgument(0 < maxSize);
        this.maxSize = maxSize;
        this.deque = new ArrayDeque(maxSize);
    }

    public synchronized int size() {
        return this.deque.size();
    }

    public synchronized boolean isEmpty() {
        return this.deque.isEmpty();
    }

    public synchronized boolean contains(Object o) {
        return this.deque.contains(o);
    }

    public synchronized Iterator<E> iterator() {
        return this.deque.iterator();
    }

    public synchronized Object[] toArray() {
        return this.deque.toArray();
    }

    public synchronized <T> T[] toArray(T[] a) {
        return this.deque.toArray(a);
    }

    public synchronized boolean remove(Object o) {
        return this.deque.remove(o);
    }

    public synchronized boolean containsAll(Collection<?> c) {
        return this.deque.containsAll(c);
    }

    public synchronized boolean addAll(Collection<? extends E> c) {
        //Preconditions.checkNotNull(c);
        Iterator ite = c.iterator();

        while (ite.hasNext()) {
            E e = (E) ite.next();
            this.add(e);
        }

        return true;
    }

    public synchronized boolean removeAll(Collection<?> c) {
        return this.deque.removeAll(c);
    }

    public synchronized boolean retainAll(Collection<?> c) {
        return this.deque.retainAll(c);
    }

    public synchronized void clear() {
        this.deque.clear();
    }

    public synchronized boolean add(E e) {
        //Preconditions.checkNotNull(e);
        if (this.deque.size() == this.maxSize) {
            this.deque.remove();
        }

        this.deque.add(e);
        return true;
    }

    public synchronized boolean offer(E e) {
        return this.add(e);
    }

    public synchronized E remove() {
        return this.deque.remove();
    }

    public synchronized E poll() {
        return this.deque.poll();
    }

    public synchronized E element() {
        return this.deque.element();
    }

    public synchronized E peek() {
        return this.deque.peek();
    }
}
