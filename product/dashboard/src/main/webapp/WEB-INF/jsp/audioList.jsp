<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>Hello World</title>
</head>
<body>
   <h2>${message}</h2>
   <c:forEach items="${songs}" var="song">
        <div>${song.artist}</div>
        <div>${song.title}</div>
        <div>${song.url}</div>
	</c:forEach>
</body>
</html>