package concurrentUtils;

public class Dispatcher implements Runnable {
    private final Channel<Runnable> channel;
    private final ThreadPool threadPool;
    public Dispatcher(Channel<Runnable> channel, ThreadPool threadPool) {
        this.channel = channel;
        this.threadPool = threadPool;
        new Thread(this).start();
    }
    public void run() {
        while (true)
            threadPool.execute(channel.take());
    }
}
