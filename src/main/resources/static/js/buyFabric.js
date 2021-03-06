$(document).ready(function () {

    console.log("Hello buyFabric.js");

    $.ajax('/api/user/factory-market', {
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        headers: {token: sessionStorage.getItem("token")},
        success: function (data) {
            model.fabrics = data.fabrics;
            initialize();
        },
        error: function () {
            alert("No server connection");
        }
    });

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
        this.fabricName = ko.observable('');
        this.price = ko.observable('');
        this.miningPerSecond = ko.observable('');
        this.upgrade = ko.observable('');
        this.fabrics = ko.observableArray();
        this.userInfo = ko.observableArray();
        this.usersInfo = ko.observableArray();

        this.buyFabric = function(vieModel){
            self.model.buyFabric(vieModel);
        };

        initialize = function () {
            self.fabrics(self.model.fabrics);
            self.userInfo(self.model.userInfo);
            self.usersInfo(self.model.usersInfo);
        };

        this.leftMenuButton = function(){
            $('.sidebar').toggleClass('active-sidebar');
            $('.content').toggleClass('active-content-right');
            $('.icon-menu-right').toggleClass('icon-menu-right-action');
        };

        this.rightMenuButton = function(){
            $('.user-info').toggleClass('user-info-active');
            $('.content').toggleClass('active-content-left');
            $('.icon-menu-left').toggleClass('icon-menu-left-action');
        };

        this.logOut = function () {
            sessionStorage.clear();
            window.location = '../index.html';
        };
    };

    var Model = function () {
        this.fabrics = [];
        this.userInfo = [];
        this.usersInfo = [];

        this.buyFabric = function (vieModel) {
            $.ajax('/api/user/buy-factory', {
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                headers: {token: sessionStorage.getItem("token")},
                data: JSON.stringify({id: vieModel.id}),
                success: function (data) {
                    $('#myModal').modal('show');
                },
                error: function (jqXHR) {
                    alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
                },
            })
        }
    };

    var model = new Model();
    ko.applyBindings(new Vm(model));

    setInterval(function () {
        getUsersInfo();
    }, 1000);

    getUsersInfo();
});