package org.example.endpoints;

import com.sun.net.httpserver.HttpHandler;
import java.util.HashMap;

public interface Endpoint extends HttpHandler {

  /**
   * Public method to access the static class property that represents the URN for the endpoint that should be published by webserver.
   * @return String that represents the URN for the endpoint that should be published by webserver.
   */
  String getEndpointURN();

  /**
   *  Extract query parameters from URL to HashMap.
   * @param - query
   * @return {@link HashMap<String, String>} containing query string parameters extracted from URL.
   */
  default HashMap<String, String> queryToMap(String query) {
    if(query == null) {
      return new HashMap<>();
    }
    HashMap<String, String> result = new HashMap<>();
    for (String param : query.split("&")) {
      String[] entry = param.split("=");
      if (entry.length > 1) {
        result.put(entry[0], entry[1]);
      }else{
        result.put(entry[0], "");
      }
    }
    return result;
  }
}
