package id.co.workers.Litera.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import id.co.workers.Litera.domain.entities.ArticleEntity;

public interface ArticleService {
  ArticleEntity createArticle(String message)
    throws JsonMappingException, JsonProcessingException;
}
