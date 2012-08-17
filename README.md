Introduction
=========================

This is an application that I did for a client, GoodGuide. This is a private repository and I ask that you do not
copy this code - this is just intended as a sample of some of my Android work. I did this a couple years ago, so 
the frameworks and methodologies may have changed since then.

Design
-------------------------
I typically will code a: **View**, **View Controller**, **Business Object**, and some type of **Model**. This particular project was shipped with a 'seed' database of products that is updated periodically on resume or launch via a webservices call and passing the asOfdate.

This App Features
-------------------------
1. **Custom UI Elements** - Custom menu bar, search, item-detail tab bar.
2. **Seed DB with background refresh** - App is shipped with db of over 30,000 products and pruned/refresh in BG
3. **Scan to Search** - Scan to search functionality => locally => goodguide webservices => 3rd party upc site 
4. **Native Search** - Native android search
4. **Google Analytics** - Google Analytics page views and actions

