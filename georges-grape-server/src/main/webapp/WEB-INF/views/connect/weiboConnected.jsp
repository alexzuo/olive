<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ page session="false" %>

<h3>Connected to Weibo</h3>

<form id="disconnect" method="post">
	<div class="formInfo">
		<p>
			Spring Social Showcase is connected to your Weibo account.
			Click the button if you wish to disconnect.
		</p>		
	</div>
	<button type="submit">Disconnect</button>	
	<input type="hidden" name="_method" value="delete" />
</form>

<a href="<c:url value="/weibo"/>">View your weibo profile</a>
