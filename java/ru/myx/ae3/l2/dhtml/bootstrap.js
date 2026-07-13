if(window.document && document.createElement){
	document.close();
	var root = document.createElement('div');
	root.style.cssText = 'position:absolute;left:0;top:0;width:100%;height:100%;background-color:#aaa;overflow:hidden';
	document.body.appendChild(root);

	require.script('debug.js');
	
	var cb = function(){
		if(!window.JSON){
			require.script('json2.js', cb)
			return;
		} 
		require.source('index.jsld', function(s){
			new Layout(root,{
				layout	: 'screen',
				content	: JSON.parse(s)
			});
		});
	};
	
	require('Layouts.Layout', cb);
	
	/* earlier load */
	windows.JSON || require.script('json2.js');
	require.source('index.jsld');
}
