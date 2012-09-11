<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/spring-social/facebook/tags" prefix="facebook" %>
<%@ page session="false" %>

<%
  String host=request.getRemoteHost();
%>
Host of Client <%=host%>

<%-- <form id="signin" action="<c:url value="/signin/authenticate" />" method="post">
	<div class="formInfo">
  		<c:if test="${param.error eq 'bad_credentials'}">
  		<div class="error">
  			Your sign in information was incorrect.
  			Please try again or <a href="<c:url value="/signup" />">sign up</a>.
  		</div>
 	 	</c:if>
  		<c:if test="${param.error eq 'multiple_users'}">
  		<div class="error">
  			Multiple local accounts are connected to the provider account.
  			Try again with a different provider or with your username and password.
  		</div>
 	 	</c:if>
	</div>
	<fieldset>
		<label for="login">Username</label>
		<input id="login" name="j_username" type="text" size="25" <c:if test="${not empty signinErrorMessage}">value="${SPRING_SECURITY_LAST_USERNAME}"</c:if> />
		<label for="password">Password</label>
		<input id="password" name="j_password" type="password" size="25" />	
	</fieldset>
	<button type="submit">Sign In</button>
	
	<p>Some test user/password pairs you may use are:</p>
	<ul>
		<li>habuma/freebirds</li>
		<li>kdonald/melbourne</li>
		<li>rclarkson/atlanta</li>
	</ul>
	
	<p>Or you can <a href="<c:url value="/signup"/>">signup</a> with a new account.</p>
</form> --%>

	<!-- TWITTER SIGNIN -->
	<form id="tw_signin" action="<c:url value="/signin/twitter"/>" method="GET">
		<button type="submit"><img src="<c:url value="/resources/social/twitter/sign-in-with-twitter-d.png"/>" /></button>
	</form>

	<!-- FACEBOOK SIGNIN -->
	<form name="fb_signin" id="fb_signin" action="<c:url value="/signin/facebook"/>" method="GET">
        <input type="hidden" name="terminal" value="<%=host%>" />
		<button type="submit"><img src="<c:url value="/resources/social/facebook/sign-in-with-facebook.png"/>" /></button>
	</form>

	
	<!-- QQ SIGNIN -->
	<form name="qq_signin" id="qq_signin" action="<c:url value="/signin/qq"/>" method="GET">
        <input type="hidden" name="terminal" value="<%=host%>" />
 		<button type="submit"><img src="<c:url value="/resources/social/qq/qq.png"/>" /></button>
	</form>
	
	
    <!-- WEIBO SIGNIN -->
	<form name="weibo_signin" id="weibo_signin" action="<c:url value="/signin/weibo"/>" method="GET">
        <input type="hidden" name="terminal" value="<%=host%>" />
		<button type="submit"><img src="<c:url value="/resources/social/weibo/weibo.png"/>" /></button>
	</form>
	
	