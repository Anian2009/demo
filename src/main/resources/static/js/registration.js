$(document).ready(function () {

    console.log("Hello registration.js");

    var ViewModel = function () {
        var self = this;

        this.userName = ko.observable('');
        this.userNick = ko.observable('');
        this.userPassword1 = ko.observable('');
        this.userPassword2 = ko.observable('');

        this.registration = function () {

            if (validate(this.userName(), this.userNick(), this.userPassword1(), this.userPassword2())) {

                $.ajax("/api/guest/registration", {
                    type: "POST",
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        name:  this.userNick(),
                        email: this.userName(),
                        password: this.userPassword1()
                    }),
                    success: function () {
                        $('#myModal').modal('show');
                        self.userName('');
                        self.userNick('');
                        self.userPassword1('');
                        self.userPassword2('');
                    },
                    error: function (jqXHR) {
                        alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
                    },
                })
            }
        }
    };

    validate = function(email, name, password1, password2){
        email = email.replace(/\s/g, '');
        name = name.replace(/\s/g, '');
        password1 = password1.replace(/\s/g, '');
        password2 = password2.replace(/\s/g, '');
        if (password1 !== password2) {
            alert("Invalid password");
            return false;
        }
        if (password1 === '' || name === '' || email === '') {
            alert("All fields must be filled!");
            return false;
        } else {
            return true;
        }
    };
    ko.applyBindings(new ViewModel());
});