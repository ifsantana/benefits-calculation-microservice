package org.example.requests.interfaces;

import okhttp3.HttpUrl;
import org.example.factories.interfaces.Factory;

public interface HttpClient extends Factory<HttpUrl, String[]> {
  @Override
  HttpUrl create(String... args);
}
