import java.util.concurrent.atomic.AtomicInteger;

/**
 * 2条线程用于生产，3条线程用于消费，当没有物品可消费时，消费线程进入自旋模式，直到有物品可消费为止。
 */
public class SynchronizedModel2 {
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Runnable incrementAndGet = new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    while (true){
                        atomicInteger.incrementAndGet();
                        System.out.println(Thread.currentThread().getName()+" + : "+atomicInteger.get());
                    }
                }
            }
        };

        Runnable decrementAndGet = new Runnable() {
            @Override
            public void run() {
                while (true){

                        if(atomicInteger.get() <= 0){
                            System.out.println(Thread.currentThread().getName()+"空值，进入自旋模式。。。");
                            while (true){
                                if(atomicInteger.get() > 1){
                                    System.out.println(Thread.currentThread().getName()+"退出自旋模式！");
                                    break;
                                }
                            }
                        }else{
                            atomicInteger.decrementAndGet();
                            System.out.println(Thread.currentThread().getName()+" - : "+atomicInteger.get());
                        }
                    }
            }
        };
        //+
        Thread thread1 = new Thread(incrementAndGet);
        Thread thread2 = new Thread(incrementAndGet);
       // Thread thread3 = new Thread(incrementAndGet);

        //-
        Thread thread4 = new Thread(decrementAndGet);
        Thread thread5 = new Thread(decrementAndGet);
        Thread thread6 = new Thread(decrementAndGet);

        thread1.setName("Thread1");
        thread2.setName("Thread2");
        thread4.setName("Thread4");
        thread5.setName("Thread5");
        thread6.setName("Thread6");

        thread1.start();
        thread2.start();
        thread4.start();
        thread5.start();
        thread6.start();

    }
}
