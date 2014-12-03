%% Logistic Regression on Fisher Iris Data Set
% This script executes Logistic Regression on the Fisher Iris 
% data set.
%% Description
% This script implements a logistic regression for two class classification
% of the iris data set using the Iteratively Reweighted Least Squares
% (IRLS) algorithm. 
%
% Statistics such as the residual error, confusion matrix, and ROC plot
% are generated.
%% Dependencies
% This script uses and depends on the following:
% - Statistics Toolbox
%% Developers
% James D'Amato & Britney Gill
%% Code
format long;

% Create the training/test sets and labels by calling the createIrisSets. 
% The first input argument is the p-value and the second is the case to 
% classify.
[Xtrain, Ytrain, trainLabels, Xtest, Ytest, testLabels] ...
    = createDataSet(data, 0.7, 0);

[numRows, numCols] = size(Xtrain);
numRowst = size(testLabels,1);

% Initialize w
w = zeros(numCols, 1);

Xtrain = Xtrain + eye(numRows,numCols)*1e-9;

% Compute the mean from the training labels
ybar = mean(trainLabels);

% Set w0 from the w vector
w(1) = log(ybar) - log(1 - ybar);

% Initialize matrices, vectors, and variables
new = zeros(numRows, numRows);
s = zeros(1, numRows);
z = zeros(numRows, 1);
rel = 0.5;
nSum = 0.5;
k = 0;

% Test for convergence and iteration limit
while ((rel > 0.1) && (k < 2000)),
    % Run IRLS algorithm
    eta = Xtrain * w;
    mu = 1 ./ (1 + exp(-1 * eta));
    new(1,:) = mu';
    s = (new' * (1 - mu))';
    % Compute the working response
    z = (eta + (trainLabels - mu) ./ s');
    % Compute weight matrix
    S = diag(s);
    w = (pinv(Xtrain,1e-30) * S * Xtrain) \ (pinv(Xtrain,1e-30) * S *z);
    %w = inv(Xtrain' * S * Xtrain) * Xtrain'*S*z
    % Record previous weight matrix and compute delta
    oSum = nSum;
    nSum = sum(w);
    rel = abs(nSum - oSum) / abs(nSum);
    k=k+1;
end

out = zeros(1,numRowst);

% Compute the output for each test data using w
for i = 1:numRowst,
    out(i) = Xtest(i,:) * w;
end

% Transform output in 0 1 labels
out1 = out;
out1(out < 0) = 0;
out1(out > 0) = 1;

% Compute accuracy
accuracy = 1.0 - sum(abs(testLabels - out1'))/numRowst

% Plot confusion matrix
% plotconfusion(testLabels', out1);

% Plot ROC curves
 figure();
 plotroc(testLabels', out1);