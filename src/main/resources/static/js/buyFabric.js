$(document).ready(function () {

    console.log("Hello buyFabric.js");

    let fabricsList = function (data) {
        data.fabrics.forEach(function (item) {
            let emploeeData = '';
            emploeeData += '<tr>';
            emploeeData += '<td align="left">' + item.fabricName + '</td>';
            emploeeData += '<td align="center">' + item.price + '</td>';
            emploeeData += '<td align="center">' + item.upgrade + '</td>';
            emploeeData += '<td align="center">' + item.miningPerSecond + '</td>';
            emploeeData += '<td align="center"><button id="' + item.id + '" name="bu' + item.id + '" type="button" class="btn btn-primary btn-sm">Buy</button>';
            emploeeData += '</tr>';
            $('#factoriesToBuy').append(emploeeData);
        })
    };

    $.ajax('/api/user/factory-market', {
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        headers: {token: sessionStorage.getItem("token")},
        success: function (data) {
            fabricsList(data);
        },
        error: function (jqXHR) {
            alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
        },
    });

    let buyFabric = function (id) {
        $.ajax('/api/user/buy-factory', {
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: JSON.stringify({id: id}),
            success: function (data) {
                alert(data.message);
            },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            },
        })
    };

    $(document).on('click', 'button[name^="bu"]', function (e) {
        e.preventDefault();
        buyFabric(this.id);
    });
});