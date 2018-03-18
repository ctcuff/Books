# Books
Books is an app that displays book results using the JSON result (see below) returned by the [Google Books API](https://developers.google.com/books/docs/v1/reference/volumes/list).
This app is my first attempt at using/converting Java code to Kotlin. Books are displayed by parsing the JSON 
result returned by the API to extract properties like the title, author, a link to buy the book, etc...
Users can swipe left to add a book to a list of favorites and right to remove it. 

# Screenshots
<img src="https://github.com/ctcuff/Books/blob/master/app/screenshots/home_screen.png" width="275"> <img src="https://github.com/ctcuff/Books/blob/master/app/screenshots/search_activity.png" width="275">

# Sample JSON Response
[This is a response generated for the book Harry Potter](https://www.googleapis.com/books/v1/volumes?q=harry%20potter&maxResults=10&startIndex=0
)

Parameter Used | Definition
---------------|---------
q=             |Book title to search
maxResults     |The number of JSON objects to be returned (Max of 40 at a time so it must be paginated)
startIndex     |Where the first result of the JSON object array should start
```
{
 "kind": "books#volumes",
 "totalItems": 2617,
 "items": [
  {
   "kind": "books#volume",
   "id": "Sm5AKLXKxHgC",
   "etag": "OhKV26pdON0",
   "selfLink": "https://www.googleapis.com/books/v1/volumes/Sm5AKLXKxHgC",
   "volumeInfo": {
    "title": "Harry Potter and the Prisoner of Azkaban",
    "authors": [
     "J.K. Rowling"
    ],
    "publisher": "Pottermore",
    "publishedDate": "2015-12-08",
    "description": "\"'Welcome to the Knight Bus, emergency transport for the stranded witch or wizard. 
    Just stick out your wand hand, step on board and we can take you anywhere you want to go.'\" When the 
    Knight Bus crashes through the darkness and screeches to a halt in front of him, it's the start of another 
    far from ordinary year at Hogwarts for Harry Potter. Sirius Black, escaped mass-murderer and follower of Lord
    Voldemort, is on the run - and they say he is coming after Harry. In his first ever Divination class, Professor 
    Trelawney sees an omen of death in Harry's tea leaves... But perhaps most terrifying of all are the Dementors 
    patrolling the school grounds, with their soul-sucking kiss... Pottermore has now launched the Wizarding World 
    Book Club. Visit Pottermore to sign up and join weekly Twitter discussions at WW Book Club.",
    "industryIdentifiers": [
     {
      "type": "ISBN_13",
      "identifier": "9781781100516"
     },
     {
      "type": "ISBN_10",
      "identifier": "1781100519"
     }
    ],
    "readingModes": {
     "text": true,
     "image": true
    },
    "pageCount": 448,
    "printType": "BOOK",
    "categories": [
     "Juvenile Fiction"
    ],
    "averageRating": 4.5,
    "ratingsCount": 2225,
    "maturityRating": "NOT_MATURE",
    "allowAnonLogging": true,
    "contentVersion": "1.11.11.0.preview.3",
    "panelizationSummary": {
     "containsEpubBubbles": false,
     "containsImageBubbles": false
    },
    "imageLinks": {
     "smallThumbnail": "http://books.google.com/books/content?id=Sm5AKLXKxHgC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
     "thumbnail": "http://books.google.com/books/content?id=Sm5AKLXKxHgC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
    },
    "language": "en",
    "previewLink": "http://books.google.com/books?id=Sm5AKLXKxHgC&printsec=frontcover&dq=harry+potter&hl=&cd=2&source=gbs_api",
    "infoLink": "https://play.google.com/store/books/details?id=Sm5AKLXKxHgC&source=gbs_api",
    "canonicalVolumeLink": "https://market.android.com/details?id=book-Sm5AKLXKxHgC"
   },
   "saleInfo": {
    "country": "US",
    "saleability": "FOR_SALE",
    "isEbook": true,
    "listPrice": {
     "amount": 8.99,
     "currencyCode": "USD"
    },
    "retailPrice": {
     "amount": 8.99,
     "currencyCode": "USD"
    },
    "buyLink": "https://play.google.com/store/books/details?id=Sm5AKLXKxHgC&rdid=book-Sm5AKLXKxHgC&rdot=1&source=gbs_api",
    "offers": [
     {
      "finskyOfferType": 1,
      "listPrice": {
       "amountInMicros": 8990000.0,
       "currencyCode": "USD"
      },
      "retailPrice": {
       "amountInMicros": 8990000.0,
       "currencyCode": "USD"
      },
      "giftable": true
     }
    ]
   },
   "accessInfo": {
    "country": "US",
    "viewability": "PARTIAL",
    "embeddable": true,
    "publicDomain": false,
    "textToSpeechPermission": "ALLOWED",
    "epub": {
     "isAvailable": true,
     "acsTokenLink": "http://books.google.com/books/download/Harry_Potter_and_the_Prisoner_of_Azkaban-sample-epub.acsm?id=Sm5AKLXKxHgC&format=epub&output=acs4_fulfillment_token&dl_type=sample&source=gbs_api"
    },
    "pdf": {
     "isAvailable": true,
     "acsTokenLink": "http://books.google.com/books/download/Harry_Potter_and_the_Prisoner_of_Azkaban-sample-pdf.acsm?id=Sm5AKLXKxHgC&format=pdf&output=acs4_fulfillment_token&dl_type=sample&source=gbs_api"
    },
    "webReaderLink": "http://play.google.com/books/reader?id=Sm5AKLXKxHgC&hl=&printsec=frontcover&source=gbs_api",
    "accessViewStatus": "SAMPLE",
    "quoteSharingAllowed": false
   },
   "searchInfo": {
    "textSnippet": "But perhaps most terrifying of all are the Dementors patrolling the school grounds, 
    with their soul-sucking kiss... Pottermore has now launched the Wizarding World Book Club. Sign up 
    and join weekly Twitter discussions at WW Book Club."
   }
  }
```
