package ru.myx.ae3.l2.dhtml;

import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ru.myx.ae3.Engine;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.Transfer;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.ecma.Ecma;
import ru.myx.ae3.l2.NativeTargetContext;
import ru.myx.ae3.l2.html.HtmlCommon;
import ru.myx.ae3.l2.html.HtmlDomTargetContext;
import ru.myx.ae3.l2.json.JsonTargetContext;

final class DhtmlCommon {
	
	private final static TransferCopier REQUIRE;
	
	private final static TransferCopier JSON2;
	
	private final static TransferCopier DEBUG;
	
	private final static Element BOOTSTRAP_MINIMAL;
	
	private final static Element BOOTSTRAP_INTERACTIVE;
	
	private final static Map<String, TransferCopier> FILES;
	
	static {
		REQUIRE = Transfer.createCopier(
				new File(
						Engine.PATH_PUBLIC, //
						"resources/skin/skin-jsclient/js/require.js"));
		
		JSON2 = Transfer.createCopier(
				new File(
						Engine.PATH_PUBLIC, //
						"resources/skin/skin-jsclient/js/json2.js"));
		
		DEBUG = Transfer.createCopier(
				new File(
						Engine.PATH_PUBLIC, //
						"resources/skin/skin-jsclient/js/debug.js"));
		
		{
			final Document document = HtmlCommon.createHtmlDocument();
			
			{
				final Element script = document.createElement("script");
				script.setAttribute("type", "text/javascript");
				script.appendChild(document.createTextNode("//"));
				final String bootstrap = Transfer.createBuffer(DhtmlCommon.class.getResourceAsStream("bootstrap.js")).toString(StandardCharsets.UTF_8).trim();
				script.appendChild(document.createCDATASection("\n" + bootstrap + "\n//"));
				
				BOOTSTRAP_MINIMAL = script;
			}
			
			{
				final Element script = document.createElement("script");
				script.setAttribute("type", "text/javascript");
				script.appendChild(document.createTextNode("//"));
				final String bootstrap = Transfer.createBuffer(DhtmlCommon.class.getResourceAsStream("bootstrap-interactive.js")).toString(StandardCharsets.UTF_8).trim();
				script.appendChild(document.createCDATASection("\n" + bootstrap + "\n//"));
				
				BOOTSTRAP_INTERACTIVE = script;
			}
		}
		
		FILES = new TreeMap<>();
		for (final String name : new String[]{
				"css/bui/theme/crazy1.css", "css/bui/theme/default.css", "css/bui/theme/girlpower.css", "css/bui/theme/operax.css", "css/bui/theme/sparta.css", "js/ae3.js",
				"js/BUI/Respect.js", "js/BUI/Settings.js", "js/Effects/Busy.js", "js/Effects/BusyConfigMenu.js", "js/Effects/busy/charloop.js", "js/Effects/busy/image.js",
				"js/Effects/busy/monodimensional.js", "js/Effects/busy/pythontrail.js", "js/Effects/busy/images/feedback-wait-loading-fly.32.gif",
				"js/Effects/busy/images/hourglass.png", "js/Effects/Effect.js", "js/Effects/Shadow.js", "js/Effects/ShadowConfigMenu.js", "js/Effects/Transition.js",
				"js/Effects/TransitionConfigMenu.js", "js/Effects/transition/appear.js", "js/Effects/transition/disappear.js", "js/Effects/transition/fade.js",
				"js/Layouts/Attachment.js", "js/Layouts/Button.js", "js/Layouts/Clock.js", "js/Layouts/Container.js", "js/Layouts/Date.js", "js/Layouts/Document.js",
				"js/Layouts/DropPanel.js", "js/Layouts/Frame.js", "js/Layouts/Grid.js", "js/Layouts/Icon.js", "js/Layouts/Image.js", "js/Layouts/Layout.js",
				"js/Layouts/LayoutConfigMenu.js", "js/Layouts/Link.js", "js/Layouts/Menu.js", "js/Layouts/Numbered.js", "js/Layouts/Screen.js", "js/Layouts/Sequence.js",
				"js/Layouts/String.js", "js/Layouts/Tabble.js", "js/Layouts/Toolbar.js", "js/Utils/Cookies.js", "js/Utils/Coordinates.js", "js/Utils/Event.js"
		}) {
			DhtmlCommon.FILES.put(name, Transfer.createCopier(new File(Engine.PATH_PUBLIC, "resources/skin/skin-jsclient/" + name)));
		}
	}
	
	public static final void doFinish(final BaseObject object,
			final boolean interactive,
			final Document html,
			final Element htmlHead,
			final Element htmlBody,
			final HtmlDomTargetContext<?> original) {
		
		// <script type="text/javascript"
		// src="controls/js/require.js"></script>
		{
			final Element htmlHeadScript = html.createElement("script");
			htmlHeadScript.setAttribute("type", "text/javascript");
			final URI url = original.registerBinary("js/require.js", DhtmlCommon.REQUIRE);
			htmlHeadScript.setAttribute("src", url.toString());
			htmlHead.appendChild(htmlHeadScript);
		}
		{
			for (final String name : DhtmlCommon.FILES.keySet()) {
				original.registerBinary(name, DhtmlCommon.FILES.get(name));
			}
		}
		{
			final JsonTargetContext jsonContext = new JsonTargetContext(original, NativeTargetContext.TargetMode.SERVER);
			jsonContext.transform(object);
			original.registerBinary("js/index.jsld", Transfer.createCopier(Ecma.toEcmaSourceCompact(jsonContext.getResultLayout()).getBytes(StandardCharsets.UTF_8)));
		}
		{
			original.registerBinary("js/json2.js", DhtmlCommon.JSON2);
			original.registerBinary("js/debug.js", DhtmlCommon.DEBUG);
			final Node htmlBodyScript = html.importNode(
					interactive
						? DhtmlCommon.BOOTSTRAP_INTERACTIVE
						: DhtmlCommon.BOOTSTRAP_MINIMAL,
					true);
			htmlBody.insertBefore(htmlBodyScript, htmlBody.getFirstChild());
		}
		{
			final Element div = html.createElement("div");
			div.setAttribute("align", "center");
			div.setAttribute("style", "font-weight:bold;font-size:8px;line-height:8px;border:1px solid red;margin-left:auto;");
			div.appendChild(html.createTextNode("Sorry, your browser doesn't support javascript or have javascript support turned off."));
			htmlBody.insertBefore(div, htmlBody.getFirstChild().getNextSibling());
		}
	}
	
	private DhtmlCommon() {
		
		//
	}
}
