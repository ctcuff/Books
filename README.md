# Books
Books is an app that displays book results using the JSON result (see below) returned by the [Google Books API](https://developers.google.com/books/docs/v1/reference/volumes/list).
This app is my first attempt at using/converting Java code to Kotlin. Books are displayed by parsing the JSON 
result returnes by the API to extract properties like the title, author, a link to buy the book, etc...
Users can swipe left to add a book to a list of favorites and right to remove it. 

# Screenshots
<img src="https://github.com/ctcuff/Books/blob/master/app/screenshots/home_screen.png" width="275"> <img src="https://github.com/ctcuff/Books/blob/master/app/screenshots/search_activity.png" width="275">

# Sample JSON Response
This is a response generated for the book Harry Potter using this link: 

https://www.googleapis.com/books/v1/volumes?q=harry%20potter&maxResults=10&startIndex=0

Parameter Used | Definition
---------------|---------
q=             |Book title to search
maxResults     |The number of JSON objects to be returned (Max of 40 at a time so it must be paginated)
startIndex     |Where the first result of the JSON object array should start
```
{
 "kind": "books#volumes",
 "totalItems": 2382,
 "items": [
  {
   "kind": "books#volume",
   "id": "DU0LDAAAQBAJ",
   "etag": "jtOSVkLdflU",
   "selfLink": "https://www.googleapis.com/books/v1/volumes/DU0LDAAAQBAJ",
   "volumeInfo": {
    "title": "Fantastic Beasts and Where to Find Them: The Original Screenplay",
    "authors": [
     "J.K. Rowling"
    ],
    "publisher": "Pottermore",
    "publishedDate": "2016-11-18",
    "description": "When Magizoologist Newt Scamander arrives in New York, he intends his stay to be just a brief stopover. 
    However, when his magical case is misplaced and some of Newt's fantastic beasts escape, it spells trouble for everyone... 
    Inspired by the original Hogwart’s textbook by Newt Scamander, Fantastic Beasts and Where to Find Them: The Original 
    screenplay marks the screenwriting debut of J.K. Rowling, author of the beloved and internationally bestselling Harry 
    Potter books. A feat of imagination and featuring a cast of remarkable characters and magical creatures, this is epic 
    adventure-packed storytelling at its very best. Whether an existing fan or new to the wizarding world, this is a perfect 
    addition for any film lover or reader’s bookshelf. The film Fantastic Beasts and Where to Find Them will have its theatrical 
    release on 18th November 2016.",
    "industryIdentifiers": [
     {
      "type": "ISBN_13",
      "identifier": "9781781109601"
     },
     {
      "type": "ISBN_10",
      "identifier": "1781109605"
     }
    ],
    "readingModes": {
     "text": true,
     "image": true
    },
    "pageCount": 304,
    "printType": "BOOK",
    "categories": [
     "Performing Arts"
    ],
    "averageRating": 4.0,
    "ratingsCount": 20,
    "maturityRating": "NOT_MATURE",
    "allowAnonLogging": true,
    "contentVersion": "1.6.6.0.preview.3",
    "panelizationSummary": {
     "containsEpubBubbles": false,
     "containsImageBubbles": false
    },
    "imageLinks": {
     "smallThumbnail": "http://books.google.com/books/content?id=DU0LDAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
     "thumbnail": "http://books.google.com/books/content?id=DU0LDAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
    },
    "language": "en",
    "previewLink": "http://books.google.com/books?id=DU0LDAAAQBAJ&pg=PP1&dq=harry+potter&hl=&cd=1&source=gbs_api",
    "infoLink": "https://play.google.com/store/books/details?id=DU0LDAAAQBAJ&source=gbs_api",
    "canonicalVolumeLink": "https://market.android.com/details?id=book-DU0LDAAAQBAJ"
   },
   "saleInfo": {
    "country": "US",
    "saleability": "FOR_SALE",
    "isEbook": true,
    "listPrice": {
     "amount": 12.99,
     "currencyCode": "USD"
    },
    "retailPrice": {
     "amount": 12.99,
     "currencyCode": "USD"
    },
    "buyLink": "https://play.google.com/store/books/details?id=DU0LDAAAQBAJ&rdid=book-DU0LDAAAQBAJ&rdot=1&source=gbs_api",
    "offers": [
     {
      "finskyOfferType": 1,
      "listPrice": {
       "amountInMicros": 1.299E7,
       "currencyCode": "USD"
      },
      "retailPrice": {
       "amountInMicros": 1.299E7,
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
     "acsTokenLink": "http://books.google.com/books/download/Fantastic_Beasts_and_Where_to_Find_Them-sample-epub.acsm?id=DU0LDAAAQBAJ&format=epub&output=acs4_fulfillment_token&dl_type=sample&source=gbs_api"
    },
    "pdf": {
     "isAvailable": true,
     "acsTokenLink": "http://books.google.com/books/download/Fantastic_Beasts_and_Where_to_Find_Them-sample-pdf.acsm?id=DU0LDAAAQBAJ&format=pdf&output=acs4_fulfillment_token&dl_type=sample&source=gbs_api"
    },
    "webReaderLink": "http://play.google.com/books/reader?id=DU0LDAAAQBAJ&hl=&printsec=frontcover&source=gbs_api",
    "accessViewStatus": "SAMPLE",
    "quoteSharingAllowed": false
   },
   "searchInfo": {
    "textSnippet": "A feat of imagination and featuring a cast of remarkable characters and magical creatures, this is epic adventure-packed storytelling at its very best."
   }
  }
```
