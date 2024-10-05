package com.example.shoesstore.Api;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TelegramBotAPI {
    @GET("sendMessage")
    Call<Void> sendMessage(
            @Query("chat_id") String chatId,
            @Query("text") String text,
            @Query("parse_mode") String parseMode,
            @Query("disable_web_page_preview") boolean disableWebPagePreview
    );
}
