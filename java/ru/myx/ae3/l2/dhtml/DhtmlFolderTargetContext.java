package ru.myx.ae3.l2.dhtml;

import java.io.File;

import ru.myx.ae3.l2.html.HtmlFolderTargetContext;
import ru.myx.ae3.l2.skin.Skin;

/**
 * 
 * @author myx
 * 
 */
public class DhtmlFolderTargetContext extends HtmlFolderTargetContext {
	
	/**
	 * @param skin
	 * @param folder
	 * @param index
	 */
	public DhtmlFolderTargetContext(final Skin skin, final File folder, final File index) {
		super( skin, folder, index );
	}
	
	@Override
	public void doFinish() {
		DhtmlCommon.doFinish( this.object, true, this.html, this.htmlHead, this.htmlBody, this );
		super.doFinish();
	}
}
