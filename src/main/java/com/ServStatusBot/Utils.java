package com.ServStatusBot;

import com.ServStatusBot.model.Url;
import com.ServStatusBot.model.User;
import com.ServStatusBot.service.UrlService;
import com.ServStatusBot.service.UserService;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class Utils {

    @Autowired
    private final UserService userService;

    @Autowired
    private final UrlService urlService;

    @Transactional
    public void createUserAndUrl(Long chatId, String url, String name,
                                 Long interval, Boolean isMonitoring) {

        if (userService.findByChatId(chatId) == null) {
            User newUser = new User();
            newUser.setUserName(name);
            newUser.setChatId(chatId);

            Url newUrl = new Url();
            List<Url> urls = new ArrayList<>();
            newUrl.setUrl(url);
            newUrl.setInterval(interval);
            newUrl.setIsMonitoring(isMonitoring);
            urls.add(newUrl);
            newUser.setUrls(urls);

            urlService.saveUrl(newUrl);
            userService.saveUser(newUser);

        } else {

            User currentUser = userService.findByChatId(chatId);
            List<Url> currentUserUrls = currentUser.getUrls();
            Url newUrl = new Url();
            newUrl.setUrl(url);
            newUrl.setInterval(interval);
            newUrl.setIsMonitoring(isMonitoring);
            currentUserUrls.add(newUrl);
            currentUser.setUrls(currentUserUrls);

            urlService.saveUrl(newUrl);
            userService.saveUser(currentUser);
        }
    }
    public String getGtpAnswer(StringBuilder response) {
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray choicesArray = jsonObject.getJSONArray("choices");
            if (!choicesArray.isEmpty()) {
                JSONObject choice = choicesArray.getJSONObject(0);
                JSONObject message = choice.getJSONObject("message");
                return message.getString("content");
            }
        } catch (JSONException e) {
            // Обработка ошибки парсинга JSON
            e.printStackTrace();
        }
        return ""; // Возвращаем пустую строку, если нет данных или произошла ошибка
    }
}
