# carparkfinder-androidapp

This is the code for the android app. The code for the server is located in another git directory

## Overview
This is an android app which locates carparks nearby or based on your search destination. 
Carparks displayed are based on your search criterions. 
This map auto centers itself as you drive / move except after a direct search
This app redirects you to google maps for navigation as well.

## Data source (in a seperate git directory)
Carpark availability is extracted from data.gov.sg through a cron job.
Additional carpark information is extracted from another LTA data source on a cron job. This is to facilliate google maps polling and information display.
Locations have been pre-extracted from google maps API. There is an additional logic to poll google maps should there be a new carpark not recorded in the database.
The app communicates with the server via API endpoints.

## Development
Developed on Java SDK for android (app)
Developed on python (server)
