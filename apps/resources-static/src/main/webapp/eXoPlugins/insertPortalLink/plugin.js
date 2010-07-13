CKEDITOR.plugins.add('insertPortalLink',
	{
		init : function(editor) {
			var pluginName = 'InsertPortalLink';
			var mypath = this.path;	
			editor.ui.addButton(
				'insertPortalLink.btn',
				{
					label : "WCM Insert Portal Link",
					command : 'insertPortalLink.cmd',
					icon : mypath + '/images/insertPortalLink.gif'
				}
			);
			var cmd = editor.addCommand('insertPortalLink.cmd', {exec:showInsertPortalLink});
			cmd.modes = {wysiwyg: 1, source: 1};
			cmd.canUndo = false;	
			CKEDITOR.insertPortalLink = editor;
		}
	}
);

function showInsertPortalLink(e){
	window.open(CKEDITOR.eXoPath+'eXoPlugins/insertPortalLink/insertPortalLink.html','WCMINSERTPORTALLINK','width=600, height=400');
}
