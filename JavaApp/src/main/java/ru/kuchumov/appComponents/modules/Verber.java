package ru.kuchumov.appComponents.modules;

import ru.kuchumov.appComponents.utilites.PathInitializer;
import ru.kuchumov.appContext.annotations.CustomAutowired;
import ru.kuchumov.appContext.components.CustomComponent;

import java.io.*;
import java.util.*;

public class Verber implements CustomComponent {
    private Map<String, List<String>> verbs;
    private final String verbsPath;
    FileReader fr;
    BufferedReader br;

    @CustomAutowired
    public Verber(PathInitializer pathInitializer) {
        this.verbsPath = pathInitializer.getVerbsPath();
        getVerbsFromDisk();
    }

    private void getVerbsFromDisk() {
        File verbsFile = new File(verbsPath);
        if (verbsFile.exists()) {
            getVerbsFromDiskLogic();
        } else {
            System.out.println("Файл verbs.md в директории \"" + verbsPath + "\" не найден");
            System.exit(1);
        }
    }

    private void getVerbsFromDiskLogic() {
        String line;
        verbs = new HashMap<>();
        try {
            fr = new FileReader(verbsPath);
        } catch (FileNotFoundException e) {
            System.out.println("Файл verbs.md отсутствует");
        }
        br = new BufferedReader(fr);

        try {
            line = br.readLine();
            while (line != null) {
                if (!line.isEmpty()) {
                    Map<String, List<String>> parsedLine = parseLine(line);
                    verbs.putAll(parsedLine);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении verbs.md");
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
    }

    private Map<String, List<String>> parseLine(String line) {
        Map<String, List<String>> parsedLine = new HashMap<>();
        int dash = line.indexOf("-");
        String key = line.substring(dash + 2);
        String description = line.substring(0, dash - 1);
        List<String> descriptionList = List.of(description.split(" "));
        parsedLine.put(key, descriptionList);
        return parsedLine;
    }

    public void printAll() {
        Set<Map.Entry<String, List<String>>> set;
        set = verbs.entrySet();
        TreeMap<String, String> sortedVerbs = new TreeMap<>();
        int i = 0;
        for (Map.Entry<String, List<String>> item : set) {
            sortedVerbs.put(String.join(", ", item.getValue()), item.getKey());
        }
        Set<Map.Entry<String, String>> treeSet;
        treeSet = sortedVerbs.entrySet();
        for (Map.Entry<String, String> item : treeSet) {
            i++;
            System.out.println(i + ". " + item.getKey() + " - " + item.getValue());
        }
    }


    public LinkedHashMap<String, String> getVerbsByContain(String verb) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        TreeMap<String, String> sortedVerbs = new TreeMap<>();

        Set<Map.Entry<String, List<String>>> set;
        set = verbs.entrySet();
        for (Map.Entry<String, List<String>> item : set) {
            if (item.getValue().get(0).equals(verb)) {
                result.put(String.join(", ", item.getValue()), item.getKey());
                continue;
            }
            if (item.getValue().get(0).startsWith(verb)) {
                sortedVerbs.put(String.join(", ", item.getValue()), item.getKey());
            }
        }

        Set<Map.Entry<String, String>> set2;
        set2 = sortedVerbs.entrySet();
        for (Map.Entry<String, String> item : set2) {
            result.put(item.getKey(), item.getValue());
        }

        return result;
    }

    public Map<String, String> getShuffledVocabulary() {
        Set<Map.Entry<String, List<String>>> entrySet = verbs.entrySet();
        List<Map.Entry<String, List<String>>> shuffledEntries = new ArrayList<>(entrySet);
        Collections.shuffle(shuffledEntries);

        Map<String, String> shuffledVocabulary = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry :
                shuffledEntries) {
            shuffledVocabulary.put(entry.getKey(), String.join(" ", entry.getValue()));
        }
        return shuffledVocabulary;
    }
}
