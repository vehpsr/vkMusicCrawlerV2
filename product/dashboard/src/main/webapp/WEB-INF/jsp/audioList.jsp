<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>audio</title>
    <link rel="stylesheet" href="<c:url value='/resources/layout/index.css'/>" type="text/css"></link>
    <script src="<c:url value='/resources/js/jquery-2.1.3.js'/>" type="text/javascript"></script>
    <script src="<c:url value='/resources/js/audiojs/audio.js'/>" type="text/javascript"></script>
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