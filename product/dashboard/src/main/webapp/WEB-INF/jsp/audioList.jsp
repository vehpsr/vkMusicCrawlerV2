<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>audio</title>
    <link rel="stylesheet" href="<c:url value='/resources/layout/index.css'/>" type="text/css"></link>
    <link rel="stylesheet" href="<c:url value='/resources/layout/audioList.css'/>" type="text/css"></link>
    <script src="<c:url value='/resources/js/jquery-2.1.3.js'/>" type="text/javascript"></script>
    <script src="<c:url value='/resources/js/audiojs/audio.js'/>" type="text/javascript"></script>
</head>
<body>
    <script src="<c:url value='/resources/js/dashboard/audioPlayer.js'/>" type="text/javascript"></script>

    <div id="wrapper">
        <audio></audio> <!-- audio player container -->
        <ol class="audioList">
            <c:forEach items="${songs}" var="song">
                <li>
                    <a data-src="${song.url}" href="#">${song.artist} - ${song.title}</a>
                </li>
            </c:forEach>
        </ol>
    </div>
</body>
</html>