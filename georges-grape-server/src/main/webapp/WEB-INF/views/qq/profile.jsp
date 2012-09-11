<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

<h3>Your qq Profile</h3>
<p>Hello, <c:out value="${profile.nickName}"/>!</p>
<dl>
	<dt>qq openId:</dt>
	<dd><c:out value="${profile.openId}"/></dd>
	<dt>gender:</dt>
	<dd><c:out value="${profile.gender}"/></dd>
	<dt>vip:</dt>
	<dd><c:out value="${profile.vip}"/></dd>
	<dt>level:</dt>
	<dd><c:out value="${profile.level}"/></dd>
	<dt>image:</dt>
	<dd><img src="${profile.figureUrl}"> </dd>
</dl>

<c:url value="/connect/qq" var="disconnectUrl"/>
<form id="disconnect" action="${disconnectUrl}" method="post">
	<button type="submit">Disconnect from qq</button>	
	<input type="hidden" name="_method" value="delete" />
</form>
