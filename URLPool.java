import java.util.LinkedList;

public class URLPool {

    private LinkedList<URLDepthPair> oldURL;
    private LinkedList<URLDepthPair> newURL;
    private int maxDepth;
    private int cWait;

    public URLPool(int maxDepth){
        this.maxDepth = maxDepth;
        this.oldURL = new LinkedList<URLDepthPair>();
        this.newURL = new LinkedList<URLDepthPair>();
        this.cWait = 0;
    }

    public LinkedList<URLDepthPair> getAllNewPair() {
       return this.newURL;
    }
    public synchronized LinkedList<URLDepthPair> getAllOldPair(){
        return this.oldURL;
    }
    public synchronized URLDepthPair getAndRemoveFirstNewURL(){
        while (newURL.size() == 0) {
            cWait++;
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            cWait--;
        }
        URLDepthPair url = newURL.removeFirst();
        return url;
    }
    public synchronized int getMaxDepth(){
        return this.maxDepth;
    }

    public synchronized void addPair(URLDepthPair pair) {
        if(!URLDepthPair.containsURL(pair, oldURL)) {
            oldURL.add(pair);
            if (pair.getDepth() <= maxDepth) {
                newURL.add(pair);
                notify();
            }
        }
    }
    public synchronized int getWait(){
        return cWait;
    }
}
