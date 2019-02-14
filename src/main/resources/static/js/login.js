$(document).ready(function () {

    console.log("Hello login.js");

    var ViewModel = function () {
        var self = this;

        this.userName = ko.observable('');
        this.userPassword = ko.observable('');
        this.isVisible = ko.observable(false);

        this.login = function () {
            if (this.userName().replace(/\s/g, '') === '' || this.userPassword().replace(/\s/g, '') === '') {
                alert("All form fields must be filled in!");
            } else {

                $.ajax("/api/guest/log-in", {
                    type: 'POST',
                    dataType: 'json',
                    contentType: 'application/json',
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader("Authorization", "Basic " + btoa(self.userName() + ":" + self.userPassword()));
                    },
                    success: function (data) {
                        sessionStorage.setItem("token", data.message);
                        sessionStorage.setItem("email", data.email);
                        sessionStorage.setItem("rateSilver", data.rateSilver);
                        sessionStorage.setItem("rateGold", data.rateGold);
                        sessionStorage.setItem("price", data.price);
                        if (data.role === "ADMIN")
                            window.location = '../admin/addFabric.html';
                        else
                            window.location = '../user/dashboard.html';
                    },
                    error: function (jqXHR) {
                        alert(jqXHR.status + '  -  ' + jqXHR.statusText);
                        self.isVisible(true);
                    }
                })
            }
        }
    };
    ko.applyBindings(new ViewModel());
});