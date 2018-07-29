package com.nahuellofeudo.rolleasecontroller.response.parsing;

import java.io.IOException;

public interface MessageParser {
    public Integer[] getSignature();

    public void parse(Integer[] bytes) throws IOException;

    public boolean canParse(Integer[] type);

}
