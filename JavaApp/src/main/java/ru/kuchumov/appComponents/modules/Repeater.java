package ru.kuchumov.appComponents.modules;

import ru.kuchumov.appComponents.utilites.Parser;
import ru.kuchumov.appComponents.utilites.ScannerContainer;
import ru.kuchumov.appComponents.utilites.osInitializer.OSInitializer;
import ru.kuchumov.appComponents.utilites.osInitializer.OSStrategy;
import ru.kuchumov.appContext.AutoContext;
import ru.kuchumov.appContext.AutowiredContext;
import ru.kuchumov.appContext.annotations.CustomAutowired;
import ru.kuchumov.appContext.components.CustomComponent;

import java.util.*;

public class Repeater implements CustomComponent {
    private final AutoContext context;
    private final Parser parser;
    private final LanguagerContainer languagerContainer;
    private final Verber verber;
    private final ScannerContainer scannerContainer;
    private String startMessage;
    private String preAnswerMessage;
    private boolean fullyCheck;
    private boolean isItHardWordsOrVerbs;
    private final String GREEN;
    private final String RED;
    private final String NC;

    @CustomAutowired
    public Repeater(AutowiredContext autowiredContext, OSInitializer osInitializer, Parser parser, ScannerContainer scannerContainer,
                    LanguagerContainer languagerContainer, Verber verber) {
        context = autowiredContext;
        this.parser = parser;
        this.scannerContainer = scannerContainer;
        this.languagerContainer = languagerContainer;
        this.verber = verber;
        OSStrategy osStrategy = osInitializer.getOSContext().getOSStrategy();
        GREEN = osStrategy.getGreen();
        RED = osStrategy.getRed();
        NC = osStrategy.getNC();
    }

    public Map<String, String> repeat(ExecuteType executeType) {
        setup(executeType);
        Map<String, String> vocabulary = getVocabulary(executeType);
        if (vocabulary.isEmpty()) {
            System.out.println("Слов не найдено. Сделайте что-нибудь");
            return null;
        }
        return repeatingTemplate(vocabulary, executeType);
    }

    public Map<String, String> repeat(ExecuteType executeType, Map<String, String> vocabulary) {
        setup(executeType);
        return repeatingTemplate(vocabulary, executeType);
    }

    private Map<String, String> repeatingTemplate(Map<String, String> vocabulary, ExecuteType executeType) {
        Map<String, String> mistakes = new LinkedHashMap<>();
        boolean reverse = false;

        switch (executeType) {
            case REPEAT_RUS_TO_ENG_ALL, REPEAT_RUS_TO_ENG_HARD -> reverse = true;
        }

        int counter = 0;
        int allCounter = 0;
        List<Map.Entry<String, String>> entryList = new ArrayList<>(vocabulary.entrySet());

        Scanner scanner = scannerContainer.getScanner();

        System.out.print(startMessage);

        for (int i = 0; i < entryList.size(); i++) {
            String key = entryList.get(i).getKey();
            String rightAnswer = entryList.get(i).getValue(); // Получение ключ-значение

            System.out.println(key); // Их вывод
            System.out.print(preAnswerMessage);
            String answer = scanner.nextLine().toLowerCase(); // Считывание ответа

            if (!isItHardWordsOrVerbs) { // Проверка, введена ли просьба добавить в словарь сложных слов
                if (answer.equals("hard") || answer.equals("сложно")) {
                    tryAddToHardWords(key, rightAnswer, reverse);
                    System.out.print(preAnswerMessage);
                    answer = scanner.nextLine().toLowerCase();
                }
                if (answer.equals("hard-") || answer.equals("сложно-")) {
                    tryAddToHardWords(entryList, reverse, i);
                    System.out.print(preAnswerMessage);
                    answer = scanner.nextLine().toLowerCase();
                }
            }
            if (answer.equals("stop") || answer.equals("стоп")) { // Проверка, введена ли просьба остановить
                break;
            }
            if (parser.checkAnswer(answer, rightAnswer, fullyCheck)) { // Проверка правильности ответа
                counter++;
                allCounter++;
                System.out.println(GREEN + "Правильно! " + counter + "/" + allCounter);
                System.out.println(NC + key + " - " + rightAnswer);
            } else {
                allCounter++;
                System.out.println(RED + "Неправильно! " + counter + "/" + allCounter);
                System.out.println(NC + key + " - " + rightAnswer);
                mistakes.put(key, rightAnswer);
            }
            System.out.println();
        }

        return mistakes;
    }

    private void tryAddToHardWords(String key, String rightAnswer, boolean reverse) {
        Service service = context.getService();
        String[] hardWord;
        if (reverse) {
            hardWord = new String[]{"listadd", rightAnswer};
        } else {
            hardWord = new String[]{"listadd", key};
        }
        service.addWordToHard(hardWord);
    }

    private void tryAddToHardWords(List<Map.Entry<String, String>> entryList, boolean reverse, int i) {
        if (i > 0) {
            tryAddToHardWords(entryList.get(i - 1).getKey(), entryList.get(i - 1).getValue(), reverse);
        } else {
            System.out.println("Предыдущего слова не найдено");
        }
    }

    private void setup(ExecuteType executeType) {
        switch (executeType) {
            case REPEAT_RUS_TO_ENG_ALL -> {
                startMessage = """
                        Для остановки приложения введите "stop" или "стоп"
                        Для добавления в словарь сложных слов введите "hard" или "сложно"
                        Для добавления в словарь сложных слов пред. слова введите "hard-" или "сложно-"

                        """;
                preAnswerMessage = "Слово: ";
                fullyCheck = false;
                isItHardWordsOrVerbs = false;
            }
            case REPEAT_ENG_TO_RUS_ALL -> {
                startMessage = """
                        Для остановки приложения введите "stop" или "стоп"
                        Для добавления в словарь сложных слов введите "hard" или "сложно"
                        Для добавления в словарь сложных слов пред. слова введите "hard-" или "сложно-"

                        """;
                preAnswerMessage = "Значение: ";
                fullyCheck = false;
                isItHardWordsOrVerbs = false;
            }
            case REPEAT_RUS_TO_ENG_HARD -> {
                startMessage = "Для остановки приложения введите \"stop\" или \"стоп\"\n\n";
                preAnswerMessage = "Слово: ";
                fullyCheck = false;
                isItHardWordsOrVerbs = true;
            }
            case REPEAT_ENG_TO_RUS_HARD -> {
                startMessage = "Для остановки приложения введите \"stop\" или \"стоп\"\n\n";
                preAnswerMessage = "Значение: ";
                fullyCheck = false;
                isItHardWordsOrVerbs = true;
            }
            case REPEAT_VERBS -> {
                startMessage = "Для остановки приложения введите \"stop\" или \"стоп\"\n\n";
                preAnswerMessage = "3 формы через пробел: ";
                fullyCheck = true;
                isItHardWordsOrVerbs = true;
            }
        }
    }

    private Map<String, String> getVocabulary(ExecuteType executeType) {
        Map<String, String> vocabulary = new HashMap<>();
        Languager languager = languagerContainer.getLanguager();
        switch (executeType) {
            case REPEAT_RUS_TO_ENG_ALL -> vocabulary = languager.getShuffledVocabulary(true, false);
            case REPEAT_ENG_TO_RUS_ALL -> vocabulary = languager.getShuffledVocabulary(false, false);
            case REPEAT_RUS_TO_ENG_HARD -> vocabulary = languager.getShuffledVocabulary(true, true);
            case REPEAT_ENG_TO_RUS_HARD -> vocabulary = languager.getShuffledVocabulary(false, true);
            case REPEAT_VERBS -> vocabulary = verber.getShuffledVocabulary();
        }
        return vocabulary;
    }

    public boolean giveRepeatOffer() {
        System.out.print("Хотите повторить неверные ответы? (y/n): ");
        return parser.yesOrNo();
    }
}
