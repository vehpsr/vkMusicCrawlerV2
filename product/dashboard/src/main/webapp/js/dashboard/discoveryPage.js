function fetchUserFromVk() {
    var form = $('#discoverUserByUrl');
    var userUrl = form.find('#userVkUrl');
    var forceUpdate = form.find('#forceUpdate');

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
