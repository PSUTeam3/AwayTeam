$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

function createUser(params, successFunction, errorFunction){
    $.ajax({
           type: "POST",
           data: params,
           url: "http://api.awayteam.redshrt.com/user/createuser",
           contentType: "application/x-www-form-urlencoded",
           dataType: "json",
           success: function (data) {
                successFunction(data);
           },
           error: function (jqXHR, textStatus, errorThrown) {
                errorFunction(jqXHR, textStatus, errorThrown);  
           }
    });
    return false;
}

function authenticate(params, successFunction, errorFunction){
    $.ajax({
           type: "POST",
           data: params,
           url: "http://api.awayteam.redshrt.com/user/AuthenticatePassword",
           contentType: "application/x-www-form-urlencoded",
           dataType: "json",
           success: function (data) {
                successFunction(data);
           },
           error: function (jqXHR, textStatus, errorThrown) {
                errorFunction(jqXHR, textStatus, errorThrown);
           }
    });
}