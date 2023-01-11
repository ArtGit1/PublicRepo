package ru.kuchumov;


import ru.kuchumov.appComponents.modules.Classifier;
import ru.kuchumov.appContext.AutowiredContext;
import ru.kuchumov.appContext.contextServise.ContextContainer;

public class Main {
    public static void main(String[] args) {
        ContextContainer.ContextInitializer.initAutowiredContext(args);
        AutowiredContext autowiredContext = (AutowiredContext) ContextContainer.getContext();
        autowiredContext.getComponent(Classifier.class).startApplication();
    }
}
