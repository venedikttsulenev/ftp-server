package concurrentUtils;

public class WorkerThread implements Runnable {
    private final ThreadPool threadPool;
    private Runnable currentTask = null;
    private final Object lock = new Object();
    public WorkerThread(ThreadPool threadPool) {
        this.threadPool = threadPool;
        new Thread(this).start();
    }
    public void run() {
        synchronized (lock) {
            while (true) {
                try {
                    while (currentTask == null)
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {}
                    currentTask.run();
                    currentTask = null;
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                } finally {
                    threadPool.onTaskCompleted(this);
                }
            }
        }
    }
    public void execute(Runnable task) throws NullPointerException, IllegalStateException {
        if (task == null)
            throw new NullPointerException();
        synchronized (lock) {
            if (currentTask != null)
                throw new IllegalStateException("Cannot execute task: worker is busy");
            currentTask = task;
            lock.notifyAll();
        }
    }
}
