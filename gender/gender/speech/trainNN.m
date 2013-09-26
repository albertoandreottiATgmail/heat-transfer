%% Trains a NN on the input variables.

function nn_params = trainNN(initial_nn_params, hidden_layer_size, samples, outcomes, num_labels)
	fprintf('\nTraining Neural Network... \n')

	%  After you have completed the assignment, change the MaxIter to a larger
	%  value to see how more training helps.
	options = optimset('MaxIter', 250);

	%  You should also try different values of lambda
	lambda = 0.025;
	input_layer_size = size(samples, 2);

	% Create "short hand" for the cost function to be minimized
	costFunction = @(p) nnCostFunction(p, ...
                                   input_layer_size, ...
                                   hidden_layer_size, ...
                                   num_labels, samples, outcomes, lambda);

	% Now, costFunction is a function that takes in only one argument (the
	% neural network parameters)
	[nn_params, cost] = fmincg(costFunction, initial_nn_params, options);

	% Obtain Theta1 and Theta2 back from nn_params
	Theta1 = reshape(nn_params(1:hidden_layer_size * (input_layer_size + 1)), ...
                 hidden_layer_size, (input_layer_size + 1));

	Theta2 = reshape(nn_params((1 + (hidden_layer_size * (input_layer_size + 1))):end), ...
					num_labels, (hidden_layer_size + 1));

	fprintf('Program paused. Press enter to continue.\n');
	pause;
end
