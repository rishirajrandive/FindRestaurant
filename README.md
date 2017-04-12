# FindRestaurant
Android app to find restaurant based on location. App uses [Yelp APIs](https://www.yelp.com/developers/documentation/v2/overview).

## Working of the app
* Pick location using map.<br>
* Search type of food you wish to eat.<br>
* Get list of restuarants based on your search.<br>
* Filter results on distance using filter options.<br>
* Browse through results, check details of restaurant.<br>
* Make reservation on click of button.<br>
* Make favorites by clicking on heart.<br>
* Manage your favorites.<br>


## Build with
* [Android SDK](https://developer.android.com/studio/index.html) - used Android Studio for development
* [Place picker](https://developers.google.com/places/android-api/placepicker) - part of Google Places API
* [SearchView](https://developer.android.com/guide/topics/search/search-dialog.html) - used to provide search option
* [RecyclerView](https://developer.android.com/training/material/lists-cards.html) - displays the results in list.
* [ViewPager](https://developer.android.com/training/animation/screen-slide.html) - to enable swiping through results on details page.
* [SQLite](https://developer.android.com/training/basics/data-storage/databases.html) - used to store the favorites.
* [Navigation Drawer](https://developer.android.com/training/implementing-navigation/nav-drawer.html) - to provide option to navigate between Search and Favorites.

### Screen shots of app in action

* For first time, search page will show default search for All restaurants in San Jose location 

<img width="300" height="400" alt="search" src="https://raw.githubusercontent.com/rishirajrandive/FindRestaurant/master/images/searchpage.png"/>


* Details page shown when user selects a restaurant from the list

<img width="300" height="400" alt="details" src="https://raw.githubusercontent.com/rishirajrandive/FindRestaurant/master/images/detail.png"/>


* Favorite list

<img width="300" height="400" alt="favorite" src="https://raw.githubusercontent.com/rishirajrandive/FindRestaurant/master/images/favorite.png"/>


* Button to make reservation

<img width="300" height="400" alt="reservation" src="https://raw.githubusercontent.com/rishirajrandive/FindRestaurant/master/images/makereservation.png"/>


[Check out the video for working demo](https://www.youtube.com/watch?v=aQnOjylo00g)

