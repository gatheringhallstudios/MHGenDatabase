MHGU Database for Android (formally MHGen Database)
=======================

Source code for the Android app _MHGU Database_.

[![Download from Google Play](http://www.android.com/images/brand/android_app_on_play_large.png "Download from Google Play")](https://play.google.com/store/apps/details?id=com.ghstudios.android.mhgendatabase&hl=en)

List of To-Dos can be found on our [Trello Board](https://trello.com/b/tI4PYsgH/mhgen-database)

Join our team on [Slack](gatheringhallstudios.slack.com). Request a slack invitation via <jayson.p.delacruz@gmail.com>

You can also freely join our [Discord](https://discord.gg/k5rmEWh).

### Building
Source runs in Android Studio. Open it, compile it, and run the project. You may need to create an emulator using the AVD Manager.

### Database
Located in `MHGenDatabase\app\src\main\assets\databases`.

### Art
Located in `MHGenDatabase\app\src\main\assets\` and `MHGenDatabase\app\src\main\icon-res\drawable`

### Package Layout

`com.ghstudios.android.data`
  - Contains classes to query the database

`com.ghstudios.android.loader`
  - Contains loaders for UI to load data. Being phased out in favor of ViewModels and LiveData.

`com.ghstudios.android.adapter`
  - Pager adapters
  - RecyclerView adapters and adapter delegates
  
 `com.ghstudios.android.ClickListeners`
 - View ClickListener objects used to handle navigation to a different subsystem.

`com.ghstudios.android.features`
  - Contains subpackages, one for each subsystem supported by the app

`com.ghstudios.android.components`
  - Custom Views

`com.ghstudios.android.util`
  - Various general-purpose utilities and extension functions 
