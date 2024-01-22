package id.co.workers.Litera.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

  @GetMapping(path = "/sample")
  public String sample() {
    return "Hello World";
  }
}
