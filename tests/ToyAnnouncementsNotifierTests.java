import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ToyAnnouncementsNotifierTests {
  public void main() {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    scheduler.scheduleAtFixedRate(() -> {

      try {
        String[] keywords = {"S.H. Figurarts", "The Robot Spirits", "Metal Robot Spirits", "DX Chogokin", "Hi-Metal R", "Toyrise", "Legioss", "Adamas", "Synergenex"};
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Tokyo"));
        DateTimeFormatter formatterTakara = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        DateTimeFormatter formatterBandai = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("https://tamashiiweb.com/?wovn=en"))
          .GET()
          .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        HttpRequest request2 = HttpRequest.newBuilder()
          .uri(URI.create("https://www.takaratomy.co.jp/products/tsparkofficial/"))
          .GET()
          .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        System.out.println(response2.body());

      } catch (IOException | InterruptedException e) {
        e.getStackTrace();
      }
        
    }, 0, 1, TimeUnit.DAYS);
  }
} 
