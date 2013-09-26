%ZEROCROSS Short-time ZCC.
%   zc = ZEROCROSS(X) computes the zero crossing count 
%   of sequence x.
%   
%   Author: Beto Andreotti.
%   Date: 2012/11/28
	
function zc = zerocross(x)

	%TODO:
	%error(nargchk(1,4,nargin,'struct'));
	%generate x[n] and x[n-1]
	x1 = x;
	x2 = [0, x(1:end-1)];

	% generate the first difference
	firstDiff = sign(x1)-sign(x2);

	% magnitude only
	absFirstDiff = abs(firstDiff)*0.5;

	%Final Sum
	zc = sum(absFirstDiff);
end