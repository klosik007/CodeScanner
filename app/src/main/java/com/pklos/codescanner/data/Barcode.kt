package com.pklos.codescanner.data

data class Barcode(
    val barcodeValue: String,
    val manufacturer: String,
    val brand: String,
    val ingredients: List<String>,
    val nutritionFacts: List<String>,
    val releaseDate: String
)

/*
    Barcode	String	859670003107
    Barcode Formats	String	UPC-A 736211911186, EAN-13 0736211911186
    MPN	String	LX-F942607
    Model	String	6500
    ASIN	String	B01EI7RUPI
    Title	String	A New Earth
    Category	String	Camping & Hiking > Tents
    Manufacturer	String	Ford Motor Company
    Brand	String	Michael Kors
    Contributors	String	Malcolm Gladwell
    Age Group	String	PG-13
    Ingredients	String	Tomatoes, Onions, Celery
    Nutrition Facts	String	Calories 75, Protein 6g
    Color	String	Yellow
    Gender	String	Female
    Material	String	Cotton
    Pattern	String	Striped
    Energy Efficiency Rating	String	A+ (A+++ to D)
    Multipack	String	4
    Size	String	Medium
    Length	String	2.6 inches
    Width	String	7.4 inches
    Height	String	3.5 inches
    Weight	String	1.29 lbs
    Release Date	String	1994-05-01
    Description	String	Detailed product description...
    Features	Array	•Sturdy •Real Wood •2 Year Warranty
    Images	Array	https://images.barcodelookup.com/id
    Stores	Array	Store Name, Price, Link, Currency and more
    Reviews	Array	Name, Rating, Title, Review, Date
*/