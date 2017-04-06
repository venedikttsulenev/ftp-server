package concurrentUtils;

import java.util.LinkedList;

public class ThreadPool {
    private final LinkedList<WorkerThread> allWorkers;
    private final Channel<WorkerThread> freeWorkers;
    private final int maxSize;
    private final Object lock = new Object();
    public ThreadPool(int maxSize) {
        this.maxSize = maxSize;
        this.freeWorkers = new Channel<>(maxSize);
        this.allWorkers = new LinkedList<>();
        WorkerThread workerThread = new WorkerThread(this); /* starts implicitly */
        allWorkers.add(workerThread);
        freeWorkers.put(workerThread);
    }
    public void execute(Runnable task) {
        if (freeWorkers.isEmpty()) {
            synchronized (lock) {
                if (allWorkers.size() < maxSize) {
                    WorkerThread workerThread = new WorkerThread(this);
                    allWorkers.addLast(workerThread);
                    freeWorkers.put(workerThread);
                }
            }
        }
        freeWorkers.take().execute(task);
    }
    public int getSessionsCount() {
        synchronized (lock) {
            return allWorkers.size() - freeWorkers.size();
        }
    }
    public void onTaskCompleted(WorkerThread workerThread) {
        freeWorkers.put(workerThread);
    }
}
