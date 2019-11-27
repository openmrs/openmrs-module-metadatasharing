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
 * Main function - initializes DataTables and buttons.
 */
$j(document).ready(function() {
	$j("#metadataPackages").dataTable({
		"bJQueryUI": true,
		"sPaginationType": "full_numbers",
		"bPaginate": true,
		"bAutoWidth": false,
		"bLengthChange": false,
		"iDisplayLength": 25,
		"aoColumns": [ 
			            {"bVisible": false}, // ID column is invisible
			            {"bUseRendered": false, "fnRender": nameColumnRenderer, "sClass": "nameColumn"},
			            {"sClass": "descriptionColumn"},
			  			{"sClass": "dateColumn", "fnRender": modificationDateColumnRenderer}
			  		],
		"aaData": $packages,
		"sDom":'<"fg-toolbar ui-corner-tl ui-corner-tr ui-helper-clearfix"lfr>t<"fg-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix"ip>'
	});
	
	$j("#metadataPackages_wrapper").prepend('<input type="button" id="createPackageButton" class="toolbarButton" />');
	$j("#createPackageButton").attr("value", $messages.createPackage).click(createPackageListener);
});


//--------------- BUTTON LISTENERS
function createPackageListener() {
	window.location = 'create.form';
}
//END------------ BUTTON LISTENERS

//--------------- COLUMN RENDERERS

/**
 * The renderer for "Name" column. Creates a link to the package summary and provides some information about published versions.
 * It also handles "old" packages (without version and group).
 * 
 * @returns {String} - html representation of cell content
 */
function nameColumnRenderer($data){
	var $ret = $j("<div/>");
	var $p = $j("<p/>");
	$ret.append( $p );
	var $name = null;
	var $nameData = $data.aData[1];
	if ($nameData.latestVersion != null && $nameData.latestVersion != 0) { //normal package
		$name = $j("<a/>").text($nameData.name).attr("href", "details.form?group="+$nameData.groupUuid);
		$p.append($name);
		if($publishConfigured) {
			var $publishedVersions = $nameData.publishedVersions;
			var $latestPublishedVersion = ($publishedVersions == null ? 0 : $publishedVersions.pop());

			var $txt = $messages.latestVersion + ": " + $nameData.latestVersion;
			$txt += ", " + $messages.latestPublishedVersion + ": ";
			$txt += ($latestPublishedVersion === 0 ? $messages.notPublished : $latestPublishedVersion);

			if($latestPublishedVersion < $nameData.latestVersion) {
				if ($publishConfigured) {
					var $publish = " <a href='togglePublished.form?returnTo=main&id=" + $nameData.id + "'>Publish</a>";
					$txt += "<span class='publishWarning warning'>" + $messages.warning + ": " + $messages.latestVersionNotPublished +
						$publish + "</span>";
				}
			}
			$p.append("<small>" + $txt + "</small>");
		}
	}
	else { //old package type
		$name = $j("<span/>").text($nameData.name);
		$p.append($name);
		var $download = "<a href='download.form?id=" + $nameData.id + "'>" + $messages.downloadPackage + "</a>";
		var $delete = "<a href='delete.form?id=" + $nameData.id + "'>" + $messages.deletePackage + "</a>";
		$p.append("<small>" + $messages.oldPackage + " " + $download + " " + $delete  + "</small>");
	}
	return $ret.html();
}

/**
 * The renderer for "Modification date" column. It removes information about seconds and miliseconds from the date.
 *  
 * @returns {String} - html representation of the date
 */
function modificationDateColumnRenderer($data){
	if($data.aData[3].length == 21) { //it's regular date format
		return $data.aData[3].substring(0, $data.aData[3].length-5);
	} else { //different date format - do not touch it
		return $data.aData[3];
	}
}

//END------------ COLUMN RENDERERS


//--------------- COLUMN SORTING AND SEARCHING

/**
 * Compares two names 
 * 
 * @returns {Boolean} - result of <pre>strcmp(x.name.toLowerCase(), y.name.toLoweCase())</pre>
 */
function compareName($x, $y) {
	if($x.name == $y.name) 
		return 0;
	else
		return ($x.name.toLowerCase() < $y.name.toLowerCase()) ? -1 : 1;
}

/**
 * We register function for sorting names in ascending order
 */
jQuery.fn.dataTableExt.oSort['name-asc'] = compareName;

/**
 * We register function for sorting names in descending order
 */
jQuery.fn.dataTableExt.oSort['name-desc'] = function($x, $y) {
		return -compareName($x, $y);
};

/**
 * We register function for detecting "Name" column object
 */
jQuery.fn.dataTableExt.aTypes.push(
		function ($sData) {
			if($sData.constructor == Object) {
				if($sData.name != undefined) {
					return "name";
				}
			}
			return null;
		}
	);

/**
 * We register function for seraching in "Name" column
 */
$j.fn.dataTableExt.ofnSearch["name"] = function($sData) {
	return $sData.name;
}

//END------------ COLUMN SORTING AND SEARCHING
