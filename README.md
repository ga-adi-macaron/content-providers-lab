---
title: Content Providers
type: lab
duration: "1:20"
creator:
    name: Drew Mahrt
    city: NYC
---



# ![](https://ga-dash.s3.amazonaws.com/production/assets/logo-9f88ae6c9c3871690e33280fcf557f33.png) Content Providers

**This lab can be done in pairs.**

In this lab, you will be creating an app that will use the [MarkIt On Demand API](http://dev.markitondemand.com/MODApis/) to create a Stock portfolio. All data must be stored in a local database through a Content Provider. The user must be able to add a stock as well as the quantity they bought to the database in a dialog window (see screenshots below). On the screen, you must show the company name and quantity of stock purchased in a RecyclerView.

The MarkIt API will be used to retrieve the company name and quantity of stock for each company.

## Exercise

#### Requirements

- Use a Content Provider to store data in a local database
- Store the stock symbol, company name, quantity of stock, and the stock exchange name in the database
- Use the MarkIt On Demand API
- Display the Company name and quantity of stock for each company in a RecyclerView
- Allow the user to enter a new stock using a dialog window

**Bonus:**
- Display the remaining information in the database for each stock entry
- Use ContentObservers to reflect content changes on the UI

#### Starter code

Use the starter code in the starter-code folder if you would like.

#### Deliverable

An Android app that follows the requirements above:

<p align="center">
  <img src="screenshots/screen1.png" height="400px" />
</p>

<p align="center">
  <img src="screenshots/screen2.png" height="400px" />
</p>

## Additional Resources  
- [Android Developer Docs on Calendar Provider](http://developer.android.com/guide/topics/providers/calendar-provider.html)

---

## Licensing
1. All content is licensed under a CC­BY­NC­SA 4.0 license.
2. All software code is licensed under GNU GPLv3. For commercial use or alternative licensing, please contact [legal@ga.co](mailto:legal@ga.co).
