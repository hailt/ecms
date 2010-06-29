/*
	eXo config plugins
*/

CKEDITOR.eXoPath = CKEDITOR.basePath.substr(0, CKEDITOR.basePath.indexOf("ckeditor/"));

// config to add custom plugin	
(function() {CKEDITOR.plugins.addExternal('helloworld',CKEDITOR.eXoPath+'eXoPlugins/helloworld/', 'plugin.js');})();
(function() {CKEDITOR.plugins.addExternal('content',CKEDITOR.eXoPath+'eXoPlugins/content/','plugin.js');})();
(function() {CKEDITOR.plugins.addExternal('insertGadget',CKEDITOR.eXoPath+'eXoPlugins/insertGadget/','plugin.js');})();
(function() {CKEDITOR.plugins.addExternal('insertPortalLink',CKEDITOR.eXoPath+'eXoPlugins/insertPortalLink/','plugin.js');})();

CKEDITOR.editorConfig = function( config ){
	config.extraPlugins = 'helloworld,content,insertGadget,insertPortalLink';
//	config.toolbar_Basic.push(['helloworld.btn','-','content.btn','-','insertPortalLink']);	
	config.toolbar_Basic = 
	[
		['Source','-','Bold','Italic','-','NumberedList', 'BulletedList', '-', 'Link','Unlink'],  
		['helloworld.btn','-','content.btn','-','insertGadget.btn','-','insertPortalLink.btn']
	]
};