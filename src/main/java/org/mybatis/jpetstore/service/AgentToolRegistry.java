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

import java.util.*;

import org.mybatis.jpetstore.domain.agent.Tool;
import org.mybatis.jpetstore.domain.agent.ToolFunction;
import org.springframework.stereotype.Service;

/**
 * AI Agent가 사용할 수 있는 Tool 정의 레지스트리
 */
@Service
public class AgentToolRegistry {

  /**
   * 사용 가능한 모든 도구 정의 반환
   */
  public List<Tool> getAllTools() {
    return Arrays.asList(createGetCategoriesTool(), createGetProductsByCategoryTool(),
        createGetProductWithCharacteristicsTool());
  }

  /**
   * 카테고리 목록 조회 도구
   */
  private Tool createGetCategoriesTool() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("type", "object");
    parameters.put("properties", new HashMap<>());
    parameters.put("required", new ArrayList<>());

    ToolFunction function = new ToolFunction("getCategories",
        "JPetStore의 모든 반려동물 카테고리(FISH, DOGS, CATS, REPTILES, BIRDS) 목록을 조회합니다. 각 카테고리의 ID, 이름, 설명을 반환합니다.", parameters);

    return new Tool(function);
  }

  /**
   * 카테고리별 제품 조회 도구
   */
  private Tool createGetProductsByCategoryTool() {
    Map<String, Object> categoryIdProp = new HashMap<>();
    categoryIdProp.put("type", "string");
    categoryIdProp.put("description", "카테고리 ID (FISH, DOGS, CATS, REPTILES, BIRDS 중 하나)");
    categoryIdProp.put("enum", Arrays.asList("FISH", "DOGS", "CATS", "REPTILES", "BIRDS"));

    Map<String, Object> properties = new HashMap<>();
    properties.put("categoryId", categoryIdProp);

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("type", "object");
    parameters.put("properties", properties);
    parameters.put("required", Arrays.asList("categoryId"));

    ToolFunction function = new ToolFunction("getProductsByCategory",
        "특정 카테고리에 속한 반려동물 품종(Product) 목록을 조회합니다. 각 품종의 ID, 이름, 설명, 그리고 상세 특성 정보(케어 난이도, 공간 요구사항, 월 비용, 알레르기 위험, 소음, 활동량, 사회성, 훈련 난이도, 수명, 적합한 주거환경, 성격 등)를 반환합니다.",
        parameters);

    return new Tool(function);
  }

  /**
   * 특정 품종의 상세 특성 조회 도구
   */
  private Tool createGetProductWithCharacteristicsTool() {
    Map<String, Object> productIdProp = new HashMap<>();
    productIdProp.put("type", "string");
    productIdProp.put("description", "제품(품종) ID (예: FI-SW-01, K9-BD-01 등)");

    Map<String, Object> properties = new HashMap<>();
    properties.put("productId", productIdProp);

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("type", "object");
    parameters.put("properties", properties);
    parameters.put("required", Arrays.asList("productId"));

    ToolFunction function = new ToolFunction("getProductWithCharacteristics",
        "특정 품종의 상세 정보와 특성을 조회합니다. 품종명, 카테고리, 케어 난이도, 공간 요구사항, 월 비용, 알레르기 위험, 소음 수준, 활동량, 사회성, 훈련 난이도, 수명, 적합한 주거환경(원룸/아파트/단독주택), 바쁜 사람 적합성, 어린이 친화성, 성격, 상세 설명을 반환합니다.",
        parameters);

    return new Tool(function);
  }
}
