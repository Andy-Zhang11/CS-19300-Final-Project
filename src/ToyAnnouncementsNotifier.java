import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToyAnnouncementsNotifier {
  public void main() {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    LocalTime run = LocalTime.of(20, 0, 0);
    long initialDelay = Duration.between(LocalTime.now(), run).getSeconds();

    if (initialDelay < 0) {
        initialDelay += TimeUnit.DAYS.toSeconds(1);
    }

    scheduler.scheduleAtFixedRate(() -> {

      try {
        String[] keywords = {"S.H. Figurarts", "The Robot Spirits", "Metal Robot Spirits", "DX Chogokin", "Hi-Metal R", "Toyrise", "Legioss", "Adamas", "Synergenex"};
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        DateTimeFormatter formatterTakara = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        DateTimeFormatter formatterBandai = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("https://tamashiiweb.com/?wovn=en"))
          .GET()
          .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Pattern bandaiLinkandDate = Pattern.compile("(?is)<li\\b[^>]*class\\s*=\\s*[\"'][^\"']*kvSlide__item[^\"']*[\"'][^>]*>" +
                                                    ".*?<a\\b[^>]*href\\s*=\\s*[\"']([^\"']+)[\"'][^>]*>" +
                                                    ".*?<p\\b[^>]*class\\s*=\\s*[\"']kvSlide__brand[\"'][^>]*>" +
                                                    "(?is)<time\\b[^>]*datetime\\s*=\\s*[\"'](\\d{4}-\\d{2}-\\d{2})[\"'][^>]*>" +
                                                    "(?:(?!</li>).)*?</li>",
                                                    Pattern.CASE_INSENSITIVE);
        Matcher bandaiMatcher = bandaiLinkandDate.matcher(response.body());
        while (bandaiMatcher.find()) {
          LocalDate bandaiDate = LocalDate.parse(bandaiMatcher.group(4), formatterBandai);
          if (bandaiDate.equals(today)) {
            String bandaiBrand = bandaiMatcher.group(3);
            for (String keyword : keywords) {
              if (bandaiBrand.toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(bandaiMatcher.group(2));
                break;
              }
            }
          }
        }

        HttpRequest request2 = HttpRequest.newBuilder()
          .uri(URI.create("https://www.takaratomy.co.jp/products/tsparkofficial/"))
          .GET()
          .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Pattern takaraLinkandDate = Pattern.compile("(?is)<a\\b[^>]*href\\s*=\\s*[\"']([^\"']+)[\"'][^>]*>" + 
                                                    "\\s*<a\\s+href=\"([^\"]+)\"[^>]*>" +
                                                    "<p\\s+class=\"date\">\\s*(\\d{4}\\.\\d{2}\\.\\d{2})\\s*</p>" +
                                                    "(.*?)</a>",
                                                    Pattern.CASE_INSENSITIVE);
        Matcher takaraMatcher = takaraLinkandDate.matcher(response2.body());
        while (takaraMatcher.find()) {
          LocalDate takaraDate = LocalDate.parse(takaraMatcher.group(3), formatterTakara);
          if (takaraDate.equals(today)) {
            String takaraLink = takaraMatcher.group(2);
            for (String keyword : keywords) {
              if (takaraLink.toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(takaraLink);
                break;
              }
            }
          }
        }

      } catch (IOException | InterruptedException e) {
        e.getStackTrace();
      }
        
    }, initialDelay, 1, TimeUnit.DAYS);
  }
}