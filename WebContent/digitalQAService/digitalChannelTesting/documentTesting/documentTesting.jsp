<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Digiassure | Document Testing</title>

<link rel="stylesheet" href="../styles/css/bootstrap.css">
<link rel="stylesheet" href="../styles/css/main.css">
<link rel="stylesheet" href="../styles/css/digi.css">

<link
	href="https://fortawesome.github.io/Font-Awesome/assets/font-awesome/css/font-awesome.css"
	rel="stylesheet">
<script
	src="${pageContext.request.contextPath}/vendor/jquery/dist/jquery.js"></script>
<script
	src="${pageContext.request.contextPath}/vendor/angular/angular.js"></script>
<script
	src="${pageContext.request.contextPath}/vendor/angular-ui-router/release/angular-ui-router.js"></script>
<script
	src="${pageContext.request.contextPath}/vendor/angular-route/angular-route.js"></script>
<script
	src="${pageContext.request.contextPath}/vendor/angular-ui-bootstrap-bower/ui-bootstrap.js"></script>
<script
	src="${pageContext.request.contextPath}/vendor/angular-ui-bootstrap-bower/ui-bootstrap-tpls.js"
	type="text/javascript"></script>
<script
	src="${pageContext.request.contextPath}/vendor/bootstrap/dist/js/bootstrap.js"></script>
<script src="${pageContext.request.contextPath}/js/app/app.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>

<script type="text/javascript">
function validate(){
	var expected=document.getElementById("expectedDoc").value;
	var actual=document.getElementById("actualDoc").value;
	var result=true;
	if(!expected.match(/(.*\.pdf)$/)){
		document.getElementById("expectedError").innerHTML="*   Upload Expected File in PDF format only";
		result=false;
	}
	
	if(!actual.match(/(.*\.pdf)$/)){
		document.getElementById("actualError").innerHTML="*   Upload Actual File in PDF format only";
		result=false;
	}
	return result;
}

function validateBulk(){
	var expected=document.getElementById("eDoc").value;
	var actual=document.getElementById("aDoc").value;
	var result=true;
	if(!expected.match(/(.*\.pdf)$/)){
		document.getElementById("eError").innerHTML="*   Upload Expected File in PDF format only";
		result=false;
	}
	
	if(!actual.match(/(.*\.zip)$/)){
		document.getElementById("aError").innerHTML="*   Upload Actual File in zip format only";
		result=false;
	}
	return result;
}
</script>
</head>
<body>
	<div>
		<!-- Header and Navigation Bar -->
		<jsp:include page="../../../common/logo.jsp"></jsp:include>
		<div class="marginTop50px"></div>
		<div class="container">

			<div class="col-sm-2 col-md-2"></div>
			<div class="col-sm-8 col-md-8">
				<div class="marginTop15px"></div>
<div class="col-md-6">
				<div class="panel panel-info">
					<div class="panel-heading">
						<span class="panel-title"><strong>Document Testing</strong></span>
					</div>
					<div class="panel-body">
						<div class="marginTop15px"></div>
						<form class="form-horizontal" action=testDocument method="POST" onsubmit="return validate();"
							id="testDocument" title="testDocument"
							enctype="multipart/form-data">
							<div class="form-group">
								<label class="col-sm-3 control-label">Expected Document</label>
								<div class="col-sm-4">
									<input type="file" name="expectedDocument" id="expectedDoc" required=required />
								</div>
									<font color="red"><span id="expectedError"></span></font>
							</div>

							<div class="form-group">
								<label class="col-sm-3 control-label">Actual Document</label>
								<div class="col-sm-4">
									<input type="file" name="actualDocument" id="actualDoc" required=required />
								</div>
								<font color="red"><span id="actualError"></span></font>
							</div>

							<div class="marginTop50px"></div>
							<div class="form-group">
								<div class="col-sm-3 col-sm-offset-5">
									<input type="submit" class="btn btn-success btnCenter" 
										value="Compare" />
								</div>
							</div>
						</form>


					</div>
				</div>
				</div>
				<div class="col-md-6">
					<div class="panel panel-info">
					<div class="panel-heading">
						<span class="panel-title"><strong>Bulk Upload</strong></span>
					</div>
					<div class="panel-body">
						<div class="marginTop15px"></div>
						<form class="form-horizontal" action=testDocument method="POST"
							id="testDocument" title="testDocument" onsubmit="return validateBulk();"
							enctype="multipart/form-data">
							<div class="form-group">
								<label class="col-sm-3 control-label">Expected Document</label>
								<div class="col-sm-4">
									<input type="file" name="expectedDocument" id="eDoc" required=required />
								</div>
								<font color="red"><span id="eError"></span></font>
							</div>

							<div class="form-group">
								<label class="col-sm-3 control-label">Actual Document</label>
								<div class="col-sm-4">
									<input type="file" name="actualDocument" id="aDoc" required=required />
								</div>
								<font color="red"><span id="aError"></span></font>
							</div>

							<div class="marginTop50px"></div>
							<div class="form-group">
								<div class="col-sm-3 col-sm-offset-5">
									<input type="submit" class="btn btn-success btnCenter"
										value="Compare" />
								</div>
							</div>
						</form>


					</div>
				</div>
</div>
				<div>
				
				
						<c:if test="${not empty message}">
							<div class="alert alert-success alert-dismissible" role="alert">
								<button type="button" class="close" data-dismiss="alert">
									<span aria-hidden="true">×</span><span class="sr-only">Close</span>
								</button>
								<strong>Success!</strong> ${message}
								<script>
									speak('${message }')
								</script>
							</div>
							<br>
						</c:if>

						<c:if test="${not empty error}">
							<div class="alert alert-danger alert-dismissible" role="alert">
								<button type="button" class="close" data-dismiss="alert">
									<span aria-hidden="true">×</span><span class="sr-only">Close</span>
								</button>
								<strong>Error!</strong> ${error}
								<script>
									speak('${error }')
								</script>
							</div>
							<br>
						</c:if>
				<c:if test="${empty report}">
					<div class="alert alert-info alert-dismissible" role="alert">
								<button type="button" class="close" data-dismiss="alert">
									<span aria-hidden="true">×</span><span class="sr-only">Close</span>
								</button>
								<strong>Info!</strong> No Documents Found..!
							
							</div>
							<br>
				</c:if>
					<c:if test="${not empty report}">
						<table class="table table-striped table-bordered">
							<tr>
								<th>Expected Document</th>
								<th>Actual Document</th>
								<th>Creation Time</th>
								<th>Download Report</th>
								<th>Delete</th>
							</tr>
							<c:forEach items="${report }" var="r">
								<tr>
									<td>${r.basedocument}</td>
									<td>${r.actualdocument }</td>
									<td>${r.creationtime }</td>
									<td><a href="./downloadReport?report=${r.reportpath }"><button type="button"
												class="btn btn-primary">
												<span class="glyphicon glyphicon-download-alt"></span>
											</button> </a></td>
									
									<td><a
													href='./deleteReport?reportid=${r.comparisionreportid}'><button
															type="button" class="btn btn-danger">
															<span class="glyphicon glyphicon-trash"></span>
														</button></a></td>
								</tr>

							</c:forEach>
						</table>
					</c:if>
				</div>
			</div>


		</div>
	</div>

	<!--  footer  -->
	<div class="workInProgressMargin">
		<jsp:include page="../../../common/footer.jsp"></jsp:include>
	</div>
</body>
</html>