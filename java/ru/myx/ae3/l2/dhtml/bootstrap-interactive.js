if(window.document && document.createElement){
	document.close();
	var root = document.createElement('div');
	root.style.cssText = 'position:absolute;left:0;top:0;width:100%;height:100%;background-color:#aaa;overflow:hidden';
	document.body.appendChild(root);
	require.script('debug.js');
	windows.JSON || require.script('json2.js');
	var ae3 = requireScript("ae3.js");
	ae3.someParameter = "someValue";
	ae3.intl = {
		language : "ru",
		languages : ["en", "ru"]
	};
	
	setTimeout(function(){
		ae3 = new ae3(root);
		ae3.defaultIconBase = "images/icons/";
		ae3.display();
		require.source('index.jsld', function(s){
			ae3.apps.add(JSON.parse(s));
		});
	}, 0);
}
