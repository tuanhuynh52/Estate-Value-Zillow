# Estate-Value-Zillow
## Introduction

This Android application allows user to check their home values or other properties as well as they input the correct and valid address. There are so many applications out there that they have the same purposes but most of them offer so many functionalities which do not aim for a specific purpose. I created this application just for fun to provide the user a fast way to check their home value based on Zillow APIs.  All of information given in this application are provided by Zillow web service, some of addresses are not up to date in Zillow XML database so it sometimes cannot be obtained and retrieved to the users.

## User Interactions

* Users can just input a given address into search bar to retrieve its home value.
*	The address doesn’t need to be in full or good spell, it will provide user more options of different addresses based on input key words using Google Place Autocomplete.
* Users can be able to view images of their home if available and its location on a mini map which is provided by Google Map APIs.
* User can share their home value to someone else by sending the link to the receivers.
* User can also able to view neighborhood properties’ values on Google map and another cool stuff is to take user to the location of selected address.
* User can see the value of the place where he/she is standing if available (current location GPS)

## Implementations
* Google Place Autocomplete service for place search if misspelling 
* Retrieve and display place’s information by parsing XML data after sending through a request
*	Performs permission checks for using device current location GPS and change different colors of menu bar of each fragment. This requires Android API 21 or above to use
*	Vertical scroll View to display many information in a page of fragment and horizontal scroll view to show many images along the view.
*	Google Maps to display many different properties’ values of neighborhood as well as for directions and navigation.
