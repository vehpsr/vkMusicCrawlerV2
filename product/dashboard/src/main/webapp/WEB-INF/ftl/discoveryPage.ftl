<html>
<head>
    <meta charset="UTF-8" />
    <title>discover</title>
    <link rel="icon" type="image/png" href="${rc.getContextPath()}/favicon/favicon.ico">
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/index.css" type="text/css"></link>
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/discoveryPage.css" type="text/css"></link>
    <script src="${rc.getContextPath()}/resources/js/jquery-2.1.3.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/dashboard/discoveryPage.js" type="text/javascript"></script>
</head>
<body>
    <div class="wrapper">
        <div class="discoverUserByUrl">
            <h3>Discover user by VK URL</h3>
            <form id="discoverUserByUrl" action="${rc.getContextPath()}/discover/user">
                <label for="userVkUrl">User URL:</label>
                <input id="userVkUrl" />
                <div class="separator"></div>
                <label for="forceUpdate">Force update</label>
                <input id="forceUpdate" type="checkbox" />
                <div class="separator"></div>
                <input type="button" value="Get User" onclick="fetchUserFromVk();" />
            </form>
        </div>
    </div>
</body>
</html>
