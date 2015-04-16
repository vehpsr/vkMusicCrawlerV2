<html>
<head>
    <meta charset="UTF-8" />
    <title>audio</title>
    <link rel="icon" type="image/png" href="${rc.getContextPath()}/favicon/favicon.ico">
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/index.css" type="text/css"></link>
    <link rel="stylesheet" href="${rc.getContextPath()}/resources/layout/audioList.css" type="text/css"></link>
    <script src="${rc.getContextPath()}/resources/js/jquery-2.1.3.js" type="text/javascript"></script>
    <script src="${rc.getContextPath()}/resources/js/audiojs/audio.js" type="text/javascript"></script>
</head>
<body>

    <script src="${rc.getContextPath()}/resources/js/dashboard/audioList.js" type="text/javascript"></script>

    <div id="wrapper">
        <div id="audioPlayer">
            <div class="currentSong">
                <span class="currentArtist"></span>
                &nbsp;&ndash;&nbsp;
                <span class="currentTitle"></span>
            </div>
            <audio></audio> <!-- audio player container -->
        </div>
        <ol class="audioList">
            <#list songs as song>
                <li data-song-id="${song.id}">
                    <div data-src="${song.url}" class="songWrap">
                        <div class="titleWrap">
                            <span class="artist">${song.artist}</span>
                            -
                            <span class="title">${song.title}</span>
                        </div>
                        <div class="songMeta">
                            <span class="time">${song.time}</span>
                        </div>
                    </div>
                    <form class="stars" action="${rc.getContextPath()}/song/rate">
                        <input type="radio" name="rating" id="star1" value="1">
                        <input type="radio" name="rating" id="star2" value="2">
                        <input type="radio" name="rating" id="star3" value="3">
                        <input type="radio" name="rating" id="star4" value="4">
                        <input type="radio" name="rating" id="star5" value="5">
                    </form>
                </li>
            </#list>
        </ol>
    </div>
</body>
</html>
