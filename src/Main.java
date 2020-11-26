
public class Main {

    public static void main(String[] args) {
        EntityLocker locker = new ReentrantEntityLocker();


        Thread t1 = new Thread(new CountThread(locker, 1, 2), "Thread "+ 1);
        Thread t2 = new Thread(new CountThread(locker), "Thread "+ 2);
        Thread t3 = new Thread(new CountThread(locker, 2), "Thread "+ 3);

        t1.start();
        t2.start();
        t3.start();
    }

}

class CountThread implements Runnable{

    private final EntityLocker locker;
    private final Object[] keys;

    CountThread(EntityLocker locker, Object...keys){
        this.locker = locker;
        this.keys = keys;
    }

    public void run() {

        for (Object key: keys) locker.lock(key);
        try{
            for (int i = 1; i < 5; i++){
                System.out.printf("%s | %d \n", Thread.currentThread().getName(), i);
                Thread.sleep(10);
            }
        } catch(InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
//            for (Object key: keys) locker.unlock(key);
            locker.unlockAll();
        }
    }
}
