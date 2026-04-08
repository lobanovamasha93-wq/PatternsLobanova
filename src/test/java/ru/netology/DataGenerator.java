package ru.netology;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataGenerator {
    private static final Faker faker = new Faker(new Locale("ru"));

    private DataGenerator() {
    }

    public static String generateDate(int shift) {
        return LocalDate.now().plusDays(shift).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String generateCity() {
        // Список городов, которые точно поддерживает SUT (административные центры)
        String[] cities = new String[]{"Москва", "Санкт-Петербург", "Казань", "Нижний Новгород", "Новосибирск", "Екатеринбург"};
        return cities[faker.random().nextInt(cities.length)];
    }

    public static String generateName() {
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    public static String generatePhone() {
        return faker.phoneNumber().phoneNumber().replaceAll("[^0-9+]", "");
    }

    public static class Registration {
        private Registration() {
        }

        public static UserInfo generateUser() {
            return new UserInfo(generateCity(), generateName(), generatePhone());
        }
    }

    @Value
    public static class UserInfo {
        String city;
        String name;
        String phone;
    }
}