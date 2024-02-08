package id.co.workers.Litera.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.workers.Litera.domain.entities.ArticleEntity;
import id.co.workers.Litera.services.ArticleService;
import java.util.ArrayList;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class ArticleController {

  private ArticleService articleService;
  Logger logger = LoggerFactory.getLogger(ArticleController.class);

  public ArticleController(ArticleService articleService) {
    this.articleService = articleService;
  }

  @GetMapping(path = "/articles")
  public ArticleEntity[] getAllArticles(
    @RequestHeader Map<String, String> header
  ) throws JsonMappingException, JsonProcessingException {
    //Get all articles from Gmail API
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", header.get("access-token"));
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    String apiKey = System.getenv("API_KEY");
    String url = "https://gmail.googleapis.com/gmail/v1/users/me/messages";
    String urlTemplate = UriComponentsBuilder
      .fromHttpUrl(url)
      .queryParam("maxResults", 10)
      .queryParam("key", apiKey)
      .encode()
      .toUriString();

    ResponseEntity<String> response = restTemplate.exchange(
      urlTemplate,
      HttpMethod.GET,
      requestEntity,
      String.class
    );

    //Response to JSON
    ObjectMapper mapper = new ObjectMapper();
    JsonNode json = mapper.readTree(response.getBody());

    //Process email to Article
    ArrayList<ArticleEntity> articles = new ArrayList<ArticleEntity>();

    json
      .get("messages")
      .forEach(message -> {
        String id = message.get("id").asText();
        String messageUrl =
          "https://gmail.googleapis.com/gmail/v1/users/me/messages/" +
          id +
          "?key=" +
          apiKey;

        ResponseEntity<String> messageResponse = restTemplate.exchange(
          messageUrl,
          HttpMethod.GET,
          requestEntity,
          String.class
        );

        try {
          ArticleEntity articleEntity = articleService.createArticle(
            messageResponse.getBody()
          );

          articles.add(articleEntity);
        } catch (JsonMappingException e) {
          e.printStackTrace();
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      });

    return articles.toArray(new ArticleEntity[0]);
  }
}
