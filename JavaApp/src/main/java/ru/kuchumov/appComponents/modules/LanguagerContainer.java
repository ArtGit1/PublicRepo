package ru.kuchumov.appComponents.modules;

import ru.kuchumov.appComponents.utilites.PathInitializer;
import ru.kuchumov.appContext.annotations.CustomAutowired;
import ru.kuchumov.appContext.components.CustomComponent;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class LanguagerContainer implements CustomComponent {
    private final Languager languager;

    @CustomAutowired
    public LanguagerContainer(Saver saver, PathInitializer pathInitializer) {
        String vocabularyPath = pathInitializer.getVocabularyPath();

        File vocabulary = new File(vocabularyPath);
        if (vocabulary.exists()) {
            languager = saver.download(vocabularyPath);
        } else {
            languager = new Languager();
        }
    }

    public Languager getLanguager() {
        return languager;
    }
}

class Languager implements Serializable {
    private final TreeMap<String, String> vocabulary;
    private final TreeSet<String> hardVocabulary;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Languager languager = (Languager) o;
        return Objects.equals(vocabulary, languager.vocabulary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vocabulary);
    }

    public Languager() {
        vocabulary = new TreeMap<>();
        hardVocabulary = new TreeSet<>();
    }

    public void addWorld(String word, String description) {
        vocabulary.put(word.trim(), description);
    }

    public Map<String, String> getWords (String word) {
        Map<String, String> words = new LinkedHashMap<>();
        if (vocabulary.containsKey(word)) {
            words.put(word, vocabulary.get(word));
        }
        Set<Map.Entry<String, String>> set = vocabulary.entrySet();
        for (Map.Entry<String, String> item : set) {
            if (!word.equals(item.getKey()) && word.length() <= item.getKey().length() && word.equals(item.getKey().substring(0, word.length()))) {
                words.put(item.getKey(), item.getValue());
            }
        }
        if (words.isEmpty()) {
            System.out.println("Слово \"" + word + "\" не найдено");
            System.exit(0);
        }
        return words;
    }

    public void printVocabulary() {
        printVoc(false);
    }

    public void printHardVocabulary() {
        printVoc(true);
    }

    public void printVoc(boolean hard) {
        Set<Map.Entry<String, String>> set;
        int i = 0;
        if (hard) {
            for (String key :
                    hardVocabulary) {
                i++;
                System.out.println(i + ". " + key + " - " + vocabulary.get(key));
            }
        } else {
            set = vocabulary.entrySet();
            for (Map.Entry<String, String> item : set) {
                i++;
                System.out.println(i + ". " + item.getKey() + " - " + item.getValue());
            }
        }
    }

    public Map<String, String> getShuffledVocabulary(boolean reversed, boolean hard) {
        Map<String, String> shuffledVocabulary = new LinkedHashMap<>();
        List<Map.Entry<String, String>> shuffledEntriesList = getShuffledEntriesList(hard);

        for (Map.Entry<String, String> entry : shuffledEntriesList) {
            if (reversed) {
                shuffledVocabulary.put(entry.getValue(), entry.getKey());
            } else {
                shuffledVocabulary.put(entry.getKey(), entry.getValue());
            }
        }
        return shuffledVocabulary;
    }

    private List<Map.Entry<String, String>> getShuffledEntriesList(boolean hard) {
        Set<Map.Entry<String, String>> entrySet;
        if (hard) {
            Map<String, String> tempMap= new HashMap<>();
            for (String key :
                    hardVocabulary) {
                tempMap.put(key, vocabulary.get(key));
            }
            entrySet = tempMap.entrySet();
        } else {
            entrySet = vocabulary.entrySet();
        }
        List<Map.Entry<String, String>> entriesList = new ArrayList<>(entrySet);
        Collections.shuffle(entriesList);
        return entriesList;
    }

    public boolean duplicateCheck(String word) {
        return vocabulary.containsKey(word);
    }

    public String getWord(int id, boolean hard) {
        if (hard) {
            return getHardWord(id);
        } else {
            return getWord(id);
        }
    }

    public String getWord(int id) {
        return (String) vocabulary.keySet().toArray()[id];
    }

    public String getWord(String name) {
        return vocabulary.get(name);
    }

    public String getHardWord(int id) {
        return (String) hardVocabulary.toArray()[id];
    }

    public void deleteAll() {
        hardVocabulary.clear();
        vocabulary.clear();
    }

    public void deleteAll(boolean hard) {
        if (hard) {
            deleteAllHard();
        } else {
            hardVocabulary.clear();
            vocabulary.clear();
        }
    }

    public void deleteAllHard() {
        hardVocabulary.clear();
    }

    public void deleteByKey(Set<String> keys, boolean hard) {
        if (hard) {
            deleteByKeyHard(keys);
        } else {
            deleteByKey(keys);
        }
    }

    public void deleteByKey(Set<String> keys) {
        for (String key :
                keys) {
            System.out.println("Слово: \"" + key + " - " + vocabulary.get(key) + "\" успешно удалено");
            hardVocabulary.remove(key);
            vocabulary.remove(key);
        }
    }

    public void deleteByKeyHard(Set<String> keys) {
        for (String key :
                keys) {
            System.out.println("Слово: \"" + key + " - " + vocabulary.get(key) + "\" успешно удалено из словаря сложных слов");
            hardVocabulary.remove(key);
        }
    }


    public void addToHardByKey(Set<String> keys) {
        for (String key :
                keys) {
            System.out.println("Слово: \"" + key + " - " + vocabulary.get(key) + "\" успешно добавлено в словарь сложных слов");
            hardVocabulary.add(key);
        }
    }

    public int getCount() {
        return vocabulary.size();
    }

    public int getCount(boolean hard) {
        if (hard) {
            return getHardCount();
        } else {
            return getCount();
        }
    }

    public int getHardCount() {
        return hardVocabulary.size();
    }

    public boolean existCheck(String key) {
        return vocabulary.containsKey(key);
    }

    public boolean existCheck(String key, boolean hard) {
        if (hard) {
            return existHardCheck(key);
        } else {
            return existCheck(key);
        }
    }

    public boolean existHardCheck(String key) {
        return hardVocabulary.contains(key);
    }
}



