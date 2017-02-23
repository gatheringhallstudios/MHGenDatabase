MHGen Database for Android
=======================

Source code for the Android app _MHGen Database_.

[![Download from Google Play](http://www.android.com/images/brand/android_app_on_play_large.png "Download from Google Play")](https://play.google.com/store/apps/details?id=com.ghstudios.android.mhgendatabase&hl=en)

List of To-Dos can be found on our [Trello Board](https://trello.com/b/tI4PYsgH/mhgen-database)

Join our team on [Slack](gatheringhallstudios.slack.com). Request a slack invitation via <jayson.p.delacruz@gmail.com>

### Building
Source runs in Android Studio.

### Database
Located in `MHGenDatabase\app\src\main\assets\databases`.

### Art
Located in `MHGenDatabase\app\src\main\assets\`

### Package Layout

`com.daviancorp.android.data.classes`
  - Contains classes for data objects

`com.daviancorp.android.data.database`
  - Contains Cursors to return rows/tuples from the database queries.
  - MonsterHunterDatabaseHelper.java: direct queries to database
  - DataHelper.java: Used by loaders to query database; uses MonsterHunterDatabaseHelper for queries

`com.daviancorp.android.loader`
  - Contains loaders for UI to load data

`com.daviancorp.android.ui.adapter`
  - Pager adapters

`com.daviancorp.android.ui.detail`
  - Detail activities + fragments to display a specific object and related data

`com.daviancorp.android.ui.dialog`
  - 'About' dialog
  - Wishlist-related dialogs

`com.daviancorp.android.ui.general`
  - Base activities, all containing navigation drawers

`com.daviancorp.android.ui.list`
  - List activities + fragments
