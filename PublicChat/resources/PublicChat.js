var uID = "id" + (Math.random() * new Date().getTime());

var isFirstTime = true;
var oldIndex = -1
var index = -1;

var timeOutTime = 100;
var missedNotifications = 0;

var startTime = new Date().getTime();
var time = 0;

function toggleNav() {
    if($("#burger").attr("isOn") == "true") {
        document.getElementById("mySidenav").style.width = "0";
        document.getElementById("main").style.marginLeft= "0";
        $("#burger").html("&#9776;")
        $("#burger").attr("isOn", "false");
    } else {
        document.getElementById("mySidenav").style.width = "250px";
        document.getElementById("main").style.marginLeft = "250px";
        $("#burger").html("&times;");
        $("#burger").attr("isOn", "true");
    }
}

function isPhone() {
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
}

function isChrome() {
    return /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor);
}

function hideIfMobile() {
    if(isPhone()) {
        $("#shiftPlusEnterP").hide();
    }else {
        $("#send").hide();
    }
}

function htmlEncode(value) {
    //create a in-memory div, set it's inner text(which jQuery automatically encodes)
    //then grab the encoded contents back out.  The div never exists on the page.
    return $('<div/>').text(value).html();
}

function htmlDecode(value) {
    return $('<div/>').html(value).text();
}

function generateNotification(title, text) {
    var notification = new Notification(title, { body: text});
}

function optimizePulling() {
    var timeOutTimeBase = 100;
    var pullingLimitTime = 1000;
    var dt = 1E-3 * (new Date().getTime() - startTime);
    startTime = new Date().getTime();
    time += dt;
    timeOutTime = index - oldIndex > 0 ? timeOutTimeBase : timeOutTime + 5;
    timeOutTime = Math.min(timeOutTime, pullingLimitTime);
}

function scrollDown() {
    $("#chat").scrollTop( $("#chat").prop("scrollHeight"));
}

function isEndChat() {
    let chat = $("#chat");
    return chat.prop("scrollTop") + chat.prop("offsetHeight") == chat.prop("scrollHeight");
}

function optimizeScroll(isEndChat) {
    if(isFirstTime) {
        scrollDown();
        isFirstTime = false;
        return;
    }
    if(isEndChat){
        missedNotifications = 0;
        $("#chatHeader").html(`Chat:`);
        document.title = `PublicChat`;
        scrollDown();
    } else {
        let diffLog = index - oldIndex;
        missedNotifications += diffLog;
        $("#chatHeader").html(`Chat:<a onclick=scrollDown()>Scroll down${missedNotifications == 0 ? "" : `(${missedNotifications})`} <i class="fas fa-chevron-down"></i></a>`);
        document.title = `(${missedNotifications})PublicChat`;
    }
}

function optimizeNotification(id, text) {
    if(id !== uID && !isChrome() && document.visibilityState == "hidden") {
        generateNotification("PublicChat", `${id} > ${text}`);
    }
}

function getChat() {
    $.ajax({
        method:"POST",
        url:"/chat",
        data: {
           id : uID,
           index : index
        },
        success: function(result) {
            var pattern = new RegExp('^(https?:\/\/)');
            var chat = JSON.parse(result);
            if(chat.needClean) {
                $("#chat").empty();
                index = -1;
            } else {
                $("#numberOfUsers").html(chat.users.length);
                $("#userNames").empty()
                for(var i = 0; i < chat.users.length; i++) {
                    $("#userNames").append("<li>" + chat.users[i] + "</li>");
                }
                let endChat = isEndChat();
                for(var i = 0; i < chat.log.length; i++) {
                    // replace new line in http (%0A) by new line in HTML (<br />) then  decode http and replace spaces in http(+) by a space char
                    var text = decodeURIComponent(chat.log[i].text.replace(new RegExp("%0A", "g"),"<br />")).replace(/\+/g,  " ");
                    var id = decodeURIComponent(chat.log[i].id);
                    var split = text.split("<br />");
                    split.forEach(t => {
                        if(pattern.test(t)) {
                            $("#chat").append(`<p> ${id} > <a target='_blank' href='${htmlEncode(t)}' > ${t} </a></p>`);
                        } else {
                            $("#chat").append("<p>" + id + " > " + htmlEncode(t) + "</p>")
                        }
                    });
                    optimizeNotification(id, text);
                }
                optimizeScroll(endChat);
                oldIndex = index;
                index += chat.log.length;
            }
            optimizePulling();
            setTimeout(getChat, timeOutTime);
        }
    });
}

function sendText() {
    var text = $("#input").val();
    //send request to server
    $.ajax({
            method:"POST",
            url:"/putText",
            data: {
               id : uID,
               log : text
            },
            success: function(result) {
                if("OK" === result) {
                    $("#input").val("");
                }
            }
        });
    $( "#chat" ).scrollTop( $("#chat").prop("scrollHeight"));
}

function inputKeyPress(event) {
    if("Enter" === event.key && !event.shiftKey) {
        sendText();
    }
}

function inputKeyId(event) {
    if("Enter" === event.key ) {
        uID = $("#myIdIn").val();
    }
}

function clearServer() {
        $.ajax({
                method:"POST",
                url:"/clear",
                data: {
                   id : uID
                },
                success: function(result) {
                    if("OK" === result) {
                        console.log("console clear");
                    }
                }
            });
}

var NameGenerator = function() {
    this.alphabet = "abcdefghijklmnopqrstuvwxyz";

    this.randomInt = (min, max) => {
        let diff = max  -  min;
        return Math.floor(min + Math.random() * diff);
    }

    this.powInt = (x, n) => {
      if(n == 0) return 1;
      if(n == 1) return x;
      // n > 1
      var q = Math.floor(n / 2);
      var r = n % 2;
      if(r == 0)
        return this.powInt(x * x, q);
      else
        return x * this.powInt(x * x, q);
    }

    // n must be in [0, ..., this.alphabet.length - 1]
    this.number2Alpha = n => {
        return this.alphabet[n];
    }

    this.coding = n => {
        var code = [];
        let b = this.alphabet.length;
        while(n > 0) {
            code.push(n % b);
            n = Math.floor(n / b);
        }
        return code;
    }

    this.generateName = () => {
        let randInt = this.randomInt(0,18);
        let nameNumber = Math.floor(this.powInt(10, randInt) * Math.random());
        let n = this.alphabet.length;
        let remainderArray = this.coding(nameNumber);
        var strBuilder = [];
        for(let i = 0; i < remainderArray.length; i++) {
            strBuilder.push(this.number2Alpha(remainderArray[remainderArray.length - i - 1]));
        }
        return strBuilder.join("");
    }
}

// taken from https://stackoverflow.com/questions/2320069/jquery-ajax-file-upload answer by Ziinloader
var Upload = function (file) {
    this.file = file;
};

Upload.prototype.getType = function() {
    return this.file.type;
};
Upload.prototype.getSize = function() {
    return this.file.size;
};
Upload.prototype.getName = function() {
    return this.file.name;
};
Upload.prototype.doUpload = function () {
    var that = this;
    var formData = new FormData();

    // add assoc key values, this will be posts values
    formData.append("file", this.file, this.getName());
    formData.append("upload_file", true);

    $.ajax({
        type: "POST",
        url: "/upload",
        xhr: function () {
            $("#progress-wrp").css("visibility", "visible");
            $("#progress-wrp").slideDown();
            var myXhr = $.ajaxSettings.xhr();
            if (myXhr.upload) {
                myXhr.upload.addEventListener('progress', that.progressHandling, false);
            }
            return myXhr;
        },
        success: function (data) {
            $("#input").val(window.location.href + "/data/" + data);
            // your callback here
            console.log("Data uploaded " + data);
        },
        error: function (error) {
            // handle error
        },
        async: true,
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        timeout: 1E10
    });
};

Upload.prototype.progressHandling = function (event) {
    var percent = 0;
    var position = event.loaded || event.position;
    var total = event.total;
    var progress_bar_id = "#progress-wrp";
    if (event.lengthComputable) {
        percent = Math.ceil(position / total * 100);
    }
    // update progress bars classes so it fits your code
    $(progress_bar_id + " .progress-bar").css("width", +percent + "%");
    $(progress_bar_id + " .status").text(percent + "%");

    if(percent == 100) {
        $(progress_bar_id).slideUp();
    }
};

// init
uID = new NameGenerator().generateName();

$("#clear").click(clearServer);
$("#send").click(sendText);
$("#changeNameButton").click(() => { uID = $("#myIdIn").val(); });
$("#myIdIn").val(uID);
$("#burger").click(toggleNav);

hideIfMobile();
getChat();

document.addEventListener('DOMContentLoaded', function () {
  if (Notification.permission !== "granted")
    Notification.requestPermission();
});

$("#file").on("change", function (e) {
    var file = $(this)[0].files[0];
    var upload = new Upload(file);
    // execute upload
    upload.doUpload();
});
$("#progress-wrp").css("visibility", "hidden");