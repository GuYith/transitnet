# Transitnet
A back-end of transit network visualization platform supports data query, speed data calculation and so on.
## Getting started
### 1. Database configuration
Ensure that the database is configured correctly in application.properties.
### 2. Data Prepare (Optional)
1. GTFS Data: https://transitfeeds.com/p/mta
2. GTFS RealTime Data: http://bt.mta.info/wiki/Developers/Index

The real-time bus data is preprocessed by data cleaning, stopping point detection and map-matching.