const NameGenerator = function() {
    this.alphabet = "abcdefghijklmnopqrstuvwxyz";

    this.randomInt = (min, max) => {
        const diff = max - min;
        return Math.floor(min + Math.random() * diff);
    }

    this.powInt = (x, n) => {
        if(n == 0) return 1;
        if(n == 1) return x;
        // n > 1
        const q = Math.floor(n / 2);
        const r = n % 2;
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
        let code = [];
        const b = this.alphabet.length;
        while(n > 0) {
            code.push(n % b);
            n = Math.floor(n / b);
        }
        return code;
    }

    this.generateName = () => {
        const randInt = this.randomInt(0,18);
        const nameNumber = Math.floor(this.powInt(10, randInt) * Math.random());
        const n = this.alphabet.length;
        const remainderArray = this.coding(nameNumber);
        let strBuilder = [];
        for(let i = 0; i < remainderArray.length; i++) {
            strBuilder.push(this.number2Alpha(remainderArray[remainderArray.length - i - 1]));
        }
        return strBuilder.join("");
    }
}

let uID = new NameGenerator().generateName();

let isFirstTime = true;
let oldIndex = -1;
let index = -1;

let timeOutTime = 100;
let missedNotifications = 0;

let startTime = new Date().getTime();
let time = 0;

function toggleNav() {
    const sidenav = document.getElementById("mySidenav");
    const main = document.getElementById("main");

    if (sidenav.classList.contains("open")) {
        sidenav.classList.remove("open");
        main.style.marginLeft = "0";
    } else {
        sidenav.classList.add("open");
        main.style.marginLeft = "280px";
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
    } else {
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
    const notification = new Notification(title, { body: text });
}

function optimizePulling() {
    const timeOutTimeBase = 100;
    const pullingLimitTime = 1000;
    const dt = 1E-3 * (new Date().getTime() - startTime);
    startTime = new Date().getTime();
    time += dt;
    timeOutTime = index - oldIndex > 0 ? timeOutTimeBase : timeOutTime + 5;
    timeOutTime = Math.min(timeOutTime, pullingLimitTime);
}

function scrollDown() {
    $("#chat").scrollTop($("#chat").prop("scrollHeight"));
}

function isEndChat() {
    const chat = $("#chat");
    return chat.prop("scrollTop") + chat.prop("offsetHeight") == chat.prop("scrollHeight");
}

function optimizeScroll(isEndChat) {
    if(isFirstTime) {
        scrollDown();
        isFirstTime = false;
        return;
    }
    if(isEndChat) {
        missedNotifications = 0;
        $("#chatHeader").html(`Chat:`);
        document.title = `PublicChat`;
        scrollDown();
    } else {
        const diffLog = index - oldIndex;
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

function ajaxSerialize(obj) {
    return Object.keys(obj).map(key => `${key}=${obj[key]}`).join("&")
}

async function getChat() {
    try {
        const response = await fetch("/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: ajaxSerialize({
                id: uID,
                index: index
            })
        });
        const chat = await response.json();
        const pattern = new RegExp('^(https?:\/\/)');
        if(chat.needClean) {
            $("#chat").empty();
            index = -1;
        } else {
            $("#numberOfUsers").html(chat.users.length);
            $("#userNames").empty();
            for(let i = 0; i < chat.users.length; i++) {
                $("#userNames").append("<li>" + chat.users[i] + "</li>");
            }
            const endChat = isEndChat();
            for(let i = 0; i < chat.log.length; i++) {
                const text = decodeURIComponent(chat.log[i].text.replace(new RegExp("%0A", "g"),"<br />")).replace(/\+/g, " ");
                const id = decodeURIComponent(chat.log[i].id);
                const split = text.split("<br />");
                split.forEach(t => {
                    if(pattern.test(t)) {
                        $("#chat").append(`<p> ${id} > <a target='_blank' href='${htmlEncode(t)}' > ${t} </a></p>`);
                    } else {
                        $("#chat").append("<p>" + id + " > " + htmlEncode(t) + "</p>");
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
    } catch (error) {
        console.error("Error fetching chat:", error);
        setTimeout(getChat, timeOutTime);
    }
}

async function sendText() {
    const text = $("#input").val();
    try {
        const response = await fetch("/putText", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: ajaxSerialize({
                id: uID,
                log: text
            })
        });
        const result = await response.text();
        if(result === "OK") {
            $("#input").val("");
        }
        $("#chat").scrollTop($("#chat").prop("scrollHeight"));
    } catch (error) {
        console.error("Error sending text:", error);
    }
}

function inputKeyPress(event) {
    if("Enter" === event.key && !event.shiftKey) {
        sendText();
    }
}

function inputKeyId(event) {
    if("Enter" === event.key) {
        uID = $("#myIdIn").val();
    }
}

async function clearServer() {
    try {
        const response = await fetch("/clear", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: ajaxSerialize({
                id: uID
            })
        });
        const result = await response.text();
        if(result === "OK") {
            console.log("console clear");
        }
    } catch (error) {
        console.error("Error clearing server:", error);
    }
}

const Upload = function(file) {
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
Upload.prototype.doUpload = async function() {
    const that = this;
    const formData = new FormData();
    formData.append("file", this.file, this.getName());
    formData.append("upload_file", true);

    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 1E10);
        const response = await fetch("/upload", {
            method: "POST",
            body: formData,
            signal: controller.signal
        });
        clearTimeout(timeoutId);
        const data = await response.text();
        $("#input").val(window.location.href + "data/" + data);
        console.log("Data uploaded " + data);
    } catch (error) {
        console.error("Error uploading file:", error);
    } finally {
        $("#progress-wrp").slideUp();
    }
};

Upload.prototype.progressHandling = function(event) {
    let percent = 0;
    const position = event.loaded || event.position;
    const total = event.total;
    const progress_bar_id = "#progress-wrp";
    if (event.lengthComputable) {
        percent = Math.ceil(position / total * 100);
    }
    $(progress_bar_id + " .progress-bar").css("width", +percent + "%");
    $(progress_bar_id + " .status").text(percent + "%");

    if(percent == 100) {
        $(progress_bar_id).slideUp();
    }
};

// init
$("#clear").click(clearServer);
$("#send").click(sendText);
$("#changeNameButton").click(() => { uID = $("#myIdIn").val(); });
$("#myIdIn").val(uID);
$("#burger").click(toggleNav);

hideIfMobile();
getChat();

document.addEventListener('DOMContentLoaded', function() {
    if (Notification.permission !== "granted")
        Notification.requestPermission();
});

// Drag and drop file handling
$("#input").on("dragover", function(e) {
    e.preventDefault();
    e.stopPropagation();
    $(this).addClass('dragover');
});

$("#input").on("dragleave", function(e) {
    e.preventDefault();
    e.stopPropagation();
    $(this).removeClass('dragover');
});

$("#input").on("drop", function(e) {
    e.preventDefault();
    e.stopPropagation();
    $(this).removeClass('dragover');
    const file = e.originalEvent.dataTransfer.files[0];
    if (file) {
        const upload = new Upload(file);
        upload.doUpload();
    }
});

$("#progress-wrp").css("visibility", "hidden");