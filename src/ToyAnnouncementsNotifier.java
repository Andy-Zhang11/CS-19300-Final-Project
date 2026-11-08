import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.net.http.*;
import java.net.URI;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

public class ToyAnnouncementsNotifier {
  public void main() {
    try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor() {
      scheduler.scheduleAtFixedRate(() -> {
        
      }, 0, 1, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
} 
