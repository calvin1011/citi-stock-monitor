package citistocksystem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class App {
    private static final String BASE_URL = "https://finance.yahoo.com/quote/";
    private static final String STOCK_SYMBOL = "^DJI";
    private static final int FETCH_INTERVAL_MS = 5000; // 5 seconds

    public static void main(String[] args) {
        Queue<StockData> stockQueue = new LinkedList<>();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    // Encode the stock symbol
                    String encodedSymbol = URLEncoder.encode(STOCK_SYMBOL, "UTF-8");
                    String url = BASE_URL + encodedSymbol + "?p=" + encodedSymbol;

                    Document doc = Jsoup.connect(url).get();
                    String price = doc.select("td[data-test=OPEN-value]").text();
                    StockData stockData = new StockData(price, new Timestamp(System.currentTimeMillis()));

                    stockQueue.offer(stockData);

                    // Print the fetched stock data
                    System.out.println("Fetched: " + stockData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, FETCH_INTERVAL_MS);

        // Keep the application running to continue fetching data
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class StockData {
    private String price;
    private Timestamp timestamp;

    public StockData(String price, Timestamp timestamp) {
        this.price = price;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "StockData{" +
                "price='" + price + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
