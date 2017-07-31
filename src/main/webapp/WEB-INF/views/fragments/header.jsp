<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<head>
<title>Spring MVC Example</title>

<spring:url value="/resources/css/bootstrap.min.css"
	var="bootstrapCss" />
<link href="${bootstrapCss}" rel="stylesheet" />

<spring:url value="/users/add" var="urlAddUser" />
</head>

<nav>
	<ul>
		<li><a href="${urlAddUser}">Add User</a></li>
	</ul>
</nav>
