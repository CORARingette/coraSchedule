<#-- @ftlvariable name="error" type="cora.page.CwPageView" -->
<#import "coramacros.ftl" as coramacros>

		<!DOCTYPE html>
	<html lang="en">
	<head>
	  <title>Bootstrap Example</title>
	  <meta charset="utf-8">
	  <meta name="viewport" content="width=device-width, initial-scale=1">
	  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
	  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
	  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
	  <link href="/corawebif/assets/css/loginstyle.css" rel="stylesheet">

	</head>
	<body>
	
<div class="wrapper">
  <div id="formContent">
    <!-- Tabs Titles -->

    <!-- Icon -->
    <div>
      <img src="/corawebif/assets/ice-bear-purple.jpg" id="icon" alt="User Icon" />
    </div>

    <!-- Login Form -->
    <form autocomplete="on" method="post">
      <input type="text" id="login" name="username" placeholder="username" autocomplete="username">
      <input type="password" id="password"name="password" placeholder="password" autocomplete="current-password">
      <input type="submit" value="Log In">
    </form>


    <@coramacros.errormessages/>

    </div>
</div>

	</body>
	</html>
