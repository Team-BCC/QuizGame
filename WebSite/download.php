<?php
  header('Content-Type: application/download');
  header('Content-Disposition: attachment; filename="QuizGame.zip"');
  header("Content-Length: " . filesize("QuizGame.zip"));
  $fp = fopen("QuizGame.zip", "r");
  fpassthru($fp);
  fclose($fp);
?>