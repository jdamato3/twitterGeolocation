%% Creating Dataset
% Import dataset
data = importTweets('matlabInput.txt', ' ');

% Function to create training and test sets w/desired p-value
[Xtrain, Ytrain, trainLabels, Xtest, Ytest, testLabels] = ...
    createDataSet(data, 0.7, 1);

format long;

%% Training Naive Bayes Bernoulli Model
% Start timer
tic
% Initializing number of examples and number of examples in class caseNum
Nc = 0;
% removing first column for IRLS and neural network bias
Xtrain = Xtrain(:,2:end);
% Initialize matrices and vectors
[row, col] = size(Xtrain);
N = row;
condProb = zeros(col,5);
Prior = zeros(1,5);

% Train Naive Bayes Bernoulli model
for k = 0:4
    Nc = sum(Ytrain(:) == k);
    % prior probability
    Prior(k+1) = Nc / N;
    for j = 1:col
        Nct = 0;
        for i = 1:N
            if ((Ytrain(i) == k) && (Xtrain(i,j) == 1))
                Nct = Nct + 1;
            end
        end
        % conditional probability
        condProb(j,(k+1)) = (Nct + 1) / (Nc + 2);
    end
end

%% Apply Naive Bayes Bernoulli Model

% remove first column for IRLS and neural network bias from test set
Xtest = Xtest(:,2:end);
% get sizes
[rowT, colT] = size(Xtest);
[rowTy, colTy] = size(Ytest);
% initialize arrays and posterior probability
out = zeros(rowTy,colTy);
score = log(Prior);

% apply naive bayes bernoulli on test set
for i = 1:rowT
    Vd = Xtest(i,:);
    % iterate through all of the classes
    for k = 0:4
        for t = 1:colT
            if (Vd(t) == 1)
                score(k+1) = score(k+1) + log(condProb(t,k+1));
            else
                score(k+1) = score(k+1) + log(1 - condProb(t,k+1));
            end
        end
    end
    % find max score for all classes and assign
    out(i) = (find(max(score)) - 1);
end
% end timer
toc
%% Analyze Results

% Compute accuracy
accuracy = sum(Ytest == out) / rowT;

% Plot confusion matrix
% plotconfusion(Ytest, out);
targetOutputs = ones(5,rowTy);
outputs = ones(5,rowTy);
for k = 0:4
targetOutputs(k+1,:) = (Ytest == k);
outputs(k+1,:) = (out == k);
end

% Plot confusion matrix
  plotconfusion(targetOutputs, outputs);

% Plot ROC curves
 figure();
 plotroc(targetOutputs, outputs);

