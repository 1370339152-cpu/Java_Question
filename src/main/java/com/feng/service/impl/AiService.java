package com.feng.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/*
* 大部分是api，是连接智普ai固定模板，用了线程池进行异步调用
* */
@Slf4j
@Service
public class AiService {
    //这个ai的固定路径
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    //这是我的密钥，你可以直接用，如果你想用你的可以去智普ai的官网创建一个，要实名制后才能用
    private static final String API_KEY = "5b28887d015f47169b7c6520105005e5.kLkcvRgwAlD87ty5";
    private final OkHttpClient client;
    private final Gson gson;

    public AiService() {
        //限制了输出时间，超过会报错
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    /*
    * 异步调用AI接口（@Async指定线程池）
    * question: 问题
    * userAnswer: 答案
    * 上述二者均为controller层传过来的
    * */
    @Async("aiAsyncPool")
    public CompletableFuture<Map<String, Object>> getAiFeedback(String question, String userAnswer) {
        //答案存储map
        Map<String, Object> result = new HashMap<>();
        try {
            // 构建请求体
            JsonObject requestBody = new JsonObject();
            //value：它的模型，可以进行更换，这个最快
            requestBody.addProperty("model", "glm-4v-flash");

            JsonArray messages = new JsonArray();
            JsonObject userMsg = new JsonObject();
            userMsg.addProperty("role", "user");
            userMsg.addProperty("content", "以面试官的角度帮我看看这道题回答的如何，并严格按照10分满分进行打分，这是题目:" + question + "，这是我的答案:" + userAnswer);
            messages.add(userMsg);
            requestBody.add("messages", messages);

            // 构建请求
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .post(RequestBody.create(
                            MediaType.parse("application/json"),
                            gson.toJson(requestBody)
                    ))
                    .build();

            // 同步调用但运行在异步线程中（不阻塞Tomcat工作线程）
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("请求失败: " + response);
                }
                // 解析响应
                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                String pureText = jsonObject
                        .getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();
                result.put("success", true);
                result.put("correctAnswer", pureText);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "AI反馈生成失败：" + e.getMessage());
            e.printStackTrace();
        }
        log.info("生成的答案为:{}",result);
        //将最后答案进行提交
        return CompletableFuture.completedFuture(result);
    }
}