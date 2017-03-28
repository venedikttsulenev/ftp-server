import java.util.LinkedList;

public class Channel<T> {
    private final int maxSize;
    private final LinkedList<T> queue = new LinkedList<>();
    private final Object lock = new Object();
    public Channel(int maxSize) {
        this.maxSize = maxSize;
    }
    void put(T x) {
        synchronized (lock) {
            while (queue.size() == maxSize)
                try { lock.wait(); }
                catch (InterruptedException e) {}
            queue.addLast(x);
            lock.notifyAll();
        }
    }
    T take() {
        synchronized (lock) {
            while (queue.isEmpty())
                try { lock.wait(); }
                catch (InterruptedException e) {}
            lock.notifyAll();
            return queue.removeFirst();
        }
    }
    boolean isEmpty() {
        synchronized (lock) {
            return queue.isEmpty();
        }
    }
}