package ru.kuchumov.appComponents.utilites;

import ru.kuchumov.appContext.annotations.CustomAutowired;
import ru.kuchumov.appContext.components.CustomComponent;

import java.util.Scanner;

public class Parser implements CustomComponent {
    private final ScannerContainer scannerContainer;
    @CustomAutowired
    public Parser(ScannerContainer scannerContainer) {
        this.scannerContainer = scannerContainer;
    }
    public boolean checkAnswer(String answer, String rightAnswer, boolean fullyCheck) {
        if (fullyCheck) {
            return answer.equals(rightAnswer);
        } else {
            return rightAnswer.contains(answer) && !answer.isEmpty();
        }
    }

    public boolean yesOrNo() {
        Scanner scanner = scannerContainer.getScanner();
        while (true) {
            String answer = scanner.nextLine().toLowerCase();
            if (answer.equals("y") || answer.equals("д")) {
                return true;
            } else if (answer.equals("n") || answer.equals("н")) {
                return false;
            } else {
                System.out.print("Введите (y/n): ");
            }
        }
    }
}
