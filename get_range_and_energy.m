function [range, energy] = get_range_and_energy(signal, ref)
    [a, ~] = xcorr(signal, ref);
    a = a(ceil(length(a) / 2) : end);
    [peaks, loc] = findpeaks(a);
    maxpeaks = max(peaks);
    for ii = 1 : length(peaks)
        if peaks(ii) > 0.7 * maxpeaks
            range = loc(peaks) / 48000 * 34000 / 2;
            break;
        end
    end
    energy = maxpeaks;
end