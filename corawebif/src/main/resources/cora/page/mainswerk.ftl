<#import "coramacros.ftl" as coramacros>

<@coramacros.page>
	   	 <div class="container">
	      <div class="panel panel-primary">
			<div class="panel-heading">
	    	   <h2>Home Page</h2>
		    </div>
		    <p> This is a system to allow administrators to upload new schedules from an ScheduleWerks export file and have 
		    those schedules loaded into TeamSnap.</p>
		    <br>
		    <a href="/corawebifswerk/uploadnewschedule" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Upload New Schedule Spreadsheet</a>
		    <br>
		    <p> </p>
		    <a href="/corawebifswerk/rerunschedule" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Update Schedule From Last Spreadsheet</a>
		    <br>
		    <p> </p>
		    <a href="/corawebifswerk/downloadlastschedule" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Download Last Schedule Uploaded</a>
		    <br>
		    <p> </p>
		    <a href="/corawebifswerk/viewarenalist" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">View Arena List</a>
   	        <br>
		    <p> </p>
		    
	      </div>
	     </div>
</@coramacros.page>
