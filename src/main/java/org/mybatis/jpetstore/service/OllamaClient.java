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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import org.mybatis.jpetstore.domain.agent.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Ollama REST API 클라이언트 (Function Calling 지원) Llama 3.1의 Tool Use 기능을 활용
 */
public class OllamaClient {

  private String baseUrl = "http://localhost:11434";
  private String model = "llama3.1:8b";
  private int timeoutMs = 120000; // 2분 (로컬 LLM은 느릴 수 있음)

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  public OllamaClient() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(timeoutMs);
    factory.setReadTimeout(timeoutMs);
    this.restTemplate = new RestTemplate(factory);

    this.objectMapper = new ObjectMapper();
    this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public void setTimeoutMs(int timeoutMs) {
    this.timeoutMs = timeoutMs;
  }

  /**
   * Tool Calling을 지원하는 채팅 API 호출
   *
   * @param messages
   *          대화 히스토리
   * @param tools
   *          사용 가능한 도구 목록
   *
   * @return OllamaChatResponse (message 또는 tool_calls 포함)
   */
  public OllamaChatResponse chatWithTools(List<Message> messages, List<Tool> tools) {
    try {
      Map<String, Object> requestBody = new HashMap<>();
      requestBody.put("model", model);
      requestBody.put("stream", false);

      // messages 변환
      List<Map<String, Object>> messagesList = new ArrayList<>();
      for (Message msg : messages) {
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("role", msg.getRole());
        msgMap.put("content", msg.getContent());
        if (msg.getToolCalls() != null && !msg.getToolCalls().isEmpty()) {
          msgMap.put("tool_calls", msg.getToolCalls());
        }
        messagesList.add(msgMap);
      }
      requestBody.put("messages", messagesList);

      // tools 변환
      if (tools != null && !tools.isEmpty()) {
        List<Map<String, Object>> toolsList = new ArrayList<>();
        for (Tool tool : tools) {
          Map<String, Object> toolMap = new HashMap<>();
          toolMap.put("type", tool.getType());

          Map<String, Object> functionMap = new HashMap<>();
          functionMap.put("name", tool.getFunction().getName());
          functionMap.put("description", tool.getFunction().getDescription());
          functionMap.put("parameters", tool.getFunction().getParameters());

          toolMap.put("function", functionMap);
          toolsList.add(toolMap);
        }
        requestBody.put("tools", toolsList);
      }

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

      String url = baseUrl + "/api/chat";

      @SuppressWarnings("unchecked")
      Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

      return parseResponse(response);

    } catch (Exception e) {
      throw new RuntimeException("Ollama API call failed: " + e.getMessage(), e);
    }
  }

  /**
   * 단순 채팅 (도구 없이)
   */
  public String chat(String prompt) {
    List<Message> messages = new ArrayList<>();
    messages.add(new Message("user", prompt));

    OllamaChatResponse response = chatWithTools(messages, null);
    return response.getMessage().getContent();
  }

  /**
   * Ollama 응답 파싱
   */
  @SuppressWarnings("unchecked")
  private OllamaChatResponse parseResponse(Map<String, Object> response) {
    if (response == null) {
      throw new RuntimeException("Empty Ollama response");
    }

    OllamaChatResponse result = new OllamaChatResponse();
    result.setModel((String) response.get("model"));
    result.setDone(Boolean.TRUE.equals(response.get("done")));

    Map<String, Object> messageMap = (Map<String, Object>) response.get("message");
    if (messageMap != null) {
      Message message = new Message();
      message.setRole((String) messageMap.get("role"));
      message.setContent((String) messageMap.get("content"));

      // tool_calls 파싱
      List<Map<String, Object>> toolCallsList = (List<Map<String, Object>>) messageMap.get("tool_calls");
      if (toolCallsList != null && !toolCallsList.isEmpty()) {
        List<ToolCall> toolCalls = new ArrayList<>();
        for (Map<String, Object> tcMap : toolCallsList) {
          ToolCall toolCall = new ToolCall();

          Map<String, Object> funcMap = (Map<String, Object>) tcMap.get("function");
          if (funcMap != null) {
            FunctionCall functionCall = new FunctionCall();
            functionCall.setName((String) funcMap.get("name"));

            // arguments 파싱 - 문자열이면 JSON으로 파싱, Map이면 그대로 사용
            Object argsObj = funcMap.get("arguments");
            if (argsObj instanceof String) {
              try {
                Map<String, Object> args = objectMapper.readValue((String) argsObj, Map.class);
                functionCall.setArguments(args);
              } catch (Exception e) {
                functionCall.setArguments(new HashMap<>());
              }
            } else if (argsObj instanceof Map) {
              functionCall.setArguments((Map<String, Object>) argsObj);
            } else {
              functionCall.setArguments(new HashMap<>());
            }

            toolCall.setFunction(functionCall);
          }
          toolCalls.add(toolCall);
        }
        message.setToolCalls(toolCalls);
      }

      result.setMessage(message);
    }

    return result;
  }

  /**
   * JSON 응답에서 추천 결과 JSON 배열 추출 (confidence 필드가 있는 마지막 JSON)
   */
  public String extractJsonFromResponse(String response) {
    if (response == null || response.trim().isEmpty()) {
      return response;
    }

    String trimmed = response.trim();

    // confidence 필드가 포함된 JSON 블록 찾기 (추천 결과)
    // 마지막 코드블록에서 찾기
    int lastCodeBlockStart = trimmed.lastIndexOf("```json");
    if (lastCodeBlockStart >= 0) {
      int startIndex = lastCodeBlockStart + 7;
      int endIndex = trimmed.indexOf("```", startIndex);
      if (endIndex > startIndex) {
        String content = trimmed.substring(startIndex, endIndex).trim();
        if (content.contains("confidence")) {
          return content;
        }
      }
    }

    // ``` 코드블록 (json 태그 없이) - 마지막 블록
    int lastBacktickStart = trimmed.lastIndexOf("```");
    if (lastBacktickStart >= 0) {
      // 이전 ``` 찾기
      int prevBacktick = trimmed.lastIndexOf("```", lastBacktickStart - 1);
      if (prevBacktick >= 0) {
        String content = trimmed.substring(prevBacktick + 3, lastBacktickStart).trim();
        if (content.startsWith("[") && content.contains("confidence")) {
          return content;
        }
      }
    }

    // confidence가 포함된 마지막 JSON 배열 찾기
    int lastJsonStart = trimmed.lastIndexOf("[{\"categoryId\"");
    if (lastJsonStart < 0) {
      lastJsonStart = trimmed.lastIndexOf("[{");
    }

    if (lastJsonStart >= 0) {
      // 해당 위치부터 JSON 배열 끝 찾기
      int bracketCount = 0;
      int jsonEnd = -1;
      for (int i = lastJsonStart; i < trimmed.length(); i++) {
        char c = trimmed.charAt(i);
        if (c == '[')
          bracketCount++;
        else if (c == ']') {
          bracketCount--;
          if (bracketCount == 0) {
            jsonEnd = i;
            break;
          }
        }
      }
      if (jsonEnd > lastJsonStart) {
        String json = trimmed.substring(lastJsonStart, jsonEnd + 1);
        if (json.contains("confidence")) {
          return json;
        }
      }
    }

    // [ 로 시작하면 그대로 반환
    if (trimmed.startsWith("[")) {
      return trimmed;
    }

    return trimmed;
  }
}
