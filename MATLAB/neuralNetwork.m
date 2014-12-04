%% Neural Network Algorithm
% This script implements a neural network classifier using the gradient
% descent algorithm operating in batch mode.
%
% Statistics such as the accuracy, execution time, confusion matrix, and 
% ROC Curve are generated.
%% Dependencies
% This script uses and depends on the following:
% - Statistics Toolbox
%% Initialization Steps
% Import dataset
data = importTweets('matlabInput.txt', ' ');

% Function to create training and test sets w/desired p-value
[Xtrain, Ytrain, trainLabels, Xtest, Ytest, testLabels] = ...
    createDataSet(data, 0.7, 1);

format long;
%% Initialization Steps
X = data.terms;
Y = data.locations;

tabulate(Y);
% start timer
tic
% Initialize all variables and arrays
Xtrain = Xtrain(:,2:end);
Xtest = Xtest(:,2:end);
row = size(Xtrain,1);
rowt = size(Xtest,1);
Xtrain = [Xtrain ones(row,1)];
Xtest = [Xtest ones(rowt,1)];
[rows,cols] = size(Xtrain);
[rowst,colst] = size(Xtest);
eta = 0.1;
error1 = 0.01;
epochs = 1000;

% Initialize target and temp vector
Targ = zeros(rows,1);
w = rand(1,cols);
iterations = 0;
e = error1;
out = ones(1,rows);
err = ones(1,rows);
deltaw = 0.0;
k = 4;
% Use sigmoid function on the target for each class
for i = 1:rows;
    if Ytrain(i) == k;
        Targ(i) = 1;
    else
        Targ(i) = -1;
    end
end

%% Train the Neural Network
% Implement gradient descent in batch mode
while e >= error1 &&  iterations <= epochs
    iterations = iterations + 1;
    wrong = 0;
    for i=1:rows,
        out(i) = sum(w .* Xtrain(i,:));  % delta rule
        temp = sum(w .* Xtrain(i,:));
        if temp < 0
            out(i) = -1;
        else
            out(i) = 1;
        end
        deltaw = deltaw + (eta * (Targ(i) - out(i)) * ...
            Xtrain(i,:));
    end
    
    % Update the weight vector after iterating through the entire
    % dataset for batch mode
    w = w + deltaw;
    
    % Iterate through each data example and calculate the error
    for i = 1:rows
        if Targ(i) * out(i) < 0
            wrong = wrong + 1;
        end
    end
    e = wrong / rows;
    Eout(k+1,iterations) = e;
end
out1 = zeros(1,rowst);

%% Neural Network Predictions
% Compute the output for each test data using w
for i = 1:rowst,
    out1(i) = Xtest(i,:) * w';
end

%% Analyze Results
% Transform output in 0 1 labels
out1(out1 < 0) = 0;
out1(out1 > 0) = 1;
tempO = zeros(rowst,1);
tempO(Ytest == k) = 1;
targetOutputs(k+1,:) = tempO;
Outputs(k+1,:) = out1(1,:);

% Compute Accuracy
accuracy = mean(sum(targetOutputs == Outputs,2) / rowst)

% Plot Confusion Matrix
plotconfusion(targetOutputs,Outputs);

% Plot ROC Curve
figure();
plotroc(targetOutputs,Outputs);

% End Timer
toc
