package org.example.factories.interfaces;

import java.io.IOException;

public interface Factory<Output, Input> {
  Output create(Input input) throws IOException;
}
