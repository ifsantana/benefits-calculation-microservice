package org.example.endpoints;

import com.sun.net.httpserver.HttpHandler;

public interface Endpoint extends HttpHandler {

  /**
   * Public method to access the static class property that represents the URN for the endpoint that should be published by webserver.
   * @return String that represents the URN for the endpoint that should be published by webserver.
   */
  String getEndpointURN();
}
