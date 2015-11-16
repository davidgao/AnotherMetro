package edu.fudan.davidgao.anothermetro.tools;

public class ToolException extends Exception {
    public ToolException() {
        super();
    }

    public ToolException(String message) {
        super(message);
    }

    public ToolException(Throwable cause) {
        super(cause);
    }

    public ToolException(String message, Throwable cause) {
        super(message, cause);
    }
}