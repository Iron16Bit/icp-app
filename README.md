# Interactive Code Playgrounds - App

A mobile app used to show [ICP slides](https://lucademenego99.github.io/icp-slides/#/) on mobile devices.

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

## Remarks:

In order to use the editors with C++, Java and Python the browser must support SharedWorkers. At the moment, no mobile browser supports them except for Firefox. It is still possible to use ICP with other browsers, but these languages won't be functional.
Additionally, these languages are quite heavy. Mobile devices with less than 2GB of RAM won't be able to run them. Other devices might need up to 1 minute to initialize the environment, but will then compile in quite reasonable times.