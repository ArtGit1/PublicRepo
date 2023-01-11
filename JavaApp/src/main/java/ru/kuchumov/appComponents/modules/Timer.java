package ru.kuchumov.appComponents.modules;

import ru.kuchumov.appComponents.utilites.PathInitializer;
import ru.kuchumov.appComponents.utilites.osInitializer.OSInitializer;
import ru.kuchumov.appComponents.utilites.osInitializer.OSStrategy;
import ru.kuchumov.appContext.annotations.CustomAutowired;
import ru.kuchumov.appContext.components.CustomComponent;

import java.io.*;
import java.util.*;

public class Timer implements CustomComponent {
    private Map<String, LinkedList<String>> times;
    private final String timesPath;
    FileReader fr;
    BufferedReader br;
    private final String GREEN;
    private final String RED;
    private final String NC;

    @CustomAutowired
    public Timer(OSInitializer osInitializer, PathInitializer pathInitializer) {
        OSStrategy osStrategy = osInitializer.getOSContext().getOSStrategy();

        timesPath = pathInitializer.getTimesPath();
        GREEN = osStrategy.getGreen();
        RED = osStrategy.getRed();
        NC = osStrategy.getNC();

        getTimesFromDisk();
    }

    private void getTimesFromDisk() {
        File timesFile = new File(timesPath);
        if (timesFile.exists()) {
            getTimesFromDiskLogic();
        } else {
            System.out.println("Файл times.md в директории \"" + timesPath + "\" не найден");
            System.exit(1);
        }
    }

    private void getTimesFromDiskLogic() {
        String line;
        boolean flag = false;
        String name = null;
        LinkedList<String> lines = new LinkedList<>();
        times = new LinkedHashMap<>();
        try {
            fr = new FileReader(timesPath);
        } catch (FileNotFoundException e) {
            System.out.println("Файл times.md отсутствует");
        }
        br = new BufferedReader(fr);

        try {
            line = br.readLine();
            while (line != null) {
                if (line.equals("--ОТБИВОЧКА--")) {
                    if (!flag) {
                        flag = true;
                        line = br.readLine();
                        if (!lines.isEmpty() && name != null) {
                            LinkedList<String> tempList = new LinkedList<>(lines);
                            times.put(name, tempList);
                            lines.clear();
                        }
                        continue;
                    }
                }
                if (flag) {
                    name = line.substring(0, line.indexOf(":"));
                    flag = false;
                    line = br.readLine();
                    continue;
                }
                lines.add(line);
                line = br.readLine();

            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении times.md");
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

    public void printAll() {
        Set<Map.Entry<String, LinkedList<String>>> set;
        set = times.entrySet();
        System.out.println();
        System.out.println(RED + "Справка по временам:");
        System.out.println();
        for (Map.Entry<String, LinkedList<String>> item : set) {
            System.out.println(GREEN + item.getKey() + ":" + NC);
            System.out.println();
            for (String line : item.getValue()) {
                System.out.println("   " + line);
            }
            System.out.println();
        }
    }

    public void printByContain(String[] args) {
        String parsedArgs = parseArgs(args);
        Set<Map.Entry<String, LinkedList<String>>> set;
        set = times.entrySet();
        LinkedHashSet<Map.Entry<String, LinkedList<String>>> accept = new LinkedHashSet<>();
        for (Map.Entry<String, LinkedList<String>> item : set) {
            if (item.getKey().toLowerCase().contains(parsedArgs)) {
                accept.add(item);
                continue;
            }
            for (String line : item.getValue()) {
                if (line.toLowerCase().contains(parsedArgs)) {
                    accept.add(item);
                }
            }
        }
        if (accept.isEmpty()) {
            System.out.println("По запросу \"" + parsedArgs + "\" ничего не найдено");
        } else {
            System.out.println();
            System.out.println(RED + "Результаты поиска:");
            System.out.println();
            for (Map.Entry<String, LinkedList<String>> item :
                    accept) {
                int beginIndex;
                if (item.getKey().toLowerCase().contains(parsedArgs)) {
                    beginIndex = item.getKey().toLowerCase().indexOf(parsedArgs);
                    if (beginIndex != 0) {
                        System.out.println(GREEN + item.getKey().substring(0, beginIndex) +
                                RED + item.getKey().substring(beginIndex, beginIndex + parsedArgs.length()) +
                                GREEN + item.getKey().substring(beginIndex + parsedArgs.length()) + ":" + NC);
                    } else {
                        System.out.println(RED + item.getKey().substring(beginIndex, beginIndex + parsedArgs.length()) +
                                GREEN + item.getKey().substring(beginIndex + parsedArgs.length()) + ":" + NC);
                    }
                } else {
                    System.out.println(GREEN + item.getKey() + ":" + NC);
                }
                System.out.println();
                for (String line : item.getValue()) {

                    if (line.toLowerCase().contains(parsedArgs)) {
                        beginIndex = line.toLowerCase().indexOf(parsedArgs);
                        String foundResult = line.substring(beginIndex, beginIndex + parsedArgs.length());
                        String endOfLine = line.substring(beginIndex + parsedArgs.length());
                        if (beginIndex != 0) {
                            System.out.println("   " + NC + line.substring(0, beginIndex) +
                                    RED + foundResult +
                                    NC + endOfLine);
                        } else {
                            System.out.println("   " + RED + foundResult +
                                    NC + endOfLine);
                        }
                    } else {
                        System.out.println("   " + line);
                    }
                }
                System.out.println();
            }
        }
    }

    private String parseArgs(String[] args) {
        List<String> listArgs = List.of(args);
        return String.join(" ", listArgs.subList(1, listArgs.size())).toLowerCase();
    }
}


