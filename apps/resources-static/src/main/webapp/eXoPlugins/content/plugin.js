CKEDITOR.plugins.add('content',
	{
		init : function(editor) {
			var pluginName = 'content';
			var mypath = this.path;	
			editor.ui.addButton(
				'content.btn',
				{
					label : "WCM Content Selector",
					command : 'content.cmd',
					icon : mypath + '/images/content.jpg'
				}
			);
			var cmd = editor.addCommand('content.cmd', {exec:showContentSelector});
			cmd.modes = {wysiwyg: 1, source: 1};
			cmd.canUndo = false;	
			CKEDITOR.ContentSelector = editor;
		}
	}
);

function showContentSelector(e){
	window.open('http://127.0.0.1:8080/eXoStaticResources/eXoPlugins/content/content.html', 'CONTENT SELECTOR', 'width=1024, height=600, scroll=no, resizable=yes');
}