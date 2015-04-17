<html>
<head>
    <meta charset="UTF-8" />
    <title>users</title>
    <link rel="icon" type="image/png" href="${rc.getContextPath()}/favicon/favicon.ico">
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/index.css" type="text/css"></link>
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/userList.css" type="text/css"></link>
    <script src="${rc.getContextPath()}/resources/js/jquery-2.1.3.js" type="text/javascript"></script>
</head>
<body>
    <div class="wrapper">
        <div class="userTable">
            <div class="userTableHeader">
                <div class="userTableColumn userColumn">name</div>
                <div class="userTableColumn audioColumn">stats</div>
            </div>
            <div class="separator"></div>
            <ol class="userTableBody">
                <#list users as user>
                    <li class="userTableRow">
                        <div class="userTableColumn userColumn">
                            <a href="${vkDomainUrl}${user.url}" target="_blank">${user.name}</a>
                        </div>
                        <div class="userTableColumn audioColumn">
                            <a href="${rc.getContextPath()}/audio/${user.vkId}">${user.ratedAudioCount}/${user.totalAudioCount}</a>
                        </div>

                    </li>
                </#list>
            </ol>
        </div>
    </div>
</body>
</html>