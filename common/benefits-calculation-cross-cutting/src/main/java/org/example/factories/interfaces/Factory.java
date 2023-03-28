package org.example.factories.interfaces;

import java.io.IOException;

public interface Factory<Output, Input> {

  /**
   * This interface implements the abstract factory pattern enabling the possibility to create
   * objects (instances) based on a specific input.
   *
   * @param - input - is a generic type that must be defined on the concrete factory and will be
   *          used to create the output object.
   * @return - instance of the object defined on the concrete factory.
   * @throws IOException
   */
  Output create(Input input) throws IOException;
}
