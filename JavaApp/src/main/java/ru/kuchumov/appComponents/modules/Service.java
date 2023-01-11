package ru.kuchumov.appComponents.modules;

import ru.kuchumov.appComponents.utilites.Parser;
import ru.kuchumov.appComponents.utilites.PathInitializer;
import ru.kuchumov.appContext.AutowiredContext;
import ru.kuchumov.appContext.components.CustomComponent;

import java.util.*;

public class Service implements CustomComponent {
    private final Repeater repeater;
    private final LanguagerContainer languagerContainer;
    private final Saver saver;
    private final Parser parser;
    private  final PathInitializer pathInitializer;
    private final Backuper backuper;
    private final Timer timer;
    private final Verber verber;
    private final String[] args;

    public Service(Repeater repeater, LanguagerContainer languagerContainer, Saver saver, Parser parser,
                   PathInitializer pathInitializer, Backuper backuper, Timer timer, Verber verber, AutowiredContext autowiredContext) {
        this.repeater = repeater;
        this.languagerContainer = languagerContainer;
        this.saver = saver;
        this.parser = parser;
        this.pathInitializer = pathInitializer;
        this.backuper = backuper;
        this.timer = timer;
        this.verber = verber;
        this.args = autowiredContext.getArgs();
    }

    public void repeat(ExecuteType executeType) {
        boolean repeatFlag = false;

        Map<String, String> mistakes = repeater.repeat(executeType);
        if (mistakes == null) {
            return;
        }
        if (!mistakes.isEmpty()) {
            repeatFlag = repeater.giveRepeatOffer();
        }

        while (repeatFlag) {
            mistakes = repeater.repeat(executeType, mistakes);
            if (!mistakes.isEmpty()) {
                repeatFlag = repeater.giveRepeatOffer();
            } else {
                repeatFlag = false;
            }
        }
    }
    
    public void print(ExecuteType executeType) {
        switch (executeType) {
            case PRINT_ALL_WORDS -> languagerContainer.getLanguager().printVocabulary();
            case PRINT_ALL_HARD_WORDS -> languagerContainer.getLanguager().printHardVocabulary();
            case PRINT_ALL_VERBS -> verber.printAll();
            case PRINT_ALL_TIMES -> timer.printAll();
            default -> throw new RuntimeException("Что-то пошло не так");
        }
    }
    
    public void getWordDescription() {
        Languager languager = languagerContainer.getLanguager();

        if (args.length > 0) {
            String word = args[0].toLowerCase();
            Map<String, String> words = languager.getWords(word);

            Set<Map.Entry<String, String>> set2 = words.entrySet();
            for (Map.Entry<String, String> item : set2) {
                System.out.println(item.getKey() + " - " + item.getValue());
            }
        }
    }
    
    public void getVerbForms() {
        if (args.length < 2) {
            return;
        }
        String verb = args[1];

        LinkedHashMap<String, String> verbs = verber.getVerbsByContain(verb);
        if (!verbs.isEmpty()) {
            Set<Map.Entry<String, String>> set;
            set = verbs.entrySet();
            for (Map.Entry<String, String> item : set) {
                System.out.println(item.getKey() + " - " + item.getValue());
            }
        } else {
            System.out.println("Глагол \"" + verb + "\" не найден");
        }
        
    }
    
    public void deleteWord() {
        deleteBody(false);
    }
    
    public void deleteWordFromHard() {
        deleteBody(true);
    }

    private void deleteBody(boolean hard) {
        Languager languager = languagerContainer.getLanguager();

        Set<String> mustBeDeletedNames = new HashSet<>();
        boolean allDeleteFlag = false;
        for (int i = 1; i < args.length; i++) {
            int wordId;
            String word;

            try {
                wordId = Integer.parseInt(args[i]);
                if (inSizeCheck(wordId, hard)) {
                    word = languager.getWord(wordId - 1, hard);
                    mustBeDeletedNames.add(word);
                } else {
                    System.out.println("Неверный номер слова: " + wordId);
                }
            } catch (NumberFormatException e) {
                word = args[i];
                if (word.equals(".")) {
                    System.out.println("Удаление всех слов...");
                    System.out.println("Слов удалено: " + languager.getCount(hard));
                    languager.deleteAll(hard);
                    allDeleteFlag = true;
                    break;
                }
                if (languager.existCheck(word, hard)) {
                    mustBeDeletedNames.add(word);
                } else {
                    System.out.println("Неверное имя слова: " + word);
                }
            }
        }

        if (!allDeleteFlag) {
            if (mustBeDeletedNames.isEmpty()) {
                System.out.println("Слов для удаления из словаря не найдено");
            } else {
                languager.deleteByKey(mustBeDeletedNames, hard);
            }
        }

        String path = pathInitializer.getVocabularyPath();
        saver.save(languager, path);
    }

    private boolean inSizeCheck(int i, boolean hard) {
        Languager languager = languagerContainer.getLanguager();
        if (hard) {
            return i > 0 && i <= languager.getHardCount();
        } else {
            return i > 0 && i <= languager.getCount();
        }
    }

    public void addWord() {
        String[] arrayArgs = args;

        Languager languager = languagerContainer.getLanguager();

        List<String> args = formatValid(arrayArgs);
        int dash = args.indexOf("-");
        List<String> subArgs = args.subList(dash + 1, args.size());

        String word = String.join(" ", args.subList(0, dash)).toLowerCase();
        String description = String.join(" ", subArgs);

        if (duplicateValid(word, description)) {
            languager.addWorld(word, description);
            saver.save(languager, pathInitializer.getVocabularyPath());
            backuper.addToBackup(word, description);
            System.out.println("Слово \"" + word + "\" успешно сохранено со значением \"" + description + "\"");
        } else {
            System.out.println("Изменения отменены");
        }
    }

    private List<String> formatValid(String[] arrayArgs) {
        List<String> args = new ArrayList<>();
        Collections.addAll(args, arrayArgs);
        if (!args.contains("-")) {
            System.out.println("Неверный формат слова. Не найден дефис");
            System.out.println("Используйте: lang <word> - <значение>");
            System.exit(0);
        }
        return args;
    }

    private boolean duplicateValid(String word, String description) {
        Languager languager = languagerContainer.getLanguager();

        if (languager.duplicateCheck(word)) {
            String wordDescription = languager.getWord(word);
            System.out.println("Слово \"" + word + "\" уже существует в словаре:");
            System.out.println(word + " - " + wordDescription);
            System.out.println("Хотите его заменить на:");
            System.out.print("\"" + word + " - " + description + "\"? (y/n): ");
            return parser.yesOrNo();
        } else {
            return true;
        }
    }
    
    public void addWordToHard() {
        addWordToHardBody(args);
    }

    public void addWordToHard(String[] args) {
        addWordToHardBody(args);
    }

    private void addWordToHardBody(String[] args) {
        Languager languager = languagerContainer.getLanguager();

        Set<String> mustBeAddedToHard = new HashSet<>();
        for (int i = 1; i < args.length; i++) {
            int wordId;
            String word;

            try {
                wordId = Integer.parseInt(args[i]);
                if (inSizeCheck(wordId, false)) {
                    word = languager.getWord(wordId - 1);
                    if (!languager.existHardCheck(word)) {
                        mustBeAddedToHard.add(word);
                    } else {
                        System.out.println("Слово \"" + word + " - " + languager.getWord(word) +"\" уже есть в словаре сложных слов");
                    }

                } else {
                    System.out.println("Неверный номер слова: " + wordId);
                }
            } catch (NumberFormatException e) {
                word = args[i];
                if (languager.existCheck(word)) {
                    if (!languager.existHardCheck(word)) {
                        mustBeAddedToHard.add(word);
                    } else {
                        System.out.println("Слово \"" + word + " - " + languager.getWord(word) +"\" уже есть в словаре сложных слов");
                    }
                } else {
                    System.out.println("Неверное имя слова: " + word);
                }
            }
        }
        if (mustBeAddedToHard.isEmpty()) {
            System.out.println("Слов для добавления в словарь сложных слов не найдено");
        } else {
            languager.addToHardByKey(mustBeAddedToHard);
        }

        saver.save(languager, pathInitializer.getVocabularyPath());
    }

    public void findInTimes() {
        timer.printByContain(args);
    }
    
    public void loadFromBackup() {
        System.out.print("Вы уверены, что хотите загрузиться из backup.md? Весь текущий словарь будет удален. (y/n): ");
        if (parser.yesOrNo()) {
            Languager languager = new Languager();
            backuper.loadFromBackup(languager);
            saver.save(languager, pathInitializer.getVocabularyPath());
        } else {
            System.out.println("Загрузка из backup.md отменена");
        }
    }
}
