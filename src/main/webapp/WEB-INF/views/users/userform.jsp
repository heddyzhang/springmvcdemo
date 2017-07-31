<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Add User Form</title>
    <style>
    .error
    {
        color: #ff0000;
        font-weight: bold;
    }
    </style>
</head>
<body>
	<c:choose>
		<c:when test="${userForm['new']}">
			<h1>Add User</h1>
		</c:when>
		<c:otherwise>
			<h1>Update User</h1>
		</c:otherwise>
	</c:choose>
	<br />
	<spring:url value="/users" var="userActionUrl" />

	<form:form method="post" modelAttribute="userForm"
		action="${userActionUrl}">
		<table>
			<tr>
				<td><label>Name</label>
					<form:input path="name" type="text" placeholder="Name" />
					<form:errors path="name" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td><label>Email</label>
					<form:input path="email" type="text" placeholder="Email" />
				</td>
			</tr>
			<tr>
				<td><label>Address</label>
					<form:input path="address" type="text" placeholder="Address" />
				</td>
			</tr>
			<tr>
				<td><label>Sex</label>
					<form:radiobutton path="sex" value="M"/>Male
					<form:radiobutton path="sex" value="F"/>Female
					<form:errors path="sex" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td><label>Number</label>
					<form:radiobuttons path="number" items="${numberList}" />
					<form:errors path="number" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td><label>Frameworks</label>
					<form:checkboxes path="framework" items="${frameworkList}" />
					<form:errors path="framework" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td><label>JavaSkills</label>
					<form:select path="skill" multiple="true" items="${javaSkills}" />
					<form:errors path="skill" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td><label>Country</label>
					<form:select path="country" class="form-control">
						<form:option value="" label="--- Select ---" />
						<form:options items="${countryList}" />
					</form:select>
					<form:errors path="country" cssClass="error" />
				</td>
			</tr>
			<tr>
				<td><button type="submit" >Add</button><button type="submit" >Update</button></td>
			</tr>
		</table>
	</form:form>
</body>
</html>