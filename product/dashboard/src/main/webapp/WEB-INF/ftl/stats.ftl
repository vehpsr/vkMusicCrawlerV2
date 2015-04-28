<html>
<head>
    <meta charset="UTF-8" />
    <title>stats</title>
    <link rel="icon" type="image/png" href="${rc.getContextPath()}/favicon/favicon.ico">
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/nv.d3.css" type="text/css"></link>
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/index.css" type="text/css"></link>
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/stats.css" type="text/css"></link>
    <script src="${rc.getContextPath()}/resources/js/jquery-2.1.3.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/d3.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/nv.d3.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/dashboard/stats.js" type="text/javascript"></script>
</head>
<body>
    <div id="userStats" style="display:none;">
        <div class="close" onclick="$('#userStats svg').empty(); $('#userStats').hide();" >close</div>
        <div id="avgRatingData">
            <svg></svg>
        </div>
        <div id="songsCountData">
            <svg></svg>
        </div>
    </div>
</body>
</html>