<#import "coramacros.ftl" as coramacros>

<@coramacros.page>
   	 <div class="container">
      <div class="panel panel-primary">
		<div class="panel-heading">
    	   <h2>Processing New Schedule</h2>
	    </div>
	    
	    
	    
	    <#if doRefresh>
		    <div style="display:none;" id="myDiv" class="animate-bottom">
		        <meta http-equiv="refresh" content="3;url=uploadwait" />
		    </div>
		    <script>
		        var myVar;
		
		        function myFunction() {
		            myVar = setInterval(showPage, 1000);
		        }
		
		        function showPage() {
		            document.getElementById("loader").style.display = "none";
		            document.getElementById("myDiv").style.display = "block";
		        }
		    </script>
		</#if>
	    <#if showConfirm>
	    	<a href="/corawebif/uploadconfirm" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Accept Changes</a>
		</#if>
	    <#if showDone>
	      <#if showError>
	    	<p>Schedule updating has failed</p>
		  <#else>
	    	<p>Schedule updating has completed</p>
		  </#if>
        	<a href="/corawebif" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Return to Main</a>
		</#if>
	
	    <#if stderr != "">
		    <div style="overflow:auto;border:8px solid red;padding:2%">
		    	<p>Error message:</p>
		    	<pre>
					${stderr}
				</pre>
		    </div>
		</#if>

	    <#if showCancel>
		    <a href="/corawebif/uploadcancel" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Cancel</a>
		</#if>

	    <div style="overflow:auto;border:8px solid purple;padding:2%">
	    	<pre>
				${stdout}
			</pre>
	    </div>

	    <#if showConfirm>
	    	<a href="/corawebif/uploadconfirm" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Accept Changes</a>
		</#if>
	    <#if showDone>
	      <#if showError>
	    	<p>Schedule updating has failed</p>
		  <#else>
	    	<p>Schedule updating has completed</p>
		  </#if>
	    	<a href="/corawebif" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Return to Main</a>
		</#if>
		
	    <#if showCancel>
		    <a href="/corawebif/uploadcancel" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Cancel</a>
		</#if>

      </div>
    </body>
    
    
</@coramacros.page>
