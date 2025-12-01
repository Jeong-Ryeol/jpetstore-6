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
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.SessionScope;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.integration.spring.SpringBean;

import org.mybatis.jpetstore.domain.Account;
import org.mybatis.jpetstore.domain.recommendation.CategoryRecommendation;
import org.mybatis.jpetstore.service.InitialRecommendationService;
import org.mybatis.jpetstore.service.OllamaRecommendationService;

@SessionScope
public class InitialRecommendationActionBean extends AbstractActionBean implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final String INITIAL_RECOMMENDATION = "/WEB-INF/jsp/recommendation/InitialRecommendation.jsp";
  private static final String LOADING_PAGE = "/WEB-INF/jsp/recommendation/LoadingRecommendation.jsp";

  // 실시간 로그용 세션 키
  private static final String SESSION_LOG_KEY = "ollamaAgentLog";
  private static final String SESSION_STATUS_KEY = "ollamaStatus"; // "processing", "done", "error"
  private static final String SESSION_RESULT_KEY = "ollamaResult";

  @SpringBean
  private transient InitialRecommendationService initialRecommendationService;

  @SpringBean
  private transient OllamaRecommendationService ollamaRecommendationService;

  private List<CategoryRecommendation> top5Categories;
  private String llmMode = "ollama"; // 기본값: ollama (Local LLM)
  private String agentLog; // Ollama Agent 실행 로그

  public List<CategoryRecommendation> getTop5Categories() {
    return top5Categories;
  }

  public String getAgentLog() {
    return agentLog;
  }

  public void setTop5Categories(List<CategoryRecommendation> top5Categories) {
    this.top5Categories = top5Categories;
  }

  public String getLlmMode() {
    return llmMode;
  }

  public void setLlmMode(String llmMode) {
    this.llmMode = llmMode;
  }

  @DefaultHandler
  public Resolution getRecommendations() {
    // 세션에서 account 가져오기
    AccountActionBean accountBean = (AccountActionBean) getContext().getRequest().getSession()
        .getAttribute("/actions/Account.action");

    if (accountBean == null || !accountBean.isAuthenticated()) {
      setMessage("로그인이 필요합니다.");
      return new ForwardResolution(ERROR);
    }

    Account account = accountBean.getAccount();

    // 라이프스타일 정보가 입력되지 않은 경우 체크
    if (account.getAge() == null || account.getHomeHours() == null) {
      setMessage("회원정보를 먼저 입력해주세요.");
      return new ForwardResolution(ERROR);
    }

    // Gemini 모드는 바로 처리
    if (!"ollama".equals(llmMode)) {
      try {
        top5Categories = initialRecommendationService.getTop5Categories(account);
        agentLog = null;
        return new ForwardResolution(INITIAL_RECOMMENDATION);
      } catch (Exception e) {
        e.printStackTrace();
        setMessage("추천 중 오류가 발생했습니다: " + e.getMessage());
        return new ForwardResolution(ERROR);
      }
    }

    // Ollama 모드: 로딩 페이지로 이동 후 비동기 처리
    getContext().getRequest().getSession().setAttribute(SESSION_STATUS_KEY, "processing");
    getContext().getRequest().getSession().setAttribute(SESSION_LOG_KEY, "");

    // 백그라운드 스레드에서 추천 처리
    final Account finalAccount = account;
    final String sessionId = getContext().getRequest().getSession().getId();
    final javax.servlet.http.HttpSession session = getContext().getRequest().getSession();

    // 세션 ID 설정
    ollamaRecommendationService.setSessionId(sessionId);

    new Thread(() -> {
      try {
        ollamaRecommendationService.setSessionId(sessionId);
        List<CategoryRecommendation> result = ollamaRecommendationService.getTop5Categories(finalAccount);
        session.setAttribute(SESSION_RESULT_KEY, result);
        session.setAttribute(SESSION_LOG_KEY, ollamaRecommendationService.getLastAgentLog());
        session.setAttribute(SESSION_STATUS_KEY, "done");
      } catch (Exception e) {
        session.setAttribute(SESSION_STATUS_KEY, "error");
        session.setAttribute(SESSION_LOG_KEY, "오류 발생: " + e.getMessage());
      }
    }).start();

    return new ForwardResolution(LOADING_PAGE);
  }

  // AJAX: 현재 로그 상태 조회 (실시간)
  public Resolution getLogStatus() {
    String status = (String) getContext().getRequest().getSession().getAttribute(SESSION_STATUS_KEY);
    String sessionId = getContext().getRequest().getSession().getId();

    // 실시간 로그 가져오기
    String log = ollamaRecommendationService.getLogBySessionId(sessionId);

    if (status == null)
      status = "idle";
    if (log == null)
      log = "";

    String json = "{\"status\":\"" + status + "\",\"log\":" + escapeJson(log) + "}";
    return new StreamingResolution("application/json", json);
  }

  // AJAX: 결과 가져오기 (완료 후)
  @SuppressWarnings("unchecked")
  public Resolution getResult() {
    String status = (String) getContext().getRequest().getSession().getAttribute(SESSION_STATUS_KEY);

    if ("done".equals(status)) {
      top5Categories = (List<CategoryRecommendation>) getContext().getRequest().getSession()
          .getAttribute(SESSION_RESULT_KEY);
      agentLog = (String) getContext().getRequest().getSession().getAttribute(SESSION_LOG_KEY);
      return new ForwardResolution(INITIAL_RECOMMENDATION);
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
