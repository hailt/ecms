/*
	eXo config plugins
*/

CKEDITOR.eXoPath = CKEDITOR.basePath.substr(0, CKEDITOR.basePath.indexOf("ckeditor/"));

// config to add custom plugin	
(function() {CKEDITOR.plugins.addExternal('helloworld',CKEDITOR.eXoPath+'eXoPlugins/helloworld/', 'plugin.js');})();
(function() {CKEDITOR.plugins.addExternal('test',CKEDITOR.eXoPath+'eXoPlugins/test/','plugin.js');})();
(function() {CKEDITOR.plugins.addExternal('content',CKEDITOR.eXoPath+'eXoPlugins/content/','plugin.js');})();

CKEDITOR.editorConfig = function( config ){
	config.extraPlugins = 'helloworld,content';
	config.toolbar_Basic.push(['helloworld.btn','-','content.btn']);	
/*
 	config.toolbar = "MyToolbarSet"; 
	config.toolbar_MyToolbarSet = 
	[
	    ['Bold','Italic','-'],['test.btn']
	];	
	config.toolbar_MyToolbarSet.push(['test.btn']);
* */
};

