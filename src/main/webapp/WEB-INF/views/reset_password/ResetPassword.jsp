<!DOCTYPE html>
<html lang="en">
<head>
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/jquery-ui-bootstrap/0.5pre/assets/css/bootstrap.min.css"
	rel="stylesheet" type="text/css" />
<script type="text/javascript"
	src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.0/js/bootstrap.min.js"></script>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script type="text/javascript" th:inline="javascript">
$(document).ready(function(){
	var id=[[${id}]];
	var uEmail=[[${email}]];
	
	$("#afterChange").hide();
	var path ="/StartupHub/api/"+id+"/updatePassword";
	$("#btn").click(function(){
	if($("#password").val() != "" || $("#newPassword").val() != ""){
	
		if($("#password").val()==$("#newPassword").val()){
			var jsonObj={
			        password: $("#password").val(),
			        key: uEmail
			};
			$.ajax({
	 			url: path,
	 			type: "POST",
				contentType: "application/json", // send as JSON
		   		data: JSON.stringify(jsonObj),
	
				complete: function() {
				  
				},
				
				success: function() {
					 $("#afterChange").show();
					   $("#beforeChange").hide();
				 },
				
				error: function() {
					$("#afterFail").show();
					 $("#beforeChange").hide();
				 },
			});
		}else{
			alert("Password and confirm Password must be same");
		}

		}
		else{
			alert("All fields are mandatory");
		}
	});	 
});

</script>

<style>
.box{
    width: 400px;
    border-width: 10px 5px 5px 5px;
    border-color : #1B99A7;
    border-style: solid;
    margin:25px;

}

</style>
</head>
<body>
	<div id="beforeChange" class="row" align="center">
		<div style="float: inherit;" class="col-md-8">
			<div class="panel panel-default">
				<div class="box panel-body">
	<div style="background-color:#1B99A7;height:30px;">
			<div class="row">
				<span style="font-size:24px;color:white">Startup Hub</span>
			</div>
		</div>					
<div class="text-center">
						<h3>
							<i class="fa fa-lock fa-4x"></i>
						</h3>
						<h2 class="text-center" style="color: #1B99A7;">Reset Password</h2>
						<p style="color: #1B99A7;">Enter password for Startup Hub.</p>
						<div class="panel-body">
							<form id="register-form" role="form" autocomplete="off"
								class="form" method="post">
								<div class="form-group">
									<div class="input-group" style="margin-bottom: 20px;">
										<span class="input-group-addon"><i class="fa fa-key"></i></span>
										<input id="password" placeholder="New Password"
											class="form-control" type="password">
									</div>
									<div class="input-group">
										<span class="input-group-addon"><i class="fa fa-key"></i></span>
										<input id="newPassword" placeholder="Confirm Password"
											class="form-control" type="password">
									</div>

								</div>
								<div>
									<input id="btn" class="btn btn-lg btn-primary"
										value="Reset Password">
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="afterChange" style="display: none" align="center" class="row"
		ng-show="!resetpass">
		<div style="float: inherit;" class="col-md-8">
			<div class="panel panel-default">
				<div class="panel-body">
					<div class="text-center">
						<h3>
							<i class="fa fa-thumbs-o-up fa-4x"></i>
						</h3>
						<h2 class="text-center">Success</h2>
						<div class="panel-body">
							<p>Your password has been changes successfully.</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div id="afterFail" style="display: none" align="center" class="row"
		ng-show="!resetpass">
		<div style="float: inherit;" class="col-md-8">
			<div class="panel panel-default">
				<div class="panel-body">
					<div class="text-center">
						<h3>
							<i class="fa fa-thumbs-o-up fa-4x"></i>
						</h3>
						<h2 class="text-center">Error</h2>
						<div class="panel-body">
							<p>There might be some error while updating password. Please try after sometime</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


</body>

</html>
