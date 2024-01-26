package id.co.workers.Litera.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.springframework.http.HttpStatus;

public class GoogleOAuthFilter implements Filter {

  @Override
  public void doFilter(
    ServletRequest servletRequest,
    ServletResponse servletResponse,
    FilterChain chain
  ) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    // Get the id token from the request
    String idToken = request.getHeader("Authorization");

    // Verify the id token
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
      new NetHttpTransport(),
      GsonFactory.getDefaultInstance()
    )
      .setAudience(
        Collections.singletonList(
          "129567181158-pf0hbucssngqars18hr2al28uma6vrf8.apps.googleusercontent.com"
        )
      )
      .build();

    GoogleIdToken token;

    try {
      token = verifier.verify(idToken.split(" ")[1]);
      if (token != null) {
        chain.doFilter(servletRequest, servletResponse);
      } else {
        CustomErrorResponse errorResponse = new CustomErrorResponse(
          HttpStatus.UNAUTHORIZED.value(),
          "Unauthorized ID Token"
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(convertObjectToJson(errorResponse));
      }
    } catch (GeneralSecurityException e) {
      CustomErrorResponse errorResponse = new CustomErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal server error " + e.getMessage()
      );

      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.getWriter().write(convertObjectToJson(errorResponse));
    } catch (IOException e) {
      CustomErrorResponse errorResponse = new CustomErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal server error: " + e.getMessage()
      );

      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.getWriter().write(convertObjectToJson(errorResponse));
    }
  }

  public String convertObjectToJson(Object object)
    throws JsonProcessingException {
    if (object == null) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(object);
  }
}
