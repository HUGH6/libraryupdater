package main;

public enum ExecutionMode {
    DEFAULT("default"),
    GENERATE_VALIDATE("generate_validate");

    private String name;

    ExecutionMode(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
