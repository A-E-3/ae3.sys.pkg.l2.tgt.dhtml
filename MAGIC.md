# MAGIC.md — ae3.sys.pkg.l2.tgt.dhtml

Team-owned. Durable findings/gotchas for this repo, not investigation narration.

## For keeper-ae3

- Despite the name similarity, `XhtmlDomTargetContext` does NOT live here (this unit's package is `ru.myx.ae3.l2.dhtml`, unrelated to `xhtml`) — it's in `ae3.sys.pkg.l2.tgt.html`.
- **This target cannot be selected, and no file in the tree names it.** The project ships no `ae3-packages/<pkg>/settings/` folder, so no `settings/system/l3/targets/*.json` descriptor points at `DhtmlFolderTargetContext`. Files naming `skin-standard-dhtml`, excluding `bin/`: this project 1, and that one hit is its own `.git/index`; every other AE3 project 0. Control that the probe finds matches: `skin-jsclient` gives 7 here and 5 in `/Volumes/workspace/myx/ae3.sys.pkg.l2.tgt.html`. It still builds and packages: `/Volumes/workspace/myx/acm-cvs/sys-current/axiom/ru.myx.ae3sys.l2.tgt.dhtml-5.0.jar` holds `DhtmlCommon.class` and `DhtmlFolderTargetContext.class`, and `/Volumes/workspace/myx/ae3.system/project.inf:19` requires the project.
- **`skin-standard-dhtml` carries no layout, so nothing arrives by inheritance either.** `/Volumes/workspace/myx/acm-cvs/sys-current/resources/skin/skin-standard-dhtml/` holds `skin.settings.xml` alone, byte-identical to the project copy. It is `abstract: true`, prototypes `skin-standard-html` and imports `skin-jsclient`. `/Volumes/workspace/myx/acm-cvs/sys-current/resources/skin/skin-standard-html/layouts/` holds one file, `DataForm.jso`.
- **The render model is intact and is what this target contributed.** `DhtmlCommon.doFinish` serialises the layout object with a `JsonTargetContext` and registers it as `js/index.jsld` (`DhtmlCommon.java:114-118`). It also registers `js/require.js`, `js/json2.js`, `js/debug.js` and the 48 entries of its own `FILES` list (`DhtmlCommon.java:78-88`: 5 CSS, 41 JS, 2 images), then inserts one of two bootstraps into the page body. A folder target writes the index and every registered binary, so one `transform` call (`DhtmlFolderTargetContext.java:25-28`) produces a complete static bundle.
- **Both bootstraps carry name errors, so neither can run as written.** `bootstrap.js:15` calls a bare `Layout`, and no file in `skin-jsclient/js` assigns that name: `Layouts/Layout.js:139` assigns `Layouts.Layout`, and `ae3.js:2` assigns `ae3`. `bootstrap.js:25` and `bootstrap-interactive.js:7` read `windows.JSON`, which is not `window.JSON`. Reasoned from the files; not reproduced in a browser, and no page was served.
- **The render model's stylesheets are five, and the skins carry no modern CSS at all.** `DhtmlCommon.java:78-88` registers five stylesheets, every one under `css/bui/theme/`, and `js/Layouts/Layout.js:891` loads one of them by name from `css/bui/theme/<name>.css`. The other eleven CSS files in `skin-jsclient` are named only from its own `test/` pages, save three that are named nowhere. `css/reset.css`, `css/reset-ie6.css` and `css/reset-ie7.css` are 27 bytes each at md5 `e86a111e2cd6d18e2967f53030b813e9`, holding `{padding:0;margin:0;}` with no selector, so they reset nothing; the only two files naming any of the three, here and in `/Volumes/workspace/myx/acm-cvs/sys-current`, are those trees' `.git/index`. Measured over the 244 CSS files and 19332 lines under `/Volumes/workspace/myx/acm-cvs/sys-current/resources/skin/`, occurrences: `@media` 0, `@supports` 0, `var(--` 0, `calc(` 0, `display:flex` 0, `display:grid` 0, `:has(` 0, `:is(` 0, `:where(` 0, against `progid:` 248, `zoom:` 196, `filter: alpha` 59 and `behavior:` 39. Controls on the same read: 140 files carry `color:` and 236 carry an opening brace. So a browser floor stated for a new client binds that client's own CSS; these skins hold no media query, custom property or layout module to inherit.

## Two `skin-jsclient` layout defects, each measured on its own source lines

Two defects sit in this project's `skin-jsclient`, one per file, and both are measured.

### `Attachment.js:339` to `:342`: the flag lands on the wrong prototype

The block reads:

```
Layouts.AttachFixed.prototype = new ((window.Layouts && (Layouts.Screen || ...)) || function(){})(),
Layouts.AttachGrow.prototype.putAll({
	isComponentAttachFixed : true
}),
```

The second line names `AttachGrow` and writes `isComponentAttachFixed`. So the flag lands on `AttachGrow`'s prototype, and no `AttachFixed` instance carries it.

- Measured under JavaScriptCore, with those four source lines read from the file and evaluated as written: `fixed.isComponentAttachFixed = undefined` and `grow.isComponentAttachFixed = true`.
- `AttachFixed` does inherit correctly: `fixed instanceof Screen` is `true`.
- `AttachFixed` is reachable. `Attachment.js:151` gives it as the default when `size` is set and `definition.attachment` is absent.

Nothing reads either flag. `isComponentAttachFixed` and `isComponentAttachGrow` each occur once in the project tree, on their own definition line.

The same holds in `/Volumes/workspace/myx/acm-cvs/sys-current/resources/skin` and `/Volumes/workspace/myx/acm-cvs/sys-release/resources/skin`: 1 line each, that same one.

A control on the same trees: `isComponentContainer` occurs 21 times in the project tree and 21 times in `sys-current`. So a reader of a component flag would be found by this probe.

Read it as a latent defect. The flag is wrong, and no code asks for it.

### `Layout.js:153`: the missing-layout alert cannot fire

The line reads:

```
var LayoutClass = Layouts[layoutClass] || require ? require("Layouts." + layoutClass) : alert("Missing required javascript: Layouts/" + layoutClass + ".js");
```

Operator precedence makes the condition `(Layouts[layoutClass] || require)`. Wherever `require` is defined, that condition is truthy, so the `alert` branch never runs.

- Measured under JavaScriptCore with the source line read from the file: with `require` defined, `LayoutClass` comes back `null` and `alert` is called 0 times. With `require` set to `undefined`, `alert` is called once.

The silence is deeper than that line. `require.js:268` to `:292` returns `null` for a 404 and never alerts.

- Measured with `loadResult` lifted from `require.js` and evaluated as written: a 404 gives 0 alerts and `null`. The control, a 500, gives 1 alert. A 200 returns the body and alerts nothing.

So a missing layout class is reported nowhere: not by the descriptor line, and not by the loader it falls through to.

What the reader gets instead is `Layout.js:157` onward, which logs to `top.debug` and builds a `Container`-based default class under the asked name.

### Where the two are repaired

Here, in `ae3-packages/ae3.sys.l2.tgt.dhtml/resources/skin/skin-jsclient/js/Layouts/`, in the `eclipse-ae3` checkout.

The axiom's copies are exports of this project, not a second source. `ae3-devel-tools/MAKE-AE3-DISTRO.xml:123` stages this project's `resources/**` into the shared distro, and `acm-devel-tools/EXPORT-MAKE-SYS-CURRENT.xml:494` pulls the distro's `ae3-data/resources` into the axiom.

Measured: this project's `skin-jsclient` and `sys-current`'s hold 142 files each, and their `skin.settings.xml` are one md5, `543d436b56ea5e4a6fdbbda6ca8da394`.

`Attachment.js` and `Layout.js` are md5-identical across all four copies. A fix here reaches the other three through the build.

## Which layout names stay silent, measured

`Layout.js:153` never reaches its `alert`. Operator precedence makes the condition `(Layouts[layoutClass] || require)`, which is truthy wherever `require` is defined. `require.js:206` to `:216` then returns `null` for a 404 in silence. So a layout name that resolves nowhere builds a default `Container` and reports nothing.

Measured under JavaScriptCore, with every source region read out of the file. The `require` stand-in follows require.js's own sync path through its own `loadResult`.

- Five names give 0 alerts and build a default container: `outset`, `title`, `final`, `number` and `boolean`.
- Control on the same probe: `screen` and `menu` return their classes, and `padding` resolves through `Layouts.Dummy`. All three give 0 alerts, as they should.
- `loadResult` alerts for a 500 alone. Statuses 404, 204, 301, -1100 and 1223 return `null` in silence, so the loader was never a channel that would have spoken.
- One name costs one lookup, not one per construction. The fallback caches the name at `Layout.js:163`.

Against the names this project's own sources use, six stay silent by file, binding or `Dummy` alias. They are `screen`, `string`, `attach-fixed`, `attach-grow`, `inset` and `padding`. Three resolve nowhere.

- `outset` sits in six shipped `test/Layouts/*.html` pages and in `ae3.js:75`, which is commented out.
- `intl` is the root of `java/ru/myx/ae3/l2/dhtml/about.jsld`, so the about page's content carries it.
- `interactive` is `js/Effects/ShadowConfigMenu.js:116`.

The repair is two lines, and `magic-coordinator` accepted them at 16:52 on 2026-11-13 against this file's md5 as the base. Its verdict of 17:33 is to wait for the board task that carries it. So no line is edited, and the silence stands here as measured.

The two lines, for the task that lands them. Line 153 becomes these two:

```
	var LayoutClass = Layouts[layoutClass] || (require && require("Layouts." + layoutClass));
	LayoutClass || alert("Missing required javascript: Layouts/" + layoutClass + ".js");
```

The file is md5 `01e6f499b2d57d1005492607f70bd051` in all four copies, so the one repair reaches the axiom and the devel checkout through the build.

The `AttachFixed` flag stays as it is. The line at `Attachment.js:339` to `:342` is not touched by that repair. A later change that makes `isComponentAttachFixed` or `isComponentAttachGrow` load-bearing must repair that block at that point.

## This project test entry opens its output in the desktop browser

`TestDhtml.java:62` calls `Engine.createProcess( index.getName(), null, index.getParentFile() )` unconditionally, after the render at `:60` has written `index.js.html` into a temp folder. On macOS that method's own macOS arm runs `/usr/bin/open`, so one window opens on the human-owner's screen per run and no output of the run shows it.

**Do not run this entry as it stands.** Render through a sandbox copy with line 62 removed, or through an offscreen probe. Change only the sandbox copy.

`EXPORT-ALONE.xml:8` names `ru.myx.ae3.l2.dhtml.TestDhtml` as the main class, while `TestDhtml.java:1` declares `package ru.myx.ae3.l2.dhtml.test`. The packaged class in `/Volumes/workspace/myx/acm-cvs/sys-current/axiom/ru.myx.ae3sys.l2.tgt.dhtml-5.0.jar` sits at `ru/myx/ae3/l2/dhtml/test/TestDhtml.class`, so the export descriptor's name is the wrong one.

The cross-cutting measurement, over all five render-target entries and over `ae3.sdk`'s three reply handlers, is in `ae3.sys.pkg.l2.tgt.html/MAGIC.md`.
