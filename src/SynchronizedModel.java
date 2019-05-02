import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SynchronizedModel {
    private static AtomicInteger counts = new AtomicInteger(10);
    private static volatile boolean isRunning = true;
    private static ConcurrentHashMap<String,Double> mapPrice = new ConcurrentHashMap();
    private static ConcurrentHashMap<String,String> mapDate = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String,String> mapTrain = new ConcurrentHashMap<>();
    private static ConcurrentLinkedQueue<Ticket> queue = new ConcurrentLinkedQueue<Ticket>();
    static final CountDownLatch countDownLatch = new CountDownLatch(3);
    static {
        mapPrice.put("01",752.5);
        mapPrice.put("02",550.0);
        mapPrice.put("12",579.5);
        mapPrice.put("10",752.5);
        mapPrice.put("20",550.0);
        mapPrice.put("21",579.5);


        mapDate.put("01","2019-05-03 09:00");
        mapDate.put("02","2019-05-03 10:15");
        mapDate.put("12","2019-05-03 15:00");
        mapDate.put("10","2019-05-03 12:00");
        mapDate.put("20","2019-05-03 19:00");
        mapDate.put("21","2019-05-03 22:00");

        mapTrain.put("01","G520");
        mapTrain.put("02","G375");
        mapTrain.put("12","G729");
        mapTrain.put("10","G520");
        mapTrain.put("20","G375");
        mapTrain.put("21","G729");
    }
    public static void main(String[] args) {

        Producer producer1 = new Producer("Thread1");
        Producer producer2 = new Producer("Thread2");
        Producer producer3 = new Producer("Thread3");

        Thread thread1 = new Thread(producer1);
        Thread thread2 = new Thread(producer2);
        Thread thread3 = new Thread(producer3);

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("队列总数： "+queue.size());

        Consumer consumer = new Consumer();
        Thread thread4 = new Thread((Runnable) consumer);
        thread4.start();
        try {
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    static class Producer implements Runnable{

        private String ThreadName;
        private final String[] address = {"北京","广州","上海"};

        public Producer(String threadName) {
            ThreadName = threadName;
        }

        @Override
        public void run() {
            while (isRunning){
                System.out.println(ThreadName+" : "+counts.decrementAndGet());
                synchronized (queue){
                    Ticket ticket = new Ticket();
                    int origin = getRandom();
                    int dest =getRandom();
                    while (dest==origin){
                        dest=getRandom();
                    }
                    String flag =String.valueOf(origin)+String.valueOf(dest);
                    ticket.setId(String.valueOf((long)(Math.random()*1000000000l)));
                    ticket.setOriginDirection(address[origin]);
                    ticket.setDestination(address[dest]);
                    ticket.setRidingtime(mapDate.get(flag));
                    ticket.setPrice(mapPrice.get(flag));
                    ticket.setTrainNUm(mapTrain.get(flag));
                    ticket.setSeatNum(generateTrainNum());
                    queue.offer(ticket);
                    System.out.println(ticket.toString());

                }
                if(counts.get() <= 0){
                    isRunning = false;
                }

            }
            countDownLatch.countDown();
        }
        private synchronized int getRandom(){
            return (int)(Math.random()*3);
        }
        private synchronized String generateTrainNum(){
            Random random = new Random();
            int nextInt = random.nextInt( 2 ) % 2 == 0 ? 65 : 97;
            char val = (char) ( nextInt + random.nextInt( 26 ) );
            int numA = (int)(Math.random()*9);
            int numB = (int)(Math.random()*9);

            return String.valueOf(val).toUpperCase() + numA +""+numB;
        }
    }

    static class Consumer implements Runnable{
        @Override
        public void run()  {
            while (true){
                synchronized (queue){
                    Ticket ticket = queue.poll();
                    System.out.println("消费一个Ticket:"+ticket.toString());
                    System.out.println("剩余："+queue.size());
                    if(queue.size() == 0){
                        break;
                    }
                }

            }

        }
    }
}

class Ticket{
    //票务代码
    private String id = null;
    //启程
    private String originDirection = null;
    //终点站
    private String destination = null;
    //乘车时间
    private String ridingtime = null;
    //车次
    private String trainNUm = null;
    //座位号
    private String seatNum = null;
    //票价
    private Double price = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginDirection() {
        return originDirection;
    }

    public void setOriginDirection(String originDirection) {
        this.originDirection = originDirection;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getRidingtime() {
        return ridingtime;
    }

    public void setRidingtime(String ridingtime) {
        this.ridingtime = ridingtime;
    }

    public String getTrainNUm() {
        return trainNUm;
    }

    public void setTrainNUm(String trainNUm) {
        this.trainNUm = trainNUm;
    }

    public String getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(String seatNum) {
        this.seatNum = seatNum;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", originDirection='" + originDirection + '\'' +
                ", destination='" + destination + '\'' +
                ", ridingtime=" + ridingtime +
                ", trainNUm='" + trainNUm + '\'' +
                ", seatNum='" + seatNum + '\'' +
                ", price=" + price +
                '}';
    }
}