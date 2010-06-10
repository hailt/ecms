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
	window.open('http://127.0.0.1:8080/eXoStaticResources/eXoPlugins/test/test.html', 'TEST', 'width=500, height=400, scroll=no, resizable=yes');
}