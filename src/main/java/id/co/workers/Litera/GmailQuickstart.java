package id.co.workers.Litera;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.oauth2.Oauth2Scopes;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

public class GmailQuickstart {

  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  private static final String TOKENS_DIRECTORY_PATH = "tokens";

  private static final List<String> SCOPES = Arrays.asList(
    GmailScopes.GMAIL_LABELS,
    GmailScopes.GMAIL_READONLY,
    GmailScopes.GMAIL_METADATA,
    Oauth2Scopes.USERINFO_PROFILE
  );

  private static Credential getCredentials(
    final NetHttpTransport HTTP_TRANSPORT
  ) throws IOException {
    // Load client secrets.
    File file = ResourceUtils.getFile("classpath:credentials.json");
    InputStream in = new FileInputStream(file);
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
      JSON_FACTORY,
      new InputStreamReader(in)
    );

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
      HTTP_TRANSPORT,
      JSON_FACTORY,
      clientSecrets,
      SCOPES
    )
      .setDataStoreFactory(
        new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH))
      )
      .setAccessType("offline")
      .build();

    LocalServerReceiver receiver = new LocalServerReceiver.Builder()
      .setPort(8888)
      .build();

    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }

  public static String getProfile(String... args)
    throws IOException, GeneralSecurityException {
    // Build a new authorized API client service.
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    final Credential credential = getCredentials(HTTP_TRANSPORT);

    // Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
    //   .setApplicationName(APPLICATION_NAME)
    //   .build();

    // Fetch using HTTP_TRANSPORT and print the user profile via this endpoint https://www.googleapis.com/oauth2/v2/userinfo?access_token=
    RestTemplate restTemplate = new RestTemplate();
    String url =
      "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" +
      credential.getAccessToken();

    String result = restTemplate.getForObject(url, String.class);

    //return result as JSON from GET mapping
    return result;
  }
}
