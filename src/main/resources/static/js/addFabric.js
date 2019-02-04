$(document).ready(function () {
    console.log("Hello addFabric.js");

    var image;

    function showFabricList(data) {
        data.fabrics.forEach(function (item) {
            var emploeeData = '';
            emploeeData += '<tr>';
            emploeeData += '<td>' + item.fabricName + '</td>';
            emploeeData += '<td>' + item.price + '</td>';
            emploeeData += '<td>' + item.upgrade + '</td>';
            emploeeData += '<td>' + item.miningPerSecond + '</td>';
            emploeeData += '</tr>';
            $('#emploe_table').append(emploeeData);
        })
    }

    $.ajax('/api/admin/factory-list', {
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        headers: {token: sessionStorage.getItem("token")},
        success: function (data) {
            showFabricList(data);
        },
        error: function () {
            alert("No server connection");
        }
    });

    $('#add_fabric').click(function () {
        let p, n, u, m, i;

        if ((p = $('#new_price').val()) === null || p.replace(/\s+/g, '') === "") {
            alert("Any factory is worth something!");
            return false;
        }

        if ((n = $('#new_name').val()) === null || n.replace(/\s+/g, '') === "") {
            alert("How do you call the plant - so it will work.");
            return false
        }
        if ((u = $('#new_upgrad').val()) === null || u.replace(/\s+/g, '') === "") {
            alert("Any equipment worth the money.");
            return false
        }

        if ((m = $('#new_mining').val()) === null || m.replace(/\s+/g, '') === "") {
            alert("Why do we need the plant - which does not make a profit?");
            return false
        }

        if ((i = $('#new_image').val()) === null || i.replace(/\s+/g, '') === "") {
            alert("Selected a factory photo.");
            return false
        }

        $.ajax('/api/admin/add-factory', {
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: JSON.stringify({
                newPrice: p,
                newName: n,
                newUpgrade: u,
                newMining: m,
                image: i,
            }),
            success: function () {

                var emploee_data = '';
                emploee_data += '<tr>';
                emploee_data += '<td>' + $('#new_name').val() + '</td>';
                emploee_data += '<td>' + $('#new_price').val() + '</td>';
                emploee_data += '<td>' + $('#new_upgrad').val() + '</td>';
                emploee_data += '<td>' + $('#new_mining').val() + '</td>';
                emploee_data += '</tr>';
                $('#emploe_table').append(emploee_data);

                $('#new_image').type = 'hidden';
                // $('#new_image').style.display = 'none';
                $('#new_price').val("");
                $('#new_name').val("");
                $('#new_upgrad').val("");
                $('#new_mining').val("");

            },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            },
        });
    });

    $(document).on('click', 'img[name^="im"]', function (e) {
        e.preventDefault();
        $('#new_image').val(this.id);
        $('#new_im').html('');
        $('#new_im').append('<img width="70" height="50" src=' + this.id + ' className="rounded" />');
    });

    $('#log_out').click(function () {
        sessionStorage.clear();
        window.location = '../index.html';
    });

});