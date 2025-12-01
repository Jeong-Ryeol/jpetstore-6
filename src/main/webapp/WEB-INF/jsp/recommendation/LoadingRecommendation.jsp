<%--

       Copyright 2010-2025 the original author or authors.

       Licensed under the Apache License, Version 2.0 (the "License");
       you may not use this file except in compliance with the License.
       You may obtain a copy of the License at

          https://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       See the License for the specific language governing permissions and
       limitations under the License.

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="../common/IncludeTop.jsp"%>

<div id="Catalog">
  <h2>AI가 분석 중입니다...</h2>
  <p>Local LLM (Ollama llama3.1:8b)이 DB를 조회하며 추천을 생성하고 있습니다.</p>

  <!-- 로딩 애니메이션 -->
  <div id="loadingSpinner" style="text-align: center; margin: 30px 0;">
    <div style="display: inline-block; width: 50px; height: 50px; border: 5px solid #f3f3f3; border-top: 5px solid #667eea; border-radius: 50%; animation: spin 1s linear infinite;"></div>
    <p style="margin-top: 15px; color: #666;">처리 중... 잠시만 기다려주세요</p>
  </div>

  <!-- 실시간 로그 영역 -->
  <div style="margin-top: 30px;">
    <h3>Agent 실행 로그 (실시간)</h3>
    <div id="logContainer" style="background: #1e1e1e; border-radius: 8px; padding: 20px; max-height: 400px; overflow-y: auto;">
      <pre id="logContent" style="color: #d4d4d4; font-family: 'Consolas', 'Monaco', monospace; font-size: 13px; line-height: 1.5; margin: 0; white-space: pre-wrap;">로그 로딩 중...</pre>
    </div>
  </div>

  <!-- 오류 메시지 영역 (숨김) -->
  <div id="errorMessage" style="display: none; margin-top: 20px; padding: 15px; background: #ffebee; border: 1px solid #f44336; border-radius: 5px; color: #c62828;">
  </div>
</div>

<style>
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>

<script type="text/javascript">
var pollInterval;
var lastLogLength = 0;

function pollLogStatus() {
  var xhr = new XMLHttpRequest();
  xhr.open('GET', '${pageContext.request.contextPath}/actions/InitialRecommendation.action?getLogStatus=', true);
  xhr.onreadystatechange = function() {
    if (xhr.readyState === 4 && xhr.status === 200) {
      try {
        var response = JSON.parse(xhr.responseText);

        // 로그 업데이트
        if (response.log && response.log.length > 0) {
          document.getElementById('logContent').textContent = response.log;

          // 새 로그가 추가되면 스크롤을 아래로
          if (response.log.length > lastLogLength) {
            var logContainer = document.getElementById('logContainer');
            logContainer.scrollTop = logContainer.scrollHeight;
            lastLogLength = response.log.length;
          }
        }

        // 상태 확인
        if (response.status === 'done') {
          clearInterval(pollInterval);
          // 결과 페이지로 이동
          window.location.href = '${pageContext.request.contextPath}/actions/InitialRecommendation.action?getResult=';
        } else if (response.status === 'error') {
          clearInterval(pollInterval);
          document.getElementById('loadingSpinner').style.display = 'none';
          document.getElementById('errorMessage').style.display = 'block';
          document.getElementById('errorMessage').textContent = '오류가 발생했습니다: ' + response.log;
        }
      } catch (e) {
        console.error('JSON parse error:', e);
      }
    }
  };
  xhr.send();
}

// 페이지 로드 시 폴링 시작
window.onload = function() {
  pollInterval = setInterval(pollLogStatus, 500); // 0.5초마다 폴링
  pollLogStatus(); // 즉시 한번 실행
};
</script>

<%@ include file="../common/IncludeBottom.jsp"%>
