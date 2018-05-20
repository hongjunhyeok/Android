<?php
$con = mysqli_connect("localhost","ygyg331","ghdwnsgur331","ygyg331");

$userID = $_POST["userID"];
$userPassword = $_POST["userPassword"];

$statement = mysqli_prepare($con,"SELECT * FROM login WHERE userID =? AND userPassword =?");
mysqli_stmt_bind_param($statement, "ss", $userID, $userPassword);
mysqli_stmt_execute($statement);

mysqli_stmt_store_result($statement);
mysqli_stmt_bind_result($statement,$userID,$userPassword,$userName,$userAge);

$response = array();
$response["success"] = false;
//해당 유저가 존재한다면 true 값을 가지게 된다.
while(mysqli_stmt_fetch($statement)){
    $response["success"]= true;
    $response["userID"]=$userID;
    $response["userPassword"]=$userPassword;
    $response["userName"]=$userName;
    $response["userAge"]=$userAge;        
}

echo json_encode($response);
?>