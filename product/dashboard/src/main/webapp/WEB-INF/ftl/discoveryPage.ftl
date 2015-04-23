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
        <@discoveryPanel entityName="user" />
        <@discoveryPanel entityName="group" />
        <div class="discoveryPanel discoverNewUsers">
            <h3>There is <span>${undiscoveredUsers}</span> undiscovered users in system</h3>
            <form id="discoverNewUsers" action="${rc.getContextPath()}/discover/newusers">
                <label for="discoverUsersCount">Numer of users</label>
                <input type="number" id="discoverUsersCount" min="0" max="100" />
                <input type="button" value="Fetch Users" onclick="fetchUsers('discoverNewUsers');" />
            </form>
        </div>
    </div>
</body>
</html>

<#macro discoveryPanel entityName>
    <#local selector="discover${entityName?cap_first}ByUrl" >
    <div class="discoveryPanel ${selector}">
        <h3>Discover <span>${entityName?cap_first}</span> by VK URL</h3>
        <form id="${selector}" action="${rc.getContextPath()}/discover/${entityName}">
            <label for="${entityName}VkUrl">URL:</label>
            <input id="${entityName}VkUrl" type="text" />
            <div class="separator"></div>
            <label for="force${entityName}Update">Force update</label>
            <input id="force${entityName}Update" type="checkbox" />
            <div class="separator"></div>
            <input type="button" value="Fetch from VK" onclick="fetchFromVk('${selector}');" />
        </form>
    </div>
</#macro>
