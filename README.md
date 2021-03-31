- [Introduction](#introduction)
- [About the Usecase](#about-the-usecase)
- [Twitter API Details](#twitter-api-details)
- [Execution Options](#execution-options)
    - [Maven or Java Command](#maven-or-java-command)
    - [Heroku Dyno](#heroku-dyno)


## Introduction

Capture N View is an application that is developed to showcase the capabilities of the Quarkus. The application uses Quarkus, Quarkus Resteasy, Quarkus Rest Client, Quarkus Scheduler, Mongo DB.

## About the Usecase

The usecase here is application that captures the Top 10 Trending keywords from Twitter for given place and displays that as a graphs on UI. HTML, Java Script and D3 Framework are used for UI rendering.

## Twitter API Details

We will be utilizing the Twitter Trends for location API. Please refere to [twitter documentation on trends API](https://developer.twitter.com/en/docs/trends/trends-for-location/overview) for more details. 

The application  needs 3 runtime parameters, Twitter Consumer API Key, Secret and Place Id. You can create an App by login into the [Twitter Developer site](https://developer.twitter.com/en/apps) and then you can create the API Key and Secret against your newly created App. 

For the Place Id, twitter takes the [WOEID](https://en.wikipedia.org/wiki/WOEID). For example for India, the WOEID is 23424848.

## Execution Options

For executing the application it needs 4 environment variables, Mongo DB Connection String, twitter consumer key, twitter secret and place id. Please refere to [twitter documentation on trends API](https://developer.twitter.com/en/docs/trends/trends-for-location/overview) for more details.

### Maven or Java Command

For building the application the regular Quarkus Way by executing the mvn command below 

```
mvn compile -Dtwitter.consumerKey=<<Twitter Consumer API Key>> -Dtwitter.secret=<<API Secret>> -Dtwitter.woeid=<<WOEID>> quarkus:dev
```

or you can perform 2 steps.

Step 1 :

```
mvn compile
```

Stpe 2 :

```
java -jar -Dtwitter.consumerKey=<<Twitter Consumer API Key>> -Dtwitter.secret=<<API Secret>> -Dtwitter.woeid=<<WOEID>> -Dquarkus.mongodb.connection-string=<<Mongo DB Connection String>> target/capture-n-view-1.0.0-SNAPSHOT-runner.jar
```

### Heroku Dyno

The file "system.properties" has been added for picking Java 11 during the build process in Heroku.

The file "Procfile" has been added pass the port and timezone as dynamic runtime parameters.

Once you had deployed the container on Dyno, you need to set the environment variables "quarkus.mongodb.connection-string", "twitter.consumerKey", "twitter.secret" and "twitter.woeid" at the "Config Vars" Section under Settings menu of the App under Heroku
