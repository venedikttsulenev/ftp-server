import java.util.LinkedList;

public class Channel {
    private final int maxSize;
    private final LinkedList<Runnable> queue = new LinkedList<>();
    private final Object lock = new Object();
    public Channel(int maxSize) {
        this.maxSize = maxSize;
    }
    void put(Runnable x) {
        synchronized (lock) {
            while (queue.size() == maxSize)
                try { lock.wait(); }
                catch (InterruptedException e) {}
            queue.addLast(x);
            lock.notifyAll();
        }
    }
    Runnable take() {
        synchronized (lock) {
            while (queue.isEmpty())
                try { lock.wait(); }
                catch (InterruptedException e) {}
            lock.notifyAll();
            return queue.removeFirst();
        }
    }
}