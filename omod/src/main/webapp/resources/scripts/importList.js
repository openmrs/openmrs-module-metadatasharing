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
 * DataTable variable.
 */
var $table = null ;

/**
 * Main function. It creates and initializes data table and buttons
 */
$j(document).ready(function() {
	$table = $j("#subscriptions").dataTable({
		"bJQueryUI" : true,
		"sPaginationType" : "full_numbers",
		"bPaginate": false,
		"bAutoWidth": false,
		"aaSorting": [[0,'desc']],
		"aoColumns": [ 
		            { "bVisible" : false }, // ID column is invisible
		            { "bUseRendered": false, "fnRender": subscriptionColumnRenderer, "sClass" : "subscriptionColumn" },
		            { "bUseRendered": false, "fnRender": statusColumnRenderer, "sClass" : "statusColumn" },
		  			{  "bUseRendered": false, "bSortable": false, "fnRender": unsubscribeColumnRenderer, "sClass" : "unsubscribeColumn" }
		  		],
		  "sDom": '<"fg-toolbar ui-corner-tl ui-corner-tr ui-helper-clearfix"lfr>t<"fg-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix"ip>'
	});
	$j("#subscriptions_wrapper").prepend('<span id="subscriptionsTitle">' + $messages.subscriptions + "</span>");
	
	$j("#manageSubscriptions").prepend('<input type="button" id="checkForUpdatesButton" class="toolbarButton" />');
	$j("#checkForUpdatesButton").click(checkForUpdatesListener).attr("value", $messages.checkForUpdates);
	
	$j("#manageSubscriptions").prepend('<input type="button" id="addSubscriptionButton" class="toolbarButton" />');
	$j("#addSubscriptionButton").click(importPackageListener).attr("value", $messages.importPackage);
	
	initTableData($subscriptions);
});

/**
 * Initializes data table.
 * 
 * @param $data - array containing data
 */
function initTableData($data) {
	for( var i = 0 ; i < $data.length ; i++ ) {
		$data[i].push(false); // we push one more value to array to fit
								// DataTable's requirements
							// we will use this value to determine whether
							// checking for updates process is pending
	}
	$table.fnAddData($data);
} 

// --------------- BUTTON LISTENERS

/**
 * Listener for "Import package" button
 */
function importPackageListener() {
	window.location = "upload.form";
}

/**
 * This function checks for updates for all subscriptions that are currently
 * visible to the user.
 */
function checkForUpdatesListener() {
	$j($table.fnSettings().aoData).each(function (){
		if($j(this.nTr).is(":visible")) {
			checkForUpdates(this._iId)
		}
	});	
}

/**
 * This function checks for updates for single subscription.
 * 
 * @param $sender - DOM node was clicked. It must be inside DataTable's row, which
 *            represents subscription.
 */
function checkNowListener($sender) {
	checkForUpdates(getRowNumber($sender));
}

/**
 * This is a function which listens to onClick event on "Unsubscribe" links. Asks
 * user if he really want to unsubscribe and redirects to unsubscribing page.
 * 
 * @param $sender - DOM node representing "Unsubscribe" link
 */
function unsubscribeListener($sender) {
	var $aData =$table.fnGetData(getRowNumber($sender));
	window.location = "toggleSubscribe.form?id=" + $aData[0].id;
}

function deleteListener($sender) {
	var $aData =$table.fnGetData(getRowNumber($sender));
	if (confirm("Are you sure you want to delete subscription to the '" +  $aData[1].name + "' package?")) {
		window.location = "deleteSubscribe.form?id=" + $aData[0].id;
	}
}

/**
 * This is a listener for "Install" or "Apply update" link. This link is shown when there is an
 * update available for subscription
 * 
 * @param $sender - DOM node was clicked. It must be inside DataTable's row, which
 *            represents subscription.
 */
function importListener($sender) {
	var $aData =$table.fnGetData(getRowNumber($sender));
	window.location = "download.form?id=" + $aData[0].id;
}

/**
 * This function shows back the entire URL (if it was shortened)
 * 
 * @param $sender - DOM node was clicked
 */
function unshortenUrlLinkListener($sender) {
	var $rowNumber = getRowNumber($sender);
	var $aData =$table.fnGetData($rowNumber);
	$aData[1].unshorten = true;
    $table.fnUpdate($aData, $rowNumber);
}

// END------------ BUTTON LISTENERS

/**
 * This function checks for updates for the subscription which lays in the given row.
 * It performs an AJAX call and refreshes table row
 * 
 * @param $rowNubmer the number of the row of subscription we want to check updates for
 */
function checkForUpdates($rowNumber) {
	var $aData = $table.fnGetData($rowNumber);
	if ($aData[0].status == 0) {
		return;
	}
	if( $aData[3] == false ) {
		$aData[3] = true; //start progress
		$table.fnUpdate($aData, $rowNumber);
		var $request = $j.getJSON("json/checkUpdates.form?id=" + $aData[0].id,
		        function($response){
			        if($response.subscriptionStatus.constructor == String) {
			        	$aData[2].status = parseInt($response.subscriptionStatus);
			        } else {
			        	$aData[2].status = $response.subscriptionStatus;
			        }
			        if($aData[2].status == 20 || $aData[2].status == 10) { //update available or up to date
				        $aData[1].name =  $j("<div/>").text($response.name).html(); 
				        $aData[2].remoteVersion = $response.remoteVersion;
				        $aData[1].imported = $response.imported;
				        $aData[1].version = $response.version;
			        }
			    	$aData[3] = false; //finish progress
			        $table.fnUpdate($aData, $rowNumber); 
	      });
		$request.error(function() { //error function. Shows fatal error and finshes progress
			$aData[2].status = 30;
	    	$aData[3] = false;
	        $table.fnUpdate($aData, $rowNumber);
		});
	}
}

/**
 * This function returns the row number where the $sender is.
 * 
 * @param $sender - DOM node inside DataTable's row
 * @returns row number
 */
function getRowNumber($sender){
	var $aPos = $table.fnGetPosition($j($sender).parents("td")[0]);
	return $aPos[0]; 
}

// --------------- COLUMN RENDERERS

/**
 * Renderer for "Unsubscribe" column. It simply creates a link to unsubscribing.
 * 
 * @returns {String} - html representation of cell content
 */
function unsubscribeColumnRenderer($data) {
	var $subscribe = $messages.unsubscribe;
	if ($data.aData[2].status == 0) {
		$subscribe = $messages.subscribe;
	}
	return "<a href='javascript:void(0);' onclick='unsubscribeListener(this);'>" + $subscribe + "</a> <a href='javascript:;' onclick='deleteListener(this);'/>Delete</a>";
}

/**
 * Renderer for "Status" column
 * 
 * @param $data - row data
 * @returns {String} - html representation of cell content
 */
function statusColumnRenderer($data) {
	var $ret = $j("<div/>");
	var $div = $j("<div/>");
	$ret.append( $div );
	if($data.aData[3] == true) { //checking for updates is pending
		$div.addClass("loading");
	} else {
		var $status = $data.aData[2].status;
		var $remoteVersion = $data.aData[2].remoteVersion;
		var $p = $j("<p/>");
		$div.append( $p );
		if($status >= 30) { // error
			$div.addClass( "statusError" ) ;
		} else if ($status >= 20 && $status <= 29) { // neutral
			$div.addClass( "statusNeutral" ) ;
		} else if ($status >= 10 && $status <= 19) { // good
			$div.addClass( "statusGood" ) ;
		}
	
		switch ($status) {
			case 0:
				$p.append($messages.unsubscribed);
			break;
			case 1: // not initialized
				$p.append($messages.neverChecked);
			break;
		
			case 10: // up to date
				$p.append($messages.upToDate);
			break;
		
			case 20: // update available
				var $linkText = null;
				if(!$data.aData[1].imported || $data.aData[1].version == null) {//never imported
					$p.append("Version" + " " + $remoteVersion + " " + $messages.available + " ");
					$linkText = "Install";
				} else {
					$p.append($messages.updateToVersion + " " + $remoteVersion + " " + $messages.available + " ");
					$linkText = "Apply update";
				}
				$p.append("<a href='javascript:void(0);' onclick='importListener(this);' style='font-weight:bold;'>" + $linkText + "</a>") ;
			break;
			
			case 31: // timeout
				$p.append($messages.timeout);
			break;
	
			case 32: // invalid subscription
				$p.append($messages.invalidSubscription);
			break;
	
			case 33: // service unavailable
				$p.append($messages.serviceUnavailable);
			break;
			
			case 34: // duplicate subscription
				$p.append($messages.duplicateSubscription);
			break;
	
			case 30: // fatal error is the default status
			default:
				$p.append($messages.fatalError);
				if(!$div.hasClass("statusError")) {
					$div.removeClass("statusNeutral") ; 
					$div.removeClass("statusGood") ; 
					$div.addClass("statusError");
				}
		}
		if ($status != 0) {
			$p.append( "<br /><small><a href='javascript:void(0);' onclick='checkNowListener(this);'>"+$messages.checkNow+"</a></small>" ) ;
		}
	}
	return $ret.html();
}

/**
 * Renderer for "Subscription" column. It shortens the URLs with specified pattern inside, because this pattern will be 
 * in almost all URLs and to show typical subscription URL we need ~1400 pixels width screen. Not everyone has that. 
 * (the shortened URL can be convinced back to normal any time)
 * 
 * @param $data - row data
 * @returns {String} - html representation of cell content
 */
function subscriptionColumnRenderer($data) {
	var $pattern = "ws/rest/metadatasharing/package"; //this pattern appears usually in URLs
	var $pos = -1;
	if($data.aData[1].unshorten == undefined) {
		$pos = $data.aData[1].url.search($pattern);
	}
	var $ret = $j("<p/>");
	if($pos != -1) {
		var $enlarge = $j("<a href='javascript:void(0);' onclick='unshortenUrlLinkListener(this);' title='" + 
				$messages.showEntire + "'>...</a>");
		$ret.append($data.aData[1].url.substring(0, $pos));
		$ret.append($enlarge);
		$ret.append($data.aData[1].url.substring($pos + $pattern.length));
	} else {
		$ret.append($data.aData[1].url);
	}
	if($data.aData[1].name != null) {
		$ret.append("<br />");
		var $small = $j("<small/>").append("<span style='float:left;'>" + $data.aData[1].name + "</span>");
		$ret.append($small);
		if($data.aData[1].imported && $data.aData[1].version != null) {
			$small.append("<span class='version'>" + $messages.latestImportedVersion + ": " + $data.aData[1].version + "</span>");
		} else {
			$small.append("<span class='version'>" + $messages.neverImported + "</span>");
		}
	}
	return $j("<div/>").append($ret).html();
}

// END------------ COLUMN RENDERERS

// --------------- COLUMN SORTING AND SEARCHING

/**
 * Function for detecting data type in column. We are using arrays in two
 * columns, so let's detect it. This detection will be used for sorting columns.
 */
jQuery.fn.dataTableExt.aTypes.push(
		function ($sData) {
			if($sData.constructor == Object) {
				if($sData.url != undefined) {
					return "subscription";
				} else if($sData.id != undefined){ 
					return "id";
				} else {
					return "status";
				}
			} else {
				return null;
			}
		}
	);

/**
 * This function compares two statuses. It computes operation <pre>x.status &lt; y.status</pre>
 * 
 * @param x - status to compare
 * @param y - status to compare
 * @returns {Boolean} - result of <pre>x.status &lt; y.status</pre>
 */
function compareStatus($x, $y) {
	if($x.status == $y.status) 
		return 0;
	else
		return ($x.status < $y.status) ? -1 : 1;
}

/**
 * This function compares two ID. It computes moves to top subscriptions with status "Update available"
 * 
 * @param x - ID to compare
 * @param y - ID to compare
 * @returns {Boolean} - result of <pre>x &lt; y</pre>
 */
function compareId($x, $y) {
	if($y.status == 20) {
		if($x.status == 20) {
			return ($x.id < $y.id) ? 1 : -1;
		} else {
			return -1;
		}
	}
	if($x.status == 20) return 1;
	if($x.id == $y.id) 
		return 0;
	else
		return ($x.id < $y.id) ? -1 : 1;
}

/**
 * We register function for sorting statuses in ascending order
 */
jQuery.fn.dataTableExt.oSort['status-asc'] = compareStatus;


/**
 * We register function for sorting statuses in descending order
 */
jQuery.fn.dataTableExt.oSort['status-desc'] = function($x, $y) {
		return -compareStatus($x, $y);
};

/**
 * This function compares two subscriptions. It computes operation <pre>x.name &lt; y.name</pre>.
 * 
 * @param x - pair to compare
 * @param y - pair to compare
 * @returns {Boolean} - result of <pre>x.name &lt; y.name</pre>
 */
function compareSubscription($x, $y) {
	if($x.name == $y.name) 
		return 0;
	else {
		if($x.name == null) 
			return -1;
		else if($y.name == null)
			return 1;
		else 
			return ($x.name.toLowerCase() < $y.name.toLowerCase()) ? -1 : 1;
	}
}

/**
 * We register a function for sorting IDs in ascending order
 */
jQuery.fn.dataTableExt.oSort['id-asc'] = compareId;


/**
 * We register a function for sorting IDs in descending order
 */
jQuery.fn.dataTableExt.oSort['id-desc'] = function($x, $y) {
		return -compareId($x, $y);
};

/**
 * We register a function for sorting subscriptions in ascending order
 */
jQuery.fn.dataTableExt.oSort['subscription-asc'] = compareSubscription;


/**
 * We register a function for sorting subscriptions in descending order
 */
jQuery.fn.dataTableExt.oSort['subscription-desc'] = function($x, $y) {
		return -compareSubscription($x, $y);
};

/**
 * We register a function for searching the subscriptions. It returns the name of subscription
 * (the search is performed on names)
 */
$j.fn.dataTableExt.ofnSearch["subscription"] = function($sData) {
	return $sData.name;
}

// END------------ COLUMN SORTING AND SEARCHING
