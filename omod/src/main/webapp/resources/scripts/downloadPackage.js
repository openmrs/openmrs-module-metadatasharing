/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */

var $j = jQuery.noConflict();

/**
 * Main function. Sets default behaviors on AJAX requests, initializes buttons
 * and stats the download process. 
 */
$j(document).ready(function() { 
	$j.ajaxSetup({
		error: function($response) { 
			showFatalError($response); 
	    }, 
	    dataType : "json"
	}) ;
	$j("#backButton").click(function(){
		window.location = "list.form";
	});
	
	$j("#toggleStackTrace").click(function(){
		if( $j("#errorStackTrace").is(":visible") ) {
			$j("#toggleStackTrace").text($messages.showStackTrace);
			$j("#errorStackTrace").hide();
		} else {
			$j("#toggleStackTrace").text($messages.hideStackTrace);
			$j("#errorStackTrace").show();
		}
	});
	initializeConnection();
});

/**
 * Initializes connection to the remote server. The response of an AJAX request is a SubscritpionStatus value. 
 */
function initializeConnection() {
	$j("#downloadStatusBar").text($messages.connecting);
	$j.ajax({
		  url: "json/initializeDownload.form",
		  success: function($response){
				if($response < 30) {
					downloadPackage();
				} else {
					showFatalError($response);
				}
		  }
		});
}

/**
 * Performs download package AJAX call 
 */
function downloadPackage() {
	$j("#downloadStatusBar").text($messages.downloading);
	$j.ajax({ 
		url: "json/performDownload.form",
		success: function($response) {
			if($response < 30) {
				window.location = "validate.form";
			} else {
				showFatalError($response);
			}
        }
	});
}

/**
 * This function is called when any error occurs. It tries to provide some 
 * information about the error to the user
 * 
 * @param $response the AJAX response object or SubscriptionStatus value
 */
function showFatalError($response){
	$j("#downloadStatusBar").hide();
	$j("#downloadErrorBar").show();
	$j("#backButton").show();
	if($response.responseText != undefined){ //let's show the stack trace
		$j("#toggleStackTrace").show();
		$j("#downloadErrorBar").text($messages.fatalError);
		console.dir( $j($response.responseText).find("#stackTrace"));
		$j("#errorStackTrace").append($j($response.responseText).find("#stackTrace").html());
	} else { //it's SubscriptionStatus
		switch ($response) {		 
			case 31: // timeout
				$j("#downloadErrorBar").text($messages.timeout);
			break;
	
			case 32: // invalid subscription
				$j("#downloadErrorBar").text($messages.invalidSubscription);
			break;
	
			case 33: // service unavailable
				$j("#downloadErrorBar").text($messages.serviceUnavailable);
			break;
	
			case 30: // fatal error
			default:
				$j("#downloadErrorBar").text($messages.fatalError);
		}
	}
}