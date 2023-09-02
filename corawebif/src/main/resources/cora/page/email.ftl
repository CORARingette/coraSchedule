<#-- @ftlvariable name="error" type="cora.page.CwPageView" -->
<#import "coramacros.ftl" as coramacros>

		<!DOCTYPE html>
	<html lang="en">
	<head>
	  <title>CORA Schedule Loader</title>
	  <meta charset="utf-8">
	  <meta name="viewport" content="width=device-width, initial-scale=1">
	  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
	  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
	  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
	  <link href="/corawebif/assets/css/style.css" rel="stylesheet">

	</head>
	<body>
	
<div class="wrapper">
  <div id="formContent">
    <!-- Tabs Titles -->

    <!-- Icon -->
    <div>
      <img src="/corawebif/assets/ice-bear-purple.jpg" id="icon" alt="User Icon" />
    </div>

    <!-- Email Form -->
    <form autocomplete="on" method="post">
	<h2>
	City of Ottawa Ringette Association Contact Form
	</h2>

	  <!-- Uncomment for testing
	  <label for="destemail">Dest Email Address: </label>
      <input type="text" id="destemail2" name="destemail2" value="${(destEmail)!}" autocomplete="">
      -->
      <input type="hidden" id="destemail" name="destemail" value="${(destEmail)!}" autocomplete="">
      <br>
	  <label for="sourceemail">Your Email Address: </label>
      <input type="text" id="sourceemail" name="sourceemail" placeholder="" autocomplete="">
      <br>
	  <label for="subject">Subject: </label>
      <input type="text" id="subject" name="subject" placeholder="" autocomplete="">
      <br>
	  <label for="message">Your Message: </label>
	  <br>
      <textarea id="message" name="message" rows="8" cols="80"></textarea>
      <br>
      <input type="submit" value="Send Email">
    </form>


    <@coramacros.errormessages/>

    </div>
</div>

	</body>
	</html>
