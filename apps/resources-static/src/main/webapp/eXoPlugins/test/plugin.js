CKEDITOR.plugins.add('test',
	{
		init : function(editor) {
			var pluginName = 'test';
			var mypath = this.path;		
			editor.ui.addButton(
				'test.btn',
				{
					label : "Test window opener",
					command : 'test.cmd',
					icon : mypath + '/images/album.gif'
				}
			);
			var cmd = editor.addCommand('test.cmd', {exec:showWindowPlugin});
		}
	}
);

function showWindowPlugin(e){
	window.open(CKEDITOR.eXoPath+'eXoPlugins/test/test.html');
}