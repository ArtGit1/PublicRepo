package ru.kuchumov.appComponents.modules;

import ru.kuchumov.appComponents.utilites.PathInitializer;
import ru.kuchumov.appContext.annotations.CustomAutowired;
import ru.kuchumov.appContext.components.CustomComponent;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Backuper implements CustomComponent {
    private final File backup;
    private final String backupPath;
    FileWriter fw;
    BufferedWriter bw;
    FileReader fr;
    BufferedReader br;

    @CustomAutowired
    public Backuper(PathInitializer pathInitializer) {
        backupPath = pathInitializer.getBackupPath();
        backup = new File(backupPath);
    }

    public void addToBackup(String word, String description) {
        try {
            fw = new FileWriter(backup, true);
            bw = new BufferedWriter(fw);

            bw.newLine();
            bw.write(word + " - " + description);

        } catch (IOException e) {
            System.out.println("Файл .gitignore в данной директории отсутствует");
            e.printStackTrace();
            throw new RuntimeException();
        }

        try {
            bw.close();
            fw.close();
        } catch (IOException e) {
            System.out.println("Ошибка при закрытии BufferedWriter / FileWriter");
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            try {
                fw.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void loadFromBackup(Languager languager) {
        File backup = new File(backupPath);
        if (backup.exists()) {
            languager.deleteAll();
            backupLogic(languager);
        } else {
            System.out.println("Файл backup.md в директории \"" + backupPath + "\" не найден");
        }
    }

    private void backupLogic(Languager languager) {
        String line;
        try {
            fr = new FileReader(backupPath);
        } catch (FileNotFoundException e) {
            System.out.println("Файл backup.md отсутствует");
        }
        br = new BufferedReader(fr);

        try {
            line = br.readLine();
            while (line != null) {
                if (!line.isEmpty()) {
                    List<String> parsedLine = parseLine(line);
                    languager.addWorld(parsedLine.get(0), parsedLine.get(1));
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении backup.md");
            e.printStackTrace();
            throw new RuntimeException();
        }

        try {
            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("Ошибка при закрытии BufferedReader / FileReader");
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            try {
                fr.close();
            } catch (IOException ignored) {
            }
        }

//        finally {
//            try {
//                fr.close();
//            } catch (IOException e) {
//                System.out.println("Ошибка при закрытии FileReader");
//                e.printStackTrace();
//                throw new RuntimeException();
//            }
//        }
        System.out.println("Успешно загружено слов: " + languager.getCount());
    }

    private List<String> parseLine(String line) {
        List<String> parsedLine = new LinkedList<>();

        List<String> arrayLine = List.of(line.split(" "));
        int dash = arrayLine.indexOf("-");
        List<String> subArgs = arrayLine.subList(dash + 1, arrayLine.size());
        String word = String.join(" ", arrayLine.subList(0, dash)).toLowerCase();
        String description = String.join(" ", subArgs);
        parsedLine.add(word);
        parsedLine.add(description);
        return parsedLine;
    }
}
