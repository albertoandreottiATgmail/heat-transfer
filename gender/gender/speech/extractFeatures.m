%Extracts features from a .wav file. Runs k-means to separate between voiced and unvoiced samples.
%Runs on voiced frames only.	
function fvector = extractFeatures(source, options)

	%load samples, compute zcc, energy, plot signal in time
	
	[zcc_energy, samples, fs] = preprocess(source, plot_progress = false);
	
	%normalize
	samples = maxMinNormalization(samples);

	%Obtain centroids and assignments to separate between voice and unvoiced
	initial_centroids = [2 3;50 2]
	[centroids, idx] = runkMeans(zcc_energy, initial_centroids, iterations = 8, plot_progress = 0);
	
	%Find class with greatest ZCC
	if 	centroids(1,1) > centroids(2,1)
	    voiced = 2;
	else
		voiced = 1;
	end

	%TODO: remove hard coded frame size
	j=1;
	vector = 0;
	for i = 2:(length(samples)/160 -2)
		%Obtain pitch and formants using LPC, only if this is a VOICED frame.
		if idx(i)==voiced
		    if zcc_energy(i,1)>175
			   continue;
			end
			
			if zcc_energy(i,2)<0.01
			   continue;
			end
			
			begining = (i-1)*160 + 1 -110;
			ending = begining + 320 + 110;
			frame = samples(begining:ending);
			
			%Apply hamming window
			window = hamming(length(frame));
			frame = frame.*window;

			%If we need pitch or formants, lets include them
			if options.pitch || options.formants
				[pitch, formants] = linearPrediction(frame, fs);
				vector = [pitch];
				if pitch==0 && options.pitch
					[pitch, _formants] = cepstral_features(frame);
					if pitch==0 
						continue;
					end
					vector = [pitch];
				elseif options.pitch
					vector = [pitch];
				end			
				%include as many formants as required
				if length(formants)<options.formants
					continue;
				end
				vector = [vector, formants(1:options.formants)];

			end
			
			%options.mfcc not currently used, using 14 coefficients
			if options.mfcc
				vector = [vector, mfcc(frame)]; %, cepstrum(frame), mfcc(frame)
			end

			%Dynamic frequency features
			%For variants on the computation of these features, check 2.1.2 of http://speechlab.eece.mu.edu/papers/Ye_thesis.pdf.
			if options.delta
			    if j>1
					delta = fvector(j-1, end-13:end) - vector(end-13:end);
				else
					delta = zeros(1,14) - vector(end-13:end);
			    end
				vector = [vector, delta]; %, cepstrum(frame), mfcc(frame)
				
			end
			
			fvector(j, :) = vector;
			j = j + 1;
			
		end
	end
	
	fprintf('Extracted %f feature vectors from %f frames \n', size(fvector,1), length(samples)/160);
	
end
