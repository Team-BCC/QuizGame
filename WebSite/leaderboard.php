<!DOCTYPE html>
<html lang="it">
	<head>
		<link rel="shortcut icon" href="img/Montani.png" />
		<title>Quiz Game</title>

		<meta charset="UTF-8">
		<meta http-equiv="Content-type" content="text/html; charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		
		<meta name="description" content="Quiz Game">
		<meta name="keywords" content="Quiz Game">
		<meta name="author" content="Boussoufa Yacine">
		<meta name="author" content="Camilletti Samuele">
		<meta name="author" content="Cerasuolo Daniel Damiano">
		
		<link rel="stylesheet" href="css/bootstrap.css">
		<link rel="stylesheet" href="css/style.css">
	</head>

	<body>
	
		<div class="topnav">
			<a href="index.php">Home</a>
			<a href="info.php">Info</a>
			<a class="active">Leaderboard</a>
		</div>
	
		<div class="div1" id="nascondi" style="display:block;">

			<h1>Quiz Game</h1>
			<h3>Leaderboard</h3> <br>
            <?php

						function array_sort_by_column(&$array, $column, $direction = SORT_DESC) 
						{
						    $reference_array = array();

						    foreach($array as $key => $row) {
						        $reference_array[$key] = $row[$column];
						    }

						    array_multisort($reference_array, $direction, $array);
						}

						$xml = simplexml_load_file("/home/quintab2122/public_html/sistemi/BCCOrientamento/leaderboard.xml");
			        if($xml == NULL)
			          echo "Rotto";

						foreach($xml->children() as $element) 
						{
						    $data[] = array(
                 'nickname'             => (string)$element->nickname,
                 'points' => intval($element->points)
                );
                
						}

						array_sort_by_column($data, 'points');

						

            // Creazione tabella
            echo "<table class='table'>";
            echo "<tr>";
            echo "<th> Nickname </th>";
            echo "<th> Miglior punteggio </th>";
            echo "</tr>";

            // Stampa "in ampiezza" dell'albero
              foreach($data as $element)
              {
 

             				echo "<tr>";
                	echo "<td>".$element['nickname']."</td>";
                	echo "<td>".$element['points']."</td>";
                	echo"</tr>";   
      
  

              }
              echo "</table>";
            ?>
		</div>
		
	</body>
</html>