<%@page contentType="text/html" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set value="/resources" var="baseResourcesPath" />
<t:layout>
	<jsp:body>
	
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">Test ${testCaseIndex} | ${folderLabel} / ${testCaseLabel}  </h1>
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->
			<div class="row">
				<c:choose>
				<c:when test="${implemented}">
					<div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            Test implémenté : ${implementation}
                        </div>
                        <div class="panel-body">
                            <div class="row">
                                    <form role="form" method="POST"
										action="." id="testcase-exec-form">
                                      
                                        <c:forEach items="${parameters}"
											var="parameter">
						<t:parameters-switch tagname="${parameter}" />
						</c:forEach>
                                        <div class="col-lg-12">
											<button class="btn btn-default" type="submit">Exécuter <img
													alt="Patientez" title="Patientez" class="hidden"
													src='<spring:url value="${baseResourcesPath}/img/ajax-loader.gif"></spring:url>'>
											</button>
										</div>
										
                                        
                                    </form>
                                
                                
                            </div>
                            <!-- /.row (nested) -->
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
				
				</c:when>
				<c:otherwise>
					<span class="label label-default">Test non implémenté : </span> </c:otherwise>
				</c:choose>
				
			</div>
			<!-- /.row -->
			<section id="result-area" class="hidden">
	        <div class="row">
                <div class="col-lg-12">
                    <h1 class="page-header">Résultat</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="col-lg-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            Erreurs
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body" id="errors-area">
                            <div class="alert alert-danger hidden"
								id="error-message-template">
                                <strong class="title"></strong> : 
                                <span class="content">Lorem ipsum dolor sit amet, consectetur adipisicing elit.</span> <a
									href="#" class="alert-link hidden"></a>.
                            
							</div>
                        </div>
                        <!-- .panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-6 -->
                <div class="col-lg-6">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            Informations
                        </div>
                        <!-- /.panel-heading -->
                        <div class="panel-body" id="infos-area">
                            <div class="alert alert-info hidden"
								id="info-message-template">
								<strong class="title"></strong> : 
                                <span class="content">Lorem ipsum dolor sit amet, consectetur adipisicing elit.</span> <a
									href="#" class="alert-link hidden"></a>.
                            
							</div>
                        </div>
                        <!-- .panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-6 -->
                
            </div>
            <!-- /.row -->
        </section>
    </jsp:body>
</t:layout>






<



