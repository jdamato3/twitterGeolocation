%% createDataSets Function
function [Xtrain, Ytrain, trainLabels, Xtest, Ytest, testLabels] ...
    = createDataSet(Dataset, p, caseNum)
%% Description
% Creates randomized training data and test data from the data-set 
% set with a P-Value and a species to classify as inputs to the function.
%
% P-values are increments of 0.1 from 0.1 to 0.9.
% Location classifier are integers 0, 1, 2, 3, 4. 
%
% Outputs are the following:
% Xtrain: Training input data with the column 1 concatenated to the left 
%         most column for the bias
% Ytrain: Column vector of training results
% trainLabels: Column vector of binary output values representing locations
%              to classify
% Xtest: Matrix of test data with a column of "1's" concatenated to the
%        left most column for the bias
% Ytest: Column vector of actual test results to compare against
%        predictions
% testLabels: Column vector of binary output values representing tweets to
%             classify
%% Developers
% James D'Amato & Britney Gill
%% Code
% Load the Dataset
X = Dataset.terms;
Y = Dataset.locations;

% Get size of measurements matrix
[rows,cols] = size(X);

% Extend meas by 1 to account for the bias
col1 = ones(rows, 1);
emeas = [col1 X];

% Transform numerical labels into 1 / -1 labels
newclass = ones(rows,1);
newclass(Y == caseNum) = 0; % Active class gets the "1" label
for i=1:rows
    if Y(i) == caseNum,
        Y(i) = 1;
    else Y(i) = 0;
    end
end

% Split indices based on desired P-Value
IndLowBnd = rows - round(rows * p);
IndLowBnd1 = IndLowBnd + 1;

% Training and Testing sizes
trainSize = round(rows * p);
testSize = rows - round(rows * p);


% Split for training and test sets based upon desired P-Value
i = randperm(rows);
testInd = i(1:IndLowBnd);
trainingInd = i(IndLowBnd1:rows);

% Initialize arrays and matrices
Ytrain = ones(trainSize,1);
Ytest = ones(testSize,1);
Xtrain = ones(trainSize,(cols+1));
Xtest = ones(testSize,(cols+1));
testLabels = ones(testSize,1);
trainLabels = ones(trainSize,1);

% Create randomized test set for measurements and species
for k = 1:length(testInd)
    Ytest(k) = Y(testInd(k));
    Xtest(k,:) = emeas(testInd(k),:);
    testLabels(k) = newclass(testInd(k));
end

% Create randomized training set for tweets and locations
for j = 1:length(trainingInd)
    Xtrain(j,:) = emeas(trainingInd(j),:);
    Ytrain(j) = Y(trainingInd(j));
    trainLabels(j) = newclass(trainingInd(j));
end

end