<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta property="qc:admins" content="5151205357275363475467564216375" />
	<meta property="wb:webmaster" content="cd0b0bbb004a27b1" />
	<title>Georges</title>
	<link rel="stylesheet" href="resources/css/main.css" type="text/css"></link>
	<link rel="stylesheet" href="resources/css/colors.css" type="text/css"></link>
	<link rel="stylesheet" href="resources/css/local.css" type="text/css"></link>
</head>
<body>
	<div id="page">
		<div id="header">
			<div id="name-and-company">
				<div id='site-name'>
					<a href="" title="Georges Grape Server ${environmentName}" rel="home">Georges Grape Server with MongoDb on ${environmentName}</a>
				</div>
				<div id='company-name'>
					<a href="http://kangaroo.vicp.cc:5164/p/" title="SpringSource">Georges Home</a>
				</div>
			</div>
			<!-- /name-and-company -->
		</div>
		<!-- /header -->
		<div id="container">
			<div id="content" class="no-side-nav">


<h2>The following events have been stored in the database:</h2>
<ul>
	<c:forEach items="${events}" var="event">
		<li><p>${event}</p></li>	
	</c:forEach>
</ul>

<h2>The following services have been bound to this application:</h2>
<ul>
	<c:forEach items="${services}" var="service">
		<li><p>${service}</p></li>	
	</c:forEach>
</ul>

<h2>The following services properties available to the application are available:</h2>
<ul>
	<c:forEach items="${serviceProperties}" var="serviceProperties">
		<li><p>${serviceProperties}</p></li>	
	</c:forEach>
</ul>


			</div>
		</div>
	</div>
</body>
</html>
