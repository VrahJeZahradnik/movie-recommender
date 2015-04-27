<!DOCTYPE html>

<html lang="cs" class="no-js">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Movie Recommender</title>
	<link rel="stylesheet" type="text/css" href="index.css" />
	<link rel="stylesheet" type="text/css" href="slider.css" />
	<script type="text/javascript" src="datahandler.js"></script>
	<script>
		getAllData();
	</script>
</head>
<body>
	<header>
		<select id="users-sel" onchange="getUsersMovies(this)">
		</select>
		<button onclick="recommend()">DOPORUČ</button>
		<div>
			<input id="slider" type="range" min="1" max="100" step="1" value="20" onchange="this.nextElementSibling.value=this.value">
			<output id="output" for="slider">20</output>
		</div>
		
	</header>
	
	<div id="container">
		<header>
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
		<div id="left">	
		</div>
		
		<div id="right">
		</div>
	</div>
</body>
</html>
