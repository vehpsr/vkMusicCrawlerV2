// set up audio player
$(function() {

    // Setup the player to autoplay the next track
    var audio = audiojs.createAll({
        trackEnded : function() {
            var next = $('ol li.playing').next();
            if (!next.length)
                next = $('ol li').first();
            next.addClass('playing').siblings().removeClass('playing');
            audio.load($('.songWrap', next).attr('data-src'));
            audio.play();
            updateStatusPanel();
        }
    })[0];

    injectVolumeSlider(audio);

    // Load in the first track
    var first = $('ol .songWrap').attr('data-src');
    if (!first) {
        console.log('No tracks are avaliable for user with this Id');
        return;
    }
    $('ol li').first().addClass('playing');
    audio.load(first);
    updateStatusPanel();
    // Load track or pause if already playing
    $('ol li').click(function(e) {
        e.preventDefault();
        if ($(this).hasClass('playing')) {
            if ($('#audiojs_wrapper0').hasClass('playing')) {
                audio.pause();
            } else {
                audio.play();
            }
        } else {
            $(this).addClass('playing').siblings().removeClass('playing');
            audio.load($('.songWrap', this).attr('data-src'));
            audio.play();
            updateStatusPanel();
        }
    });

    function injectVolumeSlider(player) {
        var VOLUME_COOKIE = "crawler_volume_value";

        var globalVolume = parseInt(readCookie(VOLUME_COOKIE)) || 100;
        player.setVolume(convert(globalVolume));
        var slider = $('<input id="volumeSlider" type="range" min="0" max="100" value="' + globalVolume + '" step="1"/>');
        slider.on('mousemove', function() {
            player.setVolume(convert(slider.val()));
        }).on('mouseup', function() {
            createCookie(VOLUME_COOKIE, slider.val());
        });
        $('#audioPlayer .time').append(slider);

        function convert(val) {
            val = parseInt(val) / 100;
            if (val < 0) {
                return 0;
            } else if (val > 1) {
                return 1;
            } else {
                return val * val;
            }
        }
    }

    function updateStatusPanel() {
        var artistMaxLen = 30;
        var titleMaxLen = 40;
        var artist = $('ol li.playing .songWrap .artist').text();
        var title = $('ol li.playing .songWrap .title').text();

        var vkRef = $('#songVkRef');
        vkRefUrl = vkRef.attr('href').split("?")[0];
        vkRef.attr('href', vkRefUrl + "?q=" + artist + " " + title);

        while (artist.length + title.length > artistMaxLen + titleMaxLen) {
            if (artist.length > artistMaxLen) {
                artist = artist.substring(0, artistMaxLen);
                continue;
            }
            if (title.length > titleMaxLen) {
                title = title.substring(0, titleMaxLen);
                continue;
            }
            break;
        }
        $('#audioPlayer .currentSong .currentArtist').text(artist);
        $('#audioPlayer .currentSong .currentTitle').text(title);
    }
});

//set up rating system
$(function() {
    $('ol li .stars').click(function(e) {
        e.stopPropagation();

        var url = $(this).attr('action');
        var rating = parseInt($(this).find('input[type="radio"]:checked').val());
        if (!rating) {
            return;
        }

        $.ajax({
            url: url,
            method: 'POST',
            data: JSON.stringify({value: rating}),
            beforeSend: function(xhr) {
                xhr.setRequestHeader("Accept", "application/json");
                xhr.setRequestHeader("Content-Type", "application/json");
            }
        }).fail(function(d) {
            console.log(d);
            alert(d.status + ": " + d.responseText);
        });
    });

    // deselect all radio buttons
    $('input[type="radio"]').prop('checked', false);
});

function scrollToCurrentSong(e) {
    $('body').scrollTop($('ol li.playing').first().offset().top - 80);

    e.preventDefault();
    e.stopPropagation();
}

function resolveConfirmation() {
    var msg = "Are you sure you want to resolve this Audio Library?";
    var result = confirm(msg);
    return result;
}

// scroll to top on refresh
$(function() {
    $('body').scrollTop(0);
});

// Utils
function createCookie(name, value) {
    var date = new Date();
    date.setTime(date.getTime() + (30*24*60*60*1000));
    var expires = "; expires="+date.toGMTString();
    document.cookie = name+"="+value+expires+"; path=/";
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}
