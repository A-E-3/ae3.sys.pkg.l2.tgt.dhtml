# CLAUDE.md — ae3.sys.pkg.l2.tgt.dhtml

AE3 L2 media target: a decorator around the HTML DOM target that bootstraps a JS rich-client onto the rendered page. Not related to XML/XSLT rendering — see "Not the same 'xhtml' as ae3.sys.pkg.l2.tgt.xml" below.

## Structure

- `java/ru/myx/ae3/l2/dhtml/` — package is `dhtml`, not `xhtml`.
  - `DhtmlFolderTargetContext` — `extends ru.myx.ae3.l2.html.HtmlFolderTargetContext`. `doFinish()` calls `DhtmlCommon.doFinish(this.object, true, this.html, this.htmlHead, this.htmlBody, this)` before `super.doFinish()` — decorates the standard HTML rendering with a JS-client bootstrap injection, then defers to the normal HTML finish logic.
  - `DhtmlCommon` — package-private, not extending/implementing anything (a static helper only). `doFinish(...)` takes an already-built HTML DOM (`Document html`, `Element htmlHead`/`htmlBody`) and: registers a fixed list of JS/CSS files (`require.js`, `json2.js`, `debug.js`, and the whole bundled BUI widget framework under `resources/skin/skin-jsclient/`) as servable binaries via `original.registerBinary(...)`; serializes the target's own data object to `js/index.jsld` via `JsonTargetContext` + `Ecma.toEcmaSourceCompact(...)`; injects a `<script>` bootstrap tag (`bootstrap.js` or `bootstrap-interactive.js`, chosen by an `interactive` flag) and a "your browser doesn't support javascript" fallback `<div>` into the page body.
  - `test/TestDhtml.java` — manual smoke-test entry point (`main`), same shape as `ae3.sys.pkg.l2.tgt.xml`'s `TestXml`: renders `LayoutEngine.getDocumentation()` (or a `.jsld` file from `args[0]`) via `DhtmlFolderTargetContext`, output to `index.js.html` in a temp folder, opened via `Engine.createProcess`.
- `ae3-packages/ae3.sys.l2.tgt.dhtml/resources/skin/skin-jsclient/` — the bundled JS rich-client framework `DhtmlCommon` registers: `require.js`-based module loading, a BUI (Basic UI?) widget library (`Layouts/*.js` — Button, Grid, Menu, Tabble, etc.), effects (`Effects/*.js` — Busy, Shadow, Transition), CSS themes. File content and screenshots are dated 2011 — an old rich-client framework, not actively evolved alongside the rest of AE3.

## Build

Eclipse Java project. `.classpath` references `/ae3.api`, `/ae3.sdk`, `/ae3.sys.pkg.l2.tgt.html`, `/ae3.sys.pkg.l2.tgt.json` by Eclipse project name. **`project.inf` is still missing** — write its `Requires:` from the actual `Provides:` strings in `ae3.sys.pkg.l2.tgt.html`'s (and possibly `ae3.sys.pkg.l2.tgt.json`'s) own `project.inf`, not inferred from `package.json`.

`ae3-packages/ae3.sys.l2.tgt.dhtml/package.json`: name `ae3.sys.l2.tgt.dhtml`, `"requires": "ae3.sys.l2.tgt.html"` — this is the **JS/`ae3-packages` module system's** dependency declaration, a separate graph from the Java `project.inf`'s `Requires:`; don't assume the two are interchangeable strings without checking.

## Gotchas

- `.gitignore`/`.project`/`.classpath` are present, modeled on `ae3.sys.pkg.l2.tgt.xml`'s versions. `project.inf` is still missing (see Build above).
- 6 compiled `.class` files are checked into git under `bin/` (e.g. `bin/ru/myx/ae3/l2/dhtml/DhtmlCommon.class`) — `.gitignore` prevents future additions but doesn't retroactively untrack what's already committed; needs an explicit `git rm --cached`.
- **Not the same "xhtml" as `ae3.sys.pkg.l2.tgt.xml`'s `WebContextXmlXhtml`/`WebContextXmlAutoDetect`.** Those apply an XML target's own registered XSLT templates to that target's own XML responses (see that unit's CLAUDE.md) — a narrow, XML-target-specific mechanism, unrelated to this unit's JS-client bootstrap. This unit's package is `ru.myx.ae3.l2.dhtml`; its two real classes (`DhtmlCommon`, `DhtmlFolderTargetContext`) are entirely about the JS-client bootstrap described above, nothing DOM/XHTML-building of its own. `ae3.sys.pkg.l2.tgt.html` is where `ru.myx.ae3.l2.xhtml.XhtmlDomTargetContext` actually lives.
