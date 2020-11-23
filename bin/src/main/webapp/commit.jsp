<%@ include file="common/header.jspf"%>
<%@ include file="common/navigation.jspf"%>
 
<div class="container">
	<div class="row">
		<div class="col-md-6 col-md-offset-3 ">
			<div class="panel panel-primary">
				<div class="panel-heading">GitHub Details</div>
				<div class="panel-body">
					<form:form method="post" modelAttribute="git">
						<form:hidden path="id" />
						<fieldset class="form-group">
							<label for="repoURL">RepoURL</label> <input
								type="text" id="pass" name="repoURL" class="form-control" required="required">
							<form:errors path="repoURL" cssClass="text-warning" />
						</fieldset>
						<fieldset class="form-group">
							<form:label path="username">Username</form:label>
							<form:input path="username" type="text" class="form-control"
								required="required" />
							<form:errors path="username" cssClass="text-warning" />
						</fieldset>

						<fieldset class="form-group">
							<label for="password">Password </label> <input
								type="password" id="pass" name="password" class="form-control" required="required">
							<form:errors path="password" cssClass="text-warning" />
						</fieldset>
						<fieldset class="form-group">
							<label for="message">Commit message </label> <input
								type="text" id="message" name="message" class="form-control" required="required">
							<form:errors path="message" cssClass="text-warning" />
						</fieldset>
						<button type="submit" class="btn btn-success"> Perform Commit</button>
					</form:form>
				</div>
			</div>
		</div>
	</div>
</div>
<%@ include file="common/footer.jspf"%>