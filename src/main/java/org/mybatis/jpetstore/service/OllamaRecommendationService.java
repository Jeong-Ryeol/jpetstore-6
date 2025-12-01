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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import org.mybatis.jpetstore.domain.Account;
import org.mybatis.jpetstore.domain.Category;
import org.mybatis.jpetstore.domain.Product;
import org.mybatis.jpetstore.domain.ProductCharacteristics;
import org.mybatis.jpetstore.domain.agent.FunctionCall;
import org.mybatis.jpetstore.domain.agent.Message;
import org.mybatis.jpetstore.domain.agent.OllamaChatResponse;
import org.mybatis.jpetstore.domain.agent.Tool;
import org.mybatis.jpetstore.domain.agent.ToolCall;
import org.mybatis.jpetstore.domain.recommendation.CategoryRecommendation;
import org.springframework.stereotype.Service;

/**
 * Ollama + Function Calling 기반 추천 서비스 AI Agent가 도구를 호출하여 DB 데이터를 조회하고 추천을 생성
 */
@Service
public class OllamaRecommendationService {

  private final OllamaClient ollamaClient;
  private final GeminiClient geminiClient;
  private final CatalogService catalogService;
  private final AgentToolRegistry toolRegistry;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final int MAX_ITERATIONS = 5;

  // 로그 저장용 (세션 ID별로 관리)
  private static final java.util.concurrent.ConcurrentHashMap<String, StringBuilder> sessionLogs = new java.util.concurrent.ConcurrentHashMap<>();
  private static final ThreadLocal<String> currentSessionId = new ThreadLocal<>();

  public void setSessionId(String sessionId) {
    currentSessionId.set(sessionId);
    sessionLogs.put(sessionId, new StringBuilder());
  }

  public String getLogBySessionId(String sessionId) {
    StringBuilder log = sessionLogs.get(sessionId);
    return log != null ? log.toString() : "";
  }

  public String getLastAgentLog() {
    String sessionId = currentSessionId.get();
    if (sessionId != null) {
      return getLogBySessionId(sessionId);
    }
    return "";
  }

  private void log(String message) {
    String sessionId = currentSessionId.get();
    if (sessionId != null) {
      StringBuilder sb = sessionLogs.get(sessionId);
      if (sb != null) {
        sb.append(message).append("\n");
      }
    }
    System.out.println(message); // 터미널에도 출력
  }

  private void clearLog() {
    String sessionId = currentSessionId.get();
    if (sessionId != null) {
      sessionLogs.put(sessionId, new StringBuilder());
    }
  }

  public void cleanupSession(String sessionId) {
    sessionLogs.remove(sessionId);
  }

  private static final String SYSTEM_PROMPT = """
      You are a pet recommendation AI agent with database access.

      IMPORTANT WORKFLOW:
      1. FIRST, you MUST call getCategories tool to get available categories
      2. THEN, call getProductsByCategory tool for EACH category to get pet breed data
      3. FINALLY, after analyzing all data, output your recommendation as JSON

      DO NOT skip tool calls. You MUST actually call the tools, not just output tool names as text.

      FINAL OUTPUT FORMAT (only after calling tools):
      You MUST output EXACTLY 5 categories (ALL categories: FISH, DOGS, CATS, REPTILES, BIRDS).
      [{"categoryId":"CATS","categoryName":"Cats","confidence":90,"reason":"2-3 sentences"},
       {"categoryId":"DOGS","categoryName":"Dogs","confidence":80,"reason":"2-3 sentences"},
       {"categoryId":"FISH","categoryName":"Fish","confidence":70,"reason":"2-3 sentences"},
       {"categoryId":"BIRDS","categoryName":"Birds","confidence":60,"reason":"2-3 sentences"},
       {"categoryId":"REPTILES","categoryName":"Reptiles","confidence":50,"reason":"2-3 sentences"}]

      RULES:
      - You MUST include ALL 5 categories in your response
      - reason in English (2-3 sentences based on user lifestyle and pet characteristics)
      - confidence: 0-100 integer (different for each category)
      - categoryId must be one of: FISH, DOGS, CATS, REPTILES, BIRDS
      """;

  public OllamaRecommendationService(OllamaClient ollamaClient, GeminiClient geminiClient,
      CatalogService catalogService, AgentToolRegistry toolRegistry) {
    this.ollamaClient = ollamaClient;
    this.geminiClient = geminiClient;
    this.catalogService = catalogService;
    this.toolRegistry = toolRegistry;
  }

  /**
   * 사용자 라이프스타일 기반 Top 5 카테고리 추천
   */
  public List<CategoryRecommendation> getTop5Categories(Account account) {
    // 로그 초기화
    clearLog();

    // 사용자 프롬프트 생성
    String userPrompt = buildUserPrompt(account);

    // Agent 루프 실행
    String response = runAgentLoop(userPrompt);

    // JSON 파싱
    return parseRecommendations(response);
  }

  /**
   * Agent 루프 실행 - 도구 호출 처리
   */
  private String runAgentLoop(String userPrompt) {
    log("================================================================");
    log("  Local LLM Agent (Ollama llama3.1:8b) 시작");
    log("  실시간 DB 연동 Function Calling 방식");
    log("================================================================");
    log("");
    log("[사용자 라이프스타일 정보 수신]");
    log(userPrompt.length() > 300 ? userPrompt.substring(0, 300) + "..." : userPrompt);

    List<Message> messages = new ArrayList<>();
    messages.add(new Message("system", SYSTEM_PROMPT));
    messages.add(new Message("user", userPrompt));

    List<Tool> tools = toolRegistry.getAllTools();

    for (int i = 0; i < MAX_ITERATIONS; i++) {
      log("");
      log("────────────────────────────────────────────────────────────");
      log("[Agent 반복 " + (i + 1) + "/" + MAX_ITERATIONS + "] LLM에게 판단 요청 중...");
      log("────────────────────────────────────────────────────────────");

      OllamaChatResponse response = ollamaClient.chatWithTools(messages, tools);
      Message assistantMessage = response.getMessage();
      messages.add(assistantMessage);

      // 도구 호출이 없으면 텍스트에서 tool call 패턴 확인
      if (assistantMessage.getToolCalls() == null || assistantMessage.getToolCalls().isEmpty()) {
        String content = assistantMessage.getContent();

        // 텍스트에서 tool call 패턴 감지 (LLM이 텍스트로 tool call을 출력한 경우)
        List<ToolCall> textToolCalls = parseToolCallsFromText(content);
        if (!textToolCalls.isEmpty()) {
          log("");
          log("[텍스트에서 Tool Call 감지] " + textToolCalls.size() + "개의 DB 조회 요청");
          for (ToolCall toolCall : textToolCalls) {
            String toolResult = executeToolCall(toolCall);
            Message toolMessage = new Message("tool", toolResult);
            messages.add(toolMessage);
          }
          // tool 결과를 받은 후 JSON 출력을 요청하는 메시지 추가
          messages.add(new Message("user",
              "You have received all the data. Now output your final JSON recommendation with ALL 5 categories. Start your response with '[' character. No explanation, just JSON array."));
          continue; // 다음 반복으로
        }

        // 진짜 최종 응답
        log("");
        log("[LLM 최종 분석 완료]");
        log("원본 응답:");
        log(content != null ? content : "(null)");
        log("");
        String extracted = ollamaClient.extractJsonFromResponse(content);
        log("추출된 JSON:");
        log(extracted != null ? extracted : "(null)");
        log("");
        log("================================================================");
        log("  Agent 작업 완료 - DB 조회 -> LLM 분석 -> 추천 생성");
        log("================================================================");
        return extracted;
      }

      // 도구 호출 처리
      log("");
      log("[Function Calling 감지] LLM이 " + assistantMessage.getToolCalls().size() + "개의 DB 조회 요청");
      for (ToolCall toolCall : assistantMessage.getToolCalls()) {
        String toolResult = executeToolCall(toolCall);
        Message toolMessage = new Message("tool", toolResult);
        messages.add(toolMessage);
      }
    }

    throw new RuntimeException("Agent loop exceeded maximum iterations");
  }

  /**
   * 텍스트에서 tool call 패턴 파싱 (LLM이 tool_calls 대신 텍스트로 출력한 경우)
   */
  private List<ToolCall> parseToolCallsFromText(String content) {
    List<ToolCall> toolCalls = new ArrayList<>();
    if (content == null || content.isEmpty()) {
      return toolCalls;
    }

    // {"name": "getCategories", "parameters": {}} 패턴 찾기
    java.util.regex.Pattern pattern = java.util.regex.Pattern
        .compile("\\{\\s*\"name\"\\s*:\\s*\"(\\w+)\"\\s*,\\s*\"parameters\"\\s*:\\s*\\{([^}]*)\\}\\s*\\}");
    java.util.regex.Matcher matcher = pattern.matcher(content);

    while (matcher.find()) {
      String functionName = matcher.group(1);
      String paramsStr = matcher.group(2).trim();

      // 유효한 tool인지 확인
      if (functionName.equals("getCategories") || functionName.equals("getProductsByCategory")
          || functionName.equals("getProductWithCharacteristics")) {

        ToolCall toolCall = new ToolCall();
        FunctionCall functionCall = new FunctionCall();
        functionCall.setName(functionName);

        // parameters 파싱
        Map<String, Object> args = new HashMap<>();
        if (!paramsStr.isEmpty()) {
          // "categoryId": "CATS" 같은 패턴 파싱
          java.util.regex.Pattern paramPattern = java.util.regex.Pattern.compile("\"(\\w+)\"\\s*:\\s*\"([^\"]+)\"");
          java.util.regex.Matcher paramMatcher = paramPattern.matcher(paramsStr);
          while (paramMatcher.find()) {
            args.put(paramMatcher.group(1), paramMatcher.group(2));
          }
        }
        functionCall.setArguments(args);
        toolCall.setFunction(functionCall);
        toolCalls.add(toolCall);
      }
    }

    return toolCalls;
  }

  /**
   * 도구 호출 실행
   */
  private String executeToolCall(ToolCall toolCall) {
    String functionName = toolCall.getFunction().getName();
    Map<String, Object> args = toolCall.getFunction().getArguments();

    try {
      String result;
      switch (functionName) {
        case "getCategories":
          log("   +----------------------------------------------------------");
          log("   | [DB] SELECT * FROM CATEGORY");
          log("   | [실행] MyBatis -> HSQLDB");
          result = executeGetCategories();
          log("   | [완료] 전체 카테고리 목록 반환");
          log("   +----------------------------------------------------------");
          return result;
        case "getProductsByCategory":
          String categoryId = (String) args.get("categoryId");
          log("   +----------------------------------------------------------");
          log("   | [DB] SELECT * FROM PRODUCT WHERE CATEGORY = '" + categoryId + "'");
          log("   | [DB] SELECT * FROM PRODUCT_CHARACTERISTICS");
          log("   | [실행] MyBatis -> HSQLDB");
          result = executeGetProductsByCategory(categoryId);
          log("   | [완료] " + categoryId + " 카테고리의 품종 및 특성 데이터 반환");
          log("   +----------------------------------------------------------");
          return result;
        case "getProductWithCharacteristics":
          String productId = (String) args.get("productId");
          log("   +----------------------------------------------------------");
          log("   | [DB] SELECT * FROM PRODUCT WHERE ID = '" + productId + "'");
          log("   | [실행] MyBatis -> HSQLDB");
          result = executeGetProductWithCharacteristics(productId);
          log("   | [완료] 품종 상세 정보 반환");
          log("   +----------------------------------------------------------");
          return result;
        default:
          return "{\"error\": \"Unknown function: " + functionName + "\"}";
      }
    } catch (Exception e) {
      return "{\"error\": \"" + e.getMessage() + "\"}";
    }
  }

  private String executeGetCategories() {
    try {
      List<Category> categories = catalogService.getCategoryList();
      List<Map<String, Object>> result = new ArrayList<>();

      for (Category cat : categories) {
        Map<String, Object> catMap = new HashMap<>();
        catMap.put("categoryId", cat.getCategoryId());
        catMap.put("name", cat.getName());
        catMap.put("description", cat.getDescription());
        result.add(catMap);
      }

      return objectMapper.writeValueAsString(result);
    } catch (Exception e) {
      return "{\"error\": \"" + e.getMessage() + "\"}";
    }
  }

  private String executeGetProductsByCategory(String categoryId) {
    try {
      List<Product> products = catalogService.getProductListByCategory(categoryId);
      List<ProductCharacteristics> characteristics = catalogService.getCharacteristicsByCategory(categoryId);

      // 특성 정보를 productId로 매핑
      Map<String, ProductCharacteristics> charMap = new HashMap<>();
      for (ProductCharacteristics pc : characteristics) {
        charMap.put(pc.getProductId(), pc);
      }

      List<Map<String, Object>> result = new ArrayList<>();
      for (Product product : products) {
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("productId", product.getProductId());
        productMap.put("name", product.getName());
        productMap.put("categoryId", product.getCategoryId());

        ProductCharacteristics pc = charMap.get(product.getProductId());
        if (pc != null) {
          productMap.put("careLevel", pc.getCareLevel());
          productMap.put("spaceRequirement", pc.getSpaceRequirement());
          productMap.put("monthlyCost", pc.getMonthlyCost());
          productMap.put("allergyRisk", pc.getAllergyRisk());
          productMap.put("noiseLevel", pc.getNoiseLevel());
          productMap.put("activityLevel", pc.getActivityLevel());
          productMap.put("socialNeed", pc.getSocialNeed());
          productMap.put("trainingDifficulty", pc.getTrainingDifficulty());
          productMap.put("lifespan", pc.getLifespan());
          productMap.put("suitableForStudio", pc.getSuitableForStudio());
          productMap.put("suitableForApartment", pc.getSuitableForApartment());
          productMap.put("suitableForHouse", pc.getSuitableForHouse());
          productMap.put("suitableForBusy", pc.getSuitableForBusy());
          productMap.put("childFriendly", pc.getChildFriendly());
          productMap.put("temperament", pc.getTemperament());
          productMap.put("description", pc.getDescription());
        }

        result.add(productMap);
      }

      return objectMapper.writeValueAsString(result);
    } catch (Exception e) {
      return "{\"error\": \"" + e.getMessage() + "\"}";
    }
  }

  private String executeGetProductWithCharacteristics(String productId) {
    try {
      Product product = catalogService.getProduct(productId);
      ProductCharacteristics pc = catalogService.getProductCharacteristics(productId);

      Map<String, Object> result = new HashMap<>();
      if (product != null) {
        result.put("productId", product.getProductId());
        result.put("name", product.getName());
        result.put("categoryId", product.getCategoryId());
      }

      if (pc != null) {
        result.put("careLevel", pc.getCareLevel());
        result.put("spaceRequirement", pc.getSpaceRequirement());
        result.put("monthlyCost", pc.getMonthlyCost());
        result.put("allergyRisk", pc.getAllergyRisk());
        result.put("noiseLevel", pc.getNoiseLevel());
        result.put("activityLevel", pc.getActivityLevel());
        result.put("socialNeed", pc.getSocialNeed());
        result.put("trainingDifficulty", pc.getTrainingDifficulty());
        result.put("lifespan", pc.getLifespan());
        result.put("suitableForStudio", pc.getSuitableForStudio());
        result.put("suitableForApartment", pc.getSuitableForApartment());
        result.put("suitableForHouse", pc.getSuitableForHouse());
        result.put("suitableForBusy", pc.getSuitableForBusy());
        result.put("childFriendly", pc.getChildFriendly());
        result.put("temperament", pc.getTemperament());
        result.put("description", pc.getDescription());
      }

      return objectMapper.writeValueAsString(result);
    } catch (Exception e) {
      return "{\"error\": \"" + e.getMessage() + "\"}";
    }
  }

  private String buildUserPrompt(Account account) {
    StringBuilder sb = new StringBuilder();

    sb.append("User profile: ");
    sb.append("age=").append(account.getAge() != null ? account.getAge() : "unknown");
    sb.append(", occupation=").append(account.getOccupation() != null ? account.getOccupation() : "unknown");
    sb.append(", homeHours=").append(getHomeHoursDisplayEn(account.getHomeHours()));
    sb.append(", housing=").append(getHousingTypeDisplayEn(account.getHousingType()));
    sb.append(", budget=").append(getMonthlyBudgetDisplayEn(account.getMonthlyBudget()));
    sb.append(", hasAllergy=").append(account.getHasAllergy() != null && account.getHasAllergy() ? "yes" : "no");
    sb.append("\n\n");

    sb.append("Please recommend 5 pet categories for this user.\n\n");

    sb.append("STEP 1: Call getCategories tool NOW to see available categories.\n");
    sb.append("STEP 2: Call getProductsByCategory for each category to get breed details.\n");
    sb.append("STEP 3: After receiving all data, output final JSON recommendation.\n");

    return sb.toString();
  }

  private String getHomeHoursDisplayEn(String homeHours) {
    if (homeHours == null)
      return "unknown";
    switch (homeHours) {
      case "LESS_THAN_2":
        return "less than 2 hours";
      case "TWO_TO_SIX":
        return "2-6 hours";
      case "MORE_THAN_6":
        return "more than 6 hours";
      default:
        return homeHours;
    }
  }

  private String getHousingTypeDisplayEn(String housingType) {
    if (housingType == null)
      return "unknown";
    switch (housingType) {
      case "STUDIO":
        return "studio";
      case "APARTMENT":
        return "apartment";
      case "HOUSE":
        return "house";
      default:
        return housingType;
    }
  }

  private String getMonthlyBudgetDisplayEn(String monthlyBudget) {
    if (monthlyBudget == null)
      return "unknown";
    switch (monthlyBudget) {
      case "UNDER_100K":
        return "under 100K KRW";
      case "BETWEEN_100K_300K":
        return "100K-300K KRW";
      case "OVER_300K":
        return "over 300K KRW";
      default:
        return monthlyBudget;
    }
  }

  private String getHomeHoursDisplay(String homeHours) {
    if (homeHours == null)
      return "미제공";
    switch (homeHours) {
      case "LESS_THAN_2":
        return "2시간 미만";
      case "TWO_TO_SIX":
        return "2-6시간";
      case "MORE_THAN_6":
        return "6시간 이상";
      default:
        return homeHours;
    }
  }

  private String getHousingTypeDisplay(String housingType) {
    if (housingType == null)
      return "미제공";
    switch (housingType) {
      case "STUDIO":
        return "원룸";
      case "APARTMENT":
        return "아파트";
      case "HOUSE":
        return "단독주택";
      default:
        return housingType;
    }
  }

  private String getMonthlyBudgetDisplay(String monthlyBudget) {
    if (monthlyBudget == null)
      return "미제공";
    switch (monthlyBudget) {
      case "UNDER_100K":
        return "10만원 미만";
      case "BETWEEN_100K_300K":
        return "10-30만원";
      case "OVER_300K":
        return "30만원 이상";
      default:
        return monthlyBudget;
    }
  }

  private List<CategoryRecommendation> parseRecommendations(String jsonResponse) {
    List<CategoryRecommendation> recommendations = new ArrayList<>();

    try {
      JsonNode rootNode = objectMapper.readTree(jsonResponse);

      Set<String> validCategories = Set.of("FISH", "DOGS", "CATS", "REPTILES", "BIRDS");

      if (rootNode.isArray()) {
        for (JsonNode node : rootNode) {
          // 필수 필드 확인 (categoryName과 reason이 있어야 유효한 추천)
          if (node.has("categoryName") && node.has("reason") && node.has("confidence")) {
            String categoryId = node.get("categoryId").asText();

            // 유효한 카테고리인지 확인
            if (validCategories.contains(categoryId)) {
              CategoryRecommendation recommendation = new CategoryRecommendation();
              recommendation.setCategoryId(categoryId);
              recommendation.setCategoryName(node.get("categoryName").asText());

              // confidence가 0-1 소수면 100을 곱함
              double conf = node.get("confidence").asDouble();
              recommendation.setConfidence(conf <= 1 ? (int) (conf * 100) : (int) conf);

              // 영어 reason을 한국어로 번역
              String englishReason = node.get("reason").asText();
              String koreanReason = translateToKorean(englishReason);
              recommendation.setReason(koreanReason);
              recommendations.add(recommendation);
            }
          }
        }
      }

      if (recommendations.size() < 5) {
        log("[경고] 유효한 추천이 " + recommendations.size() + "개만 파싱됨");
      }

      // confidence 기준 내림차순 정렬
      recommendations.sort((a, b) -> Integer.compare(b.getConfidence(), a.getConfidence()));

    } catch (Exception e) {
      throw new RuntimeException("Failed to parse Ollama response: " + jsonResponse, e);
    }

    return recommendations;
  }

  private String translateToKorean(String englishText) {
    try {
      log("[번역] 영어 -> 한국어: " + englishText.substring(0, Math.min(50, englishText.length())) + "...");
      String prompt = "Translate the following English text to Korean. Output ONLY the Korean translation, nothing else:\n\n"
          + englishText;
      String korean = geminiClient.chat(prompt);
      log("[번역 완료] " + korean.substring(0, Math.min(50, korean.length())) + "...");
      return korean;
    } catch (Exception e) {
      log("[번역 실패] " + e.getMessage());
      return englishText; // 실패 시 영어 그대로 반환
    }
  }
}
