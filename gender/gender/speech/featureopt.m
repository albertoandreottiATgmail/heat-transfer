%Creates an options object for choosing which features to extract

function options = featureopt(pitch, pitchv, formants, formantsv, mfcc, mfccv, delta, deltav)


	%TODO: to allow more flexible arg order, replace with this
    % for i = 1:length (varargin)
    %   if strcmp(varargin{i}, 'pitch')
	%		options.pitch = varargin{i+1}
	%	end
    % endfor

	options.pitch = false;
	if exist('pitch', 'var') && strcmp(pitch, 'pitch')
		options.pitch = pitchv;
	end	


	options.formants = 0;
	if exist('formants', 'var') && strcmp(formants, 'formants') && formantsv < 4
		options.formants = formantsv;
	end	

	options.mfcc = 0;
	if exist('mfcc', 'var') && strcmp(mfcc, 'mfcc') && mfccv>10 && mfccv<30
		options.mfcc = mfccv;
	end
	
	
	options.delta = false;
	if exist('delta', 'var') && strcmp(delta, 'delta')
		options.delta = delta;
	end
end