import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;

public class CrawlerTask implements Runnable {

    URLPool urlPool;

    public CrawlerTask(URLPool urlPool){
        this.urlPool = urlPool;
    }

    public static void request(PrintWriter out, URLDepthPair udp) throws MalformedURLException {
        out.println("GET " + udp.getPath() + " HTTP/1.1");
        out.println("Host: " + udp.getHost());
        out.println("Connection: close");
        out.println();
        out.flush();
    }

    @Override
    public void run() {
        while (true){
            URLDepthPair udp = urlPool.getAndRemoveFirstNewURL();
            if (udp.getDepth() < urlPool.getMaxDepth()){
                try{
                    Socket socket = new Socket(udp.getHost(), 80);
                    socket.setSoTimeout(1000);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    request(out, udp);
                    String line;
                    try {
                        while ((line = in.readLine()) != null) {
                            while (line.contains(Crawler.LINK_PREFIX + URLDepthPair.URL_PREFIX)) {
                                StringBuilder currentLink = new StringBuilder();
                                int i = line.indexOf(URLDepthPair.URL_PREFIX);
                                while (line.charAt(i) != '"') {
                                    currentLink.append(line.charAt(i));
                                    i++;
                                }
                                if (!currentLink.toString().isEmpty()){
                                    line = line.substring(i);
                                }
                                URLDepthPair newUDP = new URLDepthPair(currentLink.toString(), udp.getDepth() + 1);
                                urlPool.addPair(newUDP);
                            }
                        }
                    }catch (IOException | StringIndexOutOfBoundsException e){
                        continue;
                    }
                    socket.close();
                }catch (Exception e){
                    continue;
                }
            }
        }
    }
}
