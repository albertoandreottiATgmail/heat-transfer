%Basic Front End for Speech Processing
%Author: Alberto Andreotti.
%Prevent Octave from thinking that this is a function file:
1;

%Train NN with [pitch, formant1, formant2, ..., formantN, cepstrum] feature vector using male/female frames.
function main(samples)
		
    %warning ("error", "Octave:broadcast");
	%record time
	initial = time
	%Load feature vectors from file, ds_sp1, ds_sp2, ds_sp3
	options = featureopt('pitch', true, 'formants', 1, 'mfcc', 0);
	ds_sp1 = extractFeatures(source = samples, options);
	%save pekish.dat ds_sp1 -append
	
	%define centroids for euclidean distance
	male_centroid = [120/220, 387/400]; %, 1178];
	female_centroid = [210/220, 432/400]; %, 1268];
	
    for i=1:size(ds_sp1,2)
	    mean_sp1(i) = mean(ds_sp1(:, i));
	end
	
	mean_sp1(1) = mean_sp1(1)/220;
	mean_sp1(2) = mean_sp1(2)/400;
	
	dist_male = sqrt(sum((mean_sp1-male_centroid).*(mean_sp1-male_centroid)));
	dist_female = sqrt(sum((mean_sp1-female_centroid).*(mean_sp1-female_centroid)));
	
	if dist_male > dist_female
		fprintf('Speaker 1 is detected to be female \n');
	else
		fprintf('Speaker 1 is detected to be male \n');
	end
	
	elapsed = time -initial
	fprintf('Total time: ')
	fprintf(mat2str(elapsed));
end


function plotSpectrogram(mtlb, Fs)
	segmentlen = 100;
	noverlap = 90;
	NFFT = 128;
	[y,f,t,p] = spectrogram(mtlb,segmentlen,noverlap,NFFT,Fs);
	surf(t,f,10*log10(abs(p)),'EdgeColor','none');
	axis xy; axis tight; colormap(jet); view(0,90);
	xlabel('Time');
	ylabel('Frequency (Hz)');
end



