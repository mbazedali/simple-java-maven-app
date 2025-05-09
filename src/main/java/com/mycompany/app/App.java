package com.mycompany.app;

/**
 * A simple application that prints a greeting message.
 */
public final class App {

    /**
     * The greeting message to be printed.
     */
    private static final String MESSAGE = "Hello World!";

    /**
     * Default constructor.
     */
    public App() {
        // No-op
    }

    /**
     * The application's entry point.
     *
     * @param args the command-line arguments
     */
    public static void main(final String[] args) {
        System.out.println(MESSAGE);
    }

    /**
     * Returns the greeting message.
     *
     * @return the greeting message
     */
    public String getMessage() {
        return MESSAGE;
    }
}
