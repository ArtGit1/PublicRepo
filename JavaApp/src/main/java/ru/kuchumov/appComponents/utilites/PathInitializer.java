package ru.kuchumov.appComponents.utilites;

import ru.kuchumov.Main;
import ru.kuchumov.appContext.components.CustomComponent;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class PathInitializer implements CustomComponent {
    private final String vocabularyPath;
    private final String backupPath;
    private final String verbsPath;
    private final String timesPath;

    public PathInitializer() {

        URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
        File f;
        try {
            f = new File(url.toURI());
        } catch (URISyntaxException e) {
            System.out.println("Ошибка инициализации пути до ресурсов. Ошибка синтаксиса URL");
            throw new RuntimeException(e);
        }
        String decoder;
        decoder = URLDecoder.decode(f.getParentFile().getAbsolutePath(), StandardCharsets.UTF_8);

        String intermediatePath = "/resources";
        vocabularyPath = decoder + intermediatePath + "/vocabulary.out";
        backupPath = decoder + intermediatePath + "/backup.md";
        verbsPath = decoder + intermediatePath + "/verbs.md";
        timesPath = decoder + intermediatePath + "/times.md";
    }

    public String getVocabularyPath() {
        return vocabularyPath;
    }

    public String getBackupPath() {
        return backupPath;
    }

    public String getVerbsPath() {
        return verbsPath;
    }
    public String getTimesPath() {
        return timesPath;
    }
}
