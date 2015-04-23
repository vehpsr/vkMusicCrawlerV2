function fetchFromVk(discoveryPanelId) {
    var form = $('#' + discoveryPanelId);
    var userUrl = form.find('input[type=text]');
    var forceUpdate = form.find('input[type=checkbox]');

    if (!userUrl) {
        return;
    }

    $.ajax({
        url: form.attr('action'),
        method: 'POST',
        data: JSON.stringify({'url': userUrl.val(), 'forceUpdate': forceUpdate.is(':checked')}),
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
        }
    }).always(function(d) {
        if (d.status === 200) {
            return;
        }
        console.log(d);
        alert(d.status + ": " + d.responseText);
    });

    userUrl.val('');
    forceUpdate.prop('checked', false);
}

function fetchUsers(selector) {
    var form = $('#' + selector);
    var count = form.find('input[type=number]');

    if (!count) {
        return;
    }

    $.ajax({
        url: form.attr('action'),
        method: 'POST',
        data: JSON.stringify({'count': count.val()}),
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
        }
    });

    count.val('');
}
