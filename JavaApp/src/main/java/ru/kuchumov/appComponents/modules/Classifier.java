package ru.kuchumov.appComponents.modules;


import ru.kuchumov.appContext.components.CustomComponent;
import ru.kuchumov.appContext.contextServise.ContextContainer;

public class Classifier implements CustomComponent {

    public void startApplication() {
        String[] args = ContextContainer.getContext().getArgs();
        ExecuteType executeType = null;
        switch (args.length) {
            case 0:
                System.out.println("lang <word> - получить значение слова");
                System.out.println("lang <word> - <значение> - сохранить новое слово");
                System.out.println("lang rus - начать повторять rus-eng");
                System.out.println("lang eng - начать повторять eng-rus");
                System.out.println("lang all - получить весь словарь");
                System.out.println("lang del <номера или названия слов> - удалить выбранные слова из словаря");
                System.out.println("lang list - получить словарь сложных слов");
                System.out.println("lang listadd <номера или названия слов> - добавить слова в словарь сложных слов");
                System.out.println("lang listdel <номера или названия слов> - удалить выбранные слова из словаря сложных слов");
                System.out.println("lang listrus - начать повторять сложные слова rus-eng");
                System.out.println("lang listeng - начать повторять сложные слова eng-rus");
                System.out.println("lang verbsrand - начать повторять неправильные глаголы");
                System.out.println("lang verbs - просмотреть список неправильных глаголов");
                System.out.println("lang verb <глагол> - найти 3 формы интересующего неправильного глагола");
                System.out.println("lang times - просмотреть справку по временам");
                System.out.println("lang backup - восстановить основной словарь из backup.md");
                break;
            case 1:
                switch (args[0]) {
                    case "rus" -> executeType = ExecuteType.REPEAT_RUS_TO_ENG_ALL; // random rus logic
                    case "eng" -> executeType = ExecuteType.REPEAT_ENG_TO_RUS_ALL; // random eng logic
                    case "all" -> executeType = ExecuteType.PRINT_ALL_WORDS; // get all word logic
                    case "backup" -> executeType = ExecuteType.LOAD_FROM_BACKUP; // loading from backup logic
                    case "del" -> System.out.println("Укажите названия или номера файлов для удаления");
                    case "list" -> executeType = ExecuteType.PRINT_ALL_HARD_WORDS; // hard words showing logic
                    case "listadd" ->
                            System.out.println("Укажите названия или номера файлов для добавления в словарь сложных слов");
                    case "listdel" ->
                            System.out.println("Укажите названия или номера файлов для удаления из словаря сложных слов");
                    case "listrus" -> executeType = ExecuteType.REPEAT_RUS_TO_ENG_HARD; // random rus hard words logic
                    case "listeng" -> executeType = ExecuteType.REPEAT_ENG_TO_RUS_HARD; // random eng hard words logic
                    case "verbs" -> executeType = ExecuteType.PRINT_ALL_VERBS; // showing all verbs logic
                    case "verbsrand" -> executeType = ExecuteType.REPEAT_VERBS; // random verb logic
                    case "verb" -> System.out.println("Введите интересующий глагол");
                    case "times" -> executeType = ExecuteType.PRINT_ALL_TIMES; // showing all times info logic
                    default -> executeType = ExecuteType.GET_WORD_DESCRIPTION; // get word logic
                }
                break;
            default:
                switch (args[0]) {
                    case "del" -> executeType = ExecuteType.DELETE_WORD; // delete words logic
                    case "listadd" -> executeType = ExecuteType.ADD_WORD_TO_HARD; // list add logic
                    case "listdel" -> executeType = ExecuteType.DELETE_WORD_FROM_HARD; // list del logic
                    case "verb" -> executeType = ExecuteType.GET_VERB_FORMS; // particular verb(s) show logic
                    case "times" -> executeType = ExecuteType.FIND_IN_TIMES;  // finding in time help logic
                    default -> executeType = ExecuteType.ADD_WORD; // add word logic
                }
        }
        if (executeType == null) {
            return;
        }
        executeType.execute();
    }
}

enum ExecuteType {
    REPEAT_RUS_TO_ENG_ALL {
        public void execute() {
            SERVICE.repeat(this);
            ContextContainer.getContext().getScannerContainer().closeScanner();
        }
    },
    REPEAT_ENG_TO_RUS_ALL {
        public void execute() {
            SERVICE.repeat(this);
            ContextContainer.getContext().getScannerContainer().closeScanner();
        }
    },
    REPEAT_RUS_TO_ENG_HARD {
        public void execute() {
            SERVICE.repeat(this);
            ContextContainer.getContext().getScannerContainer().closeScanner();
        }
    },
    REPEAT_ENG_TO_RUS_HARD {
        public void execute() {
            SERVICE.repeat(this);
            ContextContainer.getContext().getScannerContainer().closeScanner();
        }
    },
    REPEAT_VERBS {
        public void execute() {
            SERVICE.repeat(this);
            ContextContainer.getContext().getScannerContainer().closeScanner();
        }
    },
    PRINT_ALL_WORDS {
        public void execute() {
            SERVICE.print(this);
        }
    },
    PRINT_ALL_HARD_WORDS {
        public void execute() {
            SERVICE.print(this);
        }
    },
    PRINT_ALL_VERBS {
        public void execute() {
            SERVICE.print(this);
        }
    },
    PRINT_ALL_TIMES {
        public void execute() {
            SERVICE.print(this);
        }
    },
    GET_WORD_DESCRIPTION {
        public void execute() {
            SERVICE.getWordDescription();
        }
    },
    GET_VERB_FORMS {
        public void execute() {
            SERVICE.getVerbForms();
        }
    },
    DELETE_WORD {
        public void execute() {
            SERVICE.deleteWord();
        }
    },
    DELETE_WORD_FROM_HARD {
        public void execute() {
            SERVICE.deleteWordFromHard();
        }
    },
    ADD_WORD {
        public void execute() {
            SERVICE.addWord();
        }
    },
    ADD_WORD_TO_HARD {
        public void execute() {
            SERVICE.addWordToHard();
        }
    },
    FIND_IN_TIMES {
        public void execute() {
            SERVICE.findInTimes();
        }
    },
    LOAD_FROM_BACKUP {
        public void execute() {
            SERVICE.loadFromBackup();
        }
    };
    static final Service SERVICE = ContextContainer.getContext().getService();
    public abstract void execute();
}
