var data, rating = 0;

function showUsersMovies(response) {
    document.getElementById('right').innerHTML = "";
    var elem = document.getElementById('left');
    elem.innerHTML = "";
    var mov = JSON.parse(response);
    if (mov.empty) {
        elem.innerHTML += "Tento uživatel neohodnotil žádný film.";
    } else {
        for (var i = 0; i < mov.items.length; i++) {
            elem.innerHTML += getStars(mov.items[i].value) + " " + data.movies[mov.items[i].id].name + "<br>";
        }
    }
}

function showRecommendations(response) {
    var elem = document.getElementById('right');
    elem.innerHTML = "";
    var rec = JSON.parse(response);
    for (var i = 0; i < rec.items.length; i++) {
        elem.innerHTML += getStars(rec.items[i].value) + " " + data.movies[rec.items[i].id].name + "<br>";
    }
}

function checkRequest() {
    if(this.status == 200 && this.readyState == 4 && this.response != null) {
        if (this.option === "users") {
            data = JSON.parse(this.response);
            var elem = document.getElementById("users-sel");
            fillWithData(data.users.slice(), elem);
        } else if (this.option === "movies") {
            data = JSON.parse(this.response);
            elem = document.getElementById("movies-sel");
            fillWithData(data.movies.slice(), elem);
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

function httpGet(theUrl, option) {
    var request = new XMLHttpRequest();
	request.onload = checkRequest;
    request.option = option;
	request.open("GET", theUrl);
	request.send();
}

function recommend() {
    var elem = document.getElementById("users-sel");
    var slider = document.getElementById("slider");
    httpGet("http://localhost:8080/recommender/recommender?userID=" + elem.value + "&howMany=" + slider.value, "recommend");
}

function fillWithData(sorted, elem) {
    var option;
    sorted.sort(function(a, b) {return (a.name > b.name) ? 1 : ((b.name > a.name) ? -1 : 0);});
    for (var i = 0; i < sorted.length; i++) {
        option = document.createElement("option");
        option.innerHTML = sorted[i].name.substr(0, 120);
        option.value = sorted[i].id;
        elem.appendChild(option);
    }
}

function getUsersMovies(elem) {
    httpGet("http://localhost:8080/recommender/recommender?userID=" + elem.value);
}

function getAllData() {
    httpGet("http://localhost:8080/recommender/recommender?getmovies=true", "movies");
    httpGet("http://localhost:8080/recommender/recommender?getusers=true", "users");
}

function setRating() {
    var elem = document.getElementById("users-sel");
    var select = document.getElementById("movies-sel");
    httpGet("http://localhost:8080/recommender/recommender?userID=" + elem.value + "&itemID="
        + select.value + "&value=" + rating, "rating");
    var res = document.getElementById("left");
    left.innerHTML = getStars(rating) + " " + select.options[select.selectedIndex].text + "<br>" + left.innerHTML;
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
