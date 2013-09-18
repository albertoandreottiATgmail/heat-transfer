
parameters;

%fprintf("Hi dude, you entered a: ")
%fprintf(mat2str(parameters.the_matrix))

addpath("/media/sf_SpeechProcessing/")
addpath("/usr/share/octave/packages/3.2/tsa-4.1.0/")
runOnSamples
main(parameters.the_matrix/1000)
