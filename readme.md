# Site Crawler

A collection of tools for crawling sites and doing tedious downloads. While configurable, this is mostly hard coded / uses 'strategies' I'm writing in Kotlin, as opposed to a DSL or json config files.

## Book Stitcher

Given an url and a strategy, reads through a web book, downloads the pages, and stitches them together into an ebook

## Asset Downloader

Allows for mass downloading pictures or videos. Currently, used to download all of my xbox clips or screenshots. Can follow links across multiple pages to find and download all assets in a chunked manner.