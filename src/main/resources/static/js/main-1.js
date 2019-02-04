$(document).ready(function () {

    console.log("Hello main-1.js");

    let userInafo = function (data) {
        document.getElementById('userName').innerHTML = '<text>' + data.name + ':</text>';
        document.getElementById('silverBal').innerHTML = '<text>Silver balance - ' + data.silverBalance + ';</text>';
        document.getElementById('goldBal').innerHTML = '<text>Gold balance - ' + data.goldBalance + ';</text>';
        document.getElementById('increase').innerHTML = '<text>Increase per second - ' + data.increase + ';</text>';
    };

    let usersInafo = function (data) {
        $('#usersList').html('');
        let list;
        data.forEach(function (item) {
            list = '<li>';
            list += item.name + ' - ';
            list += item.silverBalance + ';';
            list += '</li>';
            $('#usersList').append(list);
        })
    };

    let getUsersInfo = function () {
        $.ajax('/api/user/dashboard', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            success: function (data) {
                userInafo(data.user);
                usersInafo(data.users)
            },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            },
        });
    };

    $('#logOut').click(function () {
        sessionStorage.clear();
        window.location = '../index.html';
    });

    setInterval(function () {
        getUsersInfo();
    }, 1000);

    getUsersInfo();
});