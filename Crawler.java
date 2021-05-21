
import java.io.*;
import java.net.*;
import java.util.*;

public class Crawler {

    public static final String LINK_PREFIX = "<a href=" + '"';

    private static LinkedList<URLDepthPair> oldURL = new LinkedList<URLDepthPair>();
    private static LinkedList<URLDepthPair> newURL = new LinkedList<URLDepthPair>();
    private static int threadNum;
    public static void showResult(LinkedList<URLDepthPair> list){
        for (URLDepthPair udp : list){
            System.out.println(udp.toString());
        }
    }


    public static void main(String[] args) {
        try {
            URLDepthPair firstURLDepthPair = new URLDepthPair(URLDepthPair.URL_PREFIX + args[0] + "/",
                    Integer.parseInt(args[1]));
            threadNum = Integer.parseInt(args[2]);
            URLPool urlPool = new URLPool(firstURLDepthPair.getDepth());
            firstURLDepthPair.setDepth(0);
            urlPool.addPair(firstURLDepthPair);
            for (int i = 0; i < threadNum; i++) {
                CrawlerTask c = new CrawlerTask(urlPool);
                Thread thread = new Thread(c);
                thread.start();
            }
            while (urlPool.getWait()!= threadNum){
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    System.out.println(e.getMessage());
                }
            }
            showResult(urlPool.getAllOldPair());
            System.exit(1);
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("usage: java crawler " + " URL " + " Depth " + " ThreadNum ");
        }
    }

}
