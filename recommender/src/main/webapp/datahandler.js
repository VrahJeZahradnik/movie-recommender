/**
 * @fileoverview This script enables the communication between the client app and the recommender system by using asynchronous HTTP requests.
 * @author Matěj Lochman
 */
var rating = 0,
    URL = "http://localhost:8080/recommender/recommender?",
    NO_RATING = "Tento uživatel neohodnotil žádný film.",
    TEST_USER = 23609,
    data = {};

data.movies = {};
data.users = {};

function showUsersMovies(response) {
    document.getElementById('right').innerHTML = "";
    var elem = document.getElementById('left');
    elem.innerHTML = "";
    var mov = JSON.parse(response);
    if (mov.empty) {
        elem.innerHTML += NO_RATING;
    } else {
        for (var i = 0; i < mov.items.length; i++) {
            elem.innerHTML += getStars(mov.items[i].value) + " " + data.movies[mov.items[i].id] + "<br>";
        }
    }
}

function showRecommendations(response) {
    var elem = document.getElementById('right');
    elem.innerHTML = "";
    var rec = JSON.parse(response);
    for (var i = 0; i < rec.items.length; i++) {
        elem.innerHTML += getStars(rec.items[i].value) + " " + data.movies[rec.items[i].id] + "<br>";
    }
}

function checkRequest() {
    if(this.status == 200 && this.readyState == 4 && this.response != null) {
        if (this.option === "users") {
            var elem = document.getElementById("users-sel");
            fillWithData(JSON.parse(this.response), elem, data.users);
            elem.value = TEST_USER;
        } else if (this.option === "movies") {
            var elem = document.getElementById("movies-sel");
            // data.sorted.movies = JSON.parse(JSON.stringify(data.movies));
            fillWithData(JSON.parse(this.response), elem, data.movies);
            document.getElementById("users-sel").onchange();
        } else if (this.option === "recommend") {
            showRecommendations(this.response);
        } else if (this.option === "rating") {
            if (this.response === "fail") {
                alert("Failed to send rating.");
            }
        } else {
            showUsersMovies(this.response);
        }
    } else {
        alert("Failed to get response.");
    }
}

function httpGet(url, option) {
    url = URL + url;
    document.getElementById("progress").className = "";
    var request = new XMLHttpRequest();
	request.onload = checkRequest;
    request.option = option;
    request.onloadend = function(pe) {
        document.getElementById("progress").className = "stop";
    };
    request.open("GET", url, true);
	request.send();
}

function recommend() {
    var elem = document.getElementById("users-sel");
    var slider = document.getElementById("slider");
    httpGet("userID=" + elem.value + "&howMany=" + slider.value, "recommend");
}

function fillWithData(array, elem, dat) {
    var option, item = [];
    for (var i = 0; i < array.length; i++) {
        item = array[i];
        option = document.createElement("option");
        option.innerHTML = item[1];
        option.value = item[0];
        elem.appendChild(option);
        dat[item[0]] = item[1];
    }
}

// function fillWithData(obj, elem) {
//     var option, sorted = [];
//     for (var item in obj)
//         sorted.push([item, obj[item]]);

//     sorted.sort(function(a, b) {
//         return (a[1].toLowerCase() > b[1].toLowerCase()) ? 1
//                 : ((b[1].toLowerCase() > a[1].toLowerCase()) ? -1
//                 : 0);
//     });
    
//     for (var i = 0; i < sorted.length; i++) {
//         option = document.createElement("option");
//         option.innerHTML = sorted[i][1].substr(0, 120);
//         option.value = sorted[i][0];
//         elem.appendChild(option);
//     }
// }

function getUsersMovies(elem) {
    httpGet("userID=" + elem.value);
}

function getAllData() {
    httpGet("getmovies=true", "movies");
    httpGet("getusers=true", "users");
}

function setRating() {
    var elem = document.getElementById("users-sel");
    var select = document.getElementById("movies-sel");
    httpGet("userID=" + elem.value + "&itemID=" + select.value + "&value=" + rating, "rating");
    var res = document.getElementById("left");
    if (left.innerHTML === NO_RATING) {
        left.innerHTML = "";
    }
    // left.innerHTML = getStars(rating) + " " + select.options[select.selectedIndex].text + "<br>" + left.innerHTML;
    left.innerHTML = getStars(rating) + " " + data.movies[select.value] + "<br>" + left.innerHTML;
    rating = 0;
    var star = document.createElement('span');
    star.setAttribute("value", "0");
    starOnClick(star);
}

function getStars(count) {
    var stars = "";
    var count = Math.round(count);
    for (var i = 0; i < count; i++) {
        stars += "\u2605";
    }
    for (var i = 0; i < 5 - count; i++) {
        stars += "\u2606";
    }
    return stars;
}

function starOnClick(elem) {
    var span = document.getElementById("rating").getElementsByTagName('span');
    var count = elem.getAttribute("value");
    for (var i = 0; i < span.length; i++) {
        if (span[i].getAttribute("value") <= count) {
            if (count <= 1 && span[i].innerHTML === "\u2605") {
                span[i].innerHTML = "\u2606";
            } else {
                span[i].innerHTML = "\u2605";
            }
        } else {
            span[i].innerHTML = "\u2606";
        }
    }
    rating = count;
}
