package org.example.endpoints;

import com.sun.net.httpserver.HttpHandler;

public interface Endpoint extends HttpHandler {

  /**
   *
   * @return
   */
  String getEndpointURN();
}
