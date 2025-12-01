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
<%@ page language="java"
		 contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>

<%@ include file="../common/IncludeTop.jsp"%>

<div id="Catalog">
	<stripes:messages/>
	<stripes:errors/>

<!-- 샘플 데이터 자동 입력 버튼 -->
<div style="margin-bottom: 20px; padding: 15px; background: #f0f8ff; border: 2px solid #4a90d9; border-radius: 8px;">
  <button type="button" onclick="fillSampleData()"
          style="padding: 12px 24px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 14px;">
    샘플 데이터 자동 입력 (데모용)
  </button>
  <span style="margin-left: 10px; color: #666; font-size: 13px;">클릭하면 테스트용 데이터가 자동으로 입력됩니다.</span>
</div>

<script type="text/javascript">
function fillSampleData() {
  // User Information
  document.querySelector('input[name="username"]').value = 'testuser';
  document.querySelector('input[name="password"]').value = 'test123';
  document.querySelector('input[name="repeatedPassword"]').value = 'test123';

  // Account Information
  document.querySelector('input[name="account.firstName"]').value = '길동';
  document.querySelector('input[name="account.lastName"]').value = '홍';
  document.querySelector('input[name="account.email"]').value = 'hong@test.com';
  document.querySelector('input[name="account.phone"]').value = '010-1234-5678';
  document.querySelector('input[name="account.address1"]').value = '서울시 강남구 테헤란로 123';
  document.querySelector('input[name="account.address2"]').value = '101동 202호';
  document.querySelector('input[name="account.city"]').value = 'Seoul';
  document.querySelector('input[name="account.state"]').value = 'KR';
  document.querySelector('input[name="account.zip"]').value = '06234';
  document.querySelector('input[name="account.country"]').value = 'Korea';

  // Profile Information
  document.querySelector('select[name="account.languagePreference"]').value = 'english';
  document.querySelector('select[name="account.favouriteCategoryId"]').value = 'DOGS';
  document.querySelector('input[name="account.listOption"]').checked = true;
  document.querySelector('input[name="account.bannerOption"]').checked = true;

  // Lifestyle Information
  document.querySelector('input[name="account.age"]').value = '28';
  document.querySelector('input[name="account.occupation"]').value = '소프트웨어 개발자';
  document.querySelector('select[name="account.homeHours"]').value = 'TWO_TO_SIX';
  document.querySelector('select[name="account.housingType"]').value = 'APARTMENT';
  document.querySelector('select[name="account.monthlyBudget"]').value = 'BETWEEN_100K_300K';
  document.querySelector('input[name="account.hasAllergy"][value="false"]').checked = true;

  alert('샘플 데이터가 입력되었습니다!');
}
</script>

<stripes:form
	beanclass="org.mybatis.jpetstore.web.actions.AccountActionBean"
	focus="" id="newAccountForm">

	<h3>User Information</h3>

	<table>
		<tr>
			<td>User ID:</td>
			<td><stripes:text name="username" id="username" /></td>
		</tr>
		<tr>
			<td>New password:</td>
			<td><stripes:password name="password" id="password" /></td>
		</tr>
		<tr>
			<td>Repeat password:</td>
			<td><stripes:password name="repeatedPassword" id="repeatedPassword" /></td>
		</tr>
	</table>

	<%@ include file="IncludeAccountFields.jsp"%>

	<stripes:submit name="newAccount" value="Save Account Information" onclick="return validateNewAccountForm();" />

</stripes:form></div>

<script type="text/javascript">
function validateNewAccountForm() {
	var username = document.getElementById('username').value.trim();
	var password = document.getElementById('password').value.trim();
	var repeatedPassword = document.getElementById('repeatedPassword').value.trim();
	var firstName = document.getElementById('account.firstName').value.trim();
	var lastName = document.getElementById('account.lastName').value.trim();
	var email = document.getElementById('account.email').value.trim();
	var phone = document.getElementById('account.phone').value.trim();
	var address1 = document.getElementById('account.address1').value.trim();
	var city = document.getElementById('account.city').value.trim();
	var state = document.getElementById('account.state').value.trim();
	var zip = document.getElementById('account.zip').value.trim();
	var country = document.getElementById('account.country').value.trim();

	if (!username || !password || !repeatedPassword || !firstName || !lastName ||
	    !email || !phone || !address1 || !city || !state || !zip || !country) {
		alert('모든 필수 정보를 입력해주세요.');
		return false;
	}

	if (password !== repeatedPassword) {
		alert('비밀번호가 일치하지 않습니다.');
		return false;
	}

	return true;
}
</script>

<%@ include file="../common/IncludeBottom.jsp"%>
