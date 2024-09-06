package ru.checkdev.notification.telegram.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 3. Мидл
 * Класс дополнительных функций телеграм бота, проверка почты, генерация пароля.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
public class TgConfig {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}");
    private final String prefix;
    private final int passSize;

    public TgConfig(String prefix, int passSize) {
        this.prefix = "tg/";
        this.passSize = 8;
    }

    /**
     * Метод проверяет входящую строку на соответствие формату email
     *
     * @param email String
     * @return boolean
     */
    public boolean isEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * метод генерирует пароль для пользователя
     *
     * @return String
     */
    public String getPassword() {
        String password = prefix + UUID.randomUUID();
        return password.substring(0, passSize);
    }

    /**
     * Метод преобразовывает Object в карту Map<String,String>
     *
     * @param object Object or Person(Auth)
     * @return Map
     */
    public Map<String, String> getObjectToMap(Object object) {
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return MAPPER.convertValue(object, Map.class);
    }

    /**
     * Метод преобразовывает Message в карту Map<String,String>
     * с FIO и email.
     *
     */
    public Map<String, String> parseMessageFioAndEmail(Message message) {
        Map<String, String> result = checkFormat(message.getText());
        if (!result.isEmpty()) {
            result.put("fio", result.get("first"));
            result.put("email", result.get("second"));
        }
        return result;
    }

    /**
     * Метод преобразовывает Message в карту Map<String,String>
     * с login и password.
     *
     */
    public Map<String, String> parseMessageLoginAndPassword(Message message) {
        Map<String, String> result = checkFormat(message.getText());
        if (!result.isEmpty()) {
            result.put("login", result.get("first"));
            result.put("password", result.get("second"));
        }
        return result;
    }

    /**
     * Проверка правильности введенных данных согласно формату.
     */
    private Map<String, String> checkFormat(String data) {
        Map<String, String> result = new HashMap<>();
        int index = data.indexOf(" ");
        if (index != -1 && index != 0 && index != data.length() - 1) {
            String first = data.substring(0, index);
            String second = data.substring(index + 1);
            if (!first.contains(" ") && !second.contains(" ")) {
                result.put("first", first);
                result.put("second", second);
            }
        }
        return result;
    }
}
