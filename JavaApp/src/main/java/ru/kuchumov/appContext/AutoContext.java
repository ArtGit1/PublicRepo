package ru.kuchumov.appContext;

/*
    Как пользоваться:
    2 вида автоматического (прокси) контекста:
    1. ContextContainer.ContextInitializer.initContextWithDeclaringComponentsByReturnTypeOfGetter(args);
    Создает экземпляры по классу возвращаемого значения в геттерах, определенных ниже. Компоненты хранятся где угодно

    2. ContextContainer.ContextInitializer.initContextWithStaticComponentsPackage("appContext.components", args);
    Создает экземпляры по имени геттера, все компоненты должны храниться в переданном в конструктор пакете без вложенности

    Общие требования -
    1. Каждый компонент необходимо implements CustomComponent
    2. Для каждого компонента уникальное название
    2. Определить геттеры в данном интерфейсе (ClassName get<ClassName>())
    3. Вызывать в любом месте ContextContainer.getContext().get<ClassName>() / getArgs()

    Примечание:
    AutoContext удобен в использовании, но работает медленее менее удобного ManualContext
 */

import ru.kuchumov.appComponents.modules.*;
import ru.kuchumov.appComponents.utilites.Parser;
import ru.kuchumov.appComponents.utilites.PathInitializer;
import ru.kuchumov.appComponents.utilites.ScannerContainer;
import ru.kuchumov.appComponents.utilites.osInitializer.OSInitializer;
import ru.kuchumov.appContext.contextServise.GlobalCustomContext;

public interface AutoContext extends GlobalCustomContext {
    Classifier getClassifier();
    Service getService();
    Repeater getRepeater();
    PathInitializer getPathInitializer();
    Saver getSaver();
    LanguagerContainer getLanguagerContainer();
    Verber getVerber();
    OSInitializer getOSInitializer();
    ScannerContainer getScannerContainer();
    Parser getParser();
    Timer getTimer();
    Backuper getBackuper();
}

