<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>

<html lang="cs" class="no-js">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" pageEncoding="UTF-8"/>
	<title>Movie Recommender</title>
	<link rel="stylesheet" type="text/css" href="index.css" />
	<link rel="stylesheet" type="text/css" href="slider.css" />
	<script type="text/javascript" src="datahandler.js" charset="utf-8"></script>
	<script>
		
	</script>
</head>
<body>
	<header>
		<select id="users-sel" onchange="getUsersMovies(this)">
		</select>
		<button onclick="recommend()">DOPORUČ</button>
		<div>
			<input id="slider" type="range" min="20" max="500" step="10" value="100" onchange="this.nextElementSibling.value=this.value">
			<output id="output" for="slider">100</output>
		</div>
		
	</header>
	
	<div id="container">
		<header>
			<span class="label">Přidat hodnocení:<br><br></span>
			<select id="movies-sel"></select>
			<div id="rating">
				<span value="5" onclick="starOnClick(this)">&#9734</span>
				<span value="4" onclick="starOnClick(this)">&#9734</span>
				<span value="3" onclick="starOnClick(this)">&#9734</span>
				<span value="2" onclick="starOnClick(this)">&#9734</span>
				<span value="1" onclick="starOnClick(this)">&#9734</span>
			</div>
			<button onclick="setRating()">OHODNOŤ</button>
		</header>
		<div>
			<span class="label">Ohodnocené filmy:<br><br></span>
			<div id="left"></div>
		</div>
		<div>
			<span class="label">Doporučené filmy:<br><br></span>
			<div id="right"></div>
		</div>
	</div>
	<div id="progress" class="stop"><div>
	<!-- <progress id="progress" max="1.0" value="0"></progress> -->
	<script>
	getAllData();
// var progressBar = document.getElementById("p"),
// client = new XMLHttpRequest();
// client.open("GET", "http://localhost:8080/recommender/recommender?getmovies=true");
// client.onprogress = function(pe) {
// if(pe.lengthComputable) {
//   progressBar.max = pe.total;
//   progressBar.value = pe.loaded;
// }
// }
// client.onloadend = function(pe) {
// progressBar.value = pe.loaded;
// }
// client.send();
	</script>
</body>
</html>
