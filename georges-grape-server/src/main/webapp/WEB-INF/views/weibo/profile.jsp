<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf"%>
<%@ page session="false"%>

<h3>Your Weibo Profile</h3>
<p>
	Hello,
	<c:out value="${profile.screenName}" />
	!
</p>
<dl>
	<dt>Weibo ID:</dt>
	<dd>
		<c:out value="${profile.id}" />
	</dd>

	<dt>name</dt>
	<dd>
		<c:out value="${profile.name}" />
	</dd>

	<dt>screenName</dt>
	<dd>
		<c:out value="${profile.screenName}" />
	</dd>

	<dt>imageUrl</dt>
	<dd>
		<c:out value="${profile.imageUrl}" />
	</dd>

	<dt>domain</dt>
	<dd>
		<c:out value="${profile.domain}" />
	</dd>

	<dt>url</dt>
	<dd>
		<c:out value="${profile.url}" />
	</dd>

	<dt>province</dt>
	<dd>
		<c:out value="${profile.province}" />
	</dd>

	<dt>city</dt>
	<dd>
		<c:out value="${profile.city}" />
	</dd>

	<dt>location</dt>
	<dd>
		<c:out value="${profile.location}" />
	</dd>

	<dt>description</dt>
	<dd>
		<c:out value="${profile.description}" />
	</dd>

	<dt>gender</dt>
	<dd>
		<c:out value="${profile.gender}" />
	</dd>

	<dt>followersCount</dt>
	<dd>
		<c:out value="${profile.followersCount}" />
	</dd>

	<dt>friendsCount</dt>
	<dd>
		<c:out value="${profile.friendsCount}" />
	</dd>

	<dt>statusesCount</dt>
	<dd>
		<c:out value="${profile.statusesCount}" />
	</dd>

	<dt>favouritesCount</dt>
	<dd>
		<c:out value="${profile.favouritesCount}" />
	</dd>





</dl>

<c:url value="/connect/weibo" var="disconnectUrl" />
<form id="disconnect" action="${disconnectUrl}" method="post">
	<button type="submit">Disconnect from Weibo</button>
	<input type="hidden" name="_method" value="delete" />
</form>
