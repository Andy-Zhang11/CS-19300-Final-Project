import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Path;
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
  public static void main(String[] args) {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    LocalTime run = LocalTime.of(20, 0, 0, 0);
    long initialDelay = Duration.between(LocalTime.now(ZoneId.systemDefault()), run).toNanos();

    if (initialDelay < 0) {
        initialDelay += TimeUnit.DAYS.toNanos(1);
    }

    scheduler.scheduleAtFixedRate(() -> {
      try {
        String[] keywords = {"Origin of Valkyrie", "DX Chogokin", "Hi-Metal R", "Legioss", "Toyrise", "Adamas", "Synergenex"};
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        DateTimeFormatter formatterTakara = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        DateTimeFormatter formatterBandai = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("https://tamashiiweb.com/?wovn=en&sa=JAPAN"))
          .GET()
          .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        /* Second datetime (release date) in the pattern is included to distinguish between items just put up for
         * preorder or items already released and prioritize the former.
         * Example: Released Product (2026-11-20) Product on preorder (2026-11).
         */
        Pattern bandaiLinkandDate = Pattern.compile("<li class=\"productList__item[^\\\"]*\">[\\s\\S]*?" +
                                                    "<a\\s+href=\"([^\"]+)\"[\\s\\S]*?" +
                                                    "<p class=\"productList__brand\">[\\s\\S]*?>([^<]+)</p>[\\s\\S]*?" +
                                                    "<p class=\"productList__date\">[\\s\\S]*?" +
                                                    "<time datetime=\"([^\"]+)\">[\\s\\S]*?</time>[\\s\\S]*?" +
                                                    "<time datetime=\"([^\"]+)\">" +
                                                    "[\\s\\S]*?</li>",
                                                    Pattern.CASE_INSENSITIVE);
        Matcher bandaiMatcher = bandaiLinkandDate.matcher(response.body());
        while (bandaiMatcher.find()) {
          LocalDate bandaiDate = LocalDate.parse(bandaiMatcher.group(3), formatterBandai);
          if (bandaiDate.equals(today)) {
            String bandaiBrand = bandaiMatcher.group(2);
            for (String keyword : keywords) {
              if (bandaiBrand.toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println("NEW BANDAI PRODUCT LISTING!!!");
                try (PrintWriter pw = new PrintWriter(new FileWriter(Path.of("docs").resolve("announcements.txt").toFile(), true))) {
                  pw.println(today.toString() + ": " + URI.create("https://tamashiiweb.com/?wovn=en&sa=JAPAN").resolve(bandaiMatcher.group(1)).toString());
                } catch (FileNotFoundException e) {
                  e.getStackTrace();
                }
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
        Pattern takaraLinkandDate = Pattern.compile("(?is)<div\\b[^>]*class\\s*=\\s*[\"'][^\"']*\\bslide\\b[^\"']*[\"'][^>]*>" +
                                                    "\\s*<a\\b[^>]*href\\s*=\\s*[\"']([^\"']+)[\"'][^>]*>" +
                                                    "\\s*<p\\s+class\\s*=\\s*[\"']date[\"']\\s*>\\s*(\\d{4}\\.\\d{2}\\.\\d{2})\\s*</p>" +
                                                    ".*?</a>",
                                                    Pattern.CASE_INSENSITIVE);
        Matcher takaraMatcher = takaraLinkandDate.matcher(response2.body());
        while (takaraMatcher.find()) {
          LocalDate takaraDate = LocalDate.parse(takaraMatcher.group(2), formatterTakara);
          if (takaraDate.equals(today)) {
            String takaraLink = takaraMatcher.group(1);
            for (String keyword : keywords) {
              if (takaraLink.toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println("NEW TAKARA PRODUCT LISTING!!!");
                try (PrintWriter pw = new PrintWriter(new FileWriter(Path.of("docs").resolve("announcements.txt").toFile(), true))) {
                  pw.println(today.toString() + ": " + URI.create("https://www.takaratomy.co.jp/products/tsparkofficial/").resolve(takaraLink).toString());
                } catch (FileNotFoundException e) {
                  e.getStackTrace();
                }
                break;
              }
            }
          }
        }

        /* Threezero's API has no publication date for any new product listings, thus requiring an alternate method keeping track of
         * hyperlinks just added and hyperlinks already present on the site.
         */
        HttpRequest request3 = HttpRequest.newBuilder()
          .uri(URI.create("https://www.threezerohk.com/shop/category/collections-robotech-77"))
          .GET()
          .build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Pattern threeZeroPattern = Pattern.compile("href=\"([^\"]*/shop/robotech-robo-dou-robotech-[^\"]+)\"",
                                                   Pattern.CASE_INSENSITIVE);
        Matcher threeZeroMatcher = threeZeroPattern.matcher(response3.body());
        while (threeZeroMatcher.find()) {
          boolean broken = false;
          String threeZeroLink = threeZeroMatcher.group(1);

          try (BufferedReader br = new BufferedReader(new FileReader(Path.of("docs").resolve("threezerohref.txt").toFile()))) {
            String listedLink = br.readLine();
            while (listedLink != null) {
              if (threeZeroLink.equalsIgnoreCase(listedLink)) {
                broken = true;
                break;
              }
              listedLink = br.readLine();
            }
          } catch (FileNotFoundException e) {
            e.getStackTrace();
          }

          try (PrintWriter pw1 = new PrintWriter(new FileWriter(Path.of("docs").resolve("threezerohref.txt").toFile(), true));
               PrintWriter pw2 = new PrintWriter(new FileWriter(Path.of("docs").resolve("announcements.txt").toFile(), true))) {
            if (broken == false) {
              System.out.println("NEW THREEZERO PRODUCT LISTING!!!");
              pw1.println(threeZeroLink);
              pw2.println(today.toString() + ": " + URI.create("https://www.threezerohk.com/shop/category/collections-robotech-77").resolve(threeZeroLink).toString());
            }
          } catch (FileNotFoundException e) {
            e.getStackTrace();
          }

        }

      } catch (IOException | InterruptedException e) {
        e.getStackTrace();
      }
        
    }, initialDelay, TimeUnit.DAYS.toNanos(1), TimeUnit.NANOSECONDS);
  }
}
