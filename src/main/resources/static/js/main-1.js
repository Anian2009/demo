$(document).ready(function () {

    console.log("Hello main-1.js");

    let getUsersInfo = function () {
        $.ajax('/api/user/dashboard', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            success: function (data) {
                model.userInfo = data.user;
                model.usersInfo = data.users;
                initialize();
            },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            },
        });
    };

    var Vm = function (model) {
        this.model = model;
        var self = this;

        this.name = ko.observable('');
        this.silverBalance = ko.observable('');
        this.goldBalance = ko.observable('');
        this.increase = ko.observable('');

        this.userInfo = ko.observableArray();
        this.usersInfo = ko.observableArray();

        initialize = function () {
            self.userInfo(self.model.userInfo);
            self.usersInfo(self.model.usersInfo);
        }
    };

    var Model = function () {
        this.userInfo = [];
        this.usersInfo = [];

    };

    var model = new Model();
    ko.applyBindings(new Vm(model));

    $('#logOut').click(function () {
        sessionStorage.clear();
        window.location = '../index.html';
    });

    setInterval(function () {
        getUsersInfo();
    }, 1000);

    getUsersInfo();
});