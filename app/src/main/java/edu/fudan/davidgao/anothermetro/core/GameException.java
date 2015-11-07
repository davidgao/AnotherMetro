package edu.fudan.davidgao.anothermetro.core;

public class GameException extends Exception {
    public GameException() {
        super();
    }

    public GameException(String message) {
        super(message);
    }

    public GameException(Throwable cause) {
        super(cause);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
