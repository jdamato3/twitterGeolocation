function [dataset] = importTweets(filename, delimeter)
A = dlmread(filename, delimeter);
dataset = struct;
dataset.terms = A(:,1:(end-1));
dataset.locations = A(:,end);
end