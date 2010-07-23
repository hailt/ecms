﻿﻿﻿/*
Copyright (c) 2003-2010, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.dialog.add( 'insertPortalLink', function( editor )
{
	var curInstance = editor.name;
	return {
		title : 'InsertPortalLink',
		minWidth : 390,
		minHeight : 230,
		contents : [
			{
				id : 'tab1',
				label : '',
				title : '',
				expand : true,
				padding : 0,
				elements :
				[
					{
						type : 'html',
						html :
							'<style type="text/css">' +
									'.ck_about_link .UIFormGrid {'+
										'margin:auto;'+
										'width:auto;'+
									'}'+

									'.ck_about_link .FieldLabel {'+
										'padding:4px;'+
									'}'+
	
									'.ck_about_link .FieldComponent {'+
										'padding:4px;'+
									'}'+

									'.ck_about_link .FieldComponent input {'+
										'width:230px;'+
										'background: white;'+
										'border: 1px solid #c7c7c7;'+
										'color: black;'+
									'}'+

									'.ck_about_link .UIFormGrid .FieldComponent .UIAction {'+
										'padding: 0px;'+
									'}'+

									'.ck_about_link .UIAction {'+
										'padding: 8px 0px;'+
									'}'+

									'.ck_about_link .UIAction .ActionContainer {'+
										'margin:auto;'+
										'width:auto;'+
									'}'+

									'.ck_about_link .UIFormGrid .FieldComponent .UIAction .ActionButton {'+
										'display:block;'+
										'float:none;'+
										'margin:0 3px;'+
										'cursor: pointer;'+
									'}'+

									'.ck_about_link .UIAction .ActionButton {'+
										'display:block;'+
										'float:left;'+
										'margin:0 3px;'+
										'cursor: pointer;'+
									'}'+

									'.ck_about_link .UIAction .LightBlueStyle .ButtonLeft {'+
										'background:url("/ecm-wcm-extension/fckeditor/exo/plugins/insertPortalLink/skin/background/LightBlueStyle.gif") no-repeat scroll left top transparent;'+
										'padding:0 0 0 3px;'+
									'}'+

									'.ck_about_link .UIAction .LightBlueStyle .ButtonRight {'+
										'background:url("/ecm-wcm-extension/fckeditor/exo/plugins/insertPortalLink/skin/background/LightBlueStyle.gif") no-repeat scroll right top transparent;'+
										'padding:0 3px 0 0;'+
									'}'+

									'.ck_about_link .UIAction .LightBlueStyle .ButtonMiddle {'+
										'background:url("/ecm-wcm-extension/fckeditor/exo/plugins/insertPortalLink/skin/background/LightBlueStyle.gif") repeat-x scroll center bottom transparent;'+
										'line-height:22px;'+
										'padding:0 12px;'+
										'text-align:center;'+
										'white-space:nowrap;'+
									'}'+

									'.ck_about_link .UIAction .LightBlueStyle .ButtonMiddle {'+
										'line-height:22px;'+
										'text-align:center;'+
										'white-space:nowrap;'+
									'}'+
							'</style>' +
							'<script type="text/javascript">'+
								'function getPortalLink() {'+
									'var width = 800;'+
									'var height = 500;'+
									'var sOptions = "toolbar=no,status=no,resizable=yes,dependent=yes,scrollbars=yes" ;'+
									'sOptions += ",width=" + width ;'+
									'sOptions += ",height=" + height ;'+
									'var newWindow = window.open( "'+CKEDITOR.eXoPath+'eXoPlugins/insertPortalLink/insertPortalLink.html?type=PortalLink", "WCMInsertPortalLink", sOptions );'+
									'newWindow.focus();'+
								'}'+
								
								'function previewLink() {'+
									'var sOptions = "toolbar=no, status=no, resizable=yes, dependent=yes, scrollbars=yes";'+
									'sOptions += ", width=" +  800;' +
									'sOptions += ", height=" + 500;' +
									'var url = document.getElementById("txtUrl").value;' +
									'if (url) window.open(url, "", sOptions);' +	
								'}'+		
								
								'function addURL() {'+
									'var url = document.getElementById("txtUrl").value;'+
									'if (url == "") {'+
										'alert("Field URL is not empty");'+ 
										'return;'+
									'}'+
									'var newTag = CKEDITOR.document.createElement("a");'+
									'newTag.setHtml(url);'+
									'newTag.setAttribute("href", url);'+
									'CKEDITOR.instances["'+curInstance+'"].insertElement(newTag);'+
									'CKEDITOR.dialog.getCurrent().hide();'+
								'}'+
							'</script>'+
							'<div class="cke_about_container">' +
								'<div class="ck_about_link">'+

								'<table class="UIFormGrid">' +
									'<tbody>' +
										'<tr>' +
											'<td class="FieldLabel">'+
												'<label fcklang="WCMInsertPortalLinkInputTitle">Title: </label>'+
											'</td>'+
											'<td colspan="2" class="FieldComponent">'+
												'<input type="text" id="inputTitle">'+
											'</td>'+
										'</tr>'+
										'<tr>'+
											'<td class="FieldLabel">'+
												'<label>URL: </label>'+
											'</td>'+
											'<td class="FieldComponent">'+
												'<input type="text" id="txtUrl">'+
											'</td>'+
											'<td class="FieldComponent">'+
												'<div class="UIAction">'+
													'<table align="center" class="ActionContainer">'+
														'<tbody>'+
															'<tr>'+
																'<td align="center">'+
																	'<div class="ActionButton LightBlueStyle" onclick="getPortalLink();">'+
																		'<div class="ButtonLeft">'+
																			'<div class="ButtonRight">'+
																				'<div class="ButtonMiddle">'+
																					'<label>'+
																						'<a xmlns="http://www.w3.org/1999/xhtml">'+
																							'Get portal link'+
																						'</a>'+
																					'</label>'+
																				'</div>'+
																			'</div>'+
																		'</div>'+
																	'</div>'+
																'</td>'+
															'</tr>'+
														'</tbody>'+
													'</table>'+
												'</div>'+
											'</td>'+
										'</tr>'+
		  						'</tbody>'+
								'</table>'+
								
								'<div class="UIAction">'+
					  			'<table align="center" class="ActionContainer">'+
					    			'<tbody>'+
											'<tr>'+
					      				'<td align="center">'+
					        				'<div class="ActionButton LightBlueStyle" onclick="previewLink();">'+
					          					'<div class="ButtonLeft">'+
					            					'<div class="ButtonRight">'+
					              						'<div class="ButtonMiddle">'+
					              							'<label >'+
																				'<a xmlns="http://www.w3.org/1999/xhtml">'+
																					'Preview'+
																				'</a>'+
																			'</label>'+
					              						'</div>'+
					            					'</div>'+
					          					'</div>'+
					        				'</div>'+
					        				'<div class="ActionButton LightBlueStyle" onclick="addURL();">'+
					          					'<div class="ButtonLeft">'+
					            					'<div class="ButtonRight">'+
					              						'<div class="ButtonMiddle">'+
					                						'<label fcklang="WCMInsertPortalLinkButtonSave">'+
																				'<a xmlns="http://www.w3.org/1999/xhtml">'+
																					'Save'+
																				'</a>'+
																			'</label>'+
					              						'</div>'+
					            					'</div>'+
					          					'</div>'+
					        				'</div>'+
					      				'</td>'+
					    				'</tr>'+
					  				'</tbody>'+
									'</table>'+
	    					'</div>'+
							'</div>'+
						'</div>'
					}
				]
			}
		],
		buttons : [ CKEDITOR.dialog.cancelButton ]
		};
} );