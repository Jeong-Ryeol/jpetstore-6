/*
 *    Copyright 2010-2025 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.jpetstore.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Service;

/**
 * AI-powered chatbot service using Google Gemini API.
 *
 * @author JPetStore Team
 */
@Service
public class ChatbotService {

  private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent";
  private static final String API_KEY_ENV = "GEMINI_API_KEY";

  private final HttpClient httpClient;
  private final String apiKey;

  public ChatbotService() {
    this.httpClient = HttpClient.newHttpClient();
    this.apiKey = System.getenv(API_KEY_ENV);
  }

  /**
   * Get AI chatbot response for user question.
   *
   * @param userMessage
   *          the user's question
   *
   * @return AI response string
   */
  public String getChatResponse(String userMessage) {
    // API 키가 설정되지 않은 경우 데모 모드
    if (apiKey == null || apiKey.isEmpty()) {
      return getDemoResponse(userMessage);
    }

    try {
      String prompt = buildChatPrompt(userMessage);
      String requestBody = buildRequestBody(prompt);

      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(GEMINI_API_URL + "?key=" + apiKey))
          .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        return parseResponse(response.body());
      } else {
        System.err.println("Gemini API Error: " + response.statusCode() + " - " + response.body());
        return getDemoResponse(userMessage);
      }

    } catch (Exception e) {
      System.err.println("Error calling Gemini API: " + e.getMessage());
      e.printStackTrace();
      return getDemoResponse(userMessage);
    }
  }

  /**
   * Build a chat prompt for the AI.
   */
  private String buildChatPrompt(String userMessage) {
    return String.format(
        "당신은 JPetStore 애완동물 쇼핑몰의 친절한 AI 고객 지원 도우미입니다.\n\n" + "다음 질문에 친절하고 도움이 되는 답변을 제공해주세요:\n\n" + "고객 질문: %s\n\n"
            + "답변 시 다음을 고려해주세요:\n" + "1. 애완동물 용품에 대한 전문적인 조언\n" + "2. JPetStore에서 판매하는 상품 (물고기, 강아지, 고양이, 새, 파충류 용품)\n"
            + "3. 간단명료하고 친근한 톤\n" + "4. 필요시 구체적인 상품 추천\n\n" + "3-4문장 이내로 답변해주세요.",
        userMessage);
  }

  /**
   * Build JSON request body for Gemini API.
   */
  private String buildRequestBody(String prompt) {
    String escapedPrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")
        .replace("\t", "\\t");

    return String.format("{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}", escapedPrompt);
  }

  /**
   * Parse the Gemini API response.
   */
  private String parseResponse(String responseBody) {
    try {
      int textStart = responseBody.indexOf("\"text\": \"");
      if (textStart != -1) {
        textStart += 9;
        int textEnd = responseBody.indexOf("\"", textStart);

        if (textEnd != -1) {
          String content = responseBody.substring(textStart, textEnd);
          content = content.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
          return content.trim();
        }
      }

      return "죄송합니다. 응답을 처리하는 중 문제가 발생했습니다.";

    } catch (Exception e) {
      System.err.println("Error parsing Gemini response: " + e.getMessage());
      return "죄송합니다. 응답을 처리하는 중 문제가 발생했습니다.";
    }
  }

  /**
   * Provide demo responses when API is not available.
   */
  private String getDemoResponse(String userMessage) {
    String lowerMessage = userMessage.toLowerCase();

    if (lowerMessage.contains("배송") || lowerMessage.contains("delivery") || lowerMessage.contains("shipping")) {
      return "JPetStore는 전국 어디든 2-3일 내에 배송해드립니다. 50,000원 이상 구매 시 무료 배송 혜택을 받으실 수 있습니다!";
    } else if (lowerMessage.contains("반품") || lowerMessage.contains("교환") || lowerMessage.contains("return")) {
      return "상품 수령 후 7일 이내에 반품 및 교환이 가능합니다. 단, 상품이 미개봉 상태여야 하며, 반려동물 사료의 경우 유통기한이 충분해야 합니다.";
    } else if (lowerMessage.contains("추천") || lowerMessage.contains("recommend") || lowerMessage.contains("어떤")
        || lowerMessage.contains("좋은")) {
      return "애완동물 종류에 따라 추천 상품이 다릅니다! 물고기를 키우신다면 고급 어류 사료와 여과 시스템을, 강아지라면 프리미엄 사료와 장난감을 추천드립니다. 상품 페이지에서 AI 추천 기능도 확인해보세요!";
    } else if (lowerMessage.contains("가격") || lowerMessage.contains("price") || lowerMessage.contains("할인")
        || lowerMessage.contains("discount")) {
      return "JPetStore는 합리적인 가격으로 다양한 애완동물 용품을 제공합니다. 회원 가입 시 첫 구매 10% 할인 쿠폰을 드립니다!";
    } else if (lowerMessage.contains("사료") || lowerMessage.contains("food") || lowerMessage.contains("먹이")) {
      return "저희는 각 동물별로 특화된 고품질 사료를 판매하고 있습니다. 영양 균형이 잘 맞춰진 프리미엄 사료부터 기본 사료까지 다양하게 준비되어 있습니다.";
    } else if (lowerMessage.contains("안녕") || lowerMessage.contains("hello") || lowerMessage.contains("hi")) {
      return "안녕하세요! JPetStore AI 고객 지원입니다. 무엇을 도와드릴까요? 상품 문의, 배송, 반품 등 궁금하신 점을 말씀해주세요!";
    } else {
      return "JPetStore에 오신 것을 환영합니다! 물고기, 강아지, 고양이, 새, 파충류 용품을 판매하고 있습니다. 구체적으로 어떤 도움이 필요하신가요? 상품 추천, 배송, 반품 등에 대해 문의해주세요!";
    }
  }
}
