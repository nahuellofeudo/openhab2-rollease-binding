package com.nahuellofeudo.rolleasecontroller.response.parsing;

import java.io.IOException;
import java.io.InputStream;

public interface MessageParser<T> {
  public T parse() throws IOException;
}
