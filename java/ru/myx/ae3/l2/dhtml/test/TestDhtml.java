package ru.myx.ae3.l2.dhtml.test;

import java.io.File;
import java.io.FileInputStream;

import ru.myx.ae3.Engine;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.l2.LayoutEngine;
import ru.myx.ae3.l2.dhtml.DhtmlFolderTargetContext;
import ru.myx.ae3.l2.skin.Skin;
import ru.myx.ae3.report.Report;

/**
 * @author myx
 * 
 */
public class TestDhtml {
	
	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(final String[] args) throws Throwable {
		{
			final File userDir = new File( System.getProperty( "user.dir" ) );
			final File pathPublic = new File( userDir.getParentFile(), "acm-cvs/sys-current" );
			assert pathPublic.exists() : "public doesn't exist: " + pathPublic.getAbsolutePath();
			assert new File( pathPublic, "axiom" ).exists() : "public/axiom doesn't exist: public="
					+ pathPublic.getAbsolutePath();
			final File pathProtected = new File( userDir.getParentFile(), "unit-test/!protected" );
			final File pathPrivate = new File( userDir.getParentFile(), "unit-test/!private" );
			System.setProperty( "ru.myx.ae3.properties.path.public", pathPublic.getAbsolutePath() );
			System.setProperty( "ru.myx.ae3.properties.path.protected", pathProtected.getAbsolutePath() );
			System.setProperty( "ru.myx.ae3.properties.path.private", pathPrivate.getAbsolutePath() );
			System.setProperty( "ru.myx.ae3.properties.log.level", "DEBUG" );
			Engine.createGuid();
			System.out.println( "public: " + Engine.PATH_PUBLIC );
			System.out.println( "protected: " + Engine.PATH_PROTECTED );
			System.out.println( "private: " + Engine.PATH_PRIVATE );
			System.out.println( "level: " + Report.LEVEL_NAME );
		}
		
		final File folder = File.createTempFile( "dhtmltest-", ".tmp" );
		folder.delete();
		folder.mkdirs();
		System.out.println( "LayoutEngine2 DHTML renderer test, output to: "
				+ folder.getAbsolutePath()
				+ ", exists?="
				+ folder.exists()
				+ ", folder?="
				+ folder.isDirectory() );
		
		final File index = new File( folder, "index.js.html" );
		
		final BaseObject text = args == null || args.length == 0
				? LayoutEngine.getDocumentation() // context.getLayoutAbout()
				: args.length > 1
						? LayoutEngine.getDocumentation()
						: LayoutEngine.parseJSLD( new FileInputStream( args[0] ) );
		new DhtmlFolderTargetContext( Skin.SKIN_STANDARD, folder, index ).transform( text ).baseValue();
		
		Engine.createProcess( index.getName(), null, index.getParentFile() );
	}
}
