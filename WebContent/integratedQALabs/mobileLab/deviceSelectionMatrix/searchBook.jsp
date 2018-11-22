<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search & Book</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/styles/css/bootstrap.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/styles/css/main.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/styles/css/digi.css">
<link
	href="https://fortawesome.github.io/Font-Awesome/assets/font-awesome/css/font-awesome.css"
	rel="stylesheet">

<%-- 
<script
	src="${pageContext.request.contextPath}/vendor/datetimepicker-master/jquery.datetimepicker.js"></script> --%>
<link
	href="${pageContext.request.contextPath}/vendor/jquery-ui-1.12.1.custom/jquery-ui.css"
	rel="stylesheet">
<script
	src="${pageContext.request.contextPath}/vendor/jquery/dist/jquery.js"></script>
<script
	src="${pageContext.request.contextPath}/vendor/jquery-ui-1.12.1.custom/jquery-ui.js"></script>
<%-- 	<script
	src="${pageContext.request.contextPath}/vendor/bootstrap-datepicker-master/js/bootstrap-datepicker.js"
	type="text/javascript"></script> --%>
<script
	src="${pageContext.request.contextPath}/vendor/angular/angular.js"></script>
<script
	src="${pageContext.request.contextPath}/vendor/angular-ui-router/release/angular-ui-router.js"></script>
<script
	src="${pageContext.request.contextPath}/vendor/angular-route/angular-route.js"></script>




<script
	src="${pageContext.request.contextPath}/vendor/bootstrap/dist/js/bootstrap.js"></script>
<script src="${pageContext.request.contextPath}/js/app/app.js"></script>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
<script type="text/javascript">
	//mdTemp = new Date();
	//maxiDate = new Date(mdTemp.setDate(mdTemp.getDate() + 7));
	function speak(msg) {
		var msg = new SpeechSynthesisUtterance(msg);
		window.speechSynthesis.speak(msg);
	}

	$(document).ready(function() {
		var todayDate = new Date().getDate();
		$("#startTime").datetimepicker({
			format : 'HH:mm'
		});
		$("#endTime").datetimepicker({
			format : 'HH:mm'
		});
		$("#startDate").datepicker({
			startDate : '1d',
			endDate : '+30d',
			format : 'yyyy-mm-dd'
		});
		$("#endDate").datepicker({
			startDate : '1d',
			endDate : '+30d',
			format : 'yyyy-mm-dd'
		});
	});
</script>
<script type="text/javascript">
	function bookDevice(deviceId, startDate) {
		$
				.ajax({
					type : "GET",
					url : "${pageContext.request.contextPath}/integratedQALabs/mobileLab/deviceSelectionMatrix/bookDevice",
					data : {
						"deviceid" : deviceId,
						"startDate" : startDate
					},
					contentType : "application/text; charset=utf-8",
					dataType : "text",
					success : function(data) {

						var table = document.getElementById('reservationTable');

						var rowLength = table.rows.length;
						for (var i = 0; i < rowLength; i += 1) {
							var row = table.rows[i];
							var cell = row.cells[2].innerHTML;
							if (cell.includes("bookDevice(")) {
								var cell = cell.split("','")[1];
								cell = cell.split("')")[0];
								if (cell == startDate) {
									row.cells[2].innerHTML = "<button type='button' class='btn btn-warning' disabled='disabled'>In Progress</button>";
								}

							}
						}
					},
					error : function(jqXHR, textStatus, errorThrown) {
						alert('error: ' + textStatus + ': ' + errorThrown);
					}
				});
	}

	function populateDevices(vendor) {
		var devices = ${devices};
		console.log(devices);
		$('#deviceDetail').children('option:not(:first)').remove();
		for (var i = 0; i < devices.length; i++) {
			if (devices[i].vendor == vendor) {

				var options = $("#deviceDetail");
				options.append($("<option />").val(devices[i].devicecatalogid)
						.text(devices[i].devicename));

			}
			/*  for (var key in obj){
			     var attrName = key;
			     var attrValue = obj[key];
			 } */
		}

	}
</script>

</head>
<body>
	<jsp:include page="../../../common/logo.jsp"></jsp:include>

	<!-- PAGE CONTENT WRAPPER -->
	<div class="page-content-wrap">

		<!-- START WIDGETS -->
		<div class="row">


			<div class="page-sidebar" style="height: 100%;">
				<div id="mCSB_1"
					class="mCustomScrollBox mCS-light mCSB_vertical mCSB_inside"
					tabindex="0">
					<div id="mCSB_1_container" class="mCSB_container"
						style="top: 0px; left: 0px;" dir="ltr">
									<!-- START X-NAVIGATION -->
						<ul class="x-navigation">




							<li class="xn-openable"><a href="#"><span
									class="fa fa-files-o"></span> <span class="xn-text">Lab</span></a>
								<ul>
								


									<li><a
										href="../../mobileLab/deviceSelectionMatrix/showLabDetails"><span
											class="fa fa-image"></span>Inventory</a></li>




									<c:if test="${sessionScope.rolename == 'ADMIN'}">
										<li><a
											href="../../mobileLab/deviceSelectionMatrix/addDeviceDeatils"><span
												class="fa fa-user"></span>Add Device</a></li>

										<li><a
											href="../../mobileLab/deviceSelectionMatrix/bulkDeviceDetails"><span
												class="fa fa-users"></span>Excel Import</a></li>

									</c:if>
									<li><a
										href="../../mobileLab/deviceSelectionMatrix/availabilityDetails"><span
											class="fa fa-comments"></span> Availability</a></li>

								</ul></li>

							<li class="xn-openable active"><a href="#"><span
									class="fa fa-file-text-o"></span> <span class="xn-text">Bookings</span></a>
								<ul>
									<li class="active"><a
										href="../../mobileLab/deviceSelectionMatrix/searchNBookPage">Search
											& Book</a></li>
									<c:if test="${sessionScope.rolename == 'ADMIN'}">
										<li><a
											href="../../mobileLab/deviceSelectionMatrix/adminShowDeviceRequests">Approve/Reject</a></li>
									</c:if>
									<c:if test="${sessionScope.rolename != 'ADMIN'}">
									<li><a
										href="../../mobileLab/deviceSelectionMatrix/requestedBookings">requested
											Bookings</a></li>
											</c:if>
									<li><a
										href="../../mobileLab/deviceSelectionMatrix/allRequests">All
											Requests</a></li>

								</ul></li>


							<li class="xn-openable"><a href="#"><span
									class="fa fa-cogs"></span> <span class="xn-text">Dashboard</span></a>
								<ul>

									<li><a
										href="../../mobileLab/deviceSelectionMatrix/dashboard"><span
											class="fa fa-random"></span> Dashboard View</a></li>
								</ul></li>
							<c:if test="${sessionScope.rolename == 'ADMIN'}">
								<li class="xn-openable"><a href="#"><span
										class="fa fa-pencil"></span> <span class="xn-text">Users</span></a>
									<ul>
										<li><a
											href="../../mobileLab/deviceSelectionMatrix/viewUser"><span
												class="fa fa-tasks"></span>View User</a></li>
										<li><a
											href="../../mobileLab/deviceSelectionMatrix/beforeAddUser"><span
												class="fa fa-user"></span>Add User</a></li>
									</ul></li>
								<li class="xn-openable"><a href="#"><span
										class="fa fa-pencil"></span> <span class="xn-text">Projects</span></a>
									<ul>
										<li><a
											href="../../mobileLab/deviceSelectionMatrix/viewProjects"><span
												class="fa fa-tasks"></span>View Projects</a></li>
										<li><a
											href="../../mobileLab/deviceSelectionMatrix/beforeAddProject"><span
												class="fa fa-user"></span>Add Project</a></li>
									</ul></li>

							</c:if>
							<li class="xn-openable"><a href="#"><span
									class="fa fa-table"></span> <span class="xn-text">Reports</span></a>
								<ul>
									<li><a href="../../mobileLab/deviceSelectionMatrix/report"><span
											class="fa fa-align-justify"></span>Usage report</a></li>

								</ul></li>

						</ul>
						<!-- END X-NAVIGATION -->
					</div>



					<div id="mCSB_1_scrollbar_vertical"
						class="mCSB_scrollTools mCSB_1_scrollbar mCS-light mCSB_scrollTools_vertical"
						style="display: block;">
						<div class="mCSB_draggerContainer">
							<div id="mCSB_1_dragger_vertical" class="mCSB_dragger"
								style="position: absolute; min-height: 30px; display: block; height: 113px; max-height: 286px; top: 0px;"
								oncontextmenu="return false;">
								<div class="mCSB_dragger_bar" style="line-height: 30px;"></div>
							</div>
							<div class="mCSB_draggerRail"></div>
						</div>
					</div>
				</div>
			</div>



			<div class="col-md-9">
				<div class="marginTop50px"></div>
				<!-- <div class="top-bar">
			<div class="font24px">
				<label>Search & Book</label>
			</div>

		</div> -->







				<div class="col-md-6 col-sm-6 col-xs-12">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">
								<strong>Search & Book</strong>
							</h3>


						</div>
						<div class="panel-body">

							<div class="form-group form-horizontal">
								<label class="col-sm-3 control-label">Host</label>
								<div class="col-sm-9">
									<c:if test="${empty vendor }">

										<select name="device" class="form-control" style="-webkit-appearance:menulist"
											onchange="return populateDevices(this.value)">
											<option>--SELECT--</option>
											<c:forEach var="vendor" items="${vendors }">
											
													<c:if test="${vendor =='Seetest' }">
							<option value=${vendor }>Cloud</option>
							</c:if>
							<c:if test="${vendor !='Seetest' }">
								<option value='${vendor }'>${vendor }</option>
								</c:if>
											</c:forEach>

										</select>

									</c:if>

									<c:if test="${not empty vendor }">

										<select name="device" class="form-control" style="-webkit-appearance:menulist"
											onchange="return populateDevices(this.value)">
											<option>--SELECT--</option>
											<c:forEach var="v" items="${vendors }">
												<c:if test="${v == vendor }">
													
														<c:if test="${v =='Seetest' }">
							<option value=${v } selected=selected>Cloud</option>
							</c:if>
							<c:if test="${v !='Seetest' }">
								<option value='${v }'selected=selected>${v }</option>
								</c:if>
													
													
													
													
												</c:if>
												<c:if test="${v != vendor }">
														<c:if test="${v =='Seetest' }">
							<option value=${v }>Cloud</option>
							</c:if>
							<c:if test="${v !='Seetest' }">
								<option value='${v }'>${v }</option>
								</c:if>
												</c:if>
											</c:forEach>

										</select>

									</c:if>




								</div>
							</div>
							<br> <br>
							<div class="form-group">


								<form action="searchNBook" class="form-horizontal">
									<div class="form-group">
										<label class="col-sm-3 control-label">Device</label>
										<div class="col-sm-9">
											<c:if test="${empty deviceid }">
												<select name="device" class="form-control" id="deviceDetail" style="-webkit-appearance:menulist">
													<option>--SELECT--</option>


												</select>
											</c:if>
											<c:if test="${not empty deviceid }">
												<select name="device" class="form-control" id="deviceDetail" style="-webkit-appearance:menulist">

													<option>--SELECT--</option>
													<c:forEach begin="0" end="${devices.size() -1}" var="index">
														<c:if
															test="${devices.get(index).getAsJsonObject().get('vendor').getAsString() == vendor }">
															<c:if
																test="${devices.get(index).getAsJsonObject().get('devicecatalogid').getAsString() ==  deviceid}">
																<option value="${deviceid}" selected=selected>${devices.get(index).getAsJsonObject().get('devicename').getAsString() }</option>
															</c:if>

															<c:if
																test="${devices.get(index).getAsJsonObject().get('devicecatalogid').getAsString() !=  deviceid}">
																<option
																	value="${devices.get(index).getAsJsonObject().get('devicecatalogid').getAsString()}">${devices.get(index).getAsJsonObject().get('devicename').getAsString() }</option>
															</c:if>
														</c:if>


													</c:forEach>
												</select>
											</c:if>
										</div>
									</div>

									<div class="form-group">
										<div class="col-sm-3">
										<label class=" control-label" style="float: right;">Start Date  <br>(yyyy-mm-dd)</label>
											
										</div>
										<div class="col-sm-3">

											<!-- <input type="text" class="form-control" name="startDate"
												id="startDate" /> -->
											<c:if test="${empty date }">

												<div class='input-group date' id='startDate'>
													<input type='text' class="form-control" name="startDate" />
													<span class="input-group-addon"> <span
														class="glyphicon glyphicon-calendar"></span>
													</span>
												</div>


											</c:if>
											<c:if test="${not empty date }">

												<div class='input-group date' id='startDate'>
													<input type='text' class="form-control" name="startDate"
														value="${date }" /> <span class="input-group-addon">
														<span class="glyphicon glyphicon-calendar"></span>
													</span>
												</div>


											</c:if>


										</div>
									
										<label class="col-sm-3 control-label">Start Time</label>
										<div class="col-sm-3">
											<!-- <input type="text" id="endDate" class="form-control"
												name="endDate"> -->

											<div class='input-group date' id='startTime'>
												<input type='text' class="form-control" name="startTime" />
												<span class="input-group-addon"> <span
													class="glyphicon glyphicon-calendar"></span>
												</span>
											</div>


												
										</div>
									</div>
									
												
									<div class="form-group">
										<div class="col-sm-3">
										<label class=" control-label" style="float: right;">End Date  <br>(yyyy-mm-dd)</label>
											
										</div>
										<div class="col-sm-3">

											<!-- <input type="text" class="form-control" name="startDate"
												id="startDate" /> -->

												<div class='input-group date' id='endDate'>
													<input type='text' class="form-control" name="endDate" />
													<span class="input-group-addon"> <span
														class="glyphicon glyphicon-calendar"></span>
													</span>
												</div>


											
											


										</div>
									
										<label class="col-sm-3 control-label">End Time</label>
										<div class="col-sm-3">
											<!-- <input type="text" id="endDate" class="form-control"
												name="endDate"> -->

											<div class='input-group date' id='endTime'>
												<input type="text" type='text' class="form-control"   name="endTime" id="endTime"/>
											 <span
								class="input-group-addon add-on"><span
								class="glyphicon glyphicon-calendar"></span></span>
											</div>


												
										</div>
									</div>
									
									
									
									


									<div class="container">
										<div class="row">
											<div class='col-sm-6'>
												<!--   <input type="text" id="datetimepicker1"> -->
											</div>

										</div>
									</div>


									<div class="marginTop50px"></div>

									<div class="form-group">
										<div class="col-sm-3 col-sm-offset-5">
											<input type="submit" value="Book"
												class="btn btn-success btnCenter"
												onsubmit="return validate()">
										</div>
									</div>

								</form>
							</div>
						</div>
					</div>
				</div>
				
				
				
				<div class="col-md-6 col-sm-6 col-xs-12">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title">
								<strong>Notes</strong>
							</h3>


						</div>
						<div class="panel-body">
						
						<ul>
						<li>Maximum booking hours for cloud devices is 3 hours</li>
						<li>Only one booking per device is allowed per day</li>
						
						</ul>
						</div>
						</div>
						</div>
						
						
				
				
				
				
				<c:if test="${ not empty reservationSuccess }">
					<div class="alert alert-success alert-dismissible" role="alert">
						<button type="button" class="close" data-dismiss="alert">
							<span aria-hidden="true">×</span><span class="sr-only">Close</span>
						</button>
						<strong>Success:</strong> ${reservationSuccess}
						<script>
							speak('${reservationSuccess }')
						</script>
					</div>
				</c:if>
				<c:if test="${ not empty reservationFailure }">
					<div class="alert alert-danger alert-dismissible" role="alert">
						<button type="button" class="close" data-dismiss="alert">
							<span aria-hidden="true">×</span><span class="sr-only">Close</span>
						</button>
						<strong>Failure:</strong> ${reservationFailure}
						<script>
							speak('${reservationFailure }')
						</script>
					</div>
				</c:if>


				<%-- 			<c:if test="${ not empty reservations }">
					<table class="table table-striped table-hover "
						id="reservationTable">
						<thead>
							<tr>
								<th>Device Name</th>
								<th>Date</th>
								<th>Book</th>

							</tr>
						</thead>
						<tbody>
							<c:forEach items="${reservations }" var="reservation">
								<c:if
									test="${reservation.approvalStatus != 'Declined' && reservation.approvalStatus != 'Cancelled' }">
									<tr>
										<td>${reservation.devicename }</td>
										<td>${reservation.starttime }</td>
										<c:if test="${reservation.approvalStatus == 'Available'}">
											<td><button
													onClick="return bookDevice('${reservation.devicecatalogid}','${reservation.starttime }')"
													class="btn btn-success active">Book</button></td>
										</c:if>
										<c:if test="${reservation.approvalStatus == 'Approved' }">
											<td>

												<button type="button" class="btn btn-danger"
													disabled="disabled">Unavailable</button>
											</td>
										</c:if>
										<c:if test="${reservation.approvalStatus == 'Requested' }">
											<td>

												<button type="button" class="btn btn-warning"
													disabled="disabled">In Progress</button>
											</td>
										</c:if>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</c:if> --%>
			</div>
		</div>
		<!-- Footer -->

		<jsp:include page="../../../common/footer.jsp"></jsp:include>
	</div>
	</div>
	</div>
	<!--     <div ng-include="'common/footer.html'" a></div> -->




	<!-- START SCRIPTS -->
	<!-- START PLUGINS -->
	<%--  <script type="text/javascript" src="${pageContext.request.contextPath}/styles/js/plugins/jquery/jquery.min.js"></script>  --%>
	<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/plugins/jquery/jquery-ui.min.js"></script> --%>
	<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/plugins/bootstrap/bootstrap.min.js"></script>    --%>
	<!-- END PLUGINS -->
	<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.slimscroll.min.js"></script>
       <script type="text/javascript" src="${pageContext.request.contextPath}//styles/js/jquery.slimscroll.js"></script> --%>
	<!-- THIS PAGE PLUGINS -->
	<script type='text/javascript'
		src='${pageContext.request.contextPath}/js/plugins/icheck/icheck.min.js'></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/js/plugins/mcustomscrollbar/jquery.mCustomScrollbar.min.js"></script>

	<script type="text/javascript"
		src="${pageContext.request.contextPath}/js/plugins/datatables/jquery.dataTables.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/js/plugins/tableexport/tableExport.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/js/plugins/tableexport/jquery.base64.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/js/plugins/tableexport/html2canvas.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/js/plugins/tableexport/jspdf/libs/sprintf.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/js/plugins/tableexport/jspdf/jspdf.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/js/plugins/tableexport/jspdf/libs/base64.js"></script>
	<!-- END PAGE PLUGINS -->

	<!-- START TEMPLATE -->
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/js/settings.js"></script>

	<script type="text/javascript"
		src="${pageContext.request.contextPath}/js/plugins.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/js/actions.js"></script>
	<!-- END TEMPLATE -->
	<!-- END SCRIPTS -->
</body>

</html>