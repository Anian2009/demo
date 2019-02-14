$(document).ready(function () {

    console.log("Hello forgotPassword.js");

    var VewModel = function () {
        var self = this;

        this.isVisible = ko.observable(false);
        this.userName = ko.observable('');
        this.dis = ko.observable(false);

        this.submitButton = function () {
            console.log('Submit start');
            $.ajax("/api/guest/forgotPassword", {
                type: 'POST',
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify({
                    email: self.userName()
                }),
                success: function () {
                    $('#myModal').modal('show');
                    self.dis(true);
                },
                error: function (jqXHR) {
                    alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
                    self.userName('');
                },
            })
        }

    };

    ko.applyBindings(new VewModel());
});