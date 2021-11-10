<html>
	<head>
		<title></title>
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
		<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
		<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
	</head>
	<body>
		<nav class="navbar  navbar-dark bg-primary"">
		  <div class="container-fluid">
			<h5 style="color:white"><b>WM4M</b>  report</h5>
		  </div>
		</nav>  
		
		<br/>
		
		<div class="container">
		<h5>Project Name : ${projectName}</h5>
		<h5>${percentageMigrated} % migrated</h5>
		<br/>
		<div class="accordion" id="accordionExample">
		  <div class="card">
			<div class="card-header" id="headingOne" data-toggle="collapse" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
			  <h5 class="mb-0">
				
				<button class="btn btn-link" style="color:black" type="button" >
				  <b>Mule flow migration issue count: ${flowIssueCount}</b>
				</button>
			  </h5>
			</div>

			<div id="collapseOne" class="collapse show" aria-labelledby="headingOne" data-parent="#accordionExample">
			  <div class="card-body">
			        <#list flowIssuesMap as flowName, issueList>
						<div class="card">
							<div class="card-header" id="headingOne${flowName?index}" data-toggle="collapse" data-target="#collapseOne${flowName?index}" aria-expanded="true" aria-controls="collapseOne${flowName?index}">
							  <h6 class="mb-0">
								  <b>flow name : ${flowName} , issue count ${issueList?size}</b>
								</button>
							  </h6>
							</div>
							<#if flowName?is_first>
							  <#assign y = "show">
							<#else>
							  <#assign y = "">
							</#if>
							<div id="collapseOne${flowName?index}" class="collapse" aria-labelledby="headingOne${flowName?index}" >
							  <div class="card-body">
							     <#list issueList as issue>
									<div class="row">
										<div class="col">${issue.description}</div>
										<!--	<div class="col">${issue.lineNo}</div> -->
											<div class="w-100"></div>
											<br/>
											<div class="col-2">mule element xml snippet:</div>
										<textarea class="col" disabled style="padding-top:10px;">${issue.xmlSnippet}</textarea>
										<#if (issue.refferToHref)??>
											<div>
											<br/>
											<p>Refer to for further help and assistance: <a href="${issue.refferToHref}">${issue.refferToHref}</a></p>
											</div>
										</#if>
									</div>
									<hr>
								<#else>
									<div class="row">
										<div class="col">No issues found during migration.</div>
									</div>		
								 </#list>
							  </div>
							</div>
						</div>
					</#list>	
			  </div>
			</div>
		  </div>
		  <div class="card">
			<div class="card-header" id="headingTwo"  data-toggle="collapse" data-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
			  <h5 class="mb-0">
				<button class="btn btn-link collapsed mb-0" style="color:black" type="button">
					<b>MUnit migration issue count: ${munitIssueCount}</b>
				</button>
			  </h5>
			</div>
			<div id="collapseTwo" class="collapse" aria-labelledby="headingTwo" data-parent="#accordionExample">
			  <div class="card-body">
			        <#list munitIssuesMap as munitName, issueList>
						<div class="card">
							<div class="card-header" id="headingOne${munitName?index}" data-toggle="collapse" data-target="#collapseOne${munitName?index}" aria-expanded="true" aria-controls="collapseOne${munitName?index}">
							  <h6 class="mb-0">
								  <b>flow name : ${munitName} ,issue count ${issueList?size}</b>
								</button>
							  </h6>
							</div>
							<#if munitName?is_first>
							  <#assign y = "show">
							<#else>
							  <#assign y = "">
							</#if>
							<div id="collapseOne${munitName?index}" class="collapse" aria-labelledby="headingOne${munitName?index}" >
							  <div class="card-body">
							     <#list issueList as issue>
									<div class="row">
										<div class="col">${issue.description}</div>
											<!--<div class="col">${issue.lineNo}</div> -->
											<div class="w-100"></div>
											<br/>
											<div class="col-2">munit element xml snippet:</div>
										<textarea class="col" disabled style="padding-top:10px;">${issue.xmlSnippet}</textarea>
										<#if (issue.refferToHref)??>
											<div class="w-100"></div>
											<div>
											<br/>
												<p>Refer to for further help and assistance: <a href="${issue.refferToHref}">${issue.refferToHref}</a></p>
											</div>
										</#if>
									</div>
									<hr>
								 </#list>
							  </div>
							</div>
						</div>
					<#else>
                        <div class="row">
                            <div class="col">No issues found during migration.</div>
                        </div>	
					</#list>	
			  </div>
			</div>
		  </div>
		   <div class="card">
			<div class="card-header" id="headingThree" data-toggle="collapse" data-target="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
			  <h5 class="mb-0">
				<button class="btn btn-link collapsed mb-0" style="color:black" type="button" >
				 <b> Secure Properties issue count: ${securePropIssueCount} </b>
				</button>
			  </h5>
			</div>
			<div id="collapseThree" class="collapse" aria-labelledby="headingThree" data-parent="#accordionExample">
			  <div class="card-body">
					<#list securePropIssues as issue>
							<div class="row">
								<div class="col">Couldn't replace the property access keys, replace all of them by pre-pending with 'secure::' example : secure::http.port. Make sure you replace them in all the Mule XML files.</div>
								<div class="w-100"></div>
								<br/>
								<div class="col-2">secure properties file name</div>
								<textarea class="col" style="padding-top:10px;">${issue}</textarea>
							</div>
						<hr>
					<#else>
                        <div class="row">
                            <div class="col">No issues found during migration.</div>
                        </div>		
					</#list>
			  </div>
			</div>
		  </div>
		  <div class="card">
			<div class="card-header" id="headingThree" data-toggle="collapse" data-target="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
			  <h5 class="mb-0">
				<button class="btn btn-link collapsed mb-0" style="color:black" type="button" >
				 <b> Maven dependency migration issue count: ${dependencyIssueCount} </b>
				</button>
			  </h5>
			</div>
			<div id="collapseThree" class="collapse" aria-labelledby="headingThree" data-parent="#accordionExample">
			  <div class="card-body">
					<#list dependencyIssues as issue>
							<div class="row">
								<div class="col">Couldn't resolve the below maven dependency</div>
								<div class="w-100"></div>
								<br/>
								<div class="col-2">dependency snippet:</div>
								<textarea class="col" style="padding-top:10px;">${issue}</textarea>
							</div>
						<hr>
					<#else>
                        <div class="row">
                            <div class="col">No issues found during migration.</div>
                        </div>		
					</#list>
			  </div>
			</div>
		  </div>
		  
		  <div class="card">
			<div class="card-header" id="headingThree" data-toggle="collapse" data-target="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
			  <h5 class="mb-0">
				<button class="btn btn-link collapsed mb-0" style="color:black" type="button" >
				 <b> spring migration issue count: ${springIssueCount} </b>
				</button>
			  </h5>
			</div>
			<div id="collapseThree" class="collapse" aria-labelledby="headingThree" data-parent="#accordionExample">
			  <div class="card-body">
					<#list springIssues as issue>
							<div class="row">
								<div class="col">spring-config.xml present in src/main/resources, might require manual intervention</div>
								<div class="w-100"></div>
								<br/>
								
								<textarea class="col" style="padding-top:10px;">${issue}</textarea>
								<div class="w-100"></div>
								<div>
								<br/>
								 Refer to the following for further information and assistance <a href="https://whishworks.atlassian.net/wiki/spaces/MC/pages/1198817328/Migration+cheat+sheet#Migrationcheatsheet-importing-properties-file-to-spring-config"> Importing properties file to spring-config.xml </a></div>
							</div>
						<hr>
					<#else>
                        <div class="row">
                            <div class="col">No issues found during migration.</div>
                        </div>		
					</#list>
			  </div>
			</div>
		  </div>
		</div>
		</div>
	</body>
</html>