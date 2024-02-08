package id.co.workers.Litera.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.workers.Litera.domain.entities.ArticleEntity;
import id.co.workers.Litera.domain.entities.AuthorEntity;
import id.co.workers.Litera.services.ArticleService;
import java.util.ArrayList;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements ArticleService {

  @Override
  public ArticleEntity createArticle(String message)
    throws JsonMappingException, JsonProcessingException {
    //Marshall message
    ObjectMapper mapper = new ObjectMapper();
    JsonNode messageJson = mapper.readTree(message);

    //
    //Build ArticleEntity
    //

    //1. Build AuthorEntity
    String headerFrom = getHeaderByName(
      messageJson.get("payload").get("headers"),
      "From"
    )
      .get("value")
      .asText();

    AuthorEntity authorEntity = extractNameEmail(headerFrom);

    //2. Title
    String headerSubject = getHeaderByName(
      messageJson.get("payload").get("headers"),
      "Subject"
    )
      .get("value")
      .asText();

    //3. Content
    ArrayList<String> content = new ArrayList<String>();

    String mimeType = messageJson
      .get("payload")
      .get("mimeType")
      .asText()
      .split("/")[0];

    if (mimeType.equals("multipart")) {
      //Multipart
      JsonNode partJsonNode = messageJson.get("payload").get("parts");

      partJsonNode.forEach(part -> {
        if (
          part.get("mimeType").asText().equals("text/plain") ||
          part.get("mimeType").asText().equals("text/html")
        ) {
          JsonNode partBody = part.get("body").get("data");
          if (partBody != null) {
            String partBodyString = partBody.asText();
            String partBodyDecoded = new String(
              Base64.getUrlDecoder().decode(partBodyString)
            );
            content.add(partBodyDecoded);
          }
        }
      });
    } else if (mimeType.equals("text")) {
      String bodyData = messageJson
        .get("payload")
        .get("body")
        .get("data")
        .asText();
      String bodyDecoded = new String(Base64.getUrlDecoder().decode(bodyData));
      content.add(bodyDecoded);
    }

    //4. SentAt
    // LocalDateTime receivedDateTime = null;
    // String headerReceived = getHeaderByName(
    //   messageJson.get("payload").get("headers"),
    //   "Received"
    // )
    //   .get("value")
    //   .asText();

    // // Define a pattern to match the date string
    // Pattern pattern = Pattern.compile(
    //   "(\\b\\w+, \\d{1,2} \\w{3} \\d{4} \\d{2}:\\d{2}:\\d{2} [-+]\\d{4}\\b)"
    // );

    // // Find the date string using the pattern
    // Matcher matcher = pattern.matcher(headerReceived);
    // if (matcher.find()) {
    //   String cleanedDateString = matcher.group(1);

    //   // Parse the cleaned date string
    //   DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
    //     "EEE, dd MMM yyyy HH:mm:ss Z"
    //   );
    //   receivedDateTime = LocalDateTime.parse(cleanedDateString, formatter);
    // }

    ArticleEntity article = ArticleEntity
      .builder()
      .id(messageJson.get("id").asText())
      .author(authorEntity)
      .title(headerSubject)
      .content(content.toArray(new String[0]))
      .build();

    return article;
  }

  private static JsonNode getHeaderByName(
    JsonNode headersArray,
    String targetName
  ) {
    for (JsonNode headerNode : headersArray) {
      if (
        headerNode.has("name") &&
        headerNode.get("name").asText().equals(targetName)
      ) {
        return headerNode;
      }
    }
    return null; // Return null if header not found
  }

  private static AuthorEntity extractNameEmail(String fromHeaderValue) {
    int openBracketIndex = fromHeaderValue.indexOf("<");
    int closeBracketIndex = fromHeaderValue.indexOf(">");

    if (openBracketIndex == -1 || closeBracketIndex == -1) {
      return AuthorEntity.builder().name(fromHeaderValue).build();
    } else {
      String name = fromHeaderValue.substring(0, openBracketIndex).trim();
      String email = fromHeaderValue
        .substring(openBracketIndex + 1, closeBracketIndex)
        .trim();
      return AuthorEntity.builder().name(name).email(email).build();
    }
  }
}
