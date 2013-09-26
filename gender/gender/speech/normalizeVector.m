%Mean normalization
function normalized = normalizeVector(fvector)

	for i=1:size(fvector,2)
	    std_dev = std(fvector(:, i));
		fvector(:, i) = fvector(:, i).-mean(fvector(:, i));
		fvector(:, i) = fvector(:, i)./std_dev;
	end
	normalized = fvector;
end