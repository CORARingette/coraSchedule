<#ftl encoding="utf-8">
<#import "coramacros.ftl" as coramacros>

<@coramacros.page>
   	 <div class="container">
      <div class="panel panel-primary">
		<div class="panel-heading">
    	   <h2>View Arena List</h2>
	    </div>
        <table>
        <tr>
          <th>Internal Name</th>
          <th>Allowed Spreadsheet Names</th>
        </tr>
          
	    <#list arenaInfo as a_info>
	       <tr>
	         <td>
	           ${a_info.arenaName}
	         </td>
	         <td>	          
	           <#list a_info.nameList as a_name>
	             ${a_name} <br>
	           </#list>
	         </td>
	       </tr>
	       
	    </#list>
	    </table>
	    <br>
	    
      </div>
    </body>
    
    
</@coramacros.page>
