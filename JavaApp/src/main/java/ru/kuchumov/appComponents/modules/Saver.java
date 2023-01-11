package ru.kuchumov.appComponents.modules;

import ru.kuchumov.appContext.components.CustomComponent;

import java.io.*;

public class Saver implements CustomComponent {
    public Saver() {
    }

    public void save(Object object, String path) {
        FileOutputStream fOS;
        try {
            fOS = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ObjectOutputStream oOS;
        try {
            oOS = new ObjectOutputStream(fOS);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении словаря (создание ObjectOutputStream)");
            throw new RuntimeException(e);
        }
        try {
            oOS.writeObject(object);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении словаря (запись в файл)");
            throw new RuntimeException(e);
        }
        try {
            oOS.close();
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении словаря (закрытие ObjectOutputStream)");
            throw new RuntimeException(e);
        }
    }

    public Languager download(String path) {
        FileInputStream fIS = null;
        try {
            fIS = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            System.out.println("Файла со словарем нет. Сначала что-нибудь добавьте (lang <word> - <значение>)");
            System.exit(0);
        }
        ObjectInputStream oIS;
        try {
            oIS = new ObjectInputStream(fIS);
        } catch (IOException e) {
            System.out.println("Ошибка при чтении словаря (создание ObjectInputStream)");
            throw new RuntimeException(e);
        }
        Languager languager = null;
        try {
            languager = (Languager) oIS.readObject();
        } catch (InvalidClassException e) {
            System.out.println("Ошибка сериализации. Несовместимость класса в программе и класса на диске");
            System.out.println("Откатитесь на более старую версию");
            System.out.println("Либо удалите vocabulary.out из директории с .jar файлом и загрузитесь с backup.md: lang backup");
            System.out.println("Либо удалите vocabulary.out из директории с .jar файлом и начните все сначала");
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка при чтении словаря. Класс Languager не найден");
            System.out.println("Попробуйте загрузиться с backup.md - lang backup");
            throw new RuntimeException(e);
        }
        try {
            oIS.close();
        } catch (IOException e) {
            System.out.println("Ошибка при чтении словаря (закрытие ObjectInputStream)");
            throw new RuntimeException(e);
        }
        return languager;
    }

}
