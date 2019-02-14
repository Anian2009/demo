$(document).ready(function () {

    console.log("Hello changePassword.js");

    var code = window.location.toString().split("code=")[1];

    var VewModel = function () {
        var self = this;

        $.ajax('/changePasswordCode/'+code, {
            type: 'PUT',
            dataType: 'json',
            contentType: 'application/json',
            success: function () {
                self.dis(false);
            },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
                setTimeout(function () {
                    window.location = '/';
                }, 3000)
            }
        });

        this.userPassword2 = ko.observable('');
        this.userPassword1 = ko.observable('');
        this.dis = ko.observable(true);

        this.changePassword = function () {
            if (validate(self.userPassword2(), self.userPassword1())){
                $.ajax('api/guest/changePassword', {
                    type: 'POST',
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        password: self.userPassword2(),
                        code: code
                    }),
                    success: function () {
                        self.dis(true);
                        $('#myModal').modal('show');
                        setTimeout(function () {
                            window.location = 'login.html';
                        }, 3000)
                    },
                    error: function (jqXHR) {
                        alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
                    }
                });
            }
        }
    };

    validate = function (password1, password2) {
        password1 = password1.replace(/\s/g, '');
        password2 = password2.replace(/\s/g, '');

        if (password1 === '' || password1 !== password2){
            alert("Invalid password");
            return false;
        } else{
            return true;
        }
    };

    ko.applyBindings(new VewModel());
});