<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<jsp:include page="../fragments/header.jsp" />
<body>
	<div class="container">
		<h2>
			<spring:message code="lbl.userlist" text="USER LIST" />
		</h2>
		<table class="table table-striped">
			<thead>
				<tr>
					<th><spring:message code="lbl.id" text="ID" /></th>
					<th><spring:message code="lbl.name" text="Name" /></th>
					<th><spring:message code="lbl.email" text="Email" /></th>
					<th><spring:message code="lbl.framework" text="framkework" /></th>
					<th><spring:message code="lbl.action" text="Action" /></th>
				</tr>
			</thead>
			<c:forEach var="user" items="${users}">
				<tr>
					<td>${user.id}</td>
					<td>${user.name}</td>
					<td>${user.email}</td>
					<td>${user.framework}</td>
					<td><spring:url value="/users/${user.id}" var="userUrl" /> <spring:url
							value="/users/${user.id}/delete" var="deleteUrl" /> <spring:url
							value="/users/${user.id}/update" var="updateUrl" />

						<button class="" onclick="location.href='${userUrl}'">Query</button>
						<button class="" onclick="location.href='${updateUrl}'">Update</button>
						<button class="" onclick="location.href='${deleteUrl}'">Delete</button></td>
				</tr>
			</c:forEach>
		</table>
	</div>

</body>

</html>
