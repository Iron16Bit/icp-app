# Interactive Code Playgrounds - App

A mobile app used to show [ICP slides](https://github.com/Iron16Bit/icp-bundle) on mobile devices.

## Usage:

The app works as a media player. In order to be used, it needs:
- The HTML slides
- The languages used in the slides

Slides can be created using the [ICP editor](https://github.com/Iron16Bit/icp-editor) and exporting for `self-hosting`.
These slides use `full-offline` as  language by default, but it can be manually edited in the HTML file replacing it with the desired language.

Languages need to be manually built from the [ICP bundle](https://github.com/Iron16Bit/icp-bundle).
Once the desired languages have been built, a zip file can be found in `dist/base/export_languages.zip`.

When these 2 files are ready, they can be distributed to mobile devices and imported through the app.

### Example:

In the following case, the slides request all languages in order to work.

``` html
<script src="full-offline.iife.js"></script>
```

If we only use Javascript and C++, we can replace this with:

``` html
<script src="javascript.iife.js"></script>
<script src="cpp-offline.iife.js"></script>
```

This allows to make the package with languages smaller.