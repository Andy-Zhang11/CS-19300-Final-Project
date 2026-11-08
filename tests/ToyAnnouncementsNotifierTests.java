import java.io.IOException;
import java.net.URI;
import java.net.http.*;

public class ToyAnnouncementsNotifierTests {
  public void main() {
    try {

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://tamashiiweb.com/topics/items/?wovn=en"))
        .GET()
        .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      System.out.println(response.body());

      HttpClient client2 = HttpClient.newHttpClient();
      HttpRequest request2 = HttpRequest.newBuilder()
        .uri(URI.create("https://www.takaratomy.co.jp/products/tsparkofficial/"))
        .GET()
        .build();
      HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());
      System.out.println(response2.body());

    } catch (IOException | InterruptedException e) {
      e.getStackTrace();
    }
  }
}
