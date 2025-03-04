package org.example.exceptions;

import java.io.IOException;

public class IncorrectSyntax extends Exception {
    public IncorrectSyntax(String message) {
        super(message);
    }
}
