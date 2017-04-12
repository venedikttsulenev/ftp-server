package concurrentUtils;

public class WorkerThread implements Stoppable {
    private final ThreadPool threadPool;
    private final Thread thread;
    private final Object lock = new Object();
    private Stoppable currentTask = null;
    private volatile boolean isAlive = true;
    public WorkerThread(ThreadPool threadPool) {
        this.threadPool = threadPool;
        (this.thread = new Thread(this)).start();
    }
    @Override
    public void run() {
        synchronized (lock) {
            while (isAlive) {
                try {
                    while (currentTask == null)
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            if (!isAlive)
                                return;
                        }
                    currentTask.run();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                } finally {
                    currentTask = null;
                    threadPool.onTaskCompleted(this);
                }
            }
        }
    }
    @Override
    public void stop() {
        if (isAlive) {
            isAlive = false;
            if (null != currentTask) /* No need to synchronize because dispatcher stopped already */
                currentTask.stop();
            thread.interrupt();
        }
    }
    public void execute(Stoppable task) throws NullPointerException, IllegalStateException {
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
