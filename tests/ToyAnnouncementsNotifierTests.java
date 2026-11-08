import java.io.IOException;

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

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://www.takaratomy.co.jp/products/tsparkofficial/"))
        .GET()
        .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      System.out.println(response.body());

    } catch (IOException e) {
      e.getStackTrace();
    } catch (InterruptedException n) {
      e.getStackTrace();
    }
  }
}
