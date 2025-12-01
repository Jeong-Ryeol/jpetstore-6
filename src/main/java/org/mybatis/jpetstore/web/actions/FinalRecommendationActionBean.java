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
package org.mybatis.jpetstore.web.actions;

import java.io.Serializable;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SessionScope;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.integration.spring.SpringBean;

import org.mybatis.jpetstore.domain.recommendation.FinalRecommendation;
import org.mybatis.jpetstore.service.FinalRecommendationService;

@SessionScope
public class FinalRecommendationActionBean extends AbstractActionBean implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final String FINAL_RECOMMENDATION = "/WEB-INF/jsp/recommendation/FinalRecommendation.jsp";
  private static final String LOADING_PAGE = "/WEB-INF/jsp/recommendation/LoadingFinalRecommendation.jsp";

  // 세션 키
  private static final String SESSION_STATUS_KEY = "finalRecommendationStatus";
  private static final String SESSION_LOG_KEY = "finalRecommendationLog";
  private static final String SESSION_RESULT_KEY = "finalRecommendationResult";

  @SpringBean
  private transient FinalRecommendationService finalRecommendationService;

  private String sessionId; // 게임 세션 ID
  private FinalRecommendation finalRecommendation;
  private String agentLog;

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public FinalRecommendation getFinalRecommendation() {
    return finalRecommendation;
  }

  public void setFinalRecommendation(FinalRecommendation finalRecommendation) {
    this.finalRecommendation = finalRecommendation;
  }

  public String getAgentLog() {
    return agentLog;
  }

  @DefaultHandler
  public Resolution getRecommendation() {
    if (sessionId == null || sessionId.trim().isEmpty()) {
      setMessage("게임 세션 정보가 없습니다.");
      return new ForwardResolution(ERROR);
    }

    // 로딩 페이지로 이동 후 비동기 처리
    getContext().getRequest().getSession().setAttribute(SESSION_STATUS_KEY, "processing");
    getContext().getRequest().getSession().setAttribute(SESSION_LOG_KEY, "");

    final String gameSessionId = sessionId;
    final String httpSessionId = getContext().getRequest().getSession().getId();
    final javax.servlet.http.HttpSession httpSession = getContext().getRequest().getSession();

    // 세션 ID 설정
    finalRecommendationService.setSessionId(httpSessionId);

    new Thread(() -> {
      try {
        finalRecommendationService.setSessionId(httpSessionId);
        FinalRecommendation result = finalRecommendationService.getFinalRecommendation(gameSessionId);
        httpSession.setAttribute(SESSION_RESULT_KEY, result);
        httpSession.setAttribute(SESSION_LOG_KEY, finalRecommendationService.getLastAgentLog());
        httpSession.setAttribute(SESSION_STATUS_KEY, "done");
      } catch (Exception e) {
        httpSession.setAttribute(SESSION_STATUS_KEY, "error");
        httpSession.setAttribute(SESSION_LOG_KEY, "오류 발생: " + e.getMessage());
      }
    }).start();

    return new ForwardResolution(LOADING_PAGE);
  }

  // AJAX: 현재 로그 상태 조회
  public Resolution getLogStatus() {
    String status = (String) getContext().getRequest().getSession().getAttribute(SESSION_STATUS_KEY);
    String httpSessionId = getContext().getRequest().getSession().getId();

    String log = finalRecommendationService.getLogBySessionId(httpSessionId);

    if (status == null)
      status = "idle";
    if (log == null)
      log = "";

    String json = "{\"status\":\"" + status + "\",\"log\":" + escapeJson(log) + "}";
    return new StreamingResolution("application/json", json);
  }

  // AJAX: 결과 가져오기
  public Resolution getResult() {
    String status = (String) getContext().getRequest().getSession().getAttribute(SESSION_STATUS_KEY);

    if ("done".equals(status)) {
      finalRecommendation = (FinalRecommendation) getContext().getRequest().getSession()
          .getAttribute(SESSION_RESULT_KEY);
      agentLog = (String) getContext().getRequest().getSession().getAttribute(SESSION_LOG_KEY);
      return new ForwardResolution(FINAL_RECOMMENDATION);
    } else {
      return new ForwardResolution(LOADING_PAGE);
    }
  }

  private String escapeJson(String str) {
    if (str == null)
      return "\"\"";
    return "\""
        + str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")
        + "\"";
  }
}
