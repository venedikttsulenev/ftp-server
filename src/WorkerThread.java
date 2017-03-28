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
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    threadPool.onTaskCompleted(this);
                }
            }
        }
    }
    public void execute(Runnable task) {
        synchronized (lock) {
            currentTask = task;
            lock.notifyAll();
        }
    }
}
