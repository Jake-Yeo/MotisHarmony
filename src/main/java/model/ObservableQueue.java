/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Jake Yeo
 */
public class ObservableQueue<E> extends AbstractQueue<E> {

    public interface Listener<E> {

        void onElementAdded(E element);
    }

    private final Queue<E> delegate;  // backing queue
    private final LinkedList<Listener<E>> listeners = new LinkedList<>();

    public ObservableQueue(Queue<E> delegate) {
        this.delegate = delegate;
    }

    public ObservableQueue<E> setListener(Listener<E> listener) {
        listeners.add(listener);
        return this;
    }

    @Override
    public boolean offer(E e) {
        // here, we put an element in the backing queue, 
        // then notify listeners
        if (delegate.offer(e)) {
            listeners.forEach(listener -> listener.onElementAdded(e));
            return true;
        } else {
            return false;
        }
    }

    // following methods just delegate to backing instance
    @Override
    public E poll() {
        return delegate.poll();
    }

    @Override
    public E peek() {
        return delegate.peek();
    }

    @Override
    public int size() {
        return delegate.size();
    }
    
    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

}
