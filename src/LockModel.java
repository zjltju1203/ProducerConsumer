import java.beans.Customizer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class LockModel {
    public static int i = 0;
    public static void main(String[] args) {
        Producer producer = new Producer();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future> list = new ArrayList<>();
        for (int j = 0; j <2 ; j++) {
            list.add(executorService.submit(producer));
        }
        for (int j = 0; j < list.size(); j++) {
            try {
                System.out.println(list.get(j).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


        Consumer consumer = new Consumer();
        executorService = Executors.newCachedThreadPool();
        executorService.execute(consumer);
        executorService.execute(consumer);
        executorService.execute(consumer);
        executorService.execute(consumer);
    }
    static class Producer implements Callable{
        ReentrantLock reentrantLock = new ReentrantLock();
        @Override
        public Object call() throws Exception {
            while (true){
                reentrantLock.lock();
                try {
                    i++;
                    System.out.println(Thread.currentThread().getName() +"   值： "+i);
                }finally {
                    reentrantLock.unlock();
                }
                if(i >= 100){
                    break;
                }

            }
            return Thread.currentThread().getName()+" over!";
        }
    }
    static class Consumer implements Runnable{
        ReentrantLock reentrantLock = new ReentrantLock();
        @Override
        public void run() {
            while (true){
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                reentrantLock.lock();
                try {
                    if(i <= 0){
                        while (true){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println(Thread.currentThread().getName()+" 进入自旋模式"+ i);
                            if(i > 0){
                                break;
                            }
                        }
                    }
                    i--;
                    System.out.println(Thread.currentThread().getName() +"   值： "+i);
                }finally {
                    reentrantLock.unlock();
                }
            }
        }
    }
}

