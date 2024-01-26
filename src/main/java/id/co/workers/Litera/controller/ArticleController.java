package id.co.workers.Litera.controller;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ArticleController {

  @GetMapping(path = "/articles")
  public String getAllArticles(@RequestHeader Map<String, String> header) {
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", header.get("access-token"));

    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
    String apiKey = System.getenv("API_KEY");
    String url =
      "https://gmail.googleapis.com/gmail/v1/users/me/messages?key=" + apiKey;

    ResponseEntity<String> response = restTemplate.exchange(
      url,
      HttpMethod.GET,
      requestEntity,
      String.class
    );

    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      return "Error";
    }
  }
}
