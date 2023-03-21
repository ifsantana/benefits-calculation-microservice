package org.example.endpoints;

import com.sun.net.httpserver.HttpHandler;

public interface Endpoint extends HttpHandler {
  String getEndpointURN();
}
