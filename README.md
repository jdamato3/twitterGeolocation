twitterGeolocation
==================

Twitter Geolocation Classification Project

To complete the task of text-based geolocation prediction of Twitter users within a subset of US cities. The growing volume of user-generated text posted to social media services such as Twitter, Facebook, and Instagram can be leveraged for many purposes ranging from natural disaster response to targeted advertising. In many circumstances, it is important to know a user's location in order to accomplish these tasks effectively. Although many social media services allow a user to declare their location, such metadata is known to be unstructured and ad-hoc, as well as oftentimes non-geographical (e.g. in my house). Text based geolocation - automatically predicting a user's location based on the content of their messages is therefore becoming more advantageous and reliable.

The data-set to be analyzed is provided by Twitter and hosted by Kaggle. The Twiiter data is given in its raw form - containing a unique user ID, a unique Tweet ID, the string of alphanumeric and special characters representing the Tweet, and a time/date stamp. Each Tweet can be classified as originating from the subset of US cities: Atlanta, Chicago, Los Angeles, New York, or San Francisco. The general process involves (1) parsing the Tweets into terms (2) performing feature selection (3) creating an n x m matrix representing the features and an n x 1 vector representing the respective classes (4) creating training and test sets (5) using the training set to train the classification model (Naive Bayes, Logistical Regression, Neural Network) and the test set to perform the predictive classification for each Tweet (6) evaluating, comparing, and contrasting the results.

Testing push capabilities
