package ru.kuchumov.appComponents.utilites.osInitializer;

import java.io.Console;
import java.nio.charset.Charset;

public interface OSStrategy {
    Charset getCharset();
    String getGreen();
    String getRed();
    String getNC();
}


class LinuxOrPSStrategy implements OSStrategy {
    private final Charset charset;

    public LinuxOrPSStrategy() {
        Console console = System.console();
        if (console == null) {
            this.charset = Charset.defaultCharset();
        } else {
            this.charset = console.charset();
        }
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public String getGreen() {
        return "\033[0;32m";
    }

    @Override
    public String getRed() {
        return "\033[0;31m";
    }

    @Override
    public String getNC() {
        return "\033[0m";
    }
}

class WindowsCMDStrategy implements OSStrategy {
    private final Charset charset;

    public WindowsCMDStrategy() {
        Console console = System.console();
        if (console == null) {
            this.charset = Charset.defaultCharset();
        } else {
            this.charset = console.charset();
        }
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public String getGreen() {
        return "";
    }

    @Override
    public String getRed() {
        return "";
    }

    @Override
    public String getNC() {
        return "";
    }
}