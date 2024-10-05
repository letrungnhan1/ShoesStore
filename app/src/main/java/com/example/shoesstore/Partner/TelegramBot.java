package com.example.shoesstore.Partner;
import com.example.shoesstore.Api.RetrofitClient;
import com.example.shoesstore.Api.TelegramBotAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TelegramBot {

    private static final String BASE_URL = "https://api.telegram.org/bot";
    private TelegramBotAPI apiService;

    public TelegramBot(String botToken) {
        apiService = RetrofitClient.getClient(BASE_URL + botToken + "/").create(TelegramBotAPI.class);
    }

    public void sendMessage(String chatId, String message) {
        Call<Void> call = apiService.sendMessage(
                chatId,
                message,
                "HTML",
                true
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    System.out.println("Message sent successfully!");
                } else {
                    System.out.println("Failed to send message: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public String formatOrderMessage(String orderId, String customerName, String paymentMethod, String formattedAmount, String orderItemsDetails) {
        StringBuilder message = new StringBuilder();
        message.append("<b>📝 New Order</b>\n\n")
                .append("<b>Order ID:</b> ").append(orderId).append("\n")
                .append("<b>Customer:</b> ").append(customerName).append("\n")
                .append("<b>Payment Method:</b> ").append(paymentMethod).append("\n")
                .append("<b>Amount:</b> ").append(formattedAmount).append("\n")
                .append("<b>Order Items:</b>\n").append(orderItemsDetails);
        return message.toString();
    }

}
