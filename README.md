# ArtistSearch
A simple project that uses Retrofit and RxJava to fetch data from the Last FM api.

The initial commit is just a app that fetches data from the Last Fm Api using Retrofit, RxJava and
RxObservables.
It shows two main ways to do this, either using a single subscription(Main Activity) or a composite
subscription(SimilarArtistsActivity).

It also has examples of how to make an object parcelable, custom floating action button behaviour,
progress bar on top of a fab and in the toolbar, simple image loading with Picasso, adding a
fragment to an activity dynamically, fragment replacement, recycler view use e.t.c

This was just my practise but I hope t helps someone out there. :D

Useful Links for Initial Commit:<br />
http://www.last.fm/api<br />
http://reactivex.io/documentation/observable.html<br />
https://guides.codepath.com/android/Consuming-APIs-with-Retrofit#rxjava<br />
http://www.node.mu/2014/07/02/using-retrofit-and-rxjava-to-interact-with-web-services-on-android/<br />
http://randomdotnext.com/retrofit-rxjava/<br />
http://blog.stablekernel.com/replace-asynctask-asynctaskloader-rx-observable-rxjava-android-patterns/<br />
https://kmangutov.wordpress.com/2015/03/28/android-mvp-consuming-restful-apis/<br />

The second commit shows how to use Sugar ORM to persist data to a database. It also shows how to use
an Rx.Single instead of an Rx.Observable,  useful when you're observing a process that will emit
only a single item. I also changed the Main Activity into a Drawer Activity.

I implemented a new Saved Artists Activity (also a Drawer Activity). The Recycler view for the list
of saved artists has Multi Choice support provided by the MultiSelector from Big Nerd Ranch.
did a little code optimisation as well.

Useful Links for Second Major Commit:<br />
http://reactivex.io/documentation/single.html<br />
http://satyan.github.io/sugar/getting-started.html<br />
https://developer.android.com/guide/topics/ui/menus.html<br />
https://bignerdranch.github.io/recyclerview-multiselect/<br />
https://www.bignerdranch.com/blog/recyclerview-part-2-choice-modes/<br />
