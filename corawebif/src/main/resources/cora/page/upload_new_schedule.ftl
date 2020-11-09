<#import "coramacros.ftl" as coramacros>

<@coramacros.page>
   	 <div class="container">
      <div class="panel panel-primary">
		<div class="panel-heading">
    	   <h2>Upload a New Schedule</h2>
	    </div>
	    <p> Choose an Excel file to upload.  You will be asked to confirm the changes.</p>
	    <br>
	    
		<form>
		  <div class="form-group">
		    <label for="exampleFormControlFile1">Example file input</label>
		    <input type="file" class="form-control-file" id="exampleFormControlFile1">
		  </div>
		
		  <button type="submit" formmethod="post" class="btn btn-primary btn-lg active">Start Processing</button>
		  <a href="/" class="btn btn-primary btn-lg active">Cancel</a>
		  
		</form>
      </div>
    </body>
    
    
</@coramacros.page>
