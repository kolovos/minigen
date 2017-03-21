# Minigen

Minigen is an inline text generator which works with any text editor in Mac OS X (Sublime, Atom, Word, Pages etc.). Minigen provides support for complex templates written in EGL (http://www.eclipse.org/epsilon/doc/egl), Velocity (http://velocity.apache.org), and FreeMarker (http://www.freemarker.org), and for simpler templates written using the Java Message Format (http://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html).

The following video shows Minigen in action.

[![Minigen - Introduction](http://img.youtube.com/vi/0Nn3nuS6bC4/0.jpg)](https://www.youtube.com/watch?v=0Nn3nuS6bC4)

## Tips

Atom overrides the behaviour of `Shift+Cmd+Left` which Minigen uses to select the current line when you press `Ctrl+\`. To restore the default system behaviour, add the following lines to Atom's `keymap.json`.

```json
'atom-text-editor':
'shift-cmd-left': 'editor:select-line'
```

