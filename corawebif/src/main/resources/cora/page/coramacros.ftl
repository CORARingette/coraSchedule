<#macro errormessages>
   <!-- Avoid the message if none present: -->
    <#if message??>
 	   	 <div class="container">
	      <div class="panel panel-primary">
            <p style="color:red;">${message}</p>
      
	      </div>
	     </div>
    </#if>
    
</#macro>


<#macro page>

	<!DOCTYPE html>
	<html lang="en">
	<head>
	  <title>CORA Schedule Loader</title>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	  <meta name="viewport" content="width=device-width, initial-scale=1">
	  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
	  <link rel="shortcut icon" href="/corawebif/assets/cropped-ice-bear-logo-v4-32x32.jpg">
	  <link rel="icon" href="/corawebif/assets/cropped-ice-bear-logo-v4-32x32.jpg" type="image/png">
	  <link href="/corawebif/assets/css/style.css" rel="stylesheet">

	  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
	  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
	</head>
	<body>

	<nav class="navbar navbar-expand-sm bg-light navbar-light">
	  <!-- Brand/logo -->
	  <a class="navbar-brand" href="/">
	  	<img src="/corawebif/assets/ice-bear-purple.jpg" width="100">
	  </a>
	  
	  <!-- Links -->
	  <ul class="navbar-nav">
	    <li class="nav-item">
	      <a class="nav-link" href="/corawebif">CORA Schedule Uploader</a>
	    </li>
	  </ul>
	  
	  <ul class="navbar-nav ml-auto">
	    <li class="nav-item">
	      <a class="nav-link" href="/corawebif/profile">Profile</a>
	    </li>
	    <li class="nav-item">
	      <a class="nav-link" href="/corawebif/logout">Logout</a>
	    </li>
	  </ul>
	</nav>
	
      <@errormessages/>

	  <#--  Enclosed content -->
	  <#nested>  


	</body>
	</html>
</#macro>

