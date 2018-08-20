package com.nahuellofeudo.rolleasecontroller.response.parsing;

import java.io.IOException;

/**
 * All classes that parse messages from the hub implement this interface
 * 
 * @author Nahuel Lofeudo
 *
 */
public interface MessageParser {
    public Integer[] getSignature();

    public void parse(Integer[] bytes) throws IOException;

    public boolean canParse(Integer[] type);

}
