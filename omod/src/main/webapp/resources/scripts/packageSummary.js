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
 * The DataTables variable
 */
var $table = null;

/**
 * Initialization of package table and all other elements
 */
$j(document).ready(function() {
	$table = $j("#packages").dataTable({
		"bJQueryUI": true,
		"sPaginationType": "full_numbers",
		"bPaginate": false,
		"bSearchable": false, 
		"bAutoWidth": false,
		"aaData": $packages,
		"aaSorting": [[1,'desc']],
		"aoColumns": [ 
			            {"bVisible": false}, // ID column is invisible
			            {"sClass": "versionColumn", "bSortable": false},
			            {"sClass": "nameColumn", "bSortable": false},
			            {"sClass": "descriptionColumn", "bSortable": false},
			            {"sClass": "dateColumn", "bSortable": false},
			            {"sClass": "publishedColumn", "bVisible": $publishConfigured, "bUseRendered": false, "fnRender": publishedColumnRenderer, "bSortable": false},
			            {"sClass": "actionColumn", "fnRender": actionColumnRenderer, "bSortable": false}
			  		],
		 "sDom": 't'
	});

	$j("#latest").click(createNewVersionFromLatestVersion);

	$j("#scratch").click(createNewVersionFromScratch);
	
	$j("#downloadLatest").click(downloadLatestPackage);
	
	$j("#back").click(backListener);

	$j("#packages_wrapper").prepend('<span>' + $messages.packageVersions + '</span>'); 
	
	if($j(".unpublish").size() >= 1) { //at least one package is published
		initializeLinkTextBox(true) ;
		if($latestVersionPublished == false) {
			//Some version is published and the last one isn't. Let's give a warning.
			var $publish = " <a href='togglePublished.form?id=" + $packages[0][0] + "'>" + $messages.publish + "</a>";
			$j("#notPublishedWarning").show().html($messages.latestVersionNotPublished + $publish);
		}
	} else {
		initializeLinkTextBox(false);
	}
	$j(".delete").click(deleteIconListener);
	$j(".download").click(downloadIconListener);
	$j(".newVersion").click(newVersionIconListener);
	$j(".publish").each( function() {
		$j(this).click(togglePublishedIconListener);
	});
	$j(".unpublish").each( function() {
		$j(this).click(togglePublishedIconListener);
	});
});

/**
 * Initializes the text box where we will have a subscription URL
 * 
 * @param $enabled - should the box be enabled for copying? 
 */
function initializeLinkTextBox($enabled) {
	if(!$publishConfigured) {
		$j("#shareUrl input").attr("value", $messages.configure);
		$j("#shareUrl input").attr("disabled", "disabled").addClass("disabled");
	} else if($enabled == true && $publishUrl != null) {
		$j("#shareUrl input").focus(function() { 
			$j(this).select();
		}).click(function() {
			$j(this).select();
		}).mouseup(function(e) {
			e.preventDefault();
		});
		
		$j("#shareUrl input").attr("value", $publishUrl);
	} else {
		$j("#shareUrl input").attr("disabled", "disabled").addClass("disabled");
		var $inputValue = null;
		$inputValue = $messages.pleasePublish;
		$j("#shareUrl input").attr( "value", $inputValue);
	}
}

//--------------- BUTTON LISTENERS

function downloadLatestPackage() {
	var $id = $packages[0][0];
	window.location = "download.form?id=" + $id ;
}

/**
 * Returns to the manage packages page. 
 */
function backListener($sender) {
	window.location = "list.form";
}

/**
 * Creates new version from the latest version. The id of lastes package version is obtained 
 * from $packages variable
 */
function createNewVersionFromLatestVersion() {
	var $id = $packages[0][0];
	window.location = "upgrade.form?empty=false&id=" + $id ;
}

/**
 * Creates new version from scratch. It uses as parent package the latest version. 
 */
function createNewVersionFromScratch($sender) {
	var $id = $packages[0][0];
	window.location = "upgrade.form?empty=true&id=" + $id;
}

/**
 * Listener for "Delete" icon. Asks user for confirmation and then deletes the package. 
 */
function deleteIconListener($event) {
	var $aData = $table.fnGetData(getRowNumber($event.currentTarget));
	var $result = confirm($messages.confirmDelete + " " + $aData[1] + "?");
	if($result)
		window.location = "delete.form?id=" + $aData[0];
}

/**
 * Listener for "Download" icon. Starts the download process. 
 */
function downloadIconListener($event) {
	var $aData = $table.fnGetData(getRowNumber($event.currentTarget));
	window.location = "download.form?id=" + $aData[0]; 
}

/**
 * Creates new version from the version in which clicked icon lays
 * 
 * @param $event - the click event
 */
function newVersionIconListener($event) {
	var $aData = $table.fnGetData(getRowNumber($event.currentTarget));
	window.location = "upgrade.form?empty=false&id=" + $aData[0];
}

/**
 * Redirects to togglePublished page (that page will bounce to this after success) 
 */
function togglePublishedIconListener($event) {
	var $aData = $table.fnGetData(getRowNumber($event.currentTarget));
	window.location = "togglePublished.form?id=" + $aData[0];
}
//END------------ BUTTON LISTENERS

function getRowNumber($sender){
	var $aPos = $table.fnGetPosition($j($sender).parents("td")[0]);
	return $aPos[0]; 
}

//--------------- COLUMN RENDERERS
/**
 * The renderer for "Published" column. It informs whether the package is published and creates a link for changing the state
 *  
 * @returns {String} - html representation of the "Published" column
 */
function publishedColumnRenderer($data) {
	var $ret = $j("<div/>");
	var $div = $j("<div/>");
	$ret.append($div);
	if($data.aData[5] == true) {
		$ret.prepend("<span>Yes</span>");
		if($publishConfigured) {
			$div.addClass("unpublish iconButton").attr("title", $messages.unpublish);
		}
	} else {
		$ret.prepend("<span>No</span>");
		if($publishConfigured) {
			$div.addClass("publish iconButton").attr("title", $messages.publish);
		}
	}
	return $ret.html();
}

/**
 * The renderer for "Action" column. It creates three links for: deleting, downloadning, creating new version
 * 
 * @returns {String} - html representation of the actions
 */
function actionColumnRenderer() {
	var $ret = $j("<div/>");
	var $div = $j("<div/>");
	$ret.append($div);
	$div.append($j("<div/>").addClass("newVersion iconButton").attr("title", $messages.newVersionThisOne));
	$div.append($j("<div/>").addClass("download iconButton").attr("title", $messages.downloadZipped));
	$div.append($j("<div/>").addClass("delete iconButton").attr("title", $messages.deletePackage));
	return $ret.html();
}
//END------------ COLUMN RENDERERS