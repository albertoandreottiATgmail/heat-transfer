%Preprocesses audio, loads samples, computes zcc, and energy.
%Plots signal
function [zcc_energy, samples, fs] =  preprocess(source, plot_progress)
	
	
	%process file
	if ischar(source)
		[samples, fs, bits] = wavread(source);
		size(samples)
	else %process samples, TODO: remove harcoded FS
		
		samples = source';
		size(samples);
		fs = 16000;
		bits = 0 ;
	end
	
	
	if ~exist('plot_progress', 'var') || isempty(plot_progress)
		plot_progress = false;
    end
	
	if plot_progress 
	   %plot ([1:size(samples)], samples);
    end
	%Apply pre-emphasis, check these filter's coefs.
	preemph = [1 0.98];
    samples = filter(1,preemph, samples);
	
	%Compute the number of frames
	fsize = 0.01 * fs;
	num_windows = int32(length(samples) / fsize);
	window = hamming(2*fsize);

	%Loop through all the windows
	for window_number = 1:(num_windows-2),
		index1 = (window_number-1)*fsize +1;
		index2 = index1+2*fsize-1;
	
        current_frame = window.*samples(index1:index2);
		%Compute Short time energy, too simple for a separate function :-)
		energy = sum(current_frame.^2);	
		%Compute ZCC
		zcc = zerocross(current_frame');
	
		%store parameters for this frame
		zcc_energy(window_number,1) = zcc;
		zcc_energy(window_number,2) = energy;
	end
	
end