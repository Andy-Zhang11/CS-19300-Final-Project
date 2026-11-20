import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ToyAnnouncementsNotifier {
  public void main() {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    scheduler.scheduleAtFixedRate(() -> {

      try {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("https://tamashiiweb.com/?wovn=en"))
          .GET()
          .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder()
          .uri(URI.create("https://www.takaratomy.co.jp/products/tsparkofficial/"))
          .GET()
          .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

      } catch (IOException | InterruptedException e) {
        e.getStackTrace();
      }
        
    }, 0, 1, TimeUnit.DAYS);
  }
} 
