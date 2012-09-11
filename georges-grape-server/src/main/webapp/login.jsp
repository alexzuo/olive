<%@ page import="org.springframework.security.core.AuthenticationException" %>
<%@ page import="org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<authz:authorize ifAllGranted="ROLE_USER">
  <c:redirect url="index.jsp"/>
</authz:authorize>

  <div id="content">
    <% if (session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) != null && !(session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) instanceof UnapprovedClientAuthenticationException)) { %>
      <div class="error">
		    <h2>Woops!</h2>

      	<p>Your login attempt was not successful. (<%= ((AuthenticationException) session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>)</p>
      </div>
    <% } %>
    <c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION"/>

    <authz:authorize ifNotGranted="ROLE_USER">
      <h2>Login</h2>
         <form id="loginForm" name="loginForm" action="<c:url value="/login.do"/>" method="post">
        <p><label>Username: <input type='text' name='j_username' value="alex"></label></p>
        <p><label>Password: <input type='text' name='j_password' value="password"></label></p>
        
        <p><input name="login" value="Login" type="submit"></p>
      </form>
    </authz:authorize>
  </div>
  
  <script type="text/javascript" src="http://qzonestyle.gtimg.cn/qzone/openapi/qc.js" charset="utf-8" ></script>

<span id="qqLoginBtn"></span>




