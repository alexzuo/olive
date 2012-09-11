<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/spring-social/facebook/tags" prefix="facebook" %>
<%@ page session="false" %>

<h3>Connect to qq</h3>

<form action="<c:url value="/connect/qq" />" method="POST">
	<input type="hidden" name="scope" value="get_user_info" />
	<div class="formInfo">
		<p>You aren't connected to qq yet. Click the button to connect Spring Social Showcase with your qq account.</p>
	</div>
	<p><button type="submit"><img src="<c:url value="/resources/social/qq/qq.png" />"/></button></p>
	<label for="postToWall"><input id="postToWall" type="checkbox" name="postToWall" /> Tell your friends about Spring Social Showcase on your qq wall</label>
</form>
