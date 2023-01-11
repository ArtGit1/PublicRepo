package ru.kuchumov.appComponents.utilites;

import ru.kuchumov.appComponents.utilites.osInitializer.OSInitializer;
import ru.kuchumov.appContext.annotations.CustomAutowired;
import ru.kuchumov.appContext.components.CustomComponent;

import java.nio.charset.Charset;
import java.util.Scanner;

public class ScannerContainer implements CustomComponent {
    private final Scanner scanner;
    private boolean openFlag;

    @CustomAutowired
    public ScannerContainer(OSInitializer osInitializer) {
        Charset charset = osInitializer.getOSContext().getOSStrategy().getCharset();
        scanner = new Scanner(System.in, charset);
        openFlag = true;
    }

    public Scanner getScanner() {
        if (scanner == null) {
            throw new RuntimeException("Сначала создайте объект класса ScannerContainer");
        }
        if (!openFlag) {
            throw new RuntimeException("Сканер был где-то до этого закрыт. Повторно открыть невозможно");
        }
        return scanner;
    }

    public void closeScanner() {
        if (scanner != null && openFlag) {
            scanner.close();
            openFlag = false;
        }
    }
}
