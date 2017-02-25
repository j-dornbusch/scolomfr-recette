var $execButton;
var running = false;
$(function() {
	$('#side-menu').metisMenu({
		toggle : true
	});

	$('table').on('draw.dt', function() {
		$(this).removeClass("hidden");

	});
	if (!Cookies.get('locale')) {
		Cookies.set('locale', "fr_FR", {
			path : '/'
		});
	}
	$('.dropdown-menu.dropdown-language li').on("click", function(e) {
		e.preventDefault();
		var newLocale = $(this).data("lang");
		Cookies.set('locale', newLocale, {
			path : '/'
		});
		location.reload();
	});
	$('table').DataTable({
		"pageLength" : 50,
		"columnDefs" : [ {
			"width" : "10%",
			"targets" : 0
		}, {
			"width" : "10%",
			"targets" : 1
		}, {
			"width" : "80%",
			"targets" : 2
		} ]
	});
	$('#side-menu').removeClass("hidden");
	$execButton = $("#testcase-exec-form").find("button");
	enableExecutionButton($execButton, true);
	$("#testcase-exec-form").on('submit', function(e) {
		if (running) {
			return;
		}
		running = true;
		e.preventDefault();
		enableExecutionButton($execButton, false);
		var data = $(this).serialize();
		data += "&" + getCsrfParameter() + "=" + getCsrfToken();
		$.ajax({
			url : $(this).attr('action'),
			type : $(this).attr('method'),
			data : data,
			dataType : "json",
			success : function(json) {

				displayResultArea(true);
				if ($messageTemplate) {
					$("#errors-area").empty();
					$("#infos-area").empty();
				}
				if (json.uri) {
					executionTrackingUri = json.uri;
					handleAsyncResponse()
				} else {
					refreshMessages(json);
					enableExecutionButton($execButton, true);
					running = false;
				}
			}
		});
		return false;
	});
	$("#errors-area").on("click", handleFalsePositiveClick);

});
function getCsrfParameter() {
	return $("meta[name='_csrf_parameter']").attr("content");
}
function getCsrfHeader() {
	return $("meta[name='_csrf_header']").attr("content");
}
function getCsrfToken() {
	return $("meta[name='_csrf']").attr("content");
}
var executionTrackingUri;
function handleAsyncResponse() {
	$.ajax({
		url : executionTrackingUri,
		dataType : "json",
		success : function(json) {
			refreshMessages(json);
			if (json.state != 'FINAL') {
				setTimeout(handleAsyncResponse, 500);
			} else {
				enableExecutionButton($execButton, true);
				running = false;
				if (extractErrorCount(json) == 0) {
					displayTestsModal($("#tests-modal-no-error-title").val(),
							$("#tests-modal-no-error-content").val());
				}

			}
		}
	});

}
var $messageTemplate;
function displayResultArea(bool) {
	bool ? $("section#result-area").removeClass("hidden") : $(
			"section#result-area").addClass("hidden");
}
function extractErrorCount(json) {
	var errorCount = (json.errorCount ? json.errorCount : 0);
	return errorCount;
}
function extractComplianceIndicator(json) {
	var complianceIndicator = (json.complianceIndicator ? json.complianceIndicator
			: -1);
	return complianceIndicator;
}
function refreshMessages(json) {
	if (!$messageTemplate) {
		$messageTemplate = $("#message-template").remove()
				.removeClass("hidden");
	}

	displayErrorCount(extractErrorCount(json));
	var indicator = extractComplianceIndicator(json);

	displayComplianceIndicator(indicator == -1 ? "hide" : Math
			.round(indicator * 10000) / 100);
	var errors = new Array();
	var infos = new Array();
	$(json.messages).each(function(i, e) {
		if (e.type == 'INFO') {
			infos.push(e);
		} else {
			errors.push(e);
		}
	})
	displayMessages("errors-area", errors)
	displayMessages("infos-area", infos)
}
var $errorCountIndicator;
var previousErrorCount;
function displayErrorCount(errorCount) {
	if (!$errorCountIndicator) {
		$errorCountIndicator = $("#error-count")
	}
	$errorCountIndicator.text(errorCount);
	if (previousErrorCount != errorCount) {
		$errorCountIndicator.removeClass().addClass("label").addClass(
				errorCount == 0 ? "label-success" : "label-danger");
	}
	previousErrorCount = errorCount;
}
var $complianceIndicatorContainer, $complianceIndicator;
function displayComplianceIndicator(complianceIndicator) {
	if (!$complianceIndicatorContainer) {
		$complianceIndicatorContainer = $("#compliance-indicator-container");
		$complianceIndicator = $("#compliance-indicator");
	}
	if (complianceIndicator == "hide") {
		$complianceIndicatorContainer.addClass("hidden");
	} else {
		$complianceIndicatorContainer.removeClass("hidden");
		$complianceIndicator.text(complianceIndicator);
	}
}
function displayMessages(areaId, messages) {
	$area = $("#" + areaId);
	var messageData, key, title, content, $messageBody;
	for ( var index in messages) {
		messageData = messages[index];
		if (!messageData.key) {
			continue;
		}
		key = messageData.key.replace(
				/([\!"#$%&'()*+,./:;<=>?@\[\\\]\^`\{|\}~])/g, "\\$1");
		console.log($("#" + key).length);
		if ($("#" + key).length > 0) {
			console.log("doublon " + key)
		}
		title = messageData.title ? messageData.title : "<empty>";
		content = messageData.content ? messageData.content : messageData;
		$messageBody = $messageTemplate.clone();
		$messageBody.prop("id", key);
		$messageBody.find("strong.title").text(title);
		$messageBody.find("span.content").html(content);
		updateMessageStyle($messageBody, messageData.type);
		$area.append($messageBody);
	}
}
function updateMessageStyle($messageBody, type) {
	$messageBody.removeClass();
	$messageBody.find("a").removeProp("disabled");
	var $falsePositive = $messageBody.find(".ignore-false-positive");
	var $truePositive = $messageBody.find(".restore-true-positive");
	var styleClass = "alert ";
	switch (type) {
	case 'INFO':
		styleClass += "alert-info";
		$truePositive.hide();
		$falsePositive.hide();
		break;
	case 'ERROR':
		styleClass += "alert-danger";
		$truePositive.hide();
		$falsePositive.show();
		break;
	case 'IGNORED':
		styleClass += "alert-warning";
		$truePositive.show();
		$falsePositive.hide();
		break;
	}
	$messageBody.addClass(styleClass);
}
var $testsModal;
function displayTestsModal(title, content) {
	if (!$testsModal) {
		$testsModal = $("#tests-modal");
	}
	$testsModal.find("h4").text(title);
	$testsModal.find(".modal-body").text(content);
	$testsModal.modal();
}

function enableExecutionButton($button, bool) {
	if (!bool) {
		$button.prop("disabled", "disabled").find("img").removeClass("hidden");
	} else {
		$button.removeAttr("disabled").find("img").addClass("hidden");
	}

}
function handleFalsePositiveClick(e) {
	var $target = $(e.target);
	var action = null;
	if ($target.hasClass("ignore-false-positive")) {
		action = "ignore-false-positive";
	} else if ($target.hasClass("restore-true-positive")) {
		action = "restore-true-positive";
	} else {
		return;
	}
	$target.prop("disabled", "disabled");
	e.preventDefault();
	var $message = $target.closest("div.alert");
	if (!$message.length) {
		return;
	}
	var key = $message.prop("id");
	key = key.replace(/\\/g, "");
	var contextPath = $("#context-path").val();
	$
			.ajax({
				url : contextPath + "/tests/" + action,
				type : 'POST',
				data : "key=" + key + "&" + getCsrfParameter() + "="
						+ getCsrfToken(),
				dataType : "json",
				success : function(json) {
					if (json.state == "ACCEPTED") {
						updateMessageStyle($message,
								action == "ignore-false-positive" ? "IGNORED"
										: "ERROR")
					}

				}
			})
}